package be.sel2.api.exceptions.not_found;

public class ContactNotFoundException extends NotFoundException {

    public ContactNotFoundException(Long id) {
        super("contact", id);
    }

}
