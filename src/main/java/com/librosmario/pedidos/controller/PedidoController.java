package com.librosmario.pedidos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.librosmario.pedidos.entity.Pedido;
import com.librosmario.pedidos.service.PedidoService;

@RestController
public class PedidoController {
	
	@Autowired 
	PedidoService service;
	
	

    @PostMapping(value="/pedidos",consumes={"application/json"})
    public ResponseEntity<Pedido> createPedido(@RequestBody Pedido pedido){
    	
    	try {
    		service.createPedido(pedido);
    		return new ResponseEntity<Pedido>(pedido,HttpStatus.CREATED);
    	} catch (Exception e) {
    		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "There was an error saving the pedido", e);
    		//return new ResponseEntity<Pedido>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    @GetMapping( value = "/pedidos/search/findByAny")
    public ResponseEntity<List<Pedido>> findByAny(@Param("parametro") String parametro,
    											  @Param("fechaDesde") String fechaDesde,
    											  @Param("fechaHasta") String fechaHasta) {
    	
    	List<Pedido> pedidos=service.findByAny(parametro,fechaDesde,fechaHasta);
    	return ResponseEntity.ok(pedidos); 
    	
    }
    
    @GetMapping( value = "/pedidos/search/findByAll")
    public ResponseEntity<List<Pedido>> findByAll(@Param("libro") String libro,
    											  @Param("cliente") String cliente,
    											  @Param("fechaDesde") String fechaDesde,
    											  @Param("fechaHasta") String fechaHasta
    		) {

    	
    	List<Pedido> pedidos=service.findByAll(libro,cliente,fechaDesde,fechaHasta);
    	return ResponseEntity.ok(pedidos); 
    	
    }
    


}
