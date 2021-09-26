package be.sel2.api.dtos;

import be.sel2.api.entities.Service;
import be.sel2.api.exceptions.InvalidInputException;
import be.sel2.api.util.InputValidator;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

// Stelt een MAGDA-dienst voor
@Getter
@Setter //This adds all the getters & setters for us
public class ServiceDTO extends DTOObject<Service> {

    private static List<String> deliveryMethodOptions = Arrays.stream(Service.DeliveryMethod.values())
            .map(Enum::name)
            .collect(Collectors.toList());
    // Velden
    private String name;
    private Boolean deprecated;
    private String domain;
    private String description;
    private Boolean needsPermissions;

    private List<String> sources;
    private List<String> deliveryMethods;


    @Override
    public List<String> getInvalidNonNullableFields() {
        List<String> res = new ArrayList<>();

        if (name == null) res.add("name");
        if (domain == null) res.add("domain");
        if (description == null) res.add("description");
        if (sources == null) res.add("sources");
        if (deliveryMethods == null) res.add("deliveryMethods");

        return res;
    }

    @Override
    public void testValidity(boolean requireNotNullFields) throws InvalidInputException {
        InvalidInputException exceptionList = super.buildException(requireNotNullFields);

        validateField(name, "name",
                InputValidator::validateName, InputValidator.NAME_RULES,
                exceptionList);

        if (deliveryMethods != null && !deliveryMethodOptions.containsAll(deliveryMethods)) {
            exceptionList.addMessage("deliveryMethods",
                    InputValidator.listOfOptionsRule(deliveryMethodOptions));
        }


        if (exceptionList.containsMessages()) {
            throw exceptionList;
        }
    }

    @Override
    protected void validateMaxInputLength(InvalidInputException ex) {

        validateField(description, "description",
                InputValidator::validateMaxLenLong, InputValidator.MAX_LEN_LONG_RULES,
                ex);

        validateField(domain, "domain",
                InputValidator::validateMaxLenShort, InputValidator.MAX_LEN_SHORT_RULES,
                ex);

        if (sources != null) {
            for (int i = 0; i < sources.size(); i++) {
                String s = sources.get(i);
                validateField(s, String.format("sources[%d]", i),
                        InputValidator::validateMaxLenShort, InputValidator.MAX_LEN_SHORT_RULES,
                        ex);
            }
        }
    }

    @Override
    public Service getEntity() {
        Service res = new Service();

        res.setName(name);
        res.setDeprecated(Objects.requireNonNullElse(deprecated, false));
        res.setDomain(domain);
        res.setDescription(description);

        res.setNeedsPermissions(
                Objects.requireNonNullElse(needsPermissions, false)
        );

        res.setSources(new HashSet<>(sources));


        res.setDeliveryMethods(
                deliveryMethods.stream().map(Service.DeliveryMethod::fromString)
                        .collect(Collectors.toSet())
        );

        return res;
    }

    @Override
    public void updateEntity(Service other) {
        if (name != null) other.setName(name);
        if (deprecated != null) other.setDeprecated(deprecated);
        if (domain != null) other.setDomain(domain);
        if (description != null) other.setDescription(description);
        if (needsPermissions != null) other.setNeedsPermissions(needsPermissions);

        if (sources != null) other.setSources(new HashSet<>(sources));
        if (deliveryMethods != null) other.setDeliveryMethods(
                deliveryMethods.stream().map(Service.DeliveryMethod::fromString)
                        .collect(Collectors.toSet())
        );
    }
}
