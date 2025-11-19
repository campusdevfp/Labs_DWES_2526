package es.iesguzman.demo.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ResenaRequestDto {

    // Getters and setters
    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El producto es obligatorio")
    private Long productoId;

    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación debe ser al menos 1")
    @Max(value = 5, message = "La calificación no puede ser mayor a 5")
    private Integer calificacion;

    @Size(max = 500, message = "El comentario no puede exceder 500 caracteres")
    private String comentario;

}
