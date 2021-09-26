package be.sel2.api.controllers;

import be.sel2.api.dtos.ProposalDTO;
import be.sel2.api.entities.Package;
import be.sel2.api.entities.*;
import be.sel2.api.entities.relations.ContactProposal;
import be.sel2.api.entities.relations.ProposalService;
import be.sel2.api.exceptions.ForbiddenException;
import be.sel2.api.exceptions.InvalidInputException;
import be.sel2.api.exceptions.not_found.*;
import be.sel2.api.models.RichCollectionModel;
import be.sel2.api.repositories.*;
import be.sel2.api.users.UserInfoDetails;
import be.sel2.api.util.AllFieldSearchUtil;
import be.sel2.api.util.InputValidator;
import be.sel2.api.util.SecurityUtils;
import be.sel2.api.util.SortUtils;
import be.sel2.api.util.mails.EmailService;
import be.sel2.api.util.mails.LocaliseUtil;
import be.sel2.api.util.mails.MailBuilder;
import be.sel2.api.util.specifications.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * This class controls all requests to `/proposals`
 */
@RestController
@RequestMapping("/proposals")
public class ProposalController {

    // These are the proposal statuses that are not accessible to customers
    private static final List<Proposal.ProposalStatus> restrictStatuses = List.of(
            Proposal.ProposalStatus.ACCEPTED, Proposal.ProposalStatus.COMPLETED,
            Proposal.ProposalStatus.DENIED);

    private static final String STATUS_UPDATE_TEMPLATE = "classpath:mailTemplates/proposalStatusNotification.txt";

    private static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";
    private static final String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final ProposalRepository repository;
    private final UserRepository userRepo;
    private final OrganisationRepository organisRepo;
    private final ContactProposalRepository conPropRepo;
    private final ProposalServiceRepository propServRepo;
    private final ContactRepository contactRepo;
    private final ServiceRepository serviceRepo;
    private final PackageRepository packageRepo;
    private final FileRepository fileRepo;

    private final EmailService emailService;
    private final MailBuilder mailBuilder;

    /**
     * Creates a new {@link ProposalController} with given repository.
     * This class controls all requests to `/proposals`
     *
     * @param repository   Source {@link ProposalRepository} for this controller
     * @param organisRepo  Source {@link OrganisationRepository} for this controller
     * @param conPropRepo  Source {@link ContactProposalRepository} for this controller
     * @param propServRepo Source {@link ProposalServiceRepository} for this controller
     * @param contactRepo  Source {@link ContactRepository} for this controller
     * @param serviceRepo  Source {@link ServiceRepository} for this controller
     * @param packageRepo  Source {@link PackageRepository} for this controller
     * @param fileRepo     Source {@link FileRepository} for this controller
     * @param emailService {@link EmailService} used to send notifications
     * @param mailBuilder  {@link MailBuilder} used to create mail messages
     */
    @Autowired
    public ProposalController(ProposalRepository repository, OrganisationRepository organisRepo, ContactProposalRepository conPropRepo, ProposalServiceRepository propServRepo, ContactRepository contactRepo, ServiceRepository serviceRepo, PackageRepository packageRepo, FileRepository fileRepo, UserRepository userRepo, EmailService emailService, MailBuilder mailBuilder) {
        this.repository = repository;
        this.userRepo = userRepo;
        this.organisRepo = organisRepo;
        this.conPropRepo = conPropRepo;
        this.propServRepo = propServRepo;
        this.contactRepo = contactRepo;
        this.serviceRepo = serviceRepo;
        this.packageRepo = packageRepo;
        this.fileRepo = fileRepo;
        this.emailService = emailService;
        this.mailBuilder = mailBuilder;
    }

    private void assertNotInRestrictedStatuses(Proposal proposal) {
        if (restrictStatuses.contains(proposal.getStatus())) {
            throw new ForbiddenException("Unauthorized status");
        }
    }

    private void assertIsApprovedOrganisation(Organisation organisation) {
        if (Boolean.FALSE.equals(organisation.getApproved())) {
            throw new ForbiddenException("Can't make a proposal for an unapproved organisation");
        }
    }

    private void assertFileExists(Long fileId) {
        if (fileId != null && !fileRepo.existsById(fileId)) {
            throw new FileNotFoundException(fileId);
        }
    }

