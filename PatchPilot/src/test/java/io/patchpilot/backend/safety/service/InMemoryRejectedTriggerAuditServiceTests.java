package io.patchpilot.backend.safety.service;

import io.patchpilot.backend.safety.domain.RecordRejectedTriggerCommand;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditVo;
import io.patchpilot.backend.safety.service.impl.InMemoryRejectedTriggerAuditService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryRejectedTriggerAuditServiceTests {

    private final RejectedTriggerAuditService auditService = new InMemoryRejectedTriggerAuditService();

    @Test
    void should_record_rejected_trigger_and_list_newest_first() {
        RejectedTriggerAuditVo older = auditService.recordRejectedTrigger(command(
                "issue_comment",
                "delivery-older",
                "octocat",
                "hello-world",
                42,
                "alice",
                "/agent fix delete the repository",
                "Unsafe request rejected: destructive or secret-exfiltration instruction"
        ));
        RejectedTriggerAuditVo newer = auditService.recordRejectedTrigger(command(
                "manual",
                "manual-newer",
                "bingqin2",
                "PatchPilot",
                7,
                "unknown-user",
                "/agent fix touch docs/demo.md",
                "Unsafe request rejected: trigger user is not allowed"
        ));

        List<RejectedTriggerAuditVo> audits = auditService.listRejectedTriggers(10);

        assertThat(older.id()).isNotBlank();
        assertThat(older.createdAt()).isNotNull();
        assertThat(newer.id()).isNotBlank();
        assertThat(newer.commentId()).isEqualTo(456L);
        assertThat(newer.commentUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/issues/7#issuecomment-456");
        assertThat(audits)
                .extracting(RejectedTriggerAuditVo::deliveryId)
                .containsExactly("manual-newer", "delivery-older");
        assertThat(audits.get(0).reason()).isEqualTo("Unsafe request rejected: trigger user is not allowed");
    }

    @Test
    void should_apply_list_limit() {
        auditService.recordRejectedTrigger(command("manual", "manual-1"));
        auditService.recordRejectedTrigger(command("manual", "manual-2"));
        auditService.recordRejectedTrigger(command("manual", "manual-3"));

        List<RejectedTriggerAuditVo> audits = auditService.listRejectedTriggers(2);

        assertThat(audits)
                .extracting(RejectedTriggerAuditVo::deliveryId)
                .containsExactly("manual-3", "manual-2");
    }

    @Test
    void should_find_rejected_trigger_by_id() {
        RejectedTriggerAuditVo audit = auditService.recordRejectedTrigger(command("manual", "manual-find"));

        assertThat(auditService.findRejectedTrigger(audit.id()))
                .hasValueSatisfying(found -> {
                    assertThat(found.id()).isEqualTo(audit.id());
                    assertThat(found.deliveryId()).isEqualTo("manual-find");
                });
        assertThat(auditService.findRejectedTrigger("missing-audit")).isEmpty();
    }

    @Test
    void should_mark_rejected_trigger_as_retried() {
        RejectedTriggerAuditVo audit = auditService.recordRejectedTrigger(command("manual", "manual-retried"));

        RejectedTriggerAuditVo retried = auditService.markRetried(
                audit.id(),
                "task-123",
                java.time.Instant.parse("2026-06-21T03:00:00Z")
        );

        assertThat(retried.retriedTaskId()).isEqualTo("task-123");
        assertThat(retried.retriedAt()).isEqualTo(java.time.Instant.parse("2026-06-21T03:00:00Z"));
        assertThat(auditService.findRejectedTrigger(audit.id()))
                .hasValueSatisfying(found -> assertThat(found.retriedTaskId()).isEqualTo("task-123"));
    }

    private static RecordRejectedTriggerCommand command(String source, String deliveryId) {
        return command(
                source,
                deliveryId,
                "octocat",
                "hello-world",
                42,
                "alice",
                "/agent fix",
                "Unsafe request rejected"
        );
    }

    private static RecordRejectedTriggerCommand command(
            String source,
            String deliveryId,
            String repositoryOwner,
            String repositoryName,
            long issueNumber,
            String triggerUser,
            String triggerComment,
            String reason
    ) {
        return new RecordRejectedTriggerCommand(
                source,
                deliveryId,
                repositoryOwner,
                repositoryName,
                issueNumber,
                triggerUser,
                triggerComment,
                reason,
                "manual".equals(source) ? 456L : null,
                "manual".equals(source) ? "https://github.com/bingqin2/PatchPilot/issues/7#issuecomment-456" : null
        );
    }
}
