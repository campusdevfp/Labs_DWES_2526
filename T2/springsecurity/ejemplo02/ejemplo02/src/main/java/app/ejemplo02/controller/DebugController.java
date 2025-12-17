package app.ejemplo02.controller;

import app.ejemplo02.models.UserEntity;
import app.ejemplo02.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Endpoint disponibles: /debug/users , /debug/admin/reset
@RestController
@RequestMapping("/debug")
public class DebugController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DebugController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> listUsers() {
        List<Map<String, Object>> users = userRepository.findAll().stream()
                .map(u -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", u.getId());
                    m.put("username", u.getUsername());
                    m.put("password", u.getPassword());
                    m.put("role", u.getRole());
                    return m;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PostMapping("/admin/reset")
    public ResponseEntity<?> resetAdminPassword(@RequestParam String password) {
        UserEntity admin = userRepository.findByUsername("admin").orElse(null);
        if (admin == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Usuario admin no encontrado"));
        }
        String encoded = passwordEncoder.encode(password);
        admin.setPassword(encoded);
        userRepository.save(admin);
        return ResponseEntity.ok(Map.of("mensaje", "Contraseña del admin restablecida", "encoded", encoded));
    }
}
