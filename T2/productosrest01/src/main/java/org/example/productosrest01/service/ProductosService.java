package org.example.productosrest01.service;

import org.example.productosrest01.model.Producto;
import org.example.productosrest01.repository.ProductRepository;

import java.util.List;

public class ProductosService {
    private final ProductRepository productRepository;

    public ProductosService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Producto> listar(){
        return productRepository.findAll();
    }


}
