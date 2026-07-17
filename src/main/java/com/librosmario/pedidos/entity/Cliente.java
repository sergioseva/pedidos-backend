package com.librosmario.pedidos.entity;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="cl_cliente")
@EntityListeners(AuditingEntityListener.class)
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

	 // Populated automatically by JPA auditing. createdAt is updatable=false so an update -- including
	 // a Spring Data REST PUT that carries a null -- can never wipe the original creation time.
	 @CreatedDate
	 @Column(name="cl_created_at", updatable=false)
	 Instant createdAt;
	 @LastModifiedDate
	 @Column(name="cl_updated_at")
	 Instant updatedAt;

	public Cliente() {

	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
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
