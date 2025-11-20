package es.iesguzman.demo.repository;

import es.iesguzman.demo.model.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioRepositoryUnitTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    void testFindAll() {
        Usuario u = new Usuario();
        u.setId(1L);
        u.setUsername("usuario1");
        List<Usuario> lista = List.of(u);

        when(usuarioRepository.findAll()).thenReturn(lista);

        List<Usuario> resultado = usuarioRepository.findAll();
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        Usuario u = new Usuario();
        u.setId(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u));

        Optional<Usuario> resultado = usuarioRepository.findById(1L);
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        verify(usuarioRepository).findById(1L);
    }

    @Test
    void testFindByUsername() {
        Usuario u = new Usuario();
        u.setUsername("usuario1");
        when(usuarioRepository.findByUsername("usuario1")).thenReturn(Optional.of(u));

        Optional<Usuario> resultado = usuarioRepository.findByUsername("usuario1");
        assertTrue(resultado.isPresent());
        assertEquals("usuario1", resultado.get().getUsername());
        verify(usuarioRepository).findByUsername("usuario1");
    }

    @Test
    void testFindByEmail() {
        Usuario u = new Usuario();
        u.setEmail("usuario1@example.com");
        when(usuarioRepository.findByEmail("usuario1@example.com")).thenReturn(Optional.of(u));

        Optional<Usuario> resultado = usuarioRepository.findByEmail("usuario1@example.com");
        assertTrue(resultado.isPresent());
        assertEquals("usuario1@example.com", resultado.get().getEmail());
        verify(usuarioRepository).findByEmail("usuario1@example.com");
    }

    @Test
    void testExistsByUsername() {
        when(usuarioRepository.existsByUsername("usuario1")).thenReturn(true);

        boolean resultado = usuarioRepository.existsByUsername("usuario1");
        assertTrue(resultado);
        verify(usuarioRepository).existsByUsername("usuario1");
    }

    @Test
    void testFindByActivoTrue() {
        Usuario u = new Usuario();
        u.setActivo(true);
        List<Usuario> lista = List.of(u);

        when(usuarioRepository.findByActivoTrue()).thenReturn(lista);

        List<Usuario> resultado = usuarioRepository.findByActivoTrue();
        assertFalse(resultado.isEmpty());
        verify(usuarioRepository).findByActivoTrue();
    }

    @Test
    void testSave() {
        Usuario u = new Usuario();
        u.setUsername("nuevo");

        Usuario saved = new Usuario();
        saved.setId(99L);
        saved.setUsername("nuevo");

        when(usuarioRepository.save(u)).thenReturn(saved);

        Usuario resultado = usuarioRepository.save(u);
        assertNotNull(resultado.getId());
        assertEquals(99L, resultado.getId());
        verify(usuarioRepository).save(u);
    }

    @Test
    void testDelete() {
        doNothing().when(usuarioRepository).deleteById(1L);

        usuarioRepository.deleteById(1L);

        verify(usuarioRepository).deleteById(1L);
    }
}
