package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoReviewerDeliveryCenterArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptService {

    private static final int MAX_RECEIPTS = 20;
    private static final String SIDE_EFFECT_CONTRACT =
            "POST /api/demo/live-demo-handoff-package/reviewer-delivery-center/delivery-receipts "
                    + "records local reviewer delivery center delivery evidence only: it does not send "
                    + "messages, create tasks, call the model, run tests, mutate Git, archive records, "
                    + "or write to GitHub.";

    private final Supplier<List<DemoLiveDemoReviewerDeliveryCenterArchiveVo>> archiveSupplier;
    private final DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptRepository receiptRepository;
    private final Supplier<String> idSupplier;
    private final Supplier<Instant> nowSupplier;

    @Autowired
    public DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptService(
            DemoLiveDemoReviewerDeliveryCenterArchiveRepository archiveRepository,
            DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptRepository receiptRepository
    ) {
        this(
                () -> archiveRepository.listRecentArchives(1),
                receiptRepository,
                () -> UUID.randomUUID().toString(),
                Instant::now
        );
    }

    DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptService(
            Supplier<List<DemoLiveDemoReviewerDeliveryCenterArchiveVo>> archiveSupplier,
            DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptRepository receiptRepository,
            Supplier<String> idSupplier,
            Supplier<Instant> nowSupplier
    ) {
        this.archiveSupplier = archiveSupplier;
        this.receiptRepository = receiptRepository;
        this.idSupplier = idSupplier;
        this.nowSupplier = nowSupplier;
    }

    public DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptVo recordDeliveryReceipt(
            DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptRequestDto request
    ) {
        DemoLiveDemoReviewerDeliveryCenterArchiveVo archive = latestReadyArchive();
        Instant createdAt = nowSupplier.get();
        Instant deliveredAt = request.deliveredAt() == null ? createdAt : request.deliveredAt();
        String deliveryChannel = requiredText(request.deliveryChannel(), "deliveryChannel");
        String deliveryTarget = requiredText(request.deliveryTarget(), "deliveryTarget");
        String operator = requiredText(request.operator(), "operator");
        String notes = optionalText(request.notes());
        String summary = "Live demo reviewer delivery center delivery receipt is recorded.";

        return receiptRepository.save(new DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptVo(
                idSupplier.get(),
                "READY",
                archive.id(),
                archive.status(),
                archive.repository(),
                archive.issueNumber(),
                archive.issueUrl(),
                archive.taskId(),
                archive.taskStatus(),
                archive.pullRequestUrl(),
                summary,
                deliveryChannel,
                deliveryTarget,
                operator,
                notes,
                deliveredAt,
                createdAt,
                markdownReport(
                        archive,
                        summary,
                        deliveryChannel,
                        deliveryTarget,
                        operator,
                        notes,
                        deliveredAt,
                        createdAt
                )
        ));
    }

    public List<DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptVo> listRecentReceipts() {
        return receiptRepository.listRecentReceipts(MAX_RECEIPTS);
    }

    public Optional<DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptVo> findReceipt(String receiptId) {
        return receiptRepository.findById(receiptId);
    }

    private DemoLiveDemoReviewerDeliveryCenterArchiveVo latestReadyArchive() {
        return archiveSupplier.get().stream()
                .filter(archive -> "READY".equals(archive.status()) && archive.deliverable())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "READY reviewer delivery center archive is required before recording a delivery receipt."
                ));
    }

    private static String markdownReport(
            DemoLiveDemoReviewerDeliveryCenterArchiveVo archive,
            String summary,
            String deliveryChannel,
            String deliveryTarget,
            String operator,
            String notes,
            Instant deliveredAt,
            Instant createdAt
    ) {
        StringBuilder report = new StringBuilder();
        report.append("# PatchPilot Live Demo Reviewer Delivery Center Delivery Receipt\n\n");
        report.append("- Status: `READY`\n");
        report.append("- Reviewer delivery center archive: `").append(archive.id()).append("`\n");
        report.append("- Reviewer delivery center status: `").append(archive.status()).append("`\n");
        report.append("- Repository: ").append(valueOrMissing(archive.repository())).append("\n");
        report.append("- Issue: #").append(archive.issueNumber()).append("\n");
        report.append("- Issue URL: ").append(valueOrMissing(archive.issueUrl())).append("\n");
        report.append("- Task: `").append(valueOrMissing(archive.taskId())).append("`\n");
        report.append("- Task status: `").append(valueOrMissing(archive.taskStatus())).append("`\n");
        report.append("- Pull Request: ").append(valueOrMissing(archive.pullRequestUrl())).append("\n");
        report.append("- Delivery channel: `").append(deliveryChannel).append("`\n");
        report.append("- Delivery target: `").append(deliveryTarget).append("`\n");
        report.append("- Operator: `").append(operator).append("`\n");
        report.append("- Delivered at: `").append(deliveredAt).append("`\n");
        report.append("- Created at: `").append(createdAt).append("`\n\n");
        report.append("## Summary\n\n").append(summary).append("\n\n");
        report.append("## Notes\n\n").append(notes.isBlank() ? "No delivery notes recorded." : notes).append("\n\n");
        appendList(report, "Download Actions", archive.downloadActions());
        report.append("## Source Reviewer Delivery Center Archive\n\n")
                .append(archive.report())
                .append("\n\n");
        report.append("## Side-Effect Contract\n\n")
                .append(SIDE_EFFECT_CONTRACT)
                .append("\n");
        return report.toString();
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

    private static String requiredText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return value.trim();
    }

    private static String optionalText(String value) {
        return value == null ? "" : value.trim();
    }

    private static String valueOrMissing(String value) {
        return value == null || value.isBlank() ? "missing" : value;
    }
}
