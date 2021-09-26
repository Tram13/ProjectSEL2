package be.sel2.api.repositories.archive;

import be.sel2.api.entities.archive.OrganisationDeleted;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArchivedOrganisationRepository extends JpaRepository<OrganisationDeleted, Long> {

    List<OrganisationDeleted> findAllByApproved(Boolean approved);
}
