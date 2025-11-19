package es.iesguzman.demo.controller;

import es.iesguzman.demo.dto.PedidoRequestDto;
import es.iesguzman.demo.dto.PedidoResponseDto;
import es.iesguzman.demo.model.EstadoPedido;
import es.iesguzman.demo.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoResponseDto> createPedido(@Valid @RequestBody PedidoRequestDto dto) {
        PedidoResponseDto response = pedidoService.createPedido(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDto> getPedidoById(@PathVariable("id") Long id) {
        PedidoResponseDto response = pedidoService.getPedidoById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponseDto>> getAllPedidos() {
        List<PedidoResponseDto> response = pedidoService.getAllPedidos();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PedidoResponseDto>> getPedidosByUsuario(@PathVariable("usuarioId") Long usuarioId) {
        List<PedidoResponseDto> response = pedidoService.getPedidosByUsuario(usuarioId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<PedidoResponseDto> updateEstadoPedido(@PathVariable("id") Long id, @RequestBody EstadoPedido estado) {
        PedidoResponseDto response = pedidoService.updateEstadoPedido(id, estado);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable("id") Long id) {
        pedidoService.deletePedido(id);
        return ResponseEntity.noContent().build();
    }
}
