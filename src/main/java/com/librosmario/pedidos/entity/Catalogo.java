package com.librosmario.pedidos.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
/**
 * The index on cg_isbn serves the barcode lookup at the till: the catalog holds ~92k rows and a
 * scan must resolve instantly. It is deliberately NOT unique -- the catalog is third-party data
 * and a duplicate ISBN would otherwise fail the import rather than just look odd.
 */
@Entity
@Table(name="cg_catalogo", indexes = @Index(name="ix_cg_isbn", columnList="cg_isbn"))
public class Catalogo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="cg_catalogo_k")
	Integer id;

	@Column(name="cg_codigo_luongo")
	String codigoLuongo;
	@Column(name="cg_autor")
	String autor;
	@Column(name="cg_descripcion")
	String descripcion;
	@Column(name="cg_precio")
	Double precio;
	@Column(name="cg_editorial")
	String editorial;


	@Column(name="cg_tema")
	String tema;
	@Column(name="cg_isbn")
	String isbn;
	@Column(name="cg_observaciones")
	String observaciones;
	@Column(name="cg_creador")
	String creador;
	@Column(name="cg_inputdate")
	Date inputdate;
	
	public Catalogo() {

	}
	public Catalogo(String codigoLuongo, String autor, String descripcion, Double precio, String editorial,
			String tema, String isbn, String observaciones) {
		this.codigoLuongo = codigoLuongo;
		this.autor = autor;
		this.descripcion = descripcion;
		this.precio = precio;
		this.editorial = editorial;
		this.tema = tema;
		this.isbn = isbn;
		this.observaciones = observaciones;
	}
	

	@Override
	public String toString() {
		return "Catalogo [codigoLuongo=" + codigoLuongo + ", autor=" + autor + ", descripcion=" + descripcion
				+ ", precio=" + precio + ", editorial=" + editorial + ", tema=" + tema + ", isbn=" + isbn
				+ ", observaciones=" + observaciones + "]";
	}
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCodigoLuongo() {
		return codigoLuongo;
	}

	public void setCodigoLuongo(String codigoLuongo) {
		this.codigoLuongo = codigoLuongo;
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Double getPrecio() {
		return precio;
	}

	public void setPrecio(Double precio) {
		this.precio = precio;
	}

	public String getEditorial() {
		return editorial;
	}

	public void setEditorial(String editorial) {
		this.editorial = editorial;
	}

	public String getTema() {
		return tema;
	}

	public void setTema(String tema) {
		this.tema = tema;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public String getCreador() {
		return creador;
	}

	public void setCreador(String creador) {
		this.creador = creador;
	}

	public Date getInputdate() {
		return inputdate;
	}

	public void setInputdate(Date inputdate) {
		this.inputdate = inputdate;
	}

	
	

}
