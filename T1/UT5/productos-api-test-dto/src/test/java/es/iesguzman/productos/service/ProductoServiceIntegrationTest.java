package es.iesguzman.productos.service;

import es.iesguzman.productos.domain.Producto;
import es.iesguzman.productos.exception.ProductoNotFoundException;
import es.iesguzman.productos.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(properties = "spring.sql.init.mode=never")
class ProductoServiceIntegrationTest {

    @Autowired
    private ProductoService service;

    @Autowired
    private ProductoRepository repo;

    @Test
    void testFindByIdFound() {
        Producto p = new Producto(null, "Prod A", 10.0, 5, "cat");
        Producto saved = service.save(p);

        Producto result = service.findById(saved.getId());

        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(saved.getId(), result.getId()),
            () -> assertEquals("Prod A", result.getNombre())
        );
    }

    @Test
    void testFindByIdNotFound() {
        try {
            service.findById(999L);
            fail("Debería lanzar ProductoNotFoundException");
        } catch (ProductoNotFoundException e) {
            // OK
        }
    }

    @Test
    void testSave() {
        Producto input = new Producto(null, "Nueva", 15.0, 3, "cat");

        Producto saved = service.save(input);

        assertAll(
            () -> assertNotNull(saved.getId()),
            () -> assertEquals("Nueva", saved.getNombre()),
            () -> assertEquals(15.0, saved.getPrecio())
        );
    }

    @Test
    void testUpdateNotExists() {
        Producto p = new Producto(999L, "X", 12.0, 2, "cat");
        try {
            service.update(p);
            fail("Debería lanzar ProductoNotFoundException");
        } catch (ProductoNotFoundException e) {
            // OK
        }
    }

    @Test
    void testDelete() {
        Producto p = new Producto(null, "To Delete", 5.0, 1, "cat");
        Producto saved = service.save(p);

        service.delete(saved.getId());

        assertAll(
            () -> assertFalse(repo.findById(saved.getId()).isPresent())
        );
    }
}
