package es.iesguzman.demo.service;

import es.iesguzman.demo.dto.ProductoRequestDto;
import es.iesguzman.demo.dto.ProductoResponseDto;
import es.iesguzman.demo.mapper.ProductoMapper;
import es.iesguzman.demo.model.Categoria;
import es.iesguzman.demo.model.Producto;
import es.iesguzman.demo.repository.CategoriaRepository;
import es.iesguzman.demo.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test unitario del servicio ProductoService.
 * Usa Mockito para simular las dependencias (repository y mapper).
 */
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    ProductoRepository productoRepository;

    @Mock
    ProductoMapper productoMapper;

    @Mock
    CategoriaRepository categoriaRepository;

    @InjectMocks
    ProductoService productoService;

    ProductoRequestDto requestDto;
    Producto producto;
    ProductoResponseDto responseDto;
    Categoria categoria;

    @BeforeEach
    void setUp() {
        requestDto = new ProductoRequestDto();
        requestDto.setNombre("Laptop");
        requestDto.setPrecio(999.99);
        requestDto.setStock(10);
        requestDto.setCategoriaId(1L);

        categoria = new Categoria();
        categoria.setId(1L);

        // Crear producto usando los valores del requestDto
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre(requestDto.getNombre());
        producto.setPrecio(requestDto.getPrecio());
        producto.setStock(requestDto.getStock());
        producto.setActivo(true);

        // Crear responseDto usando los valores de la entidad
        responseDto = new ProductoResponseDto();
        responseDto.setId(producto.getId());
        responseDto.setNombre(producto.getNombre());
        responseDto.setPrecio(producto.getPrecio());
        responseDto.setStock(producto.getStock());
        responseDto.setActivo(producto.getActivo());
    }

    /**
     * Test: getAllProductos debe devolver una lista de productos activos.
     */
    @Test
    void getAllProductos_returnsList() {
        when(productoRepository.findByActivoTrue()).thenReturn(List.of(producto));
        when(productoMapper.toResponseDto(producto)).thenReturn(responseDto);

        List<ProductoResponseDto> list = productoService.getAllProductos();

        assertEquals(1, list.size());
        assertEquals("Laptop", list.get(0).getNombre());
        verify(productoRepository, times(1)).findByActivoTrue();
    }

    /**
     * Test: getProductoById debe devolver un producto cuando existe.
     */
    @Test
    void getProductoById_success() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoMapper.toResponseDto(producto)).thenReturn(responseDto);

        ProductoResponseDto result = productoService.getProductoById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Laptop", result.getNombre());
        verify(productoRepository, times(1)).findById(1L);
    }

    /**
     * Test: createProducto debe crear un nuevo producto correctamente.
     */
    @Test
    void createProducto_success() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoMapper.toEntity(requestDto)).thenReturn(producto);
        when(productoRepository.save(producto)).thenReturn(producto);
        when(productoMapper.toResponseDto(producto)).thenReturn(responseDto);

        ProductoResponseDto result = productoService.createProducto(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Laptop", result.getNombre());
        verify(productoRepository, times(1)).save(producto);
    }

    /**
     * Test: deleteProducto debe hacer soft delete (desactivar) un producto correctamente.
     */
    @Test
    void deleteProducto_success_softDelete() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        productoService.deleteProducto(1L);

        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepository).save(captor.capture());
        assertFalse(captor.getValue().getActivo());
        verify(productoRepository, never()).delete(any());
    }
}
