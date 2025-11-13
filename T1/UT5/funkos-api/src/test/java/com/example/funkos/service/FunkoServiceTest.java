package com.example.funkos.service;

import com.example.funkos.model.Funko;
import com.example.funkos.repository.FunkoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FunkoServiceTest {

    @Autowired
    private FunkoService service;

    @Autowired
    private FunkoRepository repository;

    @Test
    void contextLoadsAndCsvIsImported() {
        List<Funko> all = service.getAll(null);
        assertThat(all).isNotEmpty();
        assertThat(all.size()).isEqualTo((int) repository.count());
    }

    @Test
    void getAllByCategoria_generalReturnsOnlyGeneral() {
        List<Funko> general = service.getAll("general");
        assertThat(general).isNotEmpty();
        assertThat(general).allMatch(f -> "general".equalsIgnoreCase(f.getCategoria()));
    }

    @Test
    void createAndGetByIdWorks() {
        Funko nuevo = new Funko();
        nuevo.setNombre("Nuevo Funko");
        nuevo.setModelo("TEST");
        nuevo.setPrecio(12.34);
        nuevo.setCantidad(2);
        nuevo.setImagen("img.jpg");
        nuevo.setCategoria("general");
        nuevo.setFechaLanzamiento(LocalDate.now());

        Funko creado = service.create(nuevo);
        assertThat(creado.getId()).isNotNull();
        Funko recuperado = service.getById(creado.getId());
        assertThat(recuperado.getNombre()).isEqualTo("Nuevo Funko");
        assertThat(recuperado.getFechaCreacion()).isNotNull();
    }

    @Test
    void updatePreservesFechaCreacion() {
        Funko base = new Funko();
        base.setNombre("Base");
        base.setModelo("TEST");
        base.setPrecio(10.0);
        base.setCantidad(1);
        base.setImagen("img.jpg");
        base.setCategoria("general");
        base.setFechaLanzamiento(LocalDate.now());
        Funko saved = service.create(base);

        LocalDate fechaCreacionOriginal = saved.getFechaCreacion();

        Funko cambios = new Funko();
        cambios.setNombre("Actualizado");
        cambios.setModelo("TEST2");
        cambios.setPrecio(20.0);
        cambios.setCantidad(3);
        cambios.setImagen("img2.jpg");
        cambios.setCategoria("general");
        cambios.setFechaLanzamiento(LocalDate.now());

        Funko actualizado = service.update(saved.getId(), cambios);
        assertThat(actualizado.getNombre()).isEqualTo("Actualizado");
        assertThat(actualizado.getFechaCreacion()).isEqualTo(fechaCreacionOriginal);
        assertThat(actualizado.getId()).isEqualTo(saved.getId());
    }

    @Test
    void patchUpdatesSelectedFields() {
        Funko base = new Funko();
        base.setNombre("Patch Base");
        base.setModelo("TEST");
        base.setPrecio(10.0);
        base.setCantidad(1);
        base.setImagen("img.jpg");
        base.setCategoria("general");
        base.setFechaLanzamiento(LocalDate.now());
        Funko saved = service.create(base);

        Map<String, Object> updates = Map.of(
                "nombre", "Patch Hecho",
                "precio", 15.5,
                "cantidad", 5
        );
        Funko patched = service.patch(saved.getId(), updates);

        assertThat(patched.getNombre()).isEqualTo("Patch Hecho");
        assertThat(patched.getPrecio()).isEqualTo(15.5);
        assertThat(patched.getCantidad()).isEqualTo(5);
    }

    @Test
    void deleteRemovesEntityAndGetByIdThrows() {
        Funko base = new Funko();
        base.setNombre("Para borrar");
        base.setModelo("TEST");
        base.setPrecio(9.99);
        base.setCantidad(1);
        base.setImagen("img.jpg");
        base.setCategoria("general");
        base.setFechaLanzamiento(LocalDate.now());
        Funko saved = service.create(base);

        service.delete(saved.getId());
        assertThrows(NoSuchElementException.class, () -> service.getById(saved.getId()));
        assertThat(repository.findById(saved.getId())).isEmpty();
    }

    @Test
    void getByIdNotFoundThrows() {
        UUID random = UUID.randomUUID();
        assertThrows(NoSuchElementException.class, () -> service.getById(random));
    }
}

