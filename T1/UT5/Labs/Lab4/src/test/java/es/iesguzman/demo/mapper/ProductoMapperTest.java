package es.iesguzman.demo.mapper;

import es.iesguzman.demo.dto.ProductoRequestDto;
import es.iesguzman.demo.dto.ProductoResponseDto;
import es.iesguzman.demo.model.Categoria;
import es.iesguzman.demo.model.Producto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductoMapperTest {

    @Mock
    private CategoriaMapper categoriaMapper;

    @InjectMocks
    private ProductoMapper productoMapper;

    @Test
    public void testToEntity() {
        /*
         En el método testToEntity, no se utiliza 'when' porque el método toEntity del mapper
         no invoca ningún servicio o dependencia mockeada (como categoriaMapper). Simplemente
         crea y asigna valores directamente desde el DTO a la entidad Producto, sin llamadas a
         métodos externos que requieran simulación con Mockito. Por eso, no hay necesidad de
         configurar comportamientos mockeados. En contraste, testToResponseDto sí usa 'when'
         porque llama a categoriaMapper.toResponseDto, que debe ser mockeado para evitar
         dependencias reales.
        */

        // Arrange
        ProductoRequestDto dto = new ProductoRequestDto();
        dto.setNombre("Laptop");
        dto.setDescripcion("Laptop gaming");
        dto.setPrecio(999.99);
        dto.setStock(10);
        dto.setCategoriaId(1L);
        dto.setImagenUrl("http://example.com/laptop.jpg");

        // Act
        Producto producto = productoMapper.toEntity(dto);

        // Assert
        assertNotNull(producto);
        assertEquals("Laptop", producto.getNombre());
        assertEquals("Laptop gaming", producto.getDescripcion());
        assertEquals(999.99, producto.getPrecio());
        assertEquals(10, producto.getStock());
        assertEquals(1L, producto.getCategoria().getId());
        assertEquals("http://example.com/laptop.jpg", producto.getImagenUrl());
    }

    @Test
    public void testToResponseDto() {
        // Arrange
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Electrónica");

        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Laptop");
        producto.setDescripcion("Laptop gaming");
        producto.setPrecio(999.99);
        producto.setStock(10);
        producto.setCategoria(categoria);
        producto.setImagenUrl("http://example.com/laptop.jpg");
        producto.setFechaCreacion(LocalDateTime.now());
        producto.setFechaActualizacion(LocalDateTime.now());
        producto.setActivo(true);

        when(categoriaMapper.toResponseDto(categoria)).thenReturn(null);

        // Act
        ProductoResponseDto dto = productoMapper.toResponseDto(producto);

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Laptop", dto.getNombre());
        assertEquals("Laptop gaming", dto.getDescripcion());
        assertEquals(999.99, dto.getPrecio());
        assertEquals(10, dto.getStock());
        assertEquals("http://example.com/laptop.jpg", dto.getImagenUrl());
        assertTrue(dto.getActivo());
        verify(categoriaMapper).toResponseDto(categoria);
    }
}
