package es.iesguzman.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import es.iesguzman.demo.model.Usuario  ;
import es.iesguzman.demo.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final UsuarioRepository clienteRepository;

    public ClienteController(UsuarioRepository usuarioRepository) {
        this.clienteRepository = usuarioRepository;
    }

    @GetMapping
    public List<Usuario> listarClientes() {
        return clienteRepository.findAll();
    }
}
