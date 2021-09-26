package be.sel2.api.dtos;

import be.sel2.api.entities.Package;
import be.sel2.api.entities.*;
import be.sel2.api.entities.relations.ContactProposal;
import be.sel2.api.entities.relations.ProposalService;
import be.sel2.api.exceptions.InputValidationFailedException;
import be.sel2.api.exceptions.InvalidInputException;
import be.sel2.api.models.BooleanModel;
import be.sel2.api.util.InputValidator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter //This adds all the getters & setters for us
public class ProposalDTO extends DTOObject<Proposal> {

    private static List<String> statusOptions = Arrays.stream(Proposal.ProposalStatus.values())
            .map(proposalInstance -> proposalInstance.name().toLowerCase()).collect(Collectors.toList());
    private static List<String> numberOfRequestOptions = Arrays.stream(Proposal.NumberOfRequests.values())
            .map(Proposal.NumberOfRequests::toString).collect(Collectors.toList());
    private static List<String> onlineOptionOptions = Arrays.stream(Proposal.OnlineOption.values())
            .map(Proposal.OnlineOption::toString).collect(Collectors.toList());
    private static List<String> eboxOptions = Arrays.stream(Proposal.EboxOption.values())
            .map(Proposal.EboxOption::toString).collect(Collectors.toList());

    // Velden
    private String name;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date deadline;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date legalDeadline;
    private String businessContext;
    private String legalContext;
    private String functionalSetup;
    private String technicalSetup;
    private Long organisationId; //Organisation key
    private Set<ServiceItem> services;
    private Set<ContactItem> contacts;
    private Set<PackageItem> packages;

    private BooleanModel requiresPersonalData;
    private String purposeRequestedData;
    private BooleanModel extensionPreviousProposal;
    private String originalTanNumber;
    private String originalUri;
    private String originalLegalContext;
    private String ftpAccount;
    private String architectureVisualizationExplanation;
    private BooleanModel requestsAreSpread;
    private String estimatedNumberOfRequests;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date tiDeadline;
    private String explanationDeadline;
    private String feedback;

    private BooleanModel inspection;

    private Long architectureVisualization; //FileMeta key

    //New additional fields
    private Long authorizationPersonalData; //FileMeta key
    private BooleanModel ftp;
    private String externIpTest;
    private String externIpProd;
    private String externIpRangeTest;
    private String externIpRangeProd;

    private Long sshKey; //FileMeta key
    private BooleanModel reuseUri;
    private BooleanModel reuseLegalContext;
    private BooleanModel reuseFtpAccount;
    private BooleanModel reusePreviousProposal;
    private String cnPersonalData;
    private String cnRepertorium;
    private String cnOther;
    private BooleanModel magdaMessages;

    private String onlineOption; // enum

    private Long cooperationAgreement; //FileMeta key

    private Set<String> options; //Set of enums

    private Long processingAgreement; //FileMeta key
    private Long peaks;

    @Override
    protected List<String> getInvalidNonNullableFields() {
        List<String> res = new ArrayList<>();

        if (name == null) res.add("name");
        if (organisationId == null) res.add("organisationId");

        return res;
    }

    @Override
    public void testValidity(boolean requireNotNullFields) throws InvalidInputException {
        InvalidInputException exceptionList = super.buildException(requireNotNullFields);

        validateField(name, "name",
                InputValidator::validateName, InputValidator.NAME_RULES,
                exceptionList);

        validateField(status, "status",
                statusOptions::contains,
                InputValidator.listOfOptionsRule(statusOptions),
                exceptionList);

        validateField(estimatedNumberOfRequests, "estimatedNumberOfRequests",
                numberOfRequestOptions::contains,
                InputValidator.listOfOptionsRule(numberOfRequestOptions),
                exceptionList);

        validateField(onlineOption, "onlineOption",
                onlineOptionOptions::contains,
                InputValidator.listOfOptionsRule(onlineOptionOptions),
                exceptionList);

        // Cannot use validateField since 2 parameters are required
        validateDeadlines(tiDeadline, deadline, exceptionList);

        if (options != null) {
            for (String opt : options) {
                validateField(opt, "options",
                        eboxOptions::contains,
                        InputValidator.listOfOptionsRule(eboxOptions),
                        exceptionList);
            }
        }

        Set<String> setContentErrors = new HashSet<>();

        if (services != null) {
            for (ServiceItem item : services) {
                setContentErrors.addAll(item.getNullFields());
            }
        }

        if (contacts != null) {
            for (ContactItem item : contacts) {
                setContentErrors.addAll(item.getNullFields());
            }
        }

        if (packages != null) {
            for (PackageItem item : packages) {
                setContentErrors.addAll(item.getNullFields());
            }
        }

        for (String field : setContentErrors) {
            exceptionList.addMessage(field, nullMessage);
        }

        if (exceptionList.containsMessages()) {
            throw exceptionList;
        }
    }

