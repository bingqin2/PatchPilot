package io.patchpilot.backend.security.exposure;

import io.patchpilot.backend.demo.config.DemoProperties;
import io.patchpilot.backend.github.credential.GitHubLivePublishPreflightService;
import io.patchpilot.backend.github.credential.domain.GitHubLivePublishPreflightVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureCloseoutArchiveVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureHandoffPackageVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureOperatorHandoffChecklistCheckVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureOperatorHandoffChecklistVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureSessionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
public class ExternalExposureOperatorHandoffChecklistService {

    private static final String READY = "READY";
    private static final String NEEDS_ATTENTION = "NEEDS_ATTENTION";
    private static final String BLOCKED = "BLOCKED";
    private static final String ACTIVE = "ACTIVE";
    private static final String DOWNLOAD_ACTION = "GET /api/security/external-exposure-operator-handoff-checklist/report/download";
    private static final String SIDE_EFFECT_CONTRACT = """
            GET /api/security/external-exposure-operator-handoff-checklist is read-only: it does not create tasks, \
            does not call the model, does not run tests, does not probe public URLs, does not mutate Git, \
            does not create branches, does not open Pull Requests, does not edit GitHub webhook settings, \
            does not write GitHub comments, does not archive records, does not send messages, and does not expose secrets.\
            """;

    private final Supplier<List<ExternalExposureCloseoutArchiveVo>> closeoutArchivesSupplier;
    private final Supplier<ExternalExposureHandoffPackageVo> handoffPackageSupplier;
    private final Supplier<List<ExternalExposureSessionVo>> sessionsSupplier;
    private final Supplier<GitHubLivePublishPreflightVo> livePublishPreflightSupplier;
    private final DemoProperties demoProperties;
    private final Supplier<Instant> nowSupplier;

    @Autowired
    public ExternalExposureOperatorHandoffChecklistService(
            ExternalExposureCloseoutArchiveService closeoutArchiveService,
            ExternalExposureHandoffPackageService handoffPackageService,
            ExternalExposureSessionService sessionService,
            GitHubLivePublishPreflightService livePublishPreflightService,
            DemoProperties demoProperties
    ) {
        this(
                closeoutArchiveService::listRecentArchives,
                handoffPackageService::getHandoffPackage,
                sessionService::listRecentSessions,
                () -> livePublishPreflightService.getPreflight(
                        demoProperties.getRepositoryOwner(),
                        demoProperties.getRepositoryName()
                ),
                demoProperties,
                Instant::now
        );
    }

    ExternalExposureOperatorHandoffChecklistService(
            Supplier<List<ExternalExposureCloseoutArchiveVo>> closeoutArchivesSupplier,
            Supplier<ExternalExposureHandoffPackageVo> handoffPackageSupplier,
            Supplier<List<ExternalExposureSessionVo>> sessionsSupplier,
            Supplier<GitHubLivePublishPreflightVo> livePublishPreflightSupplier,
            DemoProperties demoProperties,
            Supplier<Instant> nowSupplier
    ) {
        this.closeoutArchivesSupplier = closeoutArchivesSupplier;
        this.handoffPackageSupplier = handoffPackageSupplier;
        this.sessionsSupplier = sessionsSupplier;
        this.livePublishPreflightSupplier = livePublishPreflightSupplier;
        this.demoProperties = demoProperties;
        this.nowSupplier = nowSupplier;
    }

