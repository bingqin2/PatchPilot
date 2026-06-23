package io.patchpilot.backend.github.webhook.service;

import io.patchpilot.backend.github.webhook.domain.RecordWebhookDeliveryDiagnosticCommand;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticStatus;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticVo;
import io.patchpilot.backend.github.webhook.service.impl.InMemoryWebhookDeliveryDiagnosticService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryWebhookDeliveryDiagnosticServiceTests {

    private final WebhookDeliveryDiagnosticService diagnosticService = new InMemoryWebhookDeliveryDiagnosticService();

    @Test
    void should_record_delivery_diagnostics_and_list_newest_first() {
        WebhookDeliveryDiagnosticVo older = diagnosticService.record(command(
                "delivery-older",
                WebhookDeliveryDiagnosticStatus.IGNORED,
                null,
                "Ignored non-issue_comment event"
        ));
        WebhookDeliveryDiagnosticVo newer = diagnosticService.record(command(
                "delivery-newer",
                WebhookDeliveryDiagnosticStatus.TASK_CREATED,
                "task-123",
                "Task created from /agent fix"
        ));

        List<WebhookDeliveryDiagnosticVo> diagnostics = diagnosticService.listRecent(10);

        assertThat(older.id()).isNotBlank();
        assertThat(older.createdAt()).isNotNull();
        assertThat(newer.id()).isNotBlank();
        assertThat(diagnostics)
                .extracting(WebhookDeliveryDiagnosticVo::deliveryId)
                .containsExactly("delivery-newer", "delivery-older");
        assertThat(diagnostics.get(0).status()).isEqualTo(WebhookDeliveryDiagnosticStatus.TASK_CREATED);
        assertThat(diagnostics.get(0).taskId()).isEqualTo("task-123");
    }

    @Test
    void should_apply_list_limit() {
        diagnosticService.record(command("delivery-1", WebhookDeliveryDiagnosticStatus.IGNORED));
        diagnosticService.record(command("delivery-2", WebhookDeliveryDiagnosticStatus.REJECTED));
        diagnosticService.record(command("delivery-3", WebhookDeliveryDiagnosticStatus.TASK_CREATED));

        List<WebhookDeliveryDiagnosticVo> diagnostics = diagnosticService.listRecent(2);

        assertThat(diagnostics)
                .extracting(WebhookDeliveryDiagnosticVo::deliveryId)
                .containsExactly("delivery-3", "delivery-2");
    }

    private static RecordWebhookDeliveryDiagnosticCommand command(
            String deliveryId,
            WebhookDeliveryDiagnosticStatus status
    ) {
        return command(deliveryId, status, null, "Recorded webhook delivery");
    }

    private static RecordWebhookDeliveryDiagnosticCommand command(
            String deliveryId,
            WebhookDeliveryDiagnosticStatus status,
            String taskId,
            String message
    ) {
        return new RecordWebhookDeliveryDiagnosticCommand(
                deliveryId,
                "issue_comment",
                status,
                taskId,
                "octocat",
                "hello-world",
                42L,
                "alice",
                "/agent fix touch docs/demo.md",
                message
        );
    }
}
