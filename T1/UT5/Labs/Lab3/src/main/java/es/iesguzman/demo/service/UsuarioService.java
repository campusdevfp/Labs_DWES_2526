package es.iesguzman.demo.service;

import es.iesguzman.demo.dto.UsuarioRequestDto;
import es.iesguzman.demo.dto.UsuarioResponseDto;
import es.iesguzman.demo.exception.UsuarioBadRequestException;
import es.iesguzman.demo.exception.UsuarioNotFoundException;
import es.iesguzman.demo.model.Usuario;
import es.iesguzman.demo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public List<UsuarioResponseDto> findAll() {
        return usuarioRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public UsuarioResponseDto findById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con id: " + id));
        return toResponseDto(usuario);
    }

    public UsuarioResponseDto save(UsuarioRequestDto dto) {
        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new UsuarioBadRequestException("El username ya existe");
        }
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new UsuarioBadRequestException("El email ya existe");
        }
        Usuario usuario = toEntity(dto);
        usuario = usuarioRepository.save(usuario);
        return toResponseDto(usuario);
    }

    public UsuarioResponseDto update(Long id, UsuarioRequestDto dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con id: " + id));
        if (!usuario.getUsername().equals(dto.getUsername()) && usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new UsuarioBadRequestException("El username ya existe");
        }
        if (!usuario.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new UsuarioBadRequestException("El email ya existe");
        }
        updateEntity(usuario, dto);
        usuario = usuarioRepository.save(usuario);
        return toResponseDto(usuario);
    }

    public void deleteById(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new UsuarioNotFoundException("Usuario no encontrado con id: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    public void desactivar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con id: " + id));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    private Usuario toEntity(UsuarioRequestDto dto) {
        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername());
        usuario.setEmail(dto.getEmail());
        usuario.setActivo(true);
        return usuario;
    }

    private void updateEntity(Usuario usuario, UsuarioRequestDto dto) {
        usuario.setUsername(dto.getUsername());
        usuario.setEmail(dto.getEmail());
    }

    private UsuarioResponseDto toResponseDto(Usuario usuario) {
        UsuarioResponseDto dto = new UsuarioResponseDto();
        dto.setId(usuario.getId());
        dto.setUsername(usuario.getUsername());
        dto.setEmail(usuario.getEmail());
        dto.setFechaCreacion(usuario.getFechaCreacion());
        dto.setFechaActualizacion(usuario.getFechaActualizacion());
        dto.setActivo(usuario.getActivo());
        return dto;
    }
}
