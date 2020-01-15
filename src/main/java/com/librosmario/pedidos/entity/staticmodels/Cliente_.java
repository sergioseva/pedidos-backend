package com.librosmario.pedidos.entity.staticmodels;

import javax.persistence.metamodel.SingularAttribute;

import com.librosmario.pedidos.entity.Cliente;

@javax.persistence.metamodel.StaticMetamodel(Cliente.class)

public class Cliente_ {
	public static volatile SingularAttribute<Cliente,Integer> id;
	public static volatile SingularAttribute<Cliente,String> nombre;
	public static volatile SingularAttribute<Cliente,String> direccion;
	public static volatile SingularAttribute<Cliente,String> telefonoFijo;
	public static volatile SingularAttribute<Cliente,String> telefonoMovil;
	public static volatile SingularAttribute<Cliente,String> telefonoLaboral;
	public static volatile SingularAttribute<Cliente,String> telefonoOtro;
	public static volatile SingularAttribute<Cliente,String> telefonoOtroDescr;
	public static volatile SingularAttribute<Cliente,String> email;
}
