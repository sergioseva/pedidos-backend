package com.librosmario.pedidos.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.librosmario.pedidos.entity.Remito;
import com.librosmario.pedidos.service.RemitoService;

@RestController
public class RemitoController {

	private static final Logger logger = LogManager.getLogger(RemitoController.class);

	@Autowired
	RemitoService service;

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

	@GetMapping(value = "/remitos/search/findByAny")
	public ResponseEntity<List<Remito>> findByAny(@Param("parametro") String parametro,
	                                               @Param("fechaDesde") String fechaDesde,
	                                               @Param("fechaHasta") String fechaHasta) {
		List<Remito> remitos = service.findByAny(parametro, fechaDesde, fechaHasta);
		return ResponseEntity.ok(remitos);
	}

	@GetMapping(value = "/remitos/search/findByAll")
	public ResponseEntity<List<Remito>> findByAll(@Param("distribuidora") String distribuidora,
	                                               @Param("observaciones") String observaciones,
	                                               @Param("fechaDesde") String fechaDesde,
	                                               @Param("fechaHasta") String fechaHasta) {
		List<Remito> remitos = service.findByAll(distribuidora, observaciones, fechaDesde, fechaHasta);
		return ResponseEntity.ok(remitos);
	}

}
