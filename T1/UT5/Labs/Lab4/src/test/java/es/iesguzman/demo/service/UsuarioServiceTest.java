package es.iesguzman.demo.service;

import es.iesguzman.demo.dto.UsuarioRequestDto;
import es.iesguzman.demo.dto.UsuarioResponseDto;
import es.iesguzman.demo.mapper.UsuarioMapper;
import es.iesguzman.demo.model.Usuario;
import es.iesguzman.demo.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test unitario del servicio UsuarioService.
 * Usa Mockito para simular las dependencias (repository y mapper).
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    // Mock del repositorio: simula las operaciones de base de datos
    @Mock
    UsuarioRepository usuarioRepository;

    // Mock del mapper: simula las conversiones entre entidad y DTO
    @Mock
    UsuarioMapper usuarioMapper;

    // Servicio a probar: Mockito inyecta automáticamente los mocks anteriores
    @InjectMocks
    UsuarioService usuarioService;

    UsuarioRequestDto requestDto;
    Usuario usuario;
    UsuarioResponseDto responseDto;

    /**
     * Configuración inicial antes de cada test.
     * Prepara objetos de prueba reutilizables usando el mapper.
     */
    @BeforeEach
    void setUp() {
        // DTO de entrada (lo que recibe el controlador)
        requestDto = new UsuarioRequestDto();
        requestDto.setUsername("user1");
        requestDto.setEmail("user1@example.com");

        // Crear usuario usando el mapper real (simulado con mock en los tests)
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername(requestDto.getUsername());
        usuario.setEmail(requestDto.getEmail());
        usuario.setActivo(true);
        usuario.setFechaCreacion(LocalDateTime.now());

        // Crear responseDto usando el mapper real (simulado con mock en los tests)
        responseDto = new UsuarioResponseDto();
        responseDto.setId(usuario.getId());
        responseDto.setUsername(usuario.getUsername());
        responseDto.setEmail(usuario.getEmail());
        responseDto.setActivo(usuario.getActivo());
        responseDto.setFechaCreacion(usuario.getFechaCreacion());
    }

    /**
     * Test: getAllUsuarios debe devolver una lista de usuarios.
     *
     * Pasos:
     * 1. Simular que el repositorio devuelve 1 usuario
     * 2. Simular que el mapper convierte la entidad a DTO
     * 3. Llamar al servicio
     * 4. Verificar que la lista tiene tamaño 1 y contiene el username esperado
     * 5. Verificar que se llamó al repositorio exactamente 1 vez
     */
    @Test
    void getAllUsuarios_returnsList() {
        // Configurar comportamiento de los mocks
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));
        when(usuarioMapper.toResponseDto(usuario)).thenReturn(responseDto);

        // Ejecutar el método a probar
        // Importante, aquí se llama al mockeado usuarioRepository y usuarioMapper
        // De esta forma no usamos la base de datos real ni el mapper real
        List<UsuarioResponseDto> list = usuarioService.getAllUsuarios();

        // Verificar resultados
        assertEquals(1, list.size());
        assertEquals("user1", list.get(0).getUsername());

        // Verificar que se llamó al método del repositorio
        verify(usuarioRepository, times(1)).findAll();
    }

    /**
     * Test: getUsuarioById debe devolver un usuario cuando existe.
     *
     * Pasos:
     * 1. Simular que el repositorio encuentra el usuario por ID
     * 2. Simular que el mapper convierte la entidad a DTO
     * 3. Llamar al servicio con ID 1L
     * 4. Verificar que el resultado tiene el ID y username correctos
     * 5. Verificar que se llamó al repositorio con el ID correcto
     */
    @Test
    void getUsuarioById_success() {
        // Configurar: el repositorio devuelve un Optional con el usuario
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toResponseDto(usuario)).thenReturn(responseDto);

        // Ejecutar
        UsuarioResponseDto result = usuarioService.getUsuarioById(1L);

        // Verificar
        assertEquals(1L, result.getId());
        assertEquals("user1", result.getUsername());

        // Verificar que se consultó el repositorio
        verify(usuarioRepository, times(1)).findById(1L);
    }

    /**
     * Test: createUsuario debe crear un nuevo usuario correctamente.
     *
     * Pasos:
     * 1. Simular que NO existe usuario con ese username/email (evitar duplicados)
     * 2. Simular que el mapper convierte el DTO a entidad
     * 3. Simular que el repositorio guarda y devuelve el usuario con ID asignado
     * 4. Simular que el mapper convierte la entidad guardada a DTO de respuesta
     * 5. Llamar al servicio
     * 6. Verificar que el resultado no es null y tiene los datos esperados
     * 7. Verificar que se llamó al método save del repositorio
     */
    @Test
    void createUsuario() {
        // Configurar: no hay usuarios duplicados
        when(usuarioRepository.existsByUsername("user1")).thenReturn(false);
        when(usuarioRepository.existsByEmail("user1@example.com")).thenReturn(false);

        // Configurar: conversión DTO -> Entidad
        when(usuarioMapper.toEntity(requestDto)).thenReturn(usuario);

        // Configurar: el repositorio guarda y devuelve el usuario con ID
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        // Configurar: conversión Entidad -> DTO de respuesta
        when(usuarioMapper.toResponseDto(usuario)).thenReturn(responseDto);

        // Ejecutar
        UsuarioResponseDto result = usuarioService.createUsuario(requestDto);

        // Verificar
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("user1", result.getUsername());

        // Verificar que se guardó en el repositorio
        verify(usuarioRepository, times(1)).save(usuario);
    }

    /**
     * Test: deleteUsuario debe eliminar un usuario correctamente.
     *
     * Pasos:
     * 1. Simular que el repositorio encuentra el usuario por ID
     * 2. Llamar al servicio para eliminar
     * 3. Verificar que se llamó al método delete del repositorio con el usuario correcto
     */
    @Test
    void deleteUsuario_success() {
        // Configurar: el repositorio encuentra el usuario
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Ejecutar
        usuarioService.deleteUsuario(1L);

        // Verificar que se llamó al método delete
        verify(usuarioRepository, times(1)).delete(usuario);
    }
}
