package be.sel2.api.exceptions.conflict;

public class UserExistsException extends ConflictException {

    public UserExistsException() {
        super("A user already exists with this email address");
    }
}
