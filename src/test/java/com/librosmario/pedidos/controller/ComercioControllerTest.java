package com.librosmario.pedidos.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
public class ComercioControllerTest {

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

	/** Ordenados por descripcion: 'Almacen Don Pedro' va antes que 'Hotel Costa Azul'. */
	@Test
	void getAllComercios() throws Exception {
		mockMvc.perform(get("/comercios")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0].descripcion").value("Almacen Don Pedro"));
	}

	@Test
	void getComercioById() throws Exception {
		mockMvc.perform(get("/comercios/1")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.descripcion").value("Hotel Costa Azul"))
				.andExpect(jsonPath("$.direccion").value("Costanera 100"));
	}

	@Test
	void getComercioNotFound() throws Exception {
		mockMvc.perform(get("/comercios/999")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isNotFound());
	}

	@Test
	void createComercio() throws Exception {
		mockMvc.perform(post("/comercios")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"descripcion\":\"Kiosco La Esquina\",\"direccion\":\"Urquiza 50\","
						+ "\"contacto\":\"Ana\",\"telefono\":\"03446-300300\",\"cuit\":\"27-33333333-3\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.descripcion").value("Kiosco La Esquina"))
				.andExpect(jsonPath("$.id").isNumber());
	}

	@Test
	void updateComercio() throws Exception {
		mockMvc.perform(put("/comercios/2")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"descripcion\":\"Almacen Don Pedro\",\"direccion\":\"San Martin 999\","
						+ "\"contacto\":\"Pedro\",\"telefono\":\"03446-200200\",\"cuit\":\"20-22222222-2\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.direccion").value("San Martin 999"));
	}

	/**
	 * Cada campo del formulario tiene que sobrevivir al guardado. La comision se agrego a la
	 * entidad despues que este servicio y quedo fuera del update: el formulario la mandaba, el
	 * backend la descartaba sin decir nada y todos los negocios quedaban sin comision.
	 */
	@Test
	void updateComercioGuardaLaComision() throws Exception {
		mockMvc.perform(put("/comercios/2")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"descripcion\":\"Almacen Don Pedro\",\"comision\":30.0}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.comision").value(30.0));

		mockMvc.perform(get("/comercios/2").header("Authorization", "Bearer " + token))
				.andExpect(jsonPath("$.comision").value(30.0));
	}

	/** Sacarle la comision a un negocio tambien tiene que persistir. */
	@Test
	void updateComercioPuedeBorrarLaComision() throws Exception {
		mockMvc.perform(put("/comercios/1")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"descripcion\":\"Hotel Costa Azul\"}"))
				.andExpect(status().isOk());

		mockMvc.perform(get("/comercios/1").header("Authorization", "Bearer " + token))
				.andExpect(jsonPath("$.comision").doesNotExist());
	}

	@Test
	void createComercioGuardaLaComision() throws Exception {
		mockMvc.perform(post("/comercios")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"descripcion\":\"Kiosco Nuevo\",\"comision\":15.5}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.comision").value(15.5));
	}

	@Test
	void deleteComercio() throws Exception {
		String response = mockMvc.perform(post("/comercios")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"descripcion\":\"Para borrar\"}"))
				.andReturn().getResponse().getContentAsString();

		String id = response.replaceAll(".*\"id\":(\\d+).*", "$1");

		mockMvc.perform(delete("/comercios/" + id)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isNoContent());
	}

	@Test
	void findByAnyBuscaEnDescripcionDireccionYContacto() throws Exception {
		mockMvc.perform(get("/comercios/search/findByAny")
				.header("Authorization", "Bearer " + token)
				.param("parametro", "Recepcion"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].descripcion").value("Hotel Costa Azul"));
	}

	/**
	 * El desplegable muestra cuantos ejemplares tiene cada negocio. El comercio 1 recibio 5 'El
	 * Principito', 2 'Martin Fierro', 1 'Zz Libro Clonado' y 1 mas con espacios: 9 en total.
	 */
	@Test
	void elResumenTraeLoQueTieneCadaNegocio() throws Exception {
		mockMvc.perform(get("/comercios/consignacion")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[?(@.descripcion == 'Hotel Costa Azul')].unidades").value(9))
				.andExpect(jsonPath("$[?(@.descripcion == 'Almacen Don Pedro')].unidades").value(4));
	}

	/** Un negocio sin nada tiene que seguir apareciendo: si no, no se lo puede elegir. */
	@Test
	void elResumenIncluyeLosNegociosSinLibros() throws Exception {
		mockMvc.perform(post("/comercios")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"descripcion\":\"Kiosco Vacio\"}"))
				.andExpect(status().isCreated());

		mockMvc.perform(get("/comercios/consignacion")
				.header("Authorization", "Bearer " + token))
				.andExpect(jsonPath("$[?(@.descripcion == 'Kiosco Vacio')].unidades").value(0));
	}

	@Test
	void elResumenTraeLaComisionParaNoPedirlaAparte() throws Exception {
		mockMvc.perform(get("/comercios/consignacion")
				.header("Authorization", "Bearer " + token))
				.andExpect(jsonPath("$[?(@.descripcion == 'Hotel Costa Azul')].comision").value(20.0));
	}

	@Test
	void elResumenRequiereAutenticacion() throws Exception {
		mockMvc.perform(get("/comercios/consignacion"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void requiresAuthentication() throws Exception {
		mockMvc.perform(get("/comercios"))
				.andExpect(status().isUnauthorized());
	}
}
