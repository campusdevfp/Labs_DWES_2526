package es.iesguzman.demo.repository;

import es.iesguzman.demo.model.Categoria;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoriaRepositoryUnitTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Test
    public void testFindAll() {
        // Arrange
        Categoria categoria1 = new Categoria();
        categoria1.setId(1L);
        categoria1.setNombre("Electrónica");
        categoria1.setDescripcion("Productos electrónicos");

        Categoria categoria2 = new Categoria();
        categoria2.setId(2L);
        categoria2.setNombre("Libros");
        categoria2.setDescripcion("Libros y literatura");

        List<Categoria> categorias = Arrays.asList(categoria1, categoria2);
        when(categoriaRepository.findAll()).thenReturn(categorias);

        // Act
        List<Categoria> result = categoriaRepository.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Electrónica", result.get(0).getNombre());
        assertEquals("Libros", result.get(1).getNombre());
    }

    @Test
    public void testFindById() {
        // Arrange
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Electrónica");
        categoria.setDescripcion("Productos electrónicos");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        // Act
        Optional<Categoria> result = categoriaRepository.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Electrónica", result.get().getNombre());
    }

    @Test
    public void testFindByIdNotFound() {
        // Arrange
        when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Categoria> result = categoriaRepository.findById(999L);

        // Assert
        assertFalse(result.isPresent());
    }
}
