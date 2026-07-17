package com.librosmario.pedidos.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * A counter sale: one ticket, many lines. Independent of Pedido -- an order is a special request
 * for a book the shop does not have, a Venta is a book leaving the shelf right now.
 *
 * fecha, total and usuario are all set by the server in VentaService and ignored on input: this
 * record exists to be accountable, so nothing about the money may come from the browser.
 */
@Entity
@Table(name = "ve_venta", indexes = @Index(name = "ix_ve_fecha", columnList = "ve_fecha"))
public class Venta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ve_venta_k")
	private Integer id;

	/** Optional: most counter sales are anonymous. */
	@ManyToOne
	@JoinColumn(name = "ve_cliente_cl")
	private Cliente cliente;

	@Column(name = "ve_fecha")
	private LocalDateTime fecha;

	@Column(name = "ve_total")
	private Double total;

	@Column(name = "ve_observaciones")
	private String observaciones;

	/**
	 * Who rang the sale up, denormalized so it survives the user being deleted. Not surfaced in the
	 * UI yet, but recorded from the start: it costs nothing now and cannot be reconstructed later.
	 */
	@Column(name = "ve_usuario")
	private String usuario;

	@OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<VentaItem> items = new ArrayList<>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public LocalDateTime getFecha() {
		return fecha;
	}

	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public List<VentaItem> getItems() {
		return items;
	}

	public void setItems(List<VentaItem> items) {
		this.items = items;
	}
}
