package com.example.funkos.controller;

import com.example.funkos.model.Funko;
import com.example.funkos.service.FunkoService;
import com.example.funkos.service.StorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.*;

@RestController
@RequestMapping("/funkos")
public class FunkosRestController {
    private final FunkoService service;
    private final StorageService storageService;

    public FunkosRestController(FunkoService service, StorageService storageService) {
        this.service = service;
        this.storageService = storageService;
    }

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

    @PatchMapping(value = "/imagen/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Funko> updateImagen(@PathVariable UUID id, @RequestPart("file") MultipartFile file) {
        if (!file.isEmpty()) {
            String imagen = storageService.store(file);
            String urlImagen = storageService.getUrl(imagen);

            Funko funko = service.getById(id);
            funko.setImagen(urlImagen);
            return ResponseEntity.ok(service.update(id, funko));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se ha enviado la imagen");
        }
    }
}
