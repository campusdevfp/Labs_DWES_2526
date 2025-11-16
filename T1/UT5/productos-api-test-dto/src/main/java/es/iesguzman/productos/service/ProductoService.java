package es.iesguzman.productos.service;

import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import es.iesguzman.productos.domain.Producto;
import es.iesguzman.productos.repository.ProductoRepository;
import es.iesguzman.productos.exception.ProductoNotFoundException;
import es.iesguzman.productos.validator.ProductoValidator;
import java.util.List;

@Service
@CacheConfig(cacheNames = {"productos"})
public class ProductoService {
    private final ProductoRepository repo;
    private final ProductoValidator validator;

    public ProductoService(ProductoRepository repo, ProductoValidator validator) {
        this.repo = repo;
        this.validator = validator;
    }

    @Cacheable(key = "#id")
    public Producto findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new ProductoNotFoundException("No existe producto con id " + id));
    }

    @Cacheable(key = "#categoria")
    public List<Producto> findByCategoria(String categoria) {
        return repo.findByCategoriaContainingIgnoreCase(categoria);
    }

    @CachePut(key = "#result.id")
    public Producto save(Producto p) {
        validator.validate(p);
        return repo.save(p);
    }

    @CachePut(key = "#result.id")
    public Producto update(Producto p) {
        validator.validate(p);
        if (!repo.existsById(p.getId()))
            throw new ProductoNotFoundException("No existe producto con id " + p.getId());
        return repo.save(p);
    }

    @CacheEvict(key = "#id")
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @CacheEvict(allEntries = true)
    public void clearCache() {}
}
