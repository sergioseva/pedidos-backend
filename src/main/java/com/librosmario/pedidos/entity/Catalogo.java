package com.librosmario.pedidos.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name="cg_catalogo")
public class Catalogo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="cg_catalogo_k")
	Integer id;
	@Column(name="cg_codigo_luongo")
	Integer codigoLuongo;
	@Column(name="cg_autor")
	String autor;
	@Column(name="cg_descripcion")
	String descripcion;
	@Column(name="cg_precio")
	Double precio;
	@Column(name="cg_pedido")
	int pedido;
	@Column(name="cg_vigente")
	Boolean vigente;
	@Column(name="cg_editorial")
	String editorial;
	@Column(name="cg_tema")
	String tema;
	@Column(name="cg_isbn")
	String isbn;
	@Column(name="cg_pst")
	int pst;
	@Column(name="cg_observaciones")
	String observaciones;
	@Column(name="cg_creador")
	String creador;
	@Column(name="cg_inputdate")
	Date inputdate;
	
	public Catalogo() {

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCodigoLuongo() {
		return codigoLuongo;
	}

	public void setCodigoLuongo(Integer codigoLuongo) {
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

	public int getPedido() {
		return pedido;
	}

	public void setPedido(int pedido) {
		this.pedido = pedido;
	}

	public Boolean getVigente() {
		return vigente;
	}

	public void setVigente(Boolean vigente) {
		this.vigente = vigente;
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

	public int getPst() {
		return pst;
	}

	public void setPst(int pst) {
		this.pst = pst;
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
