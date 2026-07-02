package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoLiveDemoHandoffPackageVo(
        String status,
        boolean readyForReview,
        String evidenceBundleArchiveId,
        String repository,
        long issueNumber,
        String issueUrl,
        String triggerUser,
        String triggerComment,
        String taskId,
        String taskStatus,
        String pullRequestUrl,
        String webhookDeliveryId,
        String summary,
        List<String> reviewChecklist,
        List<String> deliveryInstructions,
        List<String> evidenceNotes,
        String sideEffectContract,
        Instant generatedAt,
        String markdownReport
) {
    public DemoLiveDemoHandoffPackageVo {
        reviewChecklist = List.copyOf(reviewChecklist);
        deliveryInstructions = List.copyOf(deliveryInstructions);
        evidenceNotes = List.copyOf(evidenceNotes);
    }
}
