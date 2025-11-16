package com.example.funkos.service;

import com.example.funkos.model.Funko;
import com.example.funkos.repository.FunkoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FunkoServiceMockTest {

    @Mock
    private FunkoRepository repository;

    @InjectMocks
    private FunkoService service;

    private Funko createFunko(UUID id, String nombre, String categoria) {
        Funko f = new Funko();
        f.setId(id);
        f.setNombre(nombre);
        f.setModelo("TEST");
        f.setPrecio(10.0);
        f.setCantidad(1);
        f.setImagen("img.jpg");
        f.setCategoria(categoria);
        f.setFechaLanzamiento(LocalDate.now());
        f.setFechaCreacion(LocalDate.now());
        f.setFechaActualizacion(LocalDate.now());
        return f;
    }

    @Test
    void getAllWithoutCategoria_usesFindAll() {
        List<Funko> datos = List.of(
                createFunko(UUID.randomUUID(), "A", "general"),
                createFunko(UUID.randomUUID(), "B", "general")
        );
        when(repository.findAll()).thenReturn(datos);

        List<Funko> result = service.getAll(null);
        assertThat(result).hasSize(2);
        verify(repository, times(1)).findAll();
        verify(repository, never()).findByCategoriaIgnoreCase(any());
    }

    @Test
    void getAllWithCategoria_usesFindByCategoriaIgnoreCase() {
        List<Funko> datos = List.of(createFunko(UUID.randomUUID(), "C", "general"));
        when(repository.findByCategoriaIgnoreCase("general")).thenReturn(datos);

        List<Funko> result = service.getAll("general");
        assertThat(result).hasSize(1);
        assertThat(result).allMatch(f -> "general".equalsIgnoreCase(f.getCategoria()));
        verify(repository, times(1)).findByCategoriaIgnoreCase("general");
        verify(repository, never()).findAll();
    }

    @Test
    void getById_foundReturnsEntity() {
        UUID id = UUID.randomUUID();
        Funko f = createFunko(id, "Encontrado", "general");
        when(repository.findById(id)).thenReturn(Optional.of(f));

        Funko result = service.getById(id);
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getNombre()).isEqualTo("Encontrado");
        verify(repository, times(1)).findById(id);
    }

    @Test
    void getById_notFoundThrows() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.getById(id));
        verify(repository, times(1)).findById(id);
    }

    @Test
    void create_savesAndReturnsWithIdAndFechaCreacion() {
        Funko nuevo = new Funko();
        nuevo.setNombre("Nuevo");
        nuevo.setModelo("TEST");
        nuevo.setPrecio(12.3);
        nuevo.setCantidad(2);
        nuevo.setImagen("img.jpg");
        nuevo.setCategoria("general");
        nuevo.setFechaLanzamiento(LocalDate.now());

        when(repository.save(any(Funko.class))).thenAnswer(invocation -> {
            Funko arg = invocation.getArgument(0);
            arg.setId(UUID.randomUUID());
            arg.setFechaCreacion(LocalDate.now());
            arg.setFechaActualizacion(LocalDate.now());
            return arg;
        });

        Funko creado = service.create(nuevo);
        assertThat(creado.getId()).isNotNull();
        assertThat(creado.getFechaCreacion()).isNotNull();
        verify(repository, times(1)).save(any(Funko.class));
    }

    @Test
    void update_preservesFechaCreacionAndSetsId() {
        UUID id = UUID.randomUUID();
        Funko existente = createFunko(id, "Base", "general");
        LocalDate fechaCreacionOriginal = existente.getFechaCreacion();
        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.save(any(Funko.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Funko cambios = new Funko();
        cambios.setNombre("Actualizado");
        cambios.setModelo("TEST2");
        cambios.setPrecio(20.0);
        cambios.setCantidad(3);
        cambios.setImagen("img2.jpg");
        cambios.setCategoria("general");
        cambios.setFechaLanzamiento(LocalDate.now());

        Funko actualizado = service.update(id, cambios);
        assertThat(actualizado.getId()).isEqualTo(id);
        assertThat(actualizado.getFechaCreacion()).isEqualTo(fechaCreacionOriginal);
        assertThat(actualizado.getNombre()).isEqualTo("Actualizado");
        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(any(Funko.class));
    }

    @Test
    void patch_updatesSelectedFieldsOnly() {
        UUID id = UUID.randomUUID();
        Funko base = createFunko(id, "Patch Base", "general");
        base.setPrecio(10.0);
        base.setCantidad(1);
        when(repository.findById(id)).thenReturn(Optional.of(base));
        when(repository.save(any(Funko.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Map<String, Object> updates = Map.of(
                "nombre", "Patch Hecho",
                "precio", 15.5,
                "cantidad", 5
        );
        Funko patched = service.patch(id, updates);

        assertThat(patched.getNombre()).isEqualTo("Patch Hecho");
        assertThat(patched.getPrecio()).isEqualTo(15.5);
        assertThat(patched.getCantidad()).isEqualTo(5);
        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(any(Funko.class));
    }

    @Test
    void delete_callsRepositoryDeleteById() {
        UUID id = UUID.randomUUID();
        doNothing().when(repository).deleteById(eq(id));
        service.delete(id);
        verify(repository, times(1)).deleteById(id);
    }
}
