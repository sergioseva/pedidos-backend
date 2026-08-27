package com.librosmario.pedidos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.librosmario.pedidos.entity.Comercio;
import com.librosmario.pedidos.exception.ResourceNotFoundException;
import com.librosmario.pedidos.payload.ResumenComercioDTO;
import com.librosmario.pedidos.repository.ComercioRepository;

@Service
public class ComercioService {

	@Autowired
	ComercioRepository repository;

	public List<Comercio> findAll() {
		return repository.findAllByOrderByDescripcionAsc();
	}

	/** Para el desplegable: cada comercio con cuantos ejemplares tiene en consignacion. */
	public List<ResumenComercioDTO> resumenDeConsignacion() {
		return repository.resumenDeConsignacion();
	}

	public Comercio findById(Integer id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Comercio", "id", id));
	}

	public Comercio create(Comercio comercio) {
		return repository.save(comercio);
	}

	public Comercio update(Integer id, Comercio comercioDetails) {
		Comercio comercio = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Comercio", "id", id));

		comercio.setDescripcion(comercioDetails.getDescripcion());
		comercio.setDireccion(comercioDetails.getDireccion());
		comercio.setContacto(comercioDetails.getContacto());
		comercio.setTelefono(comercioDetails.getTelefono());
		comercio.setCuit(comercioDetails.getCuit());
		comercio.setComision(comercioDetails.getComision());

		return repository.save(comercio);
	}

	public void delete(Integer id) {
		Comercio comercio = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Comercio", "id", id));
		repository.delete(comercio);
	}

	public List<Comercio> findByAny(String parametro) {
		return repository
				.findByDescripcionContainingIgnoreCaseOrDireccionContainingIgnoreCaseOrContactoContainingIgnoreCase(
						parametro, parametro, parametro);
	}
}
