package com.librosmario.pedidos.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
public class VentaControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenProvider tokenProvider;

	/** 'test' holds ROLE_ADMIN + ROLE_USER; 'vendedor' holds only ROLE_USER. */
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

	private String ventaJson(String extraCampos, String... items) {
		return "{" + extraCampos + "\"items\":[" + String.join(",", items) + "]}";
	}

	private String item(String isbn, String libro, int cantidad, double precio) {
		return "{\"isbn\":\"" + isbn + "\",\"libro\":\"" + libro + "\",\"autor\":\"A\",\"editorial\":\"E\","
				+ "\"cantidad\":" + cantidad + ",\"precio\":" + precio + "}";
	}

	// --- recording -------------------------------------------------------------------------

	@Test
	void registraUnaVentaYCalculaElTotal() throws Exception {
		mockMvc.perform(post("/ventas")
				.header("Authorization", "Bearer " + vendedorToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(ventaJson("", item("9789871051014", "Pan y manteca", 2, 100.5))))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.total").value(201.0))
				.andExpect(jsonPath("$.items.length()").value(1))
				.andExpect(jsonPath("$.fecha").exists())
				.andExpect(jsonPath("$.usuario").value("vendedor"));
	}

	/**
	 * The regression test the whole feature rests on: a till that believed the browser's total
	 * would be worthless as an accountability record.
	 */
	@Test
	void ignoraElTotalEnviadoPorElCliente() throws Exception {
		mockMvc.perform(post("/ventas")
				.header("Authorization", "Bearer " + vendedorToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(ventaJson("\"total\":1.0,",
						item("9789871051014", "Pan y manteca", 3, 1000.0))))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.total").value(3000.0));
	}

	/** Likewise the clock: a wrong workstation date would silently corrupt every daily report. */
	@Test
	void ignoraLaFechaEnviadaPorElCliente() throws Exception {
		mockMvc.perform(post("/ventas")
				.header("Authorization", "Bearer " + vendedorToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(ventaJson("\"fecha\":\"1999-01-01T00:00:00\",",
						item("9789871051014", "Pan y manteca", 1, 10.0))))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.fecha").value(org.hamcrest.Matchers.startsWith(
						java.time.LocalDate.now().toString())));
	}

	@Test
	void registraUnaVentaSinCliente() throws Exception {
		mockMvc.perform(post("/ventas")
				.header("Authorization", "Bearer " + vendedorToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(ventaJson("", item("9789871051014", "Pan y manteca", 1, 50.0))))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.cliente").doesNotExist());
	}

	@Test
	void registraUnaVentaConCliente() throws Exception {
		mockMvc.perform(post("/ventas")
				.header("Authorization", "Bearer " + vendedorToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(ventaJson("\"cliente\":{\"id\":1},",
						item("9789871051014", "Pan y manteca", 1, 50.0))))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.cliente.nombre").value("Cliente 1"));
	}

	/** An ng-select left untouched posts an empty object; that means "no cliente", not an error. */
	@Test
	void unClienteVacioSeGuardaComoSinCliente() throws Exception {
		mockMvc.perform(post("/ventas")
				.header("Authorization", "Bearer " + vendedorToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(ventaJson("\"cliente\":{},", item("9789871051014", "Pan", 1, 50.0))))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.cliente").doesNotExist());
	}

	@Test
	void clienteInexistenteDevuelve404NoUn500() throws Exception {
		mockMvc.perform(post("/ventas")
				.header("Authorization", "Bearer " + vendedorToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(ventaJson("\"cliente\":{\"id\":9999},", item("978", "Pan", 1, 50.0))))
				.andExpect(status().isNotFound());
	}

	// --- validation ------------------------------------------------------------------------

	@Test
	void rechazaUnaVentaSinItems() throws Exception {
		mockMvc.perform(post("/ventas")
				.header("Authorization", "Bearer " + vendedorToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"items\":[]}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void rechazaCantidadCero() throws Exception {
		mockMvc.perform(post("/ventas")
				.header("Authorization", "Bearer " + vendedorToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(ventaJson("", item("978", "Pan", 0, 50.0))))
				.andExpect(status().isBadRequest());
	}

	@Test
	void rechazaPrecioNegativo() throws Exception {
		mockMvc.perform(post("/ventas")
				.header("Authorization", "Bearer " + vendedorToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(ventaJson("", item("978", "Pan", 1, -5.0))))
				.andExpect(status().isBadRequest());
	}

	@Test
	void registrarRequiereAutenticacion() throws Exception {
		mockMvc.perform(post("/ventas")
				.contentType(MediaType.APPLICATION_JSON)
				.content(ventaJson("", item("978", "Pan", 1, 50.0))))
				.andExpect(status().isUnauthorized());
	}

	// --- access ----------------------------------------------------------------------------

	/**
	 * Asserts "denied", not a specific code. This app answers a method-security denial with 401
	 * rather than 403 -- verified against the running server, and true of the pre-existing
	 * /api/admin/users too, so it is app-wide SecurityConfig behaviour rather than anything about
	 * ventas. MockMvc reports 403 here. Pinning either number would make this test assert something
	 * untrue of one of the two environments; what must hold is simply that a vendedor gets nothing.
	 */
	@Test
	void unVendedorNoPuedeVerLasEstadisticas() throws Exception {
		mockMvc.perform(get("/ventas/estadisticas/porDia")
				.header("Authorization", "Bearer " + vendedorToken))
				.andExpect(status().is4xxClientError());
		mockMvc.perform(get("/ventas/estadisticas/resumen")
				.header("Authorization", "Bearer " + vendedorToken))
				.andExpect(status().is4xxClientError());
		mockMvc.perform(get("/ventas/search/findByAny")
				.header("Authorization", "Bearer " + vendedorToken))
				.andExpect(status().is4xxClientError());
	}

	@Test
	void unAdminSiPuedeVerLasEstadisticas() throws Exception {
		mockMvc.perform(get("/ventas/estadisticas/porDia")
				.header("Authorization", "Bearer " + adminToken))
				.andExpect(status().isOk());
	}

	@Test
	void unVendedorNoPuedeBorrarUnaVenta() throws Exception {
		mockMvc.perform(delete("/ventas/1")
				.header("Authorization", "Bearer " + vendedorToken))
				.andExpect(status().is4xxClientError());
	}

	// --- statistics ------------------------------------------------------------------------

	/**
	 * The fan-out guard. porDia joins the lines, which multiplies each Venta row once per line;
	 * counting tickets without distinct, or summing ve_total across that join, would report a
	 * 3-line sale as three sales and triple its money. Confident, wrong numbers are the worst
	 * possible failure for a report the owner uses to check the till.
	 */
	@Test
	void unaVentaConVariosItemsNoSeCuentaVariasVeces() throws Exception {
		mockMvc.perform(post("/ventas")
				.header("Authorization", "Bearer " + vendedorToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(ventaJson("",
						item("111", "Libro uno", 2, 100.0),
						item("222", "Libro dos", 1, 50.0),
						item("333", "Libro tres", 3, 10.0))))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.total").value(280.0));

		String hoy = java.time.LocalDate.now().toString();
		mockMvc.perform(get("/ventas/estadisticas/porDia")
				.header("Authorization", "Bearer " + adminToken)
				.param("fechaDesde", hoy).param("fechaHasta", hoy))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].cantidadVentas").value(1))   // one ticket, not three
				.andExpect(jsonPath("$[0].unidades").value(6))         // 2 + 1 + 3
				.andExpect(jsonPath("$[0].total").value(280.0));       // not 840
	}

	@Test
	void elResumenSumaLasVentasDelRango() throws Exception {
		mockMvc.perform(post("/ventas")
				.header("Authorization", "Bearer " + vendedorToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(ventaJson("", item("111", "Libro uno", 2, 100.0))))
				.andExpect(status().isCreated());
		mockMvc.perform(post("/ventas")
				.header("Authorization", "Bearer " + vendedorToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(ventaJson("", item("222", "Libro dos", 1, 50.0))))
				.andExpect(status().isCreated());

		String hoy = java.time.LocalDate.now().toString();
		mockMvc.perform(get("/ventas/estadisticas/resumen")
				.header("Authorization", "Bearer " + adminToken)
				.param("fechaDesde", hoy).param("fechaHasta", hoy))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.cantidadVentas").value(2))
				.andExpect(jsonPath("$.unidades").value(3))
				.andExpect(jsonPath("$.total").value(250.0))
				.andExpect(jsonPath("$.ticketPromedio").value(125.0));
	}

	@Test
	void buscaVentasPorIsbn() throws Exception {
		mockMvc.perform(post("/ventas")
				.header("Authorization", "Bearer " + vendedorToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(ventaJson("", item("9789871051014", "Pan y manteca", 1, 10.0))))
				.andExpect(status().isCreated());

		mockMvc.perform(get("/ventas/search/findByAll")
				.header("Authorization", "Bearer " + adminToken)
				.param("isbn", "9789871051014"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1));
	}

	@Test
	void fechaInvalidaDevuelve400() throws Exception {
		mockMvc.perform(get("/ventas/estadisticas/porDia")
				.header("Authorization", "Bearer " + adminToken)
				.param("fechaDesde", "ayer"))
				.andExpect(status().isBadRequest());
	}
}
