package com.example.todolist.controller;

import com.example.todolist.util.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
            );

            SecurityUtils.setAuthenticationInSession(request, authentication);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "✅ Login exitoso",
                    "usuario", authentication.getName()
            ));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "❌ Credenciales inválidas"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        SecurityUtils.clearAuthentication(request);
        return ResponseEntity.ok(Map.of("mensaje", "✅ Sesión cerrada exitosamente"));
    }

    @GetMapping("/public")
    public ResponseEntity<String> publico() {
        return ResponseEntity.ok("Este es un endpoint público - accesible sin autenticación");
    }

    public record LoginRequest(String username, String password) {}
}