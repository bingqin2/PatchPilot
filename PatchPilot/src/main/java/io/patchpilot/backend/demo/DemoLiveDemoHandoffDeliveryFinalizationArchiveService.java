package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoHandoffDeliveryFinalizationArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoHandoffDeliveryFinalizationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class DemoLiveDemoHandoffDeliveryFinalizationArchiveService {

    private static final int MAX_ARCHIVES = 20;

    private final DemoLiveDemoHandoffDeliveryFinalizationService finalizationService;
    private final DemoLiveDemoHandoffDeliveryFinalizationArchiveRepository archiveRepository;
    private final Supplier<String> idSupplier;
    private final Supplier<Instant> nowSupplier;

    @Autowired
    public DemoLiveDemoHandoffDeliveryFinalizationArchiveService(
            DemoLiveDemoHandoffDeliveryFinalizationService finalizationService,
            DemoLiveDemoHandoffDeliveryFinalizationArchiveRepository archiveRepository
    ) {
        this(finalizationService, archiveRepository, () -> UUID.randomUUID().toString(), Instant::now);
    }

    DemoLiveDemoHandoffDeliveryFinalizationArchiveService(
            DemoLiveDemoHandoffDeliveryFinalizationService finalizationService,
            DemoLiveDemoHandoffDeliveryFinalizationArchiveRepository archiveRepository,
            Supplier<String> idSupplier,
            Supplier<Instant> nowSupplier
    ) {
        this.finalizationService = finalizationService;
        this.archiveRepository = archiveRepository;
        this.idSupplier = idSupplier;
        this.nowSupplier = nowSupplier;
    }

    public DemoLiveDemoHandoffDeliveryFinalizationArchiveVo archiveFinalization() {
        DemoLiveDemoHandoffDeliveryFinalizationVo finalization =
                finalizationService.getFinalizationGate();
        if (!finalization.finalized() || !"READY".equals(finalization.status())) {
            throw new IllegalStateException(
                    "READY live demo handoff delivery finalization is required before archiving."
            );
        }
        Instant archivedAt = nowSupplier.get();
        DemoLiveDemoHandoffDeliveryFinalizationArchiveVo archive =
                new DemoLiveDemoHandoffDeliveryFinalizationArchiveVo(
                        idSupplier.get(),
                        finalization.status(),
                        finalization.finalized(),
                        finalization.summary(),
                        finalization.nextAction(),
                        finalization.latestDeliveryReceiptId(),
                        finalization.evidenceBundleArchiveId(),
                        finalization.repository(),
                        finalization.issueNumber(),
                        finalization.issueUrl(),
                        finalization.taskId(),
                        finalization.taskStatus(),
                        finalization.pullRequestUrl(),
                        finalization.latestDeliveryTarget(),
                        finalization.latestDeliveryChannel(),
                        finalization.latestDeliveredAt(),
                        finalization.deliveryReceiptFreshness(),
                        finalization.deliveryReceiptFresh(),
                        finalization.deliveryReceiptFreshnessSummary(),
                        finalization.checks(),
                        finalization.evidenceNotes(),
                        archiveDownloadActions(finalization),
                        archiveSideEffectContract(finalization.sideEffectContract()),
                        finalization.generatedAt(),
                        archivedAt,
                        markdownReport(finalization, archivedAt)
                );
        return archiveRepository.save(archive);
    }

    public List<DemoLiveDemoHandoffDeliveryFinalizationArchiveVo> listRecentArchives() {
        return archiveRepository.listRecentArchives(MAX_ARCHIVES);
    }

    public Optional<DemoLiveDemoHandoffDeliveryFinalizationArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }

    private static List<String> archiveDownloadActions(
            DemoLiveDemoHandoffDeliveryFinalizationVo finalization
    ) {
        return List.copyOf(
                java.util.stream.Stream.concat(
                        finalization.downloadActions().stream(),
                        java.util.stream.Stream.of("Download live demo handoff delivery finalization archive report.")
                ).toList()
        );
    }

    private static String archiveSideEffectContract(String finalizationContract) {
        return "Archive creation writes only PatchPilot local archive records. " + finalizationContract;
    }

    private static String markdownReport(
            DemoLiveDemoHandoffDeliveryFinalizationVo finalization,
            Instant archivedAt
    ) {
        StringBuilder report = new StringBuilder();
        report.append("# PatchPilot Live Demo Handoff Delivery Finalization Archive\n\n");
        report.append("- Status: `").append(finalization.status()).append("`\n");
        report.append("- Finalized: `").append(finalization.finalized()).append("`\n");
        report.append("- Latest delivery receipt: `")
                .append(valueOrMissing(finalization.latestDeliveryReceiptId())).append("`\n");
        report.append("- Evidence bundle archive: `")
                .append(valueOrMissing(finalization.evidenceBundleArchiveId())).append("`\n");
        report.append("- Repository: ").append(valueOrMissing(finalization.repository())).append("\n");
        report.append("- Issue: #").append(finalization.issueNumber()).append("\n");
        report.append("- Issue URL: ").append(valueOrMissing(finalization.issueUrl())).append("\n");
        report.append("- Task: `").append(valueOrMissing(finalization.taskId())).append("`\n");
        report.append("- Task status: `").append(valueOrMissing(finalization.taskStatus())).append("`\n");
        report.append("- Pull Request: ").append(valueOrMissing(finalization.pullRequestUrl())).append("\n");
        report.append("- Delivery receipt freshness: `")
                .append(finalization.deliveryReceiptFreshness()).append("`\n");
        report.append("- Finalization generated at: `").append(finalization.generatedAt()).append("`\n");
        report.append("- Archived at: `").append(archivedAt).append("`\n\n");
        report.append("## Summary\n\n").append(finalization.summary()).append("\n\n");
        report.append("## Next Action\n\n").append(finalization.nextAction()).append("\n\n");
        appendChecks(report, finalization.checks());
        appendList(report, "Evidence Notes", finalization.evidenceNotes());
        appendList(report, "Download Actions", archiveDownloadActions(finalization));
        report.append("## Side Effect Contract\n\n")
                .append(archiveSideEffectContract(finalization.sideEffectContract()))
                .append("\n\n");
        report.append("## Frozen Finalization Report\n\n");
        report.append(finalization.markdownReport());
        return report.toString();
    }

    private static void appendChecks(
            StringBuilder report,
            List<DemoLiveDemoHandoffDeliveryFinalizationVo.Check> checks
    ) {
        report.append("## Checks\n\n");
        for (DemoLiveDemoHandoffDeliveryFinalizationVo.Check check : checks) {
            report.append("- `").append(check.status()).append("` ")
                    .append(check.name()).append(": ")
                    .append(check.summary())
                    .append(" Next: ")
                    .append(check.nextAction())
                    .append("\n");
        }
        report.append("\n");
    }

    private static void appendList(StringBuilder report, String heading, List<String> items) {
        report.append("## ").append(heading).append("\n\n");
        for (String item : items) {
            report.append("- ").append(item).append("\n");
        }
        report.append("\n");
    }

    private static String valueOrMissing(String value) {
        return value == null || value.isBlank() ? "missing" : value;
    }
}