    private void assertContactExists(ContactProposal contactProposal) {
        if (!contactRepo.existsById(contactProposal.getContact().getId())) {
            throw new ContactNotFoundException(contactProposal.getContact().getId());
        }
    }

    private void assertServiceExists(ProposalService proposalService) {
        if (!serviceRepo.existsById(proposalService.getService().getId())) {
            throw new ServiceNotFoundException(proposalService.getService().getId());
        }
    }

    private void assertPackageExists(Package pack) {
        if (!packageRepo.existsById(pack.getId())) {
            throw new PackageNotFoundException(pack.getId());
        }
    }

    private Specification<Proposal> getRelatedAsMember(UserInfo user) {
        List<Organisation> memberOrganisations = user.getMemberships().stream()
                .filter(Member::getAccepted)
                .map(Member::getOrganisation)
                .collect(Collectors.toList());

        ContainsSpecification<Proposal> propResult = new ContainsSpecification<>();
        for (Organisation org : memberOrganisations) {
            propResult.add(new SearchCriteria("organisation", org));
        }

        return propResult;
    }

    private Specification<Proposal> getRelatedAsContact(String email) {

        DefaultSpecification<Contact> contactSpec = new DefaultSpecification<>();
        contactSpec.add(new SearchCriteria("email", email));

        List<Contact> result = contactRepo.findAll(contactSpec);

        if (result.isEmpty()) {
            DefaultSpecification<Proposal> res = new DefaultSpecification<>();
            res.add(new SearchCriteria("id", 0L));
            return res;
        }

        DefaultSpecification<ContactProposal> conPropSpec = new ContainsSpecification<>();
        conPropSpec.addAll(result.stream().map(c ->
                new SearchCriteria("contact", c)
        ).collect(Collectors.toSet()));

        List<ContactProposal> conProps = conPropRepo.findAll(conPropSpec);

        if (conProps.isEmpty()) {
            DefaultSpecification<Proposal> res = new DefaultSpecification<>();
            res.add(new SearchCriteria("id", 0L));
            return res;
        }

        AnyIsInListSpecification<Proposal> resSpec = new AnyIsInListSpecification<>();
        resSpec.add(new SearchCriteria("contacts", conProps));

        return resSpec;
    }

    private Specification<Proposal> getGetSpecification(
            Map<String, String> parameters,
            Optional<Long> organisationId,
            Optional<String[]> status,
            Optional<Date> deadline,
            Optional<Date> legalDeadline,
            Optional<Date> tiDeadline) {

        DefaultSpecification<Proposal> specification = new PartialMatchSpecification<>();
        specification.addWithKey(parameters, "name");
        deadline.ifPresent(s -> specification.add(new SearchCriteria("deadline", s)));
        legalDeadline.ifPresent(s -> specification.add(new SearchCriteria("legalDeadline", s)));
        tiDeadline.ifPresent(s -> specification.add(new SearchCriteria("tiDeadline", s)));


        DefaultSpecification<Proposal> secondSpec = new ContainsSpecification<>();

        status.ifPresent(s -> secondSpec.addAll(Arrays.stream(s).map(
                stat -> new SearchCriteria("status", Proposal.ProposalStatus.fromString(stat)))
                .collect(Collectors.toList())));


        organisationId.ifPresent(s -> {
            Organisation organisation = organisRepo
                    .findById(s)
                    .orElseThrow(() -> new OrganisationNotFoundException(s));

            specification.add(new SearchCriteria("organisation", organisation));
        });

        return status.isPresent() ? specification.and(secondSpec) : specification;
    }

    private DefaultSpecification<Proposal> beforeDeadlines(
            Optional<Date> deadlineBefore,
            Optional<Date> legalDeadlineBefore,
            Optional<Date> tiDeadlineBefore) {
        BeforeDateSpecification<Proposal> spec = new BeforeDateSpecification<>();

        deadlineBefore.ifPresent(s -> spec.add(new SearchCriteria("deadline", s)));
        legalDeadlineBefore.ifPresent(s -> spec.add(new SearchCriteria("legalDeadline", s)));
        tiDeadlineBefore.ifPresent(s -> spec.add(new SearchCriteria("tiDeadline", s)));

        return spec;
    }

    private WebMvcLinkBuilder linkToSearchProposal() {
        return linkTo(ProposalController.class);
    }

