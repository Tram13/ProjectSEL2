package be.sel2.api.controllers.statistics;

import be.sel2.api.entities.StatisticsEntity;
import be.sel2.api.entities.archive.ArchivedEntity;
import be.sel2.api.models.StatisticsModel;
import be.sel2.api.util.specifications.DefaultSpecification;
import be.sel2.api.util.specifications.SearchCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractStatisticsController {

    protected static final String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";
    protected static final String ROLE_ADMIN = "ROLE_ADMIN";

    public <T extends StatisticsEntity, U extends ArchivedEntity> List<StatisticsModel> getStatistics(
            JpaRepository<T, Long> presentRepo,
            JpaRepository<U, Long> archiveRepo) {
        List<T> present = presentRepo.findAll();

        List<U> deleted = archiveRepo.findAll();

        return listsToStatistics(present, deleted);
    }

    public <T extends StatisticsEntity, U extends ArchivedEntity> List<StatisticsModel> getStatistics(
            JpaSpecificationExecutor<T> presentRepo,
            String fieldName, Object value,
            List<U> deleted) {

        DefaultSpecification<T> spec = new DefaultSpecification<>();
        spec.add(new SearchCriteria(fieldName, value));

        List<T> present = presentRepo.findAll(spec);

        return listsToStatistics(present, deleted);
    }

    public <T extends StatisticsEntity, U extends ArchivedEntity> List<StatisticsModel> listsToStatistics(
            List<T> present,
            List<U> deleted
    ) {
        List<StatisticsModel> statistics = present.stream()
                .map(this::toStatisticsModel)
                .collect(Collectors.toList());

        statistics.addAll(deleted.stream()
                .map(this::toStatisticsModel)
                .collect(Collectors.toList()));

        return statistics;
    }

    public StatisticsModel toStatisticsModel(StatisticsEntity e) {
        return new StatisticsModel(e.getCreated(), e.getLastUpdated(), false);
    }

    public StatisticsModel toStatisticsModel(ArchivedEntity e) {
        return new StatisticsModel(e.getCreated(), e.getDeletedOn(), false);
    }
}
