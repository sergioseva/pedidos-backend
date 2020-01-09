package com.librosmario.pedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.librosmario.pedidos.entity.Distribuidora;

@RepositoryRestResource(path="distribuidoras")
public interface DistribuidoraRepository extends JpaRepository<Distribuidora, Integer> {

}
