package com.librosmario.pedidos.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="pd_pedido_a_distribuidora")
public class PedidoDistribuidora {
	 
	 @Id
	 @GeneratedValue(strategy=GenerationType.IDENTITY)
	 @Column(name="pd_pedido_a_distribuidora_k")
	 Integer id;
	 
	 @Column(name="pd_fecha")
	 LocalDateTime fecha;
	 
	 @ManyToOne
	 @JoinColumn(name="pd_distribuidora_ed")
	 Distribuidora distribuidora;
	 
	 @Column(name="pd_pedido_realizado")
	 boolean realizado;
	 
	 @ManyToOne(fetch = FetchType.EAGER)
	 @JoinColumn(name = "pd_pedido_item_pi")
	 private PedidoItem item;
	 

	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public LocalDateTime getFecha() {
		return fecha;
	}


	public void setFecha(LocalDateTime localDateTime) {
		this.fecha = localDateTime;
	}


	public Distribuidora getDistribuidora() {
		return distribuidora;
	}


	public void setDistribuidora(Distribuidora distribuidora) {
		this.distribuidora = distribuidora;
	}


	public boolean isRealizado() {
		return realizado;
	}


	public void setRealizado(boolean realizado) {
		this.realizado = realizado;
	}


	public PedidoItem getItem() {
		return item;
	}

	public void setItem(PedidoItem item) {
		this.item = item;
	}
	

	 
	 
}
