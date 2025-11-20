package es.iesguzman.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.iesguzman.demo.dto.CategoriaRequestDto;
import es.iesguzman.demo.dto.CategoriaResponseDto;
import es.iesguzman.demo.exception.CategoriaNotFoundException;
import es.iesguzman.demo.service.CategoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * Test de integración del controlador CategoriaController usando MockMvc.
 *
 * @WebMvcTest: Carga solo el controlador específico sin levantar todo el contexto de Spring.
 *              No conecta a la base de datos, ideal para tests rápidos de controladores.
 *
 * MockMvc: Cliente HTTP simulado que permite hacer peticiones al controlador sin servidor real.
 * @MockBean: Reemplaza el servicio real por un mock de Mockito para controlar su comportamiento.
 */
@WebMvcTest(CategoriaController.class)
public class CategoriaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc; // Cliente HTTP simulado para hacer peticiones

    @MockBean
    private CategoriaService categoriaService; // Mock del servicio (no usa la base de datos real)

    private final ObjectMapper mapper = new ObjectMapper(); // Para convertir objetos a JSON
    private CategoriaResponseDto categoriaDto; // Objeto reutilizable en los tests
    private final String endpoint = "/api/categorias"; // Endpoint base del controlador

    /**
     * Método que se ejecuta ANTES de cada test.
     * Prepara los datos comunes para todos los tests.
     */
    @BeforeEach
    void setUp() {
        // Crear una categoría de ejemplo que usaremos en varios tests
        categoriaDto = new CategoriaResponseDto();
        categoriaDto.setId(1L);
        categoriaDto.setNombre("Electronica");
        categoriaDto.setDescripcion("Productos electronicos");
    }

    /**
     * Test: GET /api/categorias debe devolver lista de categorías (200).
     *
     * Pasos del test:
     * 1. Preparar datos de prueba (2 categorías)
     * 2. Configurar el mock para que devuelva estas categorías
     * 3. Hacer petición GET al endpoint
     * 4. Verificar que la respuesta es 200 OK y contiene las categorías
     * 5. Verificar que se llamó al servicio una vez
     */
    @Test
    public void testGetAllCategoriasRetorna200() throws Exception {
        // 1. PREPARAR: Crear segunda categoría para la lista
        CategoriaResponseDto categoria2 = new CategoriaResponseDto();
        categoria2.setId(2L);
        categoria2.setNombre("Libros");
        categoria2.setDescripcion("Libros y literatura");

        List<CategoriaResponseDto> categorias = Arrays.asList(categoriaDto, categoria2);

        // 2. CONFIGURAR MOCK: Cuando se llame a getAllCategorias(), devolver la lista preparada
        when(categoriaService.getAllCategorias()).thenReturn(categorias);

        // 3. EJECUTAR: Hacer petición GET al endpoint /api/categorias
        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint)  // GET /api/categorias
                                .accept(MediaType.APPLICATION_JSON)) // Esperar respuesta JSON
                .andReturn().getResponse(); // Obtener la respuesta

        // 4. VERIFICAR: Comprobar que la respuesta es correcta
        assertAll(
                // Verificar que el código de estado es 200 OK
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                // Verificar que el JSON contiene "Electronica"
                () -> assertTrue(response.getContentAsString().contains("Electronica")),
                // Verificar que el JSON contiene "Libros"
                () -> assertTrue(response.getContentAsString().contains("Libros"))
        );

        // 5. VERIFICAR MOCK: Comprobar que se llamó al servicio exactamente 1 vez
        verify(categoriaService, times(1)).getAllCategorias();
    }

    /**
     * Test: GET /api/categorias/{id} debe devolver una categoría cuando existe (200).
     *
     * Verifica que se puede obtener una categoría específica por su ID.
     */
    @Test
    public void testGetCategoriaByIdRetorna200() throws Exception {
        // 1. CONFIGURAR MOCK: Cuando se busque categoría con ID 1, devolver categoriaDto
        when(categoriaService.getCategoriaById(1L)).thenReturn(categoriaDto);

        // 2. EJECUTAR: Hacer petición GET al endpoint /api/categorias/1
        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint + "/1")  // GET /api/categorias/1
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // 3. VERIFICAR: Comprobar respuesta correcta
        assertAll(
                // Verificar código 200 OK
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                // Verificar que el JSON contiene el nombre de la categoría
                () -> assertTrue(response.getContentAsString().contains("Electronica"))
        );

        // 4. VERIFICAR MOCK: Comprobar que se llamó al servicio con el ID correcto
        verify(categoriaService, times(1)).getCategoriaById(1L);
    }

    /**
     * Test: GET /api/categorias/{id} debe devolver 404 cuando no existe.
     *
     * Verifica que cuando se busca una categoría que no existe,
     * el controlador devuelve 404 Not Found.
     */
    @Test
    public void testGetCategoriaByIdRetorna404() throws Exception {
        // 1. CONFIGURAR MOCK: Cuando se busque ID -1, lanzar excepción "no encontrado"
        when(categoriaService.getCategoriaById(-1L))
                .thenThrow(new CategoriaNotFoundException("Categoría no encontrada"));

        // 2. EJECUTAR: Hacer petición GET con ID inexistente
        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint + "/-1")  // GET /api/categorias/-1
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // 3. VERIFICAR: Comprobar que devuelve 404 Not Found
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

        // 4. VERIFICAR MOCK: Comprobar que se intentó buscar la categoría
        verify(categoriaService, times(1)).getCategoriaById(-1L);
    }

    /**
     * Test: POST /api/categorias debe crear una categoría con datos válidos (201).
     *
     * Verifica que se puede crear una nueva categoría enviando un JSON válido.
     */
    @Test
    public void testCreateCategoriaConDatosValidosRetorna201() throws Exception {
        // 1. PREPARAR: Crear DTO de request (lo que envía el cliente)
        CategoriaRequestDto requestDto = new CategoriaRequestDto();
        requestDto.setNombre("Deportes");
        requestDto.setDescripcion("Articulos deportivos");

        // Crear DTO de response (lo que devuelve el servicio después de crear)
        CategoriaResponseDto createdDto = new CategoriaResponseDto();
        createdDto.setId(10L); // ID generado por la BD
        createdDto.setNombre("Deportes");
        createdDto.setDescripcion("Articulos deportivos");

        // 2. CONFIGURAR MOCK: Cuando se llame a createCategoria, devolver la categoría creada
        when(categoriaService.createCategoria(any(CategoriaRequestDto.class)))
                .thenReturn(createdDto);

        // 3. EJECUTAR: Convertir requestDto a JSON y hacer POST
        String json = mapper.writeValueAsString(requestDto);
        MockHttpServletResponse response = mockMvc.perform(
                        post(endpoint)  // POST /api/categorias
                                .contentType(MediaType.APPLICATION_JSON) // Tipo de contenido: JSON
                                .content(json) // Cuerpo de la petición
                                .accept(MediaType.APPLICATION_JSON)) // Esperamos JSON de respuesta
                .andReturn().getResponse();

        // 4. VERIFICAR: Comprobar que se creó correctamente
        assertAll(
                // Verificar código 201 Created
                () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
                // Verificar que la respuesta contiene el nombre de la categoría creada
                () -> assertTrue(response.getContentAsString().contains("Deportes"))
        );

        // 5. VERIFICAR MOCK: Comprobar que se llamó al servicio para crear
        verify(categoriaService, times(1))
                .createCategoria(any(CategoriaRequestDto.class));
    }
}
