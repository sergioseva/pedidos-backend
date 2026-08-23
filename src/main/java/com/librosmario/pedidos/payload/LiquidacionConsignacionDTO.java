package com.librosmario.pedidos.payload;

import java.util.List;

/** Lo que el operador marco en el estado de cuenta al liquidar con un comercio. */
public class LiquidacionConsignacionDTO {

	private Integer comercioId;
	private List<LineaLiquidacionDTO> lineas;
	private String observaciones;
	/** Si el comercio pago en el acto se emite el recibo; si no, el remito de venta queda impago. */
	private boolean registrarPago;
	private String medioPago;

	public Integer getComercioId() {
		return comercioId;
	}

	public void setComercioId(Integer comercioId) {
		this.comercioId = comercioId;
	}

	public List<LineaLiquidacionDTO> getLineas() {
		return lineas;
	}

	public void setLineas(List<LineaLiquidacionDTO> lineas) {
		this.lineas = lineas;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public boolean isRegistrarPago() {
		return registrarPago;
	}

	public void setRegistrarPago(boolean registrarPago) {
		this.registrarPago = registrarPago;
	}

	public String getMedioPago() {
		return medioPago;
	}

	public void setMedioPago(String medioPago) {
		this.medioPago = medioPago;
	}

	/** Un titulo del estado de cuenta, con cuantos ejemplares se venden y cuantos vuelven. */
	public static class LineaLiquidacionDTO {
		private String isbn;
		private String nombreLibro;
		private String autor;
		private String editorial;
		private Double precio;
		private Integer cantidadVendida;
		private Integer cantidadDevuelta;

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

		public String getAutor() {
			return autor;
		}

		public void setAutor(String autor) {
			this.autor = autor;
		}

		public String getEditorial() {
			return editorial;
		}

		public void setEditorial(String editorial) {
			this.editorial = editorial;
		}

		public Double getPrecio() {
			return precio;
		}

		public void setPrecio(Double precio) {
			this.precio = precio;
		}

		public Integer getCantidadVendida() {
			return cantidadVendida == null ? 0 : cantidadVendida;
		}

		public void setCantidadVendida(Integer cantidadVendida) {
			this.cantidadVendida = cantidadVendida;
		}

		public Integer getCantidadDevuelta() {
			return cantidadDevuelta == null ? 0 : cantidadDevuelta;
		}

		public void setCantidadDevuelta(Integer cantidadDevuelta) {
			this.cantidadDevuelta = cantidadDevuelta;
		}
	}
}
