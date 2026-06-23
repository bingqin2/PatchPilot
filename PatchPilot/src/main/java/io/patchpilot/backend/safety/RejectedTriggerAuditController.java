package io.patchpilot.backend.safety;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditVo;
import io.patchpilot.backend.safety.service.RejectedTriggerAuditService;
import io.patchpilot.backend.safety.service.RejectedTriggerRetryService;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/rejected-triggers")
@RequiredArgsConstructor
public class RejectedTriggerAuditController {

    private final RejectedTriggerAuditService auditService;
    private final RejectedTriggerRetryService retryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RejectedTriggerAuditVo>>> listRejectedTriggers(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String category
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(auditService.listRejectedTriggers(normalizeLimit(limit), category)));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(exception.getMessage()));
        }
    }

    @PostMapping("/{id}/retry")
    public ResponseEntity<ApiResponse<FixTaskVo>> retryRejectedTrigger(@PathVariable String id) {
        try {
            FixTaskVo task = retryService.retryRejectedTrigger(id);
            return ResponseEntity.created(URI.create("/api/tasks/" + task.id())).body(ApiResponse.ok(task));
        } catch (RejectedTriggerRetryService.RejectedTriggerNotFoundException exception) {
            return ResponseEntity.status(404).body(ApiResponse.fail(exception.getMessage()));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(exception.getMessage()));
        } catch (IllegalStateException exception) {
            return ResponseEntity.status(409).body(ApiResponse.fail(exception.getMessage()));
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
