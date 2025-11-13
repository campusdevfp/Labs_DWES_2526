package es.iesguzman.demo.services;


import es.iesguzman.demo.models.Cliente;
import es.iesguzman.demo.repositories.ClienteRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    public List<Cliente> findAll() {
        return repository.findAll();
    }

    public Cliente findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente no encontrado"));
    }

    public Cliente save(Cliente cliente) {
        if (cliente.getNombre() == null || cliente.getEmail() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Campos 'nombre' y 'email' son obligatorios");
        }

        try {
            return repository.save(cliente);
        } catch (DataIntegrityViolationException e) {
            // Puede ser un email duplicado (constraint UNIQUE)
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El email '" + cliente.getEmail() + "' ya está registrado");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al guardar el cliente: " + e.getMessage());
        }
    }

    public Cliente update(Long id, Cliente cliente) {
        Cliente existing = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));

        if (cliente.getNombre() == null || cliente.getEmail() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Campos 'nombre' y 'email' son obligatorios");
        }

        existing.setNombre(cliente.getNombre());
        existing.setEmail(cliente.getEmail());

        try {
            return repository.save(existing);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El email '" + cliente.getEmail() + "' ya está registrado");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al actualizar el cliente: " + e.getMessage());
        }
    }

    public void delete(Long id) {
        // Cargar la entidad para que JPA tenga oportunidad de aplicar cascade/orphanRemoval
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
        try {
            repository.delete(cliente);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar el cliente debido a restricciones de integridad: " + e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al eliminar el cliente: " + e.getMessage());
        }
    }
}