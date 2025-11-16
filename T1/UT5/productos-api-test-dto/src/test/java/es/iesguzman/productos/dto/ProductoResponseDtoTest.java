package es.iesguzman.productos.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductoResponseDtoTest {

    @Test
    void testProductoResponseDtoCreationAndGetters() {
        ProductoResponseDto dto = new ProductoResponseDto(1L, "Producto A", 10.0, 5, "Categoría A");

        assertAll(
            () -> assertEquals(1L, dto.getId()),
            () -> assertEquals("Producto A", dto.getNombre()),
            () -> assertEquals(10.0, dto.getPrecio()),
            () -> assertEquals(5, dto.getStock()),
            () -> assertEquals("Categoría A", dto.getCategoria())
        );
    }

    @Test
    void testSetters() {
        ProductoResponseDto dto = new ProductoResponseDto();
        dto.setId(2L);
        dto.setNombre("Nuevo Nombre");
        dto.setPrecio(20.0);
        dto.setStock(10);
        dto.setCategoria("Nueva Categoría");

        assertAll(
            () -> assertEquals(2L, dto.getId()),
            () -> assertEquals("Nuevo Nombre", dto.getNombre()),
            () -> assertEquals(20.0, dto.getPrecio()),
            () -> assertEquals(10, dto.getStock()),
            () -> assertEquals("Nueva Categoría", dto.getCategoria())
        );
    }
}
