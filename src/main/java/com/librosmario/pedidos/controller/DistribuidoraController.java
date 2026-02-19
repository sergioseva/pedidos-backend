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

import com.librosmario.pedidos.entity.Distribuidora;
import com.librosmario.pedidos.service.DistribuidoraService;

@RestController
public class DistribuidoraController {

	private static final Logger logger = LogManager.getLogger(DistribuidoraController.class);

	@Autowired
	DistribuidoraService service;

	@GetMapping(value = "/distribuidoras")
	public ResponseEntity<List<Distribuidora>> getAll() {
		List<Distribuidora> distribuidoras = service.findAll();
		return ResponseEntity.ok(distribuidoras);
	}

	@GetMapping(value = "/distribuidoras/{id}")
	public ResponseEntity<Distribuidora> getById(@PathVariable Integer id) {
		Distribuidora distribuidora = service.findById(id);
		return ResponseEntity.ok(distribuidora);
	}

	@PostMapping(value = "/distribuidoras", consumes = {"application/json"})
	public ResponseEntity<Distribuidora> create(@RequestBody Distribuidora distribuidora) {
		try {
			Distribuidora created = service.create(distribuidora);
			return new ResponseEntity<>(created, HttpStatus.CREATED);
		} catch (Exception e) {
			logger.error("Error creating distribuidora", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al crear la distribuidora", e);
		}
	}

	@PutMapping(value = "/distribuidoras/{id}", consumes = {"application/json"})
	public ResponseEntity<Distribuidora> update(@PathVariable Integer id, @RequestBody Distribuidora distribuidora) {
		try {
			Distribuidora updated = service.update(id, distribuidora);
			return ResponseEntity.ok(updated);
		} catch (Exception e) {
			logger.error("Error updating distribuidora", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar la distribuidora", e);
		}
	}

	@DeleteMapping(value = "/distribuidoras/{id}")
	public ResponseEntity<Void> delete(@PathVariable Integer id) {
		try {
			service.delete(id);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			logger.error("Error deleting distribuidora", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar la distribuidora", e);
		}
	}

	@GetMapping(value = "/distribuidoras/search/findByAny")
	public ResponseEntity<List<Distribuidora>> findByAny(@Param("parametro") String parametro) {
		List<Distribuidora> distribuidoras = service.findByAny(parametro);
		return ResponseEntity.ok(distribuidoras);
	}
}
