package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoAcceptanceSummaryVo;
import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCertificateArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.DemoLaunchAcceptanceCertificateArchiveRepository;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageAcceptanceCertificateArchiveVo;
import io.patchpilot.backend.task.service.FixTaskEvidencePackageAcceptanceCertificateArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
public class DemoAcceptanceSummaryService {

    private static final String SIDE_EFFECT_CONTRACT = "GET /api/demo/acceptance-summary is read-only: it does not create tasks, call the model, run tests, archive records, mutate Git, send messages, record receipts, or write to GitHub.";

    private final Supplier<List<DemoLaunchAcceptanceCertificateArchiveVo>> launchCertificateArchiveSupplier;
    private final Supplier<List<FixTaskEvidencePackageAcceptanceCertificateArchiveVo>> taskCertificateArchiveSupplier;
    private final Clock clock;

    @Autowired
    public DemoAcceptanceSummaryService(
            DemoLaunchAcceptanceCertificateArchiveRepository launchCertificateArchiveRepository,
            FixTaskEvidencePackageAcceptanceCertificateArchiveRepository taskCertificateArchiveRepository
    ) {
        this(
                () -> launchCertificateArchiveRepository.listRecentArchives(20),
                () -> taskCertificateArchiveRepository.listRecentArchives(20),
                Clock.systemUTC()
        );
    }

    DemoAcceptanceSummaryService(
            Supplier<List<DemoLaunchAcceptanceCertificateArchiveVo>> launchCertificateArchiveSupplier,
            Supplier<List<FixTaskEvidencePackageAcceptanceCertificateArchiveVo>> taskCertificateArchiveSupplier,
            Clock clock
    ) {
        this.launchCertificateArchiveSupplier = launchCertificateArchiveSupplier;
        this.taskCertificateArchiveSupplier = taskCertificateArchiveSupplier;
        this.clock = clock;
    }

    public DemoAcceptanceSummaryVo getSummary() {
        List<DemoLaunchAcceptanceCertificateArchiveVo> launchArchives = launchCertificateArchiveSupplier.get();
        List<FixTaskEvidencePackageAcceptanceCertificateArchiveVo> taskArchives = taskCertificateArchiveSupplier.get();
        DemoLaunchAcceptanceCertificateArchiveVo launchArchive = launchArchives.isEmpty() ? null : launchArchives.get(0);
        FixTaskEvidencePackageAcceptanceCertificateArchiveVo taskArchive = taskArchives.isEmpty() ? null : taskArchives.get(0);

        DemoReadinessStatus launchStatus = launchStatus(launchArchive);
        DemoReadinessStatus taskStatus = taskStatus(taskArchive);
        boolean launchCertified = launchArchive != null && launchArchive.status() == DemoReadinessStatus.READY && launchArchive.certified();
        boolean taskCertified = taskArchive != null && "READY".equals(taskArchive.status()) && taskArchive.certified();
        DemoReadinessStatus status = aggregateStatus(launchStatus, taskStatus);
        boolean accepted = status == DemoReadinessStatus.READY && launchCertified && taskCertified;

        List<DemoAcceptanceSummaryVo.Check> checks = List.of(
                new DemoAcceptanceSummaryVo.Check(
                        "Launch acceptance certificate",
                        launchStatus,
                        launchCheckSummary(launchArchive, launchCertified),
                        launchCheckNextAction(launchArchive, launchCertified)
                ),
                new DemoAcceptanceSummaryVo.Check(
                        "Task evidence acceptance certificate",
                        taskStatus,
                        taskCheckSummary(taskArchive, taskCertified),
                        taskCheckNextAction(taskArchive, taskCertified)
                )
        );
        List<String> evidenceNotes = evidenceNotes(launchArchive, taskArchive, launchCertified, taskCertified);
        List<String> downloadActions = downloadActions(launchArchive, taskArchive);
        String latestPullRequestUrl = hasText(launchArchive == null ? null : launchArchive.latestPullRequestUrl())
                ? launchArchive.latestPullRequestUrl()
                : taskArchive == null ? null : taskArchive.latestPullRequestUrl();

        Instant generatedAt = Instant.now(clock);
        String summary = summary(status);
        String nextAction = nextAction(status, launchArchive, taskArchive, launchCertified, taskCertified);
        return new DemoAcceptanceSummaryVo(
                status,
                accepted,
                summary,
                nextAction,
                launchStatus,
                launchArchive != null,
                launchCertified,
                launchArchive == null ? null : launchArchive.id(),
                launchArchive == null ? null : launchArchive.latestCloseoutArchiveId(),
                launchArchive == null ? null : launchArchive.latestLaunchEvidenceArchiveId(),
                launchArchive == null ? null : launchArchive.latestDeliveryReceiptId(),
                taskStatus,
                taskArchive != null,
                taskCertified,
                taskArchive == null ? null : taskArchive.id(),
                taskArchive == null ? null : taskArchive.latestCloseoutArchiveId(),
                taskArchive == null ? null : taskArchive.latestEvidenceArchiveId(),
                taskArchive == null ? null : taskArchive.latestDeliveryReceiptId(),
                taskArchive == null ? null : taskArchive.latestTaskId(),
                latestPullRequestUrl,
                generatedAt,
                checks,
                evidenceNotes,
                downloadActions,
                SIDE_EFFECT_CONTRACT,
                markdownReport(status, accepted, summary, nextAction, launchArchive, taskArchive, checks, evidenceNotes, downloadActions, generatedAt)
        );
    }

