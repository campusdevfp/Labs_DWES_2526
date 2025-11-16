package es.iesguzman.productos.service;

import es.iesguzman.productos.domain.Producto;
import es.iesguzman.productos.exception.ProductoNotFoundException;
import es.iesguzman.productos.repository.ProductoRepository;
import es.iesguzman.productos.validator.ProductoValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceUnitTest {

    @Mock
    private ProductoRepository repo;

    @Mock
    private ProductoValidator validator;

    @InjectMocks
    private ProductoService service;

    @Test
    void testFindByIdFound() {
        Producto p = new Producto(1L, "Prod A", 10.0, 5, "cat");
        when(repo.findById(1L)).thenReturn(Optional.of(p));

        Producto result = service.findById(1L);

        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(1L, result.getId()),
            () -> assertEquals("Prod A", result.getNombre())
        );
    }

    @Test
    void testFindByIdNotFound() {
        when(repo.findById(2L)).thenReturn(Optional.empty());
        try {
            service.findById(2L);
            fail("Debería lanzar ProductoNotFoundException");
        } catch (ProductoNotFoundException e) {
            // OK
        }
    }

    @Test
    void testSaveCallsValidatorAndRepoSave() {
        Producto input = new Producto(null, "Nueva", 15.0, 3, "cat");
        when(repo.save(any(Producto.class))).thenAnswer(invocation -> {
            Producto arg = invocation.getArgument(0);
            arg.setId(100L);
            return arg;
        });

        Producto saved = service.save(input);

        assertAll(
            () -> verify(validator, times(1)).validate(input),
            () -> verify(repo, times(1)).save(input),
            () -> assertEquals(100L, saved.getId())
        );
    }

    @Test
    void testUpdateNotExists() {
        Producto p = new Producto(5L, "X", 12.0, 2, "cat");
        when(repo.existsById(5L)).thenReturn(false);
        try {
            service.update(p);
            fail("Debería lanzar ProductoNotFoundException");
        } catch (ProductoNotFoundException e) {
            // OK
        }
    }

    @Test
    void testDeleteCallsRepo() {
        doNothing().when(repo).deleteById(10L);
        service.delete(10L);
        verify(repo, times(1)).deleteById(10L);
    }
}
