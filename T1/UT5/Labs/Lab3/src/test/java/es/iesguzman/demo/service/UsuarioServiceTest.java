package es.iesguzman.demo.service;

import es.iesguzman.demo.dto.UsuarioRequestDto;
import es.iesguzman.demo.dto.UsuarioResponseDto;
import es.iesguzman.demo.exception.UsuarioNotFoundException;
import es.iesguzman.demo.model.Usuario;
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
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void testFindById_WhenExists() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("usuario1");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        UsuarioResponseDto result = usuarioService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_WhenNotExists() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNotFoundException.class, () -> usuarioService.findById(1L));
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void testSave() {
        UsuarioRequestDto dto = new UsuarioRequestDto();
        dto.setUsername("nuevoUsuario");
        dto.setEmail("nuevo@example.com");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("nuevoUsuario");

        when(usuarioRepository.existsByUsername("nuevoUsuario")).thenReturn(false);
        when(usuarioRepository.existsByEmail("nuevo@example.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioResponseDto result = usuarioService.save(dto);

        assertNotNull(result);
        assertEquals("nuevoUsuario", result.getUsername());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testDeleteById() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        usuarioService.deleteById(1L);

        verify(usuarioRepository, times(1)).existsById(1L);
        verify(usuarioRepository, times(1)).deleteById(1L);
    }
}