    public ExternalExposureOperatorHandoffChecklistVo getChecklist() {
        List<ExternalExposureCloseoutArchiveVo> closeoutArchives = safeList(closeoutArchivesSupplier.get());
        ExternalExposureCloseoutArchiveVo latestArchive = closeoutArchives.isEmpty() ? null : closeoutArchives.get(0);
        ExternalExposureHandoffPackageVo handoffPackage = handoffPackageSupplier.get();
        List<ExternalExposureSessionVo> sessions = safeList(sessionsSupplier.get());
        GitHubLivePublishPreflightVo publishPreflight = livePublishPreflightSupplier.get();
        int activeSessionCount = (int) sessions.stream()
                .filter(session -> ACTIVE.equals(session.status()))
                .count();

        List<ExternalExposureOperatorHandoffChecklistCheckVo> checks = checks(
                latestArchive,
                handoffPackage,
                activeSessionCount,
                publishPreflight
        );
        String status = aggregateStatus(checks);
        List<String> nextActions = nextActions(status, latestArchive, handoffPackage, activeSessionCount, publishPreflight);
        List<String> evidenceNotes = evidenceNotes(latestArchive, handoffPackage, activeSessionCount, publishPreflight);
        Instant generatedAt = nowSupplier.get();
        String summary = summary(status);
        String nextAction = nextActions.isEmpty() ? "Keep external exposure handoff evidence with the demo package." : nextActions.get(0);
        String repository = repository();
        String markdownReport = markdownReport(status, summary, nextAction, repository, latestArchive, handoffPackage,
                activeSessionCount, publishPreflight, checks, evidenceNotes, nextActions, generatedAt);
        int readyCount = (int) checks.stream().filter(check -> READY.equals(check.status())).count();
        int needsAttentionCount = (int) checks.stream().filter(check -> NEEDS_ATTENTION.equals(check.status())).count();
        int blockedCount = (int) checks.stream().filter(check -> BLOCKED.equals(check.status())).count();

        return new ExternalExposureOperatorHandoffChecklistVo(
                status,
                READY.equals(status),
                summary,
                nextAction,
                repository,
                latestArchive == null ? null : latestArchive.id(),
                latestArchive == null ? null : latestArchive.latestSessionId(),
                latestArchive == null ? null : latestArchive.latestSessionStatus(),
                latestArchive == null ? null : latestArchive.publicUrl(),
                latestArchive == null ? null : latestArchive.webhookUrl(),
                handoffPackage == null ? null : handoffPackage.status(),
                latestArchive == null ? null : latestArchive.archiveFreshness(),
                publishPreflight == null ? null : publishPreflight.status(),
                publishPreflight != null && publishPreflight.livePublishReady(),
                activeSessionCount,
                readyCount,
                needsAttentionCount,
                blockedCount,
                checks.size(),
                nextActions,
                evidenceNotes,
                List.of(DOWNLOAD_ACTION),
                SIDE_EFFECT_CONTRACT,
                checks,
                generatedAt,
                markdownReport
        );
    }

    private static List<ExternalExposureOperatorHandoffChecklistCheckVo> checks(
            ExternalExposureCloseoutArchiveVo latestArchive,
            ExternalExposureHandoffPackageVo handoffPackage,
            int activeSessionCount,
            GitHubLivePublishPreflightVo publishPreflight
    ) {
        List<ExternalExposureOperatorHandoffChecklistCheckVo> checks = new ArrayList<>();
        checks.add(closeoutArchiveCheck(latestArchive));
        checks.add(handoffPackageCheck(handoffPackage));
        checks.add(activeSessionCheck(activeSessionCount));
        checks.add(livePublishCheck(publishPreflight));
        return checks;
    }

    private static ExternalExposureOperatorHandoffChecklistCheckVo closeoutArchiveCheck(
            ExternalExposureCloseoutArchiveVo latestArchive
    ) {
        if (latestArchive == null) {
            return new ExternalExposureOperatorHandoffChecklistCheckVo(
                    "Closeout archive",
                    NEEDS_ATTENTION,
                    "No external exposure closeout archive has been recorded.",
                    "Archive the current external exposure closeout before relying on handoff evidence."
            );
        }
        String status = readyFlagStatus(latestArchive.status(), latestArchive.closeoutReady());
        return new ExternalExposureOperatorHandoffChecklistCheckVo(
                "Closeout archive",
                status,
                "Latest closeout archive " + latestArchive.id() + " is " + latestArchive.status() + ".",
                READY.equals(status) && latestArchive.closeoutReady()
                        ? "Ready."
                        : "Create a READY closeout archive after closing the temporary public URL."
        );
    }

    private static ExternalExposureOperatorHandoffChecklistCheckVo handoffPackageCheck(
            ExternalExposureHandoffPackageVo handoffPackage
    ) {
        if (handoffPackage == null) {
            return new ExternalExposureOperatorHandoffChecklistCheckVo(
                    "Exposure handoff package",
                    NEEDS_ATTENTION,
                    "Current external exposure handoff package is missing.",
                    "Refresh external exposure readiness and handoff evidence."
            );
        }
        String status = readyFlagStatus(handoffPackage.status(), handoffPackage.handoffReady());
        return new ExternalExposureOperatorHandoffChecklistCheckVo(
                "Exposure handoff package",
                status,
                "External exposure handoff package is " + handoffPackage.status() + ".",
                handoffPackage.handoffReady() && READY.equals(status) ? "Ready." : handoffPackage.nextAction()
        );
    }

