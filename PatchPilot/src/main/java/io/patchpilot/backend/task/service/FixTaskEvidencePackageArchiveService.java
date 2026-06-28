package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskDetailVo;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageArchiveVo;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageArchiveSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageShareCenterVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
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
    private static final String SHARE_SIDE_EFFECT_CONTRACT = "Task evidence share center is read-only; it reads "
            + "PatchPilot-local archived reports only and does not create tasks, call the model, run verification "
            + "commands, mutate Git, push, open Pull Requests, or write GitHub comments.";

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

    public FixTaskEvidencePackageShareCenterVo shareCenter(int limit) {
        List<FixTaskEvidencePackageArchiveVo> archives = listRecent(limit);
        FixTaskEvidencePackageArchiveVo latest = archives.isEmpty() ? null : archives.get(0);
        FixTaskEvidencePackageArchiveVo shareable = archives.stream()
                .filter(archive -> "COMPLETED".equals(archive.status()))
                .filter(archive -> hasText(archive.pullRequestUrl()))
                .findFirst()
                .orElse(null);
        String status = shareCenterStatus(archives, shareable);
        boolean shareReady = shareable != null;
        Instant generatedAt = clock.instant();
        String summary = shareCenterSummary(archives, shareable);
        String nextAction = shareCenterNextAction(archives, shareable);
        List<String> downloadActions = shareCenterDownloadActions(shareable);
        List<String> evidenceNotes = shareCenterEvidenceNotes(latest, shareable);

        FixTaskEvidencePackageShareCenterVo shareCenter = new FixTaskEvidencePackageShareCenterVo(
                status,
                shareReady,
                summary,
                nextAction,
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
                shareable == null ? null : shareable.id(),
                shareable == null ? null : shareable.taskId(),
                shareable == null ? null : shareable.repositoryOwner(),
                shareable == null ? null : shareable.repositoryName(),
                shareable == null ? null : shareable.issueNumber(),
                shareable == null ? null : shareable.pullRequestUrl(),
                downloadActions,
                evidenceNotes,
                SHARE_SIDE_EFFECT_CONTRACT,
                "",
                generatedAt
        );
        return new FixTaskEvidencePackageShareCenterVo(
                shareCenter.status(),
                shareCenter.shareReady(),
                shareCenter.summary(),
                shareCenter.nextAction(),
                shareCenter.archiveCount(),
                shareCenter.completedArchiveCount(),
                shareCenter.failedArchiveCount(),
                shareCenter.pendingReviewArchiveCount(),
                shareCenter.cancelledArchiveCount(),
                shareCenter.latestArchiveId(),
                shareCenter.latestTaskId(),
                shareCenter.latestRepositoryOwner(),
                shareCenter.latestRepositoryName(),
                shareCenter.latestIssueNumber(),
                shareCenter.latestArchivedAt(),
                shareCenter.shareableArchiveId(),
                shareCenter.shareableTaskId(),
                shareCenter.shareableRepositoryOwner(),
                shareCenter.shareableRepositoryName(),
                shareCenter.shareableIssueNumber(),
                shareCenter.shareablePullRequestUrl(),
                shareCenter.downloadActions(),
                shareCenter.evidenceNotes(),
                shareCenter.sideEffectContract(),
                formatShareCenterReport(shareCenter),
                shareCenter.generatedAt()
        );
    }

    public String shareCenterReport(int limit) {
        return shareCenter(limit).markdownReport();
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

    private static String shareCenterStatus(
            List<FixTaskEvidencePackageArchiveVo> archives,
            FixTaskEvidencePackageArchiveVo shareable
    ) {
        if (shareable != null) {
            return "READY";
        }
        if (archives.isEmpty()) {
            return "BLOCKED";
        }
        return "NEEDS_ATTENTION";
    }

    private static String shareCenterSummary(
            List<FixTaskEvidencePackageArchiveVo> archives,
            FixTaskEvidencePackageArchiveVo shareable
    ) {
        if (shareable != null) {
            return "A shareable completed task evidence package is available for external review.";
        }
        if (archives.isEmpty()) {
            return "No archived task evidence packages are available for sharing.";
        }
        return "Archived task evidence exists, but none has both COMPLETED status and a Pull Request URL.";
    }

    private static String shareCenterNextAction(
            List<FixTaskEvidencePackageArchiveVo> archives,
            FixTaskEvidencePackageArchiveVo shareable
    ) {
        if (shareable != null) {
            return "Download archived task evidence " + shareable.id()
                    + " and open Pull Request " + shareable.pullRequestUrl()
                    + " before sharing externally.";
        }
        if (archives.isEmpty()) {
            return "Archive a completed task with a Pull Request before preparing external evidence.";
        }
        return "Resolve failed or pending tasks, then archive a completed task that has a Pull Request URL.";
    }

    private static List<String> shareCenterDownloadActions(FixTaskEvidencePackageArchiveVo shareable) {
        List<String> actions = new ArrayList<>();
        if (shareable == null) {
            actions.add("Archive a completed task with Pull Request evidence before downloading a share package.");
            return actions;
        }
        actions.add("Download archived task evidence " + shareable.id() + ".");
        actions.add("Open Pull Request " + shareable.pullRequestUrl() + ".");
        return actions;
    }

    private static List<String> shareCenterEvidenceNotes(
            FixTaskEvidencePackageArchiveVo latest,
            FixTaskEvidencePackageArchiveVo shareable
    ) {
        List<String> notes = new ArrayList<>();
        if (latest == null) {
            notes.add("No latest archive is available.");
        } else {
            notes.add("Latest archive " + latest.id() + " is " + latest.status() + ".");
        }
        if (shareable == null) {
            notes.add("No completed archive with a Pull Request URL is available.");
        } else {
            notes.add("Shareable archive " + shareable.id() + " completed with a Pull Request.");
        }
        return notes;
    }

    private static String formatShareCenterReport(FixTaskEvidencePackageShareCenterVo shareCenter) {
        StringBuilder builder = new StringBuilder();
        builder.append("# PatchPilot Task Evidence Share Center\n\n");
        builder.append("- Status: `").append(shareCenter.status()).append("`\n");
        builder.append("- Share ready: `").append(shareCenter.shareReady()).append("`\n");
        builder.append("- Summary: ").append(shareCenter.summary()).append("\n");
        builder.append("- Next action: ").append(shareCenter.nextAction()).append("\n");
        builder.append("- Archive count: `").append(shareCenter.archiveCount()).append("`\n");
        builder.append("- Completed archives: `").append(shareCenter.completedArchiveCount()).append("`\n");
        builder.append("- Failed archives: `").append(shareCenter.failedArchiveCount()).append("`\n");
        builder.append("- Latest archive: `").append(nullSafe(shareCenter.latestArchiveId())).append("`\n");
        builder.append("- Shareable archive: `").append(nullSafe(shareCenter.shareableArchiveId())).append("`\n");
        builder.append("- Shareable task: `").append(nullSafe(shareCenter.shareableTaskId())).append("`\n");
        builder.append("- Shareable Pull Request: ").append(nullSafe(shareCenter.shareablePullRequestUrl())).append("\n");
        builder.append("- Generated at: `").append(shareCenter.generatedAt()).append("`\n\n");
        builder.append("## Download Actions\n\n");
        appendBulletList(builder, shareCenter.downloadActions());
        builder.append("\n## Evidence Notes\n\n");
        appendBulletList(builder, shareCenter.evidenceNotes());
        builder.append("\n## Side Effect Contract\n\n");
        builder.append(shareCenter.sideEffectContract()).append("\n");
        return builder.toString();
    }

    private static void appendBulletList(StringBuilder builder, List<String> items) {
        if (items.isEmpty()) {
            builder.append("- None\n");
            return;
        }
        items.forEach(item -> builder.append("- ").append(item).append("\n"));
    }

    private static String nullSafe(String value) {
        return value == null ? "none" : value;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static int normalizeLimit(int limit) {
        if (limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }
}
