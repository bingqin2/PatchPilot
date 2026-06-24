package io.patchpilot.backend.task.service;

import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.regex.Pattern;

public record TaskFailureFeedback(String category, String nextAction, String safeReason) {

    private static final Pattern GITHUB_TOKEN = Pattern.compile("gh[pousr]_[A-Za-z0-9_]{20,}");
    private static final Pattern OPENAI_STYLE_KEY = Pattern.compile("sk-[A-Za-z0-9_-]{20,}");
    private static final Pattern ASSIGNMENT_SECRET = Pattern.compile(
            "(?i)(api[_-]?key|token|secret|password)\\s*[:=]\\s*[^\\s]+"
    );

    public static TaskFailureFeedback from(String failureReason) {
        String safeReason = redact(failureReason);
        String normalized = safeReason == null ? "" : safeReason.toLowerCase(Locale.ROOT);
        if (PatchReviewFailureClassifier.isPatchReviewRejection(safeReason)) {
            return new TaskFailureFeedback(
                    PatchReviewFailureClassifier.REVIEW_GATE,
                    PatchReviewFailureClassifier.STATUS_COMMENT_RECOVERY,
                    safeReason
            );
        }
        if (normalized.contains("verification failed")
                || normalized.contains("test failed")
                || normalized.contains("tests failed")
                || normalized.contains("maven")
                || normalized.contains("gradle")
                || normalized.contains("npm")
                || normalized.contains("pytest")) {
            return new TaskFailureFeedback(
                    "VERIFICATION_FAILED",
                    "Inspect the verification output, fix the failing test or build error, then retry the task.",
                    safeReason
            );
        }
        if (normalized.contains("github token")
                || normalized.contains("github api")
                || normalized.contains("pull request")
                || normalized.contains("permission")
                || normalized.contains("unauthorized")
                || normalized.contains("forbidden")) {
            return new TaskFailureFeedback(
                    "GITHUB_OPERATION_FAILED",
                    "Check GitHub token or App permissions, then retry the task after access is fixed.",
                    safeReason
            );
        }
        if (normalized.contains("unsupported repository") || normalized.contains("no supported language adapter")) {
            return new TaskFailureFeedback(
                    "UNSUPPORTED_REPOSITORY",
                    "Run repository preflight, add a supported adapter shape, then create a new task.",
                    safeReason
            );
        }
        if (normalized.contains("model") || normalized.contains("chat/completions") || normalized.contains("provider")) {
            return new TaskFailureFeedback(
                    "MODEL_FAILED",
                    "Check model provider configuration and availability, then retry the task.",
                    safeReason
            );
        }
        if (normalized.contains("workspace") || normalized.contains("clone") || normalized.contains("git ")) {
            return new TaskFailureFeedback(
                    "WORKSPACE_FAILED",
                    "Check workspace and Git access, clean stale task workspaces if needed, then retry the task.",
                    safeReason
            );
        }
        return new TaskFailureFeedback(
                "TASK_FAILED",
                "Inspect the task detail timeline and tool calls, then retry after the underlying problem is fixed.",
                safeReason
        );
    }

    private static String redact(String value) {
        if (!StringUtils.hasText(value)) {
            return value;
        }
        String redacted = GITHUB_TOKEN.matcher(value).replaceAll("[REDACTED]");
        redacted = OPENAI_STYLE_KEY.matcher(redacted).replaceAll("[REDACTED]");
        redacted = ASSIGNMENT_SECRET.matcher(redacted).replaceAll("$1=[REDACTED]");
        return redacted;
    }
}
