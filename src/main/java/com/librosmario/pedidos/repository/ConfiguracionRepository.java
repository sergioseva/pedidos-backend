package com.librosmario.pedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import com.librosmario.pedidos.entity.Configuracion;

@RepositoryRestResource(exported = false)
public interface ConfiguracionRepository extends JpaRepository<Configuracion, Integer> {
}
