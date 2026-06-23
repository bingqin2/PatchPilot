package io.patchpilot.backend.github.webhook;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticVo;
import io.patchpilot.backend.github.webhook.service.WebhookDeliveryDiagnosticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/github/webhook-deliveries")
@RequiredArgsConstructor
public class WebhookDeliveryDiagnosticController {

    private final WebhookDeliveryDiagnosticService diagnosticService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<WebhookDeliveryDiagnosticVo>>> listRecent(
            @RequestParam(required = false) Integer limit
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(diagnosticService.listRecent(normalizeLimit(limit))));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(exception.getMessage()));
        }
    }

    private static int normalizeLimit(Integer limit) {
        int normalizedLimit = limit == null ? 50 : limit;
        if (normalizedLimit < 1 || normalizedLimit > 100) {
            throw new IllegalArgumentException("limit must be between 1 and 100");
        }
        return normalizedLimit;
    }
}
