package com.librosmario.pedidos.payload;

import java.time.LocalDateTime;
import java.util.List;

import com.librosmario.pedidos.entity.Cliente;
import com.librosmario.pedidos.entity.Pedido;
import com.librosmario.pedidos.entity.PedidoItem;

public class PedidoDTO {

	private double senia;
	private double total;
	private boolean adomicilio;
	private String domicilio;
	private LocalDateTime fechaEnvio;
	private String observaciones;
	private List<PedidoItem> pedidoItems;
	private Cliente cliente;
	
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	public double getSenia() {
		return senia;
	}
	public void setSenia(double senia) {
		this.senia = senia;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
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
	public void setFechaEnvio(LocalDateTime fechaEnvio) {
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
	
	public Pedido toPedido() {
		Pedido p=new Pedido();
		p.setAdomicilio(this.isAdomicilio());
		p.setCliente(this.getCliente());
		p.setDomicilio(getDomicilio());
		p.setFechaEnvio(getFechaEnvio());
		p.setObservaciones(getObservaciones());
		p.setPedidoItems(getPedidoItems());
		p.setSenia(getSenia());
		p.setTotal(getTotal());
		return p;
	}
	
}
