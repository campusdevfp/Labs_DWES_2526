package com.example.funkos.repository;

import com.example.funkos.model.Funko;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FunkoRepositoryTest {

    @Autowired
    private FunkoRepository repository;

    @Test
    void shouldLoadDataFromCsvOnStartup() {
        long count = repository.count();
        assertThat(count).isGreaterThan(0L);
    }

    @Test
    void findByCategoriaIgnoreCase_returnsGeneral() {
        List<Funko> general = repository.findByCategoriaIgnoreCase("general");
        assertThat(general).isNotEmpty();
        assertThat(general).allMatch(f -> "general".equalsIgnoreCase(f.getCategoria()));
    }

    @Test
    void save_persistsEntityWithGeneratedId() {
        Funko f = new Funko();
        f.setNombre("Test Nombre");
        f.setModelo("TEST");
        f.setPrecio(9.99);
        f.setCantidad(1);
        f.setImagen("imagen.jpg");
        f.setCategoria("general");
        f.setFechaLanzamiento(java.time.LocalDate.now());

        Funko saved = repository.save(f);
        assertThat(saved.getId()).isNotNull();
        assertThat(repository.findByCategoriaIgnoreCase("general")).anyMatch(x -> x.getId().equals(saved.getId()));
    }
}