    private static DemoReadinessStatus aggregateStatus(DemoReadinessStatus launchStatus, DemoReadinessStatus taskStatus) {
        if (launchStatus == DemoReadinessStatus.BLOCKED || taskStatus == DemoReadinessStatus.BLOCKED) {
            return DemoReadinessStatus.BLOCKED;
        }
        return launchStatus == DemoReadinessStatus.READY && taskStatus == DemoReadinessStatus.READY
                ? DemoReadinessStatus.READY
                : DemoReadinessStatus.NEEDS_ATTENTION;
    }

    private static DemoReadinessStatus launchStatus(DemoLaunchAcceptanceCertificateArchiveVo archive) {
        if (archive == null) {
            return DemoReadinessStatus.NEEDS_ATTENTION;
        }
        if (archive.status() == DemoReadinessStatus.BLOCKED) {
            return DemoReadinessStatus.BLOCKED;
        }
        return archive.status() == DemoReadinessStatus.READY && archive.certified()
                ? DemoReadinessStatus.READY
                : DemoReadinessStatus.NEEDS_ATTENTION;
    }

    private static DemoReadinessStatus taskStatus(FixTaskEvidencePackageAcceptanceCertificateArchiveVo archive) {
        if (archive == null) {
            return DemoReadinessStatus.NEEDS_ATTENTION;
        }
        if ("BLOCKED".equals(archive.status())) {
            return DemoReadinessStatus.BLOCKED;
        }
        return "READY".equals(archive.status()) && archive.certified()
                ? DemoReadinessStatus.READY
                : DemoReadinessStatus.NEEDS_ATTENTION;
    }

    private static String summary(DemoReadinessStatus status) {
        return switch (status) {
            case READY -> "PatchPilot final demo acceptance is ready for external review.";
            case BLOCKED -> "PatchPilot final demo acceptance is blocked.";
            case NEEDS_ATTENTION -> "PatchPilot final demo acceptance needs attention.";
        };
    }

    private static String nextAction(
            DemoReadinessStatus status,
            DemoLaunchAcceptanceCertificateArchiveVo launchArchive,
            FixTaskEvidencePackageAcceptanceCertificateArchiveVo taskArchive,
            boolean launchCertified,
            boolean taskCertified
    ) {
        if (status == DemoReadinessStatus.READY) {
            return "Share the launch and task evidence certificates with reviewers.";
        }
        if (launchArchive == null) {
            return "Archive a certified launch acceptance certificate before final demo acceptance.";
        }
        if (launchArchive.status() == DemoReadinessStatus.BLOCKED) {
            return "Resolve blocked launch acceptance certificate evidence before final demo acceptance.";
        }
        if (!launchCertified) {
            return "Archive a certified launch acceptance certificate before final demo acceptance.";
        }
        if (taskArchive == null) {
            return "Archive a certified task evidence acceptance certificate before final demo acceptance.";
        }
        if ("BLOCKED".equals(taskArchive.status())) {
            return "Resolve blocked task evidence acceptance certificate evidence before final demo acceptance.";
        }
        if (!taskCertified) {
            return "Archive a certified task evidence acceptance certificate before final demo acceptance.";
        }
        return "Resolve final demo acceptance evidence blockers.";
    }

    private static String launchCheckSummary(
            DemoLaunchAcceptanceCertificateArchiveVo archive,
            boolean certified
    ) {
        if (archive == null) {
            return "No launch acceptance certificate archive is available.";
        }
        if (archive.status() == DemoReadinessStatus.BLOCKED) {
            return "Latest launch acceptance certificate archive is blocked.";
        }
        return certified
                ? "Latest launch acceptance certificate archive is certified."
                : "Latest launch acceptance certificate archive is not certified.";
    }

