package io.patchpilot.backend.security.exposure;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureCloseoutVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureHandoffPackageVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureSessionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
public class ExternalExposureCloseoutService {

    private static final String STATUS_READY = "READY";
    private static final String STATUS_BLOCKED = "BLOCKED";
    private static final String STATUS_NEEDS_ATTENTION = "NEEDS_ATTENTION";
    private static final String SESSION_ACTIVE = "ACTIVE";
    private static final String SESSION_CLOSED = "CLOSED";
    private static final String DOWNLOAD_ACTION = "GET /api/security/external-exposure-closeout/report/download";
    private static final String SIDE_EFFECT_CONTRACT = """
            GET /api/security/external-exposure-closeout is read-only: it does not create tasks, \
            does not call the model, does not run tests, does not probe public URLs, does not mutate Git, \
            does not create branches, does not open Pull Requests, does not edit GitHub webhook settings, \
            does not write GitHub comments, does not archive records, and does not expose secrets.\
            """;

    private final Supplier<List<ExternalExposureSessionVo>> sessionsSupplier;
    private final Supplier<ExternalExposureHandoffPackageVo> handoffPackageSupplier;
    private final Clock clock;

    @Autowired
    public ExternalExposureCloseoutService(
            ExternalExposureSessionService sessionService,
            ExternalExposureHandoffPackageService handoffPackageService
    ) {
        this(sessionService::listRecentSessions, handoffPackageService::getHandoffPackage, Clock.systemUTC());
    }

    ExternalExposureCloseoutService(
            Supplier<List<ExternalExposureSessionVo>> sessionsSupplier,
            Supplier<ExternalExposureHandoffPackageVo> handoffPackageSupplier,
            Clock clock
    ) {
        this.sessionsSupplier = sessionsSupplier;
        this.handoffPackageSupplier = handoffPackageSupplier;
        this.clock = clock;
    }

    public ExternalExposureCloseoutVo getCloseout() {
        List<ExternalExposureSessionVo> sessions = sessionsSupplier.get();
        ExternalExposureSessionVo latestSession = sessions == null || sessions.isEmpty() ? null : sessions.get(0);
        ExternalExposureHandoffPackageVo handoffPackage = handoffPackageSupplier.get();
        Instant generatedAt = Instant.now(clock);

        List<String> evidenceNotes = evidenceNotes(latestSession, handoffPackage);
        List<String> nextActions = nextActions(latestSession, handoffPackage);
        int blockedCount = blockedCount(latestSession);
        int needsAttentionCount = needsAttentionCount(latestSession, handoffPackage);
        int readyCount = readyCount(latestSession, handoffPackage);
        int totalCount = readyCount + needsAttentionCount + blockedCount;
        String status = status(latestSession, handoffPackage, needsAttentionCount, blockedCount);
        boolean closeoutReady = STATUS_READY.equals(status);
        String summary = summary(latestSession, status);
        String nextAction = nextActions.isEmpty()
                ? "Keep the closeout report with the demo evidence bundle."
                : nextActions.get(0);
        List<String> downloadActions = List.of(DOWNLOAD_ACTION);
        String markdownReport = markdownReport(
                status,
                summary,
                nextAction,
                latestSession,
                handoffPackage,
                readyCount,
                needsAttentionCount,
                blockedCount,
                totalCount,
                evidenceNotes,
                nextActions,
                downloadActions,
                generatedAt
        );

        return new ExternalExposureCloseoutVo(
                status,
                closeoutReady,
                summary,
                nextAction,
                latestSession == null ? null : latestSession.id(),
                latestSession == null ? null : latestSession.status(),
                latestSession == null ? null : latestSession.publicUrl(),
                latestSession == null ? null : latestSession.webhookUrl(),
                latestSession == null ? null : latestSession.purpose(),
                latestSession == null ? null : latestSession.operator(),
                latestSession == null ? null : latestSession.startedAt(),
                latestSession == null ? null : latestSession.closedBy(),
                latestSession == null ? null : latestSession.closedAt(),
                latestSession == null ? null : latestSession.closeNotes(),
                latestSession == null ? null : latestSession.linkedReadinessArchiveId(),
                handoffPackage == null ? null : handoffPackage.status(),
                handoffPackage == null ? null : handoffPackage.archiveFreshness(),
                readyCount,
                needsAttentionCount,
                blockedCount,
                totalCount,
                nextActions,
                evidenceNotes,
                downloadActions,
                SIDE_EFFECT_CONTRACT,
                generatedAt,
                markdownReport
        );
    }

