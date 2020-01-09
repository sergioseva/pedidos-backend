package com.librosmario.pedidos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	
	

    @PostMapping(value="/pedidosnuevo",consumes={"application/json"})
    public ResponseEntity<Pedido> createPedido(@RequestBody Pedido pedido){
    	
    	try {
    		service.createPedido(pedido);
    		return new ResponseEntity<Pedido>(pedido,HttpStatus.CREATED);
    	} catch (Exception e) {
    		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "There was an error saving the pedido", e);
    		//return new ResponseEntity<Pedido>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    


}
