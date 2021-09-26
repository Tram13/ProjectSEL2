package be.sel2.api.entities.relations;

import be.sel2.api.entities.Contact;
import be.sel2.api.entities.Proposal;
import be.sel2.api.serializers.ContactProposalSerializer;
import be.sel2.api.serializers.EnumToLowercaseSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@IdClass(ContactProposalId.class)
@JsonSerialize(using = ContactProposalSerializer.class)
@Getter
@Setter //This adds all the getters, setters
public class ContactProposal {

    @Id
    @ManyToOne
    @JoinColumn(name = "proposalId", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "none"))
    private Proposal proposal;

    @Id
    @ManyToOne
    @JoinColumn(name = "contactId", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "none"))
    private Contact contact;

    @Id
    @Enumerated(EnumType.STRING)
    private Contactrole role;

    public ContactProposal() {
    }

    public ContactProposal(Proposal proposal, Contact contact, Contactrole role) {
        this.proposal = proposal;
        this.contact = contact;
        this.role = role;
    }

    public ContactProposalId getId() { // Samengesteld ID
        return new ContactProposalId(contact.getId(), proposal.getId(), role);
    }

    @Override
    public String toString() {
        return "ContactProposal{" +
                "proposalId=" + proposal.getId() +
                ", contactId=" + contact.getId() +
                ", role=" + role +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(proposal, contact, role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactProposal other = (ContactProposal) o;
        if (proposal == null && other.proposal == null) return true;
        if (proposal == null || other.proposal == null) return false;
        if (contact == null && other.contact == null) return true;
        if (contact == null || other.contact == null) return false;
        return Objects.equals(proposal.getId(), other.proposal.getId()) &&
                Objects.equals(contact.getId(), other.contact.getId()) &&
                role.equals(other.role);
    }

    @JsonSerialize(using = EnumToLowercaseSerializer.class)
    public enum Contactrole {
        SUBMITTER, BUSINESS, BUSINESS_BACKUP, TECHNICAL, TECHNICAL_BACKUP, SAFETY_CONSULTANT, SERVICE_PROVIDER,
        RESPONSIBLE_D2D_MANAGEMENT_CUSTOMER, MANAGER_GEOSECURE;

        public static Contactrole fromString(String val) {
            try {
                return Contactrole.valueOf(val.toUpperCase());
            } catch (IllegalArgumentException | NullPointerException e) {
                return null;
            }
        }
    }
}
