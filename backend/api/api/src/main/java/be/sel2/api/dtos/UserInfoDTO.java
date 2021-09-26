package be.sel2.api.dtos;

import be.sel2.api.entities.UserInfo;
import be.sel2.api.exceptions.InvalidInputException;
import be.sel2.api.util.InputValidator;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter //This adds all the getters & setters for us
public class UserInfoDTO extends DTOObject<UserInfo> {

    private static List<String> roleOptions = Arrays.stream(UserInfo.Userrole.values()).map(userroleInstance -> userroleInstance.name().toLowerCase()).collect(Collectors.toList());
    // Velden
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;

    @Override
    public List<String> getInvalidNonNullableFields() {
        List<String> res = new ArrayList<>();

        if (firstName == null) {
            res.add("firstName");
        }
        if (lastName == null) {
            res.add("lastName");
        }
        if (email == null) {
            res.add("email");
        }
        if (password == null) {
            res.add("password");
        }
        if (role == null) {
            res.add("role");
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

        validateField(password, "password",
                InputValidator::validatePassword, InputValidator.PASSWORD_RULES,
                exceptionList);

        validateField(email, "email",
                InputValidator::validateEmail, InputValidator.EMAIL_RULES,
                exceptionList);

        validateField(role, "role",
                roleOptions::contains,
                InputValidator.listOfOptionsRule(roleOptions),
                exceptionList);

        if (exceptionList.containsMessages()) {
            throw exceptionList;
        }
    }

    @Override
    public UserInfo getEntity() {
        UserInfo res = new UserInfo();

        res.setFirstName(firstName);
        res.setLastName(lastName);
        res.setEmail(email);
        res.setPassword(password);
        res.setRole(getEnumRole());

        return res;
    }

    @Override
    public void updateEntity(UserInfo other) {
        if (firstName != null) other.setFirstName(firstName);
        if (lastName != null) other.setLastName(lastName);
        if (email != null) other.setEmail(email);
        if (password != null) other.setPassword(password);
        if (role != null) other.setRole(getEnumRole());
    }

    public void setEmail(String email) {
        if (email == null) {
            this.email = null;
        } else {
            this.email = email.toLowerCase();
        }
    }

    public void setRole(String role) {
        if (role == null) {
            this.role = null;
        } else {
            this.role = role.toLowerCase();
        }
    }

    private UserInfo.Userrole getEnumRole() {
        return UserInfo.Userrole.fromString(this.role);
    }
}
