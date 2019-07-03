package com.librosmario.pedidos.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	 Editorial re_distribuidora_ed;	
	 
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
	public Editorial getRe_distribuidora_ed() {
		return re_distribuidora_ed;
	}
	public void setRe_distribuidora_ed(Editorial re_distribuidora_ed) {
		this.re_distribuidora_ed = re_distribuidora_ed;
	} 

}
