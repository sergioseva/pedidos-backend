package com.librosmario.pedidos.dao;

import org.springframework.data.repository.CrudRepository;

import com.librosmario.pedidos.entity.PedidoItem;

public interface PedidoItemRepository extends CrudRepository<PedidoItem, Integer> {

}
