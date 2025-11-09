package repository;


import app.TaskManagerApplication;
import app.controller.TaskController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TaskManagerApplication.class)
class TaskControllerIntegrationTest {

    @Autowired
    private TaskController controller;

    @Test
    void findAll() {
        var list = controller.findAll();

        assertAll(
                () -> assertNotNull(list),
                () -> assertTrue(list.size() >= 0)
        );
    }

    @Test
    void findByIdNotFound() {
        var ex = assertThrows(ResponseStatusException.class, () -> controller.findById(-100L));
        assertTrue(ex.getMessage().contains("No se ha encontrado la tarea"));
    }
}