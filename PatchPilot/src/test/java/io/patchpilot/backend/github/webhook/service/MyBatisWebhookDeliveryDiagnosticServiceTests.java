package io.patchpilot.backend.github.webhook.service;

import io.patchpilot.backend.github.webhook.domain.RecordWebhookDeliveryDiagnosticCommand;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticEntity;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticStatus;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticVo;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryOutcomeType;
import io.patchpilot.backend.github.webhook.mapper.WebhookDeliveryDiagnosticMapper;
import io.patchpilot.backend.github.webhook.service.impl.MyBatisWebhookDeliveryDiagnosticService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisWebhookDeliveryDiagnosticServiceTests {

    private final WebhookDeliveryDiagnosticMapper diagnosticMapper = mock(WebhookDeliveryDiagnosticMapper.class);
    private final WebhookDeliveryDiagnosticService diagnosticService =
            new MyBatisWebhookDeliveryDiagnosticService(diagnosticMapper);

    @Test
    void should_insert_webhook_delivery_diagnostic() {
        when(diagnosticMapper.insert(any(WebhookDeliveryDiagnosticEntity.class))).thenReturn(1);
        ArgumentCaptor<WebhookDeliveryDiagnosticEntity> entityCaptor =
                ArgumentCaptor.forClass(WebhookDeliveryDiagnosticEntity.class);

        WebhookDeliveryDiagnosticVo diagnostic = diagnosticService.record(new RecordWebhookDeliveryDiagnosticCommand(
                "delivery-123",
                "issue_comment",
                WebhookDeliveryDiagnosticStatus.TASK_CREATED,
                "task-123",
                "octocat",
                "hello-world",
                42L,
                "alice",
                "/agent fix touch docs/demo.md",
                "Task created from /agent fix",
                WebhookDeliveryOutcomeType.TASK,
                "task-123",
                "/tasks/task-123"
        ));

        verify(diagnosticMapper).insert(entityCaptor.capture());
        WebhookDeliveryDiagnosticEntity entity = entityCaptor.getValue();
        assertThat(entity.getId()).isNotBlank();
        assertThat(entity.getDeliveryId()).isEqualTo("delivery-123");
        assertThat(entity.getEvent()).isEqualTo("issue_comment");
        assertThat(entity.getStatus()).isEqualTo(WebhookDeliveryDiagnosticStatus.TASK_CREATED);
        assertThat(entity.getTaskId()).isEqualTo("task-123");
        assertThat(entity.getRepositoryOwner()).isEqualTo("octocat");
        assertThat(entity.getRepositoryName()).isEqualTo("hello-world");
        assertThat(entity.getIssueNumber()).isEqualTo(42L);
        assertThat(entity.getTriggerUser()).isEqualTo("alice");
        assertThat(entity.getTriggerComment()).isEqualTo("/agent fix touch docs/demo.md");
        assertThat(entity.getMessage()).isEqualTo("Task created from /agent fix");
        assertThat(entity.getOutcomeType()).isEqualTo(WebhookDeliveryOutcomeType.TASK);
        assertThat(entity.getOutcomeId()).isEqualTo("task-123");
        assertThat(entity.getOutcomeUrl()).isEqualTo("/tasks/task-123");
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(diagnostic.id()).isEqualTo(entity.getId());
        assertThat(diagnostic.redeliveryRecommended()).isFalse();
        assertThat(diagnostic.operatorAction())
                .isEqualTo("Task was created. Do not redeliver this webhook unless you intentionally want GitHub to report a duplicate delivery.");
        assertThat(diagnostic.outcomeType()).isEqualTo(WebhookDeliveryOutcomeType.TASK);
        assertThat(diagnostic.outcomeId()).isEqualTo("task-123");
        assertThat(diagnostic.outcomeUrl()).isEqualTo("/tasks/task-123");
    }

    @Test
    void should_list_webhook_delivery_diagnostics_newest_first() {
        WebhookDeliveryDiagnosticEntity older = entity("diagnostic-older", Instant.parse("2026-06-23T01:00:00Z"));
        WebhookDeliveryDiagnosticEntity newer = entity("diagnostic-newer", Instant.parse("2026-06-23T02:00:00Z"));
        when(diagnosticMapper.selectList(any())).thenReturn(List.of(older, newer));

        List<WebhookDeliveryDiagnosticVo> diagnostics = diagnosticService.listRecent(50);

        assertThat(diagnostics)
                .extracting(WebhookDeliveryDiagnosticVo::id)
                .containsExactly("diagnostic-newer", "diagnostic-older");
    }

    @Test
    void should_derive_redelivery_guidance_from_persisted_status() {
        WebhookDeliveryDiagnosticEntity entity = entity(
                "diagnostic-invalid-signature",
                Instant.parse("2026-06-23T02:00:00Z")
        );
        entity.setStatus(WebhookDeliveryDiagnosticStatus.INVALID_SIGNATURE);
        entity.setTaskId(null);
        entity.setMessage("Invalid GitHub webhook signature");
        entity.setOutcomeType(null);
        entity.setOutcomeId(null);
        entity.setOutcomeUrl(null);
        when(diagnosticMapper.selectList(any())).thenReturn(List.of(entity));

        List<WebhookDeliveryDiagnosticVo> diagnostics = diagnosticService.listRecent(50);

        assertThat(diagnostics).hasSize(1);
        assertThat(diagnostics.get(0).redeliveryRecommended()).isTrue();
        assertThat(diagnostics.get(0).operatorAction())
                .isEqualTo("Fix the webhook secret or payload URL first, then use GitHub's Redeliver action for this delivery.");
        assertThat(diagnostics.get(0).outcomeType()).isEqualTo(WebhookDeliveryOutcomeType.ERROR);
    }

    private static WebhookDeliveryDiagnosticEntity entity(String id, Instant createdAt) {
        WebhookDeliveryDiagnosticEntity entity = new WebhookDeliveryDiagnosticEntity();
        entity.setId(id);
        entity.setDeliveryId("delivery-" + id);
        entity.setEvent("issue_comment");
        entity.setStatus(WebhookDeliveryDiagnosticStatus.TASK_CREATED);
        entity.setTaskId("task-" + id);
        entity.setRepositoryOwner("octocat");
        entity.setRepositoryName("hello-world");
        entity.setIssueNumber(42L);
        entity.setTriggerUser("alice");
        entity.setTriggerComment("/agent fix");
        entity.setMessage("Task created from /agent fix");
        entity.setOutcomeType(WebhookDeliveryOutcomeType.TASK);
        entity.setOutcomeId("task-" + id);
        entity.setOutcomeUrl("/tasks/task-" + id);
        entity.setCreatedAt(createdAt);
        return entity;
    }
}
