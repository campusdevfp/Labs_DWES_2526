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

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(
            @PathVariable long id,
            @RequestBody Producto producto) {

        Optional<Producto> productoOriginal = productosService.buscarPorId(id);
        if (!productoOriginal.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Producto productoActualizado = productoOriginal.get();
        productoActualizado.setNombre(producto.getNombre());
        productoActualizado.setPrecio(producto.getPrecio());

        Producto productoGuardado = productosService.guardar(productoActualizado);
        return ResponseEntity.ok(productoGuardado);

    }

    @PatchMapping("/{id}")
    public ResponseEntity<Producto> actualizarParcialProducto(
            @PathVariable Long id,
            @RequestBody Producto productoActualizado
    ){
        Optional<Producto> productoOptional = productosService.buscarPorId(id);
        if (!productoOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Producto producto = productoOptional.get();
        if (productoActualizado.getNombre() != null) {
            producto.setNombre(productoActualizado.getNombre());
        }
        if (productoActualizado.getPrecio() != null) {
            producto.setPrecio(productoActualizado.getPrecio());
        }
        Producto productoGuardado = productosService.guardar(producto);
        return ResponseEntity.ok(productoGuardado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Producto> eliminarProducto(@PathVariable long id) {
        Optional<Producto> productoOptional = productosService.buscarPorId(id);
        if (!productoOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        productosService.eliminar(id);
        return ResponseEntity.noContent().build();
    }




}
