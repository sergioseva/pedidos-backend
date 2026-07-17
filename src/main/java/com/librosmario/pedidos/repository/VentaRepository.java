package com.librosmario.pedidos.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.librosmario.pedidos.entity.Venta;
import com.librosmario.pedidos.payload.VentaPorDiaDTO;

/** Not exported: ventas are reached only through VentaController, which enforces who may read them. */
@RepositoryRestResource(exported = false)
public interface VentaRepository extends JpaRepository<Venta, Integer>, JpaSpecificationExecutor<Venta> {

	/**
	 * Revenue per day, summed FROM THE LINES.
	 *
	 * The join multiplies each Venta row once per line it has. That is why tickets are counted with
	 * count(distinct v.id) and why sum(v.total) must NEVER be added to this query -- summing the
	 * ticket total across a fanned-out join would silently multiply a 3-line sale by three and
	 * report confident, wrong money. Range totals live in resumen(), which does not join.
	 */
	@Query("SELECT new com.librosmario.pedidos.payload.VentaPorDiaDTO("
			+ "  year(v.fecha), month(v.fecha), day(v.fecha),"
			+ "  count(distinct v.id),"
			+ "  coalesce(sum(i.cantidad), 0L),"
			+ "  coalesce(sum(i.cantidad * i.precio), 0D))"
			+ " FROM Venta v LEFT JOIN v.items i"
			+ " WHERE v.fecha >= :desde AND v.fecha <= :hasta"
			+ " GROUP BY year(v.fecha), month(v.fecha), day(v.fecha)"
			+ " ORDER BY year(v.fecha) DESC, month(v.fecha) DESC, day(v.fecha) DESC")
	List<VentaPorDiaDTO> ventasPorDia(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

	/** Tickets in a range. */
	@Query("SELECT count(v) FROM Venta v WHERE v.fecha >= :desde AND v.fecha <= :hasta")
	long contarVentas(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

	/** Revenue in a range. No join here, so ve_total is safe to sum. */
	@Query("SELECT coalesce(sum(v.total), 0D) FROM Venta v WHERE v.fecha >= :desde AND v.fecha <= :hasta")
	Double totalVendido(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

	/** Units sold in a range. Kept as its own query because it needs the join the others must avoid. */
	@Query("SELECT coalesce(sum(i.cantidad), 0L) FROM Venta v JOIN v.items i"
			+ " WHERE v.fecha >= :desde AND v.fecha <= :hasta")
	Long unidadesVendidas(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);
}
