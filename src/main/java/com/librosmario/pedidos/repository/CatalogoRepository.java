package com.librosmario.pedidos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.librosmario.pedidos.entity.Catalogo;

@RepositoryRestResource(path="catalogos")
@CrossOrigin(origins = "*")
public interface CatalogoRepository extends JpaRepository<Catalogo, Integer>,JpaSpecificationExecutor<Catalogo> {

	/**
	 * Exact match on the indexed column: this is the barcode hot path.
	 * Returns a List rather than an Optional because the catalog may hold a duplicate ISBN, and
	 * an Optional would turn that into a NonUniqueResultException -- a 500 in front of a customer.
	 */
	List<Catalogo> findByIsbn(String isbn);

	/**
	 * Fallback for catalog rows that store the ISBN with separators ("978-1111111111") while the
	 * scanner emits bare digits. Stripping the column defeats ix_cg_isbn, so this runs only after
	 * findByIsbn misses -- one scan of ~92k rows, on a path that is rare by construction.
	 */
	@Query("select c from Catalogo c where replace(replace(replace(c.isbn, '-', ''), ' ', ''), '.', '') = :isbn")
	List<Catalogo> findByIsbnNormalizado(@Param("isbn") String isbn);
}
