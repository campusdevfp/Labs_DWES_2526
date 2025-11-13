package app.service;

import app.model.Task;
import app.repository.TaskRepository;
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
class TaskServiceImplWithMocksTest {

    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskServiceImpl service;

    private Map<Long, Task> data;

    @BeforeEach
    void setUp() {
        data = Map.of(
                1L, new Task(1L, "Configurar CI", "Configurar GitHub Actions", "pendiente"),
                2L, new Task(2L, "Escribir tests", "JUnit y Mockito", "completada")
        );
    }

    @Test
    void findAll_returnsAllTasks() {
        when(repository.findAll()).thenReturn(List.copyOf(data.values()));

        var list = service.findAll();

        assertAll(
                () -> assertNotNull(list),
                () -> assertEquals(2, list.size())
        );

    }

    @Test
    void findById_returnsTask() {
        when(repository.findById(1L)).thenReturn(Optional.of(data.get(1L)));

        var task = service.findById(1L);

        assertAll(
                () -> assertNotNull(task),
                () -> assertEquals("Configurar CI", task.getTitulo())
        );

    }



}

