package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceShareCenterVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceShareDeliveryReceiptVo;
import io.patchpilot.backend.demo.service.DemoLaunchEvidenceShareDeliveryReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class DemoLaunchEvidenceShareDeliveryReceiptService {

    private static final int MAX_RECEIPTS = 20;

    private final Supplier<DemoLaunchEvidenceShareCenterVo> shareCenterSupplier;
    private final DemoLaunchEvidenceShareDeliveryReceiptRepository receiptRepository;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public DemoLaunchEvidenceShareDeliveryReceiptService(
            DemoLaunchEvidenceShareCenterService shareCenterService,
            DemoLaunchEvidenceShareDeliveryReceiptRepository receiptRepository
    ) {
        this(
                shareCenterService::getShareCenter,
                receiptRepository,
                Clock.systemUTC(),
                () -> UUID.randomUUID().toString()
        );
    }

    DemoLaunchEvidenceShareDeliveryReceiptService(
            Supplier<DemoLaunchEvidenceShareCenterVo> shareCenterSupplier,
            DemoLaunchEvidenceShareDeliveryReceiptRepository receiptRepository,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.shareCenterSupplier = shareCenterSupplier;
        this.receiptRepository = receiptRepository;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public DemoLaunchEvidenceShareDeliveryReceiptVo recordDeliveryReceipt(
            DemoLaunchEvidenceShareDeliveryReceiptRequestDto request
    ) {
        DemoLaunchEvidenceShareCenterVo center = shareCenterSupplier.get();
        if (!center.shareReady()) {
            throw new IllegalStateException("launch evidence share center is not share-ready");
        }
        Instant createdAt = Instant.now(clock);
        String channel = requiredText(request.deliveryChannel(), "deliveryChannel");
        String target = requiredText(request.deliveryTarget(), "deliveryTarget");
        String operator = requiredText(request.operator(), "operator");
        String notes = optionalText(request.notes());
        Instant deliveredAt = request.deliveredAt() == null ? createdAt : request.deliveredAt();
        String archiveId = valueOrNone(center.latestArchiveId());
        String sessionId = valueOrNone(center.latestSessionId());
        String subject = messageSubject(center);
        String markdownReport = formatMarkdownReport(
                center,
                archiveId,
                sessionId,
                channel,
                target,
                operator,
                notes,
                subject,
                deliveredAt,
                createdAt
        );
        return receiptRepository.save(new DemoLaunchEvidenceShareDeliveryReceiptVo(
                idSupplier.get(),
                center.status(),
                archiveId,
                sessionId,
                channel,
                target,
                operator,
                notes,
                subject,
                deliveredAt,
                createdAt,
                markdownReport
        ));
    }

    public List<DemoLaunchEvidenceShareDeliveryReceiptVo> listRecentReceipts() {
        return receiptRepository.listRecentReceipts(MAX_RECEIPTS);
    }

    public Optional<DemoLaunchEvidenceShareDeliveryReceiptVo> findReceipt(String receiptId) {
        return receiptRepository.findById(receiptId);
    }

    private static String messageSubject(DemoLaunchEvidenceShareCenterVo center) {
        if (hasText(center.latestSessionId())) {
            return "PatchPilot demo launch evidence: " + center.latestSessionId();
        }
        return "PatchPilot demo launch evidence: not ready";
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

    private static String formatMarkdownReport(
            DemoLaunchEvidenceShareCenterVo center,
            String archiveId,
            String sessionId,
            String channel,
            String target,
            String operator,
            String notes,
            String subject,
            Instant deliveredAt,
            Instant createdAt
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("# PatchPilot Demo Launch Evidence Delivery Receipt\n\n");
        builder.append("- Status: `").append(center.status()).append("`\n");
        builder.append("- Launch evidence archive: `").append(archiveId).append("`\n");
        builder.append("- Session: `").append(sessionId).append("`\n");
        builder.append("- Delivery channel: `").append(channel).append("`\n");
        builder.append("- Delivery target: `").append(target).append("`\n");
        builder.append("- Operator: `").append(operator).append("`\n");
        builder.append("- Message subject: ").append(subject).append('\n');
        builder.append("- Delivered at: `").append(deliveredAt).append("`\n");
        builder.append("- Created at: `").append(createdAt).append("`\n\n");
        builder.append("## Notes\n\n");
        builder.append(notes.isBlank() ? "No delivery notes recorded." : notes).append("\n\n");
        builder.append("## Source Share Center\n\n");
        builder.append(center.summary()).append("\n\n");
        builder.append("## Side-Effect Contract\n\n");
        builder.append("POST /api/demo/launch-evidence-share-delivery-receipts records local evidence only: it does not send messages, create tasks, call the model, run tests, mutate Git, or write to GitHub.\n");
        return builder.toString();
    }
}
