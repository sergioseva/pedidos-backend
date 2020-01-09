package com.librosmario.pedidos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.librosmario.pedidos.entity.PedidoItem;
import com.librosmario.pedidos.repository.PedidoItemRepository;

@Service
public class PedidoItemService {

	@Autowired
	PedidoItemRepository repository;
	
	public List<PedidoItem> getAllPending(){
		return repository.findByPendienteTrue();
	}
	
	public boolean marcarComoNoPendientes(List<PedidoItem> items) {
		items.forEach((pi) -> pi.setPendiente(false));
		return repository.saveAll(items) != null;
	}
}
