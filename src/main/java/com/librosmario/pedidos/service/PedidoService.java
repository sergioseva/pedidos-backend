package com.librosmario.pedidos.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.librosmario.pedidos.entity.Pedido;
import com.librosmario.pedidos.entity.PedidoItem;
import com.librosmario.pedidos.repository.PedidoItemRepository;
import com.librosmario.pedidos.repository.PedidoRepository;
import com.librosmario.pedidos.repository.specifications.PedidoSpecifications;

@Service
public class PedidoService {

	private static final Logger logger = LogManager.getLogger(PedidoService.class);

	@Autowired
	PedidoRepository repository;
	
	@Autowired 
	PedidoItemRepository pedidoItemRepository;
	
	Specification<Pedido> specification;
	
	public Pedido createPedido(Pedido pedido){
        for (PedidoItem pi : pedido.getPedidoItems()) {
            pi.setPedido(pedido);
        }

        repository.save(pedido);
		logger.info("Pedido created for client '{}' with {} items", pedido.getCliente().getNombre(), pedido.getPedidoItems().size());

		return pedido;
		
	}
	
	public List<Pedido> findByAll(String libro,String cliente, String fechaDesde, String fechaHasta ) {

		specification = Specification
				.where(libro == null ? null : PedidoSpecifications.getPedidosConLibro(libro))
				.and(cliente == null ? null : PedidoSpecifications.clienteContains(cliente));
		addDates(fechaDesde,fechaHasta);
		return repository.findAll(specification); 
	}
	
	public List<Pedido> findByAny(String expression, String fechaDesde, String fechaHasta ) {
		specification = Specification
				.where(PedidoSpecifications.getPedidosConLibro(expression).or(PedidoSpecifications.clienteContains(expression)))
				;
		addDates(fechaDesde,fechaHasta);
		return repository.findAll(specification); 
	}
	
	private void addDates(String fechaDesde, String fechaHasta){
    	
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    	specification=specification.and(fechaDesde == null ? null : PedidoSpecifications.pedidoFechaGreaterOrEquals(LocalDateTime.parse(fechaDesde + " 00:00:00", formatter)));
    	specification=specification.and(fechaHasta == null ? null : PedidoSpecifications.pedidoFechaLessOrEquals(LocalDateTime.parse(fechaHasta + " 00:00:00", formatter)));
		;
		
	}

}
