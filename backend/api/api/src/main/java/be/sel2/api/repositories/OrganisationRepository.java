package be.sel2.api.repositories;

import be.sel2.api.entities.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrganisationRepository extends JpaRepository<Organisation, Long>,
        JpaSpecificationExecutor<Organisation>, RefreshRepository<Organisation> {
}
