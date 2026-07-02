package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoReviewerDeliveryCenterArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoReviewerDeliveryCenterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class DemoLiveDemoReviewerDeliveryCenterArchiveService {

    private static final int MAX_ARCHIVES = 20;

    private final Supplier<DemoLiveDemoReviewerDeliveryCenterVo> centerSupplier;
    private final DemoLiveDemoReviewerDeliveryCenterArchiveRepository archiveRepository;
    private final Supplier<String> idSupplier;
    private final Supplier<Instant> nowSupplier;

    @Autowired
    public DemoLiveDemoReviewerDeliveryCenterArchiveService(
            DemoLiveDemoReviewerDeliveryCenterService centerService,
            DemoLiveDemoReviewerDeliveryCenterArchiveRepository archiveRepository
    ) {
        this(centerService::getCenter, archiveRepository, () -> UUID.randomUUID().toString(), Instant::now);
    }

    DemoLiveDemoReviewerDeliveryCenterArchiveService(
            Supplier<DemoLiveDemoReviewerDeliveryCenterVo> centerSupplier,
            DemoLiveDemoReviewerDeliveryCenterArchiveRepository archiveRepository,
            Supplier<String> idSupplier,
            Supplier<Instant> nowSupplier
    ) {
        this.centerSupplier = centerSupplier;
        this.archiveRepository = archiveRepository;
        this.idSupplier = idSupplier;
        this.nowSupplier = nowSupplier;
    }

    public DemoLiveDemoReviewerDeliveryCenterArchiveVo archiveCurrentCenter() {
        DemoLiveDemoReviewerDeliveryCenterVo center = centerSupplier.get();
        if (!center.deliverable() || !"READY".equals(center.status())) {
            throw new IllegalStateException(
                    "READY live demo reviewer delivery center is required before archiving."
            );
        }
        Instant archivedAt = nowSupplier.get();
        DemoLiveDemoReviewerDeliveryCenterArchiveVo archive =
                new DemoLiveDemoReviewerDeliveryCenterArchiveVo(
                        idSupplier.get(),
                        center.status(),
                        center.deliverable(),
                        center.summary(),
                        center.nextAction(),
                        center.repository(),
                        center.issueNumber(),
                        center.issueUrl(),
                        center.taskId(),
                        center.taskStatus(),
                        center.pullRequestUrl(),
                        center.readinessCards(),
                        center.blockers(),
                        center.evidenceLinks(),
                        archiveDownloadActions(center),
                        archiveSideEffectContract(center.sideEffectContract()),
                        center.generatedAt(),
                        archivedAt,
                        markdownReport(center, archivedAt)
                );
        return archiveRepository.save(archive);
    }

    public List<DemoLiveDemoReviewerDeliveryCenterArchiveVo> listRecentArchives() {
        return archiveRepository.listRecentArchives(MAX_ARCHIVES);
    }

    public Optional<DemoLiveDemoReviewerDeliveryCenterArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }

    private static List<String> archiveDownloadActions(DemoLiveDemoReviewerDeliveryCenterVo center) {
        return java.util.stream.Stream.concat(
                center.downloadActions().stream(),
                java.util.stream.Stream.of("Download live demo reviewer delivery center archive report.")
        ).distinct().toList();
    }

    private static String archiveSideEffectContract(String centerContract) {
        return "Archive creation writes only PatchPilot local reviewer delivery center archive records. "
                + centerContract;
    }

    private static String markdownReport(
            DemoLiveDemoReviewerDeliveryCenterVo center,
            Instant archivedAt
    ) {
        StringBuilder report = new StringBuilder();
        report.append("# PatchPilot Live Demo Reviewer Delivery Center Archive\n\n");
        report.append("- Status: `").append(center.status()).append("`\n");
        report.append("- Deliverable: `").append(center.deliverable()).append("`\n");
        report.append("- Repository: ").append(valueOrMissing(center.repository())).append("\n");
        report.append("- Issue: #").append(center.issueNumber()).append("\n");
        report.append("- Issue URL: ").append(valueOrMissing(center.issueUrl())).append("\n");
        report.append("- Task: `").append(valueOrMissing(center.taskId())).append("`\n");
        report.append("- Task status: `").append(valueOrMissing(center.taskStatus())).append("`\n");
        report.append("- Pull Request: ").append(valueOrMissing(center.pullRequestUrl())).append("\n");
        report.append("- Delivery center generated at: `").append(center.generatedAt()).append("`\n");
        report.append("- Archived at: `").append(archivedAt).append("`\n\n");
        report.append("## Summary\n\n").append(center.summary()).append("\n\n");
        report.append("## Next Action\n\n").append(center.nextAction()).append("\n\n");
        appendReadinessCards(report, center.readinessCards());
        appendList(report, "Blockers", center.blockers());
        appendEvidenceLinks(report, center.evidenceLinks());
        appendList(report, "Download Actions", archiveDownloadActions(center));
        report.append("## Side Effect Contract\n\n")
                .append(archiveSideEffectContract(center.sideEffectContract()))
                .append("\n\n");
        report.append("## Frozen Reviewer Delivery Center Report\n\n");
        report.append(center.markdownReport());
        return report.toString();
    }

    private static void appendReadinessCards(
            StringBuilder report,
            List<DemoLiveDemoReviewerDeliveryCenterVo.ReadinessCard> cards
    ) {
        report.append("## Readiness Cards\n\n");
        for (DemoLiveDemoReviewerDeliveryCenterVo.ReadinessCard card : cards) {
            report.append("- `").append(card.status()).append("` ")
                    .append(card.name()).append(": ")
                    .append(card.summary())
                    .append(" Next: ")
                    .append(card.nextAction())
                    .append("\n");
        }
        report.append("\n");
    }

    private static void appendEvidenceLinks(
            StringBuilder report,
            List<DemoLiveDemoReviewerDeliveryCenterVo.EvidenceLink> links
    ) {
        report.append("## Evidence Links\n\n");
        for (DemoLiveDemoReviewerDeliveryCenterVo.EvidenceLink link : links) {
            report.append("- ").append(link.label()).append(": ")
                    .append(link.url())
                    .append(" - ")
                    .append(link.description())
                    .append("\n");
        }
        report.append("\n");
    }

    private static void appendList(StringBuilder report, String heading, List<String> items) {
        report.append("## ").append(heading).append("\n\n");
        if (items.isEmpty()) {
            report.append("- None\n");
        } else {
            for (String item : items) {
                report.append("- ").append(item).append("\n");
            }
        }
        report.append("\n");
    }

    private static String valueOrMissing(String value) {
        return value == null || value.isBlank() ? "missing" : value;
    }
}
