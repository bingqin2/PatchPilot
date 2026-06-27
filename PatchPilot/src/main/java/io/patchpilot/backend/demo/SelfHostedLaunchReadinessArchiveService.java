package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoSelfHostedLaunchCheckVo;
import io.patchpilot.backend.demo.domain.DemoSelfHostedLaunchReadinessArchiveVo;
import io.patchpilot.backend.demo.domain.DemoSelfHostedLaunchReadinessVo;
import io.patchpilot.backend.demo.service.DemoSelfHostedLaunchReadinessArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class SelfHostedLaunchReadinessArchiveService {

    private static final int MAX_ARCHIVES = 20;

    private final Supplier<DemoSelfHostedLaunchReadinessVo> readinessSupplier;
    private final DemoSelfHostedLaunchReadinessArchiveRepository archiveRepository;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public SelfHostedLaunchReadinessArchiveService(
            SelfHostedLaunchReadinessService readinessService,
            DemoSelfHostedLaunchReadinessArchiveRepository archiveRepository
    ) {
        this(
                readinessService::getReadinessPackage,
                archiveRepository,
                Clock.systemUTC(),
                () -> UUID.randomUUID().toString()
        );
    }

    SelfHostedLaunchReadinessArchiveService(
            Supplier<DemoSelfHostedLaunchReadinessVo> readinessSupplier,
            DemoSelfHostedLaunchReadinessArchiveRepository archiveRepository,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.readinessSupplier = readinessSupplier;
        this.archiveRepository = archiveRepository;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public DemoSelfHostedLaunchReadinessArchiveVo archiveCurrentReadinessPackage() {
        DemoSelfHostedLaunchReadinessVo readiness = readinessSupplier.get();
        List<DemoSelfHostedLaunchCheckVo> checks = readiness.checks();
        DemoSelfHostedLaunchReadinessArchiveVo archive = new DemoSelfHostedLaunchReadinessArchiveVo(
                idSupplier.get(),
                readiness.status(),
                readiness.readyToLaunch(),
                readiness.summary(),
                countChecks(checks, DemoReadinessStatus.READY),
                countChecks(checks, DemoReadinessStatus.NEEDS_ATTENTION),
                countChecks(checks, DemoReadinessStatus.BLOCKED),
                Instant.now(clock),
                readiness.markdownReport()
        );
        return archiveRepository.save(archive);
    }

    public List<DemoSelfHostedLaunchReadinessArchiveVo> listRecentArchives() {
        return archiveRepository.listRecentArchives(MAX_ARCHIVES);
    }

    public Optional<DemoSelfHostedLaunchReadinessArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }

    private static int countChecks(List<DemoSelfHostedLaunchCheckVo> checks, DemoReadinessStatus status) {
        return (int) checks.stream()
                .filter(check -> check.status() == status)
                .count();
    }
}
