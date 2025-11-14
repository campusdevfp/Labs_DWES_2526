package com.example.funkos.controller;

import com.example.funkos.model.Funko;
import com.example.funkos.service.FunkoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.core.type.TypeReference;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(FunkosRestController.class)
class FunkosRestControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FunkoService service;

    private Funko funko(UUID id, String nombre, String categoria) {
        Funko f = new Funko();
        f.setId(id);
        f.setNombre(nombre);
        f.setModelo("TEST");
        f.setPrecio(10.0);
        f.setCantidad(1);
        f.setImagen("img.jpg");
        f.setCategoria(categoria);
        f.setFechaLanzamiento(LocalDate.now());
        f.setFechaCreacion(LocalDate.now());
        f.setFechaActualizacion(LocalDate.now());
        return f;
    }

    @Test
    void getAll_withoutCategoria_returnsList() throws Exception {
        List<Funko> datos = List.of(funko(UUID.randomUUID(), "A", "general"), funko(UUID.randomUUID(), "B", "general"));
        when(service.getAll(null)).thenReturn(datos);

        MvcResult res = mockMvc.perform(get("/funkos")).andReturn();
        assertEquals(200, res.getResponse().getStatus());
        String json = res.getResponse().getContentAsString();
        List<Funko> body = objectMapper.readValue(json, new TypeReference<List<Funko>>() {});
        assertEquals(2, body.size());
        assertEquals("A", body.get(0).getNombre());
    }

    @Test
    void getAll_withCategoria_returnsFilteredList() throws Exception {
        List<Funko> datos = List.of(funko(UUID.randomUUID(), "C", "general"));
        when(service.getAll("general")).thenReturn(datos);

        MvcResult res = mockMvc.perform(get("/funkos").param("categoria", "general")).andReturn();
        assertEquals(200, res.getResponse().getStatus());
        String json = res.getResponse().getContentAsString();
        List<Funko> body = objectMapper.readValue(json, new TypeReference<List<Funko>>() {});
        assertEquals(1, body.size());
        assertTrue(body.stream().allMatch(f -> "general".equalsIgnoreCase(f.getCategoria())));
    }

    @Test
    void getById_found_returns200AndBody() throws Exception {
        UUID id = UUID.randomUUID();
        Funko f = funko(id, "Encontrado", "general");
        when(service.getById(id)).thenReturn(f);

        MvcResult res = mockMvc.perform(get("/funkos/{id}", id)).andReturn();
        assertEquals(200, res.getResponse().getStatus());
        Funko body = objectMapper.readValue(res.getResponse().getContentAsString(), Funko.class);
        assertEquals(id, body.getId());
        assertEquals("Encontrado", body.getNombre());
    }

    @Test
    void getById_notFound_bubblesUpAs500() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.getById(id)).thenThrow(new NoSuchElementException("Funko no encontrado"));

        MvcResult res = mockMvc.perform(get("/funkos/{id}", id)).andReturn();
        assertEquals(500, res.getResponse().getStatus());
    }

    @Test
    void create_returnsCreatedFunko() throws Exception {
        Funko input = new Funko();
        input.setNombre("Nuevo");
        input.setModelo("TEST");
        input.setPrecio(12.3);
        input.setCantidad(2);
        input.setImagen("img.jpg");
        input.setCategoria("general");
        input.setFechaLanzamiento(LocalDate.now());

        Funko returned = funko(UUID.randomUUID(), "Nuevo", "general");
        when(service.create(any(Funko.class))).thenReturn(returned);

        MvcResult res = mockMvc.perform(post("/funkos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andReturn();
        assertEquals(200, res.getResponse().getStatus());
        Funko body = objectMapper.readValue(res.getResponse().getContentAsString(), Funko.class);
        assertEquals(returned.getId(), body.getId());
        assertEquals("Nuevo", body.getNombre());
    }

    @Test
    void update_returnsUpdatedFunko() throws Exception {
        UUID id = UUID.randomUUID();
        Funko bodyReq = new Funko();
        bodyReq.setNombre("Actualizado");
        bodyReq.setModelo("TEST2");
        bodyReq.setPrecio(20.0);
        bodyReq.setCantidad(3);
        bodyReq.setImagen("img2.jpg");
        bodyReq.setCategoria("general");
        bodyReq.setFechaLanzamiento(LocalDate.now());

        Funko returned = funko(id, "Actualizado", "general");
        when(service.update(eq(id), any(Funko.class))).thenReturn(returned);

        MvcResult res = mockMvc.perform(put("/funkos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bodyReq)))
                .andReturn();
        assertEquals(200, res.getResponse().getStatus());
        Funko body = objectMapper.readValue(res.getResponse().getContentAsString(), Funko.class);
        assertEquals(id, body.getId());
        assertEquals("Actualizado", body.getNombre());
    }

    @Test
    void patch_returnsPatchedFunko() throws Exception {
        UUID id = UUID.randomUUID();
        Map<String, Object> updates = Map.of(
                "nombre", "Patch Hecho",
                "precio", 15.5,
                "cantidad", 5
        );
        Funko returned = funko(id, "Patch Hecho", "general");
        returned.setPrecio(15.5);
        returned.setCantidad(5);
        when(service.patch(eq(id), anyMap())).thenReturn(returned);

        MvcResult res = mockMvc.perform(patch("/funkos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andReturn();
        assertEquals(200, res.getResponse().getStatus());
        Funko body = objectMapper.readValue(res.getResponse().getContentAsString(), Funko.class);
        assertEquals("Patch Hecho", body.getNombre());
        assertEquals(15.5, body.getPrecio());
        assertEquals(5, body.getCantidad());
    }

    @Test
    void delete_returnsConfirmationMessage() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(service).delete(id);

        MvcResult res = mockMvc.perform(delete("/funkos/{id}", id)).andReturn();
        assertEquals(200, res.getResponse().getStatus());
        assertEquals("Funko eliminado correctamente", res.getResponse().getContentAsString());
    }
}
