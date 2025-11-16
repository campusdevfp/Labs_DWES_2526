package es.iesguzman.productos.mapper;

import org.springframework.stereotype.Component;
import es.iesguzman.productos.domain.Producto;
import es.iesguzman.productos.dto.*;

@Component
public class ProductoMapper {
    public Producto toModel(ProductoRequestDto dto) {
        return new Producto(null, dto.getNombre(), dto.getPrecio(), dto.getStock(), dto.getCategoria());
    }

    public ProductoResponseDto toResponse(Producto p) {
        return new ProductoResponseDto(p.getId(), p.getNombre(), p.getPrecio(), p.getStock(), p.getCategoria());
    }
}
