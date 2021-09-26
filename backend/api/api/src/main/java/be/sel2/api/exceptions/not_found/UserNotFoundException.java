package be.sel2.api.exceptions.not_found;

public class UserNotFoundException extends NotFoundException { // Custom error

    public UserNotFoundException(Long id) {
        super("user", id);
    }

    public UserNotFoundException(String email) {
        super("user", "email", email);
    }

}
