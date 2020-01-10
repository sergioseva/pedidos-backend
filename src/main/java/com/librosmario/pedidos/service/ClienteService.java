package com.librosmario.pedidos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.librosmario.pedidos.entity.Cliente;
import com.librosmario.pedidos.repository.ClienteRepository;
import com.librosmario.pedidos.repository.specifications.ClienteSpecifications;

@Service
public class ClienteService {
	@Autowired
	ClienteRepository repository;
	
	public List<Cliente> findByAny(String parametro) {
		Specification<Cliente> specification = Specification
				.where(ClienteSpecifications.direccionContains(parametro))
				.or(ClienteSpecifications.emailContains(parametro))
				.or(ClienteSpecifications.nombre(parametro))
				.or(ClienteSpecifications.telefonoFijoContains(parametro))
				.or(ClienteSpecifications.telefonoLaboralContains(parametro))
				.or(ClienteSpecifications.telefonoMovilContains(parametro))
				;
		return repository.findAll(specification); 
	}
}
