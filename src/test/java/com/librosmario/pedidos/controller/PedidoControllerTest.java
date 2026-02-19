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
public class PedidoControllerTest {

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
	void createPedido() throws Exception {
		String json = """
				{
					"cliente": {"id": 1},
					"adomicilio": false,
					"pedidoItems": [
						{"libro": "Test Book 1", "cantidad": 1, "autor": "Author 1", "pedidoAeditorial": {"id": 1}},
						{"libro": "Test Book 2", "cantidad": 2, "autor": "Author 2", "pedidoAeditorial": {"id": 1}}
					]
				}
				""";
		mockMvc.perform(post("/pedidos")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists());
	}

	@Test
	void createPedidoWithoutAuth() throws Exception {
		mockMvc.perform(post("/pedidos")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"cliente\":{\"id\":1},\"adomicilio\":false,\"pedidoItems\":[]}"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void findByAll() throws Exception {
		mockMvc.perform(get("/pedidos/search/findByAll")
				.header("Authorization", "Bearer " + token)
				.param("libro", "libro1"))
				.andExpect(status().isOk());
	}

	@Test
	void findByAny() throws Exception {
		mockMvc.perform(get("/pedidos/search/findByAny")
				.header("Authorization", "Bearer " + token)
				.param("parametro", "Cliente"))
				.andExpect(status().isOk());
	}
}
