package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.task.domain.vo.FixTaskPatchReviewVo;
import io.patchpilot.backend.task.service.FixTaskPatchReviewService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Profile("default")
public class InMemoryFixTaskPatchReviewService implements FixTaskPatchReviewService {

    private final List<FixTaskPatchReviewVo> patchReviews = new CopyOnWriteArrayList<>();

    @Override
    public FixTaskPatchReviewVo recordPatchReview(
            String taskId,
            String decision,
            String reason,
            String confidence,
            String requiredFollowUp,
            List<String> editedFiles,
            Instant createdAt
    ) {
        FixTaskPatchReviewVo patchReview = new FixTaskPatchReviewVo(
                UUID.randomUUID().toString(),
                taskId,
                decision,
                reason,
                confidence,
                requiredFollowUp,
                List.copyOf(editedFiles),
                createdAt
        );
        patchReviews.add(patchReview);
        return patchReview;
    }

    @Override
    public Optional<FixTaskPatchReviewVo> findLatestPatchReview(String taskId) {
        return patchReviews.stream()
                .filter(patchReview -> patchReview.taskId().equals(taskId))
                .max(Comparator.comparing(FixTaskPatchReviewVo::createdAt));
    }
}
