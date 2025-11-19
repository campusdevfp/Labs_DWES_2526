package es.iesguzman.demo.dto;

import es.iesguzman.demo.model.EstadoPedido;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PedidoResponseDto {

    private Long id;
    private UsuarioResponseDto usuario;
    private ProductoResponseDto producto;
    private Integer cantidad;
    private Double total;
    private EstadoPedido estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
