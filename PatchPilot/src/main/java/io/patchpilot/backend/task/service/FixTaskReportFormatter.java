package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskDetailVo;
import io.patchpilot.backend.task.domain.vo.FixTaskModelCallVo;
import io.patchpilot.backend.task.domain.vo.FixTaskPatchReviewVo;
import io.patchpilot.backend.task.domain.vo.FixTaskTestRunVo;
import io.patchpilot.backend.task.domain.vo.FixTaskTimelineEventVo;
import io.patchpilot.backend.task.domain.vo.FixTaskToolCallVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.domain.vo.IssueContextCommentVo;
import io.patchpilot.backend.task.domain.vo.IssueContextVo;
import io.patchpilot.backend.task.domain.vo.RepositorySupportGuidanceVo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FixTaskReportFormatter {

    public String format(FixTaskDetailVo detail) {
        FixTaskVo task = detail.summary().task();
        StringBuilder report = new StringBuilder()
                .append("# PatchPilot Task Report\n\n")
                .append("- Task: `").append(task.id()).append("`\n")
                .append("- Repository: `").append(task.repositoryOwner()).append("/").append(task.repositoryName()).append("`\n")
                .append("- Issue: #").append(task.issueNumber()).append("\n")
                .append("- Status: `").append(task.status()).append("`\n")
                .append("- Trigger: ").append(task.triggerComment()).append("\n");
        if (task.failureReason() != null) {
            report.append("- Failure: ").append(TaskFailureFeedback.from(task.failureReason()).safeReason()).append("\n");
        }
        if (task.pullRequestUrl() != null) {
            report.append("- Pull Request: ").append(task.pullRequestUrl()).append("\n");
        }
        if (task.riskReviewApprovedAt() != null) {
            report.append("- Review approved by: `")
                    .append(valueOrUnknown(task.riskReviewApprovedBy()))
                    .append("` at `")
                    .append(task.riskReviewApprovedAt())
                    .append("`\n");
            if (task.riskReviewApprovalReason() != null) {
                report.append("- Review approval reason: ").append(task.riskReviewApprovalReason()).append("\n");
            }
        }

        appendAdapter(report, task);
        appendTriggerIntentAudit(report, detail);
        appendPreExecutionSafetySnapshot(report, detail);
        appendRetryLineage(report, task);
        appendFailureDiagnosis(report, detail);
        appendIssueContext(report, detail.issueContext());
        appendRepositorySupportGuidance(report, detail.repositorySupportGuidance());
        appendQueue(report, detail);
        appendGeneratedDiff(report, detail);
        appendPatchReview(report, detail.patchReview());
        appendTimeline(report, detail.timeline());
        appendTestRuns(report, detail.testRuns());
        appendToolCalls(report, detail.toolCalls());
        appendModelCalls(report, detail.modelCalls());
        return report.toString();
    }

    private static void appendTriggerIntentAudit(StringBuilder report, FixTaskDetailVo detail) {
        if (detail.triggerIntentAudit() == null) {
            return;
        }

        report.append("\n## Trigger Intent Audit\n\n")
                .append("- Safety: ").append(detail.triggerIntentAudit().safetyDecision()).append("\n")
                .append("- Issue context: ").append(detail.triggerIntentAudit().issueContextStatus()).append("\n")
                .append("- Model: ").append(detail.triggerIntentAudit().modelDecision()).append("\n");
    }

    private static void appendPreExecutionSafetySnapshot(StringBuilder report, FixTaskDetailVo detail) {
        if (detail.preExecutionSafetySnapshot() == null) {
            return;
        }

        report.append("\n## Pre-Execution Safety Snapshot\n\n")
                .append("- Source: `").append(detail.preExecutionSafetySnapshot().source()).append("`\n")
                .append("- Final decision: `").append(detail.preExecutionSafetySnapshot().finalDecision()).append("`\n")
                .append("- Safety: ").append(detail.preExecutionSafetySnapshot().safetyDecision()).append("\n")
                .append("- Quarantine: ").append(detail.preExecutionSafetySnapshot().quarantineDecision()).append("\n")
                .append("- Rate limit: ").append(detail.preExecutionSafetySnapshot().rateLimitDecision()).append("\n")
                .append("- Issue context: ").append(detail.preExecutionSafetySnapshot().issueContextStatus()).append("\n")
                .append("- Model: ").append(detail.preExecutionSafetySnapshot().modelDecision()).append("\n");
    }

    private static void appendFailureDiagnosis(StringBuilder report, FixTaskDetailVo detail) {
        if (detail.failureDiagnosis() == null) {
            return;
        }

        report.append("\n## Failure Diagnosis\n\n")
                .append("- Category: `").append(detail.failureDiagnosis().category()).append("`\n")
                .append("- Next action: ").append(detail.failureDiagnosis().nextAction()).append("\n")
                .append("- Safe reason: ").append(detail.failureDiagnosis().safeReason()).append("\n");
    }

    private static void appendRetryLineage(StringBuilder report, FixTaskVo task) {
        if (task.retrySourceTaskId() == null) {
            return;
        }

        report.append("\n## Retry Lineage\n\n")
                .append("- Source task: `").append(task.retrySourceTaskId()).append("`\n")
                .append("- Source status: `").append(valueOrUnknown(task.retrySourceStatus())).append("`\n");
        if (task.retrySourceFailureReason() != null) {
            report.append("- Source failure: ").append(task.retrySourceFailureReason()).append("\n");
        }
        if (task.retryReason() != null) {
            report.append("- Retry reason: ").append(task.retryReason()).append("\n");
        }
        report.append("- Retried at: `").append(task.retriedAt() == null ? "unknown" : task.retriedAt()).append("`\n");
    }

    private static void appendIssueContext(StringBuilder report, IssueContextVo issueContext) {
        if (issueContext == null) {
            return;
        }

        report.append("\n## Issue Context\n\n")
                .append("- Title: ").append(valueOrUnknown(issueContext.title())).append("\n")
                .append("- URL: ").append(valueOrUnknown(issueContext.url())).append("\n");
        if (issueContext.body() != null && !issueContext.body().isBlank()) {
            report.append("- Body: ").append(truncate(issueContext.body(), 300)).append("\n");
        }
        report.append("- Recent comments: ").append(issueContext.comments().size()).append("\n");
        issueContext.comments().stream()
                .limit(3)
                .forEach(comment -> appendIssueComment(report, comment));
    }

    private static void appendIssueComment(StringBuilder report, IssueContextCommentVo comment) {
        report.append("  - `")
                .append(valueOrUnknown(comment.author()))
                .append("`: ")
                .append(truncate(comment.body(), 180))
                .append("\n");
    }

    private static void appendAdapter(StringBuilder report, FixTaskVo task) {
        if (
                task.language() == null &&
                task.buildSystem() == null &&
                task.verificationCommand() == null &&
                task.adapterDetectionReason() == null
        ) {
            return;
        }

        report.append("\n## Adapter\n\n");
        if (task.language() != null) {
            report.append("- Language: `").append(task.language()).append("`\n");
        }
        if (task.buildSystem() != null) {
            report.append("- Build system: `").append(task.buildSystem()).append("`\n");
        }
        if (task.verificationCommand() != null) {
            report.append("- Verification: `").append(task.verificationCommand()).append("`\n");
        }
        if (task.adapterDetectionReason() != null) {
            report.append("- Detection reason: ").append(task.adapterDetectionReason()).append("\n");
        }
    }

    private static void appendRepositorySupportGuidance(StringBuilder report, RepositorySupportGuidanceVo guidance) {
        if (guidance == null) {
            return;
        }

        report.append("\n## Repository Support Guidance\n\n")
                .append("- Status: `").append(guidance.status()).append("`\n")
                .append("- Reason: ").append(guidance.reason()).append("\n")
                .append("- Action: ").append(guidance.operatorAction()).append("\n")
                .append("- Supported adapters:\n");
        guidance.supportedAdapters().forEach(adapter -> report.append("  - `")
                .append(adapter.language())
                .append("/")
                .append(adapter.buildSystem())
                .append("`: verify `")
                .append(String.join(" ", adapter.verificationCommand()))
                .append("`, signals ")
                .append(formatSignals(adapter.detectionSignals()))
                .append("\n"));
    }

    private static String formatSignals(List<String> signals) {
        return "`" + String.join("`, `", signals) + "`";
    }

    private static void appendQueue(StringBuilder report, FixTaskDetailVo detail) {
        report.append("\n## Queue\n\n");
        if (detail.queueItem() == null) {
            report.append("- Latest: none\n");
        } else {
            report.append("- Latest: `")
                    .append(detail.queueItem().status())
                    .append("`, attempt ")
                    .append(detail.queueItem().attemptCount());
            if (detail.queueItem().lastError() != null) {
                report.append(", error: ").append(detail.queueItem().lastError());
            }
            report.append("\n");
        }
        report.append("- History items: ").append(detail.queueItems().size()).append("\n");
    }

    private static void appendGeneratedDiff(StringBuilder report, FixTaskDetailVo detail) {
        if (detail.generatedDiff() == null) {
            return;
        }

        report.append("\n## Generated Diff\n\n")
                .append("- Tool call: `").append(detail.generatedDiff().toolCallId()).append("`\n")
                .append("- Generated at: `").append(detail.generatedDiff().generatedAt()).append("`\n\n")
                .append("```diff\n")
                .append(detail.generatedDiff().diff())
                .append("\n```\n");
    }

    private static void appendPatchReview(StringBuilder report, FixTaskPatchReviewVo patchReview) {
        if (patchReview == null) {
            return;
        }

        report.append("\n## Patch Review\n\n")
                .append("- Decision: `").append(patchReview.decision()).append("`\n")
                .append("- Reason: ").append(patchReview.reason()).append("\n")
                .append("- Confidence: `").append(patchReview.confidence()).append("`\n");
        if ("REJECT".equals(patchReview.decision())) {
            report.append("- Review gate: `")
                    .append(PatchReviewFailureClassifier.REVIEW_GATE)
                    .append("`\n")
                    .append("- Recovery: ")
                    .append(PatchReviewFailureClassifier.REPORT_RECOVERY)
                    .append("\n");
        }
        if (patchReview.requiredFollowUp() != null && !patchReview.requiredFollowUp().isBlank()) {
            report.append("- Required follow-up: ").append(patchReview.requiredFollowUp()).append("\n");
        }
        report.append("- Edited files: ").append(formatEditedFiles(patchReview.editedFiles())).append("\n")
                .append("- Reviewed at: `").append(patchReview.createdAt()).append("`\n");
    }

    private static void appendTimeline(StringBuilder report, List<FixTaskTimelineEventVo> timeline) {
        report.append("\n## Timeline\n\n");
        if (timeline.isEmpty()) {
            report.append("- No timeline events recorded.\n");
            return;
        }
        timeline.forEach(event -> report.append("- `")
                .append(event.eventType())
                .append("`: ")
                .append(event.message())
                .append("\n"));
    }

    private static void appendTestRuns(StringBuilder report, List<FixTaskTestRunVo> testRuns) {
        report.append("\n## Test Runs\n\n");
        if (testRuns.isEmpty()) {
            report.append("- No test runs recorded.\n");
            return;
        }
        testRuns.forEach(run -> report.append("- `")
                .append(run.command())
                .append("` -> exit ")
                .append(run.exitCode())
                .append(", ")
                .append(run.durationMs())
                .append(" ms\n"));
    }

    private static void appendToolCalls(StringBuilder report, List<FixTaskToolCallVo> toolCalls) {
        report.append("\n## Tool Calls\n\n");
        if (toolCalls.isEmpty()) {
            report.append("- No tool calls recorded.\n");
            return;
        }
        toolCalls.forEach(call -> report.append("- `")
                .append(call.toolName())
                .append("` -> ")
                .append(call.success() ? "success" : "failed")
                .append(", ")
                .append(call.durationMs())
                .append(" ms\n"));
    }

    private static void appendModelCalls(StringBuilder report, List<FixTaskModelCallVo> modelCalls) {
        report.append("\n## Model Calls\n\n");
        if (modelCalls.isEmpty()) {
            report.append("- No model calls recorded.\n");
            return;
        }
        modelCalls.forEach(call -> report.append("- `")
                .append(call.model())
                .append("` -> ")
                .append(call.success() ? "success" : "failed")
                .append(", ")
                .append(call.totalTokens())
                .append(" tokens\n"));
    }

    private static String valueOrUnknown(String value) {
        return value == null || value.isBlank() ? "unknown" : value;
    }

    private static String formatEditedFiles(List<String> editedFiles) {
        if (editedFiles.isEmpty()) {
            return "none";
        }
        return "`" + String.join("`, `", editedFiles) + "`";
    }

    private static String truncate(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        String normalized = value.replace('\n', ' ').trim();
        if (normalized.length() <= maxLength) {
            return normalized;
        }
        return normalized.substring(0, maxLength) + "...";
    }
}
