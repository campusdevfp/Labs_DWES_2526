package com.example.funkos.controller;

import com.example.funkos.model.Funko;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FunkosRestControllerMockMvcIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    private final String endpoint = "/funkos";

    private Funko nuevoFunko(String nombre) {
        Funko f = new Funko();
        f.setNombre(nombre);
        f.setModelo("TEST");
        f.setPrecio(9.99);
        f.setCantidad(1);
        f.setImagen("img.jpg");
        f.setCategoria("general");
        f.setFechaLanzamiento(LocalDate.now());
        return f;
    }

    @Test
    @Order(1)
    void findAllTest() throws Exception {
        // Prepara: crea un funko para asegurar datos
        Funko toCreate = nuevoFunko("Funko MVC All");
        MockHttpServletResponse createRes = mockMvc.perform(
                        post(endpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(toCreate)))
                .andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), createRes.getStatus());

        // Consulta GET all
        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        List<Funko> res = Arrays.asList(mapper.readValue(response.getContentAsString(), Funko[].class));

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("\"nombre\":\"Funko MVC All\"")),
                () -> assertTrue(res.size() > 0),
                () -> assertTrue(res.stream().anyMatch(f -> "Funko MVC All".equals(f.getNombre())))
        );
    }

    @Test
    @Order(2)
    void findByIdTest() throws Exception {
        // Crea uno y obtén su id
        Funko toCreate = nuevoFunko("Funko MVC ById");
        MockHttpServletResponse createRes = mockMvc.perform(
                        post(endpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(toCreate)))
                .andReturn().getResponse();
        Funko creado = mapper.readValue(createRes.getContentAsString(), Funko.class);

        // GET por id
        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint + "/" + creado.getId())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Funko res = mapper.readValue(response.getContentAsString(), Funko.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(creado.getId(), res.getId()),
                () -> assertEquals("Funko MVC ById", res.getNombre())
        );
    }

    @Test
    @Order(3)
    void findByIdNotFound() throws Exception {
        // GET de un id inexistente
        UUID missing = UUID.randomUUID();
        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint + "/" + missing)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Nota: el controlador actual propaga la excepción como 500 (no 404)
        assertAll(
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus())
        );
    }

    @Test
    @Order(4)
    void createTest() throws Exception {
        Funko toCreate = nuevoFunko("Funko MVC Create");
        MockHttpServletResponse response = mockMvc.perform(
                        post(endpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(toCreate)))
                .andReturn().getResponse();

        Funko res = mapper.readValue(response.getContentAsString(), Funko.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertNotNull(res.getId()),
                () -> assertEquals("Funko MVC Create", res.getNombre())
        );
    }

    @Test
    @Order(5)
    void updateTest() throws Exception {
        // Crea base
        Funko base = nuevoFunko("Funko MVC Update Base");
        MockHttpServletResponse createRes = mockMvc.perform(
                        post(endpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(base)))
                .andReturn().getResponse();
        Funko creado = mapper.readValue(createRes.getContentAsString(), Funko.class);

        // Cambios
        Funko cambios = nuevoFunko("Funko MVC Updated");
        cambios.setModelo("TEST2");
        cambios.setPrecio(20.0);
        cambios.setCantidad(3);
        cambios.setImagen("img2.jpg");

        MockHttpServletResponse response = mockMvc.perform(
                        put(endpoint + "/" + creado.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(cambios)))
                .andReturn().getResponse();

        Funko res = mapper.readValue(response.getContentAsString(), Funko.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals("Funko MVC Updated", res.getNombre()),
                () -> assertEquals(creado.getId(), res.getId())
        );
    }

    @Test
    @Order(6)
    void patchTest() throws Exception {
        // Crea base
        Funko base = nuevoFunko("Funko MVC Patch Base");
        MockHttpServletResponse createRes = mockMvc.perform(
                        post(endpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(base)))
                .andReturn().getResponse();
        Funko creado = mapper.readValue(createRes.getContentAsString(), Funko.class);

        // Patch
        Map<String, Object> updates = Map.of(
                "nombre", "Funko MVC Patched",
                "precio", 15.5,
                "cantidad", 5
        );

        MockHttpServletResponse response = mockMvc.perform(
                        patch(endpoint + "/" + creado.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(updates)))
                .andReturn().getResponse();

        Funko res = mapper.readValue(response.getContentAsString(), Funko.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals("Funko MVC Patched", res.getNombre()),
                () -> assertEquals(15.5, res.getPrecio()),
                () -> assertEquals(5, res.getCantidad())
        );
    }

    @Test
    @Order(7)
    void deleteTest() throws Exception {
        // Crea base
        Funko base = nuevoFunko("Funko MVC Delete Base");
        MockHttpServletResponse createRes = mockMvc.perform(
                        post(endpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(base)))
                .andReturn().getResponse();
        Funko creado = mapper.readValue(createRes.getContentAsString(), Funko.class);

        // Delete
        MockHttpServletResponse response = mockMvc.perform(
                        delete(endpoint + "/" + creado.getId()))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals("Funko eliminado correctamente", response.getContentAsString())
        );
    }
}

