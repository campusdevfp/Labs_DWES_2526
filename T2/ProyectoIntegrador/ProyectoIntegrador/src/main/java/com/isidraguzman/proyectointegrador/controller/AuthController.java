package com.isidraguzman.proyectointegrador.controller;

import com.isidraguzman.proyectointegrador.payload.LoginRequest;
import com.isidraguzman.proyectointegrador.payload.LoginResponse;
import com.isidraguzman.proyectointegrador.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req) {
        return authService.login(req);
    }
}