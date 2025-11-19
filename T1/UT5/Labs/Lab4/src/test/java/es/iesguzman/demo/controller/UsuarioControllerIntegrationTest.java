package es.iesguzman.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import es.iesguzman.demo.dto.UsuarioResponseDto;
import es.iesguzman.demo.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private String contentAsUtf8(MockHttpServletResponse response) {
        return new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
    }

    @Test
    public void testGetAllUsuarios() throws Exception {
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
        when(usuarioService.getAllUsuarios()).thenReturn(usuarios);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get("/api/usuarios")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Proceso la respuesta
        List<UsuarioResponseDto> res = mapper.readValue(contentAsUtf8(response),
                mapper.getTypeFactory().constructCollectionType(List.class, UsuarioResponseDto.class));

        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> assertTrue(response.getContentType() != null && response.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE)),
                () -> assertTrue(res.size() > 0),
                () -> assertTrue(res.stream().anyMatch(u -> u.getUsername().equals("usuario1")))
        );

        // Verifico que se ha llamado al servicio
        verify(usuarioService, times(1)).getAllUsuarios();
    }
}
