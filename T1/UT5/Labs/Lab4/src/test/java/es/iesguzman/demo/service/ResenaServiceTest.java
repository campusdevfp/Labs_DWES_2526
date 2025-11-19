package es.iesguzman.demo.service;

import es.iesguzman.demo.dto.ResenaRequestDto;
import es.iesguzman.demo.dto.ResenaResponseDto;
import es.iesguzman.demo.exception.ResenaBadRequestException;
import es.iesguzman.demo.mapper.ResenaMapper;
import es.iesguzman.demo.model.Producto;
import es.iesguzman.demo.model.Resena;
import es.iesguzman.demo.model.Usuario;
import es.iesguzman.demo.repository.ProductoRepository;
import es.iesguzman.demo.repository.ResenaRepository;
import es.iesguzman.demo.repository.UsuarioRepository;
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
public class ResenaServiceTest {

    @Mock
    private ResenaRepository resenaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ResenaMapper resenaMapper;

    @InjectMocks
    private ResenaService resenaService;

    @Test
    public void testSaveConUsuarioYProductoExistentes() {
        // Arrange
        ResenaRequestDto requestDto = new ResenaRequestDto();
        requestDto.setUsuarioId(1L);
        requestDto.setProductoId(1L);
        requestDto.setCalificacion(5);
        requestDto.setComentario("Excelente producto");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("user1");

        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Laptop");

        Resena resena = new Resena();
        resena.setUsuario(usuario);
        resena.setProducto(producto);
        resena.setCalificacion(5);
        resena.setComentario("Excelente producto");

        Resena resenaGuardada = new Resena();
        resenaGuardada.setId(1L);
        resenaGuardada.setUsuario(usuario);
        resenaGuardada.setProducto(producto);
        resenaGuardada.setCalificacion(5);
        resenaGuardada.setComentario("Excelente producto");

        ResenaResponseDto responseDto = new ResenaResponseDto();
        responseDto.setId(1L);
        responseDto.setCalificacion(5);
        responseDto.setComentario("Excelente producto");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(resenaMapper.toEntity(requestDto)).thenReturn(resena);
        when(resenaRepository.save(any(Resena.class))).thenReturn(resenaGuardada);
        when(resenaMapper.toResponseDto(resenaGuardada)).thenReturn(responseDto);

        // Act
        ResenaResponseDto result = resenaService.createResena(requestDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(5, result.getCalificacion());
        assertEquals("Excelente producto", result.getComentario());
        verify(resenaRepository).save(any(Resena.class));
    }

    @Test
    public void testSaveConUsuarioInexistente() {
        // Arrange
        ResenaRequestDto requestDto = new ResenaRequestDto();
        requestDto.setUsuarioId(999L);
        requestDto.setProductoId(1L);

        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResenaBadRequestException.class, () -> {
            resenaService.createResena(requestDto);
        });
        verify(resenaRepository, never()).save(any());
    }

    @Test
    public void testSaveConProductoInexistente() {
        // Arrange
        ResenaRequestDto requestDto = new ResenaRequestDto();
        requestDto.setUsuarioId(1L);
        requestDto.setProductoId(999L);

        Usuario usuario = new Usuario();
        usuario.setId(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResenaBadRequestException.class, () -> {
            resenaService.createResena(requestDto);
        });
        verify(resenaRepository, never()).save(any());
    }
}

