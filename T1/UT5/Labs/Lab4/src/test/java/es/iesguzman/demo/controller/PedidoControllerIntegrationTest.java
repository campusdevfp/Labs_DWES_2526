package es.iesguzman.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.iesguzman.demo.dto.PedidoRequestDto;
import es.iesguzman.demo.dto.PedidoResponseDto;
import es.iesguzman.demo.exception.PedidoNotFoundException;
import es.iesguzman.demo.model.EstadoPedido;
import es.iesguzman.demo.service.PedidoService;
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
 * Test de integración del controlador PedidoController usando MockMvc.
 * Usa @WebMvcTest para cargar solo el controlador sin el contexto completo.
 */
@WebMvcTest(PedidoController.class)
public class PedidoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PedidoService pedidoService;

    private final ObjectMapper mapper = new ObjectMapper();
    private PedidoResponseDto pedidoDto;
    private final String endpoint = "/api/pedidos";

    @BeforeEach
    void setUp() {
        pedidoDto = new PedidoResponseDto();
        pedidoDto.setId(1L);
        pedidoDto.setCantidad(2);
        pedidoDto.setTotal(1999.98);
        pedidoDto.setEstado(EstadoPedido.PENDIENTE);
    }

    /**
     * Test: GET /api/pedidos debe devolver lista de pedidos (200).
     */
    @Test
    public void testGetAllPedidosRetorna200() throws Exception {
        PedidoResponseDto pedido2 = new PedidoResponseDto();
        pedido2.setId(2L);
        pedido2.setCantidad(1);
        pedido2.setTotal(25.5);
        pedido2.setEstado(EstadoPedido.ENVIADO);

        List<PedidoResponseDto> pedidos = List.of(pedidoDto, pedido2);
        when(pedidoService.getAllPedidos()).thenReturn(pedidos);

        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("PENDIENTE")),
                () -> assertTrue(response.getContentAsString().contains("ENVIADO"))
        );

        verify(pedidoService, times(1)).getAllPedidos();
    }

    /**
     * Test: GET /api/pedidos/{id} debe devolver un pedido cuando existe (200).
     */
    @Test
    public void testGetPedidoByIdRetorna200() throws Exception {
        when(pedidoService.getPedidoById(1L)).thenReturn(pedidoDto);

        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint + "/1")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("PENDIENTE"))
        );

        verify(pedidoService, times(1)).getPedidoById(1L);
    }

    /**
     * Test: GET /api/pedidos/{id} debe devolver 404 cuando no existe.
     */
    @Test
    public void testGetPedidoByIdRetorna404() throws Exception {
        when(pedidoService.getPedidoById(-1L)).thenThrow(new PedidoNotFoundException("Pedido no encontrado"));

        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint + "/-1")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

        verify(pedidoService, times(1)).getPedidoById(-1L);
    }

    /**
     * Test: POST /api/pedidos debe crear un pedido con datos válidos (201).
     */
    @Test
    public void testCreatePedidoConDatosValidosRetorna201() throws Exception {
        PedidoRequestDto requestDto = new PedidoRequestDto();
        requestDto.setUsuarioId(1L);
        requestDto.setProductoId(1L);
        requestDto.setCantidad(3);

        PedidoResponseDto createdDto = new PedidoResponseDto();
        createdDto.setId(10L);
        createdDto.setCantidad(3);
        createdDto.setTotal(2999.97);
        createdDto.setEstado(EstadoPedido.PENDIENTE);

        when(pedidoService.createPedido(any(PedidoRequestDto.class))).thenReturn(createdDto);

        String json = mapper.writeValueAsString(requestDto);
        MockHttpServletResponse response = mockMvc.perform(
                        post(endpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("PENDIENTE"))
        );

        verify(pedidoService, times(1)).createPedido(any(PedidoRequestDto.class));
    }
}
