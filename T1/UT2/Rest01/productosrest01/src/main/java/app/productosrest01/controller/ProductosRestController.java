package app.productosrest01.controller;

import app.productosrest01.model.Producto;
import app.productosrest01.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
public class ProductosRestController {

    private final ProductoRepository productosRepository;

    @Autowired
    public ProductosRestController(ProductoRepository productosRepository) {
        this.productosRepository = productosRepository;
    }



    // --- GET /api/productos ---
    @GetMapping
    public ResponseEntity<List<Producto>> getAllProducts() {
//        return ResponseEntity.ok(productosRepository.findAll());
        return ResponseEntity.status(202).body(productosRepository.findAll());
    }

    // 🔹 2️⃣ Buscar productos por nombre (parámetro obligatorio)
    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> searchProducts(@RequestParam String nombre) {
        List<Producto> filtrados = productosRepository.findAll().stream()
                .filter(p -> p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .toList();

        return ResponseEntity.ok(filtrados);
    }

    // --- GET: buscar producto por ID ---
    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductById(@PathVariable Long id) {
        Optional<Producto> producto = productosRepository.findById(id);
        return producto
                .map(p -> ResponseEntity.status(HttpStatus.OK).body(p))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    // --- POST: crear un nuevo producto ---
    @PostMapping
    public ResponseEntity<Producto> createProduct(@RequestBody Producto producto) {
        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
        Producto nuevo = productosRepository.save(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    // Post: crear un nuevo producto y validar que no exista otro con el mismo nombre y id distinto
//    @PostMapping
//    public ResponseEntity<?> createProductVerifica(@RequestBody Producto producto) {
//
//        // Validar nombre vacío
//        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body("El nombre del producto es obligatorio");
//        }
//
//        // Buscar si ya existe un producto con el mismo nombre (ignora mayúsculas/minúsculas)
//        boolean existe = productosRepository.findAll().stream()
//                .anyMatch(p -> p.getNombre().equalsIgnoreCase(producto.getNombre()));
//
//        if (existe) {
//            return ResponseEntity.status(HttpStatus.CONFLICT)
//                    .body("Ya existe un producto con ese nombre");
//        }
//
//        // Guardar si no está repetido
//        Producto nuevo = productosRepository.save(producto);
//        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
//    }


    // --- PUT: actualizar un producto completo ---
    @PutMapping("/{id}")
    public ResponseEntity<Producto> updateProduct(
            @PathVariable Long id,
            @RequestBody Producto productoActualizado) {

        Optional<Producto> productoOpt = productosRepository.findById(id);
        if (productoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Producto producto = productoOpt.get();
        producto.setNombre(productoActualizado.getNombre());
        producto.setPrecio(productoActualizado.getPrecio());

        Producto actualizado = productosRepository.save(producto);
        return ResponseEntity.status(HttpStatus.OK).body(actualizado);
    }

    // --- PATCH: actualización parcial ---
    @PatchMapping("/{id}")
    public ResponseEntity<Producto> patchProduct(
            @PathVariable Long id,
            @RequestBody Producto cambios) {

        Optional<Producto> productoOpt = productosRepository.findById(id);
        if (productoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Producto producto = productoOpt.get();

        if (cambios.getNombre() != null && !cambios.getNombre().isBlank()) {
            producto.setNombre(cambios.getNombre());
        }
        if (cambios.getPrecio() != null) {
            producto.setPrecio(cambios.getPrecio());
        }

        Producto actualizado = productosRepository.save(producto);
        return ResponseEntity.status(HttpStatus.OK).body(actualizado);
    }

    // --- DELETE: eliminar producto ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (!productosRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        productosRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
