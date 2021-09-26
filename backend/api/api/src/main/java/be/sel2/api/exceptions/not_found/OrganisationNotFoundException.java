package be.sel2.api.exceptions.not_found;

public class OrganisationNotFoundException extends NotFoundException {

    public OrganisationNotFoundException(Long id) {
        super("organisation", id);
    }

}
