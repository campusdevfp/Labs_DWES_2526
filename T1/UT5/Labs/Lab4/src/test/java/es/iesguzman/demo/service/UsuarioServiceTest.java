package es.iesguzman.demo.service;

import es.iesguzman.demo.dto.UsuarioRequestDto;
import es.iesguzman.demo.dto.UsuarioResponseDto;
import es.iesguzman.demo.exception.UsuarioBadRequestException;
import es.iesguzman.demo.exception.UsuarioNotFoundException;
import es.iesguzman.demo.mapper.UsuarioMapper;
import es.iesguzman.demo.model.Usuario;
import es.iesguzman.demo.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioRequestDto requestDto;
    private Usuario usuario;
    private UsuarioResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new UsuarioRequestDto();
        requestDto.setUsername("user1");
        requestDto.setEmail("user1@example.com");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername(requestDto.getUsername());
        usuario.setEmail(requestDto.getEmail());
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setActivo(true);

        responseDto = new UsuarioResponseDto();
        responseDto.setId(usuario.getId());
        responseDto.setUsername(usuario.getUsername());
        responseDto.setEmail(usuario.getEmail());
    }

    @Test
    public void createUsuario_success() {
        when(usuarioRepository.existsByUsername(requestDto.getUsername())).thenReturn(false);
        when(usuarioRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(usuarioMapper.toEntity(requestDto)).thenReturn(usuario);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = (Usuario) inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(usuarioMapper.toResponseDto(any(Usuario.class))).thenReturn(responseDto);

        UsuarioResponseDto result = usuarioService.createUsuario(requestDto);

        assertNotNull(result);
        assertEquals(responseDto.getId(), result.getId());
        assertEquals(responseDto.getUsername(), result.getUsername());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    public void createUsuario_usernameExists_throws() {
        when(usuarioRepository.existsByUsername(requestDto.getUsername())).thenReturn(true);

        assertThrows(UsuarioBadRequestException.class, () -> usuarioService.createUsuario(requestDto));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    public void createUsuario_emailExists_throws() {
        when(usuarioRepository.existsByUsername(requestDto.getUsername())).thenReturn(false);
        when(usuarioRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

        assertThrows(UsuarioBadRequestException.class, () -> usuarioService.createUsuario(requestDto));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    public void getUsuarioById_success() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toResponseDto(usuario)).thenReturn(responseDto);

        UsuarioResponseDto result = usuarioService.getUsuarioById(1L);

        assertNotNull(result);
        assertEquals(responseDto.getId(), result.getId());
        verify(usuarioRepository).findById(1L);
    }

    @Test
    public void getUsuarioById_notFound_throws() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNotFoundException.class, () -> usuarioService.getUsuarioById(1L));
    }

    @Test
    public void getAllUsuarios_returnsList() {
        Usuario other = new Usuario();
        other.setId(2L);
        other.setUsername("user2");
        other.setEmail("user2@example.com");

        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario, other));
        UsuarioResponseDto dto2 = new UsuarioResponseDto();
        dto2.setId(2L);
        dto2.setUsername("user2");
        dto2.setEmail("user2@example.com");

        when(usuarioMapper.toResponseDto(usuario)).thenReturn(responseDto);
        when(usuarioMapper.toResponseDto(other)).thenReturn(dto2);

        List<UsuarioResponseDto> list = usuarioService.getAllUsuarios();

        assertNotNull(list);
        assertEquals(2, list.size());
        verify(usuarioRepository).findAll();
        verify(usuarioMapper, times(2)).toResponseDto(any(Usuario.class));
    }

    @Test
    public void updateUsuario_success() {
        UsuarioRequestDto updateDto = new UsuarioRequestDto();
        updateDto.setUsername("updated");
        updateDto.setEmail("updated@example.com");

        Usuario existing = new Usuario();
        existing.setId(1L);
        existing.setUsername("old");
        existing.setEmail("old@example.com");

        Usuario saved = new Usuario();
        saved.setId(1L);
        saved.setUsername(updateDto.getUsername());
        saved.setEmail(updateDto.getEmail());

        UsuarioResponseDto savedDto = new UsuarioResponseDto();
        savedDto.setId(1L);
        savedDto.setUsername(updateDto.getUsername());
        savedDto.setEmail(updateDto.getEmail());

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(saved);
        when(usuarioMapper.toResponseDto(saved)).thenReturn(savedDto);

        UsuarioResponseDto result = usuarioService.updateUsuario(1L, updateDto);

        assertNotNull(result);
        assertEquals("updated", result.getUsername());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    public void updateUsuario_notFound_throws() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNotFoundException.class, () -> usuarioService.updateUsuario(1L, requestDto));
    }

    @Test
    public void deleteUsuario_success() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        usuarioService.deleteUsuario(1L);

        verify(usuarioRepository).delete(usuario);
    }

    @Test
    public void deleteUsuario_notFound_throws() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNotFoundException.class, () -> usuarioService.deleteUsuario(1L));
    }
}

