// language: java
package es.iesguzman.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.iesguzman.demo.dto.CategoriaResponseDto;
import es.iesguzman.demo.dto.ProductoRequestDto;
import es.iesguzman.demo.dto.ProductoResponseDto;
import es.iesguzman.demo.exception.ProductoNotFoundException;
import es.iesguzman.demo.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * Test de integración del controlador ProductoController usando MockMvc.
 *
 * Características:
 * - Solo carga el controlador ProductoController (no toda la aplicación)
 * - No conecta a base de datos MySQL
 * - Usa mocks para simular el comportamiento del servicio
 */
@WebMvcTest(ProductoController.class)
public class ProductoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc; // Cliente HTTP simulado

    @MockBean
    private ProductoService productoService; // Mock del servicio

    private final ObjectMapper mapper = new ObjectMapper();
    private ProductoResponseDto productoDto;
    private final String endpoint = "/api/productos";

    /**
     * Configuración inicial antes de cada test.
     * Prepara un producto de ejemplo reutilizable.
     */
    @BeforeEach
    void setUp() {
        // Crear categoría para el producto
        CategoriaResponseDto categoria = new CategoriaResponseDto();
        categoria.setId(1L);
        categoria.setNombre("Electronica");

        // Crear producto de ejemplo
        productoDto = new ProductoResponseDto();
        productoDto.setId(1L);
        productoDto.setNombre("Laptop");
        productoDto.setDescripcion("Laptop de alta gama");
        productoDto.setPrecio(999.99);
        productoDto.setStock(10);
        productoDto.setCategoria(categoria);
        productoDto.setActivo(true);
    }

    // Helper para convertir respuesta a UTF-8 (evita problemas con tildes/ñ)
    private String contentAsUtf8(MockHttpServletResponse response) {
        return new String(response.getContentAsByteArray(), java.nio.charset.StandardCharsets.UTF_8);
    }

    /**
     * Test: GET /api/productos debe devolver lista de productos (200).
     *
     * Verifica que el endpoint retorna todos los productos correctamente.
     */
    @Test
    public void testGetAllProductosRetorna200() throws Exception {
        // Preparar segundo producto
        ProductoResponseDto producto2 = new ProductoResponseDto();
        producto2.setId(2L);
        producto2.setNombre("Mouse");
        producto2.setPrecio(25.50);

        List<ProductoResponseDto> productos = List.of(productoDto, producto2);

        // Configurar mock: devolver lista de productos
        when(productoService.getAllProductos()).thenReturn(productos);

        // Ejecutar GET /api/productos
        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verificar respuesta
        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("Laptop")),
                () -> assertTrue(response.getContentAsString().contains("Mouse"))
        );

        verify(productoService, times(1)).getAllProductos();
    }

    /**
     * Test: GET /api/productos/{id} debe devolver un producto cuando existe (200).
     *
     * Verifica la obtención de un producto específico por su ID.
     */
    @Test
    public void testGetProductoByIdRetorna200() throws Exception {
        // Configurar mock: devolver producto cuando se busque ID 1
        when(productoService.getProductoById(1L)).thenReturn(productoDto);

        // Ejecutar GET /api/productos/1
        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint + "/1")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verificar respuesta correcta
        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("Laptop"))
        );

        verify(productoService, times(1)).getProductoById(1L);
    }

    /**
     * Test: GET /api/productos/{id} debe devolver 404 cuando no existe.
     *
     * Verifica el manejo de error cuando se busca un producto inexistente.
     */
    @Test
    public void testGetProductoByIdRetorna404() throws Exception {
        // Configurar mock: lanzar excepción cuando se busque ID -1
        when(productoService.getProductoById(-1L))
                .thenThrow(new ProductoNotFoundException("Producto no encontrado"));

        // Ejecutar GET con ID inexistente
        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint + "/-1")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verificar que devuelve 404
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

        verify(productoService, times(1)).getProductoById(-1L);
    }

    /**
     * Test: POST /api/productos debe crear un producto con datos válidos (201).
     *
     * Verifica la creación de un nuevo producto.
     */
    @Test
    public void testCreateProductoConDatosValidosRetorna201() throws Exception {
        // Preparar request DTO (datos del cliente)
        ProductoRequestDto requestDto = new ProductoRequestDto();
        requestDto.setNombre("Teclado");
        requestDto.setDescripcion("Teclado mecanico");
        requestDto.setPrecio(89.99);
        requestDto.setStock(20);
        requestDto.setCategoriaId(1L);

        // Preparar response DTO (producto creado)
        ProductoResponseDto createdDto = new ProductoResponseDto();
        createdDto.setId(10L);
        createdDto.setNombre("Teclado");
        createdDto.setPrecio(89.99);
        createdDto.setActivo(true);

        // Configurar mock: devolver producto creado
        when(productoService.createProducto(any(ProductoRequestDto.class)))
                .thenReturn(createdDto);

        // Convertir a JSON y ejecutar POST
        String json = mapper.writeValueAsString(requestDto);
        MockHttpServletResponse response = mockMvc.perform(
                        post(endpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verificar creación exitosa
        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("Teclado"))
        );

        verify(productoService, times(1)).createProducto(any(ProductoRequestDto.class));
    }
}
