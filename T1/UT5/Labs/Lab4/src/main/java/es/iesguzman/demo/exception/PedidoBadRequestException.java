package es.iesguzman.demo.exception;

public class PedidoBadRequestException extends RuntimeException {
    public PedidoBadRequestException(String message) {
        super(message);
    }
}
