package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskPatchReviewVo;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskPatchReviewService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryFixTaskPatchReviewServiceTests {

    private final FixTaskPatchReviewService patchReviewService = new InMemoryFixTaskPatchReviewService();

    @Test
    void should_record_and_return_latest_patch_review() {
        patchReviewService.recordPatchReview(
                "task-123",
                "APPROVE",
                "Older review",
                "LOW",
                "Regenerate if tests fail.",
                List.of("docs/old.md"),
                Instant.parse("2026-06-20T04:00:00Z")
        );
        FixTaskPatchReviewVo latest = patchReviewService.recordPatchReview(
                "task-123",
                "REJECT",
                "Latest review rejected unsafe edit.",
                "HIGH",
                "Do not write the proposed patch.",
                List.of("src/main/App.java"),
                Instant.parse("2026-06-20T04:00:14Z")
        );

        assertThat(patchReviewService.findLatestPatchReview("task-123")).contains(latest);
        assertThat(patchReviewService.findLatestPatchReview("missing-task")).isEmpty();
    }
}