    @Override
    protected void validateMaxInputLength(InvalidInputException ex) {
        Field[] fields = ProposalDTO.class.getDeclaredFields();

        for (Field f : fields) {
            if (f.getType().equals(String.class)) {
                try {
                    if (f.getName().equals("feedback")) {
                        validateField((String) f.get(this), f.getName(),
                                InputValidator::validateMaxLenFeedback, InputValidator.MAX_LEN_LONG_RULES,
                                ex);
                    } else {
                        validateField((String) f.get(this), f.getName(),
                                InputValidator::validateMaxLenLong, InputValidator.MAX_LEN_LONG_RULES,
                                ex);
                    }
                } catch (IllegalArgumentException | IllegalAccessException exception) {
                    throw new InputValidationFailedException(exception);
                }
            }
        }

        if (contacts != null) {
            for (ContactItem item : contacts) {
                item.validateValues(ex);
            }
        }

        if (services != null) {
            for (ServiceItem item : services) {
                item.validateMaxInputLength(ex);
            }
        }
    }

    // This checks if the order of the deadlines makes sense: testing before production
    private void validateDeadlines(Date before, Date after, InvalidInputException ex) {
        if (before != null && after != null && !InputValidator.validateDeadlines(before, after)) {
            ex.addMessage("tiDeadline", InputValidator.INVALID_DATE_ORDER); // Deadlines are not set correctly
            ex.addMessage("deadline", InputValidator.INVALID_DATE_ORDER); // Deadlines are not set correctly
        }
    }

    @Override
    public Proposal getEntity() {
        Proposal res = new Proposal();

        res.setOrganisation(getOrganisation());

        // If these fields were null, nothing changes and they remain null
        updateEntity(res);

        // Initialize as empty if were null
        res.setServices(getServiceSet());
        res.setContacts(getContactSet());
        res.setPackages(getPackageSet());

        setBooleanDefaults(res);

        return res;
    }

    private void setBooleanDefaults(Proposal other) {

        if (inspection == null) other.setInspection(BooleanModel.NOT_SET);
        if (requestsAreSpread == null) other.setRequestsAreSpread(BooleanModel.NOT_SET);
        if (extensionPreviousProposal == null) other.setExtensionPreviousProposal(BooleanModel.NOT_SET);
        if (requiresPersonalData == null) other.setRequiresPersonalData(BooleanModel.NOT_SET);
        if (ftp == null) other.setFtp(BooleanModel.NOT_SET);
        if (reuseUri == null) other.setReuseUri(BooleanModel.NOT_SET);
        if (reuseLegalContext == null) other.setReuseLegalContext(BooleanModel.NOT_SET);
        if (reuseFtpAccount == null) other.setReuseFtpAccount(BooleanModel.NOT_SET);
        if (reusePreviousProposal == null) other.setReusePreviousProposal(BooleanModel.NOT_SET);
        if (magdaMessages == null) other.setMagdaMessages(BooleanModel.NOT_SET);
    }

    private void updateBasicInformation(Proposal other) {
        if (name != null) other.setName(name);
        if (status != null) other.setStatus(getEnumStatus());

        if (businessContext != null) other.setBusinessContext(businessContext);
        if (legalContext != null) other.setLegalContext(legalContext);
        if (functionalSetup != null) other.setFunctionalSetup(functionalSetup);
        if (technicalSetup != null) other.setTechnicalSetup(technicalSetup);

        if (requiresPersonalData != null) other.setRequiresPersonalData(requiresPersonalData);
        if (purposeRequestedData != null) other.setPurposeRequestedData(purposeRequestedData);

        if (ftpAccount != null) other.setFtpAccount(ftpAccount);
        if (ftp != null) other.setFtp(ftp);

        if (inspection != null) other.setInspection(inspection);
    }

