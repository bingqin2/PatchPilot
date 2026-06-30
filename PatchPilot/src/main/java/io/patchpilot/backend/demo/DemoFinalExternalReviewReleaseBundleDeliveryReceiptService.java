package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.DemoFinalExternalReviewReleaseBundleArchiveRepository;
import io.patchpilot.backend.demo.service.DemoFinalExternalReviewReleaseBundleDeliveryReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class DemoFinalExternalReviewReleaseBundleDeliveryReceiptService {

    private static final int MAX_RECEIPTS = 20;

    private final Supplier<List<DemoFinalExternalReviewReleaseBundleArchiveVo>> archiveSupplier;
    private final DemoFinalExternalReviewReleaseBundleDeliveryReceiptRepository receiptRepository;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public DemoFinalExternalReviewReleaseBundleDeliveryReceiptService(
            DemoFinalExternalReviewReleaseBundleArchiveRepository archiveRepository,
            DemoFinalExternalReviewReleaseBundleDeliveryReceiptRepository receiptRepository
    ) {
        this(
                () -> archiveRepository.listRecentArchives(1),
                receiptRepository,
                Clock.systemUTC(),
                () -> UUID.randomUUID().toString()
        );
    }

    DemoFinalExternalReviewReleaseBundleDeliveryReceiptService(
            DemoFinalExternalReviewReleaseBundleArchiveRepository archiveRepository,
            DemoFinalExternalReviewReleaseBundleDeliveryReceiptRepository receiptRepository,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this(() -> archiveRepository.listRecentArchives(1), receiptRepository, clock, idSupplier);
    }

    DemoFinalExternalReviewReleaseBundleDeliveryReceiptService(
            Supplier<List<DemoFinalExternalReviewReleaseBundleArchiveVo>> archiveSupplier,
            DemoFinalExternalReviewReleaseBundleDeliveryReceiptRepository receiptRepository,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.archiveSupplier = archiveSupplier;
        this.receiptRepository = receiptRepository;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo recordDeliveryReceipt(
            DemoFinalExternalReviewReleaseBundleDeliveryReceiptRequestDto request
    ) {
        DemoFinalExternalReviewReleaseBundleArchiveVo archive = archiveSupplier.get().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "no final external-review release bundle archive is available"
                ));
        if (archive.status() != DemoReadinessStatus.READY || !archive.releaseReady()) {
            throw new IllegalStateException(
                    "final external-review release bundle archive is not ready for delivery"
            );
        }

        Instant createdAt = Instant.now(clock);
        String channel = requiredText(request.deliveryChannel(), "deliveryChannel");
        String target = requiredText(request.deliveryTarget(), "deliveryTarget");
        String operator = requiredText(request.operator(), "operator");
        String notes = optionalText(request.notes());
        Instant deliveredAt = request.deliveredAt() == null ? createdAt : request.deliveredAt();
        String report = formatMarkdownReport(archive, channel, target, operator, notes, deliveredAt, createdAt);

        return receiptRepository.save(new DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo(
                idSupplier.get(),
                DemoReadinessStatus.READY,
                archive.status(),
                archive.id(),
                valueOrNone(archive.latestCertificateArchiveId()),
                valueOrNone(archive.latestDeliveryFinalizationArchiveId()),
                valueOrNone(archive.latestPackageArchiveId()),
                valueOrNone(archive.latestDeliveryReceiptId()),
                valueOrNone(archive.latestTaskId()),
                valueOrNone(archive.latestPullRequestUrl()),
                archive.summary(),
                archive.nextAction(),
                channel,
                target,
                operator,
                notes,
                deliveredAt,
                createdAt,
                report
        ));
    }

    public List<DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo> listRecentReceipts() {
        return receiptRepository.listRecentReceipts(MAX_RECEIPTS);
    }

    public Optional<DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo> findReceipt(String receiptId) {
        return receiptRepository.findById(receiptId);
    }

    private static String formatMarkdownReport(
            DemoFinalExternalReviewReleaseBundleArchiveVo archive,
            String channel,
            String target,
            String operator,
            String notes,
            Instant deliveredAt,
            Instant createdAt
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("# PatchPilot Final External Review Release Bundle Delivery Receipt\n\n");
        builder.append("- Status: `READY`\n");
        builder.append("- Release bundle archive status: `").append(archive.status()).append("`\n");
        builder.append("- Release bundle archive: `").append(archive.id()).append("`\n");
        builder.append("- Delivery certificate archive: `")
                .append(valueOrNone(archive.latestCertificateArchiveId())).append("`\n");
        builder.append("- Package delivery finalization archive: `")
                .append(valueOrNone(archive.latestDeliveryFinalizationArchiveId())).append("`\n");
        builder.append("- Package archive: `").append(valueOrNone(archive.latestPackageArchiveId())).append("`\n");
        builder.append("- Package delivery receipt: `")
                .append(valueOrNone(archive.latestDeliveryReceiptId())).append("`\n");
        builder.append("- Task: `").append(valueOrNone(archive.latestTaskId())).append("`\n");
        builder.append("- Pull Request: ").append(valueOrNone(archive.latestPullRequestUrl())).append('\n');
        builder.append("- Summary: ").append(archive.summary()).append('\n');
        builder.append("- Next action: ").append(archive.nextAction()).append('\n');
        builder.append("- Delivery channel: `").append(channel).append("`\n");
        builder.append("- Delivery target: `").append(target).append("`\n");
        builder.append("- Operator: `").append(operator).append("`\n");
        builder.append("- Delivered at: `").append(deliveredAt).append("`\n");
        builder.append("- Created at: `").append(createdAt).append("`\n\n");
        appendList(builder, "Release Bundle Required Attachments", archive.requiredAttachments());
        appendList(builder, "Release Bundle Evidence Notes", archive.evidenceNotes());
        appendList(builder, "Release Bundle Download Actions", archive.downloadActions());
        builder.append("## Notes\n\n");
        builder.append(notes.isBlank() ? "No delivery notes recorded." : notes).append("\n\n");
        builder.append("## Source Release Bundle Archive\n\n");
        builder.append(archive.report()).append("\n\n");
        builder.append("## Side-Effect Contract\n\n");
        builder.append("POST /api/demo/final-external-review-release-bundle/delivery-receipts ");
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
