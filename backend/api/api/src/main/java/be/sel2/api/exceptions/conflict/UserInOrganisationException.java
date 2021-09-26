package be.sel2.api.exceptions.conflict;

public class UserInOrganisationException extends ConflictException {

    public UserInOrganisationException() {
        super("User is already an employee of this organisation");
    }
}
