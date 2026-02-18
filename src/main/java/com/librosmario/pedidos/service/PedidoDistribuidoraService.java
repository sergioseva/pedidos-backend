package com.librosmario.pedidos.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.librosmario.pedidos.entity.Distribuidora;
import com.librosmario.pedidos.entity.PedidoDistribuidora;
import com.librosmario.pedidos.entity.PedidoItem;
import com.librosmario.pedidos.repository.PedidoDistribuidoraRepository;
import com.librosmario.pedidos.repository.PedidoItemRepository;

@Service
public class PedidoDistribuidoraService {

	@Autowired
	PedidoItemService pedidoItemService;

	@Autowired
	PedidoDistribuidoraRepository pedidoADistribuidoraRepository;

	@Autowired
	PedidoItemRepository pedidoItemRepository;

	public PedidoDistribuidora confirmarPedidoADistribuidora(List<PedidoItem> items,Distribuidora distribuidora) {
		// Load managed entities from DB to avoid Hibernate HHH000502 warnings
		// (deserialized items have the immutable 'pedido' field set from JSON)
		List<Integer> itemIds = items.stream().map(PedidoItem::getId).collect(Collectors.toList());
		List<PedidoItem> managedItems = pedidoItemRepository.findAllById(itemIds);

		PedidoDistribuidora pd=new PedidoDistribuidora();
		pd.setDistribuidora(distribuidora);
		pd.setFecha(LocalDateTime.now());
		pd.setItems(managedItems);

		for (PedidoItem pi:managedItems ) {
			pi.getPedidosADistribuidoras().add(pd);
		}

		PedidoDistribuidora pdnew =pedidoADistribuidoraRepository.save(pd);

		pedidoItemService.marcarComoNoPendientes(managedItems);
		return pdnew;
	}

}
