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
}
