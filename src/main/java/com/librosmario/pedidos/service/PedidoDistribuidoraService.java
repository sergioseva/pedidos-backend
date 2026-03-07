package com.librosmario.pedidos.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.librosmario.pedidos.entity.Distribuidora;
import com.librosmario.pedidos.entity.PedidoDistribuidora;
import com.librosmario.pedidos.entity.PedidoItem;
import com.librosmario.pedidos.repository.PedidoDistribuidoraRepository;
import com.librosmario.pedidos.repository.PedidoItemRepository;
import com.librosmario.pedidos.repository.specifications.PedidoDistribuidoraSpecifications;

@Service
public class PedidoDistribuidoraService {

	private static final Logger logger = LogManager.getLogger(PedidoDistribuidoraService.class);

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
		logger.info("Pedido a distribuidora '{}' confirmed with {} items (IDs: {})", distribuidora.getDescripcion(), managedItems.size(), itemIds);

		pedidoItemService.marcarComoNoPendientes(managedItems);
		logger.info("Marked {} items as no-pendientes", managedItems.size());
		return pdnew;
	}

	public List<PedidoDistribuidora> findByAny(String parametro, String fechaDesde, String fechaHasta) {
		Specification<PedidoDistribuidora> specification = Specification
				.where(PedidoDistribuidoraSpecifications.distribuidoraContains(parametro)
						.or(PedidoDistribuidoraSpecifications.itemLibroContains(parametro)));
		specification = addDates(specification, fechaDesde, fechaHasta);
		return pedidoADistribuidoraRepository.findAll(specification);
	}

	private Specification<PedidoDistribuidora> addDates(Specification<PedidoDistribuidora> specification, String fechaDesde, String fechaHasta) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		if (fechaDesde != null) {
			specification = specification.and(PedidoDistribuidoraSpecifications.fechaGreaterOrEquals(LocalDateTime.parse(fechaDesde + " 00:00:00", formatter)));
		}
		if (fechaHasta != null) {
			specification = specification.and(PedidoDistribuidoraSpecifications.fechaLessOrEquals(LocalDateTime.parse(fechaHasta + " 00:00:00", formatter)));
		}
		return specification;
	}

}
