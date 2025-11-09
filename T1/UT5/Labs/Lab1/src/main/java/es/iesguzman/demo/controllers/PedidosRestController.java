package es.iesguzman.demo.controllers;

import es.iesguzman.demo.models.Pedido;
import es.iesguzman.demo.services.PedidoService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@RestController
@RequestMapping("/api")
public class PedidosRestController {

    private final PedidoService service;

    public PedidosRestController(PedidoService service) {
        this.service = service;
    }

    /**
     * GET /api/pedidos
     * Lista todos los pedidos existentes
     */
    @GetMapping("/pedidos")
    public List<Pedido> getAll() {
        return service.findAll();
    }

    /**
     * GET /api/clientes/{id}/pedidos
     * Lista todos los pedidos de un cliente concreto
     */
    @GetMapping("/clientes/{id}/pedidos")
    public List<Pedido> getPedidosCliente(@PathVariable("id") Long id) {
        return service.findByCliente(id);
    }

    /**
     * POST /api/clientes/{id}/pedidos
     * Crea un pedido nuevo para un cliente existente
     */
    @PostMapping("/clientes/{id}/pedidos")
    public ResponseEntity<Pedido> createPedido(
            @PathVariable("id") Long id,
            @RequestBody Pedido pedido
    ) {
        try {
            Pedido nuevo = service.create(id, pedido);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (ResponseStatusException ex) {
            // Excepciones controladas del servicio
            throw ex;
        } catch (Exception ex) {
            // Cualquier otro error no previsto
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al crear el pedido: " + ex.getMessage());
        }
    }

    /**
     * DELETE /api/pedidos/{id}
     * Elimina un pedido existente
     */
    @DeleteMapping("/pedidos/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable("id") Long id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al eliminar el pedido: " + ex.getMessage());
        }
    }
}