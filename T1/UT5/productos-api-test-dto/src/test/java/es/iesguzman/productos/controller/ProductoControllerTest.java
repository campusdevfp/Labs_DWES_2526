package es.iesguzman.productos.controller;

import es.iesguzman.productos.domain.Producto;
import es.iesguzman.productos.dto.ProductoRequestDto;
import es.iesguzman.productos.dto.ProductoResponseDto;
import es.iesguzman.productos.mapper.ProductoMapper;
import es.iesguzman.productos.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService service;

    @MockBean
    private ProductoMapper mapper;

    @Test
    void testGetById() throws Exception {
        Producto p = new Producto(1L, "Prod", 10.0, 5, "cat");
        when(service.findById(1L)).thenReturn(p);
        when(mapper.toResponse(any(Producto.class))).thenAnswer(invocation -> {
            Producto prod = invocation.getArgument(0);
            return new ProductoResponseDto(prod.getId(), prod.getNombre(), prod.getPrecio(), prod.getStock(), prod.getCategoria());
        });

        MvcResult result = mockMvc.perform(get("/api/productos/1")).andReturn();

        assertAll(
            () -> assertEquals(200, result.getResponse().getStatus()),
            () -> assertTrue(result.getResponse().getContentAsString().contains("\"id\":1")),
            () -> assertTrue(result.getResponse().getContentAsString().contains("\"nombre\":\"Prod\""))
        );
    }

    @Test
    void testCreate() throws Exception {
        Producto p = new Producto(1L, "Nuevo", 15.0, 3, "cat");
        when(mapper.toModel(any(ProductoRequestDto.class))).thenReturn(new Producto(null, "Nuevo", 15.0, 3, "cat"));
        when(service.save(any(Producto.class))).thenReturn(p);
        when(mapper.toResponse(any(Producto.class))).thenAnswer(invocation -> {
            Producto prod = invocation.getArgument(0);
            return new ProductoResponseDto(prod.getId(), prod.getNombre(), prod.getPrecio(), prod.getStock(), prod.getCategoria());
        });

        String json = "{\"nombre\":\"Nuevo\",\"precio\":15.0,\"stock\":3,\"categoria\":\"cat\"}";

        MvcResult result = mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)).andReturn();

        assertAll(
            () -> assertEquals(201, result.getResponse().getStatus()),
            () -> assertTrue(result.getResponse().getContentAsString().contains("\"id\":1"))
        );
    }



    @Test
    void testDelete() throws Exception {
        doNothing().when(service).delete(1L);

        MvcResult result = mockMvc.perform(delete("/api/productos/1")).andReturn();

        assertEquals(204, result.getResponse().getStatus());
    }

    @Test
    void testGetByCategoria() throws Exception {
        List<Producto> list = List.of(new Producto(1L, "Prod", 10.0, 5, "cat"));
        when(service.findByCategoria("cat")).thenReturn(list);
        when(mapper.toResponse(any(Producto.class))).thenAnswer(invocation -> {
            Producto prod = invocation.getArgument(0);
            return new ProductoResponseDto(prod.getId(), prod.getNombre(), prod.getPrecio(), prod.getStock(), prod.getCategoria());
        });

        MvcResult result = mockMvc.perform(get("/api/productos/categoria/cat")).andReturn();

        assertAll(
            () -> assertEquals(200, result.getResponse().getStatus()),
            () -> assertTrue(result.getResponse().getContentAsString().contains("\"id\":1"))
        );
    }

    @Test
    void testUpdate() throws Exception {
        Producto p = new Producto(1L, "Updated", 20.0, 4, "cat");
        when(mapper.toModel(any(ProductoRequestDto.class))).thenReturn(new Producto(null, "Updated", 20.0, 4, "cat"));
        when(service.update(any(Producto.class))).thenReturn(p);
        when(mapper.toResponse(any(Producto.class))).thenAnswer(invocation -> {
            Producto prod = invocation.getArgument(0);
            return new ProductoResponseDto(prod.getId(), prod.getNombre(), prod.getPrecio(), prod.getStock(), prod.getCategoria());
        });

        String json = "{\"nombre\":\"Updated\",\"precio\":20.0,\"stock\":4,\"categoria\":\"cat\"}";

        MvcResult result = mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)).andReturn();

        assertAll(
            () -> assertEquals(200, result.getResponse().getStatus()),
            () -> assertTrue(result.getResponse().getContentAsString().contains("\"nombre\":\"Updated\""))
        );
    }
}
