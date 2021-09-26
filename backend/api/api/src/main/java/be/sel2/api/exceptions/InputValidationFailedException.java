package be.sel2.api.exceptions;

public class InputValidationFailedException extends RuntimeException {

    public InputValidationFailedException() {
        super("Input validation failed");
    }

    public InputValidationFailedException(Throwable cause) {
        super("Input validation failed", cause);
    }

    public InputValidationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public InputValidationFailedException(String message) {
        super(message);
    }
}
