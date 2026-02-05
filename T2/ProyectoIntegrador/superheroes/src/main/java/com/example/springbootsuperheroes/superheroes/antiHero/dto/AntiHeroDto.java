package com.example.springbootsuperheroes.superheroes.antiHero.dto;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AntiHeroDto {

    private UUID id;

    @NotNull(message = "First Name is required")
    private String firstName;

    private String lastName;
    private String house;
    private String knownAs;

    // explicit getter for id to avoid Lombok processing issues
    public UUID getId() { return id; }
}
