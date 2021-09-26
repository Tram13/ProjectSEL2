package be.sel2.api.controllers.statistics;

import be.sel2.api.models.StatisticsModel;
import be.sel2.api.repositories.MemberRepository;
import be.sel2.api.repositories.OrganisationRepository;
import be.sel2.api.repositories.PermissionRepository;
import be.sel2.api.repositories.ProposalRepository;
import be.sel2.api.repositories.archive.ArchivedMemberRepository;
import be.sel2.api.repositories.archive.ArchivedOrganisationRepository;
import be.sel2.api.repositories.archive.ArchivedPermissionRepository;
import be.sel2.api.repositories.archive.ArchivedProposalRepository;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/statistics/organisations")
public class OrganisationStatisticsController extends AbstractStatisticsController {

    private final OrganisationRepository organisationRepo;
    private final ArchivedOrganisationRepository archivedOrganisationRepo;

    private final ProposalRepository propRepo;
    private final ArchivedProposalRepository archPropRepo;

    private final MemberRepository memberRepo;
    private final ArchivedMemberRepository archMemberRepo;

    private final PermissionRepository permRepo;
    private final ArchivedPermissionRepository archPermRepo;

    public OrganisationStatisticsController(
            OrganisationRepository organisationRepo, ArchivedOrganisationRepository archivedOrganisationRepo,
            ProposalRepository propRepo, ArchivedProposalRepository archPropRepo,
            MemberRepository memberRepo, ArchivedMemberRepository archMemberRepo,
            PermissionRepository permRepo, ArchivedPermissionRepository archPermRepo) {
        this.organisationRepo = organisationRepo;
        this.archivedOrganisationRepo = archivedOrganisationRepo;
        this.propRepo = propRepo;
        this.archPropRepo = archPropRepo;
        this.memberRepo = memberRepo;
        this.archMemberRepo = archMemberRepo;
        this.permRepo = permRepo;
        this.archPermRepo = archPermRepo;
    }

    @Secured({ROLE_ADMIN, ROLE_EMPLOYEE})
    @GetMapping
    public List<StatisticsModel> getOrganisationStatistics(
            @RequestParam Optional<Boolean> approved
    ) {
        if (approved.isPresent()) {
            return getStatistics(organisationRepo, "approved", approved.get(),
                    archivedOrganisationRepo.findAllByApproved(approved.get()));
        }
        return getStatistics(organisationRepo, archivedOrganisationRepo);
    }

    public List<StatisticsModel> getProposalStatistics(Long organisationId) {
        return getStatistics(propRepo, "organisation",
                organisationId, archPropRepo.findAllByOrganisationId(organisationId));
    }

    public List<StatisticsModel> getMemberStatistics(Long organisationId) {
        return getStatistics(memberRepo, "organisation",
                organisationId, archMemberRepo.findAllByOrganisation(organisationId));
    }

    public List<StatisticsModel> getPermissionStatistics(Long organisationId) {
        return getStatistics(permRepo, "organisation",
                organisationId, archPermRepo.findAllByOrganisation(organisationId));
    }

    @Secured({ROLE_ADMIN, ROLE_EMPLOYEE})
    @GetMapping("/{organisationId}")
    public Map<String, List<StatisticsModel>> getOrganisationStatistics(
            @PathVariable Long organisationId) {
        Map<String, List<StatisticsModel>> result = new HashMap<>();

        result.put("proposals", getProposalStatistics(organisationId));
        result.put("members", getMemberStatistics(organisationId));
        result.put("permissions", getPermissionStatistics(organisationId));

        return result;
    }
}
