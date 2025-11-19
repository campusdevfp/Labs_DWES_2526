package es.iesguzman.demo.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PedidoRequestDto {

    // Getters and setters
    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El producto es obligatorio")
    private Long productoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

}
