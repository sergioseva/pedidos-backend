package com.librosmario.pedidos.test.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.librosmario.pedidos.entity.Cliente;
import com.librosmario.pedidos.entity.Pedido;
import com.librosmario.pedidos.entity.PedidoItem;
import com.librosmario.pedidos.repository.ClienteRepository;
import com.librosmario.pedidos.repository.PedidoItemRepository;
import com.librosmario.pedidos.repository.PedidoRepository;
@RunWith(SpringRunner.class)
@DataJpaTest
public class PedidoRepositoryTest {

	@Autowired
	PedidoRepository repository;
	
	@Autowired
	ClienteRepository clienteRepository;
	
	@Test
	public void testFindAll() {
		List<Pedido> items= repository.findAll();
		assertEquals(2,items.size());
	}
	
	@Test
	public void testSavePedido() {
		
		Cliente c= clienteRepository.findById(1).orElseThrow(IllegalArgumentException::new);
		Pedido pedido= new Pedido();
		pedido.setSenia(100D);
		pedido.setTotal(1000D);
		pedido.setAdomicilio(false);
		pedido.setCliente(c);
		PedidoItem pedidoItem=new PedidoItem();
		pedidoItem.setAutor("autor");
		pedidoItem.setLibro("libro");
		pedidoItem.setCantidad(1);
		pedidoItem.setPendiente(true);
		List<PedidoItem> items= new ArrayList<PedidoItem>();
		items.add(pedidoItem);
		pedido.setPedidoItems(items);
		
		repository.save(pedido);
		
		Optional<Pedido> pedidoSaved=repository.findById(pedido.getId());
		assertTrue(pedidoSaved.isPresent());
		assertThat(pedidoSaved.get().getPedidoItems().get(0).getAutor()).isEqualTo("autor");
		assertThat(pedidoSaved.get().getPedidoItems().get(0).getLibro()).isEqualTo("libro");
		assertThat(pedidoSaved.get().getCliente().getNombre()).isEqualTo(c.getNombre());
		

		
		
	}

}
