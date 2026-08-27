package com.librosmario.pedidos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.librosmario.pedidos.entity.Comercio;
import com.librosmario.pedidos.payload.ResumenComercioDTO;

@RepositoryRestResource(exported = false)
public interface ComercioRepository extends JpaRepository<Comercio, Integer> {

	List<Comercio> findByDescripcionContainingIgnoreCaseOrDireccionContainingIgnoreCaseOrContactoContainingIgnoreCase(
			String descripcion, String direccion, String contacto);

	List<Comercio> findAllByOrderByDescripcionAsc();

	/**
	 * Todos los comercios con su saldo en consignacion, en una sola consulta.
	 *
	 * El LEFT JOIN es lo que hace que aparezcan tambien los que no tienen nada: si no, un negocio
	 * sin libros desapareceria del desplegable y no se lo podria elegir para entregarle.
	 */
	@Query("SELECT new com.librosmario.pedidos.payload.ResumenComercioDTO("
			+ "  c.id, c.descripcion, c.comision,"
			+ "  coalesce(sum(CASE WHEN r.re_tipo = 'CONSIGNACION' THEN i.ri_cantidad"
			+ "                    ELSE -i.ri_cantidad END), 0L))"
			+ " FROM Comercio c"
			+ " LEFT JOIN Remito r ON r.re_comercio_cm = c"
			+ "   AND r.re_tipo IN ('CONSIGNACION', 'RETIRO', 'VENTA_CONSIGNACION')"
			+ " LEFT JOIN RemitoItem i ON i.ri_remito_re = r"
			+ " GROUP BY c.id, c.descripcion, c.comision"
			+ " ORDER BY c.descripcion ASC")
	List<ResumenComercioDTO> resumenDeConsignacion();
}
