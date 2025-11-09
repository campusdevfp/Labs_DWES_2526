package e2e;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = TaskManagerApplication.class)
@AutoConfigureMockMvc
@Transactional
class TaskControllerE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository repository;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        repository.saveAll(List.of(
                new Task(null, "Configurar CI", "GitHub Actions", "pendiente"),
                new Task(null, "Escribir tests", "JUnit 5 y Mockito", "completada")
        ));
    }

    @Test
    void getAll_ShouldReturnAllTasks() throws Exception {
        mockMvc.perform(get("/api/tasks")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].titulo", is("Configurar CI")))
                .andExpect(jsonPath("$[1].estado", is("completada")));
    }

    @Test
    void getById_ShouldReturnOneTask() throws Exception {
        var task = repository.findAll().get(0);

        mockMvc.perform(get("/api/tasks/{id}", task.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo", is(task.getTitulo())))
                .andExpect(jsonPath("$.descripcion", is(task.getDescripcion())));
    }

    @Test
    void getById_ShouldReturnNotFound_WhenInvalidId() throws Exception {
        mockMvc.perform(get("/api/tasks/99999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void postTask_ShouldCreateNewTask() throws Exception {
        Task nueva = new Task(null, "Nueva tarea", "Con descripción", "pendiente");
        String body = mapper.writeValueAsString(nueva);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo", is("Nueva tarea")))
                .andExpect(jsonPath("$.estado", is("pendiente")));
    }

    @Test
    void postTask_ShouldReturnBadRequest_WhenMissingTitle() throws Exception {
        Task invalid = new Task(null, "", "sin título", "pendiente");
        String body = mapper.writeValueAsString(invalid);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}