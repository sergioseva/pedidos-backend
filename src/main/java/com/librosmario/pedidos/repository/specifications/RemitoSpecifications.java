package com.librosmario.pedidos.repository.specifications;

import java.text.MessageFormat;
import java.util.Date;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import com.librosmario.pedidos.entity.Distribuidora;
import com.librosmario.pedidos.entity.Remito;
import com.librosmario.pedidos.entity.RemitoItem;

public final class RemitoSpecifications {

	private static String contains(String expression) {
	    return MessageFormat.format("%{0}%", expression);
	}

	public static Specification<Remito> distribuidoraContains(String expression) {
	    return (root, query, builder) -> {
	    	Join<Remito, Distribuidora> distribuidoraJoin = root.join("re_distribuidora_ed");
	        return builder.like(distribuidoraJoin.get("descripcion"), contains(expression));
	    };
	}

	public static Specification<Remito> observacionesContains(String expression) {
	    return (root, query, builder) ->
	        builder.like(root.get("re_observaciones"), contains(expression));
	}

	public static Specification<Remito> itemLibroContains(String expression) {
	    return (root, query, builder) -> {
	    	Join<Remito, RemitoItem> itemJoin = root.join("items");
	        Predicate likePredicate = builder.like(itemJoin.get("ri_nombre_libro"), contains(expression));
	        query.distinct(true);
	        return likePredicate;
	    };
	}

	public static Specification<Remito> fechaGreaterOrEquals(Date fecha) {
	    return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("re_fecha"), fecha);
	}

	public static Specification<Remito> fechaLessOrEquals(Date fecha) {
	    return (root, query, builder) -> builder.lessThanOrEqualTo(root.get("re_fecha"), fecha);
	}

}
