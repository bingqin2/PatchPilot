package io.patchpilot.backend.task.domain.vo;

import java.time.Instant;
import java.util.List;

public record FixTaskPatchReviewVo(
        String id,
        String taskId,
        String decision,
        String reason,
        String confidence,
        String requiredFollowUp,
        List<String> editedFiles,
        Instant createdAt
) {
}
