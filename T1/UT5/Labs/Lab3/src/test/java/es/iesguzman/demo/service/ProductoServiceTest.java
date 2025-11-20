package es.iesguzman.demo.service;

import es.iesguzman.demo.dto.ProductoRequestDto;
import es.iesguzman.demo.dto.ProductoResponseDto;
import es.iesguzman.demo.exception.ProductoNotFoundException;
import es.iesguzman.demo.model.Producto;
import es.iesguzman.demo.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void testFindById_WhenExists() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Producto Test");

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        ProductoResponseDto result = productoService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(productoRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_WhenNotExists() {
        when(productoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductoNotFoundException.class, () -> productoService.findById(1L));
        verify(productoRepository, times(1)).findById(1L);
    }

    @Test
    void testSave() {
        ProductoRequestDto dto = new ProductoRequestDto();
        dto.setNombre("Nuevo Producto");
        dto.setPrecio(100.0);
        dto.setStock(10);

        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Nuevo Producto");

        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        ProductoResponseDto result = productoService.save(dto);

        assertNotNull(result);
        assertEquals("Nuevo Producto", result.getNombre());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void testDeleteById() {
        when(productoRepository.existsById(1L)).thenReturn(true);

        productoService.deleteById(1L);

        verify(productoRepository, times(1)).existsById(1L);
        verify(productoRepository, times(1)).deleteById(1L);
    }
}
