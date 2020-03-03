package com.librosmario.pedidos.payload;

import java.util.List;

import com.librosmario.pedidos.entity.Distribuidora;
import com.librosmario.pedidos.entity.PedidoItem;

public class ConfirmacionPedidoADistribuidoraDTO {

	List<PedidoItem> items;
	Distribuidora distribuidora;
	public List<PedidoItem> getItems() {
		return items;
	}
	public void setItems(List<PedidoItem> items) {
		this.items = items;
	}
	public Distribuidora getDistribuidora() {
		return distribuidora;
	}
	public void setDistribuidora(Distribuidora distribuidora) {
		this.distribuidora = distribuidora;
	}
	
	
}
