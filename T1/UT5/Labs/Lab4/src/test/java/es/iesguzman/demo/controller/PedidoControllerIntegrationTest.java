package es.iesguzman.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import es.iesguzman.demo.dto.PedidoRequestDto;
import es.iesguzman.demo.dto.PedidoResponseDto;
import es.iesguzman.demo.model.EstadoPedido;
import es.iesguzman.demo.service.PedidoService;
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
public class PedidoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PedidoService pedidoService;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private String contentAsUtf8(MockHttpServletResponse response) {
        return new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
    }

    @Test
    public void testGetAllPedidosRetorna200() throws Exception {
        PedidoResponseDto pedido1 = new PedidoResponseDto();
        pedido1.setId(1L);
        pedido1.setCantidad(2);
        pedido1.setTotal(1999.98);
        pedido1.setEstado(EstadoPedido.PENDIENTE);
        pedido1.setFechaCreacion(LocalDateTime.now());

        PedidoResponseDto pedido2 = new PedidoResponseDto();
        pedido2.setId(2L);
        pedido2.setCantidad(1);
        pedido2.setTotal(25.50);
        pedido2.setEstado(EstadoPedido.ENVIADO);
        pedido2.setFechaCreacion(LocalDateTime.now());

        List<PedidoResponseDto> pedidos = Arrays.asList(pedido1, pedido2);
        when(pedidoService.getAllPedidos()).thenReturn(pedidos);

        MockHttpServletResponse response = mockMvc.perform(get("/api/pedidos"))
                .andReturn().getResponse();

        List<PedidoResponseDto> res = mapper.readValue(
                contentAsUtf8(response),
                mapper.getTypeFactory().constructCollectionType(List.class, PedidoResponseDto.class)
        );

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertTrue(response.getContentType() != null && response.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE)),
                () -> assertEquals(2, res.size()),
                () -> assertTrue(res.stream().anyMatch(p -> p.getCantidad() == 2)),
                () -> assertTrue(res.stream().anyMatch(p -> "ENVIADO".equals(p.getEstado().toString())))
        );

        verify(pedidoService, times(1)).getAllPedidos();
    }

    @Test
    public void testGetPedidoByIdRetorna200() throws Exception {
        PedidoResponseDto pedido = new PedidoResponseDto();
        pedido.setId(1L);
        pedido.setCantidad(2);
        pedido.setTotal(1999.98);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setFechaCreacion(LocalDateTime.now());

        when(pedidoService.getPedidoById(1L)).thenReturn(pedido);

        MockHttpServletResponse response = mockMvc.perform(get("/api/pedidos/1"))
                .andReturn().getResponse();

        PedidoResponseDto res = mapper.readValue(contentAsUtf8(response), PedidoResponseDto.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertTrue(response.getContentType() != null && response.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE)),
                () -> assertEquals(1L, res.getId()),
                () -> assertEquals(2, res.getCantidad())
        );

        verify(pedidoService, times(1)).getPedidoById(1L);
    }

    @Test
    public void testGetPedidoByIdRetorna404() throws Exception {
        when(pedidoService.getPedidoById(999L))
                .thenThrow(new es.iesguzman.demo.exception.PedidoNotFoundException("Pedido no encontrado"));

        MockHttpServletResponse response = mockMvc.perform(get("/api/pedidos/999"))
                .andReturn().getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

        verify(pedidoService, times(1)).getPedidoById(999L);
    }

    @Test
    public void testCreatePedidoConDatosValidosRetorna201() throws Exception {
        PedidoResponseDto responseDto = new PedidoResponseDto();
        responseDto.setId(1L);
        responseDto.setCantidad(2);
        responseDto.setTotal(1999.98);
        responseDto.setEstado(EstadoPedido.PENDIENTE);
        responseDto.setFechaCreacion(LocalDateTime.now());

        when(pedidoService.createPedido(any(PedidoRequestDto.class))).thenReturn(responseDto);

        String requestBody = "{"
                + "\"usuarioId\": 1,"
                + "\"productoId\": 1,"
                + "\"cantidad\": 2"
                + "}";

        MockHttpServletResponse response = mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn().getResponse();

        PedidoResponseDto res = mapper.readValue(contentAsUtf8(response), PedidoResponseDto.class);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
                () -> assertEquals(1L, res.getId()),
                () -> assertEquals(2, res.getCantidad())
        );

        verify(pedidoService, times(1)).createPedido(any(PedidoRequestDto.class));
    }

    @Test
    public void testCreatePedidoConDatosInvalidosRetorna400() throws Exception {
        String requestBody = "{"
                + "\"usuarioId\": 1,"
                + "\"productoId\": 1"
                + "}";

        MockHttpServletResponse response = mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn().getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        verify(pedidoService, times(0)).createPedido(any(PedidoRequestDto.class));
    }
