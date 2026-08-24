package com.librosmario.pedidos.payload;

/** Cuantos titulos se pudieron actualizar desde el catalogo y cuantos quedaron sin coincidencia. */
public class ResultadoPreciosDTO {

	private final int actualizados;
	private final int sinCoincidencia;

	public ResultadoPreciosDTO(int actualizados, int sinCoincidencia) {
		this.actualizados = actualizados;
		this.sinCoincidencia = sinCoincidencia;
	}

	public int getActualizados() {
		return actualizados;
	}

	public int getSinCoincidencia() {
		return sinCoincidencia;
	}
}
