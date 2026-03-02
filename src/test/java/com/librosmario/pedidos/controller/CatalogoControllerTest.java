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
public class CatalogoControllerTest {

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
	void findByAnyWithoutAuth() throws Exception {
		mockMvc.perform(get("/catalogos/search/findByAny")
				.param("parametro", "test"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void findByAny() throws Exception {
		mockMvc.perform(get("/catalogos/search/findByAny")
				.header("Authorization", "Bearer " + token)
				.param("parametro", "test"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.page.totalElements").isNumber());
	}

	@Test
	void findByAnyWithColumnFilter() throws Exception {
		mockMvc.perform(get("/catalogos/search/findByAny")
				.header("Authorization", "Bearer " + token)
				.param("parametro", "sara")
				.param("autor", "Lark"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.page.totalElements").value(1))
				.andExpect(jsonPath("$.content[0].descripcion").value("Sara y las estrellas"));
	}

	@Test
	void findByAnyWithOnlyColumnFilter() throws Exception {
		mockMvc.perform(get("/catalogos/search/findByAny")
				.header("Authorization", "Bearer " + token)
				.param("descripcion", "sara"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.page.totalElements").value(2));
	}

	@Test
	void findByAnyWithObservacionesFilter() throws Exception {
		mockMvc.perform(get("/catalogos/search/findByAny")
				.header("Authorization", "Bearer " + token)
				.param("observaciones", "tecnica"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.page.totalElements").value(1))
				.andExpect(jsonPath("$.content[0].autor").value("Donald Knuth"));
	}

	@Test
	void findByAll() throws Exception {
		mockMvc.perform(get("/catalogos/search/findByAll")
				.header("Authorization", "Bearer " + token)
				.param("libro", "test"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isArray())
					.andExpect(jsonPath("$.page.totalElements").isNumber());
	}
}
