package com.librosmario.pedidos.repository.specifications;

import java.text.MessageFormat;
import java.time.LocalDateTime;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import com.librosmario.pedidos.entity.Distribuidora;
import com.librosmario.pedidos.entity.PedidoDistribuidora;
import com.librosmario.pedidos.entity.PedidoItem;

public final class PedidoDistribuidoraSpecifications {

	private static String contains(String expression) {
	    return MessageFormat.format("%{0}%", expression);
	}

	public static Specification<PedidoDistribuidora> distribuidoraContains(String expression) {
	    return (root, query, builder) -> {
	    	Join<PedidoDistribuidora, Distribuidora> distribuidoraJoin = root.join("distribuidora");
	        return builder.like(distribuidoraJoin.get("descripcion"), contains(expression));
	    };
	}

	public static Specification<PedidoDistribuidora> itemLibroContains(String expression) {
	    return (root, query, builder) -> {
	    	Join<PedidoDistribuidora, PedidoItem> itemJoin = root.join("items");
	        Predicate likePredicate = builder.like(itemJoin.get("libro"), contains(expression));
	        query.distinct(true);
	        return likePredicate;
	    };
	}

	public static Specification<PedidoDistribuidora> fechaGreaterOrEquals(LocalDateTime fecha) {
	    return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("fecha"), fecha);
	}

	public static Specification<PedidoDistribuidora> fechaLessOrEquals(LocalDateTime fecha) {
	    return (root, query, builder) -> builder.lessThanOrEqualTo(root.get("fecha"), fecha);
	}

}
