package be.sel2.api.repositories;

import be.sel2.api.entities.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServiceRepository extends JpaRepository<Service, Long>,
        JpaSpecificationExecutor<Service>, RefreshRepository<Service> {
}
