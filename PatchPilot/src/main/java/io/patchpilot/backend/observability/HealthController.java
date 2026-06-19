package io.patchpilot.backend.observability;

import io.patchpilot.backend.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class HealthController {

    @GetMapping("/health")
    public ApiResponse<HealthStatus> health() {
        return ApiResponse.ok(new HealthStatus("UP", "patchpilot-backend", Instant.now()));
    }

    public record HealthStatus(String status, String service, Instant timestamp) {
    }
}
