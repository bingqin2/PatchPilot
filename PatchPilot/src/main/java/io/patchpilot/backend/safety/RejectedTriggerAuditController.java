package io.patchpilot.backend.safety;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditVo;
import io.patchpilot.backend.safety.service.RejectedTriggerAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rejected-triggers")
@RequiredArgsConstructor
public class RejectedTriggerAuditController {

    private final RejectedTriggerAuditService auditService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RejectedTriggerAuditVo>>> listRejectedTriggers(
            @RequestParam(required = false) Integer limit
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(auditService.listRejectedTriggers(normalizeLimit(limit))));
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
