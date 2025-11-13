package es.iesguzman.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.iesguzman.demo.controllers.ClientesRestController;
import es.iesguzman.demo.models.Cliente;
import es.iesguzman.demo.services.ClienteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientesRestController.class)
class ClientesRestControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllShouldReturn200AndJsonArray() throws Exception {
        when(service.findAll()).thenReturn(List.of(new Cliente()));

        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void createClienteShouldReturn201() throws Exception {
        Cliente c = new Cliente();
        c.setNombre("Nuevo");
        c.setEmail("nuevo@d.com");

        when(service.save(org.mockito.ArgumentMatchers.any(Cliente.class))).thenReturn(c);

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(c)))
                .andExpect(status().isCreated());
    }
}

