package be.sel2.api.repositories;

import be.sel2.api.entities.relations.ContactProposal;
import be.sel2.api.entities.relations.ContactProposalId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ContactProposalRepository extends JpaRepository<ContactProposal, ContactProposalId>,
        JpaSpecificationExecutor<ContactProposal> {
}
