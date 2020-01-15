package com.librosmario.pedidos.entity.staticmodels;

import java.time.LocalDateTime;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;

import com.librosmario.pedidos.entity.Cliente;
import com.librosmario.pedidos.entity.Pedido;
import com.librosmario.pedidos.entity.PedidoItem;

@javax.persistence.metamodel.StaticMetamodel(Pedido.class)
//https://developer.ibm.com/articles/j-typesafejpa/#N102F2
public class Pedido_ {
	
	public static volatile SingularAttribute<Pedido,Integer> id;
	public static volatile SingularAttribute<Pedido,Cliente> cliente;
	public static volatile SingularAttribute<Pedido,LocalDateTime> fecha;
	public static volatile SingularAttribute<Pedido,Double> senia;
	public static volatile SingularAttribute<Pedido,Double> total;
	public static volatile SingularAttribute<Pedido,Boolean> adomicilio;
	public static volatile SingularAttribute<Pedido,String> domicilio;
	public static volatile SingularAttribute<Pedido,LocalDateTime> fechaEnvio;
	public static volatile SingularAttribute<Pedido,String> observaciones;
	public static volatile ListAttribute<Pedido,PedidoItem> pedidoItems;
	
}
