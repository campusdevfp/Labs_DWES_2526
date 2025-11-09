package service;


import app.model.Task;
import app.repository.TaskRepository;
import app.service.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskServiceImpl service;

    private Map<Long, Task> tareas;

    @BeforeEach
    void setUp() {
        tareas = Map.of(
                1L, new Task(1L, "Configurar CI", "Configurar GitHub Actions", "pendiente"),
                2L, new Task(2L, "Escribir tests", "JUnit y Mockito", "completada")
        );
    }

    @Test
    void findAll() {
        when(repository.findAll()).thenReturn(List.copyOf(tareas.values()));

        var list = service.findAll();

        assertAll(
                () -> assertNotNull(list),
                () -> assertEquals(2, list.size())
        );

//        verify(repository, times(1)).findAll();
    }

    @Test
    void findById() {
        when(repository.findById(1L)).thenReturn(Optional.of(tareas.get(1L)));

        var tarea = service.findById(1L);

        assertAll(
                () -> assertNotNull(tarea),
                () -> assertEquals("Configurar CI", tarea.getTitulo())
        );

//        verify(repository, times(1)).findById(1L);
    }
}
