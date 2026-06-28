package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskDetailVo;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageArchiveVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class FixTaskEvidencePackageArchiveService {

    private static final int DEFAULT_LIMIT = 20;

    private final FixTaskEvidencePackageArchiveRepository archiveRepository;
    private final FixTaskReportFormatter reportFormatter;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public FixTaskEvidencePackageArchiveService(
            FixTaskEvidencePackageArchiveRepository archiveRepository,
            FixTaskReportFormatter reportFormatter
    ) {
        this(
                archiveRepository,
                reportFormatter,
                Clock.systemUTC(),
                () -> UUID.randomUUID().toString()
        );
    }

    FixTaskEvidencePackageArchiveService(
            FixTaskEvidencePackageArchiveRepository archiveRepository,
            FixTaskReportFormatter reportFormatter,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.archiveRepository = archiveRepository;
        this.reportFormatter = reportFormatter;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public FixTaskEvidencePackageArchiveVo archive(FixTaskDetailVo detail) {
        FixTaskVo task = detail.summary().task();
        String report = reportFormatter.format(detail);
        FixTaskEvidencePackageArchiveVo archive = new FixTaskEvidencePackageArchiveVo(
                idSupplier.get(),
                task.id(),
                task.repositoryOwner(),
                task.repositoryName(),
                task.issueNumber(),
                task.status().name(),
                task.pullRequestUrl(),
                clock.instant(),
                summary(task),
                report
        );
        return archiveRepository.save(archive);
    }

    public List<FixTaskEvidencePackageArchiveVo> listByTaskId(String taskId) {
        return archiveRepository.listByTaskId(taskId, DEFAULT_LIMIT);
    }

    public Optional<FixTaskEvidencePackageArchiveVo> findById(String archiveId) {
        return archiveRepository.findById(archiveId);
    }

    private static String summary(FixTaskVo task) {
        return "Task " + task.status().name()
                + " for " + task.repositoryOwner() + "/" + task.repositoryName()
                + "#" + task.issueNumber()
                + " archived as evidence.";
    }
}
