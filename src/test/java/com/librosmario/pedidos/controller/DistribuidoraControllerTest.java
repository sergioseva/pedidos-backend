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
public class DistribuidoraControllerTest {

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
	void getAllDistribuidoras() throws Exception {
		mockMvc.perform(get("/distribuidoras")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0].descripcion").value("Distribuidora test"));
	}

	@Test
	void getDistribuidoraById() throws Exception {
		mockMvc.perform(get("/distribuidoras/1")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.descripcion").value("Distribuidora test"));
	}

	@Test
	void getDistribuidoraNotFound() throws Exception {
		mockMvc.perform(get("/distribuidoras/999")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isNotFound());
	}

	@Test
	void createDistribuidora() throws Exception {
		mockMvc.perform(post("/distribuidoras")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"descripcion\":\"Nueva Dist\",\"nroCuenta\":\"ABC123\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.descripcion").value("Nueva Dist"))
				.andExpect(jsonPath("$.nroCuenta").value("ABC123"));
	}

	@Test
	void updateDistribuidora() throws Exception {
		mockMvc.perform(put("/distribuidoras/1")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"descripcion\":\"Updated\",\"nroCuenta\":\"999\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.descripcion").value("Updated"));
	}

	@Test
	void deleteDistribuidora() throws Exception {
		// Create one first so we don't break other tests' data
		String response = mockMvc.perform(post("/distribuidoras")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"descripcion\":\"To Delete\"}"))
				.andReturn().getResponse().getContentAsString();

		// Extract id from response
		String id = response.replaceAll(".*\"id\":(\\d+).*", "$1");

		mockMvc.perform(delete("/distribuidoras/" + id)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isNoContent());
	}

	@Test
	void findByAny() throws Exception {
		mockMvc.perform(get("/distribuidoras/search/findByAny")
				.header("Authorization", "Bearer " + token)
				.param("parametro", "test"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0].descripcion").value("Distribuidora test"));
	}

	@Test
	void requiresAuthentication() throws Exception {
		mockMvc.perform(get("/distribuidoras"))
				.andExpect(status().isUnauthorized());
	}
}
