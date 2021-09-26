package be.sel2.api.controllers;

import be.sel2.api.dtos.OrganisationDTO;
import be.sel2.api.entities.Member;
import be.sel2.api.entities.Organisation;
import be.sel2.api.entities.UserInfo;
import be.sel2.api.exceptions.ForbiddenException;
import be.sel2.api.exceptions.conflict.OrganisationExistsException;
import be.sel2.api.exceptions.not_found.OrganisationNotFoundException;
import be.sel2.api.models.RichCollectionModel;
import be.sel2.api.repositories.MemberRepository;
import be.sel2.api.repositories.OrganisationRepository;
import be.sel2.api.util.AllFieldSearchUtil;
import be.sel2.api.util.SecurityUtils;
import be.sel2.api.util.SortUtils;
import be.sel2.api.util.specifications.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * This class controls all requests to `/organisations`
 */
@RestController
@RequestMapping("/organisations")
public class OrganisationController {

    private final OrganisationRepository repository;
    private final MemberRepository memberRepo;

    /**
     * Creates a new {@link OrganisationController} with given repository.
     * This class controls all requests to `/organisations`
     *
     * @param repository Source {@link OrganisationRepository} for this controller
     * @param memberRepo Source {@link MemberRepository} for this controller
     */
    public OrganisationController(OrganisationRepository repository, MemberRepository memberRepo) {
        this.repository = repository;
        this.memberRepo = memberRepo;
    }


    private WebMvcLinkBuilder linkToSearchOrganisation() {
        return linkTo(OrganisationController.class);
    }

    /**
     * @param organisation that needs to be mapped
     * @return organisation mapped to an {@link EntityModel}
     */
    private EntityModel<Organisation> organisationToHateoas(Organisation organisation) {
        WebMvcLinkBuilder selfLink = linkTo(methodOn(OrganisationController.class).one(organisation.getId()));
        return EntityModel.of(organisation,
                selfLink.withSelfRel(),
                linkToSearchOrganisation().withRel("organisations"),
                Link.of(selfLink.toUri() + "/members").withRel("members")
        );
    }

    /**
     * @param newOrganisation new organisation that needs to be checked if it already exists
     * @return if there is already an organisation with the same KBO, OVO and NIS combination as the new organisation
     * in the database, then it returns true
     * otherwise false
     */
    private boolean organisationAlreadyExists(Organisation newOrganisation) {
        DefaultSpecification<Organisation> specification = new DefaultSpecification<>();
        specification.add(new SearchCriteria("kboNumber", newOrganisation.getKboNumber()));
        specification.add(new SearchCriteria("ovoCode", newOrganisation.getOvoCode()));
        specification.add(new SearchCriteria("nisNumber", newOrganisation.getNisNumber()));
        return repository.findOne(specification)
                .filter(organisation -> !organisation.getId().equals(newOrganisation.getId()))
                .isPresent();
    }

    //AUTH: ADMIN, EMPLOYEE
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE"})
    @GetMapping //Handles GET requests
    public RichCollectionModel<Organisation> searchOrganisation(
            @RequestParam Map<String, String> parameters,
            @RequestParam Optional<Boolean> approved,
            @RequestParam Optional<Long> notMember,
            @RequestParam(defaultValue = "0") int skip,
            @RequestParam(defaultValue = "50") int limit) {

        List<String> entityFields = List.of("organisationName", "kboNumber", "ovoCode", "nisNumber",
                "serviceProvider", "created", "lastUpdated");

        if (parameters.containsKey("name")) {
            parameters.put("organisationName", parameters.get("name"));
        }

        Pageable pageable = new OffsetBasedPageable(skip, limit,
                SortUtils.sortWithParameters(parameters, entityFields));
        DefaultSpecification<Organisation> specification = new PartialMatchSpecification<>();

        for (String key : entityFields) {
            specification.addWithKey(parameters, key);
        }

        approved.ifPresent(s -> specification.add(new SearchCriteria("approved", s)));

        Page<Organisation> searchResult;

        Specification<Organisation> resultingSpec = specification
                .and(AllFieldSearchUtil.getSpecification(Organisation.class, parameters));

        if (notMember.isPresent()) {
            DefaultSpecification<Member> memberSpec = new DefaultSpecification<>();
            memberSpec.add(new SearchCriteria("user", notMember.get()));
            List<Member> memberList = memberRepo.findAll(memberSpec);
            Set<Long> organisationIds = memberList
                    .stream().map(m -> m.getOrganisation().getId())
                    .collect(Collectors.toSet());
            DefaultSpecification<Organisation> spec = new ContainsSpecification<>();
            for (Long orgId : organisationIds) {
                spec.add(new SearchCriteria("id", orgId));
            }

            //Get all organisations following previous filters and notMember
            searchResult = repository.findAll(resultingSpec.and(Specification.not(spec)), pageable);
        } else {
            //Get all organisations following filters
            searchResult = repository.findAll(resultingSpec, pageable);
        }

        List<EntityModel<Organisation>> organisations = searchResult
                .stream().map(this::organisationToHateoas)
                .collect(Collectors.toList());

        return RichCollectionModel.of(organisations, searchResult,
                linkToSearchOrganisation().withSelfRel());
    }

