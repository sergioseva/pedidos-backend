package com.librosmario.pedidos.repository.specifications;

import java.text.MessageFormat;

import org.springframework.data.jpa.domain.Specification;

import com.librosmario.pedidos.entity.Catalogo;

//https://dimitr.im/writing-dynamic-queries-with-spring-data-jpa
public final class CatalogoSpecifications {

	private static String contains(String expression) {
	    return MessageFormat.format("%{0}%", expression.toLowerCase());
	}

	public static Specification<Catalogo> autorContains(String expression) {
	    return (root, query, builder) -> builder.like(builder.lower(root.get("autor")), contains(expression));
	}

	public static Specification<Catalogo> descripcionContains(String expression) {
	    return (root, query, builder) -> builder.like(builder.lower(root.get("descripcion")), contains(expression));
	}

	public static Specification<Catalogo> editorialContains(String expression) {
	    return (root, query, builder) -> builder.like(builder.lower(root.get("editorial")), contains(expression));
	}

	public static Specification<Catalogo> temaContains(String expression) {
	    return (root, query, builder) -> builder.like(builder.lower(root.get("tema")), contains(expression));
	}

	public static Specification<Catalogo> isbnContains(String expression) {
	    return (root, query, builder) -> builder.like(builder.lower(root.get("isbn")), contains(expression));
	}

	public static Specification<Catalogo> observacionesContains(String expression) {
	    return (root, query, builder) -> builder.like(builder.lower(root.get("observaciones")), contains(expression));
	}
}
