package es.iesguzman.demo.mapper;

import es.iesguzman.demo.dto.UsuarioRequestDto;
import es.iesguzman.demo.dto.UsuarioResponseDto;
import es.iesguzman.demo.model.Usuario;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class  UsuarioMapperTest {

    private final UsuarioMapper usuarioMapper = new UsuarioMapper();

    @Test
    public void testToEntity() {
        // Arrange
        UsuarioRequestDto dto = new UsuarioRequestDto();
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");

        // Act
        Usuario usuario = usuarioMapper.toEntity(dto);

        // Assert
        assertNotNull(usuario);
        assertEquals("testuser", usuario.getUsername());
        assertEquals("test@example.com", usuario.getEmail());
    }

    @Test
    public void testToResponseDto() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("testuser");
        usuario.setEmail("test@example.com");
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setActivo(true);

        // Act
        UsuarioResponseDto dto = usuarioMapper.toResponseDto(usuario);

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("testuser", dto.getUsername());
        assertEquals("test@example.com", dto.getEmail());
        assertNotNull(dto.getFechaCreacion());
        assertTrue(dto.getActivo());
    }
}

