package es.iesguzman.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.iesguzman.demo.dto.ProductoRequestDto;
import es.iesguzman.demo.dto.ProductoResponseDto;
import es.iesguzman.demo.exception.ProductoNotFoundException;
import es.iesguzman.demo.service.ProductoService;
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
public class

ProductoControllerIntegrationTest {

    @Autowired
    private ObjectMapper mapper;
    @MockBean
    ProductoService productoService;
    @Autowired
    MockMvc mockMvc;
    String myEndpoint = "/api/productos";

    private ProductoResponseDto createProductoResponse(Long id, String nombre, Double precio, Integer stock, String categoria) {
        ProductoResponseDto dto = new ProductoResponseDto();
        dto.setId(id);
        dto.setNombre(nombre);
        dto.setPrecio(precio);
        dto.setStock(stock);
        dto.setCategoria(categoria);
        return dto;
    }

    @Test
    void testGetAllProductos() throws Exception {
        ProductoResponseDto producto = createProductoResponse(1L, "Producto 1", 100.0, 10, "Categoria 1");

        when(productoService.findAll())
                .thenReturn(List.of(producto));

        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        List<ProductoResponseDto> res = mapper.readValue(response.getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, ProductoResponseDto.class));

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("\"nombre\":\"Producto 1\"")),
                () -> assertFalse(res.isEmpty()),
                () -> assertTrue(res.stream().anyMatch(p -> p.getNombre().equals("Producto 1")))
        );

        verify(productoService, times(1)).findAll();
    }

    @Test
    void testGetProductoById() throws Exception {
        ProductoResponseDto producto = createProductoResponse(1L, "Producto 1", 100.0, 10, "Categoria 1");

        when(productoService.findById(producto.getId()))
                .thenReturn(producto);

        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint + "/" + producto.getId())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        ProductoResponseDto res = mapper.readValue(response.getContentAsString(), ProductoResponseDto.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(producto.getId(), res.getId())
        );

        verify(productoService, times(1)).findById(producto.getId());
    }

    @Test
    void testGetProductoByIdNotFound() throws Exception {
        when(productoService.findById(-1000L))
                .thenThrow(new ProductoNotFoundException("Producto no encontrado"));

        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint + "/" + -1000L)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus())
        );

        verify(productoService, times(1)).findById(-1000L);
    }

    @Test
    void testCreateProducto() throws Exception {
        ProductoRequestDto nueva = new ProductoRequestDto();
        nueva.setNombre("Nuevo Producto");
        nueva.setPrecio(150.0);
        nueva.setStock(5);
        nueva.setCategoria("Categoria 1");

        ProductoResponseDto creada = createProductoResponse(10L, "Nuevo Producto", 150.0, 5, "Categoria 1");
        when(productoService.save(any(ProductoRequestDto.class)))
                .thenReturn(creada);

        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(nueva)))
                .andReturn().getResponse();

        ProductoResponseDto res = mapper.readValue(response.getContentAsString(), ProductoResponseDto.class);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
                () -> assertEquals(10L, res.getId()),
                () -> assertEquals("Nuevo Producto", res.getNombre())
        );

        verify(productoService, times(1)).save(any(ProductoRequestDto.class));
    }

    @Test
    void testUpdateProducto() throws Exception {
        ProductoRequestDto update = new ProductoRequestDto();
        update.setNombre("Producto Actualizado");
        update.setPrecio(200.0);
        update.setStock(20);
        update.setCategoria("Categoria 1");

        ProductoResponseDto actualizada = createProductoResponse(1L, "Producto Actualizado", 200.0, 20, "Categoria 1");
        when(productoService.update(eq(1L), any(ProductoRequestDto.class)))
                .thenReturn(actualizada);

        MockHttpServletResponse response = mockMvc.perform(
                        put(myEndpoint + "/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(update)))
                .andReturn().getResponse();

        ProductoResponseDto res = mapper.readValue(response.getContentAsString(), ProductoResponseDto.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals("Producto Actualizado", res.getNombre())
        );

        verify(productoService, times(1)).update(eq(1L), any(ProductoRequestDto.class));
    }

    @Test
    void testDeleteProducto() throws Exception {
        doNothing().when(productoService).deleteById(1L);

        MockHttpServletResponse response = mockMvc.perform(
                        delete(myEndpoint + "/1"))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus())
        );

        verify(productoService, times(1)).deleteById(1L);
    }
}
