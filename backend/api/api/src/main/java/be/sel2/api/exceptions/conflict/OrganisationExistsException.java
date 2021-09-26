package be.sel2.api.exceptions.conflict;

public class OrganisationExistsException extends ConflictException {

    public OrganisationExistsException() {
        super("The organisation already exists");
    }
}
