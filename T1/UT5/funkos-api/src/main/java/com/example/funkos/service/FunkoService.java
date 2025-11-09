package com.example.funkos.service;

import com.example.funkos.model.Funko;
import com.example.funkos.repository.FunkoRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class FunkoService {
    private final FunkoRepository repository;
    public FunkoService(FunkoRepository repository) { this.repository = repository; }

    public List<Funko> getAll(String categoria) {
        return (categoria != null && !categoria.isBlank())
                ? repository.findByCategoriaIgnoreCase(categoria)
                : repository.findAll();
    }
    public Funko getById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException("Funko no encontrado"));
    }
    public Funko create(Funko f) { return repository.save(f); }
    public Funko update(UUID id, Funko nuevo) {
        Funko existente = getById(id);
        nuevo.setId(id);
        nuevo.setFechaCreacion(existente.getFechaCreacion());
        return repository.save(nuevo);
    }
    public Funko patch(UUID id, Map<String, Object> updates) {
        Funko funko = getById(id);
        updates.forEach((k, v) -> {
            switch (k) {
                case "nombre":
                    funko.setNombre((String) v);
                    break;
                case "precio":
                    funko.setPrecio(Double.parseDouble(v.toString()));
                    break;
                case "cantidad":
                    funko.setCantidad(Integer.parseInt(v.toString()));
                    break;
                default:
                    break;
            }
        });
        return repository.save(funko);
    }

    public void delete(UUID id) { repository.deleteById(id); }
}
