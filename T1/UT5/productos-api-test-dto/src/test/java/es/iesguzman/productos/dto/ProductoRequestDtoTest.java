package es.iesguzman.productos.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductoRequestDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
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

        assertAll(
            () -> assertTrue(violations.isEmpty())
        );
    }

    @Test
    void testValidationInvalid() {
        ProductoRequestDto dto = new ProductoRequestDto("", -5.0, -1, "");

        var violations = validator.validate(dto);

        assertAll(
            () -> assertFalse(violations.isEmpty()),
            () -> assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("vacío"))),
            () -> assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("mayor que 0"))),
            () -> assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("negativo")))
        );
    }
}
