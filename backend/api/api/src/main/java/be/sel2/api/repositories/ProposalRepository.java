package be.sel2.api.repositories;

import be.sel2.api.entities.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProposalRepository extends JpaRepository<Proposal, Long>,
        JpaSpecificationExecutor<Proposal>, RefreshRepository<Proposal> {
}
