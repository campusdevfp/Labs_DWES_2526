package es.iesguzman.demo.service;

import es.iesguzman.demo.dto.PedidoRequestDto;
import es.iesguzman.demo.dto.PedidoResponseDto;
import es.iesguzman.demo.exception.PedidoBadRequestException;
import es.iesguzman.demo.mapper.PedidoMapper;
import es.iesguzman.demo.model.EstadoPedido;
import es.iesguzman.demo.model.Pedido;
import es.iesguzman.demo.model.Producto;
import es.iesguzman.demo.model.Usuario;
import es.iesguzman.demo.repository.PedidoRepository;
import es.iesguzman.demo.repository.ProductoRepository;
import es.iesguzman.demo.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private PedidoMapper pedidoMapper;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    public void testSaveCalculaTotalCorrectamente() {
        // Arrange
        PedidoRequestDto requestDto = new PedidoRequestDto();
        requestDto.setUsuarioId(1L);
        requestDto.setProductoId(1L);
        requestDto.setCantidad(3);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("user1");

        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Laptop");
        producto.setPrecio(500.0);
        producto.setStock(10);

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setProducto(producto);
        pedido.setCantidad(3);

        Pedido pedidoGuardado = new Pedido();
        pedidoGuardado.setId(1L);
        pedidoGuardado.setUsuario(usuario);
        pedidoGuardado.setProducto(producto);
        pedidoGuardado.setCantidad(3);
        pedidoGuardado.setTotal(1500.0);
        pedidoGuardado.setEstado(EstadoPedido.PENDIENTE);

        PedidoResponseDto responseDto = new PedidoResponseDto();
        responseDto.setId(1L);
        responseDto.setCantidad(3);
        responseDto.setTotal(1500.0);
        responseDto.setEstado(EstadoPedido.PENDIENTE);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(pedidoMapper.toEntity(requestDto)).thenReturn(pedido);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoGuardado);
        when(pedidoMapper.toResponseDto(pedidoGuardado)).thenReturn(responseDto);
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        // Act
        PedidoResponseDto result = pedidoService.createPedido(requestDto);

        // Assert
        assertNotNull(result);
        assertEquals(1500.0, result.getTotal());
        assertEquals(3, result.getCantidad());
        assertEquals(EstadoPedido.PENDIENTE, result.getEstado());
        verify(pedidoRepository).save(any(Pedido.class));
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    public void testSaveConStockInsuficiente() {
        // Arrange
        PedidoRequestDto requestDto = new PedidoRequestDto();
        requestDto.setUsuarioId(1L);
        requestDto.setProductoId(1L);
        requestDto.setCantidad(100);

        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Producto producto = new Producto();
        producto.setId(1L);
        producto.setStock(5);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // Act & Assert
        assertThrows(PedidoBadRequestException.class, () -> {
            pedidoService.createPedido(requestDto);
        });
        verify(pedidoRepository, never()).save(any());
    }
}

