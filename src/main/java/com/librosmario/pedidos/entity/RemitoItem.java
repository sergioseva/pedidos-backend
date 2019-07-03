package com.librosmario.pedidos.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="ri_remito_item")
public class RemitoItem {
	
	 @Id
	 @GeneratedValue(strategy=GenerationType.IDENTITY)
	 @Column(name="ri_remito_item_k")
	 private Integer ri_remito_item_k;
	 
	 @JsonIgnore
	 @ManyToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE,
			 CascadeType.DETACH, CascadeType.REFRESH})
	 @JoinColumn(name="re_remito_k")
	 private Remito ri_remito_re;
	 
	 @JsonIgnore
	 @ManyToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE,
			 CascadeType.DETACH, CascadeType.REFRESH})
	 @JoinColumn(name="ri_catalogo_cg")
	 private Catalogo ri_catalogo_cg;
	 
	 @Column(name="ri_cantidad")
	 private Integer ri_cantidad;
	 
	 @Column(name="ri_nombre_libro")
	 private String ri_nombre_libro;
	 
	 @Column(name="ri_autor")
	 private String ri_autor;
	 
	 @Column(name="ri_editorial")
	 private String ri_editorial;
	 
	 @Column(name="ri_isbn")
	 private String ri_isbn;
	 
	 @Column(name="ri_precio")
	 private Double ri_precio;
	 
	 @Column(name="ri_factura")
	 private String ri_factura;
	 
	 @Column(name="ri_motivo")
	 private String ri_motivo;
	 

	public Integer getRi_remito_item_k() {
		return ri_remito_item_k;
	}
	public void setRi_remito_item_k(Integer ri_remito_item_k) {
		this.ri_remito_item_k = ri_remito_item_k;
	}
	public Remito getRi_remito_re() {
		return ri_remito_re;
	}
	public void setRi_remito_re(Remito ri_remito_re) {
		this.ri_remito_re = ri_remito_re;
	}
	public Catalogo getRi_catalogo_cg() {
		return ri_catalogo_cg;
	}
	public void setRi_catalogo_cg(Catalogo ri_catalogo_cg) {
		this.ri_catalogo_cg = ri_catalogo_cg;
	}
	public Integer getRi_cantidad() {
		return ri_cantidad;
	}
	public void setRi_cantidad(Integer ri_cantidad) {
		this.ri_cantidad = ri_cantidad;
	}
	public String getRi_nombre_libro() {
		return ri_nombre_libro;
	}
	public void setRi_nombre_libro(String ri_nombre_libro) {
		this.ri_nombre_libro = ri_nombre_libro;
	}
	public String getRi_autor() {
		return ri_autor;
	}
	public void setRi_autor(String ri_autor) {
		this.ri_autor = ri_autor;
	}
	public String getRi_editorial() {
		return ri_editorial;
	}
	public void setRi_editorial(String ri_editorial) {
		this.ri_editorial = ri_editorial;
	}
	public String getRi_isbn() {
		return ri_isbn;
	}
	public void setRi_isbn(String ri_isbn) {
		this.ri_isbn = ri_isbn;
	}
	public Double getRi_precio() {
		return ri_precio;
	}
	public void setRi_precio(Double ri_precio) {
		this.ri_precio = ri_precio;
	}
	public String getRi_factura() {
		return ri_factura;
	}
	public void setRi_factura(String ri_factura) {
		this.ri_factura = ri_factura;
	}
	public String getRi_motivo() {
		return ri_motivo;
	}
	public void setRi_motivo(String ri_motivo) {
		this.ri_motivo = ri_motivo;
	}
	
	  
}
