package es.iesguzman.demo.repository;

import es.iesguzman.demo.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByCategoriaNombre(String nombreCategoria);

    List<Producto> findByActivoTrue();

    boolean existsByNombre(String nombre);

    List<Producto> findByCategoriaId(Long categoriaId);

    List<Producto> findByNombreContainingAndActivoTrue(String nombre);
}
