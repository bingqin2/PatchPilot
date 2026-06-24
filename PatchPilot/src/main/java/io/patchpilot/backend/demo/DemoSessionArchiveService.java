package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoSessionArchiveVo;
import io.patchpilot.backend.demo.domain.DemoSessionSnapshotVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

@Service
public class DemoSessionArchiveService {

    private static final int MAX_ARCHIVES = 20;

    private final DemoSessionReportService demoSessionReportService;
    private final Supplier<DemoSessionSnapshotVo> snapshotSupplier;
    private final Clock clock;
    private final Supplier<String> idSupplier;
    private final List<DemoSessionArchiveVo> archives = new CopyOnWriteArrayList<>();

    @Autowired
    public DemoSessionArchiveService(
            DemoSessionReportService demoSessionReportService,
            DemoSessionSnapshotService demoSessionSnapshotService
    ) {
        this(demoSessionReportService, demoSessionSnapshotService::getSessionSnapshot, Clock.systemUTC(), () -> UUID.randomUUID().toString());
    }

    DemoSessionArchiveService(
            DemoSessionReportService demoSessionReportService,
            Supplier<DemoSessionSnapshotVo> snapshotSupplier,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.demoSessionReportService = demoSessionReportService;
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
        archives.add(0, archive);
        trimArchives();
        return archive;
    }

    public List<DemoSessionArchiveVo> listRecentArchives() {
        return archives.stream()
                .limit(MAX_ARCHIVES)
                .toList();
    }

    private void trimArchives() {
        while (archives.size() > MAX_ARCHIVES) {
            archives.remove(archives.size() - 1);
        }
    }
}
