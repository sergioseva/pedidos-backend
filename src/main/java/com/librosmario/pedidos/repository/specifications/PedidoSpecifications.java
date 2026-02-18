package com.librosmario.pedidos.repository.specifications;

import java.text.MessageFormat;
import java.time.LocalDateTime;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.ListJoin;
import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import com.librosmario.pedidos.entity.Cliente;
import com.librosmario.pedidos.entity.Cliente_;
import com.librosmario.pedidos.entity.Pedido;
import com.librosmario.pedidos.entity.PedidoItem;
import com.librosmario.pedidos.entity.PedidoItem_;
import com.librosmario.pedidos.entity.Pedido_;

public final class  PedidoSpecifications {

	private static String contains(String expression) {
	    return MessageFormat.format("%{0}%", expression);
	}
	
	public static Specification<Pedido>  getPedidosConLibro(String libro) {
		
	    return (root, query, builder) -> { 
	    	ListJoin<Pedido, PedidoItem> pedidoItemJoin = root.join(Pedido_.pedidoItems);
            Predicate likePredicate = builder.like(pedidoItemJoin.get(PedidoItem_.libro), contains(libro));
            query.distinct(true);
            return likePredicate;
	    };	
	}
	
	public static Specification<Pedido> clienteContains(String expression) {
		
	    return (root, query, builder) -> { 
	    	Join<Pedido,Cliente> clienteJoin = root.join(Pedido_.cliente);
            Predicate nombrePredicate = builder.like(clienteJoin.get(Cliente_.nombre), contains(expression));
            Predicate telefonoPredicate = builder.like(clienteJoin.get(Cliente_.email), contains(expression));
            Predicate emailPredicate = builder.like(clienteJoin.get(Cliente_.telefonoMovil), contains(expression));
            
            return builder.or(nombrePredicate, telefonoPredicate, emailPredicate);
	    };	
	    
	}
	
	public static Specification<Pedido> pedidoFechaGreaterOrEquals(LocalDateTime fecha) {
	    return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("fecha"),fecha);
	}
	
	public static Specification<Pedido> pedidoFechaLessOrEquals(LocalDateTime fecha) {
	    return (root, query, builder) -> builder.lessThanOrEqualTo(root.get("fecha"),fecha);

	}


}
