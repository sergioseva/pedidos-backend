package com.librosmario.pedidos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.transaction.annotation.Transactional;

import com.librosmario.pedidos.entity.Cliente;
import com.librosmario.pedidos.entity.Pedido;
import com.librosmario.pedidos.entity.PedidoItem;
import com.librosmario.pedidos.repository.ClienteRepository;
import com.librosmario.pedidos.repository.PedidoItemRepository;
import com.librosmario.pedidos.repository.PedidoRepository;
import com.librosmario.pedidos.service.PedidoService;

@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class PedidoServiceTest {
	
	@Autowired
	PedidoService service;
	
	@Autowired
	PedidoRepository repository;

	@Autowired
	PedidoItemRepository pedidoItemRepository;
	
	@Autowired
	ClienteRepository clienteRepository;
	
	@Test
	@Transactional
	public void shouldCreatePedidoWithItems() {
		Pedido p= new Pedido();
		Cliente c= clienteRepository.findById(1).orElseThrow(IllegalArgumentException::new);
		p.setCliente(c);
		
		List<PedidoItem> pil=new ArrayList<PedidoItem>();
		pil.add(createPedidoItemTest("libro1"));
		pil.add(createPedidoItemTest("libro2"));
		p.setPedidoItems(pil);
		service.createPedido(p);
		
		Optional<Pedido> precovered=repository.findById(p.getId());
		
		assertTrue(precovered.isPresent());
		assertThat(precovered.get().getPedidoItems()).hasSize(2);
		assertThat(precovered.get().getPedidoItems().get(0).getLibro()).isEqualTo("libro1");
		assertThat(precovered.get().getPedidoItems().get(1).getLibro()).isEqualTo("libro2");

		List<PedidoItem> listPi= pedidoItemRepository.findByPedidoId(precovered.get().getId());
		assertThat(listPi).hasSize(2);
		
	}
	
	@Test
	public void shouldReturnNonExistingPedido() {
		Optional<Pedido> precovered=repository.findById(123);

		assertFalse(precovered.isPresent());
	}
	

	private PedidoItem createPedidoItemTest(String nombre) {
		PedidoItem pi= new PedidoItem();
		pi.setLibro(nombre);
		pi.setCantidad(1);
		pi.setAutor("test");
		
		return pi;
	}

}
