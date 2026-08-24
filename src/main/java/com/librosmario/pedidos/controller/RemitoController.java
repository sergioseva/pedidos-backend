package com.librosmario.pedidos.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.librosmario.pedidos.entity.Recibo;
import com.librosmario.pedidos.entity.Remito;
import com.librosmario.pedidos.payload.ActualizacionPrecioDTO;
import com.librosmario.pedidos.payload.ConsignacionEstadoCuentaDTO;
import com.librosmario.pedidos.payload.ResultadoPreciosDTO;
import com.librosmario.pedidos.payload.LiquidacionConsignacionDTO;
import com.librosmario.pedidos.payload.LiquidacionResultadoDTO;
import com.librosmario.pedidos.service.LiquidacionConsignacionService;
import com.librosmario.pedidos.service.RemitoService;

@RestController
public class RemitoController {

	private static final Logger logger = LogManager.getLogger(RemitoController.class);

	@Autowired
	RemitoService service;

	@Autowired
	LiquidacionConsignacionService liquidacionService;

	@PostMapping(value = "/remitos", consumes = {"application/json"})
	public ResponseEntity<Remito> createRemito(@RequestBody Remito remito) {
		try {
			Remito created = service.createRemito(remito);
			return new ResponseEntity<>(created, HttpStatus.CREATED);
		} catch (Exception e) {
			logger.error("Error saving remito", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "There was an error saving the remito", e);
		}
	}

	@PutMapping(value = "/remitos/{id}", consumes = {"application/json"})
	public ResponseEntity<Remito> updateRemito(@PathVariable Integer id, @RequestBody Remito remito) {
		try {
			Remito updated = service.updateRemito(id, remito);
			return ResponseEntity.ok(updated);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "There was an error updating the remito", e);
		}
	}

	@GetMapping(value = "/remitos/{id}")
	public ResponseEntity<Remito> getRemito(@PathVariable Integer id) {
		Remito remito = service.findById(id);
		return ResponseEntity.ok(remito);
	}

	@DeleteMapping(value = "/remitos/{id}")
	public ResponseEntity<Void> deleteRemito(@PathVariable Integer id) {
		try {
			service.deleteRemito(id);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "There was an error deleting the remito", e);
		}
	}

	/** {@code tipo} es opcional: sin el, devuelve devoluciones y consignaciones juntas. */
	@GetMapping(value = "/remitos/search/findByAny")
	public ResponseEntity<List<Remito>> findByAny(@Param("parametro") String parametro,
	                                               @Param("fechaDesde") String fechaDesde,
	                                               @Param("fechaHasta") String fechaHasta,
	                                               @Param("tipo") String tipo) {
		List<Remito> remitos = service.findByAny(parametro, fechaDesde, fechaHasta, tipo);
		return ResponseEntity.ok(remitos);
	}

	@GetMapping(value = "/remitos/search/findByAll")
	public ResponseEntity<List<Remito>> findByAll(@Param("distribuidora") String distribuidora,
	                                               @Param("observaciones") String observaciones,
	                                               @Param("fechaDesde") String fechaDesde,
	                                               @Param("fechaHasta") String fechaHasta,
	                                               @Param("tipo") String tipo) {
		List<Remito> remitos = service.findByAll(distribuidora, observaciones, fechaDesde, fechaHasta, tipo);
		return ResponseEntity.ok(remitos);
	}

	/**
	 * Cierra la cuenta de un comercio y emite los documentos.
	 *
	 * Las excepciones de negocio no se envuelven: un saldo insuficiente es un 400 con el detalle
	 * de que titulo fallo, no un 500 generico.
	 */
	@PostMapping(value = "/remitos/consignacion/liquidar", consumes = {"application/json"})
	public ResponseEntity<LiquidacionResultadoDTO> liquidar(@RequestBody LiquidacionConsignacionDTO liquidacion) {
		return new ResponseEntity<>(liquidacionService.liquidar(liquidacion), HttpStatus.CREATED);
	}

	/** Emite el recibo de un remito de venta que habia quedado impago. */
	@PostMapping(value = "/remitos/{id}/recibo")
	public ResponseEntity<Recibo> pagarRemito(@PathVariable Integer id,
			@Param("medioPago") String medioPago) {
		return new ResponseEntity<>(liquidacionService.pagarRemito(id, medioPago), HttpStatus.CREATED);
	}

	@GetMapping(value = "/remitos/{id}/recibo")
	public ResponseEntity<Recibo> getRecibo(@PathVariable Integer id) {
		return ResponseEntity.ok(service.findReciboByRemito(id));
	}

	/** Deja un precio nuevo para un titulo que el comercio tiene en consignacion. */
	@PutMapping(value = "/remitos/consignacion/precio", consumes = {"application/json"})
	public ResponseEntity<Void> actualizarPrecio(@RequestBody ActualizacionPrecioDTO actualizacion) {
		service.actualizarPrecio(actualizacion);
		return ResponseEntity.noContent().build();
	}

	/** Trae del catalogo los precios vigentes de lo que el comercio tiene en consignacion. */
	@PostMapping(value = "/remitos/consignacion/{comercioId}/precios")
	public ResponseEntity<ResultadoPreciosDTO> actualizarPreciosDesdeCatalogo(@PathVariable Integer comercioId) {
		return ResponseEntity.ok(service.actualizarPreciosDesdeCatalogo(comercioId));
	}

	/** El detalle de un comercio como .xlsx, con el mismo filtro que tenia la pantalla. */
	@GetMapping(value = "/remitos/consignacion/estadocuenta/reporte")
	public ResponseEntity<byte[]> reporteConsignacion(@RequestParam Integer comercioId,
			@RequestParam(required = false) String fechaDesde,
			@RequestParam(required = false) String fechaHasta) {
		byte[] xlsx = service.generarReporteConsignacion(comercioId, fechaDesde, fechaHasta);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,
						"attachment; filename=\"consignacion_" + comercioId + ".xlsx\"")
				.contentType(MediaType.parseMediaType(
						"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
				.body(xlsx);
	}

	/** Que hay en la calle: libros entregados en consignacion, por comercio y titulo. */
	@GetMapping(value = "/remitos/consignacion/estadocuenta")
	public ResponseEntity<List<ConsignacionEstadoCuentaDTO>> estadoCuentaConsignacion(
			@Param("comercioId") Integer comercioId,
			@Param("fechaDesde") String fechaDesde,
			@Param("fechaHasta") String fechaHasta) {
		return ResponseEntity.ok(service.estadoCuentaConsignacion(comercioId, fechaDesde, fechaHasta));
	}

}
