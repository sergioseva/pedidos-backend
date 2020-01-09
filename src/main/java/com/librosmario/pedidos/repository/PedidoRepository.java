package com.librosmario.pedidos.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.librosmario.pedidos.entity.Pedido;
import com.librosmario.pedidos.entity.projections.PedidoProjection;

@RepositoryRestResource(path="pedidos",excerptProjection=PedidoProjection.class)
@CrossOrigin(origins = "http://localhost:4200")
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {


	@Query("SELECT DISTINCT p FROM " + 
			 "Pedido p INNER JOIN p.pedidoItems b WHERE b.libro LIKE CONCAT('%',:libro,'%')")
	List<Pedido> findPedidosConLibro(@Param("libro") String libro);
	

	List<Pedido> findByClienteNombreContainsAllIgnoreCase(@Param("cliente") String cliente);
	
	@Query("SELECT DISTINCT p FROM " + 
		   "Pedido p INNER JOIN p.pedidoItems b "
		 + "WHERE p.cliente.nombre LIKE CONCAT('%',:cliente,'%') AND b.libro LIKE CONCAT('%',:libro,'%')")
	List<Pedido> findByClienteAndLibro(@Param("libro") String libro,@Param("cliente") String cliente);
}

