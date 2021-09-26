package be.sel2.api.controllers.statistics;

import be.sel2.api.models.StatisticsModel;
import be.sel2.api.repositories.UserRepository;
import be.sel2.api.repositories.archive.ArchivedUserRepository;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/statistics/users")
public class UserStatisticsController extends AbstractStatisticsController {

    private final UserRepository userRepo;
    private final ArchivedUserRepository archivedUserRepo;

    public UserStatisticsController(UserRepository userRepo, ArchivedUserRepository archivedUserRepo) {
        this.userRepo = userRepo;
        this.archivedUserRepo = archivedUserRepo;
    }

    @Secured({ROLE_ADMIN, ROLE_EMPLOYEE})
    @GetMapping
    public List<StatisticsModel> getUserStatistics() {
        return getStatistics(userRepo, archivedUserRepo);
    }
}
