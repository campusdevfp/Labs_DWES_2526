package app.e2e;

import app.TaskManagerApplication;
import app.model.Task;
import app.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Pruebas E2E reales del controlador sin usar mocks del servicio.
 * Sin @Transactional: los datos persisten entre pruebas, por eso se hace deleteAll() en @BeforeEach.
 */
@SpringBootTest(classes = TaskManagerApplication.class)
@AutoConfigureMockMvc
class TaskControllerEENoMocksTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository repository;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        repository.deleteAll(); // asegura estado limpio sin rollback automático
    }

    @Test
    void getAll_devuelveLista() throws Exception {
        repository.saveAll(List.of(
                nueva("Configurar CI", "Configurar GitHub Actions", "pendiente"),
                nueva("Escribir tests", "JUnit y Mockito", "completada")
        ));

        MockHttpServletResponse response = mockMvc.perform(
                        get("/api/tasks").accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        List<Task> res = mapper.readValue(response.getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, Task.class));
        assertAll(
                () -> assertNotNull(res),
                () -> assertEquals(2, res.size()),
                () -> assertTrue(res.stream().anyMatch(t -> t.getTitulo().equals("Configurar CI")))
        );
    }

    @Test
    void getById_devuelveTarea() throws Exception {
        Task guardada = repository.save(nueva("Configurar CI", "Configurar GitHub Actions", "pendiente"));

        MockHttpServletResponse response = mockMvc.perform(
                        get("/api/tasks/{id}", guardada.getId()).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        Task res = mapper.readValue(response.getContentAsString(), Task.class);
        assertAll(
                () -> assertNotNull(res),
                () -> assertEquals(guardada.getId(), res.getId()),
                () -> assertEquals("Configurar CI", res.getTitulo())
        );
    }

    @Test
    void getById_noExiste_404() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        get("/api/tasks/{id}", 9999L).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(404, response.getStatus());
    }

    @Test
    void post_creaConEstadoPorDefecto() throws Exception {
        Task entrada = nueva("Nueva tarea", "Descripción", null);
        String json = mapper.writeValueAsString(entrada);

        MockHttpServletResponse response = mockMvc.perform(
                        post("/api/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andReturn().getResponse();

        assertEquals(201, response.getStatus());
        Task res = mapper.readValue(response.getContentAsString(), Task.class);
        assertAll(
                () -> assertNotNull(res.getId()),
                () -> assertEquals("Nueva tarea", res.getTitulo()),
                () -> assertEquals("pendiente", res.getEstado())
        );
    }

    @Test
    void post_sinTitulo_400() throws Exception {
        Task entrada = nueva(null, "Descripción", null); // título inválido
        String json = mapper.writeValueAsString(entrada);

        MockHttpServletResponse response = mockMvc.perform(
                        post("/api/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andReturn().getResponse();

        assertEquals(400, response.getStatus());
    }

    // Helper para crear instancia sin fijar id manualmente
    private Task nueva(String titulo, String descripcion, String estado) {
        return new Task(null, titulo, descripcion, estado);
    }
}
