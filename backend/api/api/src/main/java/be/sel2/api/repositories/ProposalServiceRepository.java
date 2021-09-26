package be.sel2.api.repositories;

import be.sel2.api.entities.relations.ProposalService;
import be.sel2.api.entities.relations.ProposalServiceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProposalServiceRepository extends JpaRepository<ProposalService, ProposalServiceId>,
        JpaSpecificationExecutor<ProposalService> {
}
