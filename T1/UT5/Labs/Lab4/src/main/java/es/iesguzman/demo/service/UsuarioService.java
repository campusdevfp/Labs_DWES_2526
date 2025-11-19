package es.iesguzman.demo.service;

import es.iesguzman.demo.dto.UsuarioRequestDto;
import es.iesguzman.demo.dto.UsuarioResponseDto;
import es.iesguzman.demo.exception.UsuarioBadRequestException;
import es.iesguzman.demo.exception.UsuarioNotFoundException;
import es.iesguzman.demo.mapper.UsuarioMapper;
import es.iesguzman.demo.model.Usuario;
import es.iesguzman.demo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @CacheEvict(value = "usuarios", allEntries = true)
    public UsuarioResponseDto createUsuario(UsuarioRequestDto dto) {
        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new UsuarioBadRequestException("El username ya existe");
        }
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new UsuarioBadRequestException("El email ya existe");
        }
        Usuario usuario = usuarioMapper.toEntity(dto);
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setActivo(true);
        Usuario saved = usuarioRepository.save(usuario);
        return usuarioMapper.toResponseDto(saved);
    }

    @Cacheable(value = "usuarios", key = "#id")
    public UsuarioResponseDto getUsuarioById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));
        return usuarioMapper.toResponseDto(usuario);
    }

    @Cacheable(value = "usuarios", key = "'all'")
    public List<UsuarioResponseDto> getAllUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Caching(evict = {
        @CacheEvict(value = "usuarios", key = "#id"),
        @CacheEvict(value = "usuarios", allEntries = true)
    })
    public UsuarioResponseDto updateUsuario(Long id, UsuarioRequestDto dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));
        usuario.setUsername(dto.getUsername());
        usuario.setEmail(dto.getEmail());
        Usuario saved = usuarioRepository.save(usuario);
        return usuarioMapper.toResponseDto(saved);
    }

    @Caching(evict = {
        @CacheEvict(value = "usuarios", key = "#id"),
        @CacheEvict(value = "usuarios", allEntries = true)
    })
    public void deleteUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));
        usuarioRepository.delete(usuario);
    }
}
