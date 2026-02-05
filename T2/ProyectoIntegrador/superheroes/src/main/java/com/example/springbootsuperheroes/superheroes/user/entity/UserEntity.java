package com.example.springbootsuperheroes.superheroes.user.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUID")
  @Column(nullable = false, updatable = false)
  private UUID id;

  @Column(unique = true)
  private String email;

  private String mobileNumber;
  private byte[] storedHash;
  private byte[] storedSalt;

  public UserEntity(String email, String mobileNumber) {
    this.email = email;
    this.mobileNumber = mobileNumber;
  }

  // Explicit getters/setters to ensure compilation even if Lombok processing is unavailable
  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getMobileNumber() { return mobileNumber; }
  public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
  public byte[] getStoredHash() { return storedHash; }
  public void setStoredHash(byte[] storedHash) { this.storedHash = storedHash; }
  public byte[] getStoredSalt() { return storedSalt; }
  public void setStoredSalt(byte[] storedSalt) { this.storedSalt = storedSalt; }
}
