package be.sel2.api.entities.relations;

import be.sel2.api.entities.Proposal;
import be.sel2.api.entities.Service;
import be.sel2.api.serializers.ProposalServiceSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@IdClass(ProposalServiceId.class)
@JsonSerialize(using = ProposalServiceSerializer.class)
@Getter
@Setter //This adds all the getters, setters
public class ProposalService {

    @Id
    @ManyToOne
    @JoinColumn(name = "proposalId", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "none"))
    private Proposal proposal;

    @Id
    @ManyToOne
    @JoinColumn(name = "serviceId", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "none"))
    private Service service;

    @Enumerated(EnumType.STRING)
    private Service.DeliveryMethod deliveryMethod;
    private String source;

    public ProposalService() {
    }

    public ProposalService(Proposal proposal, Service service, Service.DeliveryMethod deliveryMethod, String source) {
        this.proposal = proposal;
        this.service = service;
        this.deliveryMethod = deliveryMethod;
        this.source = source;
    }

    //Help functions

    @JsonIgnore
    private Long getProposalId() {
        if (proposal == null) {
            return null;
        }
        return proposal.getId();
    }

    @JsonIgnore
    private Long getServiceId() {
        if (service == null) {
            return null;
        }
        return service.getId();
    }

    public ProposalServiceId getId() { // Samengesteld ID
        return new ProposalServiceId(getProposalId(), getServiceId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProposalService other = (ProposalService) o;
        return Objects.equals(proposal, other.proposal) &&
                Objects.equals(service, other.service) &&
                deliveryMethod.equals(other.deliveryMethod) &&
                source.equals(other.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), deliveryMethod, source);
    }

    @Override
    public String toString() {
        return "ProposalService{" +
                "id=" + getId() +
                ", deliveryMethod='" + deliveryMethod + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}
