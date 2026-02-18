package com.librosmario.pedidos.test.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.librosmario.pedidos.entity.PedidoItem;
import com.librosmario.pedidos.repository.PedidoItemRepository;

@DataJpaTest
public class PedidoItemRepositoryTest {

	@Autowired
	PedidoItemRepository repository;
	@Test
	public void testFindAll() {
		List<PedidoItem> items= repository.findAll();
		assertEquals(4,items.size());
	}
	
	@Test
	public void shouldReturnPendings() {
		
		List<PedidoItem> pitems=repository.findByPendienteTrueOrderByLibro();
		assertEquals(3,pitems.size());
		
	}

}