    private static String launchCheckNextAction(
            DemoLaunchAcceptanceCertificateArchiveVo archive,
            boolean certified
    ) {
        if (certified) {
            return "Use the archived launch acceptance certificate for launch-level review proof.";
        }
        if (archive == null) {
            return "Archive a certified launch acceptance certificate.";
        }
        return "Resolve launch acceptance certificate blockers, then archive a certified certificate.";
    }

    private static String taskCheckSummary(
            FixTaskEvidencePackageAcceptanceCertificateArchiveVo archive,
            boolean certified
    ) {
        if (archive == null) {
            return "No task evidence acceptance certificate archive is available.";
        }
        if ("BLOCKED".equals(archive.status())) {
            return "Latest task evidence acceptance certificate archive is blocked.";
        }
        return certified
                ? "Latest task evidence acceptance certificate archive is certified."
                : "Latest task evidence acceptance certificate archive is not certified.";
    }

    private static String taskCheckNextAction(
            FixTaskEvidencePackageAcceptanceCertificateArchiveVo archive,
            boolean certified
    ) {
        if (certified) {
            return "Use the archived task evidence acceptance certificate for task-level review proof.";
        }
        if (archive == null) {
            return "Archive a certified task evidence acceptance certificate.";
        }
        return "Resolve task evidence acceptance certificate blockers, then archive a certified certificate.";
    }

    private static List<String> evidenceNotes(
            DemoLaunchAcceptanceCertificateArchiveVo launchArchive,
            FixTaskEvidencePackageAcceptanceCertificateArchiveVo taskArchive,
            boolean launchCertified,
            boolean taskCertified
    ) {
        List<String> notes = new ArrayList<>();
        if (launchArchive == null) {
            notes.add("No launch acceptance certificate archive is available.");
        } else {
            notes.add("Launch certificate archive " + launchArchive.id() + (launchCertified ? " is certified." : " is not certified."));
        }
        if (taskArchive == null) {
            notes.add("No task evidence acceptance certificate archive is available.");
        } else {
            notes.add("Task evidence certificate archive " + taskArchive.id() + (taskCertified ? " is certified." : " is not certified."));
        }
        return List.copyOf(notes);
    }

    private static List<String> downloadActions(
            DemoLaunchAcceptanceCertificateArchiveVo launchArchive,
            FixTaskEvidencePackageAcceptanceCertificateArchiveVo taskArchive
    ) {
        List<String> actions = new ArrayList<>();
        if (launchArchive != null) {
            actions.add("Download launch acceptance certificate archive " + launchArchive.id() + ".");
        }
        if (taskArchive != null) {
            actions.add("Download task evidence acceptance certificate archive " + taskArchive.id() + ".");
        }
        return List.copyOf(actions);
    }

    private static String markdownReport(
            DemoReadinessStatus status,
            boolean accepted,
            String summary,
            String nextAction,
            DemoLaunchAcceptanceCertificateArchiveVo launchArchive,
            FixTaskEvidencePackageAcceptanceCertificateArchiveVo taskArchive,
            List<DemoAcceptanceSummaryVo.Check> checks,
            List<String> evidenceNotes,
            List<String> downloadActions,
            Instant generatedAt
    ) {
        StringBuilder report = new StringBuilder();
        report.append("# PatchPilot Final Demo Acceptance Summary\n\n")
                .append("- Status: `").append(status).append("`\n")
                .append("- Accepted: `").append(accepted).append("`\n")
                .append("- Summary: ").append(summary).append("\n")
                .append("- Next action: ").append(nextAction).append("\n")
                .append("- Generated at: `").append(generatedAt).append("`\n")
                .append("- Launch certificate archive: `").append(valueOrMissing(launchArchive == null ? null : launchArchive.id())).append("`\n")
                .append("- Task evidence certificate archive: `").append(valueOrMissing(taskArchive == null ? null : taskArchive.id())).append("`\n\n")
                .append("## Checks\n\n");
        checks.forEach(check -> report.append("- ")
                .append(check.name())
                .append(": `").append(check.status()).append("` - ")
                .append(check.summary())
                .append(" Next action: ")
                .append(check.nextAction())
                .append("\n"));
        report.append("\n## Evidence Notes\n\n");
        evidenceNotes.forEach(note -> report.append("- ").append(note).append("\n"));
        report.append("\n## Download Actions\n\n");
        if (downloadActions.isEmpty()) {
            report.append("- No certificate archives are available to download.\n");
        } else {
            downloadActions.forEach(action -> report.append("- ").append(action).append("\n"));
        }
        report.append("\n## Side Effect Contract\n\n- ").append(SIDE_EFFECT_CONTRACT).append("\n");
        return report.toString();
    }

    private static String valueOrMissing(String value) {
        return hasText(value) ? value : "missing";
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
