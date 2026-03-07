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

import com.librosmario.pedidos.entity.PedidoDistribuidora;
import com.librosmario.pedidos.payload.ConfirmacionPedidoADistribuidoraDTO;
import com.librosmario.pedidos.service.PedidoDistribuidoraService;

@RestController
public class PedidoADistribuidoraController {
	
	
	@Autowired
	PedidoDistribuidoraService pedidoDistribuidoraService;

    @PostMapping(value="/pedidodistribuidora",consumes={"application/json"})
    public ResponseEntity<PedidoDistribuidora> createPedido(@RequestBody ConfirmacionPedidoADistribuidoraDTO  cpd){
    	
    	try {
    		PedidoDistribuidora pd=pedidoDistribuidoraService.confirmarPedidoADistribuidora(cpd.getItems(), cpd.getDistribuidora());
    		return new ResponseEntity<PedidoDistribuidora>(pd,HttpStatus.CREATED);
    	} catch (Exception e) {
    		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "There was an error saving the pedidoDistribuidora", e);
    		//return new ResponseEntity<Pedido>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }

    @GetMapping(value="/pedidosdistribuidora/search/findByAny")
    public ResponseEntity<List<PedidoDistribuidora>> findByAny(@Param("parametro") String parametro,
                                                               @Param("fechaDesde") String fechaDesde,
                                                               @Param("fechaHasta") String fechaHasta) {
    	List<PedidoDistribuidora> pedidos = pedidoDistribuidoraService.findByAny(parametro, fechaDesde, fechaHasta);
    	return ResponseEntity.ok(pedidos);
    }
}
