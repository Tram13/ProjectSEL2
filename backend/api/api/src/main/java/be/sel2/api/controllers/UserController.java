package be.sel2.api.controllers;

import be.sel2.api.dtos.UserInfoDTO;
import be.sel2.api.entities.Member;
import be.sel2.api.entities.Organisation;
import be.sel2.api.entities.UserInfo;
import be.sel2.api.exceptions.conflict.UserExistsException;
import be.sel2.api.exceptions.not_found.UserNotFoundException;
import be.sel2.api.models.MemberModel;
import be.sel2.api.models.RichCollectionModel;
import be.sel2.api.repositories.MemberRepository;
import be.sel2.api.repositories.OrganisationRepository;
import be.sel2.api.repositories.UserRepository;
import be.sel2.api.util.AllFieldSearchUtil;
import be.sel2.api.util.SortUtils;
import be.sel2.api.util.specifications.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * This class controls all requests to `/users` and `/users/{userid}/organisations`
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository repository;
    private final OrganisationRepository organRepo;
    private final MemberRepository memberRepo;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new {@link UserController} with given repository.
     * This class controls all requests to `/users` and `/users/{userid}/organisations`
     *
     * @param repository      Source {@link UserRepository} for this controller
     * @param organRepo       Source {@link OrganisationRepository} for this controller
     * @param memberRepo      Source {@link MemberRepository} for this controller
     * @param passwordEncoder The {@link PasswordEncoder} used to encode our passwords
     */
    public UserController(UserRepository repository, OrganisationRepository organRepo, MemberRepository memberRepo, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.organRepo = organRepo;
        this.memberRepo = memberRepo;
        this.passwordEncoder = passwordEncoder;
    }

    private WebMvcLinkBuilder linkToSearchUser() {
        return linkTo(UserController.class);
    }

    /**
     * @param user that needs to be mapped
     * @return user mapped to an {@link EntityModel}
     */
    private EntityModel<UserInfo> userToHateoas(UserInfo user) {
        return EntityModel.of(user,
                linkTo(methodOn(UserController.class).one(user.getId())).withSelfRel(),
                linkToSearchUser().withRel("users"),
                Link.of(linkTo(UserController.class).toUri() + "/" + user.getId() + "/organisations").withRel("organisations")
        );
    }

    /**
     * @param member that needs to be mapped
     * @return member mapped to an {@link EntityModel}
     */
    private EntityModel<MemberModel> memberToHateoas(MemberModel member) {
        return EntityModel.of(member,
                linkTo(methodOn(MemberController.class).one(Objects.requireNonNull(member.getOrganisation().getContent()).getId(),
                        member.getId())).withSelfRel()
        );
    }

    /**
     * @param user contains email that will be checked if it exists
     * @return true if the email already exists by a user with another id otherwise false
     */
    private boolean emailAlreadyExists(UserInfo user) {
        DefaultSpecification<UserInfo> specification = new DefaultSpecification<>();
        specification.add(new SearchCriteria("email", user.getEmail()));
        Optional<UserInfo> sameEmail = repository.findOne(specification);
        return sameEmail.filter(userInfo -> !userInfo.getId().equals(user.getId())).isPresent();
    }

    //AUTH: ADMIN, EMPLOYEE
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE"})
    @GetMapping //Handles GET requests
    public RichCollectionModel<UserInfo> searchUsers(
            @RequestParam Map<String, String> parameters,
            @RequestParam Optional<String> role,
            @RequestParam(defaultValue = "0") int skip,
            @RequestParam(defaultValue = "50") int limit) {

        List<String> entityFields = List.of("firstName", "lastName", "email", "created", "lastUpdated");

        Pageable pageable = new OffsetBasedPageable(skip, limit,
                SortUtils.sortWithParameters(parameters, entityFields));

        DefaultSpecification<UserInfo> specification = new PartialMatchSpecification<>();

        for (String key : entityFields) {
            specification.addWithKey(parameters, key);
        }

        role.ifPresent(s -> specification.add(
                new SearchCriteria("role", UserInfo.Userrole.fromString(s))));
        Specification<UserInfo> resultingSpec;

        if (parameters.containsKey("organisation")) {
            DefaultSpecification<Organisation> spec = new PartialMatchSpecification<>();
            spec.addWithKey(parameters, "organisation", "organisationName");
            List<Organisation> organisations = organRepo.findAll(spec);
            List<Member> memberships = organisations
                    .stream().flatMap(o -> o.getMembers().stream())
                    .collect(Collectors.toList());

            AnyIsInListSpecification<UserInfo> matchOrganisationSpec = new AnyIsInListSpecification<>();
            matchOrganisationSpec.add(new SearchCriteria("memberships", memberships));

            resultingSpec = specification.and(matchOrganisationSpec);
        } else {
            resultingSpec = specification;
        }

        resultingSpec = resultingSpec.and(AllFieldSearchUtil.getSpecification(UserInfo.class, parameters));

        Page<UserInfo> searchResults = repository.findAll(resultingSpec, pageable);

        List<EntityModel<UserInfo>> users = searchResults
                .stream().map(this::userToHateoas).collect(Collectors.toList());

        return RichCollectionModel.of(users, searchResults, linkToSearchUser().withSelfRel());
    }

    // method to register a new user
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<UserInfo> registerUser(@RequestBody UserInfoDTO inputUser) {
        return newUser(inputUser);
    }

    //AUTH: ADMIN
    @Secured("ROLE_ADMIN")
    @PostMapping // Handles POST requests
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<UserInfo> newUser(@RequestBody UserInfoDTO inputUser) {
        inputUser.testValidity(true);
        UserInfo newUser = inputUser.getEntity();
        if (emailAlreadyExists(newUser)) {
            throw new UserExistsException();
        }

        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        return userToHateoas(repository.save(newUser));
    }

    //AUTH: ADMIN, EMPLOYEE or (CUSTOMER if own ID)
    // Single item
    @PreAuthorize("#id == authentication.principal.id or hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @GetMapping("/{id}") // Handles GET requests
    public EntityModel<UserInfo> one(@PathVariable Long id) {
        UserInfo user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return userToHateoas(user);
    }

    //AUTH: ADMIN, EMPLOYEE or (CUSTOMER if own ID)
    //      PATCH ROLE: ADMIN ONLY
    @PreAuthorize("(#id == authentication.principal.id and #newUser.getRole() == null) or hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}") // Handles PATCH requests
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<UserInfo> editUser(@RequestBody UserInfoDTO newUser, @PathVariable Long id) {

        newUser.testValidity(false);

        if (newUser.getPassword() != null) {
            newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        }

        UserInfo res = repository.findById(id)
                .map(user -> {
                    newUser.updateEntity(user);
                    if (emailAlreadyExists(user)) throw new UserExistsException();
                    return repository.save(user);
                })
                .orElseThrow(() -> new UserNotFoundException(id));

        return userToHateoas(res);
    }

    //AUTH: ADMIN, EMPLOYEE or (CUSTOMER if own ID)
    @PreAuthorize("#id == authentication.principal.id or hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}") // Handles DELETE requests
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        if (repository.findById(id).isEmpty()) {
            throw new UserNotFoundException(id);
        }
        repository.deleteById(id);
    }

    //AUTH: ADMIN, EMPLOYEE or (CUSTOMER if own ID)
    @PreAuthorize("#id == authentication.principal.id or hasRole('ROLE_ADMIN') or hasRole('ROLE_EMPLOYEE')")
    @GetMapping("/{id}/organisations")
    public RichCollectionModel<MemberModel> getUserOrganisations(
            @PathVariable Long id,
            @RequestParam Map<String, String> parameters,
            @RequestParam Optional<Boolean> accepted,
            @RequestParam(defaultValue = "0") int skip,
            @RequestParam(defaultValue = "50") int limit) {

        UserInfo user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        Sort sortObject;

        if (parameters.containsKey("sortBy") && parameters.get("sortBy").equals("organisationName")) {
            Sort.Direction direction = Sort.DEFAULT_DIRECTION;
            if (parameters.containsKey("sortDirection") && "desc".equals(parameters.get("sortDirection"))) {
                direction = Sort.Direction.DESC;
            }
            sortObject = Sort.by(direction, "organisation.organisationName");
        } else {
            sortObject = SortUtils.sortWithParameters(parameters, List.of("accepted", "role"));
        }

        Pageable pageable = new OffsetBasedPageable(skip, limit, sortObject);

        DefaultSpecification<Member> specification = new PartialMatchSpecification<>();

        specification.add(new SearchCriteria("user", id));
        accepted.ifPresent(a -> specification.add(new SearchCriteria("accepted", a)));
        if (parameters.containsKey("role")) {
            Member.MemberRole role = Member.MemberRole.fromString(parameters.get("role"));
            specification.add(new SearchCriteria("role", role));
        }

        Specification<Member> resSpec = specification;

        if (parameters.containsKey("organisationName")) {
            DefaultSpecification<Organisation> orgSpec = new PartialMatchSpecification<>();
            orgSpec.addWithKey(parameters, "organisationName");

            List<Organisation> organisations = organRepo.findAll(orgSpec);

            ContainsSpecification<Member> subSpec = new ContainsSpecification<>();
            subSpec.addAll(organisations.stream()
                    .map(o -> new SearchCriteria("organisation", o))
                    .collect(Collectors.toList()));

            resSpec = resSpec.and(subSpec);
        }

        Page<Member> memberList = memberRepo.findAll(resSpec, pageable);

        List<EntityModel<MemberModel>> memberModels =
                memberList.stream()
                        .map(MemberModel::from)
                        .map(this::memberToHateoas)
                        .collect(Collectors.toList());

        return RichCollectionModel.of(memberModels, memberList.getTotalElements(), memberModels.size(),
                Link.of(linkTo(UserController.class).toUri() + "/" + id + "/organisations").withSelfRel());
    }

}
