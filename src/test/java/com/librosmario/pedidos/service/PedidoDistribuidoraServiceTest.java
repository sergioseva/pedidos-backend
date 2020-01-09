package com.librosmario.pedidos.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.librosmario.pedidos.controller.AuthController;
import com.librosmario.pedidos.entity.Distribuidora;
import com.librosmario.pedidos.entity.PedidoItem;
import com.librosmario.pedidos.repository.DistribuidoraRepository;
import com.librosmario.pedidos.repository.PedidoDistribuidoraRepository;
import com.librosmario.pedidos.repository.PedidoItemRepository;




@RunWith(SpringRunner.class)
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
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void newPedidoDistribuidoraShouldBeCreated() {

		Optional<Distribuidora> distribuidora=distribuidoraRepository.findById(1);
		assertTrue(distribuidora.isPresent());
		
		List<PedidoItem> pil=pedidoItemService.getAllPending();
		System.out.println("pendings count:"+pil.size());
		
		int cantItems=pedidoItemsRepository.findAll().size();
		
		boolean result=pedidoDistribuidoraService.confirmarPedidoADistribuidora(pil, distribuidora.get());
		assertTrue(result);
		
		assertEquals(0,pedidoItemService.getAllPending().size());
		
		assertEquals(1,pedidoDistribuidoraRepository.findAll().size());
		//verifico que no haya aumentado la cantidad de pedidoItems

		assertEquals(cantItems,pedidoItemsRepository.findAll().size());
		
	}

}
