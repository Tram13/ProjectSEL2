package be.sel2.api.exceptions;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException() {
        super("Unauthorized action");
    }

    public ForbiddenException(String message) {
        super(message);
    }
}
