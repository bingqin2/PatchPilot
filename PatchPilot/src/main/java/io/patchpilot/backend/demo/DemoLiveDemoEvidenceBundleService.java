package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerLaunchPackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerOutcomeCloseoutArchiveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class DemoLiveDemoEvidenceBundleService {

    private static final String READY = "READY";
    private static final String BLOCKED = "BLOCKED";
    private static final String NEEDS_ATTENTION = "NEEDS_ATTENTION";
    private static final String SIDE_EFFECT_CONTRACT =
            "read-only live demo evidence bundle: this endpoint does not mutate GitHub, tasks, queues, repositories, "
                    + "launch packages, or closeout archives.";

    private final DemoLiveTriggerLaunchPackageArchiveRepository launchArchiveRepository;
    private final DemoLiveTriggerOutcomeCloseoutArchiveRepository closeoutArchiveRepository;
    private final Supplier<Instant> nowSupplier;

    @Autowired
    public DemoLiveDemoEvidenceBundleService(
            DemoLiveTriggerLaunchPackageArchiveRepository launchArchiveRepository,
            DemoLiveTriggerOutcomeCloseoutArchiveRepository closeoutArchiveRepository
    ) {
        this(launchArchiveRepository, closeoutArchiveRepository, Instant::now);
    }

    DemoLiveDemoEvidenceBundleService(
            DemoLiveTriggerLaunchPackageArchiveRepository launchArchiveRepository,
            DemoLiveTriggerOutcomeCloseoutArchiveRepository closeoutArchiveRepository,
            Supplier<Instant> nowSupplier
    ) {
        this.launchArchiveRepository = launchArchiveRepository;
        this.closeoutArchiveRepository = closeoutArchiveRepository;
        this.nowSupplier = nowSupplier;
    }

    public DemoLiveDemoEvidenceBundleVo createBundle() {
        Optional<DemoLiveTriggerLaunchPackageArchiveVo> launchArchive =
                launchArchiveRepository.listRecentArchives(1).stream().findFirst();
        Optional<DemoLiveTriggerOutcomeCloseoutArchiveVo> closeoutArchive =
                closeoutArchiveRepository.listRecentArchives(1).stream().findFirst();
        Instant generatedAt = nowSupplier.get();

        BundleDecision decision = decide(launchArchive, closeoutArchive);
        DemoLiveDemoEvidenceBundleVo bundle = new DemoLiveDemoEvidenceBundleVo(
                decision.status(),
                READY.equals(decision.status()),
                repository(launchArchive, closeoutArchive),
                issueNumber(launchArchive, closeoutArchive),
                issueUrl(launchArchive, closeoutArchive),
                triggerUser(launchArchive, closeoutArchive),
                triggerComment(launchArchive, closeoutArchive),
                launchArchive.map(DemoLiveTriggerLaunchPackageArchiveVo::id).orElse(null),
                launchArchive.map(DemoLiveTriggerLaunchPackageArchiveVo::archivedAt).orElse(null),
                closeoutArchive.map(DemoLiveTriggerOutcomeCloseoutArchiveVo::id).orElse(null),
                closeoutArchive.map(DemoLiveTriggerOutcomeCloseoutArchiveVo::archivedAt).orElse(null),
                closeoutArchive.map(DemoLiveTriggerOutcomeCloseoutArchiveVo::taskId).orElse(null),
                closeoutArchive.map(DemoLiveTriggerOutcomeCloseoutArchiveVo::taskStatus).orElse(null),
                closeoutArchive.map(DemoLiveTriggerOutcomeCloseoutArchiveVo::pullRequestUrl).orElse(null),
                closeoutArchive.map(DemoLiveTriggerOutcomeCloseoutArchiveVo::webhookDeliveryId).orElse(null),
                decision.summary(),
                decision.evidenceNotes(),
                decision.nextActions(),
                SIDE_EFFECT_CONTRACT,
                generatedAt,
                ""
        );
        return withReport(bundle);
    }

    private BundleDecision decide(
            Optional<DemoLiveTriggerLaunchPackageArchiveVo> launchArchive,
            Optional<DemoLiveTriggerOutcomeCloseoutArchiveVo> closeoutArchive
    ) {
        List<String> evidenceNotes = new ArrayList<>();
        List<String> nextActions = new ArrayList<>();
        if (launchArchive.isEmpty()) {
            evidenceNotes.add("No live trigger launch package archive was found.");
            nextActions.add("Create and archive a live trigger launch package before posting the GitHub issue comment.");
        }
        if (closeoutArchive.isEmpty()) {
            evidenceNotes.add("No live trigger outcome closeout archive was found.");
            nextActions.add("Generate and archive a live trigger outcome closeout after GitHub creates a PatchPilot task.");
        }
        if (launchArchive.isEmpty() || closeoutArchive.isEmpty()) {
            return new BundleDecision(
                    BLOCKED,
                    "Live demo evidence bundle is missing required launch package or outcome closeout archives.",
                    evidenceNotes,
                    nextActions
            );
        }

        DemoLiveTriggerLaunchPackageArchiveVo launch = launchArchive.get();
        DemoLiveTriggerOutcomeCloseoutArchiveVo closeout = closeoutArchive.get();
        if (!READY.equals(launch.status()) || !launch.readyToPost()) {
            evidenceNotes.add("Latest launch package archive " + launch.id() + " is not ready.");
            nextActions.add("Recreate the live trigger launch package after resolving launch package readiness gaps.");
            return new BundleDecision(
                    NEEDS_ATTENTION,
                    "Latest launch package archive is not ready for handoff.",
                    evidenceNotes,
                    nextActions
            );
        }
        if (!launch.id().equals(closeout.launchPackageArchiveId())) {
            evidenceNotes.add("Latest outcome closeout archive " + closeout.id()
                    + " references launch package archive " + valueOrMissing(closeout.launchPackageArchiveId()) + ".");
            nextActions.add("Generate and archive a new outcome closeout for launch package archive " + launch.id() + ".");
            return new BundleDecision(
                    NEEDS_ATTENTION,
                    "Latest outcome closeout archive does not match the latest launch package archive.",
                    evidenceNotes,
                    nextActions
            );
        }
        if (!READY.equals(closeout.status()) || !closeout.successful()) {
            evidenceNotes.add("Latest outcome closeout archive " + closeout.id() + " is not successful.");
            nextActions.add("Resolve the outcome closeout failure and archive a successful closeout for launch package archive "
                    + launch.id() + ".");
            return new BundleDecision(
                    NEEDS_ATTENTION,
                    "Latest outcome closeout archive is not successful.",
                    evidenceNotes,
                    nextActions
            );
        }

        evidenceNotes.add("Launch package archive " + launch.id() + " is ready.");
        evidenceNotes.add("Outcome closeout archive " + closeout.id() + " is successful.");
        evidenceNotes.addAll(closeout.evidenceNotes());
        nextActions.addAll(closeout.nextActions());
        if (nextActions.isEmpty()) {
            nextActions.add("Review the completed task and Pull Request before merging.");
        }
        return new BundleDecision(
                READY,
                "Live demo evidence bundle is ready for handoff.",
                evidenceNotes,
                nextActions
        );
    }

    private DemoLiveDemoEvidenceBundleVo withReport(DemoLiveDemoEvidenceBundleVo bundle) {
        return new DemoLiveDemoEvidenceBundleVo(
                bundle.status(),
                bundle.readyForHandoff(),
                bundle.repository(),
                bundle.issueNumber(),
                bundle.issueUrl(),
                bundle.triggerUser(),
                bundle.triggerComment(),
                bundle.launchPackageArchiveId(),
                bundle.launchPackageArchivedAt(),
                bundle.outcomeCloseoutArchiveId(),
                bundle.outcomeCloseoutArchivedAt(),
                bundle.taskId(),
                bundle.taskStatus(),
                bundle.pullRequestUrl(),
                bundle.webhookDeliveryId(),
                bundle.summary(),
                bundle.evidenceNotes(),
                bundle.nextActions(),
                bundle.sideEffectContract(),
                bundle.generatedAt(),
                markdownReport(bundle)
        );
    }

    private static String markdownReport(DemoLiveDemoEvidenceBundleVo bundle) {
        StringBuilder report = new StringBuilder();
        report.append("# PatchPilot Live Demo Evidence Bundle\n\n");
        report.append("- Status: `").append(bundle.status()).append("`\n");
        report.append("- Ready for handoff: `").append(bundle.readyForHandoff()).append("`\n");
        report.append("- Repository: ").append(valueOrMissing(bundle.repository())).append("\n");
        report.append("- Issue: #").append(bundle.issueNumber()).append("\n");
        report.append("- Issue URL: ").append(valueOrMissing(bundle.issueUrl())).append("\n");
        report.append("- Trigger user: ").append(valueOrMissing(bundle.triggerUser())).append("\n");
        report.append("- Trigger comment: `").append(valueOrMissing(bundle.triggerComment())).append("`\n");
        report.append("- Launch package archive: ").append(valueOrMissing(bundle.launchPackageArchiveId())).append("\n");
        report.append("- Launch package archived at: ").append(valueOrMissing(bundle.launchPackageArchivedAt())).append("\n");
        report.append("- Outcome closeout archive: ").append(valueOrMissing(bundle.outcomeCloseoutArchiveId())).append("\n");
        report.append("- Outcome closeout archived at: ").append(valueOrMissing(bundle.outcomeCloseoutArchivedAt())).append("\n");
        report.append("- Task: ").append(valueOrMissing(bundle.taskId())).append("\n");
        report.append("- Task status: ").append(valueOrMissing(bundle.taskStatus())).append("\n");
        report.append("- Webhook delivery: ").append(valueOrMissing(bundle.webhookDeliveryId())).append("\n");
        report.append("- Pull Request: ").append(valueOrMissing(bundle.pullRequestUrl())).append("\n");
        report.append("- Generated at: ").append(bundle.generatedAt()).append("\n\n");
        report.append("## Summary\n\n").append(bundle.summary()).append("\n\n");
        appendList(report, "Evidence", bundle.evidenceNotes());
        appendList(report, "Next Actions", bundle.nextActions());
        report.append("## Side Effect Contract\n\n").append(bundle.sideEffectContract()).append("\n");
        return report.toString();
    }

    private static void appendList(StringBuilder report, String title, List<String> items) {
        report.append("## ").append(title).append("\n\n");
        if (items.isEmpty()) {
            report.append("- None recorded.\n\n");
            return;
        }
        for (String item : items) {
            report.append("- ").append(item).append("\n");
        }
        report.append("\n");
    }

    private static String repository(
            Optional<DemoLiveTriggerLaunchPackageArchiveVo> launchArchive,
            Optional<DemoLiveTriggerOutcomeCloseoutArchiveVo> closeoutArchive
    ) {
        return launchArchive
                .map(DemoLiveTriggerLaunchPackageArchiveVo::repository)
                .or(() -> closeoutArchive.map(DemoLiveTriggerOutcomeCloseoutArchiveVo::repository))
                .orElse(null);
    }

    private static long issueNumber(
            Optional<DemoLiveTriggerLaunchPackageArchiveVo> launchArchive,
            Optional<DemoLiveTriggerOutcomeCloseoutArchiveVo> closeoutArchive
    ) {
        return launchArchive
                .map(DemoLiveTriggerLaunchPackageArchiveVo::issueNumber)
                .or(() -> closeoutArchive.map(DemoLiveTriggerOutcomeCloseoutArchiveVo::issueNumber))
                .orElse(0L);
    }

    private static String issueUrl(
            Optional<DemoLiveTriggerLaunchPackageArchiveVo> launchArchive,
            Optional<DemoLiveTriggerOutcomeCloseoutArchiveVo> closeoutArchive
    ) {
        return launchArchive
                .map(DemoLiveTriggerLaunchPackageArchiveVo::issueUrl)
                .or(() -> closeoutArchive.map(DemoLiveTriggerOutcomeCloseoutArchiveVo::issueUrl))
                .orElse(null);
    }

    private static String triggerUser(
            Optional<DemoLiveTriggerLaunchPackageArchiveVo> launchArchive,
            Optional<DemoLiveTriggerOutcomeCloseoutArchiveVo> closeoutArchive
    ) {
        return launchArchive
                .map(DemoLiveTriggerLaunchPackageArchiveVo::triggerUser)
                .or(() -> closeoutArchive.map(DemoLiveTriggerOutcomeCloseoutArchiveVo::triggerUser))
                .orElse(null);
    }

    private static String triggerComment(
            Optional<DemoLiveTriggerLaunchPackageArchiveVo> launchArchive,
            Optional<DemoLiveTriggerOutcomeCloseoutArchiveVo> closeoutArchive
    ) {
        return launchArchive
                .map(DemoLiveTriggerLaunchPackageArchiveVo::triggerComment)
                .or(() -> closeoutArchive.map(DemoLiveTriggerOutcomeCloseoutArchiveVo::triggerComment))
                .orElse(null);
    }

    private static String valueOrMissing(Object value) {
        return value == null ? "missing" : value.toString();
    }

    private record BundleDecision(
            String status,
            String summary,
            List<String> evidenceNotes,
            List<String> nextActions
    ) {
        private BundleDecision {
            evidenceNotes = List.copyOf(evidenceNotes);
            nextActions = List.copyOf(nextActions);
        }
    }
}
