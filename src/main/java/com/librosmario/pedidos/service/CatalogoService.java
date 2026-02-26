package com.librosmario.pedidos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.librosmario.pedidos.entity.Catalogo;
import com.librosmario.pedidos.repository.CatalogoRepository;
import com.librosmario.pedidos.repository.specifications.CatalogoSpecifications;

@Service
public class CatalogoService {
	
	@Autowired
	CatalogoRepository repository;
	
	public Page<Catalogo> findByAll(String descripcion, String autor, String editorial, String tema, String isbn, Pageable pageable) {
		Specification<Catalogo> specification = Specification
				.where(descripcion == null ? null : CatalogoSpecifications.descripcionContains(descripcion))
				.and(autor == null ? null : CatalogoSpecifications.autorContains(autor))
				.and(editorial == null ? null : CatalogoSpecifications.editorialContains(editorial))
				.and(tema == null ? null : CatalogoSpecifications.temaContains(autor))
				.and(isbn == null ? null : CatalogoSpecifications.isbnContains(isbn))
				;

		return repository.findAll(specification, pageable);
	}

	public Page<Catalogo> findByAny(String parametro, Pageable pageable) {
		Specification<Catalogo> specification = Specification
				.where(CatalogoSpecifications.descripcionContains(parametro))
				.or(CatalogoSpecifications.autorContains(parametro))
				.or(CatalogoSpecifications.editorialContains(parametro))
				.or(CatalogoSpecifications.temaContains(parametro))
				.or(CatalogoSpecifications.isbnContains(parametro))
				;
		return repository.findAll(specification, pageable);
	}

}
