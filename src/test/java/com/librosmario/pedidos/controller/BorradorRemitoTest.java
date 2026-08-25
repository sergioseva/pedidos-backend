package com.librosmario.pedidos.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

/**
 * El borrador del remito en curso. Guardarlo solo en el navegador no alcanzaba: se pierde si el
 * navegador limpia los datos al cerrarse y no acompana al operador entre maquinas.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class BorradorRemitoTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtTokenProvider tokenProvider;

	private String token;
	private String tokenOtro;

	@BeforeEach
	void setUp() {
		token = tokenDe("test");
		tokenOtro = tokenDe("vendedor");
	}

	private String tokenDe(String usuario) {
		Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(usuario, "12345678"));
		return tokenProvider.generateToken(auth);
	}

	private void guardar(String jwt, String tipo, String contenido) throws Exception {
		mockMvc.perform(put("/remitos/borrador")
				.header("Authorization", "Bearer " + jwt)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"tipo\":\"" + tipo + "\",\"contenido\":\"" + contenido + "\"}"))
				.andExpect(status().isNoContent());
	}

	@Test
	void guardaYDevuelveLoQueSeLlevaCargado() throws Exception {
		guardar(token, "CONSIGNACION", "items-a-medias");

		mockMvc.perform(get("/remitos/borrador")
				.header("Authorization", "Bearer " + token).param("tipo", "CONSIGNACION"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.contenido").value("items-a-medias"));
	}

	/** Es un autoguardado: cada guardado pisa al anterior en vez de acumular borradores. */
	@Test
	void guardarDeNuevoPisaElAnterior() throws Exception {
		guardar(token, "CONSIGNACION", "primero");
		guardar(token, "CONSIGNACION", "segundo");

		mockMvc.perform(get("/remitos/borrador")
				.header("Authorization", "Bearer " + token).param("tipo", "CONSIGNACION"))
				.andExpect(jsonPath("$.contenido").value("segundo"));
	}

	@Test
	void sinBorradorDevuelveVacio() throws Exception {
		mockMvc.perform(get("/remitos/borrador")
				.header("Authorization", "Bearer " + token).param("tipo", "DEVOLUCION"))
				.andExpect(status().isNoContent());
	}

    /** Un borrador de consignacion no puede aparecer en la pantalla de devoluciones. */
	@Test
	void losBorradoresNoSeMezclanEntreTipos() throws Exception {
		guardar(token, "CONSIGNACION", "de-consignacion");

		mockMvc.perform(get("/remitos/borrador")
				.header("Authorization", "Bearer " + token).param("tipo", "DEVOLUCION"))
				.andExpect(status().isNoContent());
	}

	/** El borrador es de quien lo carga: el de un operador no le aparece a otro. */
	@Test
	void elBorradorEsDeCadaUsuario() throws Exception {
		guardar(token, "CONSIGNACION", "lo-mio");

		mockMvc.perform(get("/remitos/borrador")
				.header("Authorization", "Bearer " + tokenOtro).param("tipo", "CONSIGNACION"))
				.andExpect(status().isNoContent());
	}

	@Test
	void cadaUsuarioConservaElSuyo() throws Exception {
		guardar(token, "CONSIGNACION", "lo-mio");
		guardar(tokenOtro, "CONSIGNACION", "lo-suyo");

		mockMvc.perform(get("/remitos/borrador")
				.header("Authorization", "Bearer " + token).param("tipo", "CONSIGNACION"))
				.andExpect(jsonPath("$.contenido").value("lo-mio"));
	}

	@Test
	void seBorraAlTerminar() throws Exception {
		guardar(token, "CONSIGNACION", "algo");

		mockMvc.perform(delete("/remitos/borrador")
				.header("Authorization", "Bearer " + token).param("tipo", "CONSIGNACION"))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/remitos/borrador")
				.header("Authorization", "Bearer " + token).param("tipo", "CONSIGNACION"))
				.andExpect(status().isNoContent());
	}

	@Test
	void borrarLoQueNoExisteNoFalla() throws Exception {
		mockMvc.perform(delete("/remitos/borrador")
				.header("Authorization", "Bearer " + token).param("tipo", "DEVOLUCION"))
				.andExpect(status().isNoContent());
	}

	/** Una carga larga puede ser de decenas de libros: el contenido no puede quedar corto. */
	@Test
	void soportaUnaCargaGrande() throws Exception {
		String grande = "x".repeat(200000);
		guardar(token, "CONSIGNACION", grande);

		mockMvc.perform(get("/remitos/borrador")
				.header("Authorization", "Bearer " + token).param("tipo", "CONSIGNACION"))
				.andExpect(jsonPath("$.contenido").value(grande));
	}

	@Test
	void requiereAutenticacion() throws Exception {
		mockMvc.perform(get("/remitos/borrador").param("tipo", "CONSIGNACION"))
				.andExpect(status().isUnauthorized());
	}
}
