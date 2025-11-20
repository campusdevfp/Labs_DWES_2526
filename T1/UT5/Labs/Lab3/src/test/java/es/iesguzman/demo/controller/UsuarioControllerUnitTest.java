package es.iesguzman.demo.controller;

import es.iesguzman.demo.dto.UsuarioResponseDto;
import es.iesguzman.demo.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UsuarioControllerUnitTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    @Test
    public void testGetAllUsuarios() {
        // Crear datos de prueba
        UsuarioResponseDto usuario1 = new UsuarioResponseDto();
        usuario1.setId(1L);
        usuario1.setUsername("usuario1");
        usuario1.setEmail("usuario1@example.com");
        usuario1.setFechaCreacion(LocalDateTime.now());
        usuario1.setActivo(true);

        UsuarioResponseDto usuario2 = new UsuarioResponseDto();
        usuario2.setId(2L);
        usuario2.setUsername("usuario2");
        usuario2.setEmail("usuario2@example.com");
        usuario2.setFechaCreacion(LocalDateTime.now());
        usuario2.setActivo(true);

        List<UsuarioResponseDto> usuarios = Arrays.asList(usuario1, usuario2);

        // Mockear el service
        when(usuarioService.findAll()).thenReturn(usuarios);

        // Llamar al método del controller
        ResponseEntity<List<UsuarioResponseDto>> response = usuarioController.getAllUsuarios();

        // Verificar el resultado
        assertEquals(200, response.getStatusCodeValue());
        List<UsuarioResponseDto> result = response.getBody();
        assertEquals(2, result.size());
        assertEquals("usuario1", result.get(0).getUsername());
        assertEquals("usuario2", result.get(1).getUsername());
    }
}
