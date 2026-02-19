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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class UserAdminControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenProvider tokenProvider;

	private String token;

	@BeforeEach
	void setUp() {
		// test user has ROLE_ADMIN role from data.sql
		Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken("test", "12345678"));
		token = tokenProvider.generateToken(auth);
	}

	@Test
	void getAllUsers() throws Exception {
		mockMvc.perform(get("/api/admin/users")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0].username").value("test"));
	}

	@Test
	void getUserById() throws Exception {
		mockMvc.perform(get("/api/admin/users/1")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("test"))
				.andExpect(jsonPath("$.email").value("test@test.com"));
	}

	@Test
	void getUserNotFound() throws Exception {
		mockMvc.perform(get("/api/admin/users/999")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isNotFound());
	}

	@Test
	void updateUser() throws Exception {
		mockMvc.perform(put("/api/admin/users/1")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Updated Name\",\"username\":\"test\",\"email\":\"test@test.com\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Updated Name"));
	}

	@Test
	void updateUserDuplicateUsername() throws Exception {
		// First create another user via signup
		mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/auth/signup")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Other\",\"username\":\"other\",\"email\":\"other@test.com\",\"password\":\"12345678\"}"));

		// Try to update first user with taken username
		mockMvc.perform(put("/api/admin/users/1")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"test\",\"username\":\"other\",\"email\":\"test@test.com\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void updateUserWithPassword() throws Exception {
		mockMvc.perform(put("/api/admin/users/1")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"test\",\"username\":\"test\",\"email\":\"test@test.com\",\"password\":\"newpassword\"}"))
				.andExpect(status().isOk());
	}

	@Test
	void deleteUser() throws Exception {
		// Create a user first
		mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/auth/signup")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"To Delete\",\"username\":\"todelete\",\"email\":\"todelete@test.com\",\"password\":\"12345678\"}"));

		// Get the user list to find the new user's id
		String response = mockMvc.perform(get("/api/admin/users")
				.header("Authorization", "Bearer " + token))
				.andReturn().getResponse().getContentAsString();

		// Find the id of user with username "todelete"
		// The response is a JSON array, extract the id
		String id = response.replaceAll(".*\"id\":(\\d+),\"name\":\"To Delete\".*", "$1");

		mockMvc.perform(delete("/api/admin/users/" + id)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true));
	}

	@Test
	void deleteUserNotFound() throws Exception {
		mockMvc.perform(delete("/api/admin/users/999")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isNotFound());
	}

	@Test
	void requiresAuthentication() throws Exception {
		mockMvc.perform(get("/api/admin/users"))
				.andExpect(status().isUnauthorized());
	}
}
