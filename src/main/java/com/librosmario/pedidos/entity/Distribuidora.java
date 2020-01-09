package com.librosmario.pedidos.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ed_editorial")
public class Distribuidora {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name="ed_editorial_k")
  private int id;
  
  @Column(name="ed_descripcion")
  private String descripcion;
  
public Distribuidora() {
	
}
  

public int getId() {
	return id;
}


public void setId(int id) {
	this.id = id;
}


public String getDescripcion() {
	return descripcion;
}
public void setDescripcion(String ed_descripcion) {
	this.descripcion = ed_descripcion;
}
}