    private void updateDeadlines(Proposal other) {
        if (deadline != null) other.setDeadline(deadline);
        if (legalDeadline != null) other.setLegalDeadline(legalDeadline);
        if (tiDeadline != null) other.setTiDeadline(tiDeadline);
        if (explanationDeadline != null) other.setExplanationDeadline(explanationDeadline);
    }

    private void updateRelations(Proposal other) {
        // Organisation is NOT to be updated
        if (services != null) other.setServices(getServiceSet());
        if (contacts != null) other.setContacts(getContactSet());
        if (packages != null) other.setPackages(getPackageSet());
    }

    private void updateArchitectureVisualization(Proposal other) {
        if (architectureVisualizationExplanation != null)
            other.setArchitectureVisualizationExplanation(architectureVisualizationExplanation);

        if (architectureVisualization != null) {
            other.setArchitectureVisualization(fileIdToObject(architectureVisualization));
        }
    }

    private void updateExtensionInformation(Proposal other) {
        if (extensionPreviousProposal != null) other.setExtensionPreviousProposal(extensionPreviousProposal);
        if (originalTanNumber != null) other.setOriginalTanNumber(originalTanNumber);
        if (originalUri != null) other.setOriginalUri(originalUri);
        if (originalLegalContext != null) other.setOriginalLegalContext(originalLegalContext);
    }

    private void updateFeedback(Proposal other) {
        if (feedback != null) other.setFeedback(feedback);
        if (magdaMessages != null) other.setMagdaMessages(magdaMessages);
    }

    private void updateRequestInformation(Proposal other) {
        if (requestsAreSpread != null) other.setRequestsAreSpread(requestsAreSpread);
        if (estimatedNumberOfRequests != null)
            other.setEstimatedNumberOfRequests(
                    Proposal.NumberOfRequests.fromString(estimatedNumberOfRequests)
            );
        if (peaks != null) other.setPeaks(peaks);
    }

    private void updateFileReferences(Proposal other) {
        if (authorizationPersonalData != null)
            other.setAuthorizationPersonalData(fileIdToObject(authorizationPersonalData)); //FileMeta key
        if (processingAgreement != null)
            other.setProcessingAgreement(fileIdToObject(processingAgreement)); //FileMeta key
        if (cooperationAgreement != null)
            other.setCooperationAgreement(fileIdToObject(cooperationAgreement)); //FileMeta key
        if (sshKey != null) other.setSshKey(fileIdToObject(sshKey)); //FileMeta key
    }

    private void updateReuseData(Proposal other) {
        if (reuseUri != null) other.setReuseUri(reuseUri);
        if (reuseLegalContext != null) other.setReuseLegalContext(reuseLegalContext);
        if (reuseFtpAccount != null) other.setReuseFtpAccount(reuseFtpAccount);
        if (reusePreviousProposal != null) other.setReusePreviousProposal(reusePreviousProposal);
    }

    private void updateCnData(Proposal other) {
        if (cnPersonalData != null) other.setCnPersonalData(cnPersonalData);
        if (cnRepertorium != null) other.setCnRepertorium(cnRepertorium);
        if (cnOther != null) other.setCnOther(cnOther);
    }

    private void updateOptions(Proposal other) {
        if (onlineOption != null) other.setOnlineOption(Proposal.OnlineOption.fromString(onlineOption)); // enum

        if (options != null)
            other.setOptions(
                    options.stream()
                            .map(Proposal.EboxOption::fromString)
                            .collect(Collectors.toSet())); //Set of enums
    }

    private void updateExternIpData(Proposal other) {
        if (externIpTest != null) other.setExternIpTest(externIpTest);
        if (externIpProd != null) other.setExternIpProd(externIpProd);
        if (externIpRangeTest != null) other.setExternIpRangeTest(externIpRangeTest);
        if (externIpRangeProd != null) other.setExternIpRangeProd(externIpRangeProd);
    }

    @Override
    public void updateEntity(Proposal other) {
        updateBasicInformation(other);
        updateDeadlines(other);
        updateRequestInformation(other);

        updateExtensionInformation(other);

        updateRelations(other);

        updateFeedback(other);
        updateArchitectureVisualization(other);

        updateFileReferences(other);
        updateReuseData(other);
        updateCnData(other);
        updateOptions(other);
        updateExternIpData(other);
    }

    public Organisation getOrganisation() {
        Organisation res = new Organisation();
        res.setId(organisationId);
        return res;
    }

