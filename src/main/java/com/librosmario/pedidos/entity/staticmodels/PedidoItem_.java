package com.librosmario.pedidos.entity.staticmodels;

import java.time.LocalDateTime;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;

import com.librosmario.pedidos.entity.Distribuidora;
import com.librosmario.pedidos.entity.Pedido;
import com.librosmario.pedidos.entity.PedidoDistribuidora;
import com.librosmario.pedidos.entity.PedidoItem;

@javax.persistence.metamodel.StaticMetamodel(PedidoItem.class)

public class PedidoItem_ {

	public static volatile SingularAttribute<PedidoItem,Integer> id;
	public static volatile SingularAttribute<PedidoItem,Pedido> pedido;
	public static volatile SingularAttribute<PedidoItem,Integer> cantidad;
	public static volatile SingularAttribute<PedidoItem,String> libro;
	public static volatile SingularAttribute<PedidoItem,String> autor;
	public static volatile SingularAttribute<PedidoItem,String> editorial;
	public static volatile SingularAttribute<PedidoItem,String> isbn;
	public static volatile SingularAttribute<PedidoItem,Double> precio;
	public static volatile SingularAttribute<PedidoItem,Distribuidora> pedidoAeditorial;
	public static volatile SingularAttribute<PedidoItem,Boolean> pendiente;
	public static volatile SingularAttribute<PedidoItem,Boolean> enSucursal;
	public static volatile SingularAttribute<PedidoItem,Boolean> retirado;
	public static volatile SingularAttribute<PedidoItem,LocalDateTime> fechaRetiro;
	public static volatile ListAttribute<PedidoItem,PedidoDistribuidora> pedidosADistribuidoras;

}
