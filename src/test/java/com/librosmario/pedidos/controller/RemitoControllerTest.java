package com.librosmario.pedidos.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
public class RemitoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenProvider tokenProvider;

	private String token;

	@BeforeEach
	void setUp() {
		Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken("test", "12345678"));
		token = tokenProvider.generateToken(auth);
	}

	@Test
	void getRemitoById() throws Exception {
		mockMvc.perform(get("/remitos/1")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.re_observaciones").value("Remito de prueba"));
	}

	@Test
	void getRemitoNotFound() throws Exception {
		mockMvc.perform(get("/remitos/999")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isNotFound());
	}

	@Test
	void createRemito() throws Exception {
		mockMvc.perform(post("/remitos")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"re_fecha\":\"2025-03-01\",\"re_observaciones\":\"Nuevo remito\","
						+ "\"re_distribuidora_ed\":{\"id\":1},"
						+ "\"items\":[{\"ri_nombre_libro\":\"Libro Test\",\"ri_cantidad\":2,\"ri_autor\":\"Autor Test\"}]}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.re_observaciones").value("Nuevo remito"))
				.andExpect(jsonPath("$.re_remito_k").isNumber());
	}

	@Test
	void createRemitoWithoutItems() throws Exception {
		mockMvc.perform(post("/remitos")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"re_fecha\":\"2025-03-01\",\"re_observaciones\":\"Sin items\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.re_observaciones").value("Sin items"));
	}

	@Test
	void updateRemito() throws Exception {
		mockMvc.perform(put("/remitos/1")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"re_fecha\":\"2025-04-01\",\"re_observaciones\":\"Actualizado\","
						+ "\"re_distribuidora_ed\":{\"id\":1}}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.re_observaciones").value("Actualizado"));
	}

	@Test
	void deleteRemito() throws Exception {
		// Create one first
		String response = mockMvc.perform(post("/remitos")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"re_fecha\":\"2025-05-01\",\"re_observaciones\":\"To delete\"}"))
				.andReturn().getResponse().getContentAsString();

		String id = response.replaceAll(".*\"re_remito_k\":(\\d+).*", "$1");

		mockMvc.perform(delete("/remitos/" + id)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isNoContent());
	}

	@Test
	void findByAny() throws Exception {
		mockMvc.perform(get("/remitos/search/findByAny")
				.header("Authorization", "Bearer " + token)
				.param("parametro", "prueba"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$").isNotEmpty());
	}

	@Test
	void findByAll() throws Exception {
		mockMvc.perform(get("/remitos/search/findByAll")
				.header("Authorization", "Bearer " + token)
				.param("distribuidora", "test"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray());
	}

	@Test
	void findByAllWithDates() throws Exception {
		mockMvc.perform(get("/remitos/search/findByAll")
				.header("Authorization", "Bearer " + token)
				.param("fechaDesde", "2025-01-01")
				.param("fechaHasta", "2025-12-31"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$").isNotEmpty());
	}

	@Test
	void requiresAuthentication() throws Exception {
		mockMvc.perform(get("/remitos/1"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void remitoExistenteQuedaComoDevolucion() throws Exception {
		mockMvc.perform(get("/remitos/1")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.re_tipo").value("DEVOLUCION"))
				.andExpect(jsonPath("$.re_comercio_cm").doesNotExist());
	}

	@Test
	void createRemitoConsignacion() throws Exception {
		mockMvc.perform(post("/remitos")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"re_fecha\":\"2025-06-01\",\"re_tipo\":\"CONSIGNACION\","
						+ "\"re_observaciones\":\"Entrega hotel\","
						+ "\"re_comercio_cm\":{\"id\":1},"
						+ "\"items\":[{\"ri_nombre_libro\":\"Libro Consig\",\"ri_cantidad\":2,\"ri_precio\":500.0}]}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.re_tipo").value("CONSIGNACION"))
				.andExpect(jsonPath("$.re_comercio_cm.descripcion").value("Hotel Costa Azul"))
				.andExpect(jsonPath("$.re_distribuidora_ed").doesNotExist())
				.andExpect(jsonPath("$.total").value(1000.0));
	}

	/** Un remito de consignacion no debe arrastrar una distribuidora aunque el cliente la mande. */
	@Test
	void consignacionIgnoraLaDistribuidora() throws Exception {
		mockMvc.perform(post("/remitos")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"re_fecha\":\"2025-06-01\",\"re_tipo\":\"CONSIGNACION\","
						+ "\"re_comercio_cm\":{\"id\":1},\"re_distribuidora_ed\":{\"id\":1}}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.re_distribuidora_ed").doesNotExist());
	}

	/** Un remito viejo sin re_tipo no puede desaparecer de la consulta de devoluciones. */
	@Test
	void findByAnyDevolucionIncluyeLosDeTipoNulo() throws Exception {
		mockMvc.perform(get("/remitos/search/findByAny")
				.header("Authorization", "Bearer " + token)
				.param("parametro", "heredado")
				.param("tipo", "DEVOLUCION"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].re_observaciones").value("Remito heredado"));
	}

	@Test
	void findByAnyFiltraPorTipo() throws Exception {
		mockMvc.perform(get("/remitos/search/findByAny")
				.header("Authorization", "Bearer " + token)
				.param("parametro", "")
				.param("tipo", "CONSIGNACION"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(3))
				.andExpect(jsonPath("$[?(@.re_tipo == \'DEVOLUCION\')]").isEmpty());
	}

	/**
	 * Con el destinatario nullable, un INNER JOIN sobre la distribuidora borraria las
	 * consignaciones del resultado aunque el termino matchee por otro lado.
	 */
	/** La consulta de consignacion pide los tres movimientos juntos. */
	@Test
	void findByAnyAceptaVariosTipos() throws Exception {
		mockMvc.perform(get("/remitos/search/findByAny")
				.header("Authorization", "Bearer " + token)
				.param("parametro", "")
				.param("tipo", "CONSIGNACION,RETIRO,VENTA_CONSIGNACION"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(3))
				.andExpect(jsonPath("$[?(@.re_tipo == 'DEVOLUCION')]").isEmpty());
	}

	@Test
	void findByAnyEncuentraConsignacionPorComercio() throws Exception {
		mockMvc.perform(get("/remitos/search/findByAny")
				.header("Authorization", "Bearer " + token)
				.param("parametro", "Hotel"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2));
	}

	@Test
	void findByAnyEncuentraConsignacionPorLibro() throws Exception {
		mockMvc.perform(get("/remitos/search/findByAny")
				.header("Authorization", "Bearer " + token)
				.param("parametro", "Rayuela"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].re_comercio_cm.descripcion").value("Almacen Don Pedro"));
	}

	/** Dos remitos al mismo comercio con el mismo titulo colapsan en una fila de 5 ejemplares. */
	@Test
	void estadoCuentaAgrupaPorComercioYTitulo() throws Exception {
		mockMvc.perform(get("/remitos/consignacion/estadocuenta")
				.header("Authorization", "Bearer " + token)
				.param("comercioId", "1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[?(@.nombreLibro == 'El Principito')].comercio")
						.value("Hotel Costa Azul"))
				.andExpect(jsonPath("$[?(@.nombreLibro == 'El Principito')].cantidad").value(5))
				.andExpect(jsonPath("$[?(@.nombreLibro == 'El Principito')].precio").value(1000.0))
				.andExpect(jsonPath("$[?(@.nombreLibro == 'El Principito')].subtotal").value(5000.0));
	}

	/** Sin comercioId trae todos los comercios; las devoluciones nunca entran. */
	@Test
	void estadoCuentaSoloIncluyeConsignaciones() throws Exception {
		mockMvc.perform(get("/remitos/consignacion/estadocuenta")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[?(@.nombreLibro == \'El Principito\')]").isNotEmpty())
				.andExpect(jsonPath("$[?(@.nombreLibro == \'Rayuela\')]").isNotEmpty())
				// 'Cien anos de soledad' esta en un remito de DEVOLUCION a distribuidora.
				.andExpect(jsonPath("$[?(@.nombreLibro == \'Cien anos de soledad\')]").isEmpty());
	}

	@Test
	void reporteConsignacionDevuelveUnXlsx() throws Exception {
		byte[] xlsx = mockMvc.perform(get("/remitos/consignacion/estadocuenta/reporte")
				.header("Authorization", "Bearer " + token)
				.param("comercioId", "1"))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-Disposition",
						org.hamcrest.Matchers.containsString(".xlsx")))
				.andReturn().getResponse().getContentAsByteArray();

		// Un .xlsx es un zip: si no arranca con PK no es un archivo valido.
		assertThat(xlsx.length).isGreaterThan(0);
		assertThat(xlsx[0]).isEqualTo((byte) 'P');
		assertThat(xlsx[1]).isEqualTo((byte) 'K');
	}

	@Test
	void reporteConsignacionDeUnComercioInexistente() throws Exception {
		mockMvc.perform(get("/remitos/consignacion/estadocuenta/reporte")
				.header("Authorization", "Bearer " + token)
				.param("comercioId", "999"))
				.andExpect(status().isNotFound());
	}

	@Test
	void reporteConsignacionRequiereAutenticacion() throws Exception {
		mockMvc.perform(get("/remitos/consignacion/estadocuenta/reporte").param("comercioId", "1"))
				.andExpect(status().isUnauthorized());
	}

	/** Los titulos del catalogo suelen venir con espacios adelante y no deben irse al principio. */
	@Test
	void estadoCuentaOrdenaPorTituloIgnorandoEspacios() throws Exception {
		mockMvc.perform(get("/remitos/consignacion/estadocuenta")
				.header("Authorization", "Bearer " + token)
				.param("comercioId", "1"))
				.andExpect(status().isOk())
				// Sin recortar, '  Martin Fierro' se iba al puesto 0 por sus espacios.
				.andExpect(jsonPath("$[0].nombreLibro").value("El Principito"))
				.andExpect(jsonPath("$[3].nombreLibro").value("Zz Libro Clonado"));
	}

	@Test
	void estadoCuentaFiltraPorFecha() throws Exception {
		mockMvc.perform(get("/remitos/consignacion/estadocuenta")
				.header("Authorization", "Bearer " + token)
				.param("comercioId", "1")
				.param("fechaDesde", "2025-03-10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].cantidad").value(2));
	}
}
