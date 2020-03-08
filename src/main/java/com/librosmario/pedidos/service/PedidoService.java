package com.librosmario.pedidos.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
	
	@Autowired
	PedidoRepository repository;
	
	@Autowired 
	PedidoItemRepository pedidoItemRepository;
	
	Specification<Pedido> specification;
	
	public Pedido createPedido(Pedido pedido){
		pedido.getPedidoItems().forEach( (PedidoItem pi) -> 
		{pi.setPedido(pedido);
		});
		
		repository.save(pedido);

		return pedido;
		
	}
	
	public List<Pedido> findByAll(String libro,String cliente, String fechaDesde, String fechaHasta ) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		
		specification = Specification
				.where(libro == null ? null : PedidoSpecifications.getPedidosConLibro(libro))
				.and(cliente == null ? null : PedidoSpecifications.clienteContains(cliente))
//				.and(fechaDesde == null ? null : PedidoSpecifications.pedidoFechaGreaterOrEquals(LocalDateTime.parse(fechaDesde + " 00:00:00", formatter)))
//				.and(fechaHasta == null ? null : PedidoSpecifications.pedidoFechaLessOrEquals(LocalDateTime.parse(fechaHasta + " 00:00:00", formatter)))
				;
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
