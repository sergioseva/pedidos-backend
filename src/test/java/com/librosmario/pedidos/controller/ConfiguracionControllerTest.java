package com.librosmario.pedidos.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.librosmario.pedidos.security.JwtTokenProvider;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class ConfiguracionControllerTest {

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
	void getConfiguracionPublic() throws Exception {
		// /configuracion is a public endpoint
		mockMvc.perform(get("/configuracion"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nombre").value("Libros Mario"))
				.andExpect(jsonPath("$.direccion").value("Calle Falsa 123"))
				.andExpect(jsonPath("$.telefono").value("011-1234567"));
	}

	@Test
	void updateConfiguracion() throws Exception {
		mockMvc.perform(put("/configuracion")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nombre\":\"Libros Mario Updated\",\"direccion\":\"Nueva Dir\",\"telefono\":\"555-0000\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nombre").value("Libros Mario Updated"))
				.andExpect(jsonPath("$.direccion").value("Nueva Dir"));
	}

	@Test
	void uploadLogo() throws Exception {
		MockMultipartFile file = new MockMultipartFile(
				"file", "logo.png", "image/png", new byte[]{1, 2, 3, 4});

		mockMvc.perform(multipart("/configuracion/logo")
				.file(file)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.hasLogo").value(true));
	}

	@Test
	void getLogoPublic() throws Exception {
		// /configuracion/logo is a public endpoint
		// First upload a logo
		MockMultipartFile file = new MockMultipartFile(
				"file", "logo.png", "image/png", new byte[]{1, 2, 3, 4});
		mockMvc.perform(multipart("/configuracion/logo")
				.file(file)
				.header("Authorization", "Bearer " + token));

		mockMvc.perform(get("/configuracion/logo"))
				.andExpect(status().isOk());
	}

	@Test
	void deleteLogo() throws Exception {
		// First upload a logo
		MockMultipartFile file = new MockMultipartFile(
				"file", "logo.png", "image/png", new byte[]{1, 2, 3, 4});
		mockMvc.perform(multipart("/configuracion/logo")
				.file(file)
				.header("Authorization", "Bearer " + token));

		mockMvc.perform(delete("/configuracion/logo")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());
	}

	@Test
	void getConfiguracionHasLogoFalseByDefault() throws Exception {
		mockMvc.perform(get("/configuracion"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.hasLogo").value(false));
	}
}
