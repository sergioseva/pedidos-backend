package com.librosmario.pedidos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
