package es.iesguzman.demo.controller;

import es.iesguzman.demo.dto.ResenaRequestDto;
import es.iesguzman.demo.dto.ResenaResponseDto;
import es.iesguzman.demo.service.ResenaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resenas")
@RequiredArgsConstructor
public class ResenaController {

    private final ResenaService resenaService;

    @PostMapping
    public ResponseEntity<ResenaResponseDto> createResena(@Valid @RequestBody ResenaRequestDto dto) {
        ResenaResponseDto response = resenaService.createResena(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResenaResponseDto> getResenaById(@PathVariable("id") Long id) {
        ResenaResponseDto response = resenaService.getResenaById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ResenaResponseDto>> getAllResenas() {
        List<ResenaResponseDto> response = resenaService.getAllResenas();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<ResenaResponseDto>> getResenasByProducto(@PathVariable("productoId") Long productoId) {
        List<ResenaResponseDto> response = resenaService.getResenasByProducto(productoId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ResenaResponseDto>> getResenasByUsuario(@PathVariable("usuarioId") Long usuarioId) {
        List<ResenaResponseDto> response = resenaService.getResenasByUsuario(usuarioId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResenaResponseDto> updateResena(@PathVariable("id") Long id, @Valid @RequestBody ResenaRequestDto dto) {
        ResenaResponseDto response = resenaService.updateResena(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResena(@PathVariable("id") Long id) {
        resenaService.deleteResena(id);
        return ResponseEntity.noContent().build();
    }
}
