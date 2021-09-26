package be.sel2.api.controllers;

import be.sel2.api.dtos.MemberDTO;
import be.sel2.api.entities.Member;
import be.sel2.api.entities.Organisation;
import be.sel2.api.entities.UserInfo;
import be.sel2.api.exceptions.ForbiddenException;
import be.sel2.api.exceptions.conflict.UserInOrganisationException;
import be.sel2.api.exceptions.not_found.MemberNotFoundException;
import be.sel2.api.exceptions.not_found.OrganisationNotFoundException;
import be.sel2.api.exceptions.not_found.UserNotFoundException;
import be.sel2.api.models.RichCollectionModel;
import be.sel2.api.repositories.MemberRepository;
import be.sel2.api.repositories.OrganisationRepository;
import be.sel2.api.repositories.UserRepository;
import be.sel2.api.util.SecurityUtils;
import be.sel2.api.util.specifications.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * This class controls all requests to `/organisations/{organisationId}/members`
 */
@RestController
@RequestMapping("/organisations")
public class MemberController {

    private final OrganisationRepository organRepo;
    private final UserRepository userRepo;
    private final MemberRepository memberRepo;

    public MemberController(OrganisationRepository organRepo, UserRepository userRepo, MemberRepository memberRepo) {
        this.organRepo = organRepo;
        this.userRepo = userRepo;
        this.memberRepo = memberRepo;
    }

    private Link linkToSearchMembers(Long organisationId) {
        return Link.of(linkTo(MemberController.class).toUri() + "/" +
                organisationId.toString() +
                "/members");
    }

    /**
     * @param member that needs to be mapped
     * @return member mapped to an {@link EntityModel}
     */
    private EntityModel<Member> memberToHateoas(Member member, Long userId) {
        return EntityModel.of(member,
                linkTo(methodOn(MemberController.class).one(member.getOrganisation().getId(), userId)).withSelfRel(),
                linkToSearchMembers(member.getOrganisation().getId()).withRel("members")
        );
    }

    private Specification<Member> addUserSpecification(Specification<Member> specification, Map<String, String> parameters) {
        if (parameters.containsKey("firstName") || parameters.containsKey("lastName") || parameters.containsKey("email")) {
            DefaultSpecification<UserInfo> userSpec = new PartialMatchSpecification<>();

            userSpec.addWithKey(parameters, "firstName");
            userSpec.addWithKey(parameters, "lastName");
            userSpec.addWithKey(parameters, "email");

            List<UserInfo> userList = userRepo.findAll(userSpec);

            ContainsSpecification<Member> spec = new ContainsSpecification<>();
            spec.addAll(userList.stream()
                    .map(u -> new SearchCriteria("user", u)).collect(Collectors.toList()));

            return specification.and(spec);
        }
        return specification;
    }

    //AUTH: ADMIN, EMPLOYEE or (CUSTOMER if MEMBER)
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CUSTOMER"})
    @GetMapping("/{id}/members") //Handles GET requests
    public RichCollectionModel<Member> searchMembers(
            @PathVariable Long id,
            @RequestParam Map<String, String> parameters,
            @RequestParam Optional<Member.MemberRole> role,
            @RequestParam Optional<Boolean> accepted,
            @RequestParam(defaultValue = "0") int skip,
            @RequestParam(defaultValue = "50") int limit) {

        Organisation organ = organRepo.findById(id)
                .orElseThrow(() -> new OrganisationNotFoundException(id));

        SecurityContext context = SecurityContextHolder.getContext();

        if (!SecurityUtils.isMember(context, false, organ)) {
            throw new ForbiddenException("You need to be a member of the organisation to see this information");
        }

        Pageable pageable = new OffsetBasedPageable(skip, limit, Sort.unsorted()); // There are no fields to sort by

        DefaultSpecification<Member> specification = new PartialMatchSpecification<>();

        specification.add(new SearchCriteria("organisation", organ));

        accepted.ifPresent(s -> specification.add(new SearchCriteria("accepted", s)));

        role.ifPresent(s -> specification.add(new SearchCriteria("role", s)));

        Page<Member> searchResults = memberRepo.findAll(
                addUserSpecification(specification, parameters),
                pageable);

        List<EntityModel<Member>> users = searchResults
                .stream().map(member -> memberToHateoas(member, member.getUser().getId())).collect(Collectors.toList());

        return RichCollectionModel.of(users, searchResults, linkToSearchMembers(id).withSelfRel());
    }


