package es.iesguzman.demo.repository;

import es.iesguzman.demo.model.Producto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductoRepositoryUnitTest {

    @Mock
    private ProductoRepository productoRepository;

    @Test
    void testFindAll() {
        Producto p = new Producto();
        p.setId(1L);
        p.setNombre("Demo");
        List<Producto> lista = List.of(p);

        when(productoRepository.findAll()).thenReturn(lista);

        List<Producto> resultado = productoRepository.findAll();
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        Producto p = new Producto();
        p.setId(1L);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(p));

        Optional<Producto> resultado = productoRepository.findById(1L);
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        verify(productoRepository).findById(1L);
    }

    @Test
    void testFindByCategoria() {
        Producto p = new Producto();
        p.setCategoria("Categoría 1");
        List<Producto> lista = List.of(p);

        when(productoRepository.findByCategoria("Categoría 1")).thenReturn(lista);

        List<Producto> resultado = productoRepository.findByCategoria("Categoría 1");
        assertFalse(resultado.isEmpty());
        verify(productoRepository).findByCategoria("Categoría 1");
    }

    @Test
    void testFindByNombreContainingIgnoreCase() {
        Producto p = new Producto();
        p.setNombre("Demo Producto");
        List<Producto> lista = List.of(p);

        when(productoRepository.findByNombreContainingIgnoreCase("demo")).thenReturn(lista);

        List<Producto> resultado = productoRepository.findByNombreContainingIgnoreCase("demo");
        assertFalse(resultado.isEmpty());
        verify(productoRepository).findByNombreContainingIgnoreCase("demo");
    }

    @Test
    void testSave() {
        Producto p = new Producto();
        p.setNombre("Nuevo");
        p.setPrecio(10.0);

        Producto saved = new Producto();
        saved.setId(99L);
        saved.setNombre("Nuevo");
        saved.setPrecio(10.0);

        when(productoRepository.save(p)).thenReturn(saved);

        Producto resultado = productoRepository.save(p);
        assertNotNull(resultado.getId());
        assertEquals(99L, resultado.getId());
        verify(productoRepository).save(p);
    }

    @Test
    void testDelete() {
        doNothing().when(productoRepository).deleteById(1L);

        productoRepository.deleteById(1L);

        verify(productoRepository).deleteById(1L);
    }
}
