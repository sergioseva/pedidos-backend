package com.librosmario.pedidos.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="re_remito")
public class Remito {
	
	 @Id
	 @GeneratedValue(strategy=GenerationType.IDENTITY)
	 @Column(name="re_remito_k")
	 Integer re_remito_k;
	 
	 @Column(name="re_fecha")
	 Date re_fecha;
	 
	 @ManyToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE,
			 CascadeType.DETACH, CascadeType.REFRESH})
	 @JoinColumn(name="re_distribuidora_ed")
	 Distribuidora re_distribuidora_ed;	
	 
	 @Column(name="re_observaciones")
	 String re_observaciones;
	 

	@OneToMany(cascade= {CascadeType.PERSIST, CascadeType.MERGE,
			 CascadeType.DETACH, CascadeType.REFRESH})
	@JoinColumn(name="ri_remito_re")
	private List<RemitoItem> items;
		
	 public String getRe_observaciones() {
		return re_observaciones;
	}
	public void setRe_observaciones(String re_observaciones) {
		this.re_observaciones = re_observaciones;
	}

	 
	public List<RemitoItem> getItems() {
		return items;
	}
	public void setItems(ArrayList<RemitoItem> items) {
		this.items = items;
	}
	public Integer getRe_remito_k() {
		return re_remito_k;
	}
	public void setRe_remito_k(Integer re_remito_k) {
		this.re_remito_k = re_remito_k;
	}
	public Date getRe_fecha() {
		return re_fecha;
	}
	public void setRe_fecha(Date re_fecha) {
		this.re_fecha = re_fecha;
	}
	public Distribuidora getRe_distribuidora_ed() {
		return re_distribuidora_ed;
	}
	public void setRe_distribuidora_ed(Distribuidora re_distribuidora_ed) {
		this.re_distribuidora_ed = re_distribuidora_ed;
	} 

}
