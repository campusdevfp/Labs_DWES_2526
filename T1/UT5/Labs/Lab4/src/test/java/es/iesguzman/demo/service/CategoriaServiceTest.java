package es.iesguzman.demo.service;

import es.iesguzman.demo.dto.CategoriaRequestDto;
import es.iesguzman.demo.dto.CategoriaResponseDto;
import es.iesguzman.demo.mapper.CategoriaMapper;
import es.iesguzman.demo.model.Categoria;
import es.iesguzman.demo.repository.CategoriaRepository;
import es.iesguzman.demo.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test unitario del servicio CategoriaService.
 * Usa Mockito para simular las dependencias (repository y mapper).
 */
@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    CategoriaRepository categoriaRepository;

    @Mock
    CategoriaMapper categoriaMapper;

    @Mock
    ProductoRepository productoRepository;

    @InjectMocks
    CategoriaService categoriaService;

    CategoriaRequestDto requestDto;
    Categoria categoria;
    CategoriaResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new CategoriaRequestDto();
        requestDto.setNombre("Electrónica");
        requestDto.setDescripcion("Productos electrónicos");

        // Crear categoria usando los valores del requestDto
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre(requestDto.getNombre());
        categoria.setDescripcion(requestDto.getDescripcion());

        // Crear responseDto usando los valores de la entidad
        responseDto = new CategoriaResponseDto();
        responseDto.setId(categoria.getId());
        responseDto.setNombre(categoria.getNombre());
        responseDto.setDescripcion(categoria.getDescripcion());
    }

    /**
     * Test: getAllCategorias debe devolver una lista de categorías.
     */
    @Test
    void getAllCategorias_returnsList() {
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria));
        when(categoriaMapper.toResponseDto(categoria)).thenReturn(responseDto);

        List<CategoriaResponseDto> list = categoriaService.getAllCategorias();

        assertEquals(1, list.size());
        assertEquals("Electrónica", list.get(0).getNombre());
        verify(categoriaRepository, times(1)).findAll();
    }

    /**
     * Test: getCategoriaById debe devolver una categoría cuando existe.
     */
    @Test
    void getCategoriaById_success() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaMapper.toResponseDto(categoria)).thenReturn(responseDto);

        CategoriaResponseDto result = categoriaService.getCategoriaById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Electrónica", result.getNombre());
        verify(categoriaRepository, times(1)).findById(1L);
    }

    /**
     * Test: createCategoria debe crear una nueva categoría correctamente.
     */
    @Test
    void createCategoria_success() {
        when(categoriaRepository.existsByNombre("Electrónica")).thenReturn(false);
        when(categoriaMapper.toEntity(requestDto)).thenReturn(categoria);
        when(categoriaRepository.save(categoria)).thenReturn(categoria);
        when(categoriaMapper.toResponseDto(categoria)).thenReturn(responseDto);

        CategoriaResponseDto result = categoriaService.createCategoria(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Electrónica", result.getNombre());
        verify(categoriaRepository, times(1)).save(categoria);
    }

    /**
     * Test: deleteCategoria debe eliminar una categoría correctamente.
     */
    @Test
    void deleteCategoria_success() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        // Simular que no existen productos asociados a la categoría
        when(productoRepository.findByCategoriaId(1L)).thenReturn(Collections.emptyList());

        categoriaService.deleteCategoria(1L);

        verify(categoriaRepository, times(1)).delete(categoria);
    }
}
