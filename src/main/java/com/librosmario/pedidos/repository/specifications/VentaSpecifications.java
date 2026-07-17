package com.librosmario.pedidos.repository.specifications;

import java.text.MessageFormat;
import java.time.LocalDateTime;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import com.librosmario.pedidos.entity.Cliente;
import com.librosmario.pedidos.entity.Venta;
import com.librosmario.pedidos.entity.VentaItem;

public final class VentaSpecifications {

	private VentaSpecifications() {
	}

	private static String contains(String expression) {
		return MessageFormat.format("%{0}%", expression.toLowerCase());
	}

	/** Inner join: a sale with no cliente simply cannot match a cliente filter. */
	public static Specification<Venta> clienteContains(String expression) {
		return (root, query, builder) -> {
			Join<Venta, Cliente> clienteJoin = root.join("cliente");
			return builder.like(builder.lower(clienteJoin.get("nombre")), contains(expression));
		};
	}

	public static Specification<Venta> observacionesContains(String expression) {
		return (root, query, builder) ->
			builder.like(builder.lower(root.get("observaciones")), contains(expression));
	}

	public static Specification<Venta> usuarioContains(String expression) {
		return (root, query, builder) ->
			builder.like(builder.lower(root.get("usuario")), contains(expression));
	}

	public static Specification<Venta> itemLibroContains(String expression) {
		return (root, query, builder) -> {
			Join<Venta, VentaItem> itemJoin = root.join("items");
			Predicate likePredicate = builder.like(builder.lower(itemJoin.get("libro")), contains(expression));
			query.distinct(true);
			return likePredicate;
		};
	}

	/** Exact, not contains: an ISBN filter comes from a scan or a paste, never a fragment. */
	public static Specification<Venta> itemIsbnEquals(String isbn) {
		return (root, query, builder) -> {
			Join<Venta, VentaItem> itemJoin = root.join("items");
			Predicate isbnPredicate = builder.equal(itemJoin.get("isbn"), isbn);
			query.distinct(true);
			return isbnPredicate;
		};
	}

	public static Specification<Venta> fechaGreaterOrEquals(LocalDateTime fecha) {
		return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("fecha"), fecha);
	}

	public static Specification<Venta> fechaLessOrEquals(LocalDateTime fecha) {
		return (root, query, builder) -> builder.lessThanOrEqualTo(root.get("fecha"), fecha);
	}
}
