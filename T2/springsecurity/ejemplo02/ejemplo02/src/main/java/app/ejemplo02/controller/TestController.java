package app.ejemplo02.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/test")
public class TestController {

    // Endpoint público
    @GetMapping("/public")
    public ResponseEntity<String> endpointPublico() {
        return ResponseEntity.ok("Este es un endpoint público - accesible sin autenticación");
    }

    // Endpoint que requiere autenticación
    @GetMapping("/authenticated")
    public ResponseEntity<String> endpointAutenticado(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }
        String username = authentication.getName();
        return ResponseEntity.ok("¡Hola " + username + " Te has autenticado con éxito!");
    }

    // Endpoint que muestra información del usuario autenticado
    @GetMapping("/me")
    public ResponseEntity<UsuarioInfo> obtenerMiInfo(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UsuarioInfo info = new UsuarioInfo(
                authentication.getName(),
                authentication.getAuthorities().stream()
                        .map(auth -> auth.getAuthority())
                        .toList()
        );
        return ResponseEntity.ok(info);
    }

    // DTO para respuesta
    public static class UsuarioInfo {
        public String username;
        public java.util.List<String> roles;

        public UsuarioInfo(String username, java.util.List<String> roles) {
            this.username = username;
            this.roles = roles;
        }
    }
}