package be.sel2.api.repositories.archive;

import be.sel2.api.entities.archive.CertificateDeleted;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArchivedCertificateRepository extends JpaRepository<CertificateDeleted, Long> {
}
