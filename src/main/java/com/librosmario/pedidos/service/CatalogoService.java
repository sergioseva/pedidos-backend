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
				.and(tema == null ? null : CatalogoSpecifications.temaContains(tema))
				.and(isbn == null ? null : CatalogoSpecifications.isbnContains(isbn))
				;

		return repository.findAll(specification, pageable);
	}

	public Page<Catalogo> findByAny(String parametro, String descripcion, String autor, String editorial, String isbn, String observaciones, Pageable pageable) {
		Specification<Catalogo> specification = Specification.where(null);

		if (parametro != null && !parametro.isEmpty()) {
			Specification<Catalogo> orSpec = Specification
					.where(CatalogoSpecifications.descripcionContains(parametro))
					.or(CatalogoSpecifications.autorContains(parametro))
					.or(CatalogoSpecifications.editorialContains(parametro))
					.or(CatalogoSpecifications.temaContains(parametro))
					.or(CatalogoSpecifications.isbnContains(parametro));
			specification = specification.and(orSpec);
		}

		if (descripcion != null && !descripcion.isEmpty()) {
			specification = specification.and(CatalogoSpecifications.descripcionContains(descripcion));
		}
		if (autor != null && !autor.isEmpty()) {
			specification = specification.and(CatalogoSpecifications.autorContains(autor));
		}
		if (editorial != null && !editorial.isEmpty()) {
			specification = specification.and(CatalogoSpecifications.editorialContains(editorial));
		}
		if (isbn != null && !isbn.isEmpty()) {
			specification = specification.and(CatalogoSpecifications.isbnContains(isbn));
		}
		if (observaciones != null && !observaciones.isEmpty()) {
			specification = specification.and(CatalogoSpecifications.observacionesContains(observaciones));
		}

		return repository.findAll(specification, pageable);
	}

}
