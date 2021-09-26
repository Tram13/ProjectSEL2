package be.sel2.api.dtos;

import be.sel2.api.entities.Package;
import be.sel2.api.entities.Service;
import be.sel2.api.entities.relations.PackageService;
import be.sel2.api.exceptions.InvalidInputException;
import be.sel2.api.util.InputValidator;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter //This adds all the getters & setters for us
public class PackageDTO extends DTOObject<Package> {

    @Getter
    @Setter //This adds all the getters & setters for us
    public static class ServiceItem {
        private String source;
        private String deliveryMethod;
        private Long serviceId;

        public void testForNull(InvalidInputException ex, String nullMessage) {
            if (source == null) ex.addMessage("services.source", nullMessage);
            if (deliveryMethod == null) ex.addMessage("services.deliveryMethod", nullMessage);
            if (serviceId == null) ex.addMessage("services.serviceId", nullMessage);
        }

        public PackageService getService(Package pack) throws NullPointerException {
            Service serv = new Service();
            serv.setId(serviceId);

            PackageService packageService = new PackageService();
            packageService.setService(serv);
            packageService.setPack(pack);
            packageService.setSource(source);
            packageService.setDeliveryMethod(
                    Service.DeliveryMethod.fromString(deliveryMethod));

            return packageService;
        }
    }

    private String name;
    private Boolean deprecated;
    private Set<ServiceItem> services;

    @Override
    protected List<String> getInvalidNonNullableFields() {
        List<String> res = new ArrayList<>();

        if (name == null) res.add("name");

        return res;
    }

    @Override
    public void testValidity(boolean requireNotNullFields) throws InvalidInputException {
        InvalidInputException exceptionList = super.buildException(requireNotNullFields);

        if (services != null) {
            for (ServiceItem item : services) {
                item.testForNull(exceptionList, nullMessage);
            }
        }

        validateField(name, "name",
                InputValidator::validateName, InputValidator.NAME_RULES,
                exceptionList);

        if (exceptionList.containsMessages()) {
            throw exceptionList;
        }
    }

    @Override
    public Package getEntity() {
        Package res = new Package();

        res.setName(name);
        res.setDeprecated(Objects.requireNonNullElse(deprecated, false));
        res.setServices(getServiceSet(res));

        return res;
    }

    @Override
    public void updateEntity(Package other) {
        if (name != null) other.setName(name);
        if (services != null) {
            other.setServices(getServiceSet(other));
        }
    }

    private Set<PackageService> getServiceSet(Package pack) {
        if (services == null) {
            return new HashSet<>();
        }
        return services.stream()
                .map(s -> s.getService(pack))
                .collect(Collectors.toSet());
    }
}


