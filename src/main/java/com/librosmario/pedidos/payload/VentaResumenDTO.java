package com.librosmario.pedidos.payload;

/** Totals for a date range. Read straight off ve_total, with no join, so nothing can fan out. */
public class VentaResumenDTO {

	private final long cantidadVentas;
	private final long unidades;
	private final double total;
	private final double ticketPromedio;

	public VentaResumenDTO(long cantidadVentas, long unidades, double total) {
		this.cantidadVentas = cantidadVentas;
		this.unidades = unidades;
		this.total = total;
		this.ticketPromedio = cantidadVentas == 0 ? 0d : Math.round((total / cantidadVentas) * 100d) / 100d;
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

	public double getTicketPromedio() {
		return ticketPromedio;
	}
}
