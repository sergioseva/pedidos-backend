package com.librosmario.pedidos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.librosmario.pedidos.entity.Cliente;
import com.librosmario.pedidos.repository.ClienteRepository;
import com.librosmario.pedidos.service.ClienteService;

@RestController
public class ClienteController {
	
	
	@Autowired
	private ClienteRepository repository;
	
	@Autowired
	ClienteService service;
    
    
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
    
    @GetMapping( value = "/clientes/search/findByAny")
    public @ResponseBody ResponseEntity<List<Cliente>> findByAny(@Param("parametro") String parametro) {
    	
    	List<Cliente> clientes=service.findByAny(parametro);
    	return ResponseEntity.ok(clientes); 
    }

}
