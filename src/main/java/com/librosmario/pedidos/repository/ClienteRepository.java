package com.librosmario.pedidos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.librosmario.pedidos.entity.Catalogo;
import com.librosmario.pedidos.entity.Cliente;
import com.librosmario.pedidos.entity.projections.PedidoProjection;

@RepositoryRestResource
@CrossOrigin(origins = "http://localhost:4200")
public interface ClienteRepository extends JpaRepository<Cliente, Integer>,
										   JpaSpecificationExecutor<Cliente>{

	List<Cliente> findByTelefonoMovilContainsAllIgnoreCase(String thePhone);

	List<Cliente> findByNombreContainsAllIgnoreCase(String theFirstName);

	Boolean existsByTelefonoMovil(String telefonoMovil);

    Boolean existsByEmail(String email);
}
