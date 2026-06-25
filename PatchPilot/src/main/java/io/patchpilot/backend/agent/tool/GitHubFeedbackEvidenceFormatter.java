package io.patchpilot.backend.agent.tool;

import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import org.springframework.util.StringUtils;

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

    private static void appendLine(StringBuilder body, boolean markdownList, String line) {
        if (markdownList) {
            body.append("- ");
        }
        body.append(line).append("\n");
    }

    private static String valueOrUnknown(String value) {
        return StringUtils.hasText(value) ? value : "unknown";
    }
}
