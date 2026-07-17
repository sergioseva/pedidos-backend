package com.librosmario.pedidos.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.librosmario.pedidos.repository.ClienteRepository;

import jakarta.persistence.EntityManager;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class ClienteAuditTest {

	@Autowired
	private ClienteRepository repository;

	@Autowired
	private EntityManager em;

	private Cliente nuevoCliente(String nombre) {
		Cliente c = new Cliente();
		c.setNombre(nombre);
		c.setTelefonoMovil("3446123456");
		return c;
	}

	@Test
	void poblaCreatedYUpdatedAlInsertar() {
		Cliente guardado = repository.saveAndFlush(nuevoCliente("Nuevo"));
		assertThat(guardado.getCreatedAt()).isNotNull();
		assertThat(guardado.getUpdatedAt()).isNotNull();
	}

	/**
	 * The Spring Data REST PUT sends a detached cliente with no createdAt. updatable=false must keep
	 * Hibernate from writing that null over the original creation time.
	 */
	@Test
	void unUpdateNoBorraCreatedAt() {
		Integer id = repository.saveAndFlush(nuevoCliente("Original")).getId();
		em.clear();
		// Read createdAt back at the database's precision so the comparison isn't tripped by the
		// in-memory Instant carrying more digits than the column stores.
		Instant creado = repository.findById(id).orElseThrow().getCreatedAt();
		assertThat(creado).isNotNull();
		em.clear();

		// A fresh, detached cliente carrying the id but no createdAt -- exactly what the PUT builds.
		Cliente entrante = nuevoCliente("Modificado");
		entrante.setId(id);
		repository.saveAndFlush(entrante);
		em.clear();

		Cliente recargado = repository.findById(id).orElseThrow();
		assertThat(recargado.getNombre()).isEqualTo("Modificado");
		assertThat(recargado.getCreatedAt()).isEqualTo(creado);   // not wiped, not reset
		assertThat(recargado.getUpdatedAt()).isNotNull();
	}
}
