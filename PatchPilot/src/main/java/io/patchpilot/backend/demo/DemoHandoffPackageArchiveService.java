package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveVo;
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
        DemoHandoffPackageArchiveVo archive = new DemoHandoffPackageArchiveVo(
                idSupplier.get(),
                snapshot.sessionId(),
                snapshot.status(),
                snapshot.summary(),
                snapshot.shareSummary(),
                snapshot.evidenceBundle().recentPullRequestUrl(),
                Instant.now(clock),
                demoSessionReportService.formatHandoffPackage(snapshot, request)
        );
        return archiveRepository.save(archive);
    }

    public List<DemoHandoffPackageArchiveVo> listRecentArchives() {
        return archiveRepository.listRecentArchives(MAX_ARCHIVES);
    }

    public Optional<DemoHandoffPackageArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }
}
