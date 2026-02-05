package com.isidraguzman.proyectointegrador.service;

import com.isidraguzman.proyectointegrador.infraestructure.security.JwtService;
import com.isidraguzman.proyectointegrador.payload.LoginRequest;
import com.isidraguzman.proyectointegrador.payload.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    AuthenticationManager authManager;
    @Autowired
    JwtService jwtService;

    public LoginResponse login(LoginRequest req) {

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.username(), req.password()
                )
        );

        return new LoginResponse(
                jwtService.generateToken((UserDetails) auth.getPrincipal())
        );
    }
}
