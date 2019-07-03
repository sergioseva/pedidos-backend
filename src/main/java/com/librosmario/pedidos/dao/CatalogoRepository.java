package com.librosmario.pedidos.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.librosmario.pedidos.entity.Catalogo;


public interface CatalogoRepository extends JpaRepository<Catalogo, Integer> {

}
