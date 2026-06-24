package io.patchpilot.backend.github.webhook;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.github.webhook.domain.WebhookPayloadDiagnosticDto;
import io.patchpilot.backend.github.webhook.domain.WebhookPayloadDiagnosticVo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/github/webhook-diagnostics")
public class WebhookPayloadDiagnosticController {

    private final WebhookPayloadDiagnosticService diagnosticService;

    public WebhookPayloadDiagnosticController(WebhookPayloadDiagnosticService diagnosticService) {
        this.diagnosticService = diagnosticService;
    }

    @PostMapping("/evaluate-payload")
    public ResponseEntity<ApiResponse<WebhookPayloadDiagnosticVo>> evaluatePayload(
            @RequestBody WebhookPayloadDiagnosticDto request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(diagnosticService.evaluate(request)));
    }
}
