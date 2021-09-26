package be.sel2.api.exceptions;

public class EmptyFileException extends RuntimeException {

    public EmptyFileException() {
        super("The file was empty");
    }
}
