package es.iesguzman.demo.services;

import es.iesguzman.demo.models.Cliente;
import es.iesguzman.demo.models.Pedido;
import es.iesguzman.demo.repositories.ClienteRepository;
import es.iesguzman.demo.repositories.PedidoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
public class PedidoService {

    private final PedidoRepository pedidoRepo;
    private final ClienteRepository clienteRepo;

    public PedidoService(PedidoRepository pedidoRepo, ClienteRepository clienteRepo) {
        this.pedidoRepo = pedidoRepo;
        this.clienteRepo = clienteRepo;
    }

    public List<Pedido> findAll() {
        return pedidoRepo.findAll();
    }

    public Pedido findById(Long id) {
        return pedidoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
    }

    public List<Pedido> findByCliente(Long clienteId) {
        if (!clienteRepo.existsById(clienteId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado");
        }
        return pedidoRepo.findByClienteId(clienteId);
    }

    public Pedido create(Long clienteId, Pedido pedido) {
        Cliente cliente = clienteRepo.findById(clienteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));

        if (pedido.getDescripcion() == null || pedido.getTotal() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Campos 'descripcion' y 'total' son obligatorios");
        }

        pedido.setCliente(cliente);
        return pedidoRepo.save(pedido);
    }

    public void delete(Long id) {
        if (!pedidoRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado");
        }
        pedidoRepo.deleteById(id);
    }
}