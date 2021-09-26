package be.sel2.api.repositories.archive;

import be.sel2.api.entities.archive.relations.ProposalServiceDeleted;
import be.sel2.api.entities.archive.relations.ProposalServiceDeletedId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArchivedProposalServiceRepository extends JpaRepository<ProposalServiceDeleted, ProposalServiceDeletedId> {

    List<ProposalServiceDeleted> findAllByProposal(Long proposal);

    List<ProposalServiceDeleted> findAllByService(Long service);
}
