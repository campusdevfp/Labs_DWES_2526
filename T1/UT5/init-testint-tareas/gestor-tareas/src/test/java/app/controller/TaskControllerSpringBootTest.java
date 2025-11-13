package app.controller;

import app.TaskManagerApplication;
import app.model.Task;
import app.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TaskManagerApplication.class)
class TaskControllerSpringBootTest {

    @Autowired
    private TaskController controller;

    @Autowired
    private TaskRepository repository;

    @Test
    void findAll() {
        repository.deleteAll();
        repository.saveAll(List.of(
                new Task(null, "Configurar CI", "Configurar GitHub Actions", "pendiente"),
                new Task(null, "Escribir tests", "JUnit y Mockito", "completada")
        ));

        var list = controller.findAll();

        assertAll(
                () -> assertNotNull(list),
                () -> assertEquals(2, list.size())
        );
    }

    @Test
    void findById() {
        repository.deleteAll();
        var saved = repository.save(new Task(null, "Configurar CI", "Configurar GitHub Actions", "pendiente"));

        var tarea = controller.findById(saved.getId());

        assertAll(
                () -> assertNotNull(tarea),
                () -> assertEquals("Configurar CI", tarea.getTitulo())
        );
    }

    @Test
    void findByIdNotFound() {
        repository.deleteAll();

        var res = assertThrows(ResponseStatusException.class, () -> controller.findById(-100L));
        assertTrue(res.getMessage().contains("No se ha encontrado la tarea con id: -100"));
    }

    @Test
    void create() {
        repository.deleteAll();
        var nueva = new Task(null, "Nueva tarea", "Descripción", null);

        var saved = controller.create(nueva);

        assertAll(
                () -> assertNotNull(saved.getId()),
                () -> assertEquals("pendiente", saved.getEstado()),
                () -> assertTrue(repository.findById(saved.getId()).isPresent())
        );
    }
}

