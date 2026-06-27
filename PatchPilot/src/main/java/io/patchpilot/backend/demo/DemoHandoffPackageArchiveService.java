package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveSummaryVo;
import io.patchpilot.backend.demo.domain.DemoHandoffReadinessVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoSessionSnapshotVo;
import io.patchpilot.backend.demo.service.DemoHandoffPackageArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class DemoHandoffPackageArchiveService {

    private static final int MAX_ARCHIVES = 20;

    private final DemoSessionReportService demoSessionReportService;
    private final DemoHandoffPackageArchiveRepository archiveRepository;
    private final Supplier<DemoSessionSnapshotVo> snapshotSupplier;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public DemoHandoffPackageArchiveService(
            DemoSessionReportService demoSessionReportService,
            DemoHandoffPackageArchiveRepository archiveRepository,
            DemoSessionSnapshotService demoSessionSnapshotService
    ) {
        this(
                demoSessionReportService,
                archiveRepository,
                demoSessionSnapshotService::getSessionSnapshot,
                Clock.systemUTC(),
                () -> UUID.randomUUID().toString()
        );
    }

    DemoHandoffPackageArchiveService(
            DemoSessionReportService demoSessionReportService,
            DemoHandoffPackageArchiveRepository archiveRepository,
            Supplier<DemoSessionSnapshotVo> snapshotSupplier,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.demoSessionReportService = demoSessionReportService;
        this.archiveRepository = archiveRepository;
        this.snapshotSupplier = snapshotSupplier;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public DemoHandoffPackageArchiveVo archiveCurrentHandoffPackage() {
        return archiveCurrentHandoffPackage(new DemoSessionReportRequestDto(List.of()));
    }

    public DemoHandoffPackageArchiveVo archiveCurrentHandoffPackage(DemoSessionReportRequestDto request) {
        DemoSessionSnapshotVo snapshot = snapshotSupplier.get();
        DemoHandoffReadinessVo handoffReadiness = DemoSessionReportService.handoffReadiness(snapshot, request);
        DemoHandoffPackageArchiveVo archive = new DemoHandoffPackageArchiveVo(
                idSupplier.get(),
                snapshot.sessionId(),
                snapshot.status(),
                snapshot.summary(),
                handoffReadiness.status(),
                handoffReadiness.summary(),
                handoffReadiness.nextAction(),
                countChecks(handoffReadiness, DemoReadinessStatus.READY),
                countChecks(handoffReadiness, DemoReadinessStatus.NEEDS_ATTENTION),
                countChecks(handoffReadiness, DemoReadinessStatus.BLOCKED),
                snapshot.shareSummary(),
                snapshot.evidenceBundle().recentPullRequestUrl(),
                Instant.now(clock),
                demoSessionReportService.formatHandoffPackage(snapshot, request)
        );
        return archiveRepository.save(archive);
    }

    private static int countChecks(DemoHandoffReadinessVo handoffReadiness, DemoReadinessStatus status) {
        return (int) handoffReadiness.checks().stream()
                .filter(check -> check.status() == status)
                .count();
    }

    public List<DemoHandoffPackageArchiveVo> listRecentArchives() {
        return archiveRepository.listRecentArchives(MAX_ARCHIVES);
    }

    public DemoHandoffPackageArchiveSummaryVo getArchiveSummary() {
        List<DemoHandoffPackageArchiveVo> archives = archiveRepository.listRecentArchives(MAX_ARCHIVES);
        if (archives.isEmpty()) {
            String nextAction = "Archive a demo handoff package after a completed live run before sharing handoff evidence.";
            return new DemoHandoffPackageArchiveSummaryVo(
                    "NO_ARCHIVE",
                    false,
                    0,
                    null,
                    null,
                    null,
                    null,
                    "No handoff package archive has been captured.",
                    nextAction,
                    formatSummaryReport("NO_ARCHIVE", false, 0, null, null, null, null, "No handoff package archive has been captured.", nextAction)
            );
        }

        DemoHandoffPackageArchiveVo latest = archives.get(0);
        boolean shareReady = latest.handoffReadinessStatus() == DemoReadinessStatus.READY;
        String status = latest.handoffReadinessStatus().name();
        String summary = shareReady
                ? "Latest archived handoff package is READY and can be shared."
                : "Latest archived handoff package needs attention before it is shared.";
        return new DemoHandoffPackageArchiveSummaryVo(
                status,
                shareReady,
                archives.size(),
                latest.id(),
                latest.sessionId(),
                latest.handoffReadinessStatus(),
                latest.createdAt(),
                summary,
                latest.handoffReadinessNextAction(),
                formatSummaryReport(
                        status,
                        shareReady,
                        archives.size(),
                        latest.id(),
                        latest.sessionId(),
                        latest.handoffReadinessStatus(),
                        latest.createdAt(),
                        summary,
                        latest.handoffReadinessNextAction()
                )
        );
    }

    public Optional<DemoHandoffPackageArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }

    private static String formatSummaryReport(
            String status,
            boolean shareReady,
            int archiveCount,
            String latestArchiveId,
            String latestSessionId,
            DemoReadinessStatus latestHandoffReadinessStatus,
            Instant latestCreatedAt,
            String summary,
            String nextAction
    ) {
        return """
                # PatchPilot Handoff Package Archive Summary

                - Status: `%s`
                - Share ready: `%s`
                - Archive count: `%d`
                - Latest archive: `%s`
                - Latest session: `%s`
                - Latest handoff readiness: `%s`
                - Latest created at: `%s`

                ## Summary

                %s

                ## Next Action

                %s

                ## Side-Effect Contract

                GET /api/demo/handoff-package-archives/summary is read-only: it does not create tasks, call the model, run tests, mutate Git, or write to GitHub.
                """.formatted(
                status,
                shareReady,
                archiveCount,
                valueOrNone(latestArchiveId),
                valueOrNone(latestSessionId),
                latestHandoffReadinessStatus == null ? "none" : latestHandoffReadinessStatus.name(),
                latestCreatedAt == null ? "none" : latestCreatedAt,
                summary,
                nextAction
        );
    }

    private static String valueOrNone(String value) {
        return value == null || value.isBlank() ? "none" : value;
    }
}
