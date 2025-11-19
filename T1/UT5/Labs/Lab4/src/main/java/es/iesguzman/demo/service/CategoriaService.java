package es.iesguzman.demo.service;

import es.iesguzman.demo.dto.CategoriaRequestDto;
import es.iesguzman.demo.dto.CategoriaResponseDto;
import es.iesguzman.demo.exception.CategoriaBadRequestException;
import es.iesguzman.demo.exception.CategoriaNotFoundException;
import es.iesguzman.demo.mapper.CategoriaMapper;
import es.iesguzman.demo.model.Categoria;
import es.iesguzman.demo.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    public CategoriaResponseDto createCategoria(CategoriaRequestDto dto) {
        if (categoriaRepository.existsByNombre(dto.getNombre())) {
            throw new CategoriaBadRequestException("La categoría ya existe");
        }
        Categoria categoria = categoriaMapper.toEntity(dto);
        Categoria saved = categoriaRepository.save(categoria);
        return categoriaMapper.toResponseDto(saved);
    }

    @Cacheable(value = "categorias", key = "#id")
    public CategoriaResponseDto getCategoriaById(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNotFoundException("Categoría no encontrada"));
        return categoriaMapper.toResponseDto(categoria);
    }

    @Cacheable("categorias")
    public List<CategoriaResponseDto> getAllCategorias() {
        return categoriaRepository.findAll().stream()
                .map(categoriaMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "categorias", key = "#id")
    public CategoriaResponseDto updateCategoria(Long id, CategoriaRequestDto dto) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNotFoundException("Categoría no encontrada"));
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        categoria.setImagenUrl(dto.getImagenUrl());
        Categoria saved = categoriaRepository.save(categoria);
        return categoriaMapper.toResponseDto(saved);
    }

    @CacheEvict(value = "categorias", key = "#id")
    public void deleteCategoria(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNotFoundException("Categoría no encontrada"));
        categoriaRepository.delete(categoria);
    }
}
