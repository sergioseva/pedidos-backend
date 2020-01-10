package com.librosmario.pedidos.repository.specifications;

import java.text.MessageFormat;

import org.springframework.data.jpa.domain.Specification;

import com.librosmario.pedidos.entity.Cliente;

//https://dimitr.im/writing-dynamic-queries-with-spring-data-jpa
public final class ClienteSpecifications {

	private static String contains(String expression) {
	    return MessageFormat.format("%{0}%", expression);
	}
	
	public static Specification<Cliente> nombre(String expression) {
	    return (root, query, builder) -> builder.like(root.get("nombre"), contains(expression));
	}

	public static Specification<Cliente> direccionContains(String expression) {
	    return (root, query, builder) -> builder.like(root.get("direccion"), contains(expression));
	}

	public static Specification<Cliente> telefonoFijoContains(String expression) {
	    return (root, query, builder) -> builder.like(root.get("telefonoFijo"), contains(expression));
	}
	
	public static Specification<Cliente> telefonoMovilContains(String expression) {
	    return (root, query, builder) -> builder.like(root.get("telefonoMovil"), contains(expression));
	}
	
	public static Specification<Cliente> telefonoLaboralContains(String expression) {
	    return (root, query, builder) -> builder.like(root.get("telefonoLaboral"), contains(expression));
	}
	
	public static Specification<Cliente> emailContains(String expression) {
	    return (root, query, builder) -> builder.like(root.get("email"), contains(expression));
	}
}
