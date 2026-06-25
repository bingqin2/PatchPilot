package io.patchpilot.backend.safety;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.safety.domain.OperatorSafetyAuditVo;
import io.patchpilot.backend.safety.domain.OperatorSafetyAuditQuery;
import io.patchpilot.backend.safety.domain.TriggerQuarantineScope;
import io.patchpilot.backend.safety.service.OperatorSafetyAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/operator-safety-audits", "/api/admin-audit-events"})
@RequiredArgsConstructor
public class OperatorSafetyAuditController {

    private final OperatorSafetyAuditService auditService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<OperatorSafetyAuditVo>>> listSafetyAudits(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String resourceType,
            @RequestParam(required = false) String resourceId,
            @RequestParam(required = false) TriggerQuarantineScope scope,
            @RequestParam(required = false) String scopeKey,
            @RequestParam(required = false) String operator
    ) {
        try {
            OperatorSafetyAuditQuery query = new OperatorSafetyAuditQuery(
                    normalizeLimit(limit),
                    action,
                    resourceType,
                    resourceId,
                    scope,
                    scopeKey,
                    operator
            );
            return ResponseEntity.ok(ApiResponse.ok(auditService.listSafetyAudits(query)));
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
