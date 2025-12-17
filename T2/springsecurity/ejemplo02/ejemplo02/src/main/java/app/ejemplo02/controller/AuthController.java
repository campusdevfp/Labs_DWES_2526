package app.ejemplo02.controller;

import app.ejemplo02.dto.UsuarioInfo;
import app.ejemplo02.util.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// Endpoint disponibles: /api/auth/login , /api/auth/logout, /api/auth/me, /api/auth/public

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * Endpoint de login para APIs REST
     * POST /api/auth/login
     * Body: { "username": "user", "password": "password" }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {

        try {
            // Autenticar al usuario
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(),
                            loginRequest.password()
                    )
            );

            // Centralizar guardado del SecurityContext en la sesión
            SecurityUtils.setAuthenticationInSession(request, authentication);

            HttpSession session = request.getSession(false);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "✅ Login exitoso",
                    "usuario", authentication.getName(),
                    "roles", authentication.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority).toList(),
                    "sessionId", session != null ? session.getId() : null
            ));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error", "❌ Credenciales inválidas",
                            "detalle", e.getMessage()
                    ));
        }
    }

    /**
     * Endpoint de logout
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        SecurityUtils.clearAuthentication(request);
        return ResponseEntity.ok(Map.of("mensaje", "✅ Sesión cerrada exitosamente"));
    }

    // ----------------- endpoints públicos y de prueba -----------------

    @GetMapping("/public")
    public ResponseEntity<String> publico() {
        return ResponseEntity.ok("Este es un endpoint público - accesible sin autenticación");
    }

    @GetMapping("/authenticated")
    public ResponseEntity<String> autenticado(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }
        return ResponseEntity.ok("¡Hola " + authentication.getName() + " Te has autenticado con éxito!");
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioInfo> me(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UsuarioInfo info = new UsuarioInfo(
                authentication.getName(),
                authentication.getAuthorities().stream().map(a -> a.getAuthority()).toList()
        );
        return ResponseEntity.ok(info);
    }

    // DTO para la petición de login
    public record LoginRequest(String username, String password) {}
}
