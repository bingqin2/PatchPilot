package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskDetailVo;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageArchiveVo;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageArchiveSummaryVo;
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
    private static final int MAX_LIMIT = 100;
    private static final String SIDE_EFFECT_CONTRACT = "Task evidence archive review reads PatchPilot-local archived "
            + "reports only; it does not create tasks, call the model, run verification commands, mutate Git, "
            + "push, open Pull Requests, or write GitHub comments.";

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

    public List<FixTaskEvidencePackageArchiveVo> listRecent(int limit) {
        return archiveRepository.listRecent(normalizeLimit(limit));
    }

    public FixTaskEvidencePackageArchiveSummaryVo summary(int limit) {
        List<FixTaskEvidencePackageArchiveVo> archives = listRecent(limit);
        FixTaskEvidencePackageArchiveVo latest = archives.isEmpty() ? null : archives.get(0);
        return new FixTaskEvidencePackageArchiveSummaryVo(
                archives.size(),
                countStatus(archives, "COMPLETED"),
                countStatus(archives, "FAILED"),
                countStatus(archives, "PENDING_REVIEW"),
                countStatus(archives, "CANCELLED"),
                latest == null ? null : latest.id(),
                latest == null ? null : latest.taskId(),
                latest == null ? null : latest.repositoryOwner(),
                latest == null ? null : latest.repositoryName(),
                latest == null ? null : latest.issueNumber(),
                latest == null ? null : latest.archivedAt(),
                SIDE_EFFECT_CONTRACT,
                nextAction(latest)
        );
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

    private static int countStatus(List<FixTaskEvidencePackageArchiveVo> archives, String status) {
        return (int) archives.stream()
                .filter(archive -> status.equals(archive.status()))
                .count();
    }

    private static String nextAction(FixTaskEvidencePackageArchiveVo latest) {
        if (latest == null) {
            return "Archive a completed, failed, cancelled, or pending-review task from Task Detail before sharing evidence.";
        }
        return "Download archived task evidence " + latest.id()
                + " or open task " + latest.taskId()
                + " before sharing review notes.";
    }

    private static int normalizeLimit(int limit) {
        if (limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }
}
