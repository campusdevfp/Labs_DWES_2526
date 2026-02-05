package com.example.springbootsuperheroes.superheroes.jwt.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest implements Serializable {

  private String email;
  private String password;

  // explicit getters to avoid Lombok processing issues
  public String getEmail() { return email; }
  public String getPassword() { return password; }
}
