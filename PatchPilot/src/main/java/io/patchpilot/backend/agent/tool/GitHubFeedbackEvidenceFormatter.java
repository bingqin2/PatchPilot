package io.patchpilot.backend.agent.tool;

import io.patchpilot.backend.task.domain.vo.FixTaskPatchReviewVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import org.springframework.util.StringUtils;

import java.util.List;

final class GitHubFeedbackEvidenceFormatter {

    private GitHubFeedbackEvidenceFormatter() {
    }

    static void appendRiskReviewApprovalEvidence(StringBuilder body, FixTaskVo task, boolean markdownList) {
        if (task.riskReviewApprovedAt() == null) {
            return;
        }
        appendLine(body, markdownList, "Risk review approval:");
        appendLine(body, markdownList, "Review approved by: `" + valueOrUnknown(task.riskReviewApprovedBy()) + "`");
        appendLine(body, markdownList, "Review approved at: `" + task.riskReviewApprovedAt() + "`");
        if (StringUtils.hasText(task.riskReviewApprovalReason())) {
            appendLine(body, markdownList, "Review approval reason: " + task.riskReviewApprovalReason());
        }
        appendLine(
                body,
                markdownList,
                "PatchPilot resumed this task only after an allowed operator approved the generated diff risk review."
        );
    }

    static void appendPatchReviewEvidence(
            StringBuilder body,
            FixTaskPatchReviewVo latestPatchReview,
            boolean markdownList
    ) {
        if (latestPatchReview == null) {
            return;
        }
        appendLine(body, markdownList, "Patch review:");
        appendLine(body, markdownList, "Patch review decision: `" + latestPatchReview.decision() + "`");
        if (StringUtils.hasText(latestPatchReview.reason())) {
            appendLine(body, markdownList, "Patch review reason: " + latestPatchReview.reason());
        }
        if (StringUtils.hasText(latestPatchReview.confidence())) {
            appendLine(body, markdownList, "Patch review confidence: `" + latestPatchReview.confidence() + "`");
        }
        if (StringUtils.hasText(latestPatchReview.requiredFollowUp())) {
            appendLine(body, markdownList, "Patch review follow-up: " + latestPatchReview.requiredFollowUp());
        }
        appendLine(body, markdownList, "Patch review edited files: " + formatEditedFiles(latestPatchReview.editedFiles()));
        appendLine(body, markdownList, "Patch reviewed at: `" + latestPatchReview.createdAt() + "`");
    }

    private static void appendLine(StringBuilder body, boolean markdownList, String line) {
        if (markdownList) {
            body.append("- ");
        }
        body.append(line).append("\n");
    }

    private static String valueOrUnknown(String value) {
        return StringUtils.hasText(value) ? value : "unknown";
    }

    private static String formatEditedFiles(List<String> editedFiles) {
        if (editedFiles == null || editedFiles.isEmpty()) {
            return "none";
        }
        return "`" + String.join("`, `", editedFiles) + "`";
    }
}
