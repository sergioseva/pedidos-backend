package com.librosmario.pedidos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Punto de venta al que la libreria entrega libros en consignacion: un hotel, un almacen,
 * un kiosco. Es el destinatario de un Remito de tipo CONSIGNACION, del mismo modo que la
 * Distribuidora lo es de uno de tipo DEVOLUCION.
 */
@Entity
@Table(name="cm_comercio")
public class Comercio {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="cm_comercio_k")
	private int id;

	@Column(name="cm_descripcion")
	private String descripcion;

	@Column(name="cm_direccion")
	private String direccion;

	@Column(name="cm_contacto")
	private String contacto;

	@Column(name="cm_telefono")
	private String telefono;

	@Column(name="cm_cuit")
	private String cuit;

	/**
	 * Porcentaje que el comercio retiene de cada venta (0-100). Al liquidar, el comercio paga
	 * precio de tapa menos esta comision. Se copia al remito de venta para que cambiarla despues
	 * no reescriba la plata de una liquidacion ya hecha.
	 */
	@Column(name="cm_comision")
	private Double comision;

	public Comercio() {

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

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getContacto() {
		return contacto;
	}

	public void setContacto(String contacto) {
		this.contacto = contacto;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public Double getComision() {
		return comision;
	}

	public void setComision(Double comision) {
		this.comision = comision;
	}
}