    private static ExternalExposureOperatorHandoffChecklistCheckVo activeSessionCheck(int activeSessionCount) {
        if (activeSessionCount > 0) {
            return new ExternalExposureOperatorHandoffChecklistCheckVo(
                    "Active exposure sessions",
                    BLOCKED,
                    activeSessionCount + " active external exposure session" + plural(activeSessionCount) + " remain open.",
                    "Close active external exposure sessions before posting another live /agent fix."
            );
        }
        return new ExternalExposureOperatorHandoffChecklistCheckVo(
                "Active exposure sessions",
                READY,
                "No active external exposure sessions.",
                "Ready."
        );
    }

    private static ExternalExposureOperatorHandoffChecklistCheckVo livePublishCheck(
            GitHubLivePublishPreflightVo publishPreflight
    ) {
        if (publishPreflight == null) {
            return new ExternalExposureOperatorHandoffChecklistCheckVo(
                    "Live GitHub publish preflight",
                    NEEDS_ATTENTION,
                    "Live GitHub publish preflight has not been loaded.",
                    "Run live GitHub publish preflight before posting a real /agent fix."
            );
        }
        String status = readyFlagStatus(publishPreflight.status(), publishPreflight.livePublishReady());
        return new ExternalExposureOperatorHandoffChecklistCheckVo(
                "Live GitHub publish preflight",
                status,
                publishPreflight.summary(),
                publishPreflight.livePublishReady() && READY.equals(status) ? "Ready." : publishPreflight.nextAction()
        );
    }

    private static String aggregateStatus(List<ExternalExposureOperatorHandoffChecklistCheckVo> checks) {
        if (checks.stream().anyMatch(check -> BLOCKED.equals(check.status()))) {
            return BLOCKED;
        }
        if (checks.stream().anyMatch(check -> NEEDS_ATTENTION.equals(check.status()))) {
            return NEEDS_ATTENTION;
        }
        return READY;
    }

    private static List<String> nextActions(
            String status,
            ExternalExposureCloseoutArchiveVo latestArchive,
            ExternalExposureHandoffPackageVo handoffPackage,
            int activeSessionCount,
            GitHubLivePublishPreflightVo publishPreflight
    ) {
        if (READY.equals(status)) {
            return List.of("Post the prepared /agent fix comment only after confirming the GitHub webhook payload URL still points to the intended backend.");
        }
        List<String> actions = new ArrayList<>();
        if (latestArchive == null) {
            actions.add("Archive the current external exposure closeout before relying on handoff evidence.");
        } else if (!READY.equals(normalizeStatus(latestArchive.status())) || !latestArchive.closeoutReady()) {
            actions.add("Create a READY closeout archive after closing the temporary public URL.");
        }
        if (handoffPackage == null || !handoffPackage.handoffReady()) {
            actions.add(handoffPackage == null ? "Refresh external exposure readiness and handoff evidence." : handoffPackage.nextAction());
        }
        if (activeSessionCount > 0) {
            actions.add("Close active external exposure sessions before posting another live /agent fix.");
        }
        if (publishPreflight == null || !publishPreflight.livePublishReady()) {
            actions.add(publishPreflight == null
                    ? "Run live GitHub publish preflight before posting a real /agent fix."
                    : publishPreflight.nextAction());
        }
        actions.add("Rerun this checklist before the next live GitHub issue-to-PR step.");
        return actions.stream().filter(ExternalExposureOperatorHandoffChecklistService::hasText).distinct().toList();
    }

