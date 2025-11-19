package es.iesguzman.demo.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ResenaResponseDto {

    private Long id;
    private UsuarioResponseDto usuario;
    private ProductoResponseDto producto;
    private Integer calificacion;
    private String comentario;
    private LocalDateTime fechaCreacion;
}
