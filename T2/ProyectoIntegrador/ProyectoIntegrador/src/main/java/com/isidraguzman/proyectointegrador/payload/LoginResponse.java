package com.isidraguzman.proyectointegrador.payload;

public record LoginResponse(
        String token,
        String type
) {
    public LoginResponse(String token) {
        this(token, "Bearer");
    }
}
