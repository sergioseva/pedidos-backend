package com.librosmario.pedidos.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Transactional
	public List<PedidoDistribuidora> confirmarPedidoADistribuidora(List<PedidoItem> items, Distribuidora distribuidora) {
		List<Integer> itemIds = items.stream().map(PedidoItem::getId).collect(Collectors.toList());
		List<PedidoItem> managedItems = pedidoItemRepository.findAllById(itemIds);

		List<PedidoDistribuidora> result = new ArrayList<>();
		for (PedidoItem pi : managedItems) {
			// Delete any existing active (non-realizado) PedidoDistribuidora for this item
			Optional<PedidoDistribuidora> existing = pi.getPedidosADistribuidoras().stream()
					.filter(pd -> !pd.isRealizado())
					.findFirst();
			if (existing.isPresent()) {
				PedidoDistribuidora oldPd = existing.get();
				pi.getPedidosADistribuidoras().remove(oldPd);
				pedidoADistribuidoraRepository.delete(oldPd);
				logger.info("Deleted old PedidoDistribuidora {} for item {}", oldPd.getId(), pi.getId());
			}

			// Create a new PedidoDistribuidora for this item
			PedidoDistribuidora pd = new PedidoDistribuidora();
			pd.setDistribuidora(distribuidora);
			pd.setFecha(LocalDateTime.now());
			pd.setItem(pi);
			pi.getPedidosADistribuidoras().add(pd);

			result.add(pedidoADistribuidoraRepository.save(pd));
		}

		logger.info("Pedido a distribuidora '{}' confirmed: {} item(s) (IDs: {})", distribuidora.getDescripcion(), managedItems.size(), itemIds);
		return result;
	}

	@Transactional
	public void confirmarLlegada(Integer pedidoItemId) {
		PedidoItem item = pedidoItemRepository.findById(pedidoItemId)
				.orElseThrow(() -> new RuntimeException("PedidoItem not found: " + pedidoItemId));
		item.setPendiente(false);
		pedidoItemRepository.save(item);
		logger.info("Item {} marked as arrived (pendiente=false)", pedidoItemId);

		// Mark the active PedidoDistribuidora as realizado
		item.getPedidosADistribuidoras().stream()
				.filter(pd -> !pd.isRealizado())
				.findFirst()
				.ifPresent(pd -> {
					pd.setRealizado(true);
					pedidoADistribuidoraRepository.save(pd);
					logger.info("PedidoDistribuidora {} marked as realizado", pd.getId());
				});
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
