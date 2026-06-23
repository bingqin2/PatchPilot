package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskPatchReviewVo;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface FixTaskPatchReviewService {

    FixTaskPatchReviewVo recordPatchReview(
            String taskId,
            String decision,
            String reason,
            String confidence,
            String requiredFollowUp,
            List<String> editedFiles,
            Instant createdAt
    );

    Optional<FixTaskPatchReviewVo> findLatestPatchReview(String taskId);
}
