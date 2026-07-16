package com.librosmario.pedidos.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.librosmario.pedidos.security.JwtTokenProvider;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CatalogoImportTest {

	private static final String CREADOR_FINAL = "luongo_bulk";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenProvider tokenProvider;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private CatalogoController controller;

	private String token;

	@BeforeEach
	void setUp() {
		Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken("test", "12345678"));
		token = tokenProvider.generateToken(auth);

		jdbcTemplate.update("DELETE FROM cg_catalogo WHERE cg_creador in (?, ?)", CREADOR_FINAL, "luongo_bulk_new");
		jdbcTemplate.update("INSERT INTO cg_catalogo (cg_codigo_luongo, cg_descripcion, cg_autor, cg_precio,"
				+ " cg_editorial, cg_isbn, cg_observaciones, cg_creador) VALUES (?,?,?,?,?,?,?,?)",
				"OLD001", "Catalogo previo", "Autor previo", 10.0, "Editorial", "978-0000000000", "", CREADOR_FINAL);
	}

	private long existingCatalogoCount() {
		return jdbcTemplate.queryForObject("SELECT count(*) FROM cg_catalogo WHERE cg_creador=?", Long.class,
				CREADOR_FINAL);
	}

	private MockMultipartFile csvUpload(String content) {
		return new MockMultipartFile("file", "luongo.csv", "text/csv",
				content.getBytes(StandardCharsets.UTF_8));
	}

	/** A genuine OOXML workbook, laid out like the Luongo file: Isbn, Titulo, Autor, Coleccion, Sello, Clase, Precio. */
	private byte[] realXlsxBytes() throws Exception {
		try (XSSFWorkbook workbook = new XSSFWorkbook();
			 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Sheet sheet = workbook.createSheet("catalogo");
			Row header = sheet.createRow(0);
			String[] headers = { "Isbn", "Titulo", "Autor", "Coleccion", "Sello", "Clase", "Precio" };
			for (int i = 0; i < headers.length; i++) {
				header.createCell(i).setCellValue(headers[i]);
			}
			Row data = sheet.createRow(1);
			String[] values = { "978-9999999999", "Libro xlsx", "Autor xlsx", "Coleccion", "Editorial xlsx", "INF", "150,50" };
			for (int i = 0; i < values.length; i++) {
				data.createCell(i).setCellValue(values[i]);
			}
			workbook.write(out);
			return out.toByteArray();
		}
	}

	/**
	 * Same layout, but the ISBN is a numeric cell with the default General format — which is how a
	 * spreadsheet stores it when nobody forces the column to text.
	 */
	private byte[] xlsxWithNumericIsbnBytes(double isbn) throws Exception {
		try (XSSFWorkbook workbook = new XSSFWorkbook();
			 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Sheet sheet = workbook.createSheet("catalogo");
			Row header = sheet.createRow(0);
			String[] headers = { "Isbn", "Titulo", "Autor", "Coleccion", "Sello", "Clase", "Precio" };
			for (int i = 0; i < headers.length; i++) {
				header.createCell(i).setCellValue(headers[i]);
			}
			Row data = sheet.createRow(1);
			data.createCell(0).setCellValue(isbn);
			data.createCell(1).setCellValue("Libro numerico");
			data.createCell(2).setCellValue("Autor numerico");
			data.createCell(3).setCellValue("Coleccion");
			data.createCell(4).setCellValue("Editorial numerica");
			data.createCell(5).setCellValue("INF");
			data.createCell(6).setCellValue("150,50");
			workbook.write(out);
			return out.toByteArray();
		}
	}

	/** ISBN and price both numeric with the default General format — the real Luongo file's shape. */
	private byte[] xlsxWithNumericIsbnAndGeneralPriceBytes(double isbn, double precio) throws Exception {
		try (XSSFWorkbook workbook = new XSSFWorkbook();
			 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Sheet sheet = workbook.createSheet("catalogo");
			Row header = sheet.createRow(0);
			String[] headers = { "Isbn", "Titulo", "Autor", "Coleccion", "Sello", "Clase", "Precio" };
			for (int i = 0; i < headers.length; i++) {
				header.createCell(i).setCellValue(headers[i]);
			}
			Row data = sheet.createRow(1);
			data.createCell(0).setCellValue(isbn);
			data.createCell(1).setCellValue("Libro precio general");
			data.createCell(2).setCellValue("Autor");
			data.createCell(3).setCellValue("Coleccion");
			data.createCell(4).setCellValue("Editorial");
			data.createCell(5).setCellValue("INF");
			data.createCell(6).setCellValue(precio);
			workbook.write(out);
			return out.toByteArray();
		}
	}

	private Double importedPrecio() {
		return jdbcTemplate.queryForObject(
				"SELECT cg_precio FROM cg_catalogo WHERE cg_creador=?", Double.class, CREADOR_FINAL);
	}

	private void importGeneralPrice(double precio) throws Exception {
		mockMvc.perform(multipart("/catalogos/import")
				.file(new MockMultipartFile("file", "catalogo.xlsx",
						"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
						xlsxWithNumericIsbnAndGeneralPriceBytes(9789871234567d, precio)))
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());
	}

	/** ISBN and price both numeric, price carrying an explicit two-decimal format. */
	private byte[] xlsxWithNumericIsbnAndPriceBytes(double isbn, double precio) throws Exception {
		try (XSSFWorkbook workbook = new XSSFWorkbook();
			 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Sheet sheet = workbook.createSheet("catalogo");
			Row header = sheet.createRow(0);
			String[] headers = { "Isbn", "Titulo", "Autor", "Coleccion", "Sello", "Clase", "Precio" };
			for (int i = 0; i < headers.length; i++) {
				header.createCell(i).setCellValue(headers[i]);
			}
			CellStyle priceStyle = workbook.createCellStyle();
			priceStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));

			Row data = sheet.createRow(1);
			data.createCell(0).setCellValue(isbn);
			data.createCell(1).setCellValue("Libro con precio");
			data.createCell(2).setCellValue("Autor");
			data.createCell(3).setCellValue("Coleccion");
			data.createCell(4).setCellValue("Editorial");
			data.createCell(5).setCellValue("INF");
			Cell priceCell = data.createCell(6);
			priceCell.setCellValue(precio);
			priceCell.setCellStyle(priceStyle);
			workbook.write(out);
			return out.toByteArray();
		}
	}

	/** A genuine legacy OLE2 workbook — the format the importer cannot parse. */
	private byte[] realXlsBytes() throws Exception {
		try (HSSFWorkbook workbook = new HSSFWorkbook();
			 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			workbook.createSheet("catalogo").createRow(0).createCell(0).setCellValue("Isbn");
			workbook.write(out);
			return out.toByteArray();
		}
	}

	@Test
	void importWithRowsReplacesCatalog() throws Exception {
		mockMvc.perform(multipart("/catalogos/import")
				.file(csvUpload("LU100;Libro nuevo;Autor nuevo;Editorial nueva;978-9999999999;150,50;INF\n"))
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());

		assertThat(existingCatalogoCount()).isEqualTo(1);
		assertThat(jdbcTemplate.queryForObject(
				"SELECT cg_descripcion FROM cg_catalogo WHERE cg_creador=?", String.class, CREADOR_FINAL))
				.isEqualTo("Libro nuevo");
	}

	@Test
	void importEmptyFileIsRejectedAndKeepsCatalog() throws Exception {
		mockMvc.perform(multipart("/catalogos/import")
				.file(csvUpload(""))
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isBadRequest());

		assertThat(existingCatalogoCount()).isEqualTo(1);
	}

	@Test
	void importFileWithoutDataRowsIsRejectedAndKeepsCatalog() throws Exception {
		mockMvc.perform(multipart("/catalogos/import")
				.file(csvUpload("\n   \n\n"))
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isBadRequest());

		assertThat(existingCatalogoCount()).isEqualTo(1);
	}

	@Test
	void realXlsxImports() throws Exception {
		mockMvc.perform(multipart("/catalogos/import")
				.file(new MockMultipartFile("file", "catalogo.xlsx",
						"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", realXlsxBytes()))
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());

		assertThat(jdbcTemplate.queryForObject(
				"SELECT cg_descripcion FROM cg_catalogo WHERE cg_creador=?", String.class, CREADOR_FINAL))
				.isEqualTo("Libro xlsx");
	}

	@Test
	void numericIsbnIsStoredInFullNotInScientificNotation() throws Exception {
		mockMvc.perform(multipart("/catalogos/import")
				.file(new MockMultipartFile("file", "catalogo.xlsx",
						"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
						xlsxWithNumericIsbnBytes(9789871234567d)))
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());

		assertThat(jdbcTemplate.queryForObject(
				"SELECT cg_isbn FROM cg_catalogo WHERE cg_creador=?", String.class, CREADOR_FINAL))
				.isEqualTo("9789871234567");
		assertThat(jdbcTemplate.queryForObject(
				"SELECT cg_codigo_luongo FROM cg_catalogo WHERE cg_creador=?", String.class, CREADOR_FINAL))
				.isEqualTo("9789871234567");
	}

	@Test
	void numericIsbnEndingInZerosKeepsItsTrailingDigits() throws Exception {
		mockMvc.perform(multipart("/catalogos/import")
				.file(new MockMultipartFile("file", "catalogo.xlsx",
						"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
						xlsxWithNumericIsbnBytes(9789870000000d)))
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());

		assertThat(jdbcTemplate.queryForObject(
				"SELECT cg_isbn FROM cg_catalogo WHERE cg_creador=?", String.class, CREADOR_FINAL))
				.isEqualTo("9789870000000");
	}

	/**
	 * A numeric ISBN must come through whole even when the row also carries an explicitly formatted
	 * cell, which keeps taking POI's formatting. The price is deliberately not asserted here: a
	 * dot-decimal price is mangled by NewCatalogoProcessor's European parsing, which predates and is
	 * independent of the ISBN handling.
	 */
	@Test
	void numericIsbnSurvivesAlongsideAnExplicitlyFormattedCell() throws Exception {
		mockMvc.perform(multipart("/catalogos/import")
				.file(new MockMultipartFile("file", "catalogo.xlsx",
						"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
						xlsxWithNumericIsbnAndPriceBytes(9789871234567d, 150.5d)))
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());

		assertThat(jdbcTemplate.queryForObject(
				"SELECT cg_isbn FROM cg_catalogo WHERE cg_creador=?", String.class, CREADOR_FINAL))
				.isEqualTo("9789871234567");
	}

	@Test
	void wholeGeneralPriceImportsUnchanged() throws Exception {
		importGeneralPrice(28300d);
		assertThat(importedPrecio()).isEqualTo(28300d);
	}

	@Test
	void fractionalGeneralPriceKeepsItsDecimals() throws Exception {
		importGeneralPrice(24094.5d);
		assertThat(importedPrecio()).isEqualTo(24094.5d);
	}

	/** The spreadsheet stores prices like 6477.27 as 6477.27000000000044; the noise must not survive. */
	@Test
	void generalPriceWithFloatingPointNoiseIsRoundedToCents() throws Exception {
		importGeneralPrice(6477.27000000000044d);
		assertThat(importedPrecio()).isEqualTo(6477.27d);
	}

	@Test
	void textPriceWrittenTheEuropeanWayStillImports() throws Exception {
		mockMvc.perform(multipart("/catalogos/import")
				.file(new MockMultipartFile("file", "catalogo.xlsx",
						"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", realXlsxBytes()))
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());

		assertThat(importedPrecio()).isEqualTo(150.5d);
	}

	@Test
	void legacyXlsIsRejectedWithAClearMessageAndKeepsCatalog() throws Exception {
		mockMvc.perform(multipart("/catalogos/import")
				.file(new MockMultipartFile("file", "catalogo.xls", "application/vnd.ms-excel", realXlsBytes()))
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertThat(result.getResolvedException())
						.hasMessageContaining(".xlsx"));

		assertThat(existingCatalogoCount()).isEqualTo(1);
	}

	@Test
	void xlsxMislabeledAsXlsIsImportedOnItsRealFormat() throws Exception {
		mockMvc.perform(multipart("/catalogos/import")
				.file(new MockMultipartFile("file", "catalogo.xls", "application/vnd.ms-excel", realXlsxBytes()))
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());

		assertThat(jdbcTemplate.queryForObject(
				"SELECT cg_descripcion FROM cg_catalogo WHERE cg_creador=?", String.class, CREADOR_FINAL))
				.isEqualTo("Libro xlsx");
	}

	@Test
	void concurrentImportIsRejectedWhileAnotherIsRunning() throws Exception {
		ReentrantLock importLock = (ReentrantLock) ReflectionTestUtils.getField(controller, "importLock");
		CountDownLatch held = new CountDownLatch(1);
		CountDownLatch release = new CountDownLatch(1);

		// The lock is reentrant, so it must be held by a *different* thread to mimic a second request.
		Thread holder = new Thread(() -> {
			importLock.lock();
			held.countDown();
			try {
				release.await(10, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} finally {
				importLock.unlock();
			}
		});
		holder.start();
		assertThat(held.await(10, TimeUnit.SECONDS)).isTrue();

		try {
			mockMvc.perform(multipart("/catalogos/import")
					.file(csvUpload("LU100;Libro nuevo;Autor;Editorial;978-9999999999;150,50;INF\n"))
					.header("Authorization", "Bearer " + token))
					.andExpect(status().isConflict());
		} finally {
			release.countDown();
			holder.join(10_000);
		}

		// The rejected import must not have touched the catalog.
		assertThat(existingCatalogoCount()).isEqualTo(1);
		assertThat(jdbcTemplate.queryForObject(
				"SELECT cg_descripcion FROM cg_catalogo WHERE cg_creador=?", String.class, CREADOR_FINAL))
				.isEqualTo("Catalogo previo");
	}

	@Test
	void importWithDeleteOldRecordsFalseKeepsPreviousCatalog() throws Exception {
		mockMvc.perform(multipart("/catalogos/import")
				.file(csvUpload("LU100;Libro nuevo;Autor nuevo;Editorial nueva;978-9999999999;150,50;INF\n"))
				.param("deleteOldRecords", "false")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());

		assertThat(existingCatalogoCount()).isEqualTo(2);
	}
}
