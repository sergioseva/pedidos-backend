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
public class UserControllerTest {

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
	void getCurrentUser() throws Exception {
		mockMvc.perform(get("/api/user/me")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("test"))
				.andExpect(jsonPath("$.name").value("test"));
	}

	@Test
	void getCurrentUserWithoutAuth() throws Exception {
		mockMvc.perform(get("/api/user/me"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void checkUsernameAvailabilityExisting() throws Exception {
		mockMvc.perform(get("/api/user/checkUsernameAvailability")
				.param("username", "test"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.available").value(false));
	}

	@Test
	void checkUsernameAvailabilityNew() throws Exception {
		mockMvc.perform(get("/api/user/checkUsernameAvailability")
				.param("username", "nonexistent"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.available").value(true));
	}

	@Test
	void checkEmailAvailability() throws Exception {
		mockMvc.perform(get("/api/user/checkEmailAvailability")
				.param("email", "test@test.com"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.available").value(false));
	}

	@Test
	void getUserProfile() throws Exception {
		mockMvc.perform(get("/api/users/test")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("test"))
				.andExpect(jsonPath("$.name").value("test"));
	}
}
