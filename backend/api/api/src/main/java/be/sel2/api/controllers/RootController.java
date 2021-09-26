package be.sel2.api.controllers;

import be.sel2.api.controllers.statistics.StatisticsRootController;
import be.sel2.api.entities.Member;
import be.sel2.api.entities.Proposal;
import be.sel2.api.entities.Service;
import be.sel2.api.entities.UserInfo;
import be.sel2.api.entities.relations.ContactProposal;
import be.sel2.api.models.EnumModel;
import be.sel2.api.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * This makes sure requests to `/` are met with a response
 */
@RestController
public class RootController {

    @Value("${application-version}")
    private String applicationVersion;

    @Value("${eid.discovery-url}")
    private String eidDiscoveryUrl;

    //AUTH: ALLOW ALL
    @GetMapping("/")
    public EntityModel<Object> greeting() {
        Map<String, String> map = new HashMap<>();

        map.put("message", "API reached");
        map.put("version", applicationVersion);
        return EntityModel.of(map,
                linkTo(methodOn(RootController.class).greeting()).withSelfRel(),
                linkTo(AuthenticationController.class).withRel("auth"),
                linkTo(CertificateController.class).withRel("certificates"),
                linkTo(methodOn(RootController.class).getConfig()).withRel("config"),
                linkTo(OrganisationController.class).withRel("organisations"),
                linkTo(PackageController.class).withRel("packages"),
                linkTo(PermissionController.class).withRel("permissions"),
                linkTo(ProposalController.class).withRel("proposals"),
                linkTo(ServiceController.class).withRel("services"),
                linkTo(UserController.class).withRel("users"),
                linkTo(FileController.class).withRel("files"),
                linkTo(StatisticsRootController.class).withRel("statistics"),
                Link.of(eidDiscoveryUrl).withRel("eid-endpoint")
        );
    }

    //AUTH: ALLOW ALL
    @GetMapping("/config")
    public Map<String, EnumModel> getConfig() {
        return Map.of(
                "userRoles", new EnumModel(UserInfo.Userrole.values()),
                "memberRoles", new EnumModel(Member.MemberRole.values()),
                "proposalStatuses", new EnumModel(Proposal.ProposalStatus.values()),
                "proposalRoles", new EnumModel(ContactProposal.Contactrole.values()),
                "deliveryMethod", new EnumModel(Arrays.stream(Service.DeliveryMethod.values())
                        .map(Enum::name).collect(Collectors.toList())),
                "numberOfRequests", new EnumModel(Arrays.stream(Proposal.NumberOfRequests.values())
                        .map(Object::toString).collect(Collectors.toList())),
                "tokenTypes", new EnumModel(new String[]{"refreshToken", "sessionToken"})
        );
    }

    //AUTH: Logged in
    @GetMapping("/loginData")
    public RedirectView getLoginData() {
        SecurityContext context = SecurityContextHolder.getContext();
        Long id = SecurityUtils.getDetails(context).getId();
        return new RedirectView(linkTo(methodOn(UserController.class).one(id)).toString());
    }
}
