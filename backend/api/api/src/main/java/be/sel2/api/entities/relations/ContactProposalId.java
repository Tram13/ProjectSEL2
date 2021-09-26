package be.sel2.api.entities.relations;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class ContactProposalId implements Serializable {
    private Long contact;
    private Long proposal;
    private ContactProposal.Contactrole role;

    public ContactProposalId() { // Empty constructor necessary for ID classes
    }

    public ContactProposalId(Long contact, Long proposal, ContactProposal.Contactrole role) {
        this.contact = contact;
        this.proposal = proposal;
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContactProposalId)) return false;
        ContactProposalId that = (ContactProposalId) o;
        return contact.equals(that.contact) && proposal.equals(that.proposal) && role.equals(that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contact, proposal, role);
    }
}