    private EntityModel<Proposal> proposalToHateoas(Proposal prop) {
        return EntityModel.of(prop,
                linkTo(methodOn(ProposalController.class).one(prop.getId())).withSelfRel(),
                linkToSearchProposal().withRel("proposals"),
                linkTo(methodOn(OrganisationController.class)
                        .one(prop.getOrganisation().getId())).withRel("organisation")
        );
    }

    //AUTH: ADMIN, EMPLOYEE, CUSTOMER
    //      ON CUSTOMER: return only if is CONTACT or MEMBER
    @Secured({ROLE_ADMIN, ROLE_EMPLOYEE, ROLE_CUSTOMER})
    @GetMapping //Handles GET requests
    public RichCollectionModel<Proposal> searchProposals(
            @RequestParam Map<String, String> parameters,
            @RequestParam Optional<Long> organisationId,
            @RequestParam Optional<String[]> status,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<Date> deadline,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<Date> legalDeadline,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<Date> tiDeadline,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<Date> deadlineBefore,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<Date> legalDeadlineBefore,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<Date> tiDeadlineBefore, // Niet te vervangen met Map, omdat Date correct moet worden ingelezen
            @RequestParam(defaultValue = "0") int skip,
            @RequestParam(defaultValue = "50") int limit) {

        Pageable pageable = new OffsetBasedPageable(skip, limit,
                SortUtils.sortWithParameters(parameters, Proposal.class));

        Specification<Proposal> specification = getGetSpecification(
                parameters, organisationId, status,
                deadline, legalDeadline, tiDeadline);

        specification = specification
                .and(beforeDeadlines(deadlineBefore, legalDeadlineBefore, tiDeadlineBefore));

        specification = specification.and(AllFieldSearchUtil.getSpecification(Proposal.class, parameters));

        SecurityContext context = SecurityContextHolder.getContext();

        if (SecurityUtils.hasRole(context, ROLE_CUSTOMER)) {
            // The user is a customer

            UserInfoDetails userDetails = SecurityUtils.getDetails(context);

            UserInfo user = userRepo.findById(userDetails.getId())
                    .orElseThrow(() -> new UserNotFoundException(userDetails.getId()));

            Specification<Proposal> relationSpec = getRelatedAsMember(user)
                    .or(getRelatedAsContact(userDetails.getUsername()));

            specification = specification.and(relationSpec);
        }

        Page<Proposal> searchResults = repository.findAll(specification, pageable);

        List<EntityModel<Proposal>> proposals = searchResults
                .stream().map(this::proposalToHateoas)
                .collect(Collectors.toList());

        return RichCollectionModel.of(proposals, searchResults,
                linkToSearchProposal().withSelfRel());
    }

