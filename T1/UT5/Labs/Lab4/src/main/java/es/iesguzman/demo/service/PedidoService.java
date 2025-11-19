package es.iesguzman.demo.service;

import es.iesguzman.demo.dto.PedidoRequestDto;
import es.iesguzman.demo.dto.PedidoResponseDto;
import es.iesguzman.demo.exception.PedidoBadRequestException;
import es.iesguzman.demo.exception.PedidoNotFoundException;
import es.iesguzman.demo.mapper.PedidoMapper;
import es.iesguzman.demo.model.EstadoPedido;
import es.iesguzman.demo.model.Pedido;
import es.iesguzman.demo.model.Producto;
import es.iesguzman.demo.model.Usuario;
import es.iesguzman.demo.repository.PedidoRepository;
import es.iesguzman.demo.repository.ProductoRepository;
import es.iesguzman.demo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final PedidoMapper pedidoMapper;

    public PedidoResponseDto createPedido(PedidoRequestDto dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new PedidoBadRequestException("Usuario no encontrado"));
        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new PedidoBadRequestException("Producto no encontrado"));
        if (producto.getStock() < dto.getCantidad()) {
            throw new PedidoBadRequestException("Stock insuficiente");
        }
        Pedido pedido = pedidoMapper.toEntity(dto);
        pedido.setUsuario(usuario);
        pedido.setProducto(producto);
        pedido.setTotal(producto.getPrecio() * dto.getCantidad());
        pedido.setEstado(EstadoPedido.PENDIENTE);
        producto.setStock(producto.getStock() - dto.getCantidad());
        productoRepository.save(producto);
        Pedido saved = pedidoRepository.save(pedido);
        return pedidoMapper.toResponseDto(saved);
    }

    @Cacheable(value = "pedidos", key = "#id")
    public PedidoResponseDto getPedidoById(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNotFoundException("Pedido no encontrado"));
        return pedidoMapper.toResponseDto(pedido);
    }

    @Cacheable("pedidos")
    public List<PedidoResponseDto> getAllPedidos() {
        return pedidoRepository.findAll().stream()
                .map(pedidoMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Cacheable("pedidos")
    public List<PedidoResponseDto> getPedidosByUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId).stream()
                .map(pedidoMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "pedidos", key = "#id")
    public PedidoResponseDto updateEstadoPedido(Long id, EstadoPedido estado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNotFoundException("Pedido no encontrado"));
        pedido.setEstado(estado);
        Pedido saved = pedidoRepository.save(pedido);
        return pedidoMapper.toResponseDto(saved);
    }

    @CacheEvict(value = "pedidos", key = "#id")
    public void deletePedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNotFoundException("Pedido no encontrado"));
        pedidoRepository.delete(pedido);
    }
}
