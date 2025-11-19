package es.iesguzman.demo.exception;

public class ProductoBadRequestException extends RuntimeException {
    public ProductoBadRequestException(String message) {
        super(message);
    }
}
