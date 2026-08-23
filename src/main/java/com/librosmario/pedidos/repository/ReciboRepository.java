package com.librosmario.pedidos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.librosmario.pedidos.entity.Recibo;

@RepositoryRestResource(exported = false)
public interface ReciboRepository extends JpaRepository<Recibo, Integer> {

	/**
	 * Consulta explicita en vez de un metodo derivado: los guiones bajos de los nombres de campo
	 * hacen que Spring Data no pueda resolver el path de la propiedad.
	 */
	@Query("SELECT r FROM Recibo r WHERE r.rc_remito_re.re_remito_k = :remitoId")
	Optional<Recibo> findByRemitoId(@Param("remitoId") Integer remitoId);
}
