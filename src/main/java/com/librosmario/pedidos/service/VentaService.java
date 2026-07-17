package com.librosmario.pedidos.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.librosmario.pedidos.entity.Cliente;
import com.librosmario.pedidos.entity.Venta;
import com.librosmario.pedidos.entity.VentaItem;
import com.librosmario.pedidos.exception.BadRequestException;
import com.librosmario.pedidos.exception.ResourceNotFoundException;
import com.librosmario.pedidos.payload.VentaPorDiaDTO;
import com.librosmario.pedidos.payload.VentaResumenDTO;
import com.librosmario.pedidos.repository.ClienteRepository;
import com.librosmario.pedidos.repository.VentaRepository;
import com.librosmario.pedidos.repository.specifications.VentaSpecifications;

@Service
public class VentaService {

	private static final Logger logger = LogManager.getLogger(VentaService.class);

	/** Reports default to the last month rather than scanning all history. */
	private static final int DIAS_POR_DEFECTO = 30;

	@Autowired
	VentaRepository repository;

	@Autowired
	ClienteRepository clienteRepository;

	/**
	 * Records a sale. Everything about the money is decided here, not by the caller: the total is
	 * recomputed from the lines, the timestamp comes from the server clock, and the operator is
	 * taken from the authenticated principal. A till record that trusted the browser would be
	 * worthless as an accountability record.
	 */
	@Transactional
	public Venta createVenta(Venta venta) {
		if (venta.getItems() == null || venta.getItems().isEmpty()) {
			throw new BadRequestException("La venta debe tener al menos un item");
		}

		venta.setCliente(resolverCliente(venta.getCliente()));

		double total = 0d;
		for (VentaItem item : venta.getItems()) {
			validarItem(item);
			item.setVenta(venta);
			// A client-supplied id could otherwise re-point an existing line at this sale.
			item.setId(null);
			total += item.getCantidad() * item.getPrecio();
		}

		venta.setTotal(redondear(total));
		venta.setFecha(LocalDateTime.now());
		venta.setUsuario(usuarioActual());

		Venta guardada = repository.save(venta);
		logger.info("Venta {} registrada por '{}': {} items, total {}",
				guardada.getId(), guardada.getUsuario(), guardada.getItems().size(), guardada.getTotal());
		return guardada;
	}

	/**
	 * Cliente is optional. An empty or id-less object from the form is treated as "no cliente"
	 * rather than persisted as a junk row; a real id must resolve to a real customer.
	 */
	private Cliente resolverCliente(Cliente cliente) {
		// Cliente.id is a primitive int, so an empty {} from the form arrives as id=0, not null.
		if (cliente == null || cliente.getId() <= 0) {
			return null;
		}
		return clienteRepository.findById(cliente.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", cliente.getId()));
	}

	private void validarItem(VentaItem item) {
		if (item.getCantidad() == null || item.getCantidad() <= 0) {
			throw new BadRequestException("Cantidad invalida para '" + item.getLibro() + "'");
		}
		if (item.getPrecio() == null || item.getPrecio() < 0) {
			throw new BadRequestException("Precio invalido para '" + item.getLibro() + "'");
		}
		if (item.getLibro() == null || item.getLibro().trim().isEmpty()) {
			throw new BadRequestException("El item debe tener un libro");
		}
	}

	/** Bounds the drift of Double arithmetic at the boundary. See VentaController for the debt note. */
	private double redondear(double valor) {
		return Math.round(valor * 100d) / 100d;
	}

	private String usuarioActual() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth == null ? null : auth.getName();
	}

	public Venta findById(Integer id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Venta", "id", id));
	}

	public List<Venta> findByAll(String libro, String cliente, String isbn, String fechaDesde, String fechaHasta) {
		Specification<Venta> specification = Specification
				.where(libro == null || libro.isEmpty() ? null : VentaSpecifications.itemLibroContains(libro))
				.and(cliente == null || cliente.isEmpty() ? null : VentaSpecifications.clienteContains(cliente))
				.and(isbn == null || isbn.isEmpty() ? null : VentaSpecifications.itemIsbnEquals(isbn));
		specification = addDates(specification, fechaDesde, fechaHasta);
		return repository.findAll(specification);
	}

	public List<Venta> findByAny(String expression, String fechaDesde, String fechaHasta) {
		Specification<Venta> specification = null;
		if (expression != null && !expression.isEmpty()) {
			specification = Specification.where(VentaSpecifications.itemLibroContains(expression))
					.or(VentaSpecifications.clienteContains(expression))
					.or(VentaSpecifications.observacionesContains(expression))
					.or(VentaSpecifications.itemIsbnEquals(expression));
		}
		specification = addDates(specification == null ? Specification.where(null) : specification,
				fechaDesde, fechaHasta);
		return repository.findAll(specification);
	}

	/**
	 * The ticket-level report as a .xlsx, for the same filter the consulta screen is showing (search
	 * term + date range) so the file matches what the admin is looking at. Ordered by date.
	 */
	public byte[] generarReporte(String parametro, String fechaDesde, String fechaHasta) {
		List<Venta> ventas = findByAny(parametro, fechaDesde, fechaHasta);
		ventas.sort(Comparator.comparing(Venta::getFecha,
				Comparator.nullsLast(Comparator.naturalOrder())));
		return VentaReporteExcel.build(ventas);
	}

	public List<VentaPorDiaDTO> ventasPorDia(String fechaDesde, String fechaHasta) {
		return repository.ventasPorDia(desde(fechaDesde), hasta(fechaHasta));
	}

	public VentaResumenDTO resumen(String fechaDesde, String fechaHasta) {
		LocalDateTime desde = desde(fechaDesde);
		LocalDateTime hasta = hasta(fechaHasta);
		long ventas = repository.contarVentas(desde, hasta);
		Double total = repository.totalVendido(desde, hasta);
		Long unidades = repository.unidadesVendidas(desde, hasta);
		return new VentaResumenDTO(ventas, unidades == null ? 0L : unidades,
				redondear(total == null ? 0d : total));
	}

	@Transactional
	public void deleteVenta(Integer id) {
		Venta venta = findById(id);
		logger.warn("Venta {} (total {}) eliminada", venta.getId(), venta.getTotal());
		repository.delete(venta);
	}

	private Specification<Venta> addDates(Specification<Venta> specification, String fechaDesde, String fechaHasta) {
		if (fechaDesde != null && !fechaDesde.isEmpty()) {
			specification = specification.and(VentaSpecifications.fechaGreaterOrEquals(desde(fechaDesde)));
		}
		if (fechaHasta != null && !fechaHasta.isEmpty()) {
			specification = specification.and(VentaSpecifications.fechaLessOrEquals(hasta(fechaHasta)));
		}
		return specification;
	}

	private LocalDateTime desde(String fecha) {
		if (fecha == null || fecha.isEmpty()) {
			return LocalDate.now().minusDays(DIAS_POR_DEFECTO).atStartOfDay();
		}
		return parse(fecha).atStartOfDay();
	}

	/** End of day, so "hasta=2026-07-16" includes everything sold on the 16th. */
	private LocalDateTime hasta(String fecha) {
		if (fecha == null || fecha.isEmpty()) {
			return LocalDate.now().atTime(LocalTime.MAX);
		}
		return parse(fecha).atTime(LocalTime.MAX);
	}

	private LocalDate parse(String fecha) {
		try {
			return LocalDate.parse(fecha);
		} catch (DateTimeParseException e) {
			throw new BadRequestException("Fecha invalida: '" + fecha + "'. Formato esperado yyyy-MM-dd");
		}
	}
}
