package com.example.demo.controller;



import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping("/publico/saludo")
    public String publico() {
        return "Endpoint público";
    }

    @GetMapping("/api/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String soloUser() {
        return "Solo USER o ADMIN autenticado";
    }

    @GetMapping("/api/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String soloAdmin() {
        return "Solo ADMIN";
    }
}
