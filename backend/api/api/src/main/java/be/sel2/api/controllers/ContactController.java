package be.sel2.api.controllers;

import be.sel2.api.dtos.ContactDTO;
import be.sel2.api.entities.Contact;
import be.sel2.api.entities.Organisation;
import be.sel2.api.exceptions.ForbiddenException;
import be.sel2.api.exceptions.conflict.ContactExistsException;
import be.sel2.api.exceptions.not_found.ContactNotFoundException;
import be.sel2.api.exceptions.not_found.OrganisationNotFoundException;
import be.sel2.api.models.RichCollectionModel;
import be.sel2.api.repositories.ContactRepository;
import be.sel2.api.repositories.OrganisationRepository;
import be.sel2.api.util.AllFieldSearchUtil;
import be.sel2.api.util.SecurityUtils;
import be.sel2.api.util.SortUtils;
import be.sel2.api.util.specifications.DefaultSpecification;
import be.sel2.api.util.specifications.OffsetBasedPageable;
import be.sel2.api.util.specifications.PartialMatchSpecification;
import be.sel2.api.util.specifications.SearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * This class controls all requests to `/organisations/{organisationId}/contacts`
 */
@RestController
@RequestMapping("/organisations")
public class ContactController {

    private final ContactRepository repository;
    private final OrganisationRepository organRepo;

    /**
     * Creates a new {@link ContactController} with given repository.
     * This class controls all requests to `/organisations/{organisationId}/contacts`
     *
     * @param repository Source {@link ContactRepository} for this controller
     * @param organRepo  Source {@link OrganisationRepository} for this controller
     */
    public ContactController(ContactRepository repository, OrganisationRepository organRepo) {
        this.repository = repository;
        this.organRepo = organRepo;
    }


    private Link linkToSearchContact(Long organisationId) {
        Map<String, Long> values = new HashMap<>();
        values.put("organisationId", organisationId);
        return Link.of(linkTo(ContactController.class).toUri() + "/{organisationId}/contacts")
                .expand(values);
    }

    private EntityModel<Contact> contactToHateoas(Long organisationId, Contact contact) {
        return EntityModel.of(contact,
                linkTo(methodOn(ContactController.class).one(contact.getOrganisation().getId(), contact.getId())).withSelfRel(),
                linkToSearchContact(organisationId).withRel("contacts")
        );
    }

    /**
     * @param newContact who's email needs to be checked if it already exists
     * @return true if there is already someone else with the same email in this organisation otherwise false
     */
    private boolean emailAlreadyExists(Contact newContact) {
        DefaultSpecification<Contact> specification = new DefaultSpecification<>();
        specification.add(new SearchCriteria("email", newContact.getEmail()));
        specification.add(new SearchCriteria("organisation", newContact.getOrganisation()));
        return repository.findOne(specification)
                .filter(contact -> !contact.getId().equals(newContact.getId()))
                .isPresent();
    }

    /**
     * Requires that any CUSTOMER calling the method is also a MEMBER
     *
     * @param requireManager if true, requires the user to be a MANAGER
     * @param org            the organisation for which one needs to be a MEMBER
     * @throws ForbiddenException Thrown when the requirements are not met
     */
    private void requireMember(boolean requireManager, Organisation org) throws ForbiddenException {
        SecurityContext context = SecurityContextHolder.getContext();

        if (!SecurityUtils.isMember(context, requireManager, org)) {
            throw new ForbiddenException("You do not have access to this path");
        }
    }

