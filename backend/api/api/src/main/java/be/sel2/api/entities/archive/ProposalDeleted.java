package be.sel2.api.entities.archive;

import be.sel2.api.entities.Proposal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@Getter
@Setter
public class ProposalDeleted extends ArchivedEntity {

    private Long organisationId;

    @Enumerated(EnumType.STRING)
    private Proposal.ProposalStatus status;
}

