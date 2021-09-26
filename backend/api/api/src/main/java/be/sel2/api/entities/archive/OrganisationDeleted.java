package be.sel2.api.entities.archive;

import javax.persistence.Entity;

@Entity
public class OrganisationDeleted extends ArchivedEntity {

    private Boolean approved;
}
