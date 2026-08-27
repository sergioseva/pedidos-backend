package com.librosmario.pedidos.payload;

/**
 * Un comercio con cuantos ejemplares tiene hoy en consignacion, para el desplegable.
 *
 * El numero se calcula en el momento y no se cachea: la consulta trae todos los comercios de una
 * y tarda un par de milisegundos, mientras que una cache habria que invalidarla en cada entrega,
 * retiro, venta, liquidacion y cambio de precio. Olvidarse de una sola dejaria un numero
 * equivocado sin que nada falle, que es la peor clase de error.
 */
public class ResumenComercioDTO {

	private final Integer id;
	private final String descripcion;
	private final Double comision;
	private final long unidades;

	public ResumenComercioDTO(Integer id, String descripcion, Double comision, Long unidades) {
		this.id = id;
		this.descripcion = descripcion;
		this.comision = comision;
		this.unidades = unidades == null ? 0L : unidades;
	}

	public Integer getId() {
		return id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public Double getComision() {
		return comision;
	}

	public long getUnidades() {
		return unidades;
	}
}
