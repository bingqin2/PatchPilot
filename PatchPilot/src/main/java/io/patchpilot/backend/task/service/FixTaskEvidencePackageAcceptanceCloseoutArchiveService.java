package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageAcceptanceCloseoutArchiveVo;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageFinalizationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class FixTaskEvidencePackageAcceptanceCloseoutArchiveService {

    private static final int MAX_ARCHIVES = 20;

    private final Supplier<FixTaskEvidencePackageFinalizationVo> finalizationSupplier;
    private final FixTaskEvidencePackageAcceptanceCloseoutArchiveRepository archiveRepository;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public FixTaskEvidencePackageAcceptanceCloseoutArchiveService(
            FixTaskEvidencePackageFinalizationService finalizationService,
            FixTaskEvidencePackageAcceptanceCloseoutArchiveRepository archiveRepository
    ) {
        this(
                finalizationService::getFinalizationGate,
                archiveRepository,
                Clock.systemUTC(),
                () -> UUID.randomUUID().toString()
        );
    }

    FixTaskEvidencePackageAcceptanceCloseoutArchiveService(
            Supplier<FixTaskEvidencePackageFinalizationVo> finalizationSupplier,
            FixTaskEvidencePackageAcceptanceCloseoutArchiveRepository archiveRepository,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.finalizationSupplier = finalizationSupplier;
        this.archiveRepository = archiveRepository;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public FixTaskEvidencePackageAcceptanceCloseoutArchiveVo archiveCurrentCloseout() {
        FixTaskEvidencePackageFinalizationVo finalization = finalizationSupplier.get();
        if (!finalization.finalized()) {
            throw new IllegalStateException("Task evidence finalization must be READY before archiving acceptance closeout");
        }
        Instant createdAt = Instant.now(clock);
        FixTaskEvidencePackageAcceptanceCloseoutArchiveVo archive =
                new FixTaskEvidencePackageAcceptanceCloseoutArchiveVo(
                        idSupplier.get(),
                        finalization.status(),
                        finalization.finalized(),
                        finalization.summary(),
                        finalization.latestArchiveId(),
                        finalization.latestTaskId(),
                        finalization.latestPullRequestUrl(),
                        finalization.latestDeliveryReceiptId(),
                        finalization.latestDeliveryTarget(),
                        finalization.latestDeliveryChannel(),
                        finalization.deliveryReceiptFreshness(),
                        createdAt,
                        formatArchiveReport(finalization, createdAt)
                );
        return archiveRepository.save(archive);
    }

    public List<FixTaskEvidencePackageAcceptanceCloseoutArchiveVo> listRecentArchives() {
        return archiveRepository.listRecentArchives(MAX_ARCHIVES);
    }

    public Optional<FixTaskEvidencePackageAcceptanceCloseoutArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }

    private static String formatArchiveReport(FixTaskEvidencePackageFinalizationVo finalization, Instant createdAt) {
        StringBuilder builder = new StringBuilder();
        builder.append("# PatchPilot Task Evidence Acceptance Closeout Archive\n\n");
        builder.append("- Status: `").append(finalization.status()).append("`\n");
        builder.append("- Accepted: `").append(finalization.finalized()).append("`\n");
        builder.append("- Latest archive: `").append(valueOrNone(finalization.latestArchiveId())).append("`\n");
        builder.append("- Latest task: `").append(valueOrNone(finalization.latestTaskId())).append("`\n");
        builder.append("- Latest Pull Request: ").append(valueOrNone(finalization.latestPullRequestUrl())).append('\n');
        builder.append("- Latest delivery receipt: `")
                .append(valueOrNone(finalization.latestDeliveryReceiptId()))
                .append("`\n");
        builder.append("- Latest delivery target: `")
                .append(valueOrNone(finalization.latestDeliveryTarget()))
                .append("`\n");
        builder.append("- Latest delivery channel: `")
                .append(valueOrNone(finalization.latestDeliveryChannel()))
                .append("`\n");
        builder.append("- Delivery receipt freshness: `")
                .append(finalization.deliveryReceiptFreshness())
                .append("`\n");
        builder.append("- Archived at: `").append(createdAt).append("`\n\n");
        builder.append("## Summary\n\n").append(finalization.summary()).append("\n\n");
        builder.append("## Next Action\n\n").append(finalization.nextAction()).append("\n\n");
        builder.append("## Embedded Finalization Report\n\n");
        builder.append(finalization.markdownReport()).append('\n');
        builder.append("\n## Side-Effect Contract\n\n");
        builder.append("POST /api/tasks/evidence-packages/acceptance-closeout/archives stores PatchPilot-local acceptance evidence only: it does not create tasks, call the model, run tests, mutate Git, send messages, record delivery receipts, or write to GitHub.\n");
        return builder.toString();
    }

    private static String valueOrNone(String value) {
        return value == null || value.isBlank() ? "none" : value;
    }
}
