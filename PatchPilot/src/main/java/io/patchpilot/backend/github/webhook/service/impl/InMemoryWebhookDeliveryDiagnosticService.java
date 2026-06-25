package io.patchpilot.backend.github.webhook.service.impl;

import io.patchpilot.backend.github.webhook.domain.RecordWebhookDeliveryDiagnosticCommand;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticVo;
import io.patchpilot.backend.github.webhook.service.WebhookDeliveryDiagnosticService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Profile("default")
public class InMemoryWebhookDeliveryDiagnosticService implements WebhookDeliveryDiagnosticService {

    private final List<WebhookDeliveryDiagnosticVo> diagnostics = new CopyOnWriteArrayList<>();

    @Override
    public WebhookDeliveryDiagnosticVo record(RecordWebhookDeliveryDiagnosticCommand command) {
        WebhookDeliveryDiagnosticVo diagnostic = new WebhookDeliveryDiagnosticVo(
                UUID.randomUUID().toString(),
                command.deliveryId(),
                command.event(),
                command.status(),
                command.taskId(),
                command.repositoryOwner(),
                command.repositoryName(),
                command.issueNumber(),
                command.triggerUser(),
                command.triggerComment(),
                command.message(),
                command.outcomeType(),
                command.outcomeId(),
                command.outcomeUrl(),
                Instant.now()
        );
        diagnostics.add(diagnostic);
        return diagnostic;
    }

    @Override
    public List<WebhookDeliveryDiagnosticVo> listRecent(int limit) {
        return diagnostics.stream()
                .sorted(Comparator.comparing(WebhookDeliveryDiagnosticVo::createdAt).reversed())
                .limit(limit)
                .toList();
    }
}
