package be.sel2.api.controllers.statistics;

import be.sel2.api.models.StatisticsModel;
import be.sel2.api.repositories.PermissionRepository;
import be.sel2.api.repositories.archive.ArchivedPermissionRepository;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/statistics/permissions")
public class PermissionStatisticsController extends AbstractStatisticsController {

    private final PermissionRepository permissionRepo;
    private final ArchivedPermissionRepository archivedPermissionRepo;

    public PermissionStatisticsController(PermissionRepository permissionRepo, ArchivedPermissionRepository archivedPermissionRepo) {
        this.permissionRepo = permissionRepo;
        this.archivedPermissionRepo = archivedPermissionRepo;
    }

    @Secured({ROLE_ADMIN, ROLE_EMPLOYEE})
    @GetMapping
    public List<StatisticsModel> getPermissionStatistics() {
        return getStatistics(permissionRepo, archivedPermissionRepo);
    }
}