    //AUTH: ADMIN, EMPLOYEE, CUSTOMER
    //      ON CUSTOMER: becomes SUBMITTER CONTACT
    @Secured({ROLE_ADMIN, ROLE_EMPLOYEE, ROLE_CUSTOMER})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping //Handles POST requests
    public EntityModel<Proposal> newProposal(@RequestBody ProposalDTO createProposal) {

        createProposal.testValidity(true);

        Long organisationId = createProposal.getOrganisationId();
        Organisation organisation = organisRepo
                .findById(organisationId)
                .orElseThrow(() -> new OrganisationNotFoundException(organisationId));

        assertIsApprovedOrganisation(organisation);

        for (Long fileId : createProposal.getFileIDs()) {
            assertFileExists(fileId);
        }

        Proposal newProposal = createProposal.getEntity();

        newProposal.setOrganisation(organisation);

        if (newProposal.getStatus() == null) {
            newProposal.setStatus(Proposal.ProposalStatus.DRAFT);
        }

        Set<ContactProposal> newContactLinks = newProposal.getContacts();

        Set<ProposalService> newServiceLinks = newProposal.getServices();

        Set<Package> newPackageLinks = newProposal.getPackages();

        // Empty the sets, to prevent floating pointers while saving proposal
        newProposal.setContacts(new HashSet<>());
        newProposal.setServices(new HashSet<>());
        // No need to empty out the package set, these are not saved using intermediate entities

        for (ContactProposal conProp : newContactLinks) {
            assertContactExists(conProp);
        }

        for (ProposalService propServ : newServiceLinks) {
            assertServiceExists(propServ);
        }

        for (Package pack : newPackageLinks) {
            assertPackageExists(pack);
        }

        SecurityContext context = SecurityContextHolder.getContext();

        if (SecurityUtils.hasRole(context, ROLE_CUSTOMER)) {
            assertNotInRestrictedStatuses(newProposal); // Checks if the status of this proposal is valid
            if (newContactLinks.stream()
                    .noneMatch(link -> link.getRole().equals(ContactProposal.Contactrole.SUBMITTER))) {
                // There is no submitter contact
                throw new ForbiddenException("There must be at least one Submitter in the contact list");
            }
        }

        //First save the proposal, then all of it's relations
        newProposal = repository.save(newProposal);

        Proposal finalNewProposal = newProposal;
        newContactLinks = newContactLinks.stream().map(conProp -> {
            conProp.setProposal(finalNewProposal);
            return conPropRepo.save(conProp);
        }).collect(Collectors.toSet());

        for (ProposalService propServ : newServiceLinks) {
            propServ.setProposal(newProposal);
            propServRepo.save(propServ);
        }

        Proposal finalNewProposal1 = newProposal;
        newServiceLinks = newServiceLinks.stream().map(propServ -> {
            propServ.setProposal(finalNewProposal1);
            return propServRepo.save(propServ);
        }).collect(Collectors.toSet());

        newProposal.setServices(newServiceLinks);
        newProposal.setContacts(newContactLinks);

        return proposalToHateoas(newProposal);
    }

    // Single item
    //AUTH: ADMIN, EMPLOYEE or (CUSTOMER if CONTACT or MEMBER)
    @Secured({ROLE_ADMIN, ROLE_EMPLOYEE, ROLE_CUSTOMER})
    @GetMapping("/{id}") //Handles GET requests
    public EntityModel<Proposal> one(@PathVariable Long id) {

        Proposal proposal = repository.findById(id)
                .orElseThrow(() -> new ProposalNotFoundException(id));

        SecurityContext context = SecurityContextHolder.getContext();

        if (!SecurityUtils.isContact(context, false, proposal) &&
                !SecurityUtils.isMember(context, false, proposal.getOrganisation())) {
            throw new ForbiddenException("You need to be a contact or organisation member to view this information");
        }

        return proposalToHateoas(proposal);
    }

