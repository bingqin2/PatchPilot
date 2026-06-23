package io.patchpilot.backend.safety.service;

import io.patchpilot.backend.safety.domain.RecordRejectedTriggerCommand;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditEntity;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditVo;
import io.patchpilot.backend.safety.mapper.RejectedTriggerAuditMapper;
import io.patchpilot.backend.safety.service.impl.MyBatisRejectedTriggerAuditService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisRejectedTriggerAuditServiceTests {

    private final RejectedTriggerAuditMapper auditMapper = mock(RejectedTriggerAuditMapper.class);
    private final RejectedTriggerAuditService auditService = new MyBatisRejectedTriggerAuditService(auditMapper);

    @Test
    void should_insert_rejected_trigger_audit() {
        when(auditMapper.insert(any(RejectedTriggerAuditEntity.class))).thenReturn(1);
        ArgumentCaptor<RejectedTriggerAuditEntity> entityCaptor =
                ArgumentCaptor.forClass(RejectedTriggerAuditEntity.class);

        RejectedTriggerAuditVo audit = auditService.recordRejectedTrigger(new RecordRejectedTriggerCommand(
                "issue_comment",
                "delivery-123",
                "octocat",
                "hello-world",
                42L,
                "alice",
                "/agent fix delete the repository",
                "Unsafe request rejected: destructive or secret-exfiltration instruction",
                456L,
                "https://github.com/octocat/hello-world/issues/42#issuecomment-456"
        ));

        verify(auditMapper).insert(entityCaptor.capture());
        RejectedTriggerAuditEntity entity = entityCaptor.getValue();
        assertThat(entity.getId()).isNotBlank();
        assertThat(entity.getSource()).isEqualTo("issue_comment");
        assertThat(entity.getDeliveryId()).isEqualTo("delivery-123");
        assertThat(entity.getRepositoryOwner()).isEqualTo("octocat");
        assertThat(entity.getRepositoryName()).isEqualTo("hello-world");
        assertThat(entity.getIssueNumber()).isEqualTo(42);
        assertThat(entity.getTriggerUser()).isEqualTo("alice");
        assertThat(entity.getTriggerComment()).isEqualTo("/agent fix delete the repository");
        assertThat(entity.getReason()).isEqualTo("Unsafe request rejected: destructive or secret-exfiltration instruction");
        assertThat(entity.getCommentId()).isEqualTo(456L);
        assertThat(entity.getCommentUrl()).isEqualTo("https://github.com/octocat/hello-world/issues/42#issuecomment-456");
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(audit.id()).isEqualTo(entity.getId());
        assertThat(audit.commentId()).isEqualTo(456L);
        assertThat(audit.commentUrl()).isEqualTo("https://github.com/octocat/hello-world/issues/42#issuecomment-456");
    }

    @Test
    void should_list_rejected_trigger_audits_newest_first() {
        RejectedTriggerAuditEntity older = entity("audit-older", Instant.parse("2026-06-21T01:00:00Z"));
        RejectedTriggerAuditEntity newer = entity("audit-newer", Instant.parse("2026-06-21T02:00:00Z"));
        when(auditMapper.selectList(any())).thenReturn(List.of(older, newer));

        List<RejectedTriggerAuditVo> audits = auditService.listRejectedTriggers(50);

        assertThat(audits)
                .extracting(RejectedTriggerAuditVo::id)
                .containsExactly("audit-newer", "audit-older");
    }

    @Test
    void should_find_rejected_trigger_by_id() {
        RejectedTriggerAuditEntity entity = entity("audit-123", Instant.parse("2026-06-21T01:00:00Z"));
        entity.setRetriedTaskId("task-123");
        entity.setRetriedAt(Instant.parse("2026-06-21T03:00:00Z"));
        when(auditMapper.selectById("audit-123")).thenReturn(entity);

        assertThat(auditService.findRejectedTrigger("audit-123"))
                .hasValueSatisfying(found -> {
                    assertThat(found.id()).isEqualTo("audit-123");
                    assertThat(found.repositoryOwner()).isEqualTo("octocat");
                    assertThat(found.commentUrl()).isEqualTo("https://github.com/octocat/hello-world/issues/42#issuecomment-456");
                    assertThat(found.retriedTaskId()).isEqualTo("task-123");
                    assertThat(found.retriedAt()).isEqualTo(Instant.parse("2026-06-21T03:00:00Z"));
                });
        assertThat(auditService.findRejectedTrigger("missing-audit")).isEmpty();
    }

    @Test
    void should_mark_rejected_trigger_as_retried() {
        when(auditMapper.updateById(any(RejectedTriggerAuditEntity.class))).thenReturn(1);
        RejectedTriggerAuditEntity updated = entity("audit-123", Instant.parse("2026-06-21T01:00:00Z"));
        updated.setRetriedTaskId("task-123");
        updated.setRetriedAt(Instant.parse("2026-06-21T03:00:00Z"));
        when(auditMapper.selectById("audit-123")).thenReturn(updated);
        ArgumentCaptor<RejectedTriggerAuditEntity> entityCaptor =
                ArgumentCaptor.forClass(RejectedTriggerAuditEntity.class);

        RejectedTriggerAuditVo retried = auditService.markRetried(
                "audit-123",
                "task-123",
                Instant.parse("2026-06-21T03:00:00Z")
        );

        verify(auditMapper).updateById(entityCaptor.capture());
        assertThat(entityCaptor.getValue().getId()).isEqualTo("audit-123");
        assertThat(entityCaptor.getValue().getRetriedTaskId()).isEqualTo("task-123");
        assertThat(entityCaptor.getValue().getRetriedAt()).isEqualTo(Instant.parse("2026-06-21T03:00:00Z"));
        assertThat(retried.retriedTaskId()).isEqualTo("task-123");
        assertThat(retried.retriedAt()).isEqualTo(Instant.parse("2026-06-21T03:00:00Z"));
    }

    private static RejectedTriggerAuditEntity entity(String id, Instant createdAt) {
        RejectedTriggerAuditEntity entity = new RejectedTriggerAuditEntity();
        entity.setId(id);
        entity.setSource("issue_comment");
        entity.setDeliveryId("delivery-" + id);
        entity.setRepositoryOwner("octocat");
        entity.setRepositoryName("hello-world");
        entity.setIssueNumber(42L);
        entity.setTriggerUser("alice");
        entity.setTriggerComment("/agent fix");
        entity.setReason("Unsafe request rejected");
        entity.setCommentId(456L);
        entity.setCommentUrl("https://github.com/octocat/hello-world/issues/42#issuecomment-456");
        entity.setCreatedAt(createdAt);
        return entity;
    }
}
