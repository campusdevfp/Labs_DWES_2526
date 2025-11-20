package es.iesguzman.demo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductoResponseDto {

    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer stock;
    private String categoria;
    private String imagenUrl;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private Boolean activo;
}
