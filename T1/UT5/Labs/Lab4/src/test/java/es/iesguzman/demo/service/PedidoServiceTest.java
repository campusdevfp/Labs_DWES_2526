package es.iesguzman.demo.service;

import es.iesguzman.demo.dto.PedidoRequestDto;
import es.iesguzman.demo.dto.PedidoResponseDto;
import es.iesguzman.demo.mapper.PedidoMapper;
import es.iesguzman.demo.model.EstadoPedido;
import es.iesguzman.demo.model.Pedido;
import es.iesguzman.demo.model.Producto;
import es.iesguzman.demo.model.Usuario;
import es.iesguzman.demo.repository.PedidoRepository;
import es.iesguzman.demo.repository.ProductoRepository;
import es.iesguzman.demo.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test unitario del servicio PedidoService.
 * Usa Mockito para simular las dependencias (repository y mapper).
 */
@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    PedidoRepository pedidoRepository;

    @Mock
    PedidoMapper pedidoMapper;

    @Mock
    UsuarioRepository usuarioRepository;

    @Mock
    ProductoRepository productoRepository;

    @InjectMocks
    PedidoService pedidoService;

    PedidoRequestDto requestDto;
    Pedido pedido;
    PedidoResponseDto responseDto;
    Usuario usuario;
    Producto producto;

    @BeforeEach
    void setUp() {
        requestDto = new PedidoRequestDto();
        requestDto.setUsuarioId(1L);
        requestDto.setProductoId(1L);
        requestDto.setCantidad(2);

        usuario = new Usuario();
        usuario.setId(1L);

        producto = new Producto();
        producto.setId(1L);
        producto.setPrecio(999.99);
        producto.setStock(10); // Stock suficiente

        // Crear pedido usando los valores del requestDto
        pedido = new Pedido();
        pedido.setId(1L);
        pedido.setCantidad(requestDto.getCantidad());
        pedido.setTotal(1999.98);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setFechaCreacion(LocalDateTime.now());

        // Crear responseDto usando los valores de la entidad
        responseDto = new PedidoResponseDto();
        responseDto.setId(pedido.getId());
        responseDto.setCantidad(pedido.getCantidad());
        responseDto.setTotal(pedido.getTotal());
        responseDto.setEstado(pedido.getEstado());
        responseDto.setFechaCreacion(pedido.getFechaCreacion());
    }

    /**
     * Test: getAllPedidos debe devolver una lista de pedidos.
     */
    @Test
    void getAllPedidos_returnsList() {
        when(pedidoRepository.findAll()).thenReturn(List.of(pedido));
        when(pedidoMapper.toResponseDto(pedido)).thenReturn(responseDto);

        List<PedidoResponseDto> list = pedidoService.getAllPedidos();

        assertEquals(1, list.size());
        assertEquals(2, list.get(0).getCantidad());
        verify(pedidoRepository, times(1)).findAll();
    }

    /**
     * Test: getPedidoById debe devolver un pedido cuando existe.
     */
    @Test
    void getPedidoById_success() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoMapper.toResponseDto(pedido)).thenReturn(responseDto);

        PedidoResponseDto result = pedidoService.getPedidoById(1L);

        assertEquals(1L, result.getId());
        assertEquals(2, result.getCantidad());
        verify(pedidoRepository, times(1)).findById(1L);
    }

    /**
     * Test: createPedido debe crear un nuevo pedido correctamente.
     */
    @Test
    void createPedido_success() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(pedidoMapper.toEntity(requestDto)).thenReturn(pedido);
        when(productoRepository.save(producto)).thenReturn(producto);
        when(pedidoRepository.save(pedido)).thenReturn(pedido);
        when(pedidoMapper.toResponseDto(pedido)).thenReturn(responseDto);

        PedidoResponseDto result = pedidoService.createPedido(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(2, result.getCantidad());
        verify(pedidoRepository, times(1)).save(pedido);
        verify(productoRepository, times(1)).save(producto);
    }

    /**
     * Test: deletePedido debe eliminar un pedido correctamente.
     */
    @Test
    void deletePedido_success() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        pedidoService.deletePedido(1L);

        verify(pedidoRepository, times(1)).delete(pedido);
    }
}
