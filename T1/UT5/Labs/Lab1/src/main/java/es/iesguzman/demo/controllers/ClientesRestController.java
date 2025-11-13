package es.iesguzman.demo.controllers;

import es.iesguzman.demo.models.Cliente;
import es.iesguzman.demo.services.ClienteService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClientesRestController {
    private final ClienteService service;

    public ClientesRestController(ClienteService service) {
        this.service = service;
    }

    @GetMapping
    public List<Cliente> getAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<Cliente> create(@RequestBody Cliente c) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(c));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> update(@PathVariable("id") Long id, @RequestBody Cliente c) {
        Cliente existing = service.findById(id);
        existing.setNombre(c.getNombre());
        existing.setEmail(c.getEmail());
        return ResponseEntity.ok(service.save(existing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
