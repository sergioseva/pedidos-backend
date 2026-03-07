package com.librosmario.pedidos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public ResponseEntity<List<PedidoDistribuidora>> createPedido(@RequestBody ConfirmacionPedidoADistribuidoraDTO  cpd){

    	try {
    		List<PedidoDistribuidora> result = pedidoDistribuidoraService.confirmarPedidoADistribuidora(cpd.getItems(), cpd.getDistribuidora());
    		return new ResponseEntity<>(result, HttpStatus.CREATED);
    	} catch (Exception e) {
    		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "There was an error saving the pedidoDistribuidora", e);
    	}
    }

    @PostMapping(value="/pedidodistribuidora/confirmarLlegada/{itemId}")
    public ResponseEntity<Void> confirmarLlegada(@PathVariable Integer itemId) {
    	try {
    		pedidoDistribuidoraService.confirmarLlegada(itemId);
    		return ResponseEntity.ok().build();
    	} catch (Exception e) {
    		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error confirming arrival", e);
    	}
    }

    @PostMapping(value="/pedidodistribuidora/confirmarLlegadaBulk")
    public ResponseEntity<Void> confirmarLlegadaBulk(@RequestBody List<Integer> itemIds) {
    	try {
    		for (Integer itemId : itemIds) {
    			pedidoDistribuidoraService.confirmarLlegada(itemId);
    		}
    		return ResponseEntity.ok().build();
    	} catch (Exception e) {
    		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error confirming arrival", e);
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