    public Set<ContactProposal> getContactSet() {
        if (contacts == null) {
            return new HashSet<>();
        }
        return contacts.stream().map(ContactItem::getContactProposal)
                .collect(Collectors.toSet());
    }

    public Set<ProposalService> getServiceSet() {
        if (services == null) {
            return new HashSet<>();
        }
        return services.stream().map(ServiceItem::getProposalService)
                .collect(Collectors.toSet());
    }

    public Set<Package> getPackageSet() {
        if (packages == null) {
            return new HashSet<>();
        }
        return packages.stream().map(PackageItem::getPackage)
                .collect(Collectors.toSet());
    }

    public void setStatus(String status) {
        if (status == null) {
            this.status = null;
        } else {
            this.status = status.toLowerCase();
        }
    }

    private FileMeta fileIdToObject(Long id) {
        FileMeta res = new FileMeta();
        res.setId(id);
        return res;
    }

    public Set<Long> getFileIDs() {
        Set<Long> idSet = new HashSet<>();

        idSet.add(authorizationPersonalData);
        idSet.add(processingAgreement);
        idSet.add(cooperationAgreement);
        idSet.add(sshKey);
        idSet.add(architectureVisualization);

        return idSet;
    }

    public Proposal.ProposalStatus getEnumStatus() {
        return Proposal.ProposalStatus.fromString(this.status);
    }

    @Getter
    @Setter //This adds all the getters & setters for us
    public static class ServiceItem {
        private static List<String> deliveryMethodOptions = Arrays.stream(Service.DeliveryMethod.values())
                .map(Enum::name)
                .collect(Collectors.toList());


        private String source;
        private String deliveryMethod;
        private Long serviceId;

        public Set<String> getNullFields() {
            Set<String> res = new HashSet<>();

            if (serviceId == null) res.add("services.serviceId");
            if (source == null) res.add("services.source");
            if (deliveryMethod == null) res.add("services.deliveryMethod");

            return res;
        }

        public void validateMaxInputLength(InvalidInputException ex) {
            if (!InputValidator.validateMaxLenShort(source)) {
                ex.addMessage("services.source",
                        String.format("may be at most %d characters long",
                                InputValidator.MAX_STRING_LEN_SHORT));
            }

            if (!deliveryMethodOptions.contains(deliveryMethod)) {
                ex.addMessage("services.deliveryMethod",
                        InputValidator.listOfOptionsRule(deliveryMethodOptions));
            }
        }

        public ProposalService getProposalService() {
            ProposalService res = new ProposalService();

            res.setSource(source);
            res.setDeliveryMethod(getEnumDeliveryMethod());

            Service service = new Service();
            service.setId(serviceId);

            res.setService(service);

            return res;
        }

        private Service.DeliveryMethod getEnumDeliveryMethod() {
            return Service.DeliveryMethod.fromString(this.deliveryMethod);
        }
    }

    @Getter
    @Setter //This adds all the getters & setters for us
    public static class ContactItem {
        private String role;
        private Long contactId;

        private static List<String> roleOptions = Arrays.stream(ContactProposal.Contactrole.values())
                .map(ContactProposal.Contactrole::toString)
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        public Set<String> getNullFields() {
            Set<String> res = new HashSet<>();

            if (role == null) res.add("contacts.role");
            if (contactId == null) res.add("contacts.contactId");

            return res;
        }

        public void validateValues(InvalidInputException ex) {
            if (!roleOptions.contains(role)) {
                ex.addMessage("contacts.role",
                        InputValidator.listOfOptionsRule(roleOptions));
            }
        }

        public ContactProposal getContactProposal() {
            ContactProposal res = new ContactProposal();

            res.setRole(getEnumRole());

            Contact con = new Contact();
            con.setId(contactId);

            res.setContact(con);

            return res;
        }

        private ContactProposal.Contactrole getEnumRole() {
            return ContactProposal.Contactrole.fromString(this.role);
        }
    }

    @Getter
    @Setter //This adds all the getters & setters for us
    public static class PackageItem {
        private Long packageId;

        public Set<String> getNullFields() {
            Set<String> res = new HashSet<>();

            if (packageId == null) res.add("packages.packageId");

            return res;
        }

        public Package getPackage() {
            Package res = new Package();
            res.setId(packageId);
            return res;
        }
    }
}
