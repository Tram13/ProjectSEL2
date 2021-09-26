package be.sel2.api.controllers.statistics;

import be.sel2.api.entities.Proposal;
import be.sel2.api.entities.archive.ProposalDeleted;
import be.sel2.api.entities.archive.relations.PackageProposalDeleted;
import be.sel2.api.models.StatisticsModel;
import be.sel2.api.repositories.PackageRepository;
import be.sel2.api.repositories.ProposalRepository;
import be.sel2.api.repositories.archive.ArchivedPackageProposalRepository;
import be.sel2.api.repositories.archive.ArchivedPackageRepository;
import be.sel2.api.repositories.archive.ArchivedProposalRepository;
import be.sel2.api.util.specifications.AnyIsInListSpecification;
import be.sel2.api.util.specifications.DefaultSpecification;
import be.sel2.api.util.specifications.SearchCriteria;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/statistics/packages")
public class PackageStatisticsController extends AbstractStatisticsController {

    private final PackageRepository packageRepo;
    private final ArchivedPackageRepository archivedPackageRepo;

    private final ProposalRepository propRepo;
    private final ArchivedProposalRepository archPropRepo;
    private final ArchivedPackageProposalRepository archPackPropRepo;


    public PackageStatisticsController(
            PackageRepository packageRepo, ArchivedPackageRepository archivedPackageRepo,
            ProposalRepository propRepo, ArchivedProposalRepository archPropRepo,
            ArchivedPackageProposalRepository archPackPropRepo) {
        this.packageRepo = packageRepo;
        this.archivedPackageRepo = archivedPackageRepo;
        this.propRepo = propRepo;
        this.archPropRepo = archPropRepo;
        this.archPackPropRepo = archPackPropRepo;
    }

    @Secured({ROLE_ADMIN, ROLE_EMPLOYEE})
    @GetMapping
    public List<StatisticsModel> getPackageStatistics() {
        return getStatistics(packageRepo, archivedPackageRepo);
    }

    public List<StatisticsModel> getCustomerStatistics(Long packageId) {
        // Find the deleted customers
        List<PackageProposalDeleted> deletedPackProp = archPackPropRepo.findAllByPack(packageId);
        List<Long> proposalIds = deletedPackProp.stream().map(PackageProposalDeleted::getProposal)
                .collect(Collectors.toList());

        List<ProposalDeleted> deletedProposals = archPropRepo
                .findAllByIdInAndStatus(proposalIds, Proposal.ProposalStatus.ACCEPTED);

        // Find the non-deleted customers
        DefaultSpecification<Proposal> existingPackPropSpec = new AnyIsInListSpecification<>();
        existingPackPropSpec.add(new SearchCriteria("packages", packageId));
        DefaultSpecification<Proposal> statusSpec = new DefaultSpecification<>();
        statusSpec.add(new SearchCriteria("status", Proposal.ProposalStatus.ACCEPTED));

        List<Proposal> existingProposals = propRepo.findAll(existingPackPropSpec.and(statusSpec));

        return listsToStatistics(existingProposals, deletedProposals);
    }

    @Secured({ROLE_ADMIN, ROLE_EMPLOYEE})
    @GetMapping("/{packageId}")
    public Map<String, List<StatisticsModel>> getPackageStatistics(
            @PathVariable Long packageId) {
        Map<String, List<StatisticsModel>> result = new HashMap<>();

        result.put("customers", getCustomerStatistics(packageId));

        return result;
    }
}
