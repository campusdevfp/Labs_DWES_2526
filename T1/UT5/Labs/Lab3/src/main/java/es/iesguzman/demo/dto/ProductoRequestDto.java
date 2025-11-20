package es.iesguzman.demo.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProductoRequestDto {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private Double precio;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @Size(max = 50, message = "La categoría no puede exceder 50 caracteres")
    private String categoria;

    @URL(message = "La URL de la imagen debe ser válida")
    private String imagenUrl;
}
