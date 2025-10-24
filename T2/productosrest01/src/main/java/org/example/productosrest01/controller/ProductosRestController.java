package org.example.productosrest01.controller;


import org.example.productosrest01.model.Producto;
import org.example.productosrest01.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductosRestController {
    private final ProductRepository productosRepository;

    @Autowired
    public ProductosRestController(ProductRepository productosRepository) {
        this.productosRepository = productosRepository;
    }

    @GetMapping
    public ResponseEntity<List<Producto>> getAllProductos() {
        return ResponseEntity.ok(productosRepository.findAll());
    }
}
