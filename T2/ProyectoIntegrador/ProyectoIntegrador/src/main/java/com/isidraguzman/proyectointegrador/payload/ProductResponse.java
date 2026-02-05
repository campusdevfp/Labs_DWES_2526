package com.isidraguzman.proyectointegrador.payload;

public record ProductResponse(
        Long id,
        String name,
        Double price
) {}