package be.sel2.api.dtos;

import be.sel2.api.entities.FileMeta;
import be.sel2.api.entities.Organisation;
import be.sel2.api.entities.Permission;
import be.sel2.api.exceptions.InvalidInputException;
import be.sel2.api.util.InputValidator;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter //This adds all the getters & setters for us
public class PermissionDTO extends DTOObject<Permission> {
    // Velden
    private String name;
    private String description;
    private String code;
    private String link;
    private Long proof; // ID
    private Long organisationId;

    @Override
    public List<String> getInvalidNonNullableFields() {
        List<String> res = new ArrayList<>();

        if (name == null) res.add("name");
        if (description == null) res.add("description");
        if (code == null) res.add("code");
        if (organisationId == null) res.add("organisationId");

        return res;
    }

    @Override
    public void testValidity(boolean requireNotNullFields) throws InvalidInputException {
        InvalidInputException exceptionList = super.buildException(requireNotNullFields);

        if (requireNotNullFields && proof == null && link == null) {
            exceptionList.addMessage("proof|link", "at least one must not be null");
        }

        validateField(name, "name",
                InputValidator::validateName, InputValidator.NAME_RULES,
                exceptionList);

        if (exceptionList.containsMessages()) {
            throw exceptionList;
        }
    }

    @Override
    protected void validateMaxInputLength(InvalidInputException ex) {

        validateField(description, "description",
                InputValidator::validateMaxLenLong, InputValidator.MAX_LEN_LONG_RULES,
                ex);

        String[] values = {link, code};
        String[] names = {"link", "code"};
        for (int i = 0; i < 2; i++) {
            validateField(values[i], names[i],
                    InputValidator::validateMaxLenShort, InputValidator.MAX_LEN_SHORT_RULES,
                    ex);
        }
    }

    @Override
    public Permission getEntity() {
        Permission res = new Permission();

        res.setName(name);
        res.setDescription(description);
        res.setCode(code);
        res.setLink(link);

        FileMeta proofFile = new FileMeta();
        proofFile.setId(proof);
        res.setProof(proofFile);

        Organisation organisation = new Organisation();
        organisation.setId(organisationId);
        res.setOrganisation(organisation);

        return res;
    }

    @Override
    public void updateEntity(Permission other) {
        if (name != null) other.setName(name);
        if (description != null) other.setDescription(description);
        if (code != null) other.setCode(code);
        if (link != null) other.setLink(link);
        if (proof != null) {
            FileMeta proofFile = new FileMeta();
            proofFile.setId(proof);
            other.setProof(proofFile);
        }
        // Do not alter organisation
    }
}
