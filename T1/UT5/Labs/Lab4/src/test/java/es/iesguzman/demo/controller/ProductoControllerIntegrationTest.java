// language: java
package es.iesguzman.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.iesguzman.demo.dto.ProductoRequestDto;
import es.iesguzman.demo.dto.ProductoResponseDto;
import es.iesguzman.demo.dto.ErrorResponse;
import es.iesguzman.demo.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testGetAllProductosRetorna200() throws Exception {
        ProductoResponseDto producto1 = new ProductoResponseDto();
        producto1.setId(1L);
        producto1.setNombre("Laptop");
        producto1.setPrecio(999.99);
        producto1.setStock(10);

        ProductoResponseDto producto2 = new ProductoResponseDto();
        producto2.setId(2L);
        producto2.setNombre("Mouse");
        producto2.setPrecio(25.50);
        producto2.setStock(50);

        List<ProductoResponseDto> productos = Arrays.asList(producto1, producto2);
        when(productoService.getAllProductos()).thenReturn(productos);

        MockHttpServletResponse response = mockMvc.perform(get("/api/productos"))
                .andReturn().getResponse();

        List<ProductoResponseDto> res = mapper.readValue(
                response.getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, ProductoResponseDto.class)
        );

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType()),
                () -> assertEquals(2, res.size()),
                () -> assertTrue(res.stream().anyMatch(p -> "Laptop".equals(p.getNombre())))
        );

        verify(productoService, times(1)).getAllProductos();
    }

    @Test
    public void testGetProductoByIdRetorna200() throws Exception {
        ProductoResponseDto producto = new ProductoResponseDto();
        producto.setId(1L);
        producto.setNombre("Laptop");
        producto.setPrecio(999.99);
        producto.setStock(10);

        when(productoService.getProductoById(1L)).thenReturn(producto);

        MockHttpServletResponse response = mockMvc.perform(get("/api/productos/1"))
                .andReturn().getResponse();

        ProductoResponseDto res = mapper.readValue(response.getContentAsString(), ProductoResponseDto.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType()),
                () -> assertEquals(1L, res.getId()),
                () -> assertEquals("Laptop", res.getNombre())
        );

        verify(productoService, times(1)).getProductoById(1L);
    }

    @Test
    public void testGetProductoByIdRetorna404() throws Exception {
        when(productoService.getProductoById(999L))
                .thenThrow(new es.iesguzman.demo.exception.ProductoNotFoundException("Producto no encontrado"));

        MockHttpServletResponse response = mockMvc.perform(get("/api/productos/999"))
                .andReturn().getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

        verify(productoService, times(1)).getProductoById(999L);
    }

    @Test
    public void testCreateProductoConDatosValidosRetorna201() throws Exception {
        ProductoResponseDto responseDto = new ProductoResponseDto();
        responseDto.setId(1L);
        responseDto.setNombre("Laptop");
        responseDto.setPrecio(999.99);
        responseDto.setStock(10);

        when(productoService.createProducto(any(ProductoRequestDto.class))).thenReturn(responseDto);

        String requestBody = "{"
                + "\"nombre\": \"Laptop\","
                + "\"descripcion\": \"Laptop gaming\","
                + "\"precio\": 999.99,"
                + "\"stock\": 10,"
                + "\"categoriaId\": 1,"
                + "\"imagenUrl\": \"http://example.com/laptop.jpg\""
                + "}";

        MockHttpServletResponse response = mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn().getResponse();

        ProductoResponseDto res = mapper.readValue(response.getContentAsString(), ProductoResponseDto.class);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
                () -> assertEquals(1L, res.getId()),
                () -> assertEquals("Laptop", res.getNombre())
        );

        verify(productoService, times(1)).createProducto(any(ProductoRequestDto.class));
    }

    @Test
    public void testCreateProductoConDatosInvalidosRetorna400() throws Exception {
        String requestBody = "{"
                + "\"descripcion\": \"Solo descripción\""
                + "}";

        MockHttpServletResponse response = mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn().getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        verify(productoService, times(0)).createProducto(any(ProductoRequestDto.class));
    }

