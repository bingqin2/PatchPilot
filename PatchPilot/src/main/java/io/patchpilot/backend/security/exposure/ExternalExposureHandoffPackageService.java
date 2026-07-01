package io.patchpilot.backend.security.exposure;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureHandoffPackageVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureReadinessArchiveVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureReadinessVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
public class ExternalExposureHandoffPackageService {

    private static final String STATUS_READY = "READY";
    private static final String STATUS_BLOCKED = "BLOCKED";
    private static final String STATUS_NEEDS_ATTENTION = "NEEDS_ATTENTION";
    private static final String ARCHIVE_CURRENT = "CURRENT";
    private static final String ARCHIVE_MISSING = "MISSING";
    private static final String ARCHIVE_STALE = "STALE";
    private static final String REPORT_DOWNLOAD_ACTION =
            "GET /api/security/external-exposure-handoff-package/report/download";
    private static final String SIDE_EFFECT_CONTRACT = """
            GET /api/security/external-exposure-handoff-package is read-only: it does not create tasks, \
            does not call the model, does not run tests, does not probe the network, does not mutate Git, \
            does not create branches, does not open Pull Requests, does not write GitHub comments, \
            does not archive records, does not mutate GitHub, and does not expose secrets.\
            """;

    private final Supplier<ExternalExposureReadinessVo> readinessSupplier;
    private final ExternalExposureReadinessArchiveService archiveService;
    private final Clock clock;

    @Autowired
    public ExternalExposureHandoffPackageService(
            ExternalExposureReadinessProvider readinessProvider,
            ExternalExposureReadinessArchiveService archiveService
    ) {
        this(readinessProvider::getReadiness, archiveService, Clock.systemUTC());
    }

    ExternalExposureHandoffPackageService(
            Supplier<ExternalExposureReadinessVo> readinessSupplier,
            ExternalExposureReadinessArchiveService archiveService,
            Clock clock
    ) {
        this.readinessSupplier = readinessSupplier;
        this.archiveService = archiveService;
        this.clock = clock;
    }

    public ExternalExposureHandoffPackageVo getHandoffPackage() {
        ExternalExposureReadinessVo readiness = readinessSupplier.get();
        ExternalExposureReadinessArchiveVo latestArchive = archiveService.listRecentArchives()
                .stream()
                .findFirst()
                .orElse(null);
        Instant generatedAt = Instant.now(clock);
        String archiveFreshness = archiveFreshness(readiness, latestArchive);
        String status = status(readiness, latestArchive, archiveFreshness);
        boolean handoffReady = STATUS_READY.equals(status);
        List<String> nextActions = nextActions(readiness, latestArchive, archiveFreshness, handoffReady);
        List<String> evidenceNotes = evidenceNotes(readiness, latestArchive, archiveFreshness);
        String summary = summary(status, archiveFreshness);
        String nextAction = nextActions.isEmpty() ? "Start the tunnel and keep monitoring." : nextActions.get(0);
        List<String> downloadActions = List.of(REPORT_DOWNLOAD_ACTION);
        String markdownReport = markdownReport(
                status,
                summary,
                nextAction,
                readiness,
                latestArchive,
                archiveFreshness,
                nextActions,
                evidenceNotes,
                downloadActions,
                generatedAt
        );

        return new ExternalExposureHandoffPackageVo(
                status,
                handoffReady,
                summary,
                nextAction,
                readiness.status(),
                readiness.safeToExpose(),
                readiness.readyCount(),
                readiness.needsAttentionCount(),
                readiness.blockedCount(),
                readiness.totalCount(),
                latestArchive == null ? null : latestArchive.id(),
                latestArchive == null ? null : latestArchive.status(),
                latestArchive == null ? null : latestArchive.safeToExpose(),
                latestArchive == null ? null : latestArchive.createdAt(),
                archiveFreshness,
                nextActions,
                evidenceNotes,
                downloadActions,
                SIDE_EFFECT_CONTRACT,
                generatedAt,
                markdownReport
        );
    }

    private static String status(
            ExternalExposureReadinessVo readiness,
            ExternalExposureReadinessArchiveVo latestArchive,
            String archiveFreshness
    ) {
        if (!readiness.safeToExpose() || STATUS_BLOCKED.equals(readiness.status())) {
            return STATUS_BLOCKED;
        }
        if (latestArchive == null) {
            return STATUS_BLOCKED;
        }
        if (!latestArchive.safeToExpose()) {
            return STATUS_BLOCKED;
        }
        if (!ARCHIVE_CURRENT.equals(archiveFreshness)) {
            return STATUS_NEEDS_ATTENTION;
        }
        return STATUS_READY;
    }

