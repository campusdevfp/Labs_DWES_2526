package es.iesguzman.productos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProductoInvalidoException extends RuntimeException {
    public ProductoInvalidoException(String msg) { super(msg); }
}
