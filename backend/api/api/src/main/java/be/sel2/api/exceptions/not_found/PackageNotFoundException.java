package be.sel2.api.exceptions.not_found;

public class PackageNotFoundException extends NotFoundException {

    public PackageNotFoundException(Long id) {
        super("package", id);
    }

}