//
//    @Test
//    public void testUpdateEstadoPedidoRetorna200() throws Exception {
//        PedidoResponseDto responseDto = new PedidoResponseDto();
//        responseDto.setId(1L);
//        responseDto.setCantidad(2);
//        responseDto.setTotal(1999.98);
//        responseDto.setEstado(EstadoPedido.ENVIADO);
//        responseDto.setFechaCreacion(LocalDateTime.now());
//
//        when(pedidoService.updateEstadoPedido(eq(1L), any(EstadoPedido.class))).thenReturn(responseDto);
//
//        String requestBody = "\"ENVIADO\"";
//
//        MockHttpServletResponse response = mockMvc.perform(put("/api/pedidos/1/estado")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andReturn().getResponse();
//
//        PedidoResponseDto res = mapper.readValue(contentAsUtf8(response), PedidoResponseDto.class);
//
//        assertAll(
//                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
//                () -> assertEquals("ENVIADO", res.getEstado().toString())
//        );
//
//        verify(pedidoService, times(1)).updateEstadoPedido(eq(1L), any(EstadoPedido.class));
//    }
//
//    @Test
//    public void testUpdateEstadoPedidoNoExistenteRetorna404() throws Exception {
//        when(pedidoService.updateEstadoPedido(eq(999L), any(EstadoPedido.class)))
//                .thenThrow(new es.iesguzman.demo.exception.PedidoNotFoundException("Pedido no encontrado"));
//
//        String requestBody = "\"ENVIADO\"";
//
//        MockHttpServletResponse response = mockMvc.perform(put("/api/pedidos/999/estado")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andReturn().getResponse();
//
//        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
//
//        verify(pedidoService, times(1)).updateEstadoPedido(eq(999L), any(EstadoPedido.class));
//    }
//
//    @Test
//    public void testDeletePedidoRetorna204() throws Exception {
//        MockHttpServletResponse response = mockMvc.perform(delete("/api/pedidos/1"))
//                .andReturn().getResponse();
//
//        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
//
//        verify(pedidoService, times(1)).deletePedido(1L);
//    }
//
//    @Test
//    public void testDeletePedidoNoExistenteRetorna404() throws Exception {
//        doThrow(new es.iesguzman.demo.exception.PedidoNotFoundException("Pedido no encontrado"))
//                .when(pedidoService).deletePedido(999L);
//
//        MockHttpServletResponse response = mockMvc.perform(delete("/api/pedidos/999"))
//                .andReturn().getResponse();
//
//        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
//
//        verify(pedidoService, times(1)).deletePedido(999L);
//    }

    @Test
    public void testGetPedidosByUsuarioRetorna200() throws Exception {
        PedidoResponseDto pedido = new PedidoResponseDto();
        pedido.setId(1L);
        pedido.setCantidad(2);
        pedido.setTotal(1999.98);
        pedido.setEstado(EstadoPedido.PENDIENTE);

        List<PedidoResponseDto> pedidos = Arrays.asList(pedido);
        when(pedidoService.getPedidosByUsuario(1L)).thenReturn(pedidos);

        MockHttpServletResponse response = mockMvc.perform(get("/api/pedidos/usuario/1"))
                .andReturn().getResponse();

        List<PedidoResponseDto> res = mapper.readValue(
                contentAsUtf8(response),
                mapper.getTypeFactory().constructCollectionType(List.class, PedidoResponseDto.class)
        );

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertTrue(response.getContentType() != null && response.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE)),
                () -> assertEquals(1, res.size())
        );

        verify(pedidoService, times(1)).getPedidosByUsuario(1L);
    }
}
