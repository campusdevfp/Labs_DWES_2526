package es.iesguzman.demo.repository;

import es.iesguzman.demo.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql(scripts = "/data.sql")
public class UsuarioRepositoryIntegrationTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void testFindAll() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        assertFalse(usuarios.isEmpty());
    }

    @Test
    void testFindById() {
        Optional<Usuario> usuario = usuarioRepository.findById(1L);
        assertTrue(usuario.isPresent());
    }

    @Test
    void testFindByUsername() {
        Optional<Usuario> usuario = usuarioRepository.findByUsername("usuario1");
        assertTrue(usuario.isPresent());
    }

    @Test
    void testFindByEmail() {
        Optional<Usuario> usuario = usuarioRepository.findByEmail("usuario1@example.com");
        assertTrue(usuario.isPresent());
    }

    @Test
    void testExistsByUsername() {
        boolean exists = usuarioRepository.existsByUsername("usuario1");
        assertTrue(exists);
    }

    @Test
    void testExistsByEmail() {
        boolean exists = usuarioRepository.existsByEmail("usuario1@example.com");
        assertTrue(exists);
    }

    @Test
    void testSave() {
        Usuario usuario = new Usuario();
        usuario.setUsername("usuarioNuevo");
        usuario.setEmail("nuevo@example.com");

        Usuario saved = usuarioRepository.save(usuario);
        assertNotNull(saved.getId());
    }

    @Test
    void testDelete() {
        usuarioRepository.deleteById(1L);
        Optional<Usuario> usuario = usuarioRepository.findById(1L);
        assertFalse(usuario.isPresent());
    }
}
