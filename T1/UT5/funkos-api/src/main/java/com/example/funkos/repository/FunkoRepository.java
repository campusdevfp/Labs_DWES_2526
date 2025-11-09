package com.example.funkos.repository;

import com.example.funkos.model.Funko;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface FunkoRepository extends JpaRepository<Funko, UUID> {
    List<Funko> findByCategoriaIgnoreCase(String categoria);
}
