package be.sel2.api.repositories.archive;

import be.sel2.api.entities.Proposal;
import be.sel2.api.entities.archive.ProposalDeleted;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ArchivedProposalRepository extends JpaRepository<ProposalDeleted, Long> {

    List<ProposalDeleted> findAllByOrganisationId(Long organisation);

    List<ProposalDeleted> findAllByIdInAndStatus(Collection<Long> ids, Proposal.ProposalStatus status);
}
