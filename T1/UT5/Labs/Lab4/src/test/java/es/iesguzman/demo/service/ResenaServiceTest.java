package es.iesguzman.demo.service;

import es.iesguzman.demo.dto.ResenaRequestDto;
import es.iesguzman.demo.dto.ResenaResponseDto;
import es.iesguzman.demo.mapper.ResenaMapper;
import es.iesguzman.demo.model.Producto;
import es.iesguzman.demo.model.Resena;
import es.iesguzman.demo.model.Usuario;
import es.iesguzman.demo.repository.ProductoRepository;
import es.iesguzman.demo.repository.ResenaRepository;
import es.iesguzman.demo.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test unitario del servicio ResenaService.
 * Usa Mockito para simular las dependencias (repository y mapper).
 */
@ExtendWith(MockitoExtension.class)
class ResenaServiceTest {

    @Mock
    ResenaRepository resenaRepository;

    @Mock
    ResenaMapper resenaMapper;

    @Mock
    UsuarioRepository usuarioRepository;

    @Mock
    ProductoRepository productoRepository;

    @InjectMocks
    ResenaService resenaService;

    ResenaRequestDto requestDto;
    Resena resena;
    ResenaResponseDto responseDto;
    Usuario usuario;
    Producto producto;

    @BeforeEach
    void setUp() {
        requestDto = new ResenaRequestDto();
        requestDto.setUsuarioId(1L);
        requestDto.setProductoId(1L);
        requestDto.setCalificacion(5);
        requestDto.setComentario("Excelente producto");

        usuario = new Usuario();
        usuario.setId(1L);

        producto = new Producto();
        producto.setId(1L);

        // Crear resena usando los valores del requestDto
        resena = new Resena();
        resena.setId(1L);
        resena.setCalificacion(requestDto.getCalificacion());
        resena.setComentario(requestDto.getComentario());
        resena.setFechaCreacion(LocalDateTime.now());

        // Crear responseDto usando los valores de la entidad
        responseDto = new ResenaResponseDto();
        responseDto.setId(resena.getId());
        responseDto.setCalificacion(resena.getCalificacion());
        responseDto.setComentario(resena.getComentario());
        responseDto.setFechaCreacion(resena.getFechaCreacion());
    }

    /**
     * Test: getAllResenas debe devolver una lista de reseñas.
     */
    @Test
    void getAllResenas_returnsList() {
        when(resenaRepository.findAll()).thenReturn(List.of(resena));
        when(resenaMapper.toResponseDto(resena)).thenReturn(responseDto);

        List<ResenaResponseDto> list = resenaService.getAllResenas();

        assertEquals(1, list.size());
        assertEquals(5, list.get(0).getCalificacion());
        verify(resenaRepository, times(1)).findAll();
    }

    /**
     * Test: getResenaById debe devolver una reseña cuando existe.
     */
    @Test
    void getResenaById_success() {
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resena));
        when(resenaMapper.toResponseDto(resena)).thenReturn(responseDto);

        ResenaResponseDto result = resenaService.getResenaById(1L);

        assertEquals(1L, result.getId());
        assertEquals(5, result.getCalificacion());
        verify(resenaRepository, times(1)).findById(1L);
    }

    /**
     * Test: createResena debe crear una nueva reseña correctamente.
     */
    @Test
    void createResena_success() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(resenaMapper.toEntity(requestDto)).thenReturn(resena);
        when(resenaRepository.save(resena)).thenReturn(resena);
        when(resenaMapper.toResponseDto(resena)).thenReturn(responseDto);

        ResenaResponseDto result = resenaService.createResena(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(5, result.getCalificacion());
        verify(resenaRepository, times(1)).save(resena);
    }

    /**
     * Test: deleteResena debe eliminar una reseña correctamente.
     */
    @Test
    void deleteResena_success() {
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resena));

        resenaService.deleteResena(1L);

        verify(resenaRepository, times(1)).delete(resena);
    }
}