    //AUTH: ADMIN or (CUSTOMER if MANAGER)
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER"})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{organisationId}/members")
    public EntityModel<Member> addMemberToOrganisation(
            @PathVariable Long organisationId,
            @RequestBody MemberDTO member) {

        member.testValidity(true);

        Member entity = member.getEntity();

        Organisation organisation = organRepo.findById(organisationId)
                .orElseThrow(() -> new OrganisationNotFoundException(organisationId));

        SecurityContext context = SecurityContextHolder.getContext();

        if (!SecurityUtils.isMember(context, true, organisation)) {
            throw new ForbiddenException("Unauthorized POST, you need to be a manager to do this");
        }

        DefaultSpecification<UserInfo> specification = new DefaultSpecification<>();
        UserNotFoundException error;

        // only admin can do this via ID, so he is directly accepted
        if (entity.getUser().getId() != null) {
            specification.add(new SearchCriteria("id", entity.getUser().getId()));
            entity.setAccepted(true);
            error = new UserNotFoundException(entity.getUser().getId());
        } else {
            specification.add(new SearchCriteria("email", entity.getUser().getEmail()));
            error = new UserNotFoundException(entity.getUser().getEmail());
        }

        UserInfo userInfo = userRepo.findOne(specification)
                .orElseThrow(() -> error);

        entity.setUser(userInfo);

        if (userInfo.getOrganisations().contains(organisation)) {
            throw new UserInOrganisationException();
        }

        entity.setOrganisation(organisation);

        Member res = memberRepo.save(entity);
        return memberToHateoas(res, member.getUserId());
    }

    //AUTH: ADMIN, EMPLOYEE or (CUSTOMER if MEMBER)
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CUSTOMER"})
    @GetMapping("/{organId}/members/{userId}")
    public EntityModel<Member> one(
            @PathVariable Long organId,
            @PathVariable Long userId) {

        Member member = attemptGetMember(organId, userId, false);

        return memberToHateoas(member, userId);
    }

    //AUTH: ADMIN or (CUSTOMER if MANAGER)
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{organId}/members/{userId}")
    public void removeMemberFromOrganisation(
            @PathVariable Long organId,
            @PathVariable Long userId) {

        Member member = attemptGetMember(organId, userId, false);

        memberRepo.delete(member);
    }

    //AUTH: ADMIN or (CUSTOMER if MANAGER)
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER"})
    @PatchMapping("/{organId}/members/{userId}")
    public EntityModel<Member> updateMember(
            @PathVariable Long organId,
            @PathVariable Long userId,
            @RequestBody MemberDTO memberDto) {

        memberDto.testValidity(false);
        SecurityContext context = SecurityContextHolder.getContext();

        Member member = attemptGetMember(organId, userId, false);
        if (!SecurityUtils.hasRole(context, "ROLE_ADMIN")) {
            MemberDTO safeMemberDto = new MemberDTO();

            if (SecurityUtils.getDetails(context).getId().equals(member.getUser().getId())) {
                // The user can change the accept field
                safeMemberDto.setAccepted(memberDto.getAccepted());
            }
            if (member.getRole() == Member.MemberRole.MANAGER) {
                // The user can change the role field
                safeMemberDto.setRole(memberDto.getRole());
            }
            memberDto = safeMemberDto;
        }

        memberDto.updateEntity(member);

        return memberToHateoas(memberRepo.save(member), userId);
    }

    private Member attemptGetMember(Long organId, Long userId, boolean requireManager)
            throws OrganisationNotFoundException, MemberNotFoundException, ForbiddenException {

        Organisation organ = organRepo.findById(organId)
                .orElseThrow(() -> new OrganisationNotFoundException(organId));

        SecurityContext context = SecurityContextHolder.getContext();

        if (!SecurityUtils.isMember(context, requireManager, organ)) {
            throw new ForbiddenException("Unauthorized operation");
        }

        DefaultSpecification<Member> specification = new DefaultSpecification<>();

        specification.add(new SearchCriteria("organisation", organId));
        specification.add(new SearchCriteria("user", userId));

        Member member = memberRepo.findOne(specification).orElseThrow(() -> new MemberNotFoundException(userId, organId));

        if (!member.getOrganisation().getId().equals(organId)) {
            // MemberId does not point to a member of the organisation
            throw new MemberNotFoundException(userId, organId);
        }
        return member;
    }
}
