package es.iesguzman.demo.mapper;

import es.iesguzman.demo.dto.PedidoRequestDto;
import es.iesguzman.demo.dto.PedidoResponseDto;
import es.iesguzman.demo.model.Pedido;
import es.iesguzman.demo.model.Producto;
import es.iesguzman.demo.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PedidoMapper {

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private ProductoMapper productoMapper;

    public Pedido toEntity(PedidoRequestDto dto) {
        Pedido p = new Pedido();
        Usuario u = new Usuario();
        u.setId(dto.getUsuarioId());
        p.setUsuario(u);
        Producto pr = new Producto();
        pr.setId(dto.getProductoId());
        p.setProducto(pr);
        p.setCantidad(dto.getCantidad());
        return p;
    }

    public PedidoResponseDto toResponseDto(Pedido pedido) {
        PedidoResponseDto dto = new PedidoResponseDto();
        dto.setId(pedido.getId());
        dto.setUsuario(usuarioMapper.toResponseDto(pedido.getUsuario()));
        dto.setProducto(productoMapper.toResponseDto(pedido.getProducto()));
        dto.setCantidad(pedido.getCantidad());
        dto.setTotal(pedido.getTotal());
        dto.setEstado(pedido.getEstado());
        dto.setFechaCreacion(pedido.getFechaCreacion());
        dto.setFechaActualizacion(pedido.getFechaActualizacion());
        return dto;
    }
}
