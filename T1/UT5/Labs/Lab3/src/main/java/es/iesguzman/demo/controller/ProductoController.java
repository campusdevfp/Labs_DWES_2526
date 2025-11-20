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

    @GetMapping
    public ResponseEntity<List<ProductoResponseDto>> getAllProductos() {
        List<ProductoResponseDto> productos = productoService.findAll();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDto> getProductoById(@PathVariable Long id) {
        ProductoResponseDto producto = productoService.findById(id);
        return ResponseEntity.ok(producto);
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ProductoResponseDto>> getProductosByCategoria(@PathVariable String categoria) {
        List<ProductoResponseDto> productos = productoService.findByCategoria(categoria);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoResponseDto>> buscarProductos(@RequestParam String nombre) {
        List<ProductoResponseDto> productos = productoService.findByNombre(nombre);
        return ResponseEntity.ok(productos);
    }

    @PostMapping
    public ResponseEntity<ProductoResponseDto> createProducto(@Valid @RequestBody ProductoRequestDto dto) {
        ProductoResponseDto producto = productoService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(producto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDto> updateProducto(@PathVariable Long id, @Valid @RequestBody ProductoRequestDto dto) {
        ProductoResponseDto producto = productoService.update(id, dto);
        return ResponseEntity.ok(producto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        productoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivarProducto(@PathVariable Long id) {
        productoService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductoResponseDto> updateStock(@PathVariable Long id, @RequestParam Integer cantidad) {
        ProductoResponseDto producto = productoService.updateStock(id, cantidad);
        return ResponseEntity.ok(producto);
    }
}
