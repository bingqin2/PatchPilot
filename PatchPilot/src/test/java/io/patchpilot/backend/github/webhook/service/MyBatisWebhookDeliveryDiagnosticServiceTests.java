package io.patchpilot.backend.github.webhook.service;

import io.patchpilot.backend.github.webhook.domain.RecordWebhookDeliveryDiagnosticCommand;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticEntity;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticStatus;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticVo;
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
                "Task created from /agent fix"
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
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(diagnostic.id()).isEqualTo(entity.getId());
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
        entity.setCreatedAt(createdAt);
        return entity;
    }
}
