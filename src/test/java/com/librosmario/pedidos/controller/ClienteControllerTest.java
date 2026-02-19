package com.librosmario.pedidos.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import com.librosmario.pedidos.security.JwtTokenProvider;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ClienteControllerTest {

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
	void findByName() throws Exception {
		mockMvc.perform(get("/clientes/search/findByName")
				.header("Authorization", "Bearer " + token)
				.param("name", "Cliente 1"))
				.andExpect(status().isOk());
	}

	@Test
	void findByPhone() throws Exception {
		mockMvc.perform(get("/clientes/search/findByPhone")
				.header("Authorization", "Bearer " + token)
				.param("phone", "1111111"))
				.andExpect(status().isOk());
	}

	@Test
	void findByAny() throws Exception {
		mockMvc.perform(get("/clientes/search/findByAny")
				.header("Authorization", "Bearer " + token)
				.param("parametro", "Cliente"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray());
	}

	@Test
	void checkPedidosTrue() throws Exception {
		mockMvc.perform(get("/clientes/checkPedidos/1")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());
	}

	@Test
	void checkPedidosFalse() throws Exception {
		mockMvc.perform(get("/clientes/checkPedidos/3")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());
	}

	@Test
	void findByNameWithoutAuth() throws Exception {
		mockMvc.perform(get("/clientes/search/findByName")
				.param("name", "test"))
				.andExpect(status().isUnauthorized());
	}
}
