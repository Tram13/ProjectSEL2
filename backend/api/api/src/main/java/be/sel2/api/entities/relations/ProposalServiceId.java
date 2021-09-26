package be.sel2.api.entities.relations;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter //This adds all the getters & setters for us
public class ProposalServiceId implements Serializable {
    private Long proposal;
    private Long service;

    public ProposalServiceId() { // Default constructor is nodig voor Entity-klassen
    }

    public ProposalServiceId(Long proposal, Long service) {
        this.proposal = proposal;
        this.service = service;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProposalServiceId other = (ProposalServiceId) o;
        return proposal.equals(other.getProposal()) &&
                service.equals(other.getService());
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
                "}";
    }
}
