package com.example.funkos.service;

import com.example.funkos.model.Funko;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FunkoServiceTest {

    @Autowired
    private FunkoService service;

    @Test
    void getAllByCategoria_generalReturnsOnlyGeneral() {
        List<Funko> general = service.getAll("general");
        assertFalse(general.isEmpty());
        assertTrue(general.stream().allMatch(f -> "general".equalsIgnoreCase(f.getCategoria())));
    }

    @Test
    void create() {
        Funko nuevo = new Funko();
        nuevo.setNombre("Nuevo Funko");
        nuevo.setModelo("TEST");
        nuevo.setPrecio(12.34);
        nuevo.setCantidad(2);
        nuevo.setImagen("img.jpg");
        nuevo.setCategoria("general");
        nuevo.setFechaLanzamiento(LocalDate.now());

        Funko creado = service.create(nuevo);
        assertNotNull(creado.getId());
        assertNotNull(creado.getFechaCreacion());
    }

    @Test
    void getById() {
        Funko nuevo = new Funko();
        nuevo.setNombre("Nuevo Funko");
        nuevo.setModelo("TEST");
        nuevo.setPrecio(12.34);
        nuevo.setCantidad(2);
        nuevo.setImagen("img.jpg");
        nuevo.setCategoria("general");
        nuevo.setFechaLanzamiento(LocalDate.now());

        Funko creado = service.create(nuevo);
        Funko recuperado = service.getById(creado.getId());
        assertEquals(creado.getId(), recuperado.getId());
        assertEquals("Nuevo Funko", recuperado.getNombre());
    }

    @Test
    void update() {
        Funko base = new Funko();
        base.setNombre("Base");
        base.setModelo("TEST");
        base.setPrecio(10.0);
        base.setCantidad(1);
        base.setImagen("img.jpg");
        base.setCategoria("general");
        base.setFechaLanzamiento(LocalDate.now());
        Funko saved = service.create(base);

        var fechaCreacionOriginal = saved.getFechaCreacion();

        Funko cambios = new Funko();
        cambios.setNombre("Actualizado");
        cambios.setModelo("TEST2");
        cambios.setPrecio(20.0);
        cambios.setCantidad(3);
        cambios.setImagen("img2.jpg");
        cambios.setCategoria("general");
        cambios.setFechaLanzamiento(LocalDate.now());

        Funko actualizado = service.update(saved.getId(), cambios);
        assertEquals("Actualizado", actualizado.getNombre());
        assertEquals(fechaCreacionOriginal, actualizado.getFechaCreacion());
        assertEquals(saved.getId(), actualizado.getId());
    }



    @Test
    void delete() {
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
    }

    @Test
    void getAllWithoutCategoria_includesCreatedItems() {
        Funko f1 = new Funko();
        f1.setNombre("All Null 1");
        f1.setModelo("TEST");
        f1.setPrecio(5.0);
        f1.setCantidad(1);
        f1.setImagen("img.jpg");
        f1.setCategoria("general");
        f1.setFechaLanzamiento(LocalDate.now());
        Funko s1 = service.create(f1);

        Funko f2 = new Funko();
        f2.setNombre("All Null 2");
        f2.setModelo("TEST");
        f2.setPrecio(6.0);
        f2.setCantidad(2);
        f2.setImagen("img.jpg");
        f2.setCategoria("general");
        f2.setFechaLanzamiento(LocalDate.now());
        Funko s2 = service.create(f2);

        List<Funko> all = service.getAll(null);
        var ids = all.stream().map(Funko::getId).toList();
        assertTrue(ids.contains(s1.getId()));
        assertTrue(ids.contains(s2.getId()));
    }
}
