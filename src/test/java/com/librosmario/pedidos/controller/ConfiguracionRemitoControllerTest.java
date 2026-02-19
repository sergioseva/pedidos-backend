package com.librosmario.pedidos.controller;

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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class ConfiguracionRemitoControllerTest {

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
	void getConfiguracionRemito() throws Exception {
		mockMvc.perform(get("/configuracion-remito")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.remitente").value("Libros Mario - Remitente"));
	}

	@Test
	void updateConfiguracionRemito() throws Exception {
		mockMvc.perform(put("/configuracion-remito")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"remitente\":\"Nuevo Remitente\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.remitente").value("Nuevo Remitente"));
	}

	@Test
	void requiresAuthentication() throws Exception {
		mockMvc.perform(get("/configuracion-remito"))
				.andExpect(status().isUnauthorized());
	}
}
