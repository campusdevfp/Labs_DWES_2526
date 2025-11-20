package es.iesguzman.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.iesguzman.demo.dto.ResenaRequestDto;
import es.iesguzman.demo.dto.ResenaResponseDto;
import es.iesguzman.demo.exception.ResenaNotFoundException;
import es.iesguzman.demo.service.ResenaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * Test de integración del controlador ResenaController usando MockMvc.
 *
 * Características:
 * - Prueba el sistema de reseñas (opiniones de usuarios sobre productos)
 * - Usa @WebMvcTest para pruebas rápidas sin base de datos
 * - La calificación es un entero (típicamente 1-5 estrellas)
 */
@WebMvcTest(ResenaController.class)
public class ResenaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResenaService resenaService;

    private final ObjectMapper mapper = new ObjectMapper();
    private ResenaResponseDto resenaDto;
    private final String endpoint = "/api/resenas";

    /**
     * Configuración inicial antes de cada test.
     * Crea una reseña de ejemplo con calificación 5 estrellas.
     */
    @BeforeEach
    void setUp() {
        resenaDto = new ResenaResponseDto();
        resenaDto.setId(1L);
        resenaDto.setCalificacion(5); // 5 estrellas (máxima calificación)
        resenaDto.setComentario("Excelente producto");
    }

    /**
     * Test: GET /api/resenas debe devolver lista de reseñas (200).
     *
     * Verifica que se obtienen todas las reseñas del sistema.
     * Incluye reseñas con diferentes calificaciones.
     */
    @Test
    public void testGetAllResenasRetorna200() throws Exception {
        // Preparar segunda reseña con diferente calificación
        ResenaResponseDto resena2 = new ResenaResponseDto();
        resena2.setId(2L);
        resena2.setCalificacion(4);
        resena2.setComentario("Muy bueno");

        List<ResenaResponseDto> resenas = List.of(resenaDto, resena2);

        // Configurar mock: devolver lista de reseñas
        when(resenaService.getAllResenas()).thenReturn(resenas);

        // Ejecutar GET /api/resenas
        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verificar respuesta
        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("Excelente producto")),
                () -> assertTrue(response.getContentAsString().contains("Muy bueno"))
        );

        verify(resenaService, times(1)).getAllResenas();
    }

    /**
     * Test: GET /api/resenas/{id} debe devolver una reseña cuando existe (200).
     *
     * Verifica la obtención de una reseña específica por ID.
     */
    @Test
    public void testGetResenaByIdRetorna200() throws Exception {
        // Configurar mock: devolver reseña cuando se busque ID 1
        when(resenaService.getResenaById(1L)).thenReturn(resenaDto);

        // Ejecutar GET /api/resenas/1
        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint + "/1")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verificar respuesta correcta
        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("Excelente producto"))
        );

        verify(resenaService, times(1)).getResenaById(1L);
    }

    /**
     * Test: GET /api/resenas/{id} debe devolver 404 cuando no existe.
     *
     * Verifica el manejo de error cuando se busca una reseña inexistente.
     */
    @Test
    public void testGetResenaByIdRetorna404() throws Exception {
        // Configurar mock: lanzar excepción cuando se busque ID -1
        when(resenaService.getResenaById(-1L))
                .thenThrow(new ResenaNotFoundException("Reseña no encontrada"));

        // Ejecutar GET con ID inexistente
        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint + "/-1")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verificar que devuelve 404
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

        verify(resenaService, times(1)).getResenaById(-1L);
    }

    /**
     * Test: POST /api/resenas debe crear una reseña con datos válidos (201).
     *
     * Verifica la creación de una nueva reseña.
     * Una reseña vincula un usuario con un producto mediante calificación y comentario.
     */
    @Test
    public void testCreateResenaConDatosValidosRetorna201() throws Exception {
        // Preparar request DTO (datos que envía el cliente)
        ResenaRequestDto requestDto = new ResenaRequestDto();
        requestDto.setUsuarioId(1L); // Usuario que hace la reseña
        requestDto.setProductoId(1L); // Producto reseñado
        requestDto.setCalificacion(4); // 4 estrellas
        requestDto.setComentario("Buen producto");

        // Preparar response DTO (reseña creada)
        ResenaResponseDto createdDto = new ResenaResponseDto();
        createdDto.setId(10L);
        createdDto.setCalificacion(4);
        createdDto.setComentario("Buen producto");

        // Configurar mock: devolver reseña creada
        when(resenaService.createResena(any(ResenaRequestDto.class)))
                .thenReturn(createdDto);

        // Convertir a JSON y ejecutar POST
        String json = mapper.writeValueAsString(requestDto);
        MockHttpServletResponse response = mockMvc.perform(
                        post(endpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verificar creación exitosa
        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("Buen producto"))
        );

        verify(resenaService, times(1)).createResena(any(ResenaRequestDto.class));
    }
}
