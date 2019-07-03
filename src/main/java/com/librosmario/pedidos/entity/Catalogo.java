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
	Integer cg_catalogo_k;
	@Column(name="cg_codigo_luongo")
	Integer cg_codigo_luongo;
	@Column(name="cg_autor")
	String cg_autor;
	@Column(name="cg_descripcion")
	String cg_descripcion;
	@Column(name="cg_precio")
	Double cg_precio;
	@Column(name="cg_pedido")
	int cg_pedido;
	@Column(name="cg_vigente")
	Boolean cg_vigente;
	@Column(name="cg_editorial")
	String cg_editorial;
	@Column(name="cg_tema")
	String cg_tema;
	@Column(name="cg_isbn")
	String cg_isbn;
	@Column(name="cg_pst")
	int cg_pst;
	@Column(name="cg_observaciones")
	String cg_observaciones;
	@Column(name="cg_creador")
	String cg_creador;
	@Column(name="cg_inputdate")
	Date cg_inputdate;
	
	public Catalogo() {

	}

	public Integer getCg_catalogo_k() {
		return cg_catalogo_k;
	}

	public void setCg_catalogo_k(Integer cg_catalogo_k) {
		this.cg_catalogo_k = cg_catalogo_k;
	}

	public Integer getCg_codigo_luongo() {
		return cg_codigo_luongo;
	}

	public void setCg_codigo_luongo(Integer cg_codigo_luongo) {
		this.cg_codigo_luongo = cg_codigo_luongo;
	}

	public String getCg_autor() {
		return cg_autor;
	}

	public void setCg_autor(String cg_autor) {
		this.cg_autor = cg_autor;
	}

	public String getCg_descripcion() {
		return cg_descripcion;
	}

	public void setCg_descripcion(String cg_descripcion) {
		this.cg_descripcion = cg_descripcion;
	}

	public Double getCg_precio() {
		return cg_precio;
	}

	public void setCg_precio(Double cg_precio) {
		this.cg_precio = cg_precio;
	}

	public int getCg_pedido() {
		return cg_pedido;
	}

	public void setCg_pedido(int cg_pedido) {
		this.cg_pedido = cg_pedido;
	}

	public Boolean getCg_vigente() {
		return cg_vigente;
	}

	public void setCg_vigente(Boolean cg_vigente) {
		this.cg_vigente = cg_vigente;
	}

	public String getCg_editorial() {
		return cg_editorial;
	}

	public void setCg_editorial(String cg_editorial) {
		this.cg_editorial = cg_editorial;
	}

	public String getCg_tema() {
		return cg_tema;
	}

	public void setCg_tema(String cg_tema) {
		this.cg_tema = cg_tema;
	}

	public String getCg_isbn() {
		return cg_isbn;
	}

	public void setCg_isbn(String cg_isbn) {
		this.cg_isbn = cg_isbn;
	}

	public int getCg_pst() {
		return cg_pst;
	}

	public void setCg_pst(int cg_pst) {
		this.cg_pst = cg_pst;
	}

	public String getCg_observaciones() {
		return cg_observaciones;
	}

	public void setCg_observaciones(String cg_observaciones) {
		this.cg_observaciones = cg_observaciones;
	}

	public String getCg_creador() {
		return cg_creador;
	}

	public void setCg_creador(String cg_creador) {
		this.cg_creador = cg_creador;
	}

	public Date getCg_inputdate() {
		return cg_inputdate;
	}

	public void setCg_inputdate(Date cg_inputdate) {
		this.cg_inputdate = cg_inputdate;
	}
	

}
