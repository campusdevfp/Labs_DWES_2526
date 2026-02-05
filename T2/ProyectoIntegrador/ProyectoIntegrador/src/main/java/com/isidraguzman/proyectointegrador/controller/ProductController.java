package com.isidraguzman.proyectointegrador.controller;

import com.isidraguzman.proyectointegrador.payload.ProductRequest;
import com.isidraguzman.proyectointegrador.payload.ProductResponse;
import com.isidraguzman.proyectointegrador.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    ProductService service;

    @GetMapping
    public List<ProductResponse> findAll() {
        return service.findAll();
    }

    @PostMapping
    public ProductResponse create(@Valid @RequestBody ProductRequest req) {
        return service.create(req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}