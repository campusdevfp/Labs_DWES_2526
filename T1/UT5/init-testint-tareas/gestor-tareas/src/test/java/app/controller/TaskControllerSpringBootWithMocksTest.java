package app.controller;

import app.TaskManagerApplication;
import app.model.Task;
import app.service.TaskServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = TaskManagerApplication.class)
class TaskControllerSpringBootWithMocksTest {

    @Autowired
    private TaskController controller;

    @MockBean
    private TaskServiceImpl service;

    @Test
    void findAll() {
        when(service.findAll()).thenReturn(List.of(
                new Task(1L, "Configurar CI", "Configurar GitHub Actions", "pendiente"),
                new Task(2L, "Escribir tests", "JUnit y Mockito", "completada")
        ));

        var list = controller.findAll();

        assertAll(
                () -> assertNotNull(list),
                () -> assertEquals(2, list.size())
        );
        verify(service, times(1)).findAll();
        verifyNoMoreInteractions(service);
    }

    @Test
    void findById() {
        when(service.findById(1L)).thenReturn(new Task(1L, "Configurar CI", "Configurar GitHub Actions", "pendiente"));

        var tarea = controller.findById(1L);

        assertAll(
                () -> assertNotNull(tarea),
                () -> assertEquals("Configurar CI", tarea.getTitulo())
        );
        verify(service, times(1)).findById(1L);
        verifyNoMoreInteractions(service);
    }

    @Test
    void findByIdNotFound() {
        when(service.findById(-100L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND,
                "No se ha encontrado la tarea con id: -100"));

        var res = assertThrows(ResponseStatusException.class, () -> controller.findById(-100L));
        assertTrue(res.getMessage().contains("No se ha encontrado la tarea con id: -100"));

        verify(service, times(1)).findById(-100L);
        verifyNoMoreInteractions(service);
    }

    @Test
    void create() {
        var entrada = new Task(null, "Nueva tarea", "Descripción", null);
        var devuelta = new Task(10L, "Nueva tarea", "Descripción", "pendiente");
        when(service.create(any(Task.class))).thenReturn(devuelta);

        var saved = controller.create(entrada);

        assertAll(
                () -> assertNotNull(saved.getId()),
                () -> assertEquals("pendiente", saved.getEstado())
        );
        verify(service, times(1)).create(any(Task.class));
        verifyNoMoreInteractions(service);
    }
}

