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
    private final DemoHandoffPackageArchiveSummaryService summaryService;
    private final DemoHandoffPackageArchiveRepository archiveRepository;
    private final Supplier<DemoSessionSnapshotVo> snapshotSupplier;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public DemoHandoffPackageArchiveService(
            DemoSessionReportService demoSessionReportService,
            DemoHandoffPackageArchiveSummaryService summaryService,
            DemoHandoffPackageArchiveRepository archiveRepository,
            DemoSessionSnapshotService demoSessionSnapshotService
    ) {
        this(
                demoSessionReportService,
                summaryService,
                archiveRepository,
                demoSessionSnapshotService::getSessionSnapshot,
                Clock.systemUTC(),
                () -> UUID.randomUUID().toString()
        );
    }

    DemoHandoffPackageArchiveService(
            DemoSessionReportService demoSessionReportService,
            DemoHandoffPackageArchiveSummaryService summaryService,
            DemoHandoffPackageArchiveRepository archiveRepository,
            Supplier<DemoSessionSnapshotVo> snapshotSupplier,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.demoSessionReportService = demoSessionReportService;
        this.summaryService = summaryService;
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
        return summaryService.getArchiveSummary();
    }

    public Optional<DemoHandoffPackageArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }
}
