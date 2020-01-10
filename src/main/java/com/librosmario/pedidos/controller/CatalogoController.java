package com.librosmario.pedidos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.librosmario.pedidos.entity.Catalogo;
import com.librosmario.pedidos.repository.CatalogoRepository;
import com.librosmario.pedidos.service.CatalogoService;

@RestController
public class CatalogoController {
	

	@Autowired
	CatalogoService service;

    
    @GetMapping( value = "/catalogos/search/findByAny")
    public ResponseEntity<List<Catalogo>> findByAny(@Param("parametro") String parametro) {
    	
    	List<Catalogo> catalogos=service.findByAny(parametro);
    	return ResponseEntity.ok(catalogos); 
    	
    }
    
    @GetMapping( value = "/catalogos/search/findByAll")
    public ResponseEntity<List<Catalogo>> findByAll(@Param("libro") String libro,
    												@Param("isbn") 	String isbn,
    												@Param("autor") String autor,
    												@Param("editorial") String editorial,
    												@Param("tema") String tema
    		) {
    	
    	List<Catalogo> catalogos=service.findByAll(libro,autor,editorial,tema,isbn);
    	return ResponseEntity.ok(catalogos); 
    	
    }

}
