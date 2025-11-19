package es.iesguzman.demo.service;

import es.iesguzman.demo.dto.ProductoRequestDto;
import es.iesguzman.demo.dto.ProductoResponseDto;
import es.iesguzman.demo.exception.ProductoNotFoundException;
import es.iesguzman.demo.exception.ProductoBadRequestException;
import es.iesguzman.demo.mapper.ProductoMapper;
import es.iesguzman.demo.model.Categoria;
import es.iesguzman.demo.model.Producto;
import es.iesguzman.demo.repository.CategoriaRepository;
import es.iesguzman.demo.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper productoMapper;

    @CacheEvict(value = "productos", allEntries = true)
    public ProductoResponseDto createProducto(ProductoRequestDto dto) {
        if (productoRepository.existsByNombre(dto.getNombre())) {
            throw new ProductoBadRequestException("El producto ya existe");
        }
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ProductoBadRequestException("Categoría no encontrada"));
        Producto producto = productoMapper.toEntity(dto);
        producto.setCategoria(categoria);
        producto.setActivo(true);
        Producto saved = productoRepository.save(producto);
        return productoMapper.toResponseDto(saved);
    }

    @Cacheable(value = "productos", key = "#id")
    public ProductoResponseDto getProductoById(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado"));
        return productoMapper.toResponseDto(producto);
    }

    @Cacheable("productos")
    public List<ProductoResponseDto> getAllProductos() {
        return productoRepository.findByActivoTrue().stream()
                .map(productoMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Cacheable("productos")
    public List<ProductoResponseDto> getProductosByCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId).stream()
                .filter(Producto::getActivo)
                .map(productoMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Cacheable("productos")
    public List<ProductoResponseDto> searchProductosByNombre(String nombre) {
        return productoRepository.findByNombreContainingAndActivoTrue(nombre).stream()
                .map(productoMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Caching(evict = {
        @CacheEvict(value = "productos", key = "#id"),
        @CacheEvict(value = "productos", allEntries = true)
    })
    public ProductoResponseDto updateProducto(Long id, ProductoRequestDto dto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado"));
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ProductoBadRequestException("Categoría no encontrada"));
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setCategoria(categoria);
        producto.setImagenUrl(dto.getImagenUrl());
        Producto saved = productoRepository.save(producto);
        return productoMapper.toResponseDto(saved);
    }

    @Caching(evict = {
        @CacheEvict(value = "productos", key = "#id"),
        @CacheEvict(value = "productos", allEntries = true)
    })
    public void deleteProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado"));
        producto.setActivo(false);
        productoRepository.save(producto);
    }
}
