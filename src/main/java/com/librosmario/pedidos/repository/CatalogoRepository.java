package com.librosmario.pedidos.repository;

import java.text.MessageFormat;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.librosmario.pedidos.entity.Catalogo;

@RepositoryRestResource(path="catalogos")
@CrossOrigin(origins = "*")
public interface CatalogoRepository extends JpaRepository<Catalogo, Integer>,JpaSpecificationExecutor<Catalogo> {


	

	
}
