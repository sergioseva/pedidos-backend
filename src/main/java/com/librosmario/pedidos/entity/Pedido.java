package com.librosmario.pedidos.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.sql.Date;

@Entity
@Table(name="pe_pedido")
public class Pedido {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="pe_pedido_k")
    private int id;
	
	@OneToOne( cascade= {CascadeType.PERSIST, CascadeType.MERGE,
						 CascadeType.DETACH, CascadeType.REFRESH})
	@JoinColumn(name="pe_cliente_cl")
    private Cliente cliente;
	
	@Column(name="pe_fecha")
    private Date fecha;
	@Column(name="pe_senia")
    private Double senia;
	@Column(name="pe_total")
    private Double total;
	@Column(name="pe_adomicilio")
    private boolean  adomicilio;
	@Column(name="pe_domicilio")
    private String domicilio;
	@Column(name="pe_fecha_envio")
    private Date fechaEnvio;
	@Column(name="pe_observaciones")
    private String observaciones;
	
	
	@OneToMany(mappedBy = "pedido",cascade= {CascadeType.PERSIST, CascadeType.MERGE,
			 CascadeType.DETACH, CascadeType.REFRESH})
    private List<PedidoItem> pedidoItems;


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public Cliente getCliente() {
		return cliente;
	}


	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}


	public Date getFecha() {
		return fecha;
	}


	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}


	public Double getSenia() {
		return senia;
	}


	public void setSenia(Double senia) {
		this.senia = senia;
	}


	public Double getTotal() {
		return total;
	}


	public void setTotal(Double total) {
		this.total = total;
	}


	public boolean isAdomicilio() {
		return adomicilio;
	}


	public void setAdomicilio(boolean adomicilio) {
		this.adomicilio = adomicilio;
	}


	public String getDomicilio() {
		return domicilio;
	}


	public void setDomicilio(String domicilio) {
		this.domicilio = domicilio;
	}


	public Date getFechaEnvio() {
		return fechaEnvio;
	}


	public void setFechaEnvio(Date fechaEnvio) {
		this.fechaEnvio = fechaEnvio;
	}


	public String getObservaciones() {
		return observaciones;
	}


	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}


	public List<PedidoItem> getPedidoItems() {
		return pedidoItems;
	}


	public void setPedidoItems(List<PedidoItem> pedidoItems) {
		this.pedidoItems = pedidoItems;
	}
    

    

    

    
   



    
}
