package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.entity.FixTaskPatchReviewEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskPatchReviewVo;
import io.patchpilot.backend.task.mapper.FixTaskPatchReviewMapper;
import io.patchpilot.backend.task.service.impl.MyBatisFixTaskPatchReviewService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisFixTaskPatchReviewServiceTests {

    private final FixTaskPatchReviewMapper patchReviewMapper = mock(FixTaskPatchReviewMapper.class);
    private final FixTaskPatchReviewService patchReviewService = new MyBatisFixTaskPatchReviewService(patchReviewMapper);

    @Test
    void should_insert_patch_review() {
        when(patchReviewMapper.insert(any(FixTaskPatchReviewEntity.class))).thenReturn(1);
        ArgumentCaptor<FixTaskPatchReviewEntity> entityCaptor = ArgumentCaptor.forClass(FixTaskPatchReviewEntity.class);

        FixTaskPatchReviewVo patchReview = patchReviewService.recordPatchReview(
                "task-123",
                "APPROVE",
                "The generated edit matches the issue.",
                "HIGH",
                "Run verification.",
                List.of("src/main/App.java", "docs/demo.md"),
                Instant.parse("2026-06-20T04:00:14Z")
        );

        verify(patchReviewMapper).insert(entityCaptor.capture());
        FixTaskPatchReviewEntity insertedEntity = entityCaptor.getValue();
        assertThat(insertedEntity.getId()).isNotBlank();
        assertThat(insertedEntity.getTaskId()).isEqualTo("task-123");
        assertThat(insertedEntity.getDecision()).isEqualTo("APPROVE");
        assertThat(insertedEntity.getReason()).isEqualTo("The generated edit matches the issue.");
        assertThat(insertedEntity.getConfidence()).isEqualTo("HIGH");
        assertThat(insertedEntity.getRequiredFollowUp()).isEqualTo("Run verification.");
        assertThat(insertedEntity.getEditedFiles()).isEqualTo("src/main/App.java\ndocs/demo.md");
        assertThat(patchReview.id()).isEqualTo(insertedEntity.getId());
    }

    @Test
    void should_find_latest_patch_review() {
        FixTaskPatchReviewEntity older = entity(
                "patch-review-older",
                "task-123",
                "APPROVE",
                Instant.parse("2026-06-20T04:00:00Z")
        );
        FixTaskPatchReviewEntity newer = entity(
                "patch-review-newer",
                "task-123",
                "REJECT",
                Instant.parse("2026-06-20T04:00:14Z")
        );
        when(patchReviewMapper.selectList(any())).thenReturn(List.of(older, newer));

        assertThat(patchReviewService.findLatestPatchReview("task-123"))
                .get()
                .satisfies(review -> {
                    assertThat(review.id()).isEqualTo("patch-review-newer");
                    assertThat(review.decision()).isEqualTo("REJECT");
                });
    }

    private static FixTaskPatchReviewEntity entity(String id, String taskId, String decision, Instant createdAt) {
        FixTaskPatchReviewEntity entity = new FixTaskPatchReviewEntity();
        entity.setId(id);
        entity.setTaskId(taskId);
        entity.setDecision(decision);
        entity.setReason("review reason");
        entity.setConfidence("HIGH");
        entity.setRequiredFollowUp("Run verification.");
        entity.setEditedFiles("src/main/App.java");
        entity.setCreatedAt(createdAt);
        return entity;
    }
}