    private static String status(
            ExternalExposureSessionVo latestSession,
            ExternalExposureHandoffPackageVo handoffPackage,
            int needsAttentionCount,
            int blockedCount
    ) {
        if (latestSession != null && SESSION_ACTIVE.equals(latestSession.status())) {
            return STATUS_BLOCKED;
        }
        if (blockedCount > 0) {
            return STATUS_BLOCKED;
        }
        if (latestSession == null || needsAttentionCount > 0 || !handoffReady(handoffPackage)) {
            return STATUS_NEEDS_ATTENTION;
        }
        return STATUS_READY;
    }

    private static int readyCount(ExternalExposureSessionVo latestSession, ExternalExposureHandoffPackageVo handoffPackage) {
        int count = 0;
        if (latestSession != null && SESSION_CLOSED.equals(latestSession.status())) {
            count++;
        }
        if (latestSession != null && hasText(latestSession.closedBy())
                && latestSession.closedAt() != null
                && hasText(latestSession.closeNotes())) {
            count++;
        }
        if (latestSession != null && hasText(latestSession.linkedReadinessArchiveId())) {
            count++;
        }
        if (handoffReady(handoffPackage)) {
            count++;
        }
        return count;
    }

    private static int needsAttentionCount(
            ExternalExposureSessionVo latestSession,
            ExternalExposureHandoffPackageVo handoffPackage
    ) {
        int count = 0;
        if (latestSession == null) {
            return 1;
        }
        if (SESSION_CLOSED.equals(latestSession.status())
                && (!hasText(latestSession.closedBy())
                || latestSession.closedAt() == null
                || !hasText(latestSession.closeNotes()))) {
            count++;
        }
        if (!hasText(latestSession.linkedReadinessArchiveId())) {
            count++;
        }
        if (!handoffReady(handoffPackage)) {
            count++;
        }
        return count;
    }

    private static int blockedCount(ExternalExposureSessionVo latestSession) {
        return latestSession != null && SESSION_ACTIVE.equals(latestSession.status()) ? 1 : 0;
    }

    private static List<String> evidenceNotes(
            ExternalExposureSessionVo latestSession,
            ExternalExposureHandoffPackageVo handoffPackage
    ) {
        List<String> notes = new ArrayList<>();
        if (latestSession == null) {
            notes.add("No external exposure session has been recorded.");
        } else {
            notes.add("Latest session " + latestSession.id() + " is " + latestSession.status() + ".");
            if (hasText(latestSession.closedBy())
                    && latestSession.closedAt() != null
                    && hasText(latestSession.closeNotes())) {
                notes.add("Session close evidence includes closedBy, closedAt, and closeNotes.");
            } else if (SESSION_CLOSED.equals(latestSession.status())) {
                notes.add("Session close notes are missing.");
            }
            if (hasText(latestSession.linkedReadinessArchiveId())) {
                notes.add("Session is linked to readiness archive " + latestSession.linkedReadinessArchiveId() + ".");
            } else {
                notes.add("Session is not linked to a readiness archive.");
            }
        }
        if (handoffPackage == null) {
            notes.add("Current external exposure handoff package is missing.");
        } else {
            notes.add("Current external exposure handoff package is " + handoffPackage.status() + ".");
        }
        return notes;
    }

