package com.librosmario.pedidos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.librosmario.pedidos.entity.Cliente;
import com.librosmario.pedidos.repository.ClienteRepository;

@RepositoryRestController
public class ClienteController {
	
	private final ClienteRepository repository;
	
    @Autowired
    public ClienteController(ClienteRepository repo) { 
        repository = repo;
    }
    
    @GetMapping( value = "/clientes/search/findByName")
    public @ResponseBody ResponseEntity<?> findByName(@Param("name") String name) {
    	
    	List<Cliente> clientes=repository.findByNombreContainsAllIgnoreCase(name);
    	
    	Resources<Cliente> resources = new Resources<Cliente>(clientes);
    	return ResponseEntity.ok(resources); 
    	
    }
    
    @GetMapping( value = "/clientes/search/findByPhone")
    public @ResponseBody ResponseEntity<?> findByCriteria(@Param("phone") String phone) {
    	
    	List<Cliente> clientes=repository.findByTelefonoMovilContainsAllIgnoreCase(phone);
    	
    	Resources<Cliente> resources = new Resources<Cliente>(clientes);
    	return ResponseEntity.ok(resources); 
    	
    }

}
