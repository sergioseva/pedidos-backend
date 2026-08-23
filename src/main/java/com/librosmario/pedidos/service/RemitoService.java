package com.librosmario.pedidos.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.librosmario.pedidos.entity.Comercio;
import com.librosmario.pedidos.entity.Distribuidora;
import com.librosmario.pedidos.entity.Remito;
import com.librosmario.pedidos.entity.RemitoItem;
import com.librosmario.pedidos.exception.ResourceNotFoundException;
import com.librosmario.pedidos.payload.ConsignacionEstadoCuentaDTO;
import com.librosmario.pedidos.entity.Recibo;
import com.librosmario.pedidos.repository.ComercioRepository;
import com.librosmario.pedidos.repository.ReciboRepository;
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

	@Autowired
	ComercioRepository comercioRepository;

	@Autowired
	ReciboRepository reciboRepository;

	public Remito createRemito(Remito remito) {
		normalizarDestinatario(remito);

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

		normalizarDestinatario(remitoDetails);
		remito.setRe_tipo(remitoDetails.getRe_tipo());
		remito.setRe_distribuidora_ed(remitoDetails.getRe_distribuidora_ed());
		remito.setRe_comercio_cm(remitoDetails.getRe_comercio_cm());

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

	/**
	 * Deja el remito con un solo destinatario, el que corresponde a su tipo, y con las entidades
	 * traidas de la base. Sin el fetch, CascadeType.PERSIST intentaria INSERTar una fila que ya
	 * existe; sin el borrado del otro lado, un remito reetiquetado quedaria apuntando a los dos.
	 */
	private void normalizarDestinatario(Remito remito) {
		if (remito.getRe_tipo() == null) {
			remito.setRe_tipo(Remito.TIPO_DEVOLUCION);
		}

		if (Remito.esDeComercio(remito.getRe_tipo())) {
			remito.setRe_distribuidora_ed(null);
			if (remito.getRe_comercio_cm() != null && remito.getRe_comercio_cm().getId() > 0) {
				int comercioId = remito.getRe_comercio_cm().getId();
				Comercio managed = comercioRepository.findById(comercioId)
						.orElseThrow(() -> new ResourceNotFoundException("Comercio", "id", comercioId));
				remito.setRe_comercio_cm(managed);
			}
			return;
		}

		remito.setRe_comercio_cm(null);
		if (remito.getRe_distribuidora_ed() != null && remito.getRe_distribuidora_ed().getId() > 0) {
			int distribuidoraId = remito.getRe_distribuidora_ed().getId();
			Distribuidora managed = distribuidoraRepository.findById(distribuidoraId)
					.orElseThrow(() -> new ResourceNotFoundException("Distribuidora", "id", distribuidoraId));
			remito.setRe_distribuidora_ed(managed);
		}
	}

	public Remito findById(Integer id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Remito", "id", id));
	}

	public Recibo findReciboByRemito(Integer remitoId) {
		return reciboRepository.findByRemitoId(remitoId)
				.orElseThrow(() -> new ResourceNotFoundException("Recibo", "remito", remitoId));
	}

	public List<Remito> findByAll(String distribuidora, String observaciones, String fechaDesde, String fechaHasta) {
		return findByAll(distribuidora, observaciones, fechaDesde, fechaHasta, null);
	}

	public List<Remito> findByAll(String distribuidora, String observaciones, String fechaDesde, String fechaHasta,
			String tipo) {
		Specification<Remito> specification = Specification
				.where(distribuidora == null ? null : RemitoSpecifications.destinatarioContains(distribuidora))
				.and(observaciones == null ? null : RemitoSpecifications.observacionesContains(observaciones));
		specification = addTipo(specification, tipo);
		specification = addDates(specification, fechaDesde, fechaHasta);
		return repository.findAll(specification);
	}

	public List<Remito> findByAny(String parametro, String fechaDesde, String fechaHasta) {
		return findByAny(parametro, fechaDesde, fechaHasta, null);
	}

	public List<Remito> findByAny(String parametro, String fechaDesde, String fechaHasta, String tipo) {
		Specification<Remito> specification = Specification
				.where(RemitoSpecifications.destinatarioContains(parametro)
						.or(RemitoSpecifications.observacionesContains(parametro))
						.or(RemitoSpecifications.itemLibroContains(parametro)));
		specification = addTipo(specification, tipo);
		specification = addDates(specification, fechaDesde, fechaHasta);
		return repository.findAll(specification);
	}

	/**
	 * Que hay hoy en la calle: por comercio y por libro, lo entregado en consignacion.
	 * Sin registro de ventas ni devoluciones, lo entregado ES el saldo pendiente.
	 */
	public List<ConsignacionEstadoCuentaDTO> estadoCuentaConsignacion(Integer comercioId, String fechaDesde,
			String fechaHasta) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date desde = null;
		Date hasta = null;
		try {
			if (fechaDesde != null && !fechaDesde.isEmpty()) {
				desde = sdf.parse(fechaDesde);
			}
			if (fechaHasta != null && !fechaHasta.isEmpty()) {
				hasta = sdf.parse(fechaHasta);
			}
		} catch (ParseException e) {
			logger.error("Error parsing date", e);
		}
		return remitoItemRepository.estadoCuentaConsignacion(comercioId, desde, hasta);
	}

	/** {@code tipo} admite varios separados por coma, p.ej. "CONSIGNACION,RETIRO". */
	private Specification<Remito> addTipo(Specification<Remito> specification, String tipo) {
		if (tipo == null || tipo.isEmpty()) {
			return specification;
		}
		List<String> tipos = Arrays.stream(tipo.split(","))
				.map(String::trim)
				.filter(t -> !t.isEmpty())
				.collect(Collectors.toList());
		if (tipos.isEmpty()) {
			return specification;
		}
		return specification.and(RemitoSpecifications.tipoIn(tipos));
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
