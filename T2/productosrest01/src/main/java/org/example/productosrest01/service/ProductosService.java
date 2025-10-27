package org.example.productosrest01.service;

import org.example.productosrest01.model.Producto;
import org.example.productosrest01.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductosService {
    private final ProductRepository productRepository;

    public ProductosService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Producto> listar(){
        return productRepository.findAll();
    }

    public Optional<Producto> buscarPorId(Long id){
        return productRepository.findById(id);
    }

    public Producto guardar(Producto producto){
        return productRepository.save(producto);
    }


}
