package es.iesguzman.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.iesguzman.demo.dto.UsuarioRequestDto;
import es.iesguzman.demo.dto.UsuarioResponseDto;
import es.iesguzman.demo.exception.UsuarioNotFoundException;
import es.iesguzman.demo.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * Test de integración del controlador UsuarioController usando MockMvc.
 * Usa @WebMvcTest para cargar solo el controlador sin el contexto completo.
 * Usa @MockBean para reemplazar el servicio real por un mock.
 */
@WebMvcTest(UsuarioController.class)
public class UsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc; // Cliente HTTP simulado

    @MockBean
    private UsuarioService usuarioService; // Mock del servicio

    private final ObjectMapper mapper = new ObjectMapper();
    private UsuarioResponseDto usuarioDto;
    private final String endpoint = "/api/usuarios";

    /**
     * Configuración inicial antes de cada test.
     */
    @BeforeEach
    void setUp() {
        usuarioDto = new UsuarioResponseDto();
        usuarioDto.setId(1L);
        usuarioDto.setUsername("usuario1");
        usuarioDto.setEmail("usuario1@example.com");
        usuarioDto.setFechaCreacion(LocalDateTime.now());
        usuarioDto.setActivo(true);
    }

    /**
     * Test: GET /api/usuarios debe devolver lista de usuarios (200).
     */
    @Test
    public void testGetAllUsuarios() throws Exception {
        // Preparar segundo usuario
        UsuarioResponseDto usuario2 = new UsuarioResponseDto();
        usuario2.setId(2L);
        usuario2.setUsername("usuario2");
        usuario2.setEmail("usuario2@example.com");
        usuario2.setFechaCreacion(LocalDateTime.now());
        usuario2.setActivo(true);

        List<UsuarioResponseDto> usuarios = List.of(usuarioDto, usuario2);

        // Configurar mock
        when(usuarioService.getAllUsuarios()).thenReturn(usuarios);

        // Ejecutar petición
        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verificar
        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("usuario1")),
                () -> assertTrue(response.getContentAsString().contains("usuario2"))
        );

        verify(usuarioService, times(1)).getAllUsuarios();
    }

    /**
     * Test: GET /api/usuarios/{id} debe devolver un usuario cuando existe (200).
     */
    @Test
    public void testGetUsuarioById() throws Exception {
        // Configurar mock
        when(usuarioService.getUsuarioById(1L)).thenReturn(usuarioDto);

        // Ejecutar
        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint + "/1")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verificar
        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("usuario1")),
                () -> assertTrue(response.getContentAsString().contains("usuario1@example.com"))
        );

        verify(usuarioService, times(1)).getUsuarioById(1L);
    }

    /**
     * Test: GET /api/usuarios/{id} debe devolver 404 cuando no existe.
     */
    @Test
    public void testGetUsuarioByIdNotFound() throws Exception {
        // Configurar mock para lanzar excepción
        when(usuarioService.getUsuarioById(-1L)).thenThrow(new UsuarioNotFoundException("Usuario no encontrado"));

        // Ejecutar
        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint + "/-1")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verificar
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

        verify(usuarioService, times(1)).getUsuarioById(-1L);
    }

    /**
     * Test: POST /api/usuarios debe crear un usuario con datos válidos (201).
     */
    @Test
    public void testCreateUsuario() throws Exception {
        // Preparar DTOs
        UsuarioRequestDto requestDto = new UsuarioRequestDto();
        requestDto.setUsername("nuevoUsuario");
        requestDto.setEmail("nuevo@example.com");

        UsuarioResponseDto createdDto = new UsuarioResponseDto();
        createdDto.setId(10L);
        createdDto.setUsername("nuevoUsuario");
        createdDto.setEmail("nuevo@example.com");
        createdDto.setFechaCreacion(LocalDateTime.now());
        createdDto.setActivo(true);

        // Configurar mock
        when(usuarioService.createUsuario(any(UsuarioRequestDto.class))).thenReturn(createdDto);

        // Ejecutar POST
        String json = mapper.writeValueAsString(requestDto);
        MockHttpServletResponse response = mockMvc.perform(
                        post(endpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verificar
        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("nuevoUsuario")),
                () -> assertTrue(response.getContentAsString().contains("nuevo@example.com"))
        );

        verify(usuarioService, times(1)).createUsuario(any(UsuarioRequestDto.class));
    }
}
