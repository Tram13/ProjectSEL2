package be.sel2.api.exceptions.not_found;

public class ServiceNotFoundException extends NotFoundException {

    public ServiceNotFoundException(Long id) {
        super("service", id);
    }

}
