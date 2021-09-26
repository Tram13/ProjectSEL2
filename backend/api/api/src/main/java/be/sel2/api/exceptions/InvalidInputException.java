package be.sel2.api.exceptions;

import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Error used to inform the user of invalid input
 */
public class InvalidInputException extends RuntimeException {

    /**
     * Pairs of each parameter and their related messages
     */
    @Getter
    public static class ParamErrorPair implements Serializable {
        private final String parameter;
        private final String message;

        ParamErrorPair(String parameter, String message) {
            this.parameter = parameter;
            this.message = message;
        }
    }

    private final List<ParamErrorPair> errors;

    public InvalidInputException() {
        super("Invalid input was given");
        this.errors = new ArrayList<>();
    }

    public void addMessage(String parameter, String message) {
        errors.add(new ParamErrorPair(parameter, message));
    }

    public boolean containsMessages() {
        return !errors.isEmpty();
    }

    public List<ParamErrorPair> getErrors() {
        return errors;
    }
}
