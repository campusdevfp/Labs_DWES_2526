package app.ejemplo02.dto;

import java.util.Date;

public record ItemCarrito(String producto, int cantidad, Date agregadoEn) {
    public static ItemCarrito of(String producto, int cantidad) {
        return new ItemCarrito(producto, cantidad, new Date());
    }
}

