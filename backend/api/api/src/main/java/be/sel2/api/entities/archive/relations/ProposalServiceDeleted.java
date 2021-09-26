package be.sel2.api.entities.archive.relations;

import be.sel2.api.entities.relations.ProposalServiceId;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.util.Date;

@Entity
@IdClass(ProposalServiceId.class)
@Getter
@Setter //This adds all the getters, setters
public class ProposalServiceDeleted {
    @Id
    @Column(name = "serviceId")
    private Long service;
    @Id
    @Column(name = "proposalId")
    private Long proposal;
    @Id
    private Date deletedOn;
}
