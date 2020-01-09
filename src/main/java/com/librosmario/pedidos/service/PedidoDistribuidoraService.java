package com.librosmario.pedidos.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.librosmario.pedidos.entity.Distribuidora;
import com.librosmario.pedidos.entity.PedidoDistribuidora;
import com.librosmario.pedidos.entity.PedidoItem;
import com.librosmario.pedidos.repository.PedidoDistribuidoraRepository;

@Service
public class PedidoDistribuidoraService {
	
	@Autowired
	PedidoItemService pedidoItemService;
	
	@Autowired
	PedidoDistribuidoraRepository pedidoADistribuidoraRepository;
	
	public boolean confirmarPedidoADistribuidora(List<PedidoItem> items,Distribuidora distribuidora) {
		PedidoDistribuidora pd=new PedidoDistribuidora();
		pd.setDistribuidora(distribuidora);
		pd.setFecha(LocalDateTime.now());
		pd.setItems(items);
		pedidoADistribuidoraRepository.save(pd);
		
		return pedidoItemService.marcarComoNoPendientes(items);
	}

}
