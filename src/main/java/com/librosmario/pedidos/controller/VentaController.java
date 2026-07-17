package com.librosmario.pedidos.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.librosmario.pedidos.entity.Venta;
import com.librosmario.pedidos.payload.VentaPorDiaDTO;
import com.librosmario.pedidos.payload.VentaResumenDTO;
import com.librosmario.pedidos.service.VentaService;

/**
 * Recording a sale is open to any logged-in user -- whoever is at the counter must be able to ring
 * one up. Reading them back is admin-only: the money is management's business.
 *
 * Everything lives under /ventas rather than splitting the reports under /api/admin/*: the proxy
 * rewrites those two prefixes differently, so one resource would end up with two base URLs.
 * @PreAuthorize is the actual enforcement and does not care about the path.
 *
 * Deliberately no blanket try/catch: RemitoController wraps everything into a 500, which turns
 * "cliente not found" into a server error. The @ResponseStatus exceptions map themselves.
 */
@RestController
public class VentaController {

	private static final Logger logger = LogManager.getLogger(VentaController.class);

	@Autowired
	VentaService service;

	@PostMapping(value = "/ventas")
	public ResponseEntity<Venta> createVenta(@RequestBody Venta venta) {
		logger.info("Registrando venta con {} items", venta.getItems() == null ? 0 : venta.getItems().size());
		return ResponseEntity.status(HttpStatus.CREATED).body(service.createVenta(venta));
	}

	@GetMapping(value = "/ventas/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Venta> findById(@PathVariable Integer id) {
		return ResponseEntity.ok(service.findById(id));
	}

	@GetMapping(value = "/ventas/search/findByAll")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<Venta>> findByAll(@RequestParam(required = false) String libro,
												@RequestParam(required = false) String cliente,
												@RequestParam(required = false) String isbn,
												@RequestParam(required = false) String fechaDesde,
												@RequestParam(required = false) String fechaHasta) {
		return ResponseEntity.ok(service.findByAll(libro, cliente, isbn, fechaDesde, fechaHasta));
	}

	@GetMapping(value = "/ventas/search/findByAny")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<Venta>> findByAny(@RequestParam(required = false) String parametro,
												 @RequestParam(required = false) String fechaDesde,
												 @RequestParam(required = false) String fechaHasta) {
		return ResponseEntity.ok(service.findByAny(parametro, fechaDesde, fechaHasta));
	}

	@GetMapping(value = "/ventas/estadisticas/porDia")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<VentaPorDiaDTO>> ventasPorDia(@RequestParam(required = false) String fechaDesde,
															 @RequestParam(required = false) String fechaHasta) {
		return ResponseEntity.ok(service.ventasPorDia(fechaDesde, fechaHasta));
	}

	@GetMapping(value = "/ventas/estadisticas/resumen")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<VentaResumenDTO> resumen(@RequestParam(required = false) String fechaDesde,
												   @RequestParam(required = false) String fechaHasta) {
		return ResponseEntity.ok(service.resumen(fechaDesde, fechaHasta));
	}

	/** Downloads the filtered ventas as an .xlsx. Admin-only, like the rest of the reporting. */
	@GetMapping(value = "/ventas/reporte")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<byte[]> reporte(@RequestParam(required = false) String parametro,
										  @RequestParam(required = false) String fechaDesde,
										  @RequestParam(required = false) String fechaHasta) {
		byte[] xlsx = service.generarReporte(parametro, fechaDesde, fechaHasta);
		String nombre = "ventas" + (fechaDesde != null && !fechaDesde.isEmpty() ? "_" + fechaDesde : "")
				+ (fechaHasta != null && !fechaHasta.isEmpty() ? "_a_" + fechaHasta : "") + ".xlsx";
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombre + "\"")
				.contentType(MediaType.parseMediaType(
						"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
				.body(xlsx);
	}

	/**
	 * A mis-scan needs an escape hatch, and deleting leaves an obvious hole rather than a quiet
	 * rewrite. There is intentionally no update: a sales record that can be edited is not evidence.
	 */
	@DeleteMapping(value = "/ventas/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> deleteVenta(@PathVariable Integer id) {
		service.deleteVenta(id);
		return ResponseEntity.noContent().build();
	}
}
