package com.librosmario.pedidos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.librosmario.pedidos.entity.Distribuidora;
import com.librosmario.pedidos.entity.PedidoDistribuidora;
import com.librosmario.pedidos.entity.PedidoItem;
import com.librosmario.pedidos.repository.DistribuidoraRepository;
import com.librosmario.pedidos.repository.PedidoDistribuidoraRepository;
import com.librosmario.pedidos.repository.PedidoItemRepository;




@SpringBootTest
public class PedidoDistribuidoraServiceTest {

	@Autowired
	PedidoItemService pedidoItemService;

	@Autowired
	DistribuidoraRepository distribuidoraRepository;

	@Autowired
	PedidoDistribuidoraService pedidoDistribuidoraService;

	@Autowired
	PedidoDistribuidoraRepository pedidoDistribuidoraRepository;

	@Autowired
	PedidoItemRepository pedidoItemsRepository;

	@Test
	public void newPedidoDistribuidoraShouldBeCreated() {

		Optional<Distribuidora> distribuidora=distribuidoraRepository.findById(1);
		assertTrue(distribuidora.isPresent());

		List<PedidoItem> pil=pedidoItemService.getAllPending();
		//blanqueo el pedido de los pedidoItems para simular que vienen del front end;
		pil.forEach((pi) -> pi.setPedido(null));
		assertTrue(pil.size() > 0, "There should be pending items to confirm");

		int cantItems=pedidoItemsRepository.findAll().size();
		int countBefore=pedidoDistribuidoraRepository.findAll().size();


		List<PedidoDistribuidora> result=pedidoDistribuidoraService.confirmarPedidoADistribuidora(pil, distribuidora.get());
		assertNotNull(result);
		assertEquals(pil.size(), result.size());

		// Items stay pending until arrival is confirmed
		assertEquals(pil.size(), pedidoItemService.getAllPending().size());

		// Count increased by new PDs minus deleted active ones
		int newCount = pedidoDistribuidoraRepository.findAll().size();
		assertTrue(newCount > countBefore, "Should have more PedidoDistribuidora records than before");
		//verifico que no haya aumentado la cantidad de pedidoItems

		assertEquals(cantItems,pedidoItemsRepository.findAll().size());
		//verifico que no me grabó el pedido en null
		assertNotNull(pedidoItemsRepository.findAll().get(0).getPedido());

	}

	@Test
	public void shouldFindByAnyWithDistribuidoraName() {
		List<PedidoDistribuidora> result = pedidoDistribuidoraService.findByAny("test", null, null);
		assertThat(result).isNotEmpty();
	}

	@Test
	public void shouldFindByAnyWithLibroName() {
		List<PedidoDistribuidora> result = pedidoDistribuidoraService.findByAny("libro1", null, null);
		assertThat(result).isNotEmpty();
	}

	@Test
	public void shouldFindByAnyWithDates() {
		List<PedidoDistribuidora> result = pedidoDistribuidoraService.findByAny("test", "2025-01-01", "2025-12-31");
		assertThat(result).isNotEmpty();
	}

	@Test
	public void shouldReturnEmptyForNonMatchingSearch() {
		List<PedidoDistribuidora> result = pedidoDistribuidoraService.findByAny("nonexistent_xyz", null, null);
		assertThat(result).isEmpty();
	}

	@Test
	public void confirmarLlegadaShouldMarkItemAsNotPending() {
		// Item 1 is pending and linked to PedidoDistribuidora 1
		PedidoItem item = pedidoItemsRepository.findById(1).orElseThrow();
		assertTrue(item.getPendiente());

		pedidoDistribuidoraService.confirmarLlegada(1);

		PedidoItem updated = pedidoItemsRepository.findById(1).orElseThrow();
		assertThat(updated.getPendiente()).isFalse();
	}

}
