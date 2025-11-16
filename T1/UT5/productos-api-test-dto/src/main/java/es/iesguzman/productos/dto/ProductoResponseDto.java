package es.iesguzman.productos.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ProductoResponseDto {
    private Long id;
    private String nombre;
    private Double precio;
    private Integer stock;
    private String categoria;
}
