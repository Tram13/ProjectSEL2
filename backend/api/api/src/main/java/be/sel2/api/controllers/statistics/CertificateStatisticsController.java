package be.sel2.api.controllers.statistics;

import be.sel2.api.models.StatisticsModel;
import be.sel2.api.repositories.CertificateRepository;
import be.sel2.api.repositories.archive.ArchivedCertificateRepository;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/statistics/certificates")
public class CertificateStatisticsController extends AbstractStatisticsController {

    private final CertificateRepository certificateRepo;
    private final ArchivedCertificateRepository archivedCertificateRepo;

    public CertificateStatisticsController(CertificateRepository certificateRepo, ArchivedCertificateRepository archivedCertificateRepo) {
        this.certificateRepo = certificateRepo;
        this.archivedCertificateRepo = archivedCertificateRepo;
    }

    @Secured({ROLE_ADMIN, ROLE_EMPLOYEE})
    @GetMapping
    public List<StatisticsModel> getCertificateStatistics() {
        return getStatistics(certificateRepo, archivedCertificateRepo);
    }
}
