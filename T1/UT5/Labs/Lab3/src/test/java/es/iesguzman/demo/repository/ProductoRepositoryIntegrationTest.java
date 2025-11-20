package es.iesguzman.demo.repository;

import es.iesguzman.demo.model.Producto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql(scripts = "/data.sql")
public class ProductoRepositoryIntegrationTest {

    @Autowired
    private ProductoRepository productoRepository;

    @Test
    void testFindAll() {
        List<Producto> productos = productoRepository.findAll();
        assertFalse(productos.isEmpty());
    }

    @Test
    void testFindById() {
        Optional<Producto> producto = productoRepository.findById(1L);
        assertTrue(producto.isPresent());
    }

    @Test
    void testFindByCategoria() {
        List<Producto> productos = productoRepository.findByCategoria("Categoría 1");
        assertFalse(productos.isEmpty());
    }

    @Test
    void testFindByNombreContainingIgnoreCase() {
        List<Producto> productos = productoRepository.findByNombreContainingIgnoreCase("demo");
        assertFalse(productos.isEmpty());
    }

    @Test
    void testSave() {
        Producto producto = new Producto();
        producto.setNombre("Producto Nuevo");
        producto.setPrecio(50.0);
        producto.setStock(5);

        Producto saved = productoRepository.save(producto);
        assertNotNull(saved.getId());
    }

    @Test
    void testDelete() {
        productoRepository.deleteById(1L);
        Optional<Producto> producto = productoRepository.findById(1L);
        assertFalse(producto.isPresent());
    }
}
