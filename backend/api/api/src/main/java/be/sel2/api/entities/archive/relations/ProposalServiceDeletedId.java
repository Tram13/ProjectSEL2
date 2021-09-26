package be.sel2.api.entities.archive.relations;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter //This adds all the getters & setters for us
public class ProposalServiceDeletedId implements Serializable {
    private Long proposal;
    private Long service;
    private Date deletedOn;

    public ProposalServiceDeletedId() { // Default constructor is nodig voor Entity-klassen
    }

    public ProposalServiceDeletedId(Long proposal, Long service, Date deletedOn) {
        this.proposal = proposal;
        this.service = service;
        this.deletedOn = deletedOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProposalServiceDeletedId other = (ProposalServiceDeletedId) o;
        return proposal.equals(other.getProposal()) &&
                service.equals(other.getService()) &&
                deletedOn.equals(other.getDeletedOn());
    }

    @Override
    public int hashCode() {
        return Objects.hash(proposal, service);
    }

    @Override
    public String toString() {
        return "ProposalServiceId{" +
                "proposalId=" + proposal +
                ", serviceId=" + service +
                ", deletedOn=" + deletedOn +
                "}";
    }
}
