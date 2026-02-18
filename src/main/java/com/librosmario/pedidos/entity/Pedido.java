package com.librosmario.pedidos.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="pe_pedido")
public class Pedido {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="pe_pedido_k")
    private int id;
	
	@ManyToOne()
	@JoinColumn(name="pe_cliente_cl")
	//@RestResource(path = "libraryAddress", rel="address")
    private Cliente cliente;
	
	@Column(name="pe_fecha")
    private LocalDateTime fecha=LocalDateTime.now();
	@Column(name="pe_senia")
    private Double senia;
	@Column(name="pe_total")
    private Double total;
	@Column(name="pe_adomicilio")
    private boolean  adomicilio;
	@Column(name="pe_domicilio")
    private String domicilio;
	@Column(name="pe_fecha_envio")
    private LocalDateTime fechaEnvio;
	@Column(name="pe_observaciones")
    private String observaciones;
	
	
	@OneToMany(mappedBy = "pedido",cascade = CascadeType.ALL)
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


	public LocalDateTime getFecha() {
		return fecha;
	}


	public void setFecha(LocalDateTime fecha) {
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


	public LocalDateTime getFechaEnvio() {
		return fechaEnvio;
	}


	public void setFechaEnvio(LocalDateTime localDateTime) {
		this.fechaEnvio = localDateTime;
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
