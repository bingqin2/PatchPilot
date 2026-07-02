package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoEvidenceBundleArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoEvidenceBundleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class DemoLiveDemoEvidenceBundleArchiveService {

    private static final int MAX_ARCHIVES = 20;

    private final DemoLiveDemoEvidenceBundleService bundleService;
    private final DemoLiveDemoEvidenceBundleArchiveRepository archiveRepository;
    private final Supplier<String> idSupplier;
    private final Supplier<Instant> nowSupplier;

    @Autowired
    public DemoLiveDemoEvidenceBundleArchiveService(
            DemoLiveDemoEvidenceBundleService bundleService,
            DemoLiveDemoEvidenceBundleArchiveRepository archiveRepository
    ) {
        this(bundleService, archiveRepository, () -> UUID.randomUUID().toString(), Instant::now);
    }

    DemoLiveDemoEvidenceBundleArchiveService(
            DemoLiveDemoEvidenceBundleService bundleService,
            DemoLiveDemoEvidenceBundleArchiveRepository archiveRepository,
            Supplier<String> idSupplier,
            Supplier<Instant> nowSupplier
    ) {
        this.bundleService = bundleService;
        this.archiveRepository = archiveRepository;
        this.idSupplier = idSupplier;
        this.nowSupplier = nowSupplier;
    }

    public DemoLiveDemoEvidenceBundleArchiveVo archiveBundle() {
        DemoLiveDemoEvidenceBundleVo bundle = bundleService.createBundle();
        Instant archivedAt = nowSupplier.get();
        DemoLiveDemoEvidenceBundleArchiveVo archive = new DemoLiveDemoEvidenceBundleArchiveVo(
                idSupplier.get(),
                bundle.status(),
                bundle.readyForHandoff(),
                bundle.repository(),
                bundle.issueNumber(),
                bundle.issueUrl(),
                bundle.triggerUser(),
                bundle.triggerComment(),
                bundle.launchPackageArchiveId(),
                bundle.launchPackageArchivedAt(),
                bundle.outcomeCloseoutArchiveId(),
                bundle.outcomeCloseoutArchivedAt(),
                bundle.taskId(),
                bundle.taskStatus(),
                bundle.pullRequestUrl(),
                bundle.webhookDeliveryId(),
                bundle.summary(),
                bundle.evidenceNotes(),
                bundle.nextActions(),
                archiveSideEffectContract(bundle.sideEffectContract()),
                bundle.generatedAt(),
                archivedAt,
                markdownReport(bundle, archivedAt)
        );
        return archiveRepository.save(archive);
    }

    public List<DemoLiveDemoEvidenceBundleArchiveVo> listRecentArchives() {
        return archiveRepository.listRecentArchives(MAX_ARCHIVES);
    }

    public Optional<DemoLiveDemoEvidenceBundleArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }

    private static String archiveSideEffectContract(String bundleContract) {
        return "Archive creation writes only PatchPilot local archive records. " + bundleContract;
    }

    private static String markdownReport(DemoLiveDemoEvidenceBundleVo bundle, Instant archivedAt) {
        StringBuilder report = new StringBuilder();
        report.append("# PatchPilot Live Demo Evidence Bundle Archive\n\n");
        report.append("- Status: `").append(bundle.status()).append("`\n");
        report.append("- Ready for handoff: `").append(bundle.readyForHandoff()).append("`\n");
        report.append("- Repository: ").append(valueOrMissing(bundle.repository())).append("\n");
        report.append("- Issue: #").append(bundle.issueNumber()).append("\n");
        report.append("- Issue URL: ").append(valueOrMissing(bundle.issueUrl())).append("\n");
        report.append("- Trigger user: ").append(valueOrMissing(bundle.triggerUser())).append("\n");
        report.append("- Trigger comment: `").append(valueOrMissing(bundle.triggerComment())).append("`\n");
        report.append("- Launch package archive: ").append(valueOrMissing(bundle.launchPackageArchiveId())).append("\n");
        report.append("- Outcome closeout archive: ").append(valueOrMissing(bundle.outcomeCloseoutArchiveId())).append("\n");
        report.append("- Task: ").append(valueOrMissing(bundle.taskId())).append("\n");
        report.append("- Task status: ").append(valueOrMissing(bundle.taskStatus())).append("\n");
        report.append("- Webhook delivery: ").append(valueOrMissing(bundle.webhookDeliveryId())).append("\n");
        report.append("- Pull Request: ").append(valueOrMissing(bundle.pullRequestUrl())).append("\n");
        report.append("- Bundle generated at: ").append(bundle.generatedAt()).append("\n");
        report.append("- Archived at: ").append(archivedAt).append("\n\n");
        report.append("## Summary\n\n").append(bundle.summary()).append("\n\n");
        appendList(report, "Evidence", bundle.evidenceNotes());
        appendList(report, "Next Actions", bundle.nextActions());
        report.append("## Side Effect Contract\n\n")
                .append(archiveSideEffectContract(bundle.sideEffectContract()))
                .append("\n");
        return report.toString();
    }

    private static void appendList(StringBuilder report, String title, List<String> items) {
        report.append("## ").append(title).append("\n\n");
        if (items.isEmpty()) {
            report.append("- None recorded.\n\n");
            return;
        }
        for (String item : items) {
            report.append("- ").append(item).append("\n");
        }
        report.append("\n");
    }

    private static String valueOrMissing(Object value) {
        return value == null ? "missing" : value.toString();
    }
}
