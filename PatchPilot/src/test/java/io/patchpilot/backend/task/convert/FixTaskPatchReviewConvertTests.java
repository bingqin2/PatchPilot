package io.patchpilot.backend.task.convert;

import io.patchpilot.backend.task.domain.entity.FixTaskPatchReviewEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskPatchReviewVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FixTaskPatchReviewConvertTests {

    @Test
    void should_convert_between_entity_and_vo() {
        Instant createdAt = Instant.parse("2026-06-20T04:00:14Z");

        FixTaskPatchReviewEntity entity = FixTaskPatchReviewConvert.newEntity(
                "patch-review-123",
                "task-123",
                "APPROVE",
                "The generated edit matches the issue.",
                "HIGH",
                "Run verification.",
                List.of("src/main/App.java", "docs/demo.md"),
                createdAt
        );
        FixTaskPatchReviewVo vo = FixTaskPatchReviewConvert.toVo(entity);

        assertThat(entity.getId()).isEqualTo("patch-review-123");
        assertThat(entity.getTaskId()).isEqualTo("task-123");
        assertThat(entity.getDecision()).isEqualTo("APPROVE");
        assertThat(entity.getReason()).isEqualTo("The generated edit matches the issue.");
        assertThat(entity.getConfidence()).isEqualTo("HIGH");
        assertThat(entity.getRequiredFollowUp()).isEqualTo("Run verification.");
        assertThat(entity.getEditedFiles()).isEqualTo("src/main/App.java\ndocs/demo.md");
        assertThat(entity.getCreatedAt()).isEqualTo(createdAt);
        assertThat(vo.id()).isEqualTo("patch-review-123");
        assertThat(vo.taskId()).isEqualTo("task-123");
        assertThat(vo.decision()).isEqualTo("APPROVE");
        assertThat(vo.reason()).isEqualTo("The generated edit matches the issue.");
        assertThat(vo.confidence()).isEqualTo("HIGH");
        assertThat(vo.requiredFollowUp()).isEqualTo("Run verification.");
        assertThat(vo.editedFiles()).containsExactly("src/main/App.java", "docs/demo.md");
        assertThat(vo.createdAt()).isEqualTo(createdAt);
    }
}
