package com.librosmario.pedidos.payload;

import java.time.LocalDate;

/** One row of the "ventas por dia" report. Built by a JPQL constructor expression. */
public class VentaPorDiaDTO {

	private final LocalDate fecha;
	private final long cantidadVentas;
	private final long unidades;
	private final double total;

	/**
	 * Takes the date in parts because JPQL's portable date functions are year()/month()/day();
	 * casting to a date type is what would tie the query to one database.
	 */
	public VentaPorDiaDTO(Integer anio, Integer mes, Integer dia, Long cantidadVentas, Long unidades, Double total) {
		this.fecha = LocalDate.of(anio, mes, dia);
		this.cantidadVentas = cantidadVentas == null ? 0L : cantidadVentas;
		this.unidades = unidades == null ? 0L : unidades;
		this.total = total == null ? 0d : total;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public long getCantidadVentas() {
		return cantidadVentas;
	}

	public long getUnidades() {
		return unidades;
	}

	public double getTotal() {
		return total;
	}
}
