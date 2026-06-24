package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoSessionArchiveVo;
import io.patchpilot.backend.demo.domain.DemoSessionSnapshotVo;
import io.patchpilot.backend.demo.service.DemoSessionArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class DemoSessionArchiveService {

    private static final int MAX_ARCHIVES = 20;

    private final DemoSessionReportService demoSessionReportService;
    private final DemoSessionArchiveRepository archiveRepository;
    private final Supplier<DemoSessionSnapshotVo> snapshotSupplier;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public DemoSessionArchiveService(
            DemoSessionReportService demoSessionReportService,
            DemoSessionArchiveRepository archiveRepository,
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

    DemoSessionArchiveService(
            DemoSessionReportService demoSessionReportService,
            DemoSessionArchiveRepository archiveRepository,
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

    public DemoSessionArchiveVo archiveCurrentSession() {
        DemoSessionSnapshotVo snapshot = snapshotSupplier.get();
        DemoSessionArchiveVo archive = new DemoSessionArchiveVo(
                idSupplier.get(),
                snapshot.sessionId(),
                snapshot.status(),
                snapshot.summary(),
                snapshot.shareSummary(),
                snapshot.evidenceBundle().recentPullRequestUrl(),
                Instant.now(clock),
                demoSessionReportService.formatSessionReport(snapshot)
        );
        return archiveRepository.save(archive);
    }

    public List<DemoSessionArchiveVo> listRecentArchives() {
        return archiveRepository.listRecentArchives(MAX_ARCHIVES);
    }

    public Optional<DemoSessionArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }
}
