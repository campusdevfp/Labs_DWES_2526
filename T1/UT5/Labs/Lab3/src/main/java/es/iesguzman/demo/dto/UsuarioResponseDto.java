package es.iesguzman.demo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UsuarioResponseDto {

    private Long id;
    private String username;
    private String email;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private Boolean activo;
}
