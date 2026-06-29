package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptService {

    private static final int MAX_RECEIPTS = 20;

    private final Supplier<DemoFinalAcceptanceCompletionEvidenceBundleVo> bundleSupplier;
    private final DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptRepository receiptRepository;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptService(
            DemoFinalAcceptanceCompletionEvidenceBundleService bundleService,
            DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptRepository receiptRepository
    ) {
        this(bundleService::getBundle, receiptRepository, Clock.systemUTC(), () -> UUID.randomUUID().toString());
    }

    DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptService(
            Supplier<DemoFinalAcceptanceCompletionEvidenceBundleVo> bundleSupplier,
            DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptRepository receiptRepository,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.bundleSupplier = bundleSupplier;
        this.receiptRepository = receiptRepository;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo recordDeliveryReceipt(
            DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptRequestDto request
    ) {
        DemoFinalAcceptanceCompletionEvidenceBundleVo bundle = bundleSupplier.get();
        if (!bundle.readyToShare() || bundle.status() != DemoReadinessStatus.READY) {
            throw new IllegalStateException("final acceptance completion evidence bundle is not ready to share");
        }
        Instant createdAt = Instant.now(clock);
        String channel = requiredText(request.deliveryChannel(), "deliveryChannel");
        String target = requiredText(request.deliveryTarget(), "deliveryTarget");
        String operator = requiredText(request.operator(), "operator");
        String notes = optionalText(request.notes());
        Instant deliveredAt = request.deliveredAt() == null ? createdAt : request.deliveredAt();
        String report = formatMarkdownReport(bundle, channel, target, operator, notes, deliveredAt, createdAt);

        return receiptRepository.save(new DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo(
                idSupplier.get(),
                DemoReadinessStatus.READY,
                true,
                bundle.status(),
                bundle.summary(),
                bundle.nextAction(),
                valueOrNone(bundle.latestCompletionArchiveId()),
                valueOrNone(bundle.latestSharePackageArchiveId()),
                valueOrNone(bundle.latestDeliveryReceiptId()),
                valueOrNone(bundle.latestTaskId()),
                channel,
                target,
                operator,
                notes,
                deliveredAt,
                createdAt,
                report
        ));
    }

    public List<DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo> listRecentReceipts() {
        return receiptRepository.listRecentReceipts(MAX_RECEIPTS);
    }

    public Optional<DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo> findReceipt(String receiptId) {
        return receiptRepository.findById(receiptId);
    }

    private static String formatMarkdownReport(
            DemoFinalAcceptanceCompletionEvidenceBundleVo bundle,
            String channel,
            String target,
            String operator,
            String notes,
            Instant deliveredAt,
            Instant createdAt
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("# PatchPilot Final Acceptance Completion Evidence Delivery Receipt\n\n");
        builder.append("- Status: `READY`\n");
        builder.append("- Ready to share: `true`\n");
        builder.append("- Completion evidence bundle status: `").append(bundle.status()).append("`\n");
        builder.append("- Summary: ").append(bundle.summary()).append('\n');
        builder.append("- Next action: ").append(bundle.nextAction()).append('\n');
        builder.append("- Final completion archive: `").append(valueOrNone(bundle.latestCompletionArchiveId())).append("`\n");
        builder.append("- Final acceptance share package archive: `")
                .append(valueOrNone(bundle.latestSharePackageArchiveId()))
                .append("`\n");
        builder.append("- Final acceptance share delivery receipt: `")
                .append(valueOrNone(bundle.latestDeliveryReceiptId()))
                .append("`\n");
        builder.append("- Task: `").append(valueOrNone(bundle.latestTaskId())).append("`\n");
        builder.append("- Delivery channel: `").append(channel).append("`\n");
        builder.append("- Delivery target: `").append(target).append("`\n");
        builder.append("- Operator: `").append(operator).append("`\n");
        builder.append("- Delivered at: `").append(deliveredAt).append("`\n");
        builder.append("- Created at: `").append(createdAt).append("`\n\n");
        appendList(builder, "Evidence Notes", bundle.evidenceNotes());
        appendList(builder, "Download Actions", bundle.downloadActions());
        builder.append("## Notes\n\n");
        builder.append(notes.isBlank() ? "No delivery notes recorded." : notes).append("\n\n");
        builder.append("## Source Completion Evidence Bundle\n\n");
        builder.append(bundle.markdownReport()).append("\n\n");
        builder.append("## Side-Effect Contract\n\n");
        builder.append("POST /api/demo/final-acceptance-completion-evidence-delivery-receipts ");
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

    private static String valueOrNone(String value) {
        return value == null || value.isBlank() ? "none" : value.trim();
    }
}
