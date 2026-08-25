package com.librosmario.pedidos.payload;

/** El remito a medio cargar, tal como lo maneja la pantalla. */
public class BorradorDTO {

	private String tipo;
	private String contenido;

	public BorradorDTO() {
	}

	public BorradorDTO(String tipo, String contenido) {
		this.tipo = tipo;
		this.contenido = contenido;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getContenido() {
		return contenido;
	}

	public void setContenido(String contenido) {
		this.contenido = contenido;
	}
}
