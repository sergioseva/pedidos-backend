package com.librosmario.pedidos.test.data;

import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.librosmario.pedidos.entity.Distribuidora;
import com.librosmario.pedidos.entity.PedidoDistribuidora;
import com.librosmario.pedidos.repository.DistribuidoraRepository;
import com.librosmario.pedidos.repository.PedidoDistribuidoraRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class PedidoADistribuidoraRepositoryTest {

	@Autowired
	PedidoDistribuidoraRepository repository;
	
	@Autowired
	DistribuidoraRepository distribuidoraRepository;
	
	
	@Test
	public void shouldSaveTest() {
		Optional<Distribuidora> distribuidora=distribuidoraRepository.findById(1);
		assertTrue(distribuidora.isPresent());
		
		PedidoDistribuidora pd=new PedidoDistribuidora();
		pd.setDistribuidora(distribuidora.get());
		pd.setFecha(LocalDateTime.now());
		//pd.setItems(items);
		PedidoDistribuidora saved = repository.save(pd);
		assertTrue(saved.getId() != null);
		
	}


}