    //AUTH: ADMIN, EMPLOYEE, CUSTOMER
    //      ON CUSTOMER: becomes MANAGER
    @Secured({"ROLE_CUSTOMER", "ROLE_ADMIN", "ROLE_EMPLOYEE"})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping //Handles POST requests
    public EntityModel<Organisation> newOrganisation(@RequestBody OrganisationDTO createOrganisation) {

        createOrganisation.testValidity(true);
        Organisation newOrganisation = createOrganisation.getEntity();

        if (organisationAlreadyExists(newOrganisation)) {
            throw new OrganisationExistsException();
        }

        SecurityContext context = SecurityContextHolder.getContext();
        boolean isCustomer = SecurityUtils.hasRole(context, "ROLE_CUSTOMER");

        newOrganisation.setApproved(!isCustomer);

        Organisation result = repository.save(newOrganisation);

        if (isCustomer) {
            // The user is a customer
            Long userId = SecurityUtils.getDetails(context).getId();
            UserInfo user = new UserInfo();
            user.setId(userId);

            // Make said user the manager to this organisation
            Member memberShip = new Member(result, user, Member.MemberRole.MANAGER, true);

            memberRepo.save(memberShip);
        }

        return organisationToHateoas(result);
    }

    //AUTH: ADMIN, EMPLOYEE or (CUSTOMER if MEMBER)
    // Single item
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CUSTOMER"})
    @GetMapping("/{id}") //Handles GET requests
    public EntityModel<Organisation> one(@PathVariable Long id) {

        Organisation organ = repository.findById(id)
                .orElseThrow(() -> new OrganisationNotFoundException(id));

        SecurityContext context = SecurityContextHolder.getContext();

        if (!SecurityUtils.isMember(context, false, organ)) {
            throw new ForbiddenException("Unauthorized, only members get to see this information");
        }

        return organisationToHateoas(organ);
    }

    //AUTH: ADMIN, EMPLOYEE or (CUSTOMER if MANAGER)
    //      ON EMPLOYEE: confirmation & managers ONLY
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CUSTOMER"})
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}") //Handles PATCH requests
    public EntityModel<Organisation> editOrganisation(@RequestBody OrganisationDTO newOrganisation, @PathVariable Long id) {
        newOrganisation.testValidity(false);

        Organisation organ = repository.findById(id)
                .orElseThrow(() -> new OrganisationNotFoundException(id));

        SecurityContext context = SecurityContextHolder.getContext();

        if (!SecurityUtils.isMember(context, true, organ)) {
            throw new ForbiddenException("Unauthorized operation, you need to be at least a manager");
        }

        if (SecurityUtils.hasRole(context, "ROLE_EMPLOYEE")) {
            OrganisationDTO secureNewOrganisation = new OrganisationDTO();

            secureNewOrganisation.setApproved(newOrganisation.getApproved());

            newOrganisation = secureNewOrganisation;
        } else if (SecurityUtils.hasRole(context, "ROLE_CUSTOMER")) {
            newOrganisation.setApproved(null);
        }

        newOrganisation.updateEntity(organ);
        if (organisationAlreadyExists(organ)) {
            throw new OrganisationExistsException();
        }

        return organisationToHateoas(repository.save(organ));
    }

    //AUTH: ADMIN or (CUSTOMER if MANAGER)
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}") //Handles DELETE requests
    public void deleteOrganisation(@PathVariable Long id) {
        Organisation org = repository.findById(id)
                .orElseThrow(() -> new OrganisationNotFoundException(id));

        SecurityContext context = SecurityContextHolder.getContext();

        if (!SecurityUtils.isMember(context, true, org)) {
            throw new ForbiddenException("Unauthorized delete, you need to be a manager to delete this");
        }

        repository.deleteById(id);
    }
}
