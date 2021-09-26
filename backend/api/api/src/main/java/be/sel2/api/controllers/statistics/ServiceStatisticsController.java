package be.sel2.api.controllers.statistics;

import be.sel2.api.entities.Proposal;
import be.sel2.api.entities.archive.ProposalDeleted;
import be.sel2.api.entities.archive.relations.ProposalServiceDeleted;
import be.sel2.api.entities.relations.ProposalService;
import be.sel2.api.models.StatisticsModel;
import be.sel2.api.repositories.ProposalRepository;
import be.sel2.api.repositories.ProposalServiceRepository;
import be.sel2.api.repositories.ServiceRepository;
import be.sel2.api.repositories.archive.ArchivedProposalRepository;
import be.sel2.api.repositories.archive.ArchivedProposalServiceRepository;
import be.sel2.api.repositories.archive.ArchivedServiceRepository;
import be.sel2.api.util.specifications.ContainsSpecification;
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
@RequestMapping("/statistics/services")
public class ServiceStatisticsController extends AbstractStatisticsController {

    private final ServiceRepository serviceRepo;
    private final ArchivedServiceRepository archivedServiceRepo;

    private final ProposalRepository propRepo;
    private final ArchivedProposalRepository archPropRepo;

    private final ProposalServiceRepository propServRepo;
    private final ArchivedProposalServiceRepository archPropServRepo;

    public ServiceStatisticsController(
            ServiceRepository serviceRepo, ArchivedServiceRepository archivedServiceRepo,
            ProposalRepository propRepo, ArchivedProposalRepository archPropRepo,
            ProposalServiceRepository propServRepo, ArchivedProposalServiceRepository archPropServRepo) {
        this.serviceRepo = serviceRepo;
        this.archivedServiceRepo = archivedServiceRepo;
        this.propRepo = propRepo;
        this.archPropRepo = archPropRepo;
        this.propServRepo = propServRepo;
        this.archPropServRepo = archPropServRepo;
    }

    @Secured({ROLE_ADMIN, ROLE_EMPLOYEE})
    @GetMapping
    public List<StatisticsModel> getServiceStatistics() {
        return getStatistics(serviceRepo, archivedServiceRepo);
    }

    public List<StatisticsModel> getCustomerStatistics(Long serviceId) {

        // Find the deleted customers
        List<ProposalServiceDeleted> deletedServProp = archPropServRepo.findAllByService(serviceId);
        List<Long> proposalIds = deletedServProp.stream().map(ProposalServiceDeleted::getProposal)
                .collect(Collectors.toList());

        List<ProposalDeleted> deletedProposals = archPropRepo
                .findAllByIdInAndStatus(proposalIds, Proposal.ProposalStatus.ACCEPTED);

        // Find the non-deleted customers
        DefaultSpecification<ProposalService> propServSpec = new DefaultSpecification<>();
        propServSpec.add(new SearchCriteria("service", serviceId));
        List<ProposalService> propServs = propServRepo.findAll(propServSpec);

        DefaultSpecification<Proposal> idSpec = new ContainsSpecification<>();
        idSpec.addAll(propServs.stream()
                .map(p -> new SearchCriteria("id", p.getProposal().getId()))
                .collect(Collectors.toList()));
        DefaultSpecification<Proposal> statusSpec = new DefaultSpecification<>();
        statusSpec.add(new SearchCriteria("status", Proposal.ProposalStatus.ACCEPTED));

        List<Proposal> existingProposals = propRepo.findAll(statusSpec);

        return listsToStatistics(existingProposals, deletedProposals);
    }

    @Secured({ROLE_ADMIN, ROLE_EMPLOYEE})
    @GetMapping("/{serviceId}")
    public Map<String, List<StatisticsModel>> getServiceStatistics(
            @PathVariable Long serviceId) {
        Map<String, List<StatisticsModel>> result = new HashMap<>();

        result.put("customers", getCustomerStatistics(serviceId));

        return result;
    }
}
