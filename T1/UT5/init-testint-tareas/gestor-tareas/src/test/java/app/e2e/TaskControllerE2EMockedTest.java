package app.e2e;

import app.TaskManagerApplication;
import app.model.Task;
import app.service.TaskServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(classes = TaskManagerApplication.class)
@AutoConfigureMockMvc
class TaskControllerE2EMockedTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskServiceImpl service;

    @Test
    void getAll_usesMockService() throws Exception {
        when(service.findAll()).thenReturn(List.of(
                new Task(1L, "Configurar CI", "Configurar GitHub Actions", "pendiente"),
                new Task(2L, "Escribir tests", "JUnit y Mockito", "completada")
        ));

        MockHttpServletResponse response = mockMvc.perform(
                        get("/api/tasks")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        List<Task> res = mapper.readValue(response.getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, Task.class));

        assertAll(
                () -> assertEquals(response.getStatus(), 200),
                () -> assertNotNull(res),
                () -> assertEquals(2, res.size())
        );
        verify(service, times(1)).findAll();
        verifyNoMoreInteractions(service);
    }

    @Test
    void getById_usesMockService() throws Exception {
        when(service.findById(10L)).thenReturn(new Task(10L, "Configurar CI", "Configurar GitHub Actions", "pendiente"));

        MockHttpServletResponse response = mockMvc.perform(
                        get("/api/tasks/{id}", 10)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Task res = mapper.readValue(response.getContentAsString(), Task.class);

        assertAll(
                () -> assertEquals(response.getStatus(), 200),
                () -> assertNotNull(res),
                () -> assertEquals("Configurar CI", res.getTitulo())
        );
        verify(service, times(1)).findById(10L);
        verifyNoMoreInteractions(service);
    }

    @Test
    void getById_notFound_usesMockService() throws Exception {
        when(service.findById(999L)).thenThrow(new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "No se ha encontrado la tarea con id: 999"));

        MockHttpServletResponse response = mockMvc.perform(
                        get("/api/tasks/{id}", 999)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(response.getStatus(), 404);
        verify(service, times(1)).findById(999L);
        verifyNoMoreInteractions(service);
    }

    @Test
    void post_create_usesMockService() throws Exception {
        var entrada = new Task(null, "Nueva tarea", "Descripción", null);
        var devuelta = new Task(55L, "Nueva tarea", "Descripción", "pendiente");
        when(service.create(any(Task.class))).thenReturn(devuelta);

        String json = mapper.writeValueAsString(entrada);

        MockHttpServletResponse response = mockMvc.perform(
                        post("/api/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andReturn().getResponse();

        Task res = mapper.readValue(response.getContentAsString(), Task.class);

        assertAll(
                () -> assertEquals(response.getStatus(), 201),
                () -> assertNotNull(res),
                () -> assertEquals(55L, res.getId()),
                () -> assertEquals("pendiente", res.getEstado())
        );
        verify(service, times(1)).create(any(Task.class));
        verifyNoMoreInteractions(service);
    }
}
