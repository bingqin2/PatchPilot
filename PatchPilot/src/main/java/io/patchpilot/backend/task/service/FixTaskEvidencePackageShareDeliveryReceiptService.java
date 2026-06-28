package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.dto.FixTaskEvidencePackageShareDeliveryReceiptDto;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageShareCenterVo;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageShareDeliveryReceiptVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class FixTaskEvidencePackageShareDeliveryReceiptService {

    private static final int MAX_RECEIPTS = 20;

    private final Supplier<FixTaskEvidencePackageShareCenterVo> shareCenterSupplier;
    private final FixTaskEvidencePackageShareDeliveryReceiptRepository receiptRepository;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public FixTaskEvidencePackageShareDeliveryReceiptService(
            FixTaskEvidencePackageArchiveService archiveService,
            FixTaskEvidencePackageShareDeliveryReceiptRepository receiptRepository
    ) {
        this(
                () -> archiveService.shareCenter(20),
                receiptRepository,
                Clock.systemUTC(),
                () -> UUID.randomUUID().toString()
        );
    }

    FixTaskEvidencePackageShareDeliveryReceiptService(
            Supplier<FixTaskEvidencePackageShareCenterVo> shareCenterSupplier,
            FixTaskEvidencePackageShareDeliveryReceiptRepository receiptRepository,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.shareCenterSupplier = shareCenterSupplier;
        this.receiptRepository = receiptRepository;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public FixTaskEvidencePackageShareDeliveryReceiptVo recordDeliveryReceipt(
            FixTaskEvidencePackageShareDeliveryReceiptDto request
    ) {
        FixTaskEvidencePackageShareCenterVo center = shareCenterSupplier.get();
        if (!center.shareReady()) {
            throw new IllegalStateException("task evidence share center is not share-ready");
        }
        Instant createdAt = Instant.now(clock);
        String channel = requiredText(request.deliveryChannel(), "deliveryChannel");
        String target = requiredText(request.deliveryTarget(), "deliveryTarget");
        String operator = requiredText(request.operator(), "operator");
        String notes = optionalText(request.notes());
        Instant deliveredAt = request.deliveredAt() == null ? createdAt : request.deliveredAt();
        String archiveId = requiredText(center.shareableArchiveId(), "shareableArchiveId");
        String taskId = requiredText(center.shareableTaskId(), "shareableTaskId");
        String pullRequestUrl = requiredText(center.shareablePullRequestUrl(), "shareablePullRequestUrl");
        String subject = "PatchPilot task evidence: " + taskId;
        String markdownReport = formatMarkdownReport(
                center,
                archiveId,
                taskId,
                pullRequestUrl,
                channel,
                target,
                operator,
                notes,
                subject,
                deliveredAt,
                createdAt
        );
        return receiptRepository.save(new FixTaskEvidencePackageShareDeliveryReceiptVo(
                idSupplier.get(),
                center.status(),
                archiveId,
                taskId,
                valueOrNone(center.shareableRepositoryOwner()),
                valueOrNone(center.shareableRepositoryName()),
                center.shareableIssueNumber() == null ? 0L : center.shareableIssueNumber(),
                pullRequestUrl,
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

    public List<FixTaskEvidencePackageShareDeliveryReceiptVo> listRecentReceipts() {
        return receiptRepository.listRecentReceipts(MAX_RECEIPTS);
    }

    public Optional<FixTaskEvidencePackageShareDeliveryReceiptVo> findReceipt(String receiptId) {
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
        return value == null || value.isBlank() ? "none" : value.trim();
    }

    private static String formatMarkdownReport(
            FixTaskEvidencePackageShareCenterVo center,
            String archiveId,
            String taskId,
            String pullRequestUrl,
            String channel,
            String target,
            String operator,
            String notes,
            String subject,
            Instant deliveredAt,
            Instant createdAt
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("# PatchPilot Task Evidence Delivery Receipt\n\n");
        builder.append("- Status: `").append(center.status()).append("`\n");
        builder.append("- Task evidence archive: `").append(archiveId).append("`\n");
        builder.append("- Task: `").append(taskId).append("`\n");
        builder.append("- Pull Request: ").append(pullRequestUrl).append('\n');
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
        builder.append("POST /api/tasks/evidence-packages/share-delivery-receipts records local evidence only: it does not send messages, create tasks, call the model, run tests, mutate Git, or write to GitHub.\n");
        return builder.toString();
    }
}
