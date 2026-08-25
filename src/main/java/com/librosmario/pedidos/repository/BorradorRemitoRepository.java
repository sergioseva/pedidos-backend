package com.librosmario.pedidos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.librosmario.pedidos.entity.BorradorRemito;

@RepositoryRestResource(exported = false)
public interface BorradorRemitoRepository extends JpaRepository<BorradorRemito, Integer> {

	/** Consulta explicita: los guiones bajos rompen el parseo de los metodos derivados. */
	@Query("SELECT b FROM BorradorRemito b WHERE b.br_usuario = :usuario AND b.br_tipo = :tipo")
	Optional<BorradorRemito> findByUsuarioYTipo(@Param("usuario") String usuario, @Param("tipo") String tipo);
}
