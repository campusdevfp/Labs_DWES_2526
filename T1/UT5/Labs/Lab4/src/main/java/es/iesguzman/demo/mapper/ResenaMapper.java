package es.iesguzman.demo.mapper;

import es.iesguzman.demo.dto.ResenaRequestDto;
import es.iesguzman.demo.dto.ResenaResponseDto;
import es.iesguzman.demo.model.Producto;
import es.iesguzman.demo.model.Resena;
import es.iesguzman.demo.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResenaMapper {

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private ProductoMapper productoMapper;

    public Resena toEntity(ResenaRequestDto dto) {
        Resena r = new Resena();
        Usuario u = new Usuario();
        u.setId(dto.getUsuarioId());
        r.setUsuario(u);
        Producto p = new Producto();
        p.setId(dto.getProductoId());
        r.setProducto(p);
        r.setCalificacion(dto.getCalificacion());
        r.setComentario(dto.getComentario());
        return r;
    }

    public ResenaResponseDto toResponseDto(Resena resena) {
        ResenaResponseDto dto = new ResenaResponseDto();
        dto.setId(resena.getId());
        dto.setUsuario(usuarioMapper.toResponseDto(resena.getUsuario()));
        dto.setProducto(productoMapper.toResponseDto(resena.getProducto()));
        dto.setCalificacion(resena.getCalificacion());
        dto.setComentario(resena.getComentario());
        dto.setFechaCreacion(resena.getFechaCreacion());
        return dto;
    }
}
