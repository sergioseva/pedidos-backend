package com.librosmario.pedidos.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Constancia de que el comercio pago un remito de venta de consignacion.
 *
 * Es opcional: el remito de venta se emite igual y el recibo se agrega cuando el comercio paga,
 * que puede ser en el momento o mas tarde. Un remito tiene a lo sumo un recibo -- no hay pagos
 * parciales, un remito esta pago o no lo esta.
 */
@Entity
@Table(name="rc_recibo")
public class Recibo {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="rc_recibo_k")
	private Integer rc_recibo_k;

	@JsonIgnore
	@OneToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.DETACH, CascadeType.REFRESH})
	@JoinColumn(name="rc_remito_re")
	private Remito rc_remito_re;

	@Column(name="rc_fecha")
	private Date rc_fecha;

	/** Monto cobrado, congelado igual que la comision: es lo que dice el papel que se firmo. */
	@Column(name="rc_monto")
	private Double rc_monto;

	@Column(name="rc_medio_pago", length=40)
	private String rc_medio_pago;

	@Column(name="rc_observaciones")
	private String rc_observaciones;

	public Integer getRc_recibo_k() {
		return rc_recibo_k;
	}

	public void setRc_recibo_k(Integer rc_recibo_k) {
		this.rc_recibo_k = rc_recibo_k;
	}

	public Remito getRc_remito_re() {
		return rc_remito_re;
	}

	public void setRc_remito_re(Remito rc_remito_re) {
		this.rc_remito_re = rc_remito_re;
	}

	public Date getRc_fecha() {
		return rc_fecha;
	}

	public void setRc_fecha(Date rc_fecha) {
		this.rc_fecha = rc_fecha;
	}

	public Double getRc_monto() {
		return rc_monto;
	}

	public void setRc_monto(Double rc_monto) {
		this.rc_monto = rc_monto;
	}

	public String getRc_medio_pago() {
		return rc_medio_pago;
	}

	public void setRc_medio_pago(String rc_medio_pago) {
		this.rc_medio_pago = rc_medio_pago;
	}

	public String getRc_observaciones() {
		return rc_observaciones;
	}

	public void setRc_observaciones(String rc_observaciones) {
		this.rc_observaciones = rc_observaciones;
	}
}
