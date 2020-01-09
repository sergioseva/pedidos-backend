package com.librosmario.pedidos.test.data;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.librosmario.pedidos.entity.PedidoItem;
import com.librosmario.pedidos.repository.PedidoItemRepository;

@RunWith(SpringRunner.class)
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
		
		List<PedidoItem> pitems=repository.findByPendienteTrue();
		assertEquals(3,pitems.size());
		
	}

}
