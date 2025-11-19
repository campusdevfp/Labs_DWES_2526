package es.iesguzman.demo.dto;

import lombok.Data;

@Data
public class CategoriaResponseDto {

    private Long id;
    private String nombre;
    private String descripcion;
    private String imagenUrl;
}
