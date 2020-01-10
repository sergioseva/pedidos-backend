package com.librosmario.pedidos.entity.projections;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.rest.core.config.Projection;

import com.librosmario.pedidos.entity.Cliente;
import com.librosmario.pedidos.entity.Pedido;
import com.librosmario.pedidos.entity.PedidoItem;

@Projection(name="pedidoCliente",types=Pedido.class)
public interface PedidoProjection {
	LocalDateTime getFecha();
	Double getSenia();
	Double getTotal();
	String getObservaciones();
	List<PedidoItem> getPedidoItems();
	Cliente getCliente();
}
