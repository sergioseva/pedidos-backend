package com.librosmario.pedidos.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ed_editorial")
public class Editorial {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name="ed_editorial_k")
  private int ed_editorial_k;
  
  @Column(name="ed_descripcion")
  private String ed_descripcion;
  
public Editorial() {
	
}
  
public int getEd_editorial_k() {
	return ed_editorial_k;
}
public void setEd_editorial_k(int ed_editorial_k) {
	this.ed_editorial_k = ed_editorial_k;
}
public String getEd_descripcion() {
	return ed_descripcion;
}
public void setEd_descripcion(String ed_descripcion) {
	this.ed_descripcion = ed_descripcion;
}
}
