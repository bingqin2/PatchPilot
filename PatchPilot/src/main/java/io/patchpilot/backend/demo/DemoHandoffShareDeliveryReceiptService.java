package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoHandoffShareDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareInstructionsVo;
import io.patchpilot.backend.demo.service.DemoHandoffShareDeliveryReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class DemoHandoffShareDeliveryReceiptService {

    private static final int MAX_RECEIPTS = 20;

    private final Supplier<DemoHandoffShareInstructionsVo> instructionsSupplier;
    private final DemoHandoffShareDeliveryReceiptRepository receiptRepository;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public DemoHandoffShareDeliveryReceiptService(
            DemoHandoffShareCenterService shareCenterService,
            DemoHandoffShareDeliveryReceiptRepository receiptRepository
    ) {
        this(
                shareCenterService::getShareInstructions,
                receiptRepository,
                Clock.systemUTC(),
                () -> UUID.randomUUID().toString()
        );
    }

    DemoHandoffShareDeliveryReceiptService(
            Supplier<DemoHandoffShareInstructionsVo> instructionsSupplier,
            DemoHandoffShareDeliveryReceiptRepository receiptRepository,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.instructionsSupplier = instructionsSupplier;
        this.receiptRepository = receiptRepository;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public DemoHandoffShareDeliveryReceiptVo recordDeliveryReceipt(DemoHandoffShareDeliveryReceiptRequestDto request) {
        DemoHandoffShareInstructionsVo instructions = instructionsSupplier.get();
        if (!instructions.sendReady()) {
            throw new IllegalStateException("handoff share instructions are not send-ready");
        }
        Instant createdAt = Instant.now(clock);
        String channel = requiredText(request.deliveryChannel(), "deliveryChannel");
        String target = requiredText(request.deliveryTarget(), "deliveryTarget");
        String operator = requiredText(request.operator(), "operator");
        String notes = optionalText(request.notes());
        Instant deliveredAt = request.deliveredAt() == null ? createdAt : request.deliveredAt();
        String handoffArchiveId = valueOrNone(instructions.latestArchiveId());
        String sessionId = valueOrNone(instructions.latestSessionId());
        String markdownReport = formatMarkdownReport(
                instructions,
                handoffArchiveId,
                sessionId,
                channel,
                target,
                operator,
                notes,
                deliveredAt,
                createdAt
        );
        return receiptRepository.save(new DemoHandoffShareDeliveryReceiptVo(
                idSupplier.get(),
                instructions.status(),
                handoffArchiveId,
                sessionId,
                channel,
                target,
                operator,
                notes,
                instructions.messageSubject(),
                deliveredAt,
                createdAt,
                markdownReport
        ));
    }

    public List<DemoHandoffShareDeliveryReceiptVo> listRecentReceipts() {
        return receiptRepository.listRecentReceipts(MAX_RECEIPTS);
    }

    public Optional<DemoHandoffShareDeliveryReceiptVo> findReceipt(String receiptId) {
        return receiptRepository.findById(receiptId);
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
        if (value == null || value.isBlank()) {
            return "none";
        }
        return value.trim();
    }

    private static String formatMarkdownReport(
            DemoHandoffShareInstructionsVo instructions,
            String handoffArchiveId,
            String sessionId,
            String channel,
            String target,
            String operator,
            String notes,
            Instant deliveredAt,
            Instant createdAt
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("# PatchPilot Demo Handoff Share Delivery Receipt\n\n");
        builder.append("- Status: `").append(instructions.status().name()).append("`\n");
        builder.append("- Handoff archive: `").append(handoffArchiveId).append("`\n");
        builder.append("- Session: `").append(sessionId).append("`\n");
        builder.append("- Delivery channel: `").append(channel).append("`\n");
        builder.append("- Delivery target: `").append(target).append("`\n");
        builder.append("- Operator: `").append(operator).append("`\n");
        builder.append("- Message subject: ").append(instructions.messageSubject()).append('\n');
        builder.append("- Delivered at: `").append(deliveredAt).append("`\n");
        builder.append("- Created at: `").append(createdAt).append("`\n\n");
        builder.append("## Notes\n\n");
        builder.append(notes.isBlank() ? "No delivery notes recorded." : notes).append("\n\n");
        builder.append("## Source Instructions\n\n");
        builder.append(instructions.summary()).append("\n\n");
        builder.append("## Side-Effect Contract\n\n");
        builder.append("POST /api/demo/handoff-share-delivery-receipts records local evidence only: it does not send messages, create tasks, call the model, mutate Git, or write to GitHub.\n");
        return builder.toString();
    }
}
