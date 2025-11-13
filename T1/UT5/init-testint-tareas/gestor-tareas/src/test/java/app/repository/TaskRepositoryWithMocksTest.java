package app.repository;

import app.model.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskRepositoryWithMocksTest {

    @Mock
    private TaskRepository repository;

    @Test
    void findAll() {
        var mockedTasks = List.of(
                new Task(1L, "Configurar CI", "Configurar GitHub Actions", "pendiente"),
                new Task(2L, "Escribir tests", "JUnit y Mockito", "completada")
        );
        when(repository.findAll()).thenReturn(mockedTasks);

        var list = repository.findAll();

        assertAll(
                () -> assertNotNull(list),
                () -> assertEquals(2, list.size()),
                () -> assertEquals("Configurar CI", list.get(0).getTitulo())
        );
        verify(repository, times(1)).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void findById() {
        var mockedTask = new Task(1L, "Configurar CI", "Configurar GitHub Actions", "pendiente");
        when(repository.findById(1L)).thenReturn(Optional.of(mockedTask));

        var task = repository.findById(1L);

        assertAll(
                () -> assertTrue(task.isPresent()),
                () -> assertEquals("Configurar CI", task.get().getTitulo())
        );
        verify(repository, times(1)).findById(1L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void findById_notFound_returnsEmpty() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        var task = repository.findById(999L);

        assertFalse(task.isPresent());
        verify(repository, times(1)).findById(999L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void save_returnsMockedSavedTask() {
        var inputTask = new Task(null, "Nueva tarea", "Descripción", "pendiente");
        var savedTask = new Task(10L, "Nueva tarea", "Descripción", "pendiente");
        when(repository.save(inputTask)).thenReturn(savedTask);

        var result = repository.save(inputTask);

        assertAll(
                () -> assertNotNull(result.getId()),
                () -> assertEquals(10L, result.getId()),
                () -> assertEquals("Nueva tarea", result.getTitulo())
        );
        verify(repository, times(1)).save(inputTask);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void findByEstadoIgnoreCase_returnsMockedTasks() {
        var mockedTasks = List.of(
                new Task(1L, "Task 1", "Desc", "pendiente"),
                new Task(3L, "Task 3", "Desc", "PENDIENTE")
        );
        when(repository.findByEstadoIgnoreCase("pendiente")).thenReturn(mockedTasks);

        var list = repository.findByEstadoIgnoreCase("pendiente");

        assertAll(
                () -> assertNotNull(list),
                () -> assertEquals(2, list.size()),
                () -> assertTrue(list.stream().allMatch(t -> "pendiente".equalsIgnoreCase(t.getEstado())))
        );
        verify(repository, times(1)).findByEstadoIgnoreCase("pendiente");
        verifyNoMoreInteractions(repository);
    }
}