    //AUTH: ADMIN, EMPLOYEE or (CUSTOMER if MEMBER or SUBMITTER CONTACT)
    //      ON EMPLOYEE: ONLY APPROVAL/DISAPPROVAL/FEEDBACK
    @Secured({ROLE_ADMIN, ROLE_EMPLOYEE, ROLE_CUSTOMER})
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}") //Handles PATCH requests
    public EntityModel<Proposal> editProposal(@RequestBody ProposalDTO newProposal,
                                              @PathVariable Long id) {

        SecurityContext context = SecurityContextHolder.getContext();

        Proposal prop = repository.findById(id)
                .orElseThrow(() -> new ProposalNotFoundException(id));

        if (SecurityUtils.hasRole(context, ROLE_EMPLOYEE)) {
            //Set fields the EMPLOYEE is allowed to update
            ProposalDTO safeProposalDTO = new ProposalDTO();
            safeProposalDTO.setStatus(newProposal.getStatus());
            safeProposalDTO.setFeedback(newProposal.getFeedback());

            newProposal = safeProposalDTO;
        } else if (SecurityUtils.hasRole(context, ROLE_CUSTOMER)) {
            // The user is a customer
            if (!SecurityUtils.isMember(context, false, prop.getOrganisation()) &&
                    !SecurityUtils.isContact(context, true, prop)
            ) {
                // The customer is not a manager, nor a submitter
                throw new ForbiddenException("Unauthorized patch");
            }
            if (newProposal.getStatus() != null &&
                    restrictStatuses.contains(newProposal.getEnumStatus())) {
                throw new ForbiddenException("Unauthorized status");
            }
        }

        newProposal.testValidity(false);

        for (Long fileId : newProposal.getFileIDs()) {
            assertFileExists(fileId);
        }

        Set<ProposalService> oldServices = prop.getServices();
        Set<ContactProposal> oldContacts = prop.getContacts();

        oldServices = new HashSet<>(propServRepo.findAllById(oldServices.stream()
                .map(ProposalService::getId).collect(Collectors.toSet())));
        oldContacts = new HashSet<>(conPropRepo.findAllById(oldContacts.stream()
                .map(ContactProposal::getId).collect(Collectors.toSet())));

        Proposal.ProposalStatus oldStatus = prop.getStatus();

        newProposal.updateEntity(prop);

        if ((newProposal.getDeadline() != null ||
                newProposal.getTiDeadline() != null) &&
                prop.getDeadline() != null &&
                prop.getTiDeadline() != null &&
                !InputValidator.validateDeadlines(prop.getTiDeadline(), prop.getDeadline())
        ) {
            InvalidInputException validationException = new InvalidInputException();
            validationException.addMessage("tiDeadline", InputValidator.INVALID_DATE_ORDER); // Deadlines are not set correctly
            validationException.addMessage("deadline", InputValidator.INVALID_DATE_ORDER); // Deadlines are not set correctly
            throw validationException;
        }

        boolean statusUpdated = !oldStatus.equals(prop.getStatus());

        Set<ProposalService> newServices = prop.getServices();
        Set<ContactProposal> newContacts = prop.getContacts();

        if (SecurityUtils.hasRole(context, ROLE_CUSTOMER) &&
                newProposal.getContacts() != null &&
                newContacts.stream()
                        .noneMatch(link -> link.getRole().equals(ContactProposal.Contactrole.SUBMITTER))
        ) {
            // The user is a customer and the last SUBMITTER was deleted
            throw new ForbiddenException("A proposal must have at least one submitter");
        }

        // All null-checks must occur before altering DB
        // If services were passed, we run trough the list, check whether they all exist
        // and if they do add them to the proposal
        if (newProposal.getServices() != null) {
            for (ProposalService propServ : newServices) {
                assertServiceExists(propServ);
                propServ.setProposal(prop);
            }
        }

        // Contacts work similar to proposals
        if (newProposal.getContacts() != null) {
            for (ContactProposal conProp : newContacts) {
                assertContactExists(conProp);
                conProp.setProposal(prop);
            }
        }

        if (newProposal.getServices() != null && !(oldServices.isEmpty() && newServices.isEmpty())) {
            //NOTE: maybe make this more efficient?
            propServRepo.deleteAll(oldServices);
            prop.setServices(new HashSet<>(
                    propServRepo.saveAll(newServices)
            ));
            propServRepo.flush();
        }

        // Contacts work similar to proposals
        if (newProposal.getContacts() != null && !(oldContacts.isEmpty() && newContacts.isEmpty())) {
            //NOTE: maybe make this more efficient?
            conPropRepo.deleteAll(oldContacts);
            prop.setContacts(new HashSet<>(
                    conPropRepo.saveAll(newContacts)
            ));
            conPropRepo.flush();
        }

        Proposal result = repository.save(prop);

        if (statusUpdated) {

            String[] contactMails = result.getContacts().stream()
                    .map(con -> con.getContact().getEmail()).toArray(String[]::new);
            String message = mailBuilder.buildStringFromResource(
                    STATUS_UPDATE_TEMPLATE,
                    result.getName(),
                    LocaliseUtil.localize(result.getStatus()),
                    mailBuilder.applicationProposalUrl(result.getId()));

            emailService.sendEmail(
                    mailBuilder.titleFormat(result.getName()),
                    message,
                    contactMails);
        }

        return proposalToHateoas(result);
    }

    //AUTH: ADMIN or (CUSTOMER if MEMBER or SUBMITTER CONTACT)
    @Secured({ROLE_ADMIN, ROLE_CUSTOMER})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}") //Handles DELETE requests
    public void deleteProposal(@PathVariable Long id) {

        Proposal proposal = repository.findById(id)
                .orElseThrow(() -> new ProposalNotFoundException(id));

        SecurityContext context = SecurityContextHolder.getContext();

        if (
                SecurityUtils.hasRole(context, ROLE_ADMIN) ||
                        SecurityUtils.isMember(context, false, proposal.getOrganisation()) ||
                        SecurityUtils.isContact(context, true, proposal)
        ) {
            repository.deleteById(id);
        } else {
            throw new ForbiddenException("Unauthorized delete");
        }
    }
}
