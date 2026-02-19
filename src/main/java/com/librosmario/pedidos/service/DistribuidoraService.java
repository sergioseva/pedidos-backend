package com.librosmario.pedidos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.librosmario.pedidos.entity.Distribuidora;
import com.librosmario.pedidos.exception.ResourceNotFoundException;
import com.librosmario.pedidos.repository.DistribuidoraRepository;

@Service
public class DistribuidoraService {

	@Autowired
	DistribuidoraRepository repository;

	public List<Distribuidora> findAll() {
		return repository.findAll();
	}

	public Distribuidora findById(Integer id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Distribuidora", "id", id));
	}

	public Distribuidora create(Distribuidora distribuidora) {
		return repository.save(distribuidora);
	}

	public Distribuidora update(Integer id, Distribuidora distribuidoraDetails) {
		Distribuidora distribuidora = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Distribuidora", "id", id));

		distribuidora.setDescripcion(distribuidoraDetails.getDescripcion());
		distribuidora.setNroCuenta(distribuidoraDetails.getNroCuenta());

		return repository.save(distribuidora);
	}

	public void delete(Integer id) {
		Distribuidora distribuidora = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Distribuidora", "id", id));
		repository.delete(distribuidora);
	}

	public List<Distribuidora> findByAny(String parametro) {
		return repository.findByDescripcionContainingIgnoreCaseOrNroCuentaContainingIgnoreCase(parametro, parametro);
	}
}
