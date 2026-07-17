package com.librosmario.pedidos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * One line of a sale. The book data is denormalized on purpose, exactly as PedidoItem does it:
 * cg_catalogo is deleted and rebuilt wholesale on every catalog import, so a foreign key would
 * either block the import or erase sale history. precio is the price actually charged, captured
 * at the moment of sale -- a later reprice must not rewrite what the customer paid.
 */
@Entity
@Table(name = "vi_venta_item")
public class VentaItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "vi_venta_item_k")
	private Integer id;

	/** @JsonIgnore breaks the cycle that would otherwise recurse when serializing a Venta. */
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vi_venta_ve", updatable = false)
	private Venta venta;

	@Column(name = "vi_cantidad")
	private Integer cantidad;

	@Column(name = "vi_nombre_libro")
	private String libro;

	@Column(name = "vi_autor")
	private String autor;

	@Column(name = "vi_editorial")
	private String editorial;

	@Column(name = "vi_isbn")
	private String isbn;

	@Column(name = "vi_precio")
	private Double precio;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Venta getVenta() {
		return venta;
	}

	public void setVenta(Venta venta) {
		this.venta = venta;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	public String getLibro() {
		return libro;
	}

	public void setLibro(String libro) {
		this.libro = libro;
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

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public Double getPrecio() {
		return precio;
	}

	public void setPrecio(Double precio) {
		this.precio = precio;
	}
}
