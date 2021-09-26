package be.sel2.api.exceptions.not_found;

public class FileNotFoundException extends NotFoundException {
    public FileNotFoundException(Long id) {
        super("file", id);
    }
}
