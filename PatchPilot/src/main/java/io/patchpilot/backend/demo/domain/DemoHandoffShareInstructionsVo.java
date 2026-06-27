package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoHandoffShareInstructionsVo(
        DemoReadinessStatus status,
        boolean sendReady,
        String summary,
        String nextAction,
        List<String> recommendedRecipients,
        List<String> requiredAttachments,
        List<String> preSendChecks,
        String messageSubject,
        String messageBody,
        String markdownReport,
        Instant generatedAt
) {
}
