package es.iesguzman.demo.repository;

import es.iesguzman.demo.model.Categoria;
import es.iesguzman.demo.model.Producto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductoRepositoryUnitTest {

    @Mock
    private ProductoRepository productoRepository;

    @Test
    public void testFindAll() {
        // Arrange
        Categoria categoria = new Categoria();
        categoria.setNombre("Electrónica");

        Producto producto1 = new Producto();
        producto1.setId(1L);
        producto1.setNombre("Laptop");
        producto1.setPrecio(999.99);
        producto1.setStock(10);
        producto1.setCategoria(categoria);
        producto1.setFechaCreacion(LocalDateTime.now());
        producto1.setFechaActualizacion(LocalDateTime.now());
        producto1.setActivo(true);

        Producto producto2 = new Producto();
        producto2.setId(2L);
        producto2.setNombre("Mouse");
        producto2.setPrecio(25.50);
        producto2.setStock(50);
        producto2.setCategoria(categoria);
        producto2.setFechaCreacion(LocalDateTime.now());
        producto2.setFechaActualizacion(LocalDateTime.now());
        producto2.setActivo(true);

        List<Producto> productos = Arrays.asList(producto1, producto2);
        when(productoRepository.findAll()).thenReturn(productos);

        // Act
        List<Producto> result = productoRepository.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Laptop", result.get(0).getNombre());
        assertEquals("Mouse", result.get(1).getNombre());
    }

    @Test
    public void testFindById() {
        // Arrange
        Categoria categoria = new Categoria();
        categoria.setNombre("Electrónica");

        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Laptop");
        producto.setPrecio(999.99);
        producto.setStock(10);
        producto.setCategoria(categoria);
        producto.setFechaCreacion(LocalDateTime.now());
        producto.setFechaActualizacion(LocalDateTime.now());
        producto.setActivo(true);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // Act
        Optional<Producto> result = productoRepository.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Laptop", result.get().getNombre());
    }

    @Test
    public void testFindByCategoriaNombre() {
        // Arrange
        Categoria categoria = new Categoria();
        categoria.setNombre("Electrónica");

        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Laptop");
        producto.setPrecio(999.99);
        producto.setStock(10);
        producto.setCategoria(categoria);
        producto.setFechaCreacion(LocalDateTime.now());
        producto.setFechaActualizacion(LocalDateTime.now());
        producto.setActivo(true);

        List<Producto> productos = Arrays.asList(producto);
        when(productoRepository.findByCategoriaNombre("Electrónica")).thenReturn(productos);

        // Act
        List<Producto> result = productoRepository.findByCategoriaNombre("Electrónica");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getNombre());
    }

    @Test
    public void testFindByActivoTrue() {
        // Arrange
        Categoria categoria = new Categoria();
        categoria.setNombre("Electrónica");

        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Laptop");
        producto.setPrecio(999.99);
        producto.setStock(10);
        producto.setCategoria(categoria);
        producto.setFechaCreacion(LocalDateTime.now());
        producto.setFechaActualizacion(LocalDateTime.now());
        producto.setActivo(true);

        List<Producto> productos = Arrays.asList(producto);
        when(productoRepository.findByActivoTrue()).thenReturn(productos);

        // Act
        List<Producto> result = productoRepository.findByActivoTrue();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getNombre());
        assertTrue(result.get(0).getActivo());
    }

    @Test
    public void testFindByIdNotFound() {
        // Arrange
        when(productoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Producto> result = productoRepository.findById(999L);

        // Assert
        assertFalse(result.isPresent());
    }
}
