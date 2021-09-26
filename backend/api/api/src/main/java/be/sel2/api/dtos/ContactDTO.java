package be.sel2.api.dtos;

import be.sel2.api.entities.Contact;
import be.sel2.api.exceptions.InvalidInputException;
import be.sel2.api.util.InputValidator;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter //This adds all the getters & setters for us
public class ContactDTO extends DTOObject<Contact> {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    @Override
    public List<String> getInvalidNonNullableFields() {
        List<String> res = new ArrayList<>();

        if (email == null) {
            res.add("email");
        }
        if (phoneNumber == null) {
            res.add("phoneNumber");
        }
        if (firstName == null) {
            res.add("firstName");
        }
        if (lastName == null) {
            res.add("lastName");
        }

        return res;
    }

    @Override
    public void testValidity(boolean requireNotNullFields) throws InvalidInputException {
        InvalidInputException exceptionList = super.buildException(requireNotNullFields);

        validateField(firstName, "firstName",
                InputValidator::validateName, InputValidator.NAME_RULES,
                exceptionList);

        validateField(lastName, "lastName",
                InputValidator::validateName, InputValidator.NAME_RULES,
                exceptionList);

        validateField(phoneNumber, "phoneNumber",
                InputValidator::validatePhoneNumber, InputValidator.PHONE_NUMBER_RULES,
                exceptionList);

        validateField(email, "email",
                InputValidator::validateEmail, InputValidator.EMAIL_RULES,
                exceptionList);

        if (exceptionList.containsMessages()) {
            throw exceptionList;
        }
    }

    @Override
    public Contact getEntity() {
        Contact res = new Contact();

        res.setFirstName(firstName);
        res.setLastName(lastName);
        res.setEmail(email);
        res.setPhoneNumber(phoneNumber);

        return res;
    }

    @Override
    public void updateEntity(Contact other) {
        if (firstName != null) other.setFirstName(firstName);
        if (lastName != null) other.setLastName(lastName);
        if (email != null) other.setEmail(email);
        if (phoneNumber != null) other.setPhoneNumber(phoneNumber);
    }

    public void setEmail(String email) {
        if (email == null) {
            this.email = null;
        } else {
            this.email = email.toLowerCase();
        }
    }
}
