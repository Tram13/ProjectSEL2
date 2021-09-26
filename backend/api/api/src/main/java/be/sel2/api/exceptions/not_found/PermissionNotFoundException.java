package be.sel2.api.exceptions.not_found;

public class PermissionNotFoundException extends NotFoundException {

    public PermissionNotFoundException(Long id) {
        super("permission", id);
    }

}