    private static List<String> nextActions(
            ExternalExposureSessionVo latestSession,
            ExternalExposureHandoffPackageVo handoffPackage
    ) {
        List<String> actions = new ArrayList<>();
        if (latestSession == null) {
            actions.add("Record an exposure session when a temporary public URL is shared, then close it after the tunnel is stopped.");
            return actions;
        }
        if (SESSION_ACTIVE.equals(latestSession.status())) {
            actions.add("Stop the temporary tunnel, remove or rotate the GitHub webhook payload URL if needed, and close the active exposure session.");
        } else if (SESSION_CLOSED.equals(latestSession.status())
                && (!hasText(latestSession.closedBy())
                || latestSession.closedAt() == null
                || !hasText(latestSession.closeNotes()))) {
            actions.add("Add close notes that explain how the tunnel and webhook exposure were shut down.");
        }
        if (!hasText(latestSession.linkedReadinessArchiveId())) {
            actions.add("Create or link readiness archive evidence before relying on closeout.");
        }
        if (!handoffReady(handoffPackage)) {
            actions.add("Refresh external exposure readiness and handoff evidence so closeout is tied to current safety state.");
        }
        if (actions.isEmpty()) {
            actions.add("Keep the closeout report with the demo evidence bundle and rotate or remove the temporary webhook URL if it was configured in GitHub.");
        }
        return actions.stream().distinct().toList();
    }

    private static String summary(ExternalExposureSessionVo latestSession, String status) {
        if (latestSession == null) {
            return "No external exposure session has been recorded.";
        }
        if (SESSION_ACTIVE.equals(latestSession.status())) {
            return "External exposure is still active.";
        }
        if (STATUS_READY.equals(status)) {
            return "External exposure session is closed with complete local evidence.";
        }
        return "External exposure closeout needs attention before it can be treated as complete.";
    }

    private static String markdownReport(
            String status,
            String summary,
            String nextAction,
            ExternalExposureSessionVo latestSession,
            ExternalExposureHandoffPackageVo handoffPackage,
            int readyCount,
            int needsAttentionCount,
            int blockedCount,
            int totalCount,
            List<String> evidenceNotes,
            List<String> nextActions,
            List<String> downloadActions,
            Instant generatedAt
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("# PatchPilot External Exposure Closeout\n\n");
        builder.append("- Status: ").append(status).append('\n');
        builder.append("- Summary: ").append(summary).append('\n');
        builder.append("- Next action: ").append(nextAction).append('\n');
        builder.append("- Latest session: ").append(latestSession == null ? "none" : latestSession.id()).append('\n');
        builder.append("- Latest session status: ").append(latestSession == null ? "none" : latestSession.status()).append('\n');
        builder.append("- Linked readiness archive: ")
                .append(latestSession == null ? "none" : valueOrNone(latestSession.linkedReadinessArchiveId()))
                .append('\n');
        builder.append("- Handoff status: ").append(handoffPackage == null ? "none" : handoffPackage.status()).append('\n');
        builder.append("- Archive freshness: ").append(handoffPackage == null ? "none" : handoffPackage.archiveFreshness()).append('\n');
        builder.append("- Ready checks: ").append(readyCount).append('/').append(totalCount).append('\n');
        builder.append("- Attention checks: ").append(needsAttentionCount).append('\n');
        builder.append("- Blocked checks: ").append(blockedCount).append('\n');
        builder.append("- Generated at: ").append(generatedAt).append("\n\n");
        builder.append("## Evidence Notes\n\n");
        for (String note : evidenceNotes) {
            builder.append("- ").append(note).append('\n');
        }
        builder.append("\n## Next Actions\n\n");
        for (String action : nextActions) {
            builder.append("- ").append(action).append('\n');
        }
        builder.append("\n## Download Actions\n\n");
        for (String action : downloadActions) {
            builder.append("- ").append(action).append('\n');
        }
        builder.append("\n## Side Effect Contract\n\n");
        builder.append(SIDE_EFFECT_CONTRACT).append('\n');
        return builder.toString();
    }

    private static boolean handoffReady(ExternalExposureHandoffPackageVo handoffPackage) {
        return handoffPackage != null && "READY".equals(handoffPackage.status()) && handoffPackage.handoffReady();
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static String valueOrNone(String value) {
        return hasText(value) ? value.trim() : "none";
    }
}