//    @Test
//    public void testUpdateProductoExistenteRetorna200() throws Exception {
//        ProductoResponseDto responseDto = new ProductoResponseDto();
//        responseDto.setId(1L);
//        responseDto.setNombre("Laptop Actualizada");
//        responseDto.setPrecio(899.99);
//        responseDto.setStock(5);
//
//        when(productoService.updateProducto(eq(1L), any(ProductoRequestDto.class))).thenReturn(responseDto);
//
//        String requestBody = "{"
//                + "\"nombre\": \"Laptop Actualizada\","
//                + "\"descripcion\": \"Laptop gaming mejorada\","
//                + "\"precio\": 899.99,"
//                + "\"stock\": 5,"
//                + "\"categoriaId\": 1,"
//                + "\"imagenUrl\": \"http://example.com/laptop-new.jpg\""
//                + "}";
//
//        MockHttpServletResponse response = mockMvc.perform(put("/api/productos/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andReturn().getResponse();
//
//        ProductoResponseDto res = mapper.readValue(response.getContentAsString(), ProductoResponseDto.class);
//
//        assertAll(
//                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
//                () -> assertEquals("Laptop Actualizada", res.getNombre()),
//                () -> assertEquals(899.99, res.getPrecio())
//        );
//
//        verify(productoService, times(1)).updateProducto(eq(1L), any(ProductoRequestDto.class));
//    }
//
//    @Test
//    public void testUpdateProductoNoExistenteRetorna404() throws Exception {
//        when(productoService.updateProducto(eq(999L), any(ProductoRequestDto.class)))
//                .thenThrow(new es.iesguzman.demo.exception.ProductoNotFoundException("Producto no encontrado"));
//
//        String requestBody = "{"
//                + "\"nombre\": \"Laptop\","
//                + "\"precio\": 999.99,"
//                + "\"stock\": 10,"
//                + "\"categoriaId\": 1"
//                + "}";
//
//        MockHttpServletResponse response = mockMvc.perform(put("/api/productos/999")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andReturn().getResponse();
//
//        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
//
//        verify(productoService, times(1)).updateProducto(eq(999L), any(ProductoRequestDto.class));
//    }
//
//    @Test
//    public void testDeleteProductoRetorna204() throws Exception {
//        MockHttpServletResponse response = mockMvc.perform(delete("/api/productos/1"))
//                .andReturn().getResponse();
//
//        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
//
//        verify(productoService, times(1)).deleteProducto(1L);
//    }
//
//    @Test
//    public void testDeleteProductoNoExistenteRetorna404() throws Exception {
//        doThrow(new es.iesguzman.demo.exception.ProductoNotFoundException("Producto no encontrado"))
//                .when(productoService).deleteProducto(999L);
//
//        MockHttpServletResponse response = mockMvc.perform(delete("/api/productos/999"))
//                .andReturn().getResponse();
//
//        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
//
//        verify(productoService, times(1)).deleteProducto(999L);
//    }

    @Test
    public void testValidacionNombreObligatorio() throws Exception {
        String requestBody = "{"
                + "\"precio\": 999.99,"
                + "\"stock\": 10,"
                + "\"categoriaId\": 1"
                + "}";

        MockHttpServletResponse response = mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn().getResponse();

        try {
            ErrorResponse err = mapper.readValue(response.getContentAsString(), ErrorResponse.class);
            assertEquals(400, err.getStatus());
            assertEquals("Datos inválidos", err.getMessage());
        } catch (Exception ignored) {
        }

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        verify(productoService, times(0)).createProducto(any(ProductoRequestDto.class));
    }

    @Test
    public void testValidacionPrecioPositivo() throws Exception {
        String requestBody = "{"
                + "\"nombre\": \"Laptop\","
                + "\"precio\": -50.0,"
                + "\"stock\": 10,"
                + "\"categoriaId\": 1"
                + "}";

        MockHttpServletResponse response = mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn().getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        verify(productoService, times(0)).createProducto(any(ProductoRequestDto.class));
    }
}
