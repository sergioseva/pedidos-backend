package com.librosmario.pedidos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.transaction.annotation.Transactional;

import com.librosmario.pedidos.entity.Distribuidora;
import com.librosmario.pedidos.exception.ResourceNotFoundException;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DistribuidoraServiceTest {

	@Autowired
	DistribuidoraService service;

	@Test
	public void shouldFindAll() {
		List<Distribuidora> distribuidoras = service.findAll();
		assertThat(distribuidoras).hasSizeGreaterThanOrEqualTo(2);
	}

	@Test
	public void shouldFindById() {
		Distribuidora distribuidora = service.findById(1);
		assertThat(distribuidora).isNotNull();
		assertThat(distribuidora.getDescripcion()).isEqualTo("Distribuidora test");
	}

	@Test
	public void shouldThrowWhenNotFound() {
		assertThrows(ResourceNotFoundException.class, () -> service.findById(999));
	}

	@Test
	@Transactional
	public void shouldCreateDistribuidora() {
		Distribuidora d = new Distribuidora();
		d.setDescripcion("Nueva Distribuidora");
		d.setNroCuenta("12345");

		Distribuidora created = service.create(d);
		assertThat(created.getId()).isGreaterThan(0);
		assertThat(created.getDescripcion()).isEqualTo("Nueva Distribuidora");
		assertThat(created.getNroCuenta()).isEqualTo("12345");
	}

	@Test
	@Transactional
	public void shouldUpdateDistribuidora() {
		Distribuidora details = new Distribuidora();
		details.setDescripcion("Updated Descripcion");
		details.setNroCuenta("99999");

		Distribuidora updated = service.update(1, details);
		assertThat(updated.getDescripcion()).isEqualTo("Updated Descripcion");
		assertThat(updated.getNroCuenta()).isEqualTo("99999");
	}

	@Test
	@Transactional
	public void shouldDeleteDistribuidora() {
		Distribuidora d = new Distribuidora();
		d.setDescripcion("To Delete");
		Distribuidora created = service.create(d);

		service.delete(created.getId());
		assertThrows(ResourceNotFoundException.class, () -> service.findById(created.getId()));
	}

	@Test
	public void shouldFindByAny() {
		List<Distribuidora> result = service.findByAny("test");
		assertThat(result).isNotEmpty();
	}
}
