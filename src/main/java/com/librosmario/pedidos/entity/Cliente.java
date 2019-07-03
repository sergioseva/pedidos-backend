package com.librosmario.pedidos.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="cl_cliente")
public class Cliente {
	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 @Column(name="cl_cliente_k")
	 int id;
	 @Column(name="cl_nombre")
	 String nombre;
	 @Column(name="cl_direccion")
	 String direccion;
	 @Column(name="cl_telefono_fijo")
	 String telefonoFijo;
	 @NotNull
	 @Column(name="cl_telefono_movil")
	 String telefonoMovil;
	 @Column(name="cl_telefono_laboral")
	 String telefonoLaboral;
	 @Column(name="cl_telefono_otro")
	 String telefonoOtro;
	 @Column(name="cl_telefono_otro_descr")
	 String telefonoOtroDescr;
	 @Email
	 @Column(name="cl_email")
	 String email;
	 
	public Cliente() {
		
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getTelefonoFijo() {
		return telefonoFijo;
	}

	public void setTelefonoFijo(String telefonoFijo) {
		this.telefonoFijo = telefonoFijo;
	}

	public String getTelefonoMovil() {
		return telefonoMovil;
	}

	public void setTelefonoMovil(String telefonoMovil) {
		this.telefonoMovil = telefonoMovil;
	}

	public String getTelefonoLaboral() {
		return telefonoLaboral;
	}

	public void setTelefonoLaboral(String telefonoLaboral) {
		this.telefonoLaboral = telefonoLaboral;
	}

	public String getTelefonoOtro() {
		return telefonoOtro;
	}

	public void setTelefonoOtro(String telefonoOtro) {
		this.telefonoOtro = telefonoOtro;
	}

	public String getTelefonoOtroDescr() {
		return telefonoOtroDescr;
	}

	public void setTelefonoOtroDescr(String telefonoOtroDescr) {
		this.telefonoOtroDescr = telefonoOtroDescr;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	 

}
