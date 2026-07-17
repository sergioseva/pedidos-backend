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

/**
 * The barcode lookup behind the till. Every case here is something a scanner can actually produce.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CatalogoIsbnLookupTest {

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
	void plainDigitIsbnIsFound() throws Exception {
		mockMvc.perform(get("/catalogos/isbn/9789871051014")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.descripcion").value("Pan y manteca"))
				.andExpect(jsonPath("$.precio").value(6477.27));
	}

	/** The catalog row stores '978-1111111111'; the scanner emits bare digits. */
	@Test
	void scannedDigitsMatchACatalogRowStoredWithDashes() throws Exception {
		mockMvc.perform(get("/catalogos/isbn/9781111111111")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.descripcion").value("Sara y las estrellas"));
	}

	/** Some readers inject the separators themselves. */
	@Test
	void isbnWithSeparatorsIsNormalizedBeforeLookup() throws Exception {
		mockMvc.perform(get("/catalogos/isbn/978-9871051014")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.descripcion").value("Pan y manteca"));
	}

	/** 404 is the contract the till relies on to offer manual entry instead of blocking the sale. */
	@Test
	void unknownIsbnReturns404() throws Exception {
		mockMvc.perform(get("/catalogos/isbn/9780000000000")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isNotFound());
	}

	@Test
	void lookupRequiresAuthentication() throws Exception {
		mockMvc.perform(get("/catalogos/isbn/9789871051014"))
				.andExpect(status().isUnauthorized());
	}
}
