package es.iesguzman.demo.repository;

import es.iesguzman.demo.model.Producto;
import es.iesguzman.demo.model.Resena;
import es.iesguzman.demo.model.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResenaRepositoryUnitTest {

    @Mock
    private ResenaRepository resenaRepository;

    @Test
    public void testFindAll() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("usuario1");

        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Laptop");

        Resena resena1 = new Resena();
        resena1.setId(1L);
        resena1.setUsuario(usuario);
        resena1.setProducto(producto);
        resena1.setCalificacion(5);
        resena1.setComentario("Excelente producto");
        resena1.setFechaCreacion(LocalDateTime.now());

        Resena resena2 = new Resena();
        resena2.setId(2L);
        resena2.setUsuario(usuario);
        resena2.setProducto(producto);
        resena2.setCalificacion(4);
        resena2.setComentario("Buen producto");
        resena2.setFechaCreacion(LocalDateTime.now());

        List<Resena> resenas = Arrays.asList(resena1, resena2);
        when(resenaRepository.findAll()).thenReturn(resenas);

        // Act
        List<Resena> result = resenaRepository.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(5, result.get(0).getCalificacion());
        assertEquals(4, result.get(1).getCalificacion());
    }

    @Test
    public void testFindById() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("usuario1");

        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Laptop");

        Resena resena = new Resena();
        resena.setId(1L);
        resena.setUsuario(usuario);
        resena.setProducto(producto);
        resena.setCalificacion(5);
        resena.setComentario("Excelente producto");
        resena.setFechaCreacion(LocalDateTime.now());

        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resena));

        // Act
        Optional<Resena> result = resenaRepository.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(5, result.get().getCalificacion());
    }

    @Test
    public void testFindByProductoId() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("usuario1");

        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Laptop");

        Resena resena = new Resena();
        resena.setId(1L);
        resena.setUsuario(usuario);
        resena.setProducto(producto);
        resena.setCalificacion(5);
        resena.setComentario("Excelente producto");
        resena.setFechaCreacion(LocalDateTime.now());

        List<Resena> resenas = Arrays.asList(resena);
        when(resenaRepository.findByProductoId(1L)).thenReturn(resenas);

        // Act
        List<Resena> result = resenaRepository.findByProductoId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getProducto().getId());
    }

    @Test
    public void testFindByUsuarioId() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("usuario1");

        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Laptop");

        Resena resena = new Resena();
        resena.setId(1L);
        resena.setUsuario(usuario);
        resena.setProducto(producto);
        resena.setCalificacion(5);
        resena.setComentario("Excelente producto");
        resena.setFechaCreacion(LocalDateTime.now());

        List<Resena> resenas = Arrays.asList(resena);
        when(resenaRepository.findByUsuarioId(1L)).thenReturn(resenas);

        // Act
        List<Resena> result = resenaRepository.findByUsuarioId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getUsuario().getId());
    }

    @Test
    public void testFindByIdNotFound() {
        // Arrange
        when(resenaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Resena> result = resenaRepository.findById(999L);

        // Assert
        assertFalse(result.isPresent());
    }
}