    private static String archiveFreshness(
            ExternalExposureReadinessVo readiness,
            ExternalExposureReadinessArchiveVo latestArchive
    ) {
        if (latestArchive == null) {
            return ARCHIVE_MISSING;
        }
        if (readiness.status().equals(latestArchive.status())
                && readiness.safeToExpose() == latestArchive.safeToExpose()
                && readiness.readyCount() == latestArchive.readyCount()
                && readiness.needsAttentionCount() == latestArchive.needsAttentionCount()
                && readiness.blockedCount() == latestArchive.blockedCount()
                && readiness.totalCount() == latestArchive.totalCount()) {
            return ARCHIVE_CURRENT;
        }
        return ARCHIVE_STALE;
    }

    private static List<String> nextActions(
            ExternalExposureReadinessVo readiness,
            ExternalExposureReadinessArchiveVo latestArchive,
            String archiveFreshness,
            boolean handoffReady
    ) {
        if (handoffReady) {
            return List.of("Start the temporary tunnel, share the current payload URL, and monitor webhook deliveries, rejected triggers, queue health, and exposure archives.");
        }
        List<String> actions = new ArrayList<>();
        if (!readiness.safeToExpose() || STATUS_BLOCKED.equals(readiness.status())) {
            actions.addAll(readiness.nextActions());
        }
        if (latestArchive == null) {
            actions.add("Archive the current external exposure readiness result before sharing the public URL.");
        } else if (!ARCHIVE_CURRENT.equals(archiveFreshness)) {
            actions.add("Refresh the external exposure readiness archive so the handoff package matches the current gate.");
        }
        if (latestArchive != null && !latestArchive.safeToExpose()) {
            actions.add("Do not share the public URL until the latest archived exposure evidence is safe to expose.");
        }
        return actions.stream().distinct().toList();
    }

    private static List<String> evidenceNotes(
            ExternalExposureReadinessVo readiness,
            ExternalExposureReadinessArchiveVo latestArchive,
            String archiveFreshness
    ) {
        List<String> notes = new ArrayList<>();
        notes.add("Current readiness status is " + readiness.status() + " with " + readiness.readyCount()
                + " ready, " + readiness.needsAttentionCount() + " attention, and "
                + readiness.blockedCount() + " blocked checks.");
        if (latestArchive == null) {
            notes.add("No external exposure readiness archive has been recorded for this handoff.");
        } else {
            notes.add("Latest archive " + latestArchive.id() + " captures " + latestArchive.status()
                    + " readiness evidence.");
            notes.add("Archive freshness is " + archiveFreshness + ".");
        }
        return notes;
    }

    private static String summary(String status, String archiveFreshness) {
        if (STATUS_READY.equals(status)) {
            return "External exposure handoff package is ready to share.";
        }
        if (ARCHIVE_MISSING.equals(archiveFreshness)) {
            return "External exposure handoff package is missing archived evidence.";
        }
        if (ARCHIVE_STALE.equals(archiveFreshness)) {
            return "External exposure handoff package needs a fresh readiness archive.";
        }
        return "External exposure handoff package is blocked by readiness safeguards.";
    }

    private static String markdownReport(
            String status,
            String summary,
            String nextAction,
            ExternalExposureReadinessVo readiness,
            ExternalExposureReadinessArchiveVo latestArchive,
            String archiveFreshness,
            List<String> nextActions,
            List<String> evidenceNotes,
            List<String> downloadActions,
            Instant generatedAt
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("# PatchPilot External Exposure Handoff Package\n\n");
        builder.append("- Status: ").append(status).append("\n");
        builder.append("- Summary: ").append(summary).append("\n");
        builder.append("- Next action: ").append(nextAction).append("\n");
        builder.append("- Current readiness: ").append(readiness.status()).append("\n");
        builder.append("- Current safe to expose: ").append(readiness.safeToExpose()).append("\n");
        builder.append("- Latest archive: ")
                .append(latestArchive == null ? "missing" : latestArchive.id())
                .append("\n");
        builder.append("- Archive freshness: ").append(archiveFreshness).append("\n");
        builder.append("- Generated at: ").append(generatedAt).append("\n\n");
        builder.append("## Evidence Notes\n\n");
        for (String note : evidenceNotes) {
            builder.append("- ").append(note).append("\n");
        }
        builder.append("\n## Next Actions\n\n");
        for (String action : nextActions) {
            builder.append("- ").append(action).append("\n");
        }
        builder.append("\n## Download Actions\n\n");
        for (String action : downloadActions) {
            builder.append("- ").append(action).append("\n");
        }
        builder.append("\n## Side Effect Contract\n\n");
        builder.append(SIDE_EFFECT_CONTRACT).append("\n");
        return builder.toString();
    }
}
