package com.librosmario.pedidos.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.librosmario.pedidos.entity.Distribuidora;
import com.librosmario.pedidos.entity.Remito;
import com.librosmario.pedidos.entity.RemitoItem;
import com.librosmario.pedidos.exception.ResourceNotFoundException;
import com.librosmario.pedidos.repository.DistribuidoraRepository;
import com.librosmario.pedidos.repository.RemitoItemRepository;
import com.librosmario.pedidos.repository.RemitoRepository;
import com.librosmario.pedidos.repository.specifications.RemitoSpecifications;

@Service
public class RemitoService {

	private static final Logger logger = LogManager.getLogger(RemitoService.class);

	@Autowired
	RemitoRepository repository;

	@Autowired
	RemitoItemRepository remitoItemRepository;

	@Autowired
	DistribuidoraRepository distribuidoraRepository;

	public Remito createRemito(Remito remito) {
		// Fetch managed Distribuidora to avoid CascadeType.PERSIST trying to INSERT an existing row
		if (remito.getRe_distribuidora_ed() != null && remito.getRe_distribuidora_ed().getId() > 0) {
			int distribuidoraId = remito.getRe_distribuidora_ed().getId();
			Distribuidora managed = distribuidoraRepository.findById(distribuidoraId)
					.orElseThrow(() -> new ResourceNotFoundException("Distribuidora", "id", distribuidoraId));
			remito.setRe_distribuidora_ed(managed);
		}

		if (remito.getItems() != null) {
			for (RemitoItem item : remito.getItems()) {
				item.setRi_remito_re(remito);
			}
		}
		remito = repository.save(remito);
		logger.info("Remito created with id '{}' and {} items",
				remito.getRe_remito_k(),
				remito.getItems() != null ? remito.getItems().size() : 0);
		return remito;
	}

	public Remito updateRemito(Integer id, Remito remitoDetails) {
		Remito remito = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Remito", "id", id));

		remito.setRe_fecha(remitoDetails.getRe_fecha());
		remito.setRe_observaciones(remitoDetails.getRe_observaciones());

		// Fetch managed Distribuidora
		if (remitoDetails.getRe_distribuidora_ed() != null && remitoDetails.getRe_distribuidora_ed().getId() > 0) {
			Distribuidora managed = distribuidoraRepository.findById(remitoDetails.getRe_distribuidora_ed().getId())
					.orElseThrow(() -> new ResourceNotFoundException("Distribuidora", "id", remitoDetails.getRe_distribuidora_ed().getId()));
			remito.setRe_distribuidora_ed(managed);
		} else {
			remito.setRe_distribuidora_ed(remitoDetails.getRe_distribuidora_ed());
		}

		// Remove old items
		if (remito.getItems() != null) {
			remitoItemRepository.deleteAll(remito.getItems());
			remito.getItems().clear();
		}

		// Add new items
		if (remitoDetails.getItems() != null) {
			for (RemitoItem item : remitoDetails.getItems()) {
				item.setRi_remito_re(remito);
				item.setRi_remito_item_k(null);
			}
			remito.setItems(new ArrayList<>(remitoDetails.getItems()));
		}

		remito = repository.save(remito);
		logger.info("Remito updated with id '{}'", id);
		return remito;
	}

	public void deleteRemito(Integer id) {
		Remito remito = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Remito", "id", id));

		// Delete items first since cascade doesn't include REMOVE
		if (remito.getItems() != null) {
			remitoItemRepository.deleteAll(remito.getItems());
		}
		repository.delete(remito);
		logger.info("Remito deleted with id '{}'", id);
	}

	public Remito findById(Integer id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Remito", "id", id));
	}

	public List<Remito> findByAll(String distribuidora, String observaciones, String fechaDesde, String fechaHasta) {
		Specification<Remito> specification = Specification
				.where(distribuidora == null ? null : RemitoSpecifications.distribuidoraContains(distribuidora))
				.and(observaciones == null ? null : RemitoSpecifications.observacionesContains(observaciones));
		specification = addDates(specification, fechaDesde, fechaHasta);
		return repository.findAll(specification);
	}

	public List<Remito> findByAny(String parametro, String fechaDesde, String fechaHasta) {
		Specification<Remito> specification = Specification
				.where(RemitoSpecifications.distribuidoraContains(parametro)
						.or(RemitoSpecifications.observacionesContains(parametro))
						.or(RemitoSpecifications.itemLibroContains(parametro)));
		specification = addDates(specification, fechaDesde, fechaHasta);
		return repository.findAll(specification);
	}

	private Specification<Remito> addDates(Specification<Remito> specification, String fechaDesde, String fechaHasta) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			if (fechaDesde != null) {
				specification = specification.and(RemitoSpecifications.fechaGreaterOrEquals(sdf.parse(fechaDesde)));
			}
			if (fechaHasta != null) {
				specification = specification.and(RemitoSpecifications.fechaLessOrEquals(sdf.parse(fechaHasta)));
			}
		} catch (ParseException e) {
			logger.error("Error parsing date", e);
		}
		return specification;
	}

}
