package be.sel2.api.entities.archive.relations;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.util.Date;

@Entity
@IdClass(PackageProposalDeletedId.class)
@Getter
@Setter //This adds all the getters, setters, equals & hash for us
public class PackageProposalDeleted {

    @Id
    @Column(name = "packageId")
    private Long pack; // "package" is not a valid variable name in Java

    @Id
    @Column(name = "proposalId")
    private Long proposal;

    @Id
    private Date deletedOn;
}
