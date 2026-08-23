package com.librosmario.pedidos.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.librosmario.pedidos.entity.RemitoItem;
import com.librosmario.pedidos.payload.ConsignacionEstadoCuentaDTO;

@RepositoryRestResource(exported = false)
public interface RemitoItemRepository extends JpaRepository<RemitoItem, Integer> {

	/**
	 * Saldo en la calle por comercio y titulo: entregado - devuelto - vendido.
	 *
	 * Los tres movimientos son remitos y viven en la misma tabla, asi que una sola pasada con
	 * sumas condicionales alcanza. El HAVING descarta los titulos ya saldados, que son la mayoria
	 * con el tiempo y no aportan nada a una pantalla cuyo objeto es decidir que liquidar.
	 *
	 * Agrupa por nombre y no por ISBN porque los items cargados a mano suelen venir sin ISBN, y
	 * agrupar por una clave nula juntaria titulos distintos en una sola fila. El precio se toma
	 * con max(): dentro del grupo es el mismo salvo que haya cambiado entre entregas, y en ese
	 * caso el vigente es el mas alto.
	 *
	 * El filtro de fechas se aplica solo a las entregas. Acotarlo tambien a las bajas dejaria
	 * afuera retiros y ventas posteriores al rango y el saldo saldria inflado.
	 */
	@Query("SELECT new com.librosmario.pedidos.payload.ConsignacionEstadoCuentaDTO("
			+ "  c.id, c.descripcion, i.ri_isbn, i.ri_nombre_libro, i.ri_autor, i.ri_editorial,"
			+ "  coalesce(sum(CASE WHEN r.re_tipo = 'CONSIGNACION' THEN i.ri_cantidad ELSE 0 END), 0L),"
			+ "  coalesce(sum(CASE WHEN r.re_tipo = 'RETIRO' THEN i.ri_cantidad ELSE 0 END), 0L),"
			+ "  coalesce(sum(CASE WHEN r.re_tipo = 'VENTA_CONSIGNACION' THEN i.ri_cantidad ELSE 0 END), 0L),"
			+ "  coalesce(max(i.ri_precio), 0D))"
			+ " FROM RemitoItem i JOIN i.ri_remito_re r JOIN r.re_comercio_cm c"
			+ " WHERE r.re_tipo IN ('CONSIGNACION', 'RETIRO', 'VENTA_CONSIGNACION')"
			+ "   AND (:comercioId IS NULL OR c.id = :comercioId)"
			+ "   AND (r.re_tipo <> 'CONSIGNACION'"
			+ "        OR ((:desde IS NULL OR r.re_fecha >= :desde)"
			+ "            AND (:hasta IS NULL OR r.re_fecha <= :hasta)))"
			+ " GROUP BY c.id, c.descripcion, i.ri_isbn, i.ri_nombre_libro, i.ri_autor, i.ri_editorial"
			+ " HAVING sum(CASE WHEN r.re_tipo = 'CONSIGNACION' THEN i.ri_cantidad ELSE -i.ri_cantidad END) > 0"
			+ " ORDER BY c.descripcion ASC, i.ri_nombre_libro ASC")
	List<ConsignacionEstadoCuentaDTO> estadoCuentaConsignacion(@Param("comercioId") Integer comercioId,
			@Param("desde") Date desde, @Param("hasta") Date hasta);
}
