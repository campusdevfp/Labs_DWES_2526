package app.e2e;

import app.TaskManagerApplication;
import app.model.Task;
import app.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TaskManagerApplication.class)
@AutoConfigureMockMvc
class TaskControllerMockMvcIntegrationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository repository;

    private Task task = new Task(1L, "Configurar CI", "Configurar GitHub Actions", "pendiente");
    private String myEndpoint = "/api/tasks";

    @Test
    void findAllTest() throws Exception {
        repository.deleteAll();
        repository.saveAll(List.of(
                new Task(null, "Configurar CI", "Configurar GitHub Actions", "pendiente"),
                new Task(null, "Escribir tests", "JUnit y Mockito", "completada")
        ));

        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        List<Task> res = mapper.readValue(response.getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, Task.class));

        assertAll(
                () -> assertEquals(response.getStatus(), 200),
                () -> assertTrue(response.getContentAsString().contains("\"titulo\":\"Configurar CI\"")),
                () -> assertTrue(res.size() > 0),
                () -> assertTrue(res.stream().anyMatch(t -> t.getTitulo().equals("Configurar CI")))
        );
    }

    @Test
    void findByIdTest() throws Exception {
        repository.deleteAll();
        var saved = repository.save(new Task(null, "Configurar CI", "Configurar GitHub Actions", "pendiente"));

        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint + "/" + saved.getId())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Task res = mapper.readValue(response.getContentAsString(), Task.class);

        assertAll(
                () -> assertEquals(response.getStatus(), 200),
                () -> assertEquals(res.getId(), saved.getId())
        );
    }

    @Test
    void findByIdNotFound() throws Exception {
        repository.deleteAll();

        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint + "/" + -1000L)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(response.getStatus(), 404)
        );
    }

    @Test
    void createTest() throws Exception {
        repository.deleteAll();
        Task nueva = new Task(null, "Nueva tarea", "Descripción", null);
        String json = mapper.writeValueAsString(nueva);

        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andReturn().getResponse();

        Task res = mapper.readValue(response.getContentAsString(), Task.class);

        assertAll(
                () -> assertEquals(response.getStatus(), 201),
                () -> assertNotNull(res.getId()),
                () -> assertEquals("pendiente", res.getEstado())
        );
    }
}
