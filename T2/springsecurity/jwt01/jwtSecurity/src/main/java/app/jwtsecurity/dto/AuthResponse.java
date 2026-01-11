package app.jwtsecurity.dto;

public record AuthResponse(
        String tokenType,
        String accessToken,
        String username,
        String role
) {}