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
        assertThat(entity.getPullRequestUrl()).isNull();
        assertThat(entity.getCompletedAt()).isNull();
        assertThat(entity.getUpdatedAt()).isEqualTo(createdAt);
        assertThat(entity.getLanguage()).isNull();
        assertThat(entity.getBuildSystem()).isNull();
        assertThat(entity.getVerificationCommand()).isNull();
        assertThat(entity.getStatusCommentId()).isNull();
        assertThat(entity.getStatusCommentUrl()).isNull();

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
        assertThat(vo.pullRequestUrl()).isNull();
        assertThat(vo.completedAt()).isNull();
        assertThat(vo.updatedAt()).isEqualTo(createdAt);
        assertThat(vo.language()).isNull();
        assertThat(vo.buildSystem()).isNull();
        assertThat(vo.verificationCommand()).isNull();
        assertThat(vo.statusCommentId()).isNull();
        assertThat(vo.statusCommentUrl()).isNull();
    }

    @Test
    void should_replace_status_while_preserving_task_fields() {
        Instant createdAt = Instant.parse("2026-06-19T01:02:03Z");
        FixTaskEntity current = FixTaskConvert.newEntity("task-123", command("delivery-123"), createdAt);

        current.setPullRequestUrl("https://github.com/octocat/hello-world/pull/7");
        current.setCompletedAt(Instant.parse("2026-06-19T01:04:00Z"));
        current.setUpdatedAt(Instant.parse("2026-06-19T01:04:00Z"));
        current.setStatusCommentId(123L);
        current.setStatusCommentUrl("https://github.com/octocat/hello-world/issues/42#issuecomment-123");
        Instant updatedAt = Instant.parse("2026-06-19T01:05:00Z");

        FixTaskEntity updated = FixTaskConvert.replaceStatus(current, FixTaskStatus.FAILED, "tests failed", updatedAt);

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
        assertThat(updated.getPullRequestUrl()).isEqualTo(current.getPullRequestUrl());
        assertThat(updated.getCompletedAt()).isEqualTo(current.getCompletedAt());
        assertThat(updated.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(updated.getLanguage()).isEqualTo(current.getLanguage());
        assertThat(updated.getBuildSystem()).isEqualTo(current.getBuildSystem());
        assertThat(updated.getVerificationCommand()).isEqualTo(current.getVerificationCommand());
        assertThat(updated.getStatusCommentId()).isEqualTo(123L);
        assertThat(updated.getStatusCommentUrl()).isEqualTo("https://github.com/octocat/hello-world/issues/42#issuecomment-123");
        assertThat(updated.getStatus()).isEqualTo(FixTaskStatus.FAILED.name());
        assertThat(updated.getFailureReason()).isEqualTo("tests failed");
    }

    @Test
    void should_attach_adapter_metadata_without_changing_task_status() {
        Instant createdAt = Instant.parse("2026-06-19T01:02:03Z");
        Instant updatedAt = Instant.parse("2026-06-19T01:03:00Z");
        FixTaskEntity current = FixTaskConvert.newEntity("task-123", command("delivery-123"), createdAt);
        current.setStatus(FixTaskStatus.RUNNING.name());

        FixTaskEntity updated = FixTaskConvert.attachAdapterMetadata(
                current,
                "python",
                "pytest",
                "python3 -m pytest",
                updatedAt
        );
        FixTaskVo vo = FixTaskConvert.toVo(updated);

        assertThat(updated.getId()).isEqualTo("task-123");
        assertThat(updated.getStatus()).isEqualTo(FixTaskStatus.RUNNING.name());
        assertThat(updated.getLanguage()).isEqualTo("python");
        assertThat(updated.getBuildSystem()).isEqualTo("pytest");
        assertThat(updated.getVerificationCommand()).isEqualTo("python3 -m pytest");
        assertThat(updated.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(vo.language()).isEqualTo("python");
        assertThat(vo.buildSystem()).isEqualTo("pytest");
        assertThat(vo.verificationCommand()).isEqualTo("python3 -m pytest");
    }

    @Test
    void should_replace_completed_with_pull_request_url_and_timestamps() {
        Instant createdAt = Instant.parse("2026-06-19T01:02:03Z");
        Instant completedAt = Instant.parse("2026-06-19T01:05:00Z");
        FixTaskEntity current = FixTaskConvert.newEntity("task-123", command("delivery-123"), createdAt);

        FixTaskEntity completed = FixTaskConvert.replaceCompleted(
                current,
                "https://github.com/octocat/hello-world/pull/7",
                completedAt
        );

        assertThat(completed.getId()).isEqualTo("task-123");
        assertThat(completed.getStatus()).isEqualTo(FixTaskStatus.COMPLETED.name());
        assertThat(completed.getFailureReason()).isNull();
        assertThat(completed.getPullRequestUrl()).isEqualTo("https://github.com/octocat/hello-world/pull/7");
        assertThat(completed.getCompletedAt()).isEqualTo(completedAt);
        assertThat(completed.getUpdatedAt()).isEqualTo(completedAt);
        assertThat(completed.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void should_attach_status_comment_metadata() {
        Instant createdAt = Instant.parse("2026-06-19T01:02:03Z");
        Instant updatedAt = Instant.parse("2026-06-19T01:03:00Z");
        FixTaskEntity current = FixTaskConvert.newEntity("task-123", command("delivery-123"), createdAt);

        FixTaskEntity updated = FixTaskConvert.attachStatusComment(
                current,
                123,
                "https://github.com/octocat/hello-world/issues/42#issuecomment-123",
                updatedAt
        );

        assertThat(updated.getId()).isEqualTo("task-123");
        assertThat(updated.getStatus()).isEqualTo(FixTaskStatus.PENDING.name());
        assertThat(updated.getStatusCommentId()).isEqualTo(123L);
        assertThat(updated.getStatusCommentUrl()).isEqualTo("https://github.com/octocat/hello-world/issues/42#issuecomment-123");
        assertThat(updated.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(updated.getCreatedAt()).isEqualTo(createdAt);
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
