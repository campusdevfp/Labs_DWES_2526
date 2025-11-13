package app.service;

import app.model.Task;
import app.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TaskServiceImplNoMocksTest {

    @Autowired
    private TaskRepository repository;

    @Test
    void findAll() {
        repository.deleteAll();
        repository.saveAll(List.of(
                new Task(null, "Configurar CI", "Configurar GitHub Actions", "pendiente"),
                new Task(null, "Escribir tests", "JUnit y Mockito", "completada")
        ));
        TaskServiceImpl service = new TaskServiceImpl(repository);

        var list = service.findAll();

        assertAll(
                () -> assertNotNull(list),
                () -> assertEquals(2, list.size())
        );
    }

    @Test
    void findById() {
        repository.deleteAll();
        var saved = repository.save(new Task(null, "Configurar CI", "Configurar GitHub Actions", "pendiente"));
        TaskServiceImpl service = new TaskServiceImpl(repository);

        var task = service.findById(saved.getId());

        assertAll(
                () -> assertNotNull(task),
                () -> assertEquals("Configurar CI", task.getTitulo())
        );
    }


}




