package app.jwtsecurity.service;


import app.jwtsecurity.dto.AuthResponse;
import app.jwtsecurity.dto.LoginRequest;
import app.jwtsecurity.dto.RegisterRequest;
import app.jwtsecurity.entity.AppUser;
import app.jwtsecurity.entity.Role;
import app.jwtsecurity.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthResponse register(@Valid RegisterRequest req) {
        if (repo.existsByUsername(req.username())) {
            throw new IllegalArgumentException("Username already exists");
        }

        var user = AppUser.builder()
                .username(req.username())
                .passwordHash(encoder.encode(req.password()))
                .role(Role.USER)
                .build();

        repo.save(user);

        var ud = userDetailsService.loadUserByUsername(user.getUsername());
        var token = jwtService.generateAccessToken(ud);

        return new AuthResponse("Bearer", token, user.getUsername(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );

        var user = repo.findByUsername(req.username())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var ud = userDetailsService.loadUserByUsername(user.getUsername());
        var token = jwtService.generateAccessToken(ud);

        return new AuthResponse("Bearer", token, user.getUsername(), user.getRole().name());
    }
}