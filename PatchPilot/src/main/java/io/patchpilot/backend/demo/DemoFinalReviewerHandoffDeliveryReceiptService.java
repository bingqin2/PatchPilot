package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalReviewerHandoffDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoFinalReviewerHandoffPackageVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.DemoFinalReviewerHandoffDeliveryReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class DemoFinalReviewerHandoffDeliveryReceiptService {

    private static final int MAX_RECEIPTS = 20;

    private final Supplier<DemoFinalReviewerHandoffPackageVo> packageSupplier;
    private final DemoFinalReviewerHandoffDeliveryReceiptRepository receiptRepository;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public DemoFinalReviewerHandoffDeliveryReceiptService(
            DemoFinalReviewerHandoffPackageService packageService,
            DemoFinalReviewerHandoffDeliveryReceiptRepository receiptRepository
    ) {
        this(
                packageService::getPackage,
                receiptRepository,
                Clock.systemUTC(),
                () -> UUID.randomUUID().toString()
        );
    }

    DemoFinalReviewerHandoffDeliveryReceiptService(
            Supplier<DemoFinalReviewerHandoffPackageVo> packageSupplier,
            DemoFinalReviewerHandoffDeliveryReceiptRepository receiptRepository,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.packageSupplier = packageSupplier;
        this.receiptRepository = receiptRepository;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public DemoFinalReviewerHandoffDeliveryReceiptVo recordDeliveryReceipt(
            DemoFinalReviewerHandoffDeliveryReceiptRequestDto request
    ) {
        DemoFinalReviewerHandoffPackageVo handoffPackage = packageSupplier.get();
        if (!packageReady(handoffPackage)) {
            throw new IllegalStateException("final reviewer handoff package is not ready for delivery");
        }

        Instant createdAt = Instant.now(clock);
        String channel = requiredText(request.deliveryChannel(), "deliveryChannel");
        String target = requiredText(request.deliveryTarget(), "deliveryTarget");
        String operator = requiredText(request.operator(), "operator");
        String notes = optionalText(request.notes());
        Instant deliveredAt = request.deliveredAt() == null ? createdAt : request.deliveredAt();
        String report = formatMarkdownReport(handoffPackage, channel, target, operator, notes, deliveredAt, createdAt);

        return receiptRepository.save(new DemoFinalReviewerHandoffDeliveryReceiptVo(
                idSupplier.get(),
                DemoReadinessStatus.READY,
                handoffPackage.status(),
                valueOrNone(handoffPackage.latestCertificateArchiveId()),
                valueOrNone(handoffPackage.latestDeliveryFinalizationArchiveId()),
                valueOrNone(handoffPackage.latestReleaseBundleArchiveId()),
                valueOrNone(handoffPackage.latestDeliveryReceiptId()),
                valueOrNone(handoffPackage.latestPackageCertificateArchiveId()),
                valueOrNone(handoffPackage.latestPackageArchiveId()),
                valueOrNone(handoffPackage.latestPackageDeliveryReceiptId()),
                valueOrNone(handoffPackage.latestTaskId()),
                valueOrNone(handoffPackage.latestPullRequestUrl()),
                handoffPackage.summary(),
                handoffPackage.nextAction(),
                channel,
                target,
                operator,
                notes,
                deliveredAt,
                createdAt,
                report
        ));
    }

    public List<DemoFinalReviewerHandoffDeliveryReceiptVo> listRecentReceipts() {
        return receiptRepository.listRecentReceipts(MAX_RECEIPTS);
    }

    public Optional<DemoFinalReviewerHandoffDeliveryReceiptVo> findReceipt(String receiptId) {
        return receiptRepository.findById(receiptId);
    }

    private static boolean packageReady(DemoFinalReviewerHandoffPackageVo handoffPackage) {
        return handoffPackage != null
                && handoffPackage.status() == DemoReadinessStatus.READY
                && handoffPackage.readyForReview()
                && hasText(handoffPackage.latestCertificateArchiveId());
    }

    private static String formatMarkdownReport(
            DemoFinalReviewerHandoffPackageVo handoffPackage,
            String channel,
            String target,
            String operator,
            String notes,
            Instant deliveredAt,
            Instant createdAt
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("# PatchPilot Final Reviewer Handoff Delivery Receipt\n\n");
        builder.append("- Status: `READY`\n");
        builder.append("- Final reviewer handoff package status: `")
                .append(handoffPackage.status())
                .append("`\n");
        builder.append("- Terminal certificate archive: `")
                .append(valueOrNone(handoffPackage.latestCertificateArchiveId()))
                .append("`\n");
        builder.append("- Release-bundle delivery finalization archive: `")
                .append(valueOrNone(handoffPackage.latestDeliveryFinalizationArchiveId()))
                .append("`\n");
        builder.append("- Release bundle archive: `")
                .append(valueOrNone(handoffPackage.latestReleaseBundleArchiveId()))
                .append("`\n");
        builder.append("- Release-bundle delivery receipt: `")
                .append(valueOrNone(handoffPackage.latestDeliveryReceiptId()))
                .append("`\n");
        builder.append("- Package-level certificate archive: `")
                .append(valueOrNone(handoffPackage.latestPackageCertificateArchiveId()))
                .append("`\n");
        builder.append("- Package archive: `")
                .append(valueOrNone(handoffPackage.latestPackageArchiveId()))
                .append("`\n");
        builder.append("- Package delivery receipt: `")
                .append(valueOrNone(handoffPackage.latestPackageDeliveryReceiptId()))
                .append("`\n");
        builder.append("- Task: `").append(valueOrNone(handoffPackage.latestTaskId())).append("`\n");
        builder.append("- Pull Request: ").append(valueOrNone(handoffPackage.latestPullRequestUrl())).append('\n');
        builder.append("- Summary: ").append(handoffPackage.summary()).append('\n');
        builder.append("- Next action: ").append(handoffPackage.nextAction()).append('\n');
        builder.append("- Delivery channel: `").append(channel).append("`\n");
        builder.append("- Delivery target: `").append(target).append("`\n");
        builder.append("- Operator: `").append(operator).append("`\n");
        builder.append("- Delivered at: `").append(deliveredAt).append("`\n");
        builder.append("- Created at: `").append(createdAt).append("`\n\n");
        appendList(builder, "Required Attachments", handoffPackage.requiredAttachments());
        appendList(builder, "Handoff Evidence Notes", handoffPackage.evidenceNotes());
        appendList(builder, "Handoff Download Actions", handoffPackage.downloadActions());
        builder.append("## Notes\n\n");
        builder.append(notes.isBlank() ? "No delivery notes recorded." : notes).append("\n\n");
        builder.append("## Source Final Reviewer Handoff Package\n\n");
        builder.append(handoffPackage.markdownReport()).append("\n\n");
        builder.append("## Side-Effect Contract\n\n");
        builder.append("POST /api/demo/final-reviewer-handoff-package/delivery-receipts ");
        builder.append("records local evidence only: it does not send messages, create tasks, call the model, ");
        builder.append("run tests, mutate Git, archive records, or write to GitHub.\n");
        return builder.toString();
    }

    private static void appendList(StringBuilder builder, String heading, List<String> items) {
        builder.append("## ").append(heading).append("\n\n");
        for (String item : items) {
            builder.append("- ").append(item).append('\n');
        }
        builder.append('\n');
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
