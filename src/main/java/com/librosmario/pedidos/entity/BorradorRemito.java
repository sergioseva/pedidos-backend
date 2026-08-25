package com.librosmario.pedidos.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

/**
 * Remito a medio cargar, guardado del lado del servidor para que no dependa del navegador.
 *
 * Vive en su propia tabla y no como un Remito en estado borrador a proposito: un borrador que
 * compartiera tabla con los remitos de verdad tendria que excluirse en cada consulta, y olvidarse
 * en una sola contaminaria los saldos de consignacion.
 *
 * El contenido se guarda opaco, tal como lo manda la pantalla, para que agregar un campo a los
 * items no obligue a migrar borradores a medio cargar.
 */
@Entity
@Table(name = "br_borrador")
public class BorradorRemito {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "br_borrador_k")
	private Integer br_borrador_k;

	@Column(name = "br_usuario", length = 100)
	private String br_usuario;

	@Column(name = "br_tipo", length = 20)
	private String br_tipo;

	@Lob
	@Column(name = "br_contenido")
	private String br_contenido;

	@Column(name = "br_fecha")
	private Date br_fecha;

	public Integer getBr_borrador_k() {
		return br_borrador_k;
	}

	public void setBr_borrador_k(Integer br_borrador_k) {
		this.br_borrador_k = br_borrador_k;
	}

	public String getBr_usuario() {
		return br_usuario;
	}

	public void setBr_usuario(String br_usuario) {
		this.br_usuario = br_usuario;
	}

	public String getBr_tipo() {
		return br_tipo;
	}

	public void setBr_tipo(String br_tipo) {
		this.br_tipo = br_tipo;
	}

	public String getBr_contenido() {
		return br_contenido;
	}

	public void setBr_contenido(String br_contenido) {
		this.br_contenido = br_contenido;
	}

	public Date getBr_fecha() {
		return br_fecha;
	}

	public void setBr_fecha(Date br_fecha) {
		this.br_fecha = br_fecha;
	}
}