    //AUTH: ADMIN, EMPLOYEE or (CUSTOMER if MEMBER)
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CUSTOMER"})
    @GetMapping("/{organisationId}/contacts") //Handles GET requests
    public RichCollectionModel<Contact> searchContacts(
            @PathVariable Long organisationId,
            @RequestParam Map<String, String> parameters,
            @RequestParam(defaultValue = "0") int skip,
            @RequestParam(defaultValue = "50") int limit) {

        Organisation organ = organRepo.findById(organisationId)
                .orElseThrow(() -> new OrganisationNotFoundException(organisationId));

        requireMember(false, organ);

        List<String> entityParameters = List.of("firstName", "lastName", "email", "phoneNumber", "created", "lastUpdated");

        Pageable pageable = new OffsetBasedPageable(skip, limit,
                SortUtils.sortWithParameters(parameters, entityParameters));
        DefaultSpecification<Contact> specification = new PartialMatchSpecification<>();

        for (String key : entityParameters) {
            specification.addWithKey(parameters, key);
        }

        specification.add(new SearchCriteria("organisation", organ));

        Specification<Contact> resultingSpec = specification
                .and(AllFieldSearchUtil.getSpecification(Contact.class, parameters));

        Page<Contact> searchResult = repository.findAll(resultingSpec, pageable);

        List<EntityModel<Contact>> contact = searchResult
                .stream().map(con -> contactToHateoas(organisationId, con))
                .collect(Collectors.toList());

        return RichCollectionModel.of(contact, searchResult,
                linkToSearchContact(organisationId).withSelfRel());
    }

    //AUTH: ADMIN or (CUSTOMER if MEMBER)
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER"})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{organisationId}/contacts") //Handles POST requests
    public EntityModel<Contact> newContact(@RequestBody ContactDTO createContact, @PathVariable Long organisationId) {
        Organisation organ = organRepo.findById(organisationId)
                .orElseThrow(() -> new OrganisationNotFoundException(organisationId));

        requireMember(false, organ);

        createContact.testValidity(true);
        Contact newContact = createContact.getEntity();

        newContact.setOrganisation(organ);

        if (emailAlreadyExists(newContact)) {
            throw new ContactExistsException();
        }

        return contactToHateoas(organisationId, repository.save(newContact));
    }

    // Single item
    //AUTH: ADMIN, EMPLOYEE or (CUSTOMER if MEMBER)
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CUSTOMER"})
    @GetMapping("/{organisationId}/contacts/{id}") //Handles GET requests
    public EntityModel<Contact> one(@PathVariable Long organisationId, @PathVariable Long id) {

        Organisation organ = organRepo.findById(organisationId)
                .orElseThrow(() -> new OrganisationNotFoundException(organisationId));

        requireMember(false, organ);

        Contact contact = repository.findById(id)
                .orElseThrow(() -> new ContactNotFoundException(id));

        if (!organ.equals(contact.getOrganisation())) {
            throw new ContactNotFoundException(id);
        }

        return contactToHateoas(organisationId, contact);
    }

    //AUTH: ADMIN or (CUSTOMER if MANAGER)
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER"})
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{organisationId}/contacts/{id}") //Handles PATCH requests
    public EntityModel<Contact> editOrganisation(@RequestBody ContactDTO newContact,
                                                 @PathVariable Long organisationId,
                                                 @PathVariable Long id) {

        newContact.testValidity(false);

        Organisation organ = organRepo.findById(organisationId)
                .orElseThrow(() -> new OrganisationNotFoundException(organisationId));

        requireMember(true, organ);

        Contact result = repository.findById(id)
                .map(org -> {
                    if (!organ.equals(org.getOrganisation())) {
                        throw new ContactNotFoundException(id);
                    }
                    newContact.updateEntity(org);
                    if (emailAlreadyExists(org)) throw new ContactExistsException();
                    return repository.save(org);
                })
                .orElseThrow(() -> new ContactNotFoundException(id));

        return contactToHateoas(organisationId, result);
    }

    //AUTH: ADMIN or (CUSTOMER if MANAGER)
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{organisationId}/contacts/{id}") //Handles DELETE requests
    public void deleteOrganisation(@PathVariable Long organisationId, @PathVariable Long id) {

        Organisation organ = organRepo.findById(organisationId)
                .orElseThrow(() -> new OrganisationNotFoundException(organisationId));

        requireMember(true, organ);

        Contact contact = repository.findById(id)
                .orElseThrow(() -> new ContactNotFoundException(id));

        if (!organ.equals(contact.getOrganisation())) {
            throw new ContactNotFoundException(id);
        }

        repository.deleteById(id);
    }

}
