package com.librosmario.pedidos.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayInputStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.librosmario.pedidos.security.JwtTokenProvider;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class VentaReporteTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenProvider tokenProvider;

	private String adminToken;
	private String vendedorToken;

	@BeforeEach
	void setUp() {
		adminToken = tokenFor("test");
		vendedorToken = tokenFor("vendedor");
	}

	private String tokenFor(String username) {
		Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(username, "12345678"));
		return tokenProvider.generateToken(auth);
	}

	private void registrarVenta(String json) throws Exception {
		mockMvc.perform(post("/ventas")
				.header("Authorization", "Bearer " + vendedorToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andExpect(status().isCreated());
	}

	@Test
	void descargaUnXlsxConUnaFilaPorLibroVendidoYUnaFilaDeTotales() throws Exception {
		// 2 sales, 3 lines in total: the first sale has two books, the second has one.
		registrarVenta("{\"items\":[{\"isbn\":\"111\",\"libro\":\"Uno\",\"autor\":\"Autor A\",\"cantidad\":2,\"precio\":100.0},"
				+ "{\"isbn\":\"222\",\"libro\":\"Dos\",\"autor\":\"Autor B\",\"cantidad\":1,\"precio\":50.0}]}");
		registrarVenta("{\"items\":[{\"isbn\":\"333\",\"libro\":\"Tres\",\"autor\":\"Autor C\",\"cantidad\":3,\"precio\":10.0}]}");

		byte[] xlsx = mockMvc.perform(get("/ventas/reporte")
				.header("Authorization", "Bearer " + adminToken))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-Type",
						"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
				.andExpect(header().string("Content-Disposition",
						org.hamcrest.Matchers.containsString("attachment")))
				.andReturn().getResponse().getContentAsByteArray();

		try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(xlsx))) {
			Sheet sheet = wb.getSheetAt(0);

			Row header = sheet.getRow(0);
			assertThat(header.getCell(0).getStringCellValue()).isEqualTo("Venta");
			assertThat(header.getCell(4).getStringCellValue()).isEqualTo("ISBN");
			assertThat(header.getCell(5).getStringCellValue()).isEqualTo("Libro");
			assertThat(header.getCell(7).getStringCellValue()).isEqualTo("Cantidad");
			assertThat(header.getCell(9).getStringCellValue()).isEqualTo("Subtotal");

			// 3 book lines + header + totals row = 5 rows (indices 0..4)
			assertThat(sheet.getLastRowNum()).isEqualTo(4);

			// First data row is the first book of the first sale.
			Row primera = sheet.getRow(1);
			assertThat(primera.getCell(4).getStringCellValue()).isEqualTo("111");
			assertThat(primera.getCell(5).getStringCellValue()).isEqualTo("Uno");
			assertThat(primera.getCell(6).getStringCellValue()).isEqualTo("Autor A");
			assertThat(primera.getCell(7).getNumericCellValue()).isEqualTo(2d);
			assertThat(primera.getCell(8).getNumericCellValue()).isEqualTo(100d);
			assertThat(primera.getCell(9).getNumericCellValue()).isEqualTo(200d);   // 2 x 100

			Row totales = sheet.getRow(4);
			assertThat(totales.getCell(0).getStringCellValue()).isEqualTo("TOTAL");
			assertThat(totales.getCell(7).getNumericCellValue()).isEqualTo(6d);      // 2+1+3 units
			assertThat(totales.getCell(9).getNumericCellValue()).isEqualTo(280d);    // 200+50+30
		}
	}

	@Test
	void elVendedorNoPuedeDescargarElReporte() throws Exception {
		mockMvc.perform(get("/ventas/reporte")
				.header("Authorization", "Bearer " + vendedorToken))
				.andExpect(status().is4xxClientError());
	}

	@Test
	void descargarRequiereAutenticacion() throws Exception {
		mockMvc.perform(get("/ventas/reporte"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void unRangoSinVentasDevuelveUnXlsxSoloConCabeceraYTotales() throws Exception {
		byte[] xlsx = mockMvc.perform(get("/ventas/reporte")
				.header("Authorization", "Bearer " + adminToken)
				.param("fechaDesde", "1990-01-01").param("fechaHasta", "1990-01-02"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsByteArray();

		try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(xlsx))) {
			Sheet sheet = wb.getSheetAt(0);
			// header (0) + totals (1), no data rows
			assertThat(sheet.getLastRowNum()).isEqualTo(1);
			assertThat(sheet.getRow(1).getCell(0).getStringCellValue()).isEqualTo("TOTAL");
			assertThat(sheet.getRow(1).getCell(9).getNumericCellValue()).isEqualTo(0d);
		}
	}
}
