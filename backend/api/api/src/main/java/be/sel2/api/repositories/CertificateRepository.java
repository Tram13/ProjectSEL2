package be.sel2.api.repositories;

import be.sel2.api.entities.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CertificateRepository extends JpaRepository<Certificate, Long>,
        JpaSpecificationExecutor<Certificate> {
}
