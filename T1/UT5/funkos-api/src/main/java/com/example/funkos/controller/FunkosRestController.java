package com.example.funkos.controller;

import com.example.funkos.model.Funko;
import com.example.funkos.service.FunkoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/funkos")
public class FunkosRestController {
    private final FunkoService service;
    public FunkosRestController(FunkoService service) { this.service = service; }

    @GetMapping
    public List<Funko> getAll(@RequestParam(required = false) String categoria) {
        return service.getAll(categoria);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Funko> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }
    @PostMapping
    public ResponseEntity<Funko> create(@RequestBody Funko f) {
        return ResponseEntity.ok(service.create(f));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Funko> update(@PathVariable UUID id, @RequestBody Funko f) {
        return ResponseEntity.ok(service.update(id, f));
    }
    @PatchMapping("/{id}")
    public ResponseEntity<Funko> patch(@PathVariable UUID id, @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(service.patch(id, updates));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok("Funko eliminado correctamente");
    }
}
