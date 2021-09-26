package be.sel2.api.dtos;

import be.sel2.api.entities.Organisation;
import be.sel2.api.exceptions.InvalidInputException;
import be.sel2.api.util.InputValidator;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter //This adds all the getters & setters for us
public class OrganisationDTO extends DTOObject<Organisation> {
    // Velden
    private String organisationName;
    private String kboNumber;
    private String ovoCode;
    private String nisNumber;
    private String serviceProvider;

    private Boolean approved;

    @Override
    protected List<String> getInvalidNonNullableFields() {
        List<String> res = new ArrayList<>();

        if (organisationName == null) res.add("organisationName");
        if (kboNumber == null) res.add("kboNumber");
        if (serviceProvider == null) res.add("serviceProvider");

        return res;
    }

    @Override
    public void testValidity(boolean requireNotNullFields) throws InvalidInputException {
        InvalidInputException exceptionList = super.buildException(requireNotNullFields);

        validateField(organisationName, "organisationName",
                InputValidator::validateName, InputValidator.NAME_RULES,
                exceptionList);

        validateField(kboNumber, "kboNumber",
                InputValidator::validateKBO, InputValidator.KBO_RULES,
                exceptionList);

        validateField(ovoCode, "ovoCode",
                InputValidator::validateOVO, InputValidator.OVO_RULES,
                exceptionList);

        validateField(nisNumber, "nisNumber",
                InputValidator::validateNIS, InputValidator.NIS_RULES,
                exceptionList);

        if (exceptionList.containsMessages()) {
            throw exceptionList;
        }
    }

    @Override
    public Organisation getEntity() {
        Organisation res = new Organisation();

        res.setOrganisationName(organisationName);
        res.setKboNumber(kboNumber);
        res.setOvoCode(ovoCode);
        res.setNisNumber(nisNumber);
        res.setServiceProvider(serviceProvider);
        res.setApproved(approved);

        return res;
    }

    @Override
    public void updateEntity(Organisation other) {
        if (organisationName != null) other.setOrganisationName(organisationName);
        if (kboNumber != null) other.setKboNumber(kboNumber);
        if (ovoCode != null) other.setOvoCode(ovoCode);
        if (nisNumber != null) other.setNisNumber(nisNumber);
        if (serviceProvider != null) other.setServiceProvider(serviceProvider);
        if (approved != null) other.setApproved(approved);
    }

    @Override
    public void validateMaxInputLength(InvalidInputException ex) {
        validateField(serviceProvider, "serviceProvider",
                InputValidator::validateMaxLenShort,
                InputValidator.MAX_LEN_SHORT_RULES, ex);
    }
}
