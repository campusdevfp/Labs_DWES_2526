package app.productosrest01.controller;

import app.productosrest01.model.Producto;
import app.productosrest01.repository.ProductoRepository;
import app.productosrest01.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/productos")
public class ProductosRestController {


    private final ProductoService productoService;

    @Autowired
    public ProductosRestController(ProductoService productoService) {

        this.productoService = productoService;
    }



    // --- GET /api/productos ---
    @GetMapping
    public ResponseEntity<List<Producto>> getAllProducts() {
          return ResponseEntity.ok(productoService.listar());
//        return ResponseEntity.ok().header("Pep-Info", "todo OK").body(productoService.listar());
//        return ResponseEntity.status(202).body(productoService.listar());
    }

   
}
