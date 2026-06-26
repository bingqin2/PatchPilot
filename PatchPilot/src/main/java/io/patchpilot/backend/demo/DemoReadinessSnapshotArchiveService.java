package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoReadinessCheckVo;
import io.patchpilot.backend.demo.domain.DemoReadinessSnapshotArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import io.patchpilot.backend.demo.service.DemoReadinessSnapshotArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class DemoReadinessSnapshotArchiveService {

    private static final int MAX_ARCHIVES = 20;
    static final String SIDE_EFFECT_CONTRACT = "Archiving demo readiness stores PatchPilot-local evidence only; "
            + "it does not create tasks, call the model, clone repositories, run verification commands, "
            + "mutate Git, or write to GitHub.";

    private final Supplier<DemoReadinessVo> readinessSupplier;
    private final DemoReadinessSnapshotArchiveRepository archiveRepository;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public DemoReadinessSnapshotArchiveService(
            DemoReadinessService demoReadinessService,
            DemoReadinessSnapshotArchiveRepository archiveRepository
    ) {
        this(
                demoReadinessService::getReadiness,
                archiveRepository,
                Clock.systemUTC(),
                () -> UUID.randomUUID().toString()
        );
    }

    DemoReadinessSnapshotArchiveService(
            Supplier<DemoReadinessVo> readinessSupplier,
            DemoReadinessSnapshotArchiveRepository archiveRepository,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.readinessSupplier = readinessSupplier;
        this.archiveRepository = archiveRepository;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public DemoReadinessSnapshotArchiveVo archiveCurrentReadiness() {
        DemoReadinessVo readiness = readinessSupplier.get();
        Instant createdAt = Instant.now(clock);
        DemoReadinessSnapshotArchiveVo archive = new DemoReadinessSnapshotArchiveVo(
                idSupplier.get(),
                readiness.status(),
                readiness.summary(),
                countChecks(readiness.checks(), DemoReadinessStatus.READY),
                countChecks(readiness.checks(), DemoReadinessStatus.NEEDS_ATTENTION),
                countChecks(readiness.checks(), DemoReadinessStatus.BLOCKED),
                createdAt,
                formatReport(readiness, createdAt)
        );
        return archiveRepository.save(archive);
    }

    public List<DemoReadinessSnapshotArchiveVo> listRecentArchives() {
        return archiveRepository.listRecentArchives(MAX_ARCHIVES);
    }

    public Optional<DemoReadinessSnapshotArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }

    private static int countChecks(List<DemoReadinessCheckVo> checks, DemoReadinessStatus status) {
        return (int) checks.stream()
                .filter(check -> check.status() == status)
                .count();
    }

    private static String formatReport(DemoReadinessVo readiness, Instant createdAt) {
        StringBuilder report = new StringBuilder();
        List<DemoReadinessCheckVo> checks = readiness.checks();
        report.append("# PatchPilot Demo Readiness Snapshot\n\n");
        report.append("- Created at: `").append(createdAt).append("`\n");
        report.append("- Status: `").append(readiness.status()).append("`\n");
        report.append("- Summary: ").append(readiness.summary()).append("\n");
        report.append("- Ready checks: `").append(countChecks(checks, DemoReadinessStatus.READY)).append("`\n");
        report.append("- Needs attention checks: `").append(countChecks(checks, DemoReadinessStatus.NEEDS_ATTENTION)).append("`\n");
        report.append("- Blocked checks: `").append(countChecks(checks, DemoReadinessStatus.BLOCKED)).append("`\n\n");
        report.append("## Checks\n\n");
        if (checks.isEmpty()) {
            report.append("- No readiness checks recorded.\n");
        } else {
            checks.forEach(check -> {
                report.append("- `")
                        .append(check.status())
                        .append("` ")
                        .append(check.name())
                        .append(": ")
                        .append(check.message())
                        .append("\n");
                report.append("  - Action: ").append(check.action()).append("\n");
            });
        }
        report.append("\n## Next Actions\n\n");
        if (readiness.nextActions().isEmpty()) {
            report.append("- No action needed.\n");
        } else {
            readiness.nextActions().forEach(action -> report.append("- ").append(action).append("\n"));
        }
        report.append("\n## Side Effect Contract\n\n");
        report.append(SIDE_EFFECT_CONTRACT).append("\n");
        return report.toString();
    }
}
