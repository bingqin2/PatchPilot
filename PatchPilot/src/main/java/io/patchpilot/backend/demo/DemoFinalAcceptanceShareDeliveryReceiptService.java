package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceShareDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceSharePackageArchiveVo;
import io.patchpilot.backend.demo.service.DemoFinalAcceptanceShareDeliveryReceiptRepository;
import io.patchpilot.backend.demo.service.DemoFinalAcceptanceSharePackageArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class DemoFinalAcceptanceShareDeliveryReceiptService {

    private static final int MAX_RECEIPTS = 20;

    private final DemoFinalAcceptanceSharePackageArchiveRepository archiveRepository;
    private final DemoFinalAcceptanceShareDeliveryReceiptRepository receiptRepository;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public DemoFinalAcceptanceShareDeliveryReceiptService(
            DemoFinalAcceptanceSharePackageArchiveRepository archiveRepository,
            DemoFinalAcceptanceShareDeliveryReceiptRepository receiptRepository
    ) {
        this(
                archiveRepository,
                receiptRepository,
                Clock.systemUTC(),
                () -> UUID.randomUUID().toString()
        );
    }

    DemoFinalAcceptanceShareDeliveryReceiptService(
            DemoFinalAcceptanceSharePackageArchiveRepository archiveRepository,
            DemoFinalAcceptanceShareDeliveryReceiptRepository receiptRepository,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.archiveRepository = archiveRepository;
        this.receiptRepository = receiptRepository;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public DemoFinalAcceptanceShareDeliveryReceiptVo recordDeliveryReceipt(
            DemoFinalAcceptanceShareDeliveryReceiptRequestDto request
    ) {
        DemoFinalAcceptanceSharePackageArchiveVo archive = latestArchive();
        if (!archive.sendReady()) {
            throw new IllegalStateException("final acceptance share package archive is not send-ready");
        }
        Instant createdAt = Instant.now(clock);
        String channel = requiredText(request.deliveryChannel(), "deliveryChannel");
        String target = requiredText(request.deliveryTarget(), "deliveryTarget");
        String operator = requiredText(request.operator(), "operator");
        String notes = optionalText(request.notes());
        Instant deliveredAt = request.deliveredAt() == null ? createdAt : request.deliveredAt();
        String report = formatMarkdownReport(archive, channel, target, operator, notes, deliveredAt, createdAt);
        return receiptRepository.save(new DemoFinalAcceptanceShareDeliveryReceiptVo(
                idSupplier.get(),
                archive.status(),
                archive.id(),
                valueOrNone(archive.latestTaskId()),
                channel,
                target,
                operator,
                notes,
                archive.messageSubject(),
                deliveredAt,
                createdAt,
                report
        ));
    }

    public List<DemoFinalAcceptanceShareDeliveryReceiptVo> listRecentReceipts() {
        return receiptRepository.listRecentReceipts(MAX_RECEIPTS);
    }

    public Optional<DemoFinalAcceptanceShareDeliveryReceiptVo> findReceipt(String receiptId) {
        return receiptRepository.findById(receiptId);
    }

    private DemoFinalAcceptanceSharePackageArchiveVo latestArchive() {
        return archiveRepository.listRecentArchives(1).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "no final acceptance share package archive is available"
                ));
    }

    private static String formatMarkdownReport(
            DemoFinalAcceptanceSharePackageArchiveVo archive,
            String channel,
            String target,
            String operator,
            String notes,
            Instant deliveredAt,
            Instant createdAt
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("# PatchPilot Final Demo Acceptance Share Delivery Receipt\n\n");
        builder.append("- Status: `").append(archive.status().name()).append("`\n");
        builder.append("- Final acceptance package archive: `").append(archive.id()).append("`\n");
        builder.append("- Task: `").append(valueOrNone(archive.latestTaskId())).append("`\n");
        builder.append("- Delivery channel: `").append(channel).append("`\n");
        builder.append("- Delivery target: `").append(target).append("`\n");
        builder.append("- Operator: `").append(operator).append("`\n");
        builder.append("- Message subject: ").append(archive.messageSubject()).append('\n');
        builder.append("- Delivered at: `").append(deliveredAt).append("`\n");
        builder.append("- Created at: `").append(createdAt).append("`\n\n");
        builder.append("## Notes\n\n");
        builder.append(notes.isBlank() ? "No delivery notes recorded." : notes).append("\n\n");
        builder.append("## Source Final Acceptance Share Package\n\n");
        builder.append(archive.summary()).append("\n\n");
        builder.append("## Side-Effect Contract\n\n");
        builder.append("POST /api/demo/final-acceptance-share-delivery-receipts records local evidence only: ");
        builder.append("it does not send messages, create tasks, call the model, run tests, mutate Git, record receipts, or write to GitHub.\n");
        return builder.toString();
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
