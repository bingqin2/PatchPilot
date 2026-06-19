package io.patchpilot.backend.task.convert;

import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.entity.FixTaskEntity;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class FixTaskConvertTests {

    @Test
    void should_create_pending_entity_from_command_and_convert_to_vo() {
        Instant createdAt = Instant.parse("2026-06-19T01:02:03Z");
        CreateFixTaskCommand command = command("delivery-123");

        FixTaskEntity entity = FixTaskConvert.newEntity("task-123", command, createdAt);
        FixTaskVo vo = FixTaskConvert.toVo(entity);

        assertThat(entity.getId()).isEqualTo("task-123");
        assertThat(entity.getRepositoryOwner()).isEqualTo("octocat");
        assertThat(entity.getRepositoryName()).isEqualTo("hello-world");
        assertThat(entity.getIssueNumber()).isEqualTo(42);
        assertThat(entity.getInstallationId()).isZero();
        assertThat(entity.getTriggerUser()).isEqualTo("alice");
        assertThat(entity.getTriggerComment()).isEqualTo("/agent fix");
        assertThat(entity.getDeliveryId()).isEqualTo("delivery-123");
        assertThat(entity.getCommentId()).isEqualTo(98765);
        assertThat(entity.getStatus()).isEqualTo(FixTaskStatus.PENDING.name());
        assertThat(entity.getFailureReason()).isNull();
        assertThat(entity.getCreatedAt()).isEqualTo(createdAt);

        assertThat(vo.id()).isEqualTo("task-123");
        assertThat(vo.repositoryOwner()).isEqualTo("octocat");
        assertThat(vo.repositoryName()).isEqualTo("hello-world");
        assertThat(vo.issueNumber()).isEqualTo(42);
        assertThat(vo.installationId()).isZero();
        assertThat(vo.triggerUser()).isEqualTo("alice");
        assertThat(vo.triggerComment()).isEqualTo("/agent fix");
        assertThat(vo.deliveryId()).isEqualTo("delivery-123");
        assertThat(vo.commentId()).isEqualTo(98765);
        assertThat(vo.status()).isEqualTo(FixTaskStatus.PENDING);
        assertThat(vo.failureReason()).isNull();
        assertThat(vo.createdAt()).isEqualTo(createdAt);
    }

    @Test
    void should_replace_status_while_preserving_task_fields() {
        Instant createdAt = Instant.parse("2026-06-19T01:02:03Z");
        FixTaskEntity current = FixTaskConvert.newEntity("task-123", command("delivery-123"), createdAt);

        FixTaskEntity updated = FixTaskConvert.replaceStatus(current, FixTaskStatus.FAILED, "tests failed");

        assertThat(updated.getId()).isEqualTo(current.getId());
        assertThat(updated.getRepositoryOwner()).isEqualTo(current.getRepositoryOwner());
        assertThat(updated.getRepositoryName()).isEqualTo(current.getRepositoryName());
        assertThat(updated.getIssueNumber()).isEqualTo(current.getIssueNumber());
        assertThat(updated.getInstallationId()).isEqualTo(current.getInstallationId());
        assertThat(updated.getTriggerUser()).isEqualTo(current.getTriggerUser());
        assertThat(updated.getTriggerComment()).isEqualTo(current.getTriggerComment());
        assertThat(updated.getDeliveryId()).isEqualTo(current.getDeliveryId());
        assertThat(updated.getCommentId()).isEqualTo(current.getCommentId());
        assertThat(updated.getCreatedAt()).isEqualTo(current.getCreatedAt());
        assertThat(updated.getStatus()).isEqualTo(FixTaskStatus.FAILED.name());
        assertThat(updated.getFailureReason()).isEqualTo("tests failed");
    }

    private CreateFixTaskCommand command(String deliveryId) {
        return new CreateFixTaskCommand(
                "octocat",
                "hello-world",
                42,
                0,
                "alice",
                "/agent fix",
                deliveryId,
                98765
        );
    }
}
