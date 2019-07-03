package com.librosmario.pedidos.entity;

import java.util.ArrayList;
import java.util.Date;
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
public class PedidoADistribuidora {
	 
	 @Id
	 @GeneratedValue(strategy=GenerationType.IDENTITY)
	 @Column(name="pd_pedido_a_distribuidora_k")
	 Integer pd_pedido_a_distribuidora_k;
	 
	 @Column(name="pd_fecha")
	 Date pd_fecha;
	 
	 @ManyToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE,
				 CascadeType.DETACH, CascadeType.REFRESH})
	 @JoinColumn(name="pd_distribuidora_ed")
	 Editorial pd_distribuidora_ed;
	 
	 @Column(name="pd_pedido_realizado")
	 boolean pd_pedido_realizado;
	 
	  @ManyToMany(fetch=FetchType.LAZY,
				cascade= {CascadeType.PERSIST, CascadeType.MERGE,
				 CascadeType.DETACH, CascadeType.REFRESH})
	  @JoinTable(
				name="pdpi_pedido_distribuidora_item",
				inverseJoinColumns=@JoinColumn(name="pdpi_pedido_item_pi"),
				joinColumns=@JoinColumn(name="pdpi_pedido_a_distribuidora_pd")
				)	
	 private List<PedidoItem> items; 
	 
	  
	 public void addPedidoItem(PedidoItem pi) {
		 items.add(pi);
	 }
	 
	 public boolean getPd_pedido_realizado() {
		return pd_pedido_realizado;
	}
	public void setPd_pedido_realizado(boolean pd_pedido_realizado) {
		this.pd_pedido_realizado = pd_pedido_realizado;
	}
	
	public Integer getpd_pedido_a_distribuidora_k() {
		return pd_pedido_a_distribuidora_k;
	}
	public void setpd_pedido_a_distribuidora_k(Integer pd_pedido_a_distribuidora_k) {
		this.pd_pedido_a_distribuidora_k = pd_pedido_a_distribuidora_k;
	}
	public Date getPd_fecha() {
		return pd_fecha;
	}
	public void setPd_fecha(Date pd_fecha) {
		this.pd_fecha = pd_fecha;
	}
	public Editorial getpd_distribuidora_ed() {
		return pd_distribuidora_ed;
	}
	public void setpd_distribuidora_ed(Editorial pd_distribuidora_ed) {
		this.pd_distribuidora_ed = pd_distribuidora_ed;
	}
	public List<PedidoItem> getItems() {
		return items;
	}
	public void setItems(ArrayList<PedidoItem> list) {
		this.items = list;
	}
	 
	 
}
