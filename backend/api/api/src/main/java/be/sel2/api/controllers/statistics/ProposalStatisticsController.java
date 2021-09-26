package be.sel2.api.controllers.statistics;

import be.sel2.api.models.StatisticsModel;
import be.sel2.api.repositories.ProposalRepository;
import be.sel2.api.repositories.archive.ArchivedProposalRepository;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/statistics/proposals")
public class ProposalStatisticsController extends AbstractStatisticsController {

    private final ProposalRepository proposalRepo;
    private final ArchivedProposalRepository archivedProposalRepo;

    public ProposalStatisticsController(ProposalRepository proposalRepo, ArchivedProposalRepository archivedProposalRepo) {
        this.proposalRepo = proposalRepo;
        this.archivedProposalRepo = archivedProposalRepo;
    }

    @Secured({ROLE_ADMIN, ROLE_EMPLOYEE})
    @GetMapping
    public List<StatisticsModel> getProposalStatistics() {
        return getStatistics(proposalRepo, archivedProposalRepo);
    }
}
