package io.patchpilot.backend.task.service;

import java.util.Locale;

public final class PatchReviewFailureClassifier {

    public static final String FAILURE_PREFIX = "Model patch review rejected generated edits:";
    public static final String REVIEW_GATE = "PATCH_REVIEW_REJECTED";
    public static final String STATUS_COMMENT_RECOVERY = "Retrying this task will ask the model to generate a new patch.";
    public static final String REPORT_RECOVERY = "Retry the task to regenerate a new patch; the rejected edit will not be reused.";

    private PatchReviewFailureClassifier() {
    }

    public static boolean isPatchReviewRejection(String failureReason) {
        if (failureReason == null || failureReason.isBlank()) {
            return false;
        }
        String normalizedReason = failureReason.toLowerCase(Locale.ROOT);
        return normalizedReason.startsWith(FAILURE_PREFIX.toLowerCase(Locale.ROOT))
                || normalizedReason.contains("patch review rejected");
    }
}