    private static List<String> evidenceNotes(
            ExternalExposureCloseoutArchiveVo latestArchive,
            ExternalExposureHandoffPackageVo handoffPackage,
            int activeSessionCount,
            GitHubLivePublishPreflightVo publishPreflight
    ) {
        List<String> notes = new ArrayList<>();
        if (latestArchive == null) {
            notes.add("No closeout archive is available.");
        } else {
            notes.add("Latest closeout archive " + latestArchive.id() + " is " + latestArchive.status() + ".");
            if (hasText(latestArchive.latestSessionId())) {
                notes.add("Closeout archive points to session " + latestArchive.latestSessionId() + ".");
            }
        }
        if (handoffPackage == null) {
            notes.add("External exposure handoff package is missing.");
        } else {
            notes.add("External exposure handoff package is " + handoffPackage.status() + ".");
        }
        notes.add(activeSessionCount == 0
                ? "No active external exposure sessions remain open."
                : activeSessionCount + " active external exposure session" + plural(activeSessionCount) + " remain open.");
        if (publishPreflight == null) {
            notes.add("Live GitHub publish preflight is missing.");
        } else {
            notes.add("Live GitHub publish preflight is " + publishPreflight.status() + ".");
        }
        return notes;
    }

    private String repository() {
        String owner = trim(demoProperties.getRepositoryOwner());
        String repositoryName = trim(demoProperties.getRepositoryName());
        if (!hasText(owner) || !hasText(repositoryName)) {
            return "";
        }
        return owner + "/" + repositoryName;
    }

    private static String summary(String status) {
        return switch (status) {
            case READY -> "External exposure evidence is closed and ready for the next live step.";
            case BLOCKED -> "External exposure handoff is blocked before the next live step.";
            default -> "External exposure handoff needs attention before the next live step.";
        };
    }

    private static String markdownReport(
            String status,
            String summary,
            String nextAction,
            String repository,
            ExternalExposureCloseoutArchiveVo latestArchive,
            ExternalExposureHandoffPackageVo handoffPackage,
            int activeSessionCount,
            GitHubLivePublishPreflightVo publishPreflight,
            List<ExternalExposureOperatorHandoffChecklistCheckVo> checks,
            List<String> evidenceNotes,
            List<String> nextActions,
            Instant generatedAt
    ) {
        StringBuilder report = new StringBuilder();
        report.append("# PatchPilot External Exposure Operator Handoff Checklist\n\n");
        report.append("- Status: ").append(status).append("\n");
        report.append("- Summary: ").append(summary).append("\n");
        report.append("- Repository: ").append(hasText(repository) ? repository : "not configured").append("\n");
        report.append("- Closeout archive: ").append(latestArchive == null ? "none" : latestArchive.id()).append("\n");
        report.append("- Session: ").append(latestArchive == null ? "none" : latestArchive.latestSessionId()).append("\n");
        report.append("- Handoff status: ").append(handoffPackage == null ? "missing" : handoffPackage.status()).append("\n");
        report.append("- Active exposure sessions: ").append(activeSessionCount).append("\n");
        report.append("- Live publish status: ").append(publishPreflight == null ? "missing" : publishPreflight.status()).append("\n");
        report.append("- Generated at: ").append(generatedAt).append("\n");
        report.append("- Next action: ").append(nextAction).append("\n\n");
        report.append("## Checks\n\n");
        for (ExternalExposureOperatorHandoffChecklistCheckVo check : checks) {
            report.append("- ").append(check.name()).append(": ").append(check.status())
                    .append(" - ").append(check.summary()).append("\n");
        }
        report.append("\n## Evidence Notes\n\n");
        for (String note : evidenceNotes) {
            report.append("- ").append(note).append("\n");
        }
        report.append("\n## Next Actions\n\n");
        for (String action : nextActions) {
            report.append("- ").append(action).append("\n");
        }
        report.append("\n## Side-Effect Contract\n\n");
        report.append(SIDE_EFFECT_CONTRACT).append("\n");
        return report.toString();
    }

    private static String normalizeStatus(String status) {
        if (READY.equals(status) || NEEDS_ATTENTION.equals(status) || BLOCKED.equals(status)) {
            return status;
        }
        return NEEDS_ATTENTION;
    }

    private static String readyFlagStatus(String status, boolean ready) {
        String normalized = normalizeStatus(status);
        if (BLOCKED.equals(normalized)) {
            return BLOCKED;
        }
        return READY.equals(normalized) && ready ? READY : NEEDS_ATTENTION;
    }

    private static String plural(int count) {
        return count == 1 ? "" : "s";
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private static <T> List<T> safeList(List<T> values) {
        return values == null ? List.of() : values;
    }
}
