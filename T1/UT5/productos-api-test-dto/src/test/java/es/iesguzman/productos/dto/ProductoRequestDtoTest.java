package es.iesguzman.productos.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductoRequestDtoTest {

    // Validator reutilizable para todas las pruebas de validación.
    // Lo inicializamos una vez en @BeforeAll porque crear un ValidatorFactory
    // y obtener un Validator es relativamente costoso y no depende del estado
    // de cada test. Esto también evita repetir código en cada test.
    private static Validator validator;

    /**
     * Inicialización del Validator.
     *
     * Por qué lo hacemos aquí:
     * - Las clases DTO usan anotaciones de validación (jakarta.validation / Bean Validation)
     *   como @NotNull, @Min, @Size, etc. Para comprobar estas restricciones en tests
     *   unitarios necesitamos un objeto Validator.
     * - @BeforeAll ejecuta este setup una vez para toda la clase de tests, reduciendo
     *   el coste y dejándolo disponible para todos los métodos de prueba.
     *
     * Nota práctica:
     * - No hacemos aquí pruebas de integración de Spring, sólo validaciones locales
     *   sobre los objetos DTO.
     * - En tests que dependan del contexto de Spring se usaría la infraestructua de
     *   Spring (por ejemplo, @SpringBootTest), pero aquí queremos pruebas unitarias
     *   aisladas, por eso se usa el Validator directo.
     */
    @BeforeAll
    static void setUp() {
        // Usar try-with-resources para asegurar que ValidatorFactory se cierra correctamente
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testProductoRequestDtoCreationAndGetters() {
        ProductoRequestDto dto = new ProductoRequestDto("Producto A", 10.0, 5, "Categoría A");

        assertAll(
            () -> assertEquals("Producto A", dto.getNombre()),
            () -> assertEquals(10.0, dto.getPrecio()),
            () -> assertEquals(5, dto.getStock()),
            () -> assertEquals("Categoría A", dto.getCategoria())
        );
    }

    @Test
    void testSetters() {
        ProductoRequestDto dto = new ProductoRequestDto();
        dto.setNombre("Nuevo Nombre");
        dto.setPrecio(20.0);
        dto.setStock(10);
        dto.setCategoria("Nueva Categoría");

        assertAll(
            () -> assertEquals("Nuevo Nombre", dto.getNombre()),
            () -> assertEquals(20.0, dto.getPrecio()),
            () -> assertEquals(10, dto.getStock()),
            () -> assertEquals("Nueva Categoría", dto.getCategoria())
        );
    }

    @Test
    void testValidationValid() {
        ProductoRequestDto dto = new ProductoRequestDto("Producto A", 10.0, 5, "Categoría A");

        var violations = validator.validate(dto);

        // Agrupamos los asserts con assertAll, siguiendo la guía (inittesting.md)
        assertAll(
            () -> assertTrue(violations.isEmpty())
        );
    }

    @Test
    void testValidationInvalid() {
        ProductoRequestDto dto = new ProductoRequestDto("", -5.0, -1, "");

        var violations = validator.validate(dto);

        // Comprobaciones sobre los mensajes de violación. Usamos contains porque
        // los mensajes exactos dependen de la configuración de los mensajes y
        // del locale; buscamos fragmentos significativos ('vacío', 'mayor que 0', 'negativo').
        assertAll(
            () -> assertFalse(violations.isEmpty()),
            () -> assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("vacío"))),
            () -> assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("mayor que 0"))),
            () -> assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("negativo")))
        );
    }
}
