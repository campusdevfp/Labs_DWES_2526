package com.isidraguzman.proyectointegrador.repository;

import com.isidraguzman.proyectointegrador.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {}