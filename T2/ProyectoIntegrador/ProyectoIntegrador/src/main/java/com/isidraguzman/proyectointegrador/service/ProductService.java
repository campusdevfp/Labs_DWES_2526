package com.isidraguzman.proyectointegrador.service;

import com.isidraguzman.proyectointegrador.model.Product;
import com.isidraguzman.proyectointegrador.payload.ProductRequest;
import com.isidraguzman.proyectointegrador.payload.ProductResponse;
import com.isidraguzman.proyectointegrador.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    ProductRepository repo;

    public List<ProductResponse> findAll() {
        return repo.findAll().stream()
                .map(p -> new ProductResponse(p.getId(), p.getName(), p.getPrice()))
                .toList();
    }

    public ProductResponse create(ProductRequest req) {
        Product p = new Product();
        p.setName(req.name());
        p.setPrice(req.price());
        repo.save(p);
        return new ProductResponse(p.getId(), p.getName(), p.getPrice());
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}