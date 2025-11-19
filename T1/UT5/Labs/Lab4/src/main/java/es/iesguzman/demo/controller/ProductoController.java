package es.iesguzman.demo.controller;

import es.iesguzman.demo.dto.ProductoRequestDto;
import es.iesguzman.demo.dto.ProductoResponseDto;
import es.iesguzman.demo.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping
    public ResponseEntity<ProductoResponseDto> createProducto(@Valid @RequestBody ProductoRequestDto dto) {
        ProductoResponseDto response = productoService.createProducto(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDto> getProductoById(@PathVariable("id") Long id) {
        ProductoResponseDto response = productoService.getProductoById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponseDto>> getAllProductos() {
        List<ProductoResponseDto> response = productoService.getAllProductos();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ProductoResponseDto>> getProductosByCategoria(@PathVariable("categoriaId") Long categoriaId) {
        List<ProductoResponseDto> response = productoService.getProductosByCategoria(categoriaId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductoResponseDto>> searchProductos(@RequestParam("nombre") String nombre) {
        List<ProductoResponseDto> response = productoService.searchProductosByNombre(nombre);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDto> updateProducto(@PathVariable("id") Long id, @Valid @RequestBody ProductoRequestDto dto) {
        ProductoResponseDto response = productoService.updateProducto(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable("id") Long id) {
        productoService.deleteProducto(id);
        return ResponseEntity.noContent().build();
    }
}
