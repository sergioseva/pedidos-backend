package com.librosmario.pedidos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.librosmario.pedidos.entity.Distribuidora;

@RepositoryRestResource(exported = false)
public interface DistribuidoraRepository extends JpaRepository<Distribuidora, Integer> {

	List<Distribuidora> findByDescripcionContainingIgnoreCase(String descripcion);

	List<Distribuidora> findByNroCuentaContainingIgnoreCase(String nroCuenta);

	List<Distribuidora> findByDescripcionContainingIgnoreCaseOrNroCuentaContainingIgnoreCase(String descripcion, String nroCuenta);
}
