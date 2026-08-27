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

import com.librosmario.pedidos.entity.Comercio;
import com.librosmario.pedidos.payload.ResumenComercioDTO;
import com.librosmario.pedidos.service.ComercioService;

@RestController
public class ComercioController {

	private static final Logger logger = LogManager.getLogger(ComercioController.class);

	@Autowired
	ComercioService service;

	@GetMapping(value = "/comercios")
	public ResponseEntity<List<Comercio>> getAll() {
		return ResponseEntity.ok(service.findAll());
	}

	/** Cada comercio con lo que tiene en consignacion, para elegir sabiendo. */
	@GetMapping(value = "/comercios/consignacion")
	public ResponseEntity<List<ResumenComercioDTO>> resumenDeConsignacion() {
		return ResponseEntity.ok(service.resumenDeConsignacion());
	}

	@GetMapping(value = "/comercios/{id}")
	public ResponseEntity<Comercio> getById(@PathVariable Integer id) {
		return ResponseEntity.ok(service.findById(id));
	}

	@PostMapping(value = "/comercios", consumes = {"application/json"})
	public ResponseEntity<Comercio> create(@RequestBody Comercio comercio) {
		try {
			Comercio created = service.create(comercio);
			return new ResponseEntity<>(created, HttpStatus.CREATED);
		} catch (Exception e) {
			logger.error("Error creating comercio", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al crear el comercio", e);
		}
	}

	@PutMapping(value = "/comercios/{id}", consumes = {"application/json"})
	public ResponseEntity<Comercio> update(@PathVariable Integer id, @RequestBody Comercio comercio) {
		try {
			return ResponseEntity.ok(service.update(id, comercio));
		} catch (Exception e) {
			logger.error("Error updating comercio", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar el comercio", e);
		}
	}

	@DeleteMapping(value = "/comercios/{id}")
	public ResponseEntity<Void> delete(@PathVariable Integer id) {
		try {
			service.delete(id);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			logger.error("Error deleting comercio", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar el comercio", e);
		}
	}

	@GetMapping(value = "/comercios/search/findByAny")
	public ResponseEntity<List<Comercio>> findByAny(@Param("parametro") String parametro) {
		return ResponseEntity.ok(service.findByAny(parametro));
	}
}
