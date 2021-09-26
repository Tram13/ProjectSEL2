package be.sel2.api.controllers.statistics;

import be.sel2.api.controllers.RootController;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/statistics")
public class StatisticsRootController {

    protected static final String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";
    protected static final String ROLE_ADMIN = "ROLE_ADMIN";

    @Secured({ROLE_ADMIN, ROLE_EMPLOYEE})
    @GetMapping
    public EntityModel<Object> greeting() {
        Map<String, String> map = new HashMap<>();

        map.put("message", "Statistics root reached");
        return EntityModel.of(map,
                linkTo(methodOn(StatisticsRootController.class).greeting()).withSelfRel(),
                linkTo(methodOn(RootController.class).greeting()).withRel("root"),
                linkTo(UserStatisticsController.class).withRel("users"),
                linkTo(ServiceStatisticsController.class).withRel("services"),
                linkTo(ProposalStatisticsController.class).withRel("proposals"),
                linkTo(PermissionStatisticsController.class).withRel("permissions"),
                linkTo(PackageStatisticsController.class).withRel("packages"),
                linkTo(OrganisationStatisticsController.class).withRel("organisations"),
                linkTo(FileStatisticsController.class).withRel("files"),
                linkTo(CertificateStatisticsController.class).withRel("certificates")
        );
    }
}
