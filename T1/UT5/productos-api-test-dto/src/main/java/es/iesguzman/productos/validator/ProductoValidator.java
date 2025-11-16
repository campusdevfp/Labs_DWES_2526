package es.iesguzman.productos.validator;

import org.springframework.stereotype.Component;
import es.iesguzman.productos.domain.Producto;
import es.iesguzman.productos.exception.ProductoInvalidoException;

@Component
public class ProductoValidator {
    public void validate(Producto p) {
        if (p.getPrecio() < 1 && p.getStock() > 1000) {
            throw new ProductoInvalidoException("Precio demasiado bajo para un stock tan alto");
        }
        if ("digital".equalsIgnoreCase(p.getCategoria()) && p.getStock() > 0) {
            throw new ProductoInvalidoException("Los productos digitales no deben tener stock físico");
        }
    }
}
