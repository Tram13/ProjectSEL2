package be.sel2.api.controllers.statistics;

import be.sel2.api.repositories.FileRepository;
import be.sel2.api.repositories.archive.ArchivedFileRepository;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/statistics/files")
public class FileStatisticsController extends AbstractStatisticsController {

    private final FileRepository fileRepo;
    private final ArchivedFileRepository archivedFileRepo;

    public FileStatisticsController(FileRepository fileRepo, ArchivedFileRepository archivedFileRepo) {
        this.fileRepo = fileRepo;
        this.archivedFileRepo = archivedFileRepo;
    }

    @Secured({ROLE_ADMIN, ROLE_EMPLOYEE})
    @GetMapping
    public Map<String, Object> getFileStatistics() {
        Map<String, Object> result = new HashMap<>();

        result.put("stats", getStatistics(fileRepo, archivedFileRepo));

        Long nonDeletedFileSize = Objects.requireNonNullElse(fileRepo.getUsedStorageSpace(), 0L);
        Long archivedFileSize = Objects.requireNonNullElse(archivedFileRepo.getUsedStorageSpace(), 0L);


        result.put("usedFileSize", nonDeletedFileSize);
        result.put("archivedFileSize", archivedFileSize);
        result.put("totalSize", nonDeletedFileSize + archivedFileSize);

        return result;
    }
}
