package es.iesguzman.demo.service;

import es.iesguzman.demo.dto.CategoriaRequestDto;
import es.iesguzman.demo.dto.CategoriaResponseDto;
import es.iesguzman.demo.exception.CategoriaBadRequestException;
import es.iesguzman.demo.mapper.CategoriaMapper;
import es.iesguzman.demo.model.Categoria;
import es.iesguzman.demo.repository.CategoriaRepository;
import es.iesguzman.demo.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaMapper categoriaMapper;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    public void testSaveConNombreUnico() {
        // Arrange
        CategoriaRequestDto requestDto = new CategoriaRequestDto();
        requestDto.setNombre("Electrónica");
        requestDto.setDescripcion("Productos electrónicos");

        Categoria categoria = new Categoria();
        categoria.setNombre("Electrónica");

        Categoria categoriaGuardada = new Categoria();
        categoriaGuardada.setId(1L);
        categoriaGuardada.setNombre("Electrónica");

        CategoriaResponseDto responseDto = new CategoriaResponseDto();
        responseDto.setId(1L);
        responseDto.setNombre("Electrónica");

        when(categoriaRepository.existsByNombre("Electrónica")).thenReturn(false);
        when(categoriaMapper.toEntity(requestDto)).thenReturn(categoria);
        when(categoriaRepository.save(categoria)).thenReturn(categoriaGuardada);
        when(categoriaMapper.toResponseDto(categoriaGuardada)).thenReturn(responseDto);

        // Act
        CategoriaResponseDto result = categoriaService.createCategoria(requestDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Electrónica", result.getNombre());
        verify(categoriaRepository).save(categoria);
    }

    @Test
    public void testSaveConNombreDuplicado() {
        // Arrange
        CategoriaRequestDto requestDto = new CategoriaRequestDto();
        requestDto.setNombre("Electrónica");

        when(categoriaRepository.existsByNombre("Electrónica")).thenReturn(true);

        // Act & Assert
        assertThrows(CategoriaBadRequestException.class, () -> {
            categoriaService.createCategoria(requestDto);
        });
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    public void testFindAllCategorias() {
        // Arrange
        Categoria cat1 = new Categoria();
        cat1.setId(1L);
        cat1.setNombre("Electrónica");

        Categoria cat2 = new Categoria();
        cat2.setId(2L);
        cat2.setNombre("Ropa");

        CategoriaResponseDto dto1 = new CategoriaResponseDto();
        dto1.setId(1L);
        dto1.setNombre("Electrónica");

        CategoriaResponseDto dto2 = new CategoriaResponseDto();
        dto2.setId(2L);
        dto2.setNombre("Ropa");

        when(categoriaRepository.findAll()).thenReturn(Arrays.asList(cat1, cat2));
        when(categoriaMapper.toResponseDto(cat1)).thenReturn(dto1);
        when(categoriaMapper.toResponseDto(cat2)).thenReturn(dto2);

        // Act
        List<CategoriaResponseDto> result = categoriaService.getAllCategorias();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Electrónica", result.get(0).getNombre());
        assertEquals("Ropa", result.get(1).getNombre());
        verify(categoriaRepository).findAll();
    }

    @Test
    public void testDeleteCategoria() {
        // Arrange
        Long id = 1L;
        Categoria categoria = new Categoria();
        categoria.setId(id);
        categoria.setNombre("Electrónica");

        when(categoriaRepository.findById(id)).thenReturn(java.util.Optional.of(categoria));

        // Act
        categoriaService.deleteCategoria(id);

        // Assert
        verify(categoriaRepository).delete(categoria);
    }
}

