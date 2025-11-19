package es.iesguzman.demo.repository;

import es.iesguzman.demo.model.EstadoPedido;
import es.iesguzman.demo.model.Pedido;
import es.iesguzman.demo.model.Producto;
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
public class PedidoRepositoryUnitTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Test
    public void testFindAll() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("usuario1");

        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Laptop");

        Pedido pedido1 = new Pedido();
        pedido1.setId(1L);
        pedido1.setUsuario(usuario);
        pedido1.setProducto(producto);
        pedido1.setCantidad(2);
        pedido1.setTotal(1999.98);
        pedido1.setEstado(EstadoPedido.PENDIENTE);
        pedido1.setFechaCreacion(LocalDateTime.now());

        Pedido pedido2 = new Pedido();
        pedido2.setId(2L);
        pedido2.setUsuario(usuario);
        pedido2.setProducto(producto);
        pedido2.setCantidad(1);
        pedido2.setTotal(25.50);
        pedido2.setEstado(EstadoPedido.ENVIADO);
        pedido2.setFechaCreacion(LocalDateTime.now());

        List<Pedido> pedidos = Arrays.asList(pedido1, pedido2);
        when(pedidoRepository.findAll()).thenReturn(pedidos);

        // Act
        List<Pedido> result = pedidoRepository.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2, result.get(0).getCantidad());
        assertEquals(EstadoPedido.ENVIADO, result.get(1).getEstado());
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

        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setUsuario(usuario);
        pedido.setProducto(producto);
        pedido.setCantidad(2);
        pedido.setTotal(1999.98);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setFechaCreacion(LocalDateTime.now());

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // Act
        Optional<Pedido> result = pedidoRepository.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(2, result.get().getCantidad());
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

        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setUsuario(usuario);
        pedido.setProducto(producto);
        pedido.setCantidad(2);
        pedido.setTotal(1999.98);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setFechaCreacion(LocalDateTime.now());

        List<Pedido> pedidos = Arrays.asList(pedido);
        when(pedidoRepository.findByUsuarioId(1L)).thenReturn(pedidos);

        // Act
        List<Pedido> result = pedidoRepository.findByUsuarioId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getUsuario().getId());
    }

    @Test
    public void testFindByIdNotFound() {
        // Arrange
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Pedido> result = pedidoRepository.findById(999L);

        // Assert
        assertFalse(result.isPresent());
    }
}
