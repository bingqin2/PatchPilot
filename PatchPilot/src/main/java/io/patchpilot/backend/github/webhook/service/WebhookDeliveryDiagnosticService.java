package io.patchpilot.backend.github.webhook.service;

import io.patchpilot.backend.github.webhook.domain.RecordWebhookDeliveryDiagnosticCommand;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticVo;

import java.util.List;

public interface WebhookDeliveryDiagnosticService {

    WebhookDeliveryDiagnosticVo record(RecordWebhookDeliveryDiagnosticCommand command);

    List<WebhookDeliveryDiagnosticVo> listRecent(int limit);
}
