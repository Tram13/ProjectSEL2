package be.sel2.api.exceptions.conflict;

public class ContactExistsException extends ConflictException {

    public ContactExistsException() {
        super("A contact already exists with this email inside this organisation");
    }
}
