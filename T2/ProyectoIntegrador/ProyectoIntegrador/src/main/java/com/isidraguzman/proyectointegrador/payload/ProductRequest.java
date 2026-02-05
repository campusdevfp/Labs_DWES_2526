package com.isidraguzman.proyectointegrador.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProductRequest(

        @NotBlank(message = "Product name is required")
        @Size(max = 100)
        String name,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        Double price
) {}