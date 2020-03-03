package com.librosmario.pedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.librosmario.pedidos.entity.Distribuidora;
import com.librosmario.pedidos.entity.PedidoDistribuidora;

@RepositoryRestResource(path="pedidosdistribuidora")
public interface PedidoDistribuidoraRepository extends JpaRepository<PedidoDistribuidora, Integer> {

}
