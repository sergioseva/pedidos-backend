package com.librosmario.pedidos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.librosmario.pedidos.entity.Pedido;
import com.librosmario.pedidos.entity.PedidoItem;
import com.librosmario.pedidos.repository.PedidoItemRepository;
import com.librosmario.pedidos.repository.PedidoRepository;

@Service
public class PedidoService {
	
	@Autowired
	PedidoRepository repository;
	
	@Autowired 
	PedidoItemRepository pedidoItemRepository;
	
	public Pedido createPedido(Pedido pedido){
		repository.save(pedido);
		pedido.getPedidoItems().forEach( (PedidoItem pi) -> 
											{pi.setPedido(pedido);
											pedidoItemRepository.save(pi);});
		return pedido;
		
	}

}
