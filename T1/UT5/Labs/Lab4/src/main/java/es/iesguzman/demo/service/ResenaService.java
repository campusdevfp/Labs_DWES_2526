package es.iesguzman.demo.service;

import es.iesguzman.demo.dto.ResenaRequestDto;
import es.iesguzman.demo.dto.ResenaResponseDto;
import es.iesguzman.demo.exception.ResenaBadRequestException;
import es.iesguzman.demo.exception.ResenaNotFoundException;
import es.iesguzman.demo.mapper.ResenaMapper;
import es.iesguzman.demo.model.Producto;
import es.iesguzman.demo.model.Resena;
import es.iesguzman.demo.model.Usuario;
import es.iesguzman.demo.repository.ProductoRepository;
import es.iesguzman.demo.repository.ResenaRepository;
import es.iesguzman.demo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResenaService {

    private final ResenaRepository resenaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final ResenaMapper resenaMapper;

    public ResenaResponseDto createResena(ResenaRequestDto dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResenaBadRequestException("Usuario no encontrado"));
        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new ResenaBadRequestException("Producto no encontrado"));
        Resena resena = resenaMapper.toEntity(dto);
        resena.setUsuario(usuario);
        resena.setProducto(producto);
        Resena saved = resenaRepository.save(resena);
        return resenaMapper.toResponseDto(saved);
    }

    @Cacheable(value = "resenas", key = "#id")
    public ResenaResponseDto getResenaById(Long id) {
        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> new ResenaNotFoundException("Reseña no encontrada"));
        return resenaMapper.toResponseDto(resena);
    }

    @Cacheable("resenas")
    public List<ResenaResponseDto> getAllResenas() {
        return resenaRepository.findAll().stream()
                .map(resenaMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Cacheable("resenas")
    public List<ResenaResponseDto> getResenasByProducto(Long productoId) {
        return resenaRepository.findByProductoId(productoId).stream()
                .map(resenaMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Cacheable("resenas")
    public List<ResenaResponseDto> getResenasByUsuario(Long usuarioId) {
        return resenaRepository.findByUsuarioId(usuarioId).stream()
                .map(resenaMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "resenas", key = "#id")
    public void deleteResena(Long id) {
        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> new ResenaNotFoundException("Reseña no encontrada"));
        resenaRepository.delete(resena);
    }

    @CacheEvict(value = "resenas", key = "#id")
    public ResenaResponseDto updateResena(Long id, ResenaRequestDto dto) {
        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> new ResenaNotFoundException("Reseña no encontrada"));
        resena.setCalificacion(dto.getCalificacion());
        resena.setComentario(dto.getComentario());
        Resena saved = resenaRepository.save(resena);
        return resenaMapper.toResponseDto(saved);
    }
}
