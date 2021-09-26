package be.sel2.api.models;

import be.sel2.api.controllers.OrganisationController;
import be.sel2.api.entities.Member;
import be.sel2.api.entities.Organisation;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Model used to easily return member elements, along with their organisation
 */
@Getter
@Setter
@Relation(collectionRelation = "organisationList") //Setup correct name in CollectionModel JSON
public class MemberModel {

    private Long id;
    private Member.MemberRole role;
    private Boolean accepted;

    private EntityModel<Organisation> organisation;

    public MemberModel(Long id, Member.MemberRole role, Boolean accepted) {
        this.id = id;
        this.role = role;
        this.accepted = accepted;
    }

    private static List<Link> getLinks(Organisation organisation) {
        return List.of(
                linkTo(methodOn(OrganisationController.class).one(organisation.getId())).withSelfRel(),
                linkTo(OrganisationController.class).withRel("organisations"),
                Link.of(linkTo(methodOn(OrganisationController.class).one(organisation.getId())).toUri() + "/members")
                        .withRel("members")
        );
    }

    public static MemberModel from(Member member) {
        MemberModel res = new MemberModel(member.getId(), member.getRole(), member.getAccepted());
        res.setOrganisation(EntityModel.of(member.getOrganisation(), getLinks(member.getOrganisation())));

        return res;
    }
}
