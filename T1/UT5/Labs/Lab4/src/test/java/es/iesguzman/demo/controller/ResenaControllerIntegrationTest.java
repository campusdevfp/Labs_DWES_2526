package es.iesguzman.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import es.iesguzman.demo.dto.ResenaRequestDto;
import es.iesguzman.demo.dto.ResenaResponseDto;
import es.iesguzman.demo.dto.ErrorResponse;
import es.iesguzman.demo.service.ResenaService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ResenaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResenaService resenaService;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private String contentAsUtf8(MockHttpServletResponse response) {
        return new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
    }

    @Test
    public void testGetAllResenasRetorna200() throws Exception {
        ResenaResponseDto resena1 = new ResenaResponseDto();
        resena1.setId(1L);
        resena1.setCalificacion(5);
        resena1.setComentario("Excelente producto");
        resena1.setFechaCreacion(LocalDateTime.now());

        ResenaResponseDto resena2 = new ResenaResponseDto();
        resena2.setId(2L);
        resena2.setCalificacion(4);
        resena2.setComentario("Buen producto");
        resena2.setFechaCreacion(LocalDateTime.now());

        List<ResenaResponseDto> resenas = Arrays.asList(resena1, resena2);
        when(resenaService.getAllResenas()).thenReturn(resenas);

        MockHttpServletResponse response = mockMvc.perform(get("/api/resenas"))
                .andReturn().getResponse();

        List<ResenaResponseDto> res = mapper.readValue(
                contentAsUtf8(response),
                mapper.getTypeFactory().constructCollectionType(List.class, ResenaResponseDto.class)
        );

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertTrue(response.getContentType() != null && response.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE)),
                () -> assertEquals(2, res.size())
        );

        verify(resenaService, times(1)).getAllResenas();
    }

    @Test
    public void testGetResenaByIdRetorna200() throws Exception {
        ResenaResponseDto resena = new ResenaResponseDto();
        resena.setId(1L);
        resena.setCalificacion(5);
        resena.setComentario("Excelente producto");
        resena.setFechaCreacion(LocalDateTime.now());

        when(resenaService.getResenaById(1L)).thenReturn(resena);

        MockHttpServletResponse response = mockMvc.perform(get("/api/resenas/1"))
                .andReturn().getResponse();

        ResenaResponseDto res = mapper.readValue(contentAsUtf8(response), ResenaResponseDto.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertTrue(response.getContentType() != null && response.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE)),
                () -> assertEquals(1L, res.getId()),
                () -> assertEquals(5, res.getCalificacion())
        );

        verify(resenaService, times(1)).getResenaById(1L);
    }

    @Test
    public void testGetResenaByIdRetorna404() throws Exception {
        when(resenaService.getResenaById(999L))
                .thenThrow(new es.iesguzman.demo.exception.ResenaNotFoundException("Reseña no encontrada"));

        MockHttpServletResponse response = mockMvc.perform(get("/api/resenas/999"))
                .andReturn().getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

        verify(resenaService, times(1)).getResenaById(999L);
    }

    @Test
    public void testCreateResenaConDatosValidosRetorna201() throws Exception {
        ResenaResponseDto responseDto = new ResenaResponseDto();
        responseDto.setId(1L);
        responseDto.setCalificacion(5);
        responseDto.setComentario("Excelente producto");
        responseDto.setFechaCreacion(LocalDateTime.now());

        when(resenaService.createResena(any(ResenaRequestDto.class))).thenReturn(responseDto);

        String requestBody = "{"
                + "\"usuarioId\": 1,"
                + "\"productoId\": 1,"
                + "\"calificacion\": 5,"
                + "\"comentario\": \"Excelente producto\""
                + "}";

        MockHttpServletResponse response = mockMvc.perform(post("/api/resenas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn().getResponse();

        ResenaResponseDto res = mapper.readValue(contentAsUtf8(response), ResenaResponseDto.class);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
                () -> assertEquals(1L, res.getId()),
                () -> assertEquals(5, res.getCalificacion())
        );

        verify(resenaService, times(1)).createResena(any(ResenaRequestDto.class));
    }

    @Test
    public void testCreateResenaConDatosInvalidosRetorna400() throws Exception {
        String requestBody = "{"
                + "\"usuarioId\": 1,"
                + "\"productoId\": 1,"
                + "\"calificacion\": 6,"
                + "\"comentario\": \"Comentario\""
                + "}";

        MockHttpServletResponse response = mockMvc.perform(post("/api/resenas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn().getResponse();

        try {
            ErrorResponse err = mapper.readValue(contentAsUtf8(response), ErrorResponse.class);
            assertEquals(400, err.getStatus());
        } catch (Exception ignored) {
        }

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        verify(resenaService, times(0)).createResena(any(ResenaRequestDto.class));
    }

//    @Test
//    public void testUpdateResenaExistenteRetorna200() throws Exception {
//        ResenaResponseDto responseDto = new ResenaResponseDto();
//        responseDto.setId(1L);
//        responseDto.setCalificacion(4);
//        responseDto.setComentario("Producto actualizado");
//        responseDto.setFechaCreacion(LocalDateTime.now());
//
//        when(resenaService.updateResena(eq(1L), any(ResenaRequestDto.class))).thenReturn(responseDto);
//
//        String requestBody = "{"
//                + "\"usuarioId\": 1,"
//                + "\"productoId\": 1,"
//                + "\"calificacion\": 4,"
//                + "\"comentario\": \"Producto actualizado\""
//                + "}";
//
//        MockHttpServletResponse response = mockMvc.perform(put("/api/resenas/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andReturn().getResponse();
//
//        ResenaResponseDto res = mapper.readValue(contentAsUtf8(response), ResenaResponseDto.class);
//
//        assertAll(
//                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
//                () -> assertEquals(4, res.getCalificacion())
//        );
//
//        verify(resenaService, times(1)).updateResena(eq(1L), any(ResenaRequestDto.class));
//    }
//
//    @Test
//    public void testUpdateResenaNoExistenteRetorna404() throws Exception {
//        when(resenaService.updateResena(eq(999L), any(ResenaRequestDto.class)))
//                .thenThrow(new es.iesguzman.demo.exception.ResenaNotFoundException("Reseña no encontrada"));
//
//        String requestBody = "{"
//                + "\"usuarioId\": 1,"
//                + "\"productoId\": 1,"
//                + "\"calificacion\": 4,"
//                + "\"comentario\": \"Producto actualizado\""
//                + "}";
//
//        MockHttpServletResponse response = mockMvc.perform(put("/api/resenas/999")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andReturn().getResponse();
//
//        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
//
//        verify(resenaService, times(1)).updateResena(eq(999L), any(ResenaRequestDto.class));
//    }
//
//    @Test
//    public void testDeleteResenaRetorna204() throws Exception {
//        MockHttpServletResponse response = mockMvc.perform(delete("/api/resenas/1"))
//                .andReturn().getResponse();
//
//        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
//
//        verify(resenaService, times(1)).deleteResena(1L);
//    }
//
//    @Test
//    public void testDeleteResenaNoExistenteRetorna404() throws Exception {
//        doThrow(new es.iesguzman.demo.exception.ResenaNotFoundException("Reseña no encontrada"))
//                .when(resenaService).deleteResena(999L);
//
//        MockHttpServletResponse response = mockMvc.perform(delete("/api/resenas/999"))
//                .andReturn().getResponse();
//
//        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
//
//        verify(resenaService, times(1)).deleteResena(999L);
//    }

    @Test
    public void testGetResenasByProductoRetorna200() throws Exception {
        ResenaResponseDto resena = new ResenaResponseDto();
        resena.setId(1L);
        resena.setCalificacion(5);
        resena.setComentario("Excelente");

        List<ResenaResponseDto> resenas = Arrays.asList(resena);
        when(resenaService.getResenasByProducto(1L)).thenReturn(resenas);

        MockHttpServletResponse response = mockMvc.perform(get("/api/resenas/producto/1"))
                .andReturn().getResponse();

        List<ResenaResponseDto> res = mapper.readValue(
                contentAsUtf8(response),
                mapper.getTypeFactory().constructCollectionType(List.class, ResenaResponseDto.class)
        );

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(1, res.size())
        );

        verify(resenaService, times(1)).getResenasByProducto(1L);
    }

    @Test
    public void testGetResenasByUsuarioRetorna200() throws Exception {
        ResenaResponseDto resena = new ResenaResponseDto();
        resena.setId(1L);
        resena.setCalificacion(5);
        resena.setComentario("Excelente");

        List<ResenaResponseDto> resenas = Arrays.asList(resena);
        when(resenaService.getResenasByUsuario(1L)).thenReturn(resenas);

        MockHttpServletResponse response = mockMvc.perform(get("/api/resenas/usuario/1"))
                .andReturn().getResponse();

        List<ResenaResponseDto> res = mapper.readValue(
                contentAsUtf8(response),
                mapper.getTypeFactory().constructCollectionType(List.class, ResenaResponseDto.class)
        );

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(1, res.size())
        );

        verify(resenaService, times(1)).getResenasByUsuario(1L);
    }
}
