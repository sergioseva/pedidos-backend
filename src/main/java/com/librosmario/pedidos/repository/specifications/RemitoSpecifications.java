package com.librosmario.pedidos.repository.specifications;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import com.librosmario.pedidos.entity.Comercio;
import com.librosmario.pedidos.entity.Distribuidora;
import com.librosmario.pedidos.entity.Remito;
import com.librosmario.pedidos.entity.RemitoItem;

public final class RemitoSpecifications {

	private static String contains(String expression) {
	    return MessageFormat.format("%{0}%", expression);
	}

	/**
	 * Busca en el destinatario, sea distribuidora (devolucion) o comercio (consignacion).
	 *
	 * Los joins son LEFT a proposito: un remito tiene uno de los dos destinatarios y el otro en
	 * null, asi que un INNER JOIN dejaria fuera de TODA la consulta -- no solo de este predicado --
	 * a la mitad de los remitos, incluso cuando el termino haya matcheado por observaciones o libro.
	 */
	public static Specification<Remito> destinatarioContains(String expression) {
	    return (root, query, builder) -> {
	    	Join<Remito, Distribuidora> distribuidoraJoin = root.join("re_distribuidora_ed", JoinType.LEFT);
	    	Join<Remito, Comercio> comercioJoin = root.join("re_comercio_cm", JoinType.LEFT);
	        return builder.or(
	        		builder.like(distribuidoraJoin.get("descripcion"), contains(expression)),
	        		builder.like(comercioJoin.get("descripcion"), contains(expression)));
	    };
	}

	public static Specification<Remito> observacionesContains(String expression) {
	    return (root, query, builder) ->
	        builder.like(root.get("re_observaciones"), contains(expression));
	}

	public static Specification<Remito> itemLibroContains(String expression) {
	    return (root, query, builder) -> {
	    	Join<Remito, RemitoItem> itemJoin = root.join("items", JoinType.LEFT);
	        Predicate likePredicate = builder.like(itemJoin.get("ri_nombre_libro"), contains(expression));
	        query.distinct(true);
	        return likePredicate;
	    };
	}

	/**
	 * Filtra por uno o varios tipos: la consulta de consignacion necesita ver juntos las entregas,
	 * los retiros y las ventas, que son las tres patas de la misma cuenta.
	 *
	 * DEVOLUCION tambien matchea los remitos con tipo nulo: si por lo que sea la migracion no
	 * corrio, esos remitos son devoluciones anteriores a la consignacion, y desaparecer de la
	 * consulta es peor que aparecer en la lista equivocada.
	 */
	public static Specification<Remito> tipoIn(List<String> tipos) {
	    return (root, query, builder) -> {
	        Predicate enLaLista = root.get("re_tipo").in(tipos);
	        if (tipos.contains(Remito.TIPO_DEVOLUCION)) {
	            return builder.or(enLaLista, builder.isNull(root.get("re_tipo")));
	        }
	        return enLaLista;
	    };
	}

	public static Specification<Remito> comercioIdEquals(Integer comercioId) {
	    return (root, query, builder) -> {
	    	Join<Remito, Comercio> comercioJoin = root.join("re_comercio_cm", JoinType.LEFT);
	        return builder.equal(comercioJoin.get("id"), comercioId);
	    };
	}

	public static Specification<Remito> fechaGreaterOrEquals(Date fecha) {
	    return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("re_fecha"), fecha);
	}

	public static Specification<Remito> fechaLessOrEquals(Date fecha) {
	    return (root, query, builder) -> builder.lessThanOrEqualTo(root.get("re_fecha"), fecha);
	}

}
