package com.librosmario.pedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.librosmario.pedidos.entity.RemitoItem;

@RepositoryRestResource(exported = false)
public interface RemitoItemRepository extends JpaRepository<RemitoItem, Integer> {

}
