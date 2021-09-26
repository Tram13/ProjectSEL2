package be.sel2.api.controllers;

import be.sel2.api.dtos.PermissionDTO;
import be.sel2.api.entities.FileMeta;
import be.sel2.api.entities.Organisation;
import be.sel2.api.entities.Permission;
import be.sel2.api.entities.UserInfo;
import be.sel2.api.exceptions.ForbiddenException;
import be.sel2.api.exceptions.not_found.FileNotFoundException;
import be.sel2.api.exceptions.not_found.OrganisationNotFoundException;
import be.sel2.api.exceptions.not_found.PermissionNotFoundException;
import be.sel2.api.models.RichCollectionModel;
import be.sel2.api.repositories.FileRepository;
import be.sel2.api.repositories.OrganisationRepository;
import be.sel2.api.repositories.PermissionRepository;
import be.sel2.api.repositories.UserRepository;
import be.sel2.api.users.UserInfoDetails;
import be.sel2.api.util.AllFieldSearchUtil;
import be.sel2.api.util.SecurityUtils;
import be.sel2.api.util.SortUtils;
import be.sel2.api.util.specifications.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * This class controls all requests to `/permission`
 */
@RestController
@RequestMapping("/permissions")
public class PermissionController {

    private final PermissionRepository repository;
    private final OrganisationRepository organRepo;
    private final UserRepository userRepo;
    private final FileRepository fileRepo;

    /**
     * Creates a new {@link PermissionController} with given repository.
     * This class controls all requests to `/permissions`
     *
     * @param repository Source {@link PermissionRepository} for this controller
     * @param organRepo  Source {@link OrganisationRepository} for this controller
     * @param userRepo   Source {@link UserRepository} for this controller
     * @param fileRepo   Source {@link FileRepository} for this controller
     */
    public PermissionController(PermissionRepository repository, OrganisationRepository organRepo, UserRepository userRepo, FileRepository fileRepo) {
        this.repository = repository;
        this.organRepo = organRepo;
        this.userRepo = userRepo;
        this.fileRepo = fileRepo;
    }

    private WebMvcLinkBuilder linkToSearchPermission() {
        return linkTo(PermissionController.class);
    }

    private EntityModel<Permission> permissionToHateoas(Permission perm) {
        return EntityModel.of(perm,
                linkTo(methodOn(PermissionController.class).one(perm.getId())).withSelfRel(),
                linkToSearchPermission().withRel("permissions")
        );
    }

    //AUTH: ADMIN, EMPLOYEE or (CUSTOMER if MEMBER)
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CUSTOMER"})
    @GetMapping //Handles GET requests
    public RichCollectionModel<Permission> searchPermission(
            @RequestParam Map<String, String> parameters,
            @RequestParam(defaultValue = "0") int skip,
            @RequestParam(defaultValue = "50") int limit) {

        SecurityContext context = SecurityContextHolder.getContext();

        List<String> entityFields = List.of("name", "description", "code",
                "link", "created", "lastUpdated");

        Pageable pageable = new OffsetBasedPageable(skip, limit,
                SortUtils.sortWithParameters(parameters, entityFields));
        DefaultSpecification<Permission> spec = new PartialMatchSpecification<>();
        spec.addWithKey(parameters, "code");

        Specification<Permission> specification = spec;

        if (SecurityUtils.hasRole(context, "ROLE_CUSTOMER")) {
            // The user is a customer
            UserInfoDetails userDetails = SecurityUtils.getDetails(context);

            UserInfo result = userRepo.findById(userDetails.getId())
                    .orElseThrow(() -> new ForbiddenException("This error should never be thrown"));

            Set<SearchCriteria> criteria = result.getOrganisations()
                    .stream().map(organ -> new SearchCriteria("organisation", organ))
                    .collect(Collectors.toSet());

            ContainsSpecification<Permission> relSpec = new ContainsSpecification<>();
            relSpec.addAll(criteria);

            specification = specification.and(relSpec);
        }

        specification = specification.and(AllFieldSearchUtil.getSpecification(Permission.class, parameters));

        Page<Permission> searchResults = repository.findAll(specification, pageable);

        List<EntityModel<Permission>> permissions = searchResults
                .stream().map(this::permissionToHateoas)
                .collect(Collectors.toList());

        return RichCollectionModel.of(permissions, searchResults,
                linkToSearchPermission().withSelfRel());
    }

    //AUTH: ADMIN or (CUSTOMER if MEMBER)
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER"})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping //Handles POST requests
    public EntityModel<Permission> newPermission(@RequestBody PermissionDTO createPermission) {
        createPermission.testValidity(true);

        Permission newPermission = createPermission.getEntity();

        Long organisationId = newPermission.getOrganisation().getId();

        Long proofId = newPermission.getProof().getId();

        Organisation organ = organRepo.findById(organisationId)
                .orElseThrow(() -> new OrganisationNotFoundException(organisationId));

        FileMeta proof = fileRepo.findById(proofId)
                .orElseThrow(() -> new FileNotFoundException(proofId));

        SecurityContext context = SecurityContextHolder.getContext();

        if (!SecurityUtils.isMember(context, false, organ)) {
            throw new ForbiddenException("Unauthorized post in name of other organisation");
        }

        newPermission.setOrganisation(organ);
        newPermission.setProof(proof);

        return permissionToHateoas(repository.save(newPermission));
    }

    // Single item
    //AUTH: ADMIN, EMPLOYEE or (CUSTOMER if MEMBER)
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CUSTOMER"})
    @GetMapping("/{id}") //Handles GET requests
    public EntityModel<Permission> one(@PathVariable Long id) {

        Permission perm = repository.findById(id)
                .orElseThrow(() -> new PermissionNotFoundException(id));

        SecurityContext context = SecurityContextHolder.getContext();

        if (!SecurityUtils.isMember(context, false, perm.getOrganisation())) {
            throw new ForbiddenException();
        }

        return permissionToHateoas(perm);
    }

    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER"})
    //AUTH: ADMIN or (CUSTOMER if MEMBER)
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}") //Handles PATCH requests
    public EntityModel<Permission> editPermission(@RequestBody PermissionDTO newPermission, @PathVariable Long id) {

        newPermission.testValidity(false);

        Permission res = repository.findById(id)
                .orElseThrow(() -> new PermissionNotFoundException(id));

        newPermission.updateEntity(res);

        if (newPermission.getProof() != null) {
            Long proofId = newPermission.getProof();

            FileMeta proof = fileRepo.findById(proofId)
                    .orElseThrow(() -> new FileNotFoundException(proofId));

            res.setProof(proof);
        }

        SecurityContext context = SecurityContextHolder.getContext();

        if (!SecurityUtils.isMember(context, true, res.getOrganisation())) {
            throw new ForbiddenException();
        }

        return permissionToHateoas(repository.save(res));
    }

    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER"})
    //AUTH: ADMIN or (CUSTOMER if MEMBER)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}") //Handles DELETE requests
    public void deletePermission(@PathVariable Long id) {
        Permission perm = repository.findById(id)
                .orElseThrow(() -> new PermissionNotFoundException(id));

        SecurityContext context = SecurityContextHolder.getContext();

        if (!SecurityUtils.isMember(context, true, perm.getOrganisation())) {
            throw new ForbiddenException();
        }

        repository.deleteById(id);
    }
}
