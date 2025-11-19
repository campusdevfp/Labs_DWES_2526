package es.iesguzman.demo.repository;

import es.iesguzman.demo.model.Usuario;
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
public class UsuarioRepositoryUnitTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    public void testFindAll() {
        // Arrange
        Usuario usuario1 = new Usuario();
        usuario1.setId(1L);
        usuario1.setUsername("usuario1");
        usuario1.setEmail("usuario1@example.com");
        usuario1.setFechaCreacion(LocalDateTime.now());
        usuario1.setActivo(true);

        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setUsername("usuario2");
        usuario2.setEmail("usuario2@example.com");
        usuario2.setFechaCreacion(LocalDateTime.now());
        usuario2.setActivo(true);

        List<Usuario> usuarios = Arrays.asList(usuario1, usuario2);
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        // Act
        List<Usuario> result = usuarioRepository.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("usuario1", result.get(0).getUsername());
        assertEquals("usuario2", result.get(1).getUsername());
    }

    @Test
    public void testFindById() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("usuario1");
        usuario.setEmail("usuario1@example.com");
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setActivo(true);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act
        Optional<Usuario> result = usuarioRepository.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("usuario1", result.get().getUsername());
    }

    @Test
    public void testFindByUsername() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("usuario1");
        usuario.setEmail("usuario1@example.com");
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setActivo(true);

        when(usuarioRepository.findByUsername("usuario1")).thenReturn(Optional.of(usuario));

        // Act
        Optional<Usuario> result = usuarioRepository.findByUsername("usuario1");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("usuario1", result.get().getUsername());
    }

    @Test
    public void testFindByEmail() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("usuario1");
        usuario.setEmail("usuario1@example.com");
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setActivo(true);

        when(usuarioRepository.findByEmail("usuario1@example.com")).thenReturn(Optional.of(usuario));

        // Act
        Optional<Usuario> result = usuarioRepository.findByEmail("usuario1@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("usuario1@example.com", result.get().getEmail());
    }

    @Test
    public void testFindByIdNotFound() {
        // Arrange
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Usuario> result = usuarioRepository.findById(999L);

        // Assert
        assertFalse(result.isPresent());
    }
}
