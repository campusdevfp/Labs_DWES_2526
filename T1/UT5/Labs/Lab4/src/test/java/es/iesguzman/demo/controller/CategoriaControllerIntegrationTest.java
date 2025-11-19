package es.iesguzman.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.iesguzman.demo.dto.CategoriaRequestDto;
import es.iesguzman.demo.dto.CategoriaResponseDto;
import es.iesguzman.demo.dto.ErrorResponse;
import es.iesguzman.demo.service.CategoriaService;
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
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoriaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoriaService categoriaService;

    private final ObjectMapper mapper = new ObjectMapper();

    private String contentAsUtf8(MockHttpServletResponse response) {
        return new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
    }

    @Test
    public void testGetAllCategoriasRetorna200() throws Exception {
        CategoriaResponseDto categoria1 = new CategoriaResponseDto();
        categoria1.setId(1L);
        categoria1.setNombre("Electrónica");
        categoria1.setDescripcion("Productos electrónicos");

        CategoriaResponseDto categoria2 = new CategoriaResponseDto();
        categoria2.setId(2L);
        categoria2.setNombre("Libros");
        categoria2.setDescripcion("Libros y literatura");

        List<CategoriaResponseDto> categorias = Arrays.asList(categoria1, categoria2);
        when(categoriaService.getAllCategorias()).thenReturn(categorias);

        MockHttpServletResponse response = mockMvc.perform(get("/api/categorias"))
                .andReturn().getResponse();

        List<CategoriaResponseDto> res = mapper.readValue(
                contentAsUtf8(response),
                mapper.getTypeFactory().constructCollectionType(List.class, CategoriaResponseDto.class)
        );

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertTrue(response.getContentType() != null && response.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE)),
                () -> assertEquals(2, res.size()),
                () -> assertTrue(res.stream().anyMatch(c -> "Electrónica".equals(c.getNombre())))
        );

        verify(categoriaService, times(1)).getAllCategorias();
    }

    @Test
    public void testGetCategoriaByIdRetorna200() throws Exception {
        CategoriaResponseDto categoria = new CategoriaResponseDto();
        categoria.setId(1L);
        categoria.setNombre("Electrónica");
        categoria.setDescripcion("Productos electrónicos");

        when(categoriaService.getCategoriaById(1L)).thenReturn(categoria);

        MockHttpServletResponse response = mockMvc.perform(get("/api/categorias/1"))
                .andReturn().getResponse();

        CategoriaResponseDto res = mapper.readValue(contentAsUtf8(response), CategoriaResponseDto.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertTrue(response.getContentType() != null && response.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE)),
                () -> assertEquals(1L, res.getId()),
                () -> assertEquals("Electrónica", res.getNombre())
        );

        verify(categoriaService, times(1)).getCategoriaById(1L);
    }

    @Test
    public void testGetCategoriaByIdRetorna404() throws Exception {
        when(categoriaService.getCategoriaById(999L))
                .thenThrow(new es.iesguzman.demo.exception.CategoriaNotFoundException("Categoría no encontrada"));

        MockHttpServletResponse response = mockMvc.perform(get("/api/categorias/999"))
                .andReturn().getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

        verify(categoriaService, times(1)).getCategoriaById(999L);
    }

    @Test
    public void testCreateCategoriaConDatosValidosRetorna201() throws Exception {
        CategoriaResponseDto responseDto = new CategoriaResponseDto();
        responseDto.setId(1L);
        responseDto.setNombre("Electrónica");
        responseDto.setDescripcion("Productos electrónicos");

        when(categoriaService.createCategoria(any(CategoriaRequestDto.class))).thenReturn(responseDto);

        String requestBody = "{"
                + "\"nombre\": \"Electrónica\","
                + "\"descripcion\": \"Productos electrónicos\","
                + "\"imagenUrl\": \"http://example.com/electronica.jpg\""
                + "}";

        MockHttpServletResponse response = mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn().getResponse();

        CategoriaResponseDto res = mapper.readValue(contentAsUtf8(response), CategoriaResponseDto.class);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
                () -> assertEquals(1L, res.getId()),
                () -> assertEquals("Electrónica", res.getNombre())
        );

        verify(categoriaService, times(1)).createCategoria(any(CategoriaRequestDto.class));
    }

    @Test
    public void testCreateCategoriaConDatosInvalidosRetorna400() throws Exception {
        String requestBody = "{"
                + "\"descripcion\": \"Solo descripción\""
                + "}";

        MockHttpServletResponse response = mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn().getResponse();

        // Intentamos parsear un posible ErrorResponse; si no es JSON válido lo ignoramos y comprobamos el status
        try {
            ErrorResponse err = mapper.readValue(contentAsUtf8(response), ErrorResponse.class);
            assertEquals(400, err.getStatus());
        } catch (Exception ignored) {
            // no body o formato distinto
        }

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        verify(categoriaService, times(0)).createCategoria(any(CategoriaRequestDto.class));
    }

    @Test
    public void testUpdateCategoriaExistenteRetorna200() throws Exception {
        CategoriaResponseDto responseDto = new CategoriaResponseDto();
        responseDto.setId(1L);
        responseDto.setNombre("Electrónica Actualizada");
        responseDto.setDescripcion("Productos electrónicos actualizados");

        when(categoriaService.updateCategoria(eq(1L), any(CategoriaRequestDto.class))).thenReturn(responseDto);

        String requestBody = "{"
                + "\"nombre\": \"Electrónica Actualizada\","
                + "\"descripcion\": \"Productos electrónicos actualizados\","
                + "\"imagenUrl\": \"http://example.com/electronica-new.jpg\""
                + "}";

        MockHttpServletResponse response = mockMvc.perform(put("/api/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn().getResponse();

        CategoriaResponseDto res = mapper.readValue(contentAsUtf8(response), CategoriaResponseDto.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals("Electrónica Actualizada", res.getNombre())
        );

        verify(categoriaService, times(1)).updateCategoria(eq(1L), any(CategoriaRequestDto.class));
    }

    @Test
    public void testUpdateCategoriaNoExistenteRetorna404() throws Exception {
        when(categoriaService.updateCategoria(eq(999L), any(CategoriaRequestDto.class)))
                .thenThrow(new es.iesguzman.demo.exception.CategoriaNotFoundException("Categoría no encontrada"));

        String requestBody = "{"
                + "\"nombre\": \"Electrónica\","
                + "\"descripcion\": \"Productos electrónicos\""
                + "}";

        MockHttpServletResponse response = mockMvc.perform(put("/api/categorias/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn().getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

        verify(categoriaService, times(1)).updateCategoria(eq(999L), any(CategoriaRequestDto.class));
    }

    @Test
    public void testDeleteCategoriaRetorna204() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(delete("/api/categorias/1"))
                .andReturn().getResponse();

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());

        verify(categoriaService, times(1)).deleteCategoria(1L);
    }

    @Test
    public void testDeleteCategoriaNoExistenteRetorna404() throws Exception {
        doThrow(new es.iesguzman.demo.exception.CategoriaNotFoundException("Categoría no encontrada"))
                .when(categoriaService).deleteCategoria(999L);

        MockHttpServletResponse response = mockMvc.perform(delete("/api/categorias/999"))
                .andReturn().getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

        verify(categoriaService, times(1)).deleteCategoria(999L);
    }

    @Test
    public void testValidacionNombreObligatorio() throws Exception {
        String requestBody = "{"
                + "\"descripcion\": \"Descripción sin nombre\""
                + "}";

        MockHttpServletResponse response = mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn().getResponse();

        // si se devuelve ErrorResponse en JSON comprobamos campos
        try {
            ErrorResponse err = mapper.readValue(contentAsUtf8(response), ErrorResponse.class);
            assertEquals(400, err.getStatus());
            assertEquals("Datos inválidos", err.getMessage());
        } catch (Exception ignored) {
        }

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        verify(categoriaService, times(0)).createCategoria(any(CategoriaRequestDto.class));
    }
}
