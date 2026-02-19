package com.librosmario.pedidos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.transaction.annotation.Transactional;

import com.librosmario.pedidos.entity.Distribuidora;
import com.librosmario.pedidos.entity.Remito;
import com.librosmario.pedidos.entity.RemitoItem;
import com.librosmario.pedidos.exception.ResourceNotFoundException;
import com.librosmario.pedidos.repository.DistribuidoraRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RemitoServiceTest {

	@Autowired
	RemitoService service;

	@Autowired
	DistribuidoraRepository distribuidoraRepository;

	@Test
	public void shouldFindById() {
		Remito remito = service.findById(1);
		assertThat(remito).isNotNull();
		assertThat(remito.getRe_observaciones()).isEqualTo("Remito de prueba");
	}

	@Test
	public void shouldThrowWhenNotFound() {
		assertThrows(ResourceNotFoundException.class, () -> service.findById(999));
	}

	@Test
	@Transactional
	public void shouldCreateRemitoWithItems() {
		Remito remito = new Remito();
		remito.setRe_fecha(new Date());
		remito.setRe_observaciones("Test remito");

		Distribuidora dist = new Distribuidora();
		dist.setId(1);
		remito.setRe_distribuidora_ed(dist);

		List<RemitoItem> items = new ArrayList<>();
		RemitoItem item = new RemitoItem();
		item.setRi_nombre_libro("Test Libro");
		item.setRi_cantidad(3);
		item.setRi_autor("Test Autor");
		items.add(item);
		remito.setItems(items);

		Remito created = service.createRemito(remito);
		assertThat(created.getRe_remito_k()).isNotNull();
		assertThat(created.getRe_observaciones()).isEqualTo("Test remito");
		assertThat(created.getItems()).hasSize(1);
		assertThat(created.getRe_distribuidora_ed()).isNotNull();
	}

	@Test
	@Transactional
	public void shouldCreateRemitoWithoutItems() {
		Remito remito = new Remito();
		remito.setRe_fecha(new Date());
		remito.setRe_observaciones("Empty remito");

		Remito created = service.createRemito(remito);
		assertThat(created.getRe_remito_k()).isNotNull();
		assertThat(created.getRe_observaciones()).isEqualTo("Empty remito");
	}

	@Test
	@Transactional
	public void shouldUpdateRemito() {
		Remito remitoDetails = new Remito();
		remitoDetails.setRe_fecha(new Date());
		remitoDetails.setRe_observaciones("Updated observaciones");

		Distribuidora dist = new Distribuidora();
		dist.setId(2);
		remitoDetails.setRe_distribuidora_ed(dist);

		Remito updated = service.updateRemito(1, remitoDetails);
		assertThat(updated.getRe_observaciones()).isEqualTo("Updated observaciones");
	}

	@Test
	@Transactional
	public void shouldDeleteRemito() {
		// Create a remito to delete
		Remito remito = new Remito();
		remito.setRe_fecha(new Date());
		remito.setRe_observaciones("To delete");
		Remito created = service.createRemito(remito);

		service.deleteRemito(created.getRe_remito_k());
		assertThrows(ResourceNotFoundException.class, () -> service.findById(created.getRe_remito_k()));
	}

	@Test
	public void shouldFindByAll() {
		List<Remito> result = service.findByAll("test", null, null, null);
		assertThat(result).isNotEmpty();
	}

	@Test
	public void shouldFindByAny() {
		List<Remito> result = service.findByAny("prueba", null, null);
		assertThat(result).isNotEmpty();
	}

	@Test
	public void shouldFindByAllWithDates() {
		List<Remito> result = service.findByAll(null, null, "2025-01-01", "2025-12-31");
		assertThat(result).isNotEmpty();
	}
}
