package es.iesguzman.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.iesguzman.demo.dto.UsuarioRequestDto;
import es.iesguzman.demo.dto.UsuarioResponseDto;
import es.iesguzman.demo.exception.UsuarioNotFoundException;
import es.iesguzman.demo.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class UsuarioControllerIntegrationTest {

    @Autowired
    private ObjectMapper mapper;
    @MockBean
    UsuarioService usuarioService;
    @Autowired
    MockMvc mockMvc;
    UsuarioResponseDto usuario = new UsuarioResponseDto(1L, "usuario1", "usuario1@example.com", true, null, null);
    String myEndpoint = "/api/usuarios";

    @Test
    void testGetAllUsuarios() throws Exception {
        when(usuarioService.findAll())
                .thenReturn(List.of(usuario));

        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        List<UsuarioResponseDto> res = mapper.readValue(response.getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, UsuarioResponseDto.class));

        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> assertTrue(response.getContentAsString().contains("\"username\":\"usuario1\"")),
                () -> assertTrue(res.size() > 0),
                () -> assertTrue(res.stream().anyMatch(u -> u.getUsername().equals("usuario1")))
        );

        verify(usuarioService, times(1)).findAll();
    }

    @Test
    void testGetUsuarioById() throws Exception {
        when(usuarioService.findById(usuario.getId()))
                .thenReturn(usuario);

        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint + "/" + usuario.getId())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        UsuarioResponseDto res = mapper.readValue(response.getContentAsString(), UsuarioResponseDto.class);

        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> assertEquals(res.getId(), usuario.getId())
        );

        verify(usuarioService, times(1)).findById(usuario.getId());
    }

    @Test
    void testGetUsuarioByIdNotFound() throws Exception {
        when(usuarioService.findById(999L))
                .thenThrow(new UsuarioNotFoundException("Usuario no encontrado"));

        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint + "/999")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.NOT_FOUND.value())
        );

        verify(usuarioService, times(1)).findById(999L);
    }

    @Test
    void testCreateUsuario() throws Exception {
        UsuarioRequestDto nueva = new UsuarioRequestDto();
        nueva.setUsername("usuarioNuevo");
        nueva.setEmail("nuevo@example.com");

        UsuarioResponseDto creada = new UsuarioResponseDto(10L, "usuarioNuevo", "nuevo@example.com", true, null, null);
        when(usuarioService.save(any(UsuarioRequestDto.class)))
                .thenReturn(creada);

        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(nueva)))
                .andReturn().getResponse();

        UsuarioResponseDto res = mapper.readValue(response.getContentAsString(), UsuarioResponseDto.class);

        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.CREATED.value()),
                () -> assertEquals(res.getId(), 10L),
                () -> assertEquals("usuarioNuevo", res.getUsername())
        );

        verify(usuarioService, times(1)).save(any(UsuarioRequestDto.class));
    }

    @Test
    void testCreateUsuarioInvalidData() throws Exception {
        UsuarioRequestDto nueva = new UsuarioRequestDto();
        nueva.setUsername("");
        nueva.setEmail("invalid-email");

        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(nueva)))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.BAD_REQUEST.value())
        );
    }

    @Test
    void testUpdateUsuario() throws Exception {
        UsuarioRequestDto update = new UsuarioRequestDto();
        update.setUsername("usuarioActualizado");
        update.setEmail("actualizado@example.com");

        UsuarioResponseDto actualizada = new UsuarioResponseDto(1L, "usuarioActualizado", "actualizado@example.com", true, null, null);
        when(usuarioService.update(eq(1L), any(UsuarioRequestDto.class)))
                .thenReturn(actualizada);

        MockHttpServletResponse response = mockMvc.perform(
                        put(myEndpoint + "/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(update)))
                .andReturn().getResponse();

        UsuarioResponseDto res = mapper.readValue(response.getContentAsString(), UsuarioResponseDto.class);

        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> assertEquals(res.getUsername(), "usuarioActualizado")
        );

        verify(usuarioService, times(1)).update(eq(1L), any(UsuarioRequestDto.class));
    }

    @Test
    void testDeleteUsuario() throws Exception {
        doNothing().when(usuarioService).deleteById(1L);

        MockHttpServletResponse response = mockMvc.perform(
                        delete(myEndpoint + "/1"))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.NO_CONTENT.value())
        );

        verify(usuarioService, times(1)).deleteById(1L);
    }
}
