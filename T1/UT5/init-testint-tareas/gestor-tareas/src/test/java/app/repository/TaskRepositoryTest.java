package app.repository;

import app.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository repository;

    @Test
    void findAll_returnsAllTasks() {
        repository.deleteAll();
        repository.saveAll(List.of(
                new Task(null, "Configurar CI", "Configurar GitHub Actions", "pendiente"),
                new Task(null, "Escribir tests", "JUnit y Mockito", "completada")
        ));

        var list = repository.findAll();

        assertAll(
                () -> assertNotNull(list),
                () -> assertEquals(2, list.size())
        );
    }

    @Test
    void findById_returnsTask() {
        repository.deleteAll();
        var saved = repository.save(new Task(null, "Configurar CI", "Configurar GitHub Actions", "pendiente"));

        var task = repository.findById(saved.getId());

        assertAll(
                () -> assertTrue(task.isPresent()),
                () -> assertEquals("Configurar CI", task.get().getTitulo())
        );
    }

    @Test
    void findById_notFound_returnsEmpty() {
        repository.deleteAll();

        var task = repository.findById(999L);

        assertFalse(task.isPresent());
    }

    @Test
    void save_assignsIdAndPersists() {
        repository.deleteAll();
        var task = new Task(null, "Nueva tarea", "Descripción", "pendiente");

        var saved = repository.save(task);

        assertAll(
                () -> assertNotNull(saved.getId()),
                () -> assertEquals("Nueva tarea", saved.getTitulo()),
                () -> assertTrue(repository.findById(saved.getId()).isPresent())
        );
    }

    @Test
    void findByEstadoIgnoreCase_returnsMatchingTasks() {
        repository.deleteAll();
        repository.saveAll(List.of(
                new Task(null, "Task 1", "Desc", "pendiente"),
                new Task(null, "Task 2", "Desc", "completada"),
                new Task(null, "Task 3", "Desc", "PENDIENTE") // case insensitive
        ));

        var list = repository.findByEstadoIgnoreCase("pendiente");

        assertAll(
                () -> assertNotNull(list),
                () -> assertEquals(2, list.size()),
                () -> assertTrue(list.stream().allMatch(t -> "pendiente".equalsIgnoreCase(t.getEstado())))
        );
    }
}
