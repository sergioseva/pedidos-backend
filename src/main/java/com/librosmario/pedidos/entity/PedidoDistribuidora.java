package com.librosmario.pedidos.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
	 
	  @ManyToMany(cascade = {})
	  @JoinTable(
				name="pdpi_pedido_distribuidora_item",
				inverseJoinColumns=@JoinColumn(name="pdpi_pedido_item_pi"),
				joinColumns=@JoinColumn(name="pdpi_pedido_a_distribuidora_pd")
				)	
	 private List<PedidoItem> items; 
	 
	  
	 public void addPedidoItem(PedidoItem pi) {
		 items.add(pi);
	 }
	 

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


	public void setItems(List<PedidoItem> items) {
		this.items = items;
	}


	public List<PedidoItem> getItems() {
		return items;
	}
	

	 
	 
}
