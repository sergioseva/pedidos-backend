package com.librosmario.pedidos.payload;

/** Precio nuevo para un titulo que un comercio tiene en consignacion. */
public class ActualizacionPrecioDTO {

	private Integer comercioId;
	private String isbn;
	private String nombreLibro;
	private Double precio;

	public Integer getComercioId() {
		return comercioId;
	}

	public void setComercioId(Integer comercioId) {
		this.comercioId = comercioId;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getNombreLibro() {
		return nombreLibro;
	}

	public void setNombreLibro(String nombreLibro) {
		this.nombreLibro = nombreLibro;
	}

	public Double getPrecio() {
		return precio;
	}

	public void setPrecio(Double precio) {
		this.precio = precio;
	}
}
