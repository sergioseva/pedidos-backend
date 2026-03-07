package com.librosmario.pedidos.controller;

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
public class PedidoADistribuidoraControllerTest {

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
	void confirmarPedidoADistribuidora() throws Exception {
		String json = """
				{
					"items": [
						{"id": 1, "libro": "libro1", "cantidad": 1, "pendiente": true, "pedidoAeditorial": {"id": 1}},
						{"id": 3, "libro": "libro3", "cantidad": 1, "pendiente": true, "pedidoAeditorial": {"id": 1}}
					],
					"distribuidora": {"id": 1}
				}
				""";
		mockMvc.perform(post("/pedidodistribuidora")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andExpect(status().isCreated());
	}

	@Test
	void confirmarPedidoWithoutAuth() throws Exception {
		mockMvc.perform(post("/pedidodistribuidora")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"items\":[],\"distribuidora\":{\"id\":1}}"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void findByAny() throws Exception {
		mockMvc.perform(get("/pedidosdistribuidora/search/findByAny")
				.header("Authorization", "Bearer " + token)
				.param("parametro", "test"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$").isNotEmpty());
	}

	@Test
	void findByAnyWithDates() throws Exception {
		mockMvc.perform(get("/pedidosdistribuidora/search/findByAny")
				.header("Authorization", "Bearer " + token)
				.param("parametro", "test")
				.param("fechaDesde", "2025-01-01")
				.param("fechaHasta", "2025-12-31"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$").isNotEmpty());
	}

	@Test
	void findByAnyByLibro() throws Exception {
		mockMvc.perform(get("/pedidosdistribuidora/search/findByAny")
				.header("Authorization", "Bearer " + token)
				.param("parametro", "libro1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$").isNotEmpty());
	}

	@Test
	void findByAnyRequiresAuthentication() throws Exception {
		mockMvc.perform(get("/pedidosdistribuidora/search/findByAny")
				.param("parametro", "test"))
				.andExpect(status().isUnauthorized());
	}
}
