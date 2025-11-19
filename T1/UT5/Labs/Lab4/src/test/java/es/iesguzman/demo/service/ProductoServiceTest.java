package es.iesguzman.demo.service;

import es.iesguzman.demo.dto.ProductoRequestDto;
import es.iesguzman.demo.dto.ProductoResponseDto;
import es.iesguzman.demo.exception.ProductoBadRequestException;
import es.iesguzman.demo.exception.ProductoNotFoundException;
import es.iesguzman.demo.mapper.ProductoMapper;
import es.iesguzman.demo.model.Categoria;
import es.iesguzman.demo.model.Producto;
import es.iesguzman.demo.repository.CategoriaRepository;
import es.iesguzman.demo.repository.ProductoRepository;
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
public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ProductoMapper productoMapper;

    @InjectMocks
    private ProductoService productoService;

    @Test
    public void testSaveProductoConDtoValido() {
        // Arrange
        ProductoRequestDto requestDto = new ProductoRequestDto();
        requestDto.setNombre("Laptop");
        requestDto.setPrecio(999.99);
        requestDto.setStock(10);
        requestDto.setCategoriaId(1L);

        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Electrónica");

        Producto producto = new Producto();
        producto.setNombre("Laptop");
        producto.setPrecio(999.99);
        producto.setStock(10);

        Producto productoGuardado = new Producto();
        productoGuardado.setId(1L);
        productoGuardado.setNombre("Laptop");
        productoGuardado.setPrecio(999.99);
        productoGuardado.setStock(10);
        productoGuardado.setCategoria(categoria);

        ProductoResponseDto responseDto = new ProductoResponseDto();
        responseDto.setId(1L);
        responseDto.setNombre("Laptop");

        when(productoRepository.existsByNombre(requestDto.getNombre())).thenReturn(false);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoMapper.toEntity(requestDto)).thenReturn(producto);
        when(productoRepository.save(any(Producto.class))).thenReturn(productoGuardado);
        when(productoMapper.toResponseDto(productoGuardado)).thenReturn(responseDto);

        // Act
        ProductoResponseDto result = productoService.createProducto(requestDto);

        // Assert
        assertNotNull(result);
        assertEquals("Laptop", result.getNombre());
        verify(productoRepository).save(any(Producto.class));
        verify(productoMapper).toEntity(requestDto);
        verify(productoMapper).toResponseDto(productoGuardado);
    }

    @Test
    public void testFindByIdCuandoExiste() {
        // Arrange
        Long id = 1L;
        Producto producto = new Producto();
        producto.setId(id);
        producto.setNombre("Laptop");

        ProductoResponseDto responseDto = new ProductoResponseDto();
        responseDto.setId(id);
        responseDto.setNombre("Laptop");

        when(productoRepository.findById(id)).thenReturn(Optional.of(producto));
        when(productoMapper.toResponseDto(producto)).thenReturn(responseDto);

        // Act
        ProductoResponseDto result = productoService.getProductoById(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Laptop", result.getNombre());
        verify(productoRepository).findById(id);
    }

    @Test
    public void testFindByIdCuandoNoExiste() {
        // Arrange
        Long id = 999L;
        when(productoRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductoNotFoundException.class, () -> {
            productoService.getProductoById(id);
        });
        verify(productoRepository).findById(id);
    }

    @Test
    public void testDeleteById() {
        // Arrange
        Long id = 1L;
        Producto producto = new Producto();
        producto.setId(id);
        producto.setNombre("Laptop");
        producto.setActivo(true);

        when(productoRepository.findById(id)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        // Act
        productoService.deleteProducto(id);

        // Assert
        verify(productoRepository).findById(id);
        verify(productoRepository).save(any(Producto.class));
        assertFalse(producto.getActivo());
    }

    @Test
    public void testSaveProductoConNombreDuplicado() {
        // Arrange
        ProductoRequestDto requestDto = new ProductoRequestDto();
        requestDto.setNombre("Laptop");

        when(productoRepository.existsByNombre("Laptop")).thenReturn(true);

        // Act & Assert
        assertThrows(ProductoBadRequestException.class, () -> {
            productoService.createProducto(requestDto);
        });
        verify(productoRepository, never()).save(any());
    }
}

