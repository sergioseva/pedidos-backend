package com.librosmario.pedidos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.librosmario.pedidos.entity.PedidoItem;

@RepositoryRestResource(path="librospedidos")
public interface PedidoItemRepository extends JpaRepository<PedidoItem, Integer> {
	
	List<PedidoItem> findByPendienteTrueOrderByLibro();
	List<PedidoItem> findByPedidoId(Integer pedidoId);

}
