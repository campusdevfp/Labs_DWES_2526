package es.iesguzman.demo.service;

import es.iesguzman.demo.dto.ProductoRequestDto;
import es.iesguzman.demo.dto.ProductoResponseDto;
import es.iesguzman.demo.exception.ProductoNotFoundException;
import es.iesguzman.demo.model.Producto;
import es.iesguzman.demo.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;

    public List<ProductoResponseDto> findAll() {
        List<Producto> productos = productoRepository.findAll();
        List<ProductoResponseDto> resultado = new ArrayList<>();
        for (Producto producto : productos) {
            resultado.add(toResponseDto(producto));
        }
        return resultado;
    }

    public ProductoResponseDto findById(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado con id: " + id));
        return toResponseDto(producto);
    }

    public List<ProductoResponseDto> findByCategoria(String categoria) {
        List<Producto> productos = productoRepository.findByCategoria(categoria);
        List<ProductoResponseDto> resultado = new ArrayList<>();
        for (Producto producto : productos) {
            resultado.add(toResponseDto(producto));
        }
        return resultado;
    }

    public List<ProductoResponseDto> findByNombre(String nombre) {
        List<Producto> productos = productoRepository.findByNombreContainingIgnoreCase(nombre);
        List<ProductoResponseDto> resultado = new ArrayList<>();
        for (Producto producto : productos) {
            resultado.add(toResponseDto(producto));
        }
        return resultado;
    }

    public ProductoResponseDto save(ProductoRequestDto dto) {
        Producto producto = toEntity(dto);
        producto = productoRepository.save(producto);
        return toResponseDto(producto);
    }

    public ProductoResponseDto update(Long id, ProductoRequestDto dto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado con id: " + id));
        updateEntity(producto, dto);
        producto = productoRepository.save(producto);
        return toResponseDto(producto);
    }

    public void deleteById(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ProductoNotFoundException("Producto no encontrado con id: " + id);
        }
        productoRepository.deleteById(id);
    }

    public void desactivar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado con id: " + id));
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    public ProductoResponseDto updateStock(Long id, Integer cantidad) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado con id: " + id));
        producto.setStock(producto.getStock() + cantidad);
        producto = productoRepository.save(producto);
        return toResponseDto(producto);
    }

    private Producto toEntity(ProductoRequestDto dto) {
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setCategoria(dto.getCategoria());
        producto.setImagenUrl(dto.getImagenUrl());
        producto.setActivo(true);
        return producto;
    }

    private void updateEntity(Producto producto, ProductoRequestDto dto) {
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setCategoria(dto.getCategoria());
        producto.setImagenUrl(dto.getImagenUrl());
    }

    private ProductoResponseDto toResponseDto(Producto producto) {
        ProductoResponseDto dto = new ProductoResponseDto();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setStock(producto.getStock());
        dto.setCategoria(producto.getCategoria());
        dto.setImagenUrl(producto.getImagenUrl());
        dto.setFechaCreacion(producto.getFechaCreacion());
        dto.setFechaActualizacion(producto.getFechaActualizacion());
        dto.setActivo(producto.getActivo());
        return dto;
    }
}
