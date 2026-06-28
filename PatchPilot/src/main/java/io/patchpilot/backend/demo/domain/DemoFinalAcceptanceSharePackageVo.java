package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoFinalAcceptanceSharePackageVo(
        DemoReadinessStatus status,
        boolean sendReady,
        String summary,
        String nextAction,
        String launchCertificateArchiveId,
        String taskCertificateArchiveId,
        String latestTaskId,
        String latestPullRequestUrl,
        List<String> recommendedRecipients,
        List<String> requiredAttachments,
        List<String> preSendChecks,
        String messageSubject,
        String messageBody,
        List<String> evidenceNotes,
        String sideEffectContract,
        String markdownReport,
        Instant generatedAt
) {
}
