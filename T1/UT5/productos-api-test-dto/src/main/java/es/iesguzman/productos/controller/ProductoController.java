package es.iesguzman.productos.controller;

import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import es.iesguzman.productos.dto.*;
import es.iesguzman.productos.mapper.ProductoMapper;
import es.iesguzman.productos.service.ProductoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@Validated
public class ProductoController {
    private final ProductoService service;
    private final ProductoMapper mapper;

    public ProductoController(ProductoService service, ProductoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("/{id}")
    public ProductoResponseDto getById(@PathVariable @Min(1) Long id) {
        return mapper.toResponse(service.findById(id));
    }

    @GetMapping("/categoria/{cat}")
    public List<ProductoResponseDto> getByCategoria(@PathVariable String cat) {
        return service.findByCategoria(cat).stream().map(mapper::toResponse).toList();
    }

    @PostMapping
    public ResponseEntity<ProductoResponseDto> create(@Valid @RequestBody ProductoRequestDto dto) {
        var producto = service.save(mapper.toModel(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(producto));
    }

    @PutMapping("/{id}")
    public ProductoResponseDto update(@PathVariable Long id, @Valid @RequestBody ProductoRequestDto dto) {
        var producto = mapper.toModel(dto);
        producto.setId(id);
        return mapper.toResponse(service.update(producto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @DeleteMapping("/cache")
    public void clearCache() {
        service.clearCache();
    }
}
