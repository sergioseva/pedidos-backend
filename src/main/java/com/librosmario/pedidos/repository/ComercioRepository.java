package com.librosmario.pedidos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.librosmario.pedidos.entity.Comercio;

@RepositoryRestResource(exported = false)
public interface ComercioRepository extends JpaRepository<Comercio, Integer> {

	List<Comercio> findByDescripcionContainingIgnoreCaseOrDireccionContainingIgnoreCaseOrContactoContainingIgnoreCase(
			String descripcion, String direccion, String contacto);

	List<Comercio> findAllByOrderByDescripcionAsc();
}
