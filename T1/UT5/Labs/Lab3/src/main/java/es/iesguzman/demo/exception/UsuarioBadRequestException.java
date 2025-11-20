package es.iesguzman.demo.exception;

public class UsuarioBadRequestException extends RuntimeException {

    public UsuarioBadRequestException(String message) {
        super(message);
    }
}
