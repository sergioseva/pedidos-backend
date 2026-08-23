package com.librosmario.pedidos.payload;

/**
 * Una fila del estado de cuenta de consignacion: cuantos ejemplares de un titulo tiene
 * entregados un comercio. Construida por una expresion constructor de JPQL.
 */
public class ConsignacionEstadoCuentaDTO {

	private final Integer comercioId;
	private final String comercio;
	private final String isbn;
	private final String nombreLibro;
	private final String autor;
	private final String editorial;
	private final long entregado;
	private final long devuelto;
	private final long vendido;
	private final long cantidad;
	private final double precio;
	private final double subtotal;

	public ConsignacionEstadoCuentaDTO(Integer comercioId, String comercio, String isbn, String nombreLibro,
			String autor, String editorial, Long entregado, Long devuelto, Long vendido, Double precio) {
		this.comercioId = comercioId;
		this.comercio = comercio;
		this.isbn = isbn;
		this.nombreLibro = nombreLibro;
		this.autor = autor;
		this.editorial = editorial;
		this.entregado = entregado == null ? 0L : entregado;
		this.devuelto = devuelto == null ? 0L : devuelto;
		this.vendido = vendido == null ? 0L : vendido;
		this.cantidad = this.entregado - this.devuelto - this.vendido;
		this.precio = precio == null ? 0d : precio;
		this.subtotal = this.cantidad * this.precio;
	}

	public Integer getComercioId() {
		return comercioId;
	}

	public String getComercio() {
		return comercio;
	}

	public String getIsbn() {
		return isbn;
	}

	public String getNombreLibro() {
		return nombreLibro;
	}

	public String getAutor() {
		return autor;
	}

	public String getEditorial() {
		return editorial;
	}

	public long getEntregado() {
		return entregado;
	}

	public long getDevuelto() {
		return devuelto;
	}

	public long getVendido() {
		return vendido;
	}

	/** Saldo en la calle: lo entregado menos lo que ya volvio y lo que el comercio vendio. */
	public long getCantidad() {
		return cantidad;
	}

	public double getPrecio() {
		return precio;
	}

	public double getSubtotal() {
		return subtotal;
	}
}
