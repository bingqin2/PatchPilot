package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoHandoffDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoHandoffPackageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class DemoLiveDemoHandoffDeliveryReceiptService {

    private static final int MAX_RECEIPTS = 20;
    private static final String SIDE_EFFECT_CONTRACT =
            "POST /api/demo/live-demo-handoff-package/delivery-receipts records local evidence only: "
                    + "it does not send messages, create tasks, call the model, run tests, mutate Git, "
                    + "archive records, or write to GitHub.";

    private final Supplier<DemoLiveDemoHandoffPackageVo> packageSupplier;
    private final DemoLiveDemoHandoffDeliveryReceiptRepository receiptRepository;
    private final Supplier<String> idSupplier;
    private final Supplier<Instant> nowSupplier;

    @Autowired
    public DemoLiveDemoHandoffDeliveryReceiptService(
            DemoLiveDemoHandoffPackageService packageService,
            DemoLiveDemoHandoffDeliveryReceiptRepository receiptRepository
    ) {
        this(packageService::createPackage, receiptRepository, () -> UUID.randomUUID().toString(), Instant::now);
    }

    DemoLiveDemoHandoffDeliveryReceiptService(
            Supplier<DemoLiveDemoHandoffPackageVo> packageSupplier,
            DemoLiveDemoHandoffDeliveryReceiptRepository receiptRepository,
            Supplier<String> idSupplier,
            Supplier<Instant> nowSupplier
    ) {
        this.packageSupplier = packageSupplier;
        this.receiptRepository = receiptRepository;
        this.idSupplier = idSupplier;
        this.nowSupplier = nowSupplier;
    }

    public DemoLiveDemoHandoffDeliveryReceiptVo recordDeliveryReceipt(
            DemoLiveDemoHandoffDeliveryReceiptRequestDto request
    ) {
        DemoLiveDemoHandoffPackageVo handoffPackage = packageSupplier.get();
        if (!packageReady(handoffPackage)) {
            throw new IllegalStateException("live demo handoff package is not ready for delivery");
        }

        Instant createdAt = nowSupplier.get();
        String deliveryChannel = requiredText(request.deliveryChannel(), "deliveryChannel");
        String deliveryTarget = requiredText(request.deliveryTarget(), "deliveryTarget");
        String operator = requiredText(request.operator(), "operator");
        String notes = optionalText(request.notes());
        Instant deliveredAt = request.deliveredAt() == null ? createdAt : request.deliveredAt();
        String summary = "Live demo handoff package delivery receipt is recorded.";
        String markdownReport = markdownReport(
                handoffPackage,
                summary,
                deliveryChannel,
                deliveryTarget,
                operator,
                notes,
                deliveredAt,
                createdAt
        );

        return receiptRepository.save(new DemoLiveDemoHandoffDeliveryReceiptVo(
                idSupplier.get(),
                "READY",
                handoffPackage.status(),
                handoffPackage.evidenceBundleArchiveId(),
                handoffPackage.repository(),
                handoffPackage.issueNumber(),
                handoffPackage.issueUrl(),
                handoffPackage.triggerUser(),
                handoffPackage.triggerComment(),
                handoffPackage.taskId(),
                handoffPackage.taskStatus(),
                handoffPackage.pullRequestUrl(),
                handoffPackage.webhookDeliveryId(),
                summary,
                deliveryChannel,
                deliveryTarget,
                operator,
                notes,
                deliveredAt,
                createdAt,
                markdownReport
        ));
    }

    public List<DemoLiveDemoHandoffDeliveryReceiptVo> listRecentReceipts() {
        return receiptRepository.listRecentReceipts(MAX_RECEIPTS);
    }

    public Optional<DemoLiveDemoHandoffDeliveryReceiptVo> findReceipt(String receiptId) {
        return receiptRepository.findById(receiptId);
    }

    private static boolean packageReady(DemoLiveDemoHandoffPackageVo handoffPackage) {
        return handoffPackage != null
                && "READY".equals(handoffPackage.status())
                && handoffPackage.readyForReview()
                && hasText(handoffPackage.evidenceBundleArchiveId());
    }

    private static String markdownReport(
            DemoLiveDemoHandoffPackageVo handoffPackage,
            String summary,
            String deliveryChannel,
            String deliveryTarget,
            String operator,
            String notes,
            Instant deliveredAt,
            Instant createdAt
    ) {
        StringBuilder report = new StringBuilder();
        report.append("# PatchPilot Live Demo Handoff Delivery Receipt\n\n");
        report.append("- Status: `READY`\n");
        report.append("- Handoff package status: `").append(handoffPackage.status()).append("`\n");
        report.append("- Evidence bundle archive: `")
                .append(valueOrNone(handoffPackage.evidenceBundleArchiveId()))
                .append("`\n");
        report.append("- Repository: ").append(valueOrNone(handoffPackage.repository())).append('\n');
        report.append("- Issue: #").append(handoffPackage.issueNumber()).append('\n');
        report.append("- Issue URL: ").append(valueOrNone(handoffPackage.issueUrl())).append('\n');
        report.append("- Trigger user: `").append(valueOrNone(handoffPackage.triggerUser())).append("`\n");
        report.append("- Trigger comment: `").append(valueOrNone(handoffPackage.triggerComment())).append("`\n");
        report.append("- Task: `").append(valueOrNone(handoffPackage.taskId())).append("`\n");
        report.append("- Task status: `").append(valueOrNone(handoffPackage.taskStatus())).append("`\n");
        report.append("- Webhook delivery: `").append(valueOrNone(handoffPackage.webhookDeliveryId())).append("`\n");
        report.append("- Pull Request: ").append(valueOrNone(handoffPackage.pullRequestUrl())).append('\n');
        report.append("- Delivery channel: `").append(deliveryChannel).append("`\n");
        report.append("- Delivery target: `").append(deliveryTarget).append("`\n");
        report.append("- Operator: `").append(operator).append("`\n");
        report.append("- Delivered at: `").append(deliveredAt).append("`\n");
        report.append("- Created at: `").append(createdAt).append("`\n\n");
        report.append("## Summary\n\n").append(summary).append("\n\n");
        appendList(report, "Review Checklist", handoffPackage.reviewChecklist());
        appendList(report, "Delivery Instructions", handoffPackage.deliveryInstructions());
        appendList(report, "Evidence Notes", handoffPackage.evidenceNotes());
        report.append("## Notes\n\n").append(notes.isBlank() ? "No delivery notes recorded." : notes).append("\n\n");
        report.append("## Source Live Demo Handoff Package\n\n")
                .append(handoffPackage.markdownReport())
                .append("\n\n");
        report.append("## Side-Effect Contract\n\n").append(SIDE_EFFECT_CONTRACT).append('\n');
        return report.toString();
    }

    private static void appendList(StringBuilder report, String title, List<String> items) {
        report.append("## ").append(title).append("\n\n");
        if (items.isEmpty()) {
            report.append("- None recorded.\n\n");
            return;
        }
        for (String item : items) {
            report.append("- ").append(item).append('\n');
        }
        report.append('\n');
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

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static String valueOrNone(String value) {
        return hasText(value) ? value.trim() : "none";
    }
}
