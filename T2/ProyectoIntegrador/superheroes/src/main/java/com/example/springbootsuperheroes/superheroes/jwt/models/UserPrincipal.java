package com.example.springbootsuperheroes.superheroes.jwt.models;

import com.example.springbootsuperheroes.superheroes.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
public class UserPrincipal implements UserDetails {

  private final UserEntity userEntity;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of();
  }

  @Override
  public String getPassword() {
    // storedHash is a byte[]; expose as hex for UserDetails compatibility
    if (userEntity.getStoredHash() == null) return "";
    StringBuilder sb = new StringBuilder();
    for (byte b : userEntity.getStoredHash()) {
      sb.append(String.format("%02x", b));
    }
    return sb.toString();
  }

  @Override
  public String getUsername() {
    return this.userEntity.getEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
