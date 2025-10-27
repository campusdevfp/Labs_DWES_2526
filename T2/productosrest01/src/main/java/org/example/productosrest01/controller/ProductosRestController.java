package org.example.productosrest01.controller;


import org.example.productosrest01.model.Producto;
import org.example.productosrest01.repository.ProductRepository;
import org.example.productosrest01.service.ProductosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
public class ProductosRestController {
    private final ProductosService productosService;

    @Autowired
    public ProductosRestController(ProductosService productosService) {
        this.productosService = productosService;
    }

    @GetMapping
    public ResponseEntity<List<Producto>> getAllProductos() {
      //  return ResponseEntity.ok(productosService.listar());
      //  return ResponseEntity.status(202).body(productosService.listar());
        return ResponseEntity.ok().header("Pep-Info", "Todo OK").
                body(productosService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoById(@PathVariable long id) {
        Optional<Producto> producto = productosService.buscarPorId(id);
        return ResponseEntity.ok(producto.get());
    }

    @PostMapping()
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        Producto newProducto = productosService.guardar(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProducto);
    }
}
