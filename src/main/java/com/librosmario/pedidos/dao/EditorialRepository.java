package com.librosmario.pedidos.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.librosmario.pedidos.entity.Editorial;

@RepositoryRestResource(path="editoriales")
public interface EditorialRepository extends JpaRepository<Editorial, Integer> {

}
