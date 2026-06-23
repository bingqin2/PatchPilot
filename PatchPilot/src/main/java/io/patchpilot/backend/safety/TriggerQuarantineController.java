package io.patchpilot.backend.safety;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.safety.domain.ManualTriggerQuarantineRequest;
import io.patchpilot.backend.safety.domain.RecordOperatorSafetyAuditCommand;
import io.patchpilot.backend.safety.domain.ReleaseTriggerQuarantineRequest;
import io.patchpilot.backend.safety.domain.TriggerQuarantineEvidenceVo;
import io.patchpilot.backend.safety.domain.TriggerQuarantineVo;
import io.patchpilot.backend.safety.service.OperatorSafetyAuditService;
import io.patchpilot.backend.safety.service.TriggerQuarantineEvidenceService;
import io.patchpilot.backend.safety.service.TriggerQuarantineRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/trigger-quarantines")
@RequiredArgsConstructor
public class TriggerQuarantineController {

    private final TriggerQuarantineRecordService quarantineRecordService;
    private final OperatorSafetyAuditService operatorSafetyAuditService;
    private final TriggerQuarantineEvidenceService triggerQuarantineEvidenceService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TriggerQuarantineVo>>> listTriggerQuarantines(
            @RequestParam(required = false) Boolean activeOnly,
            @RequestParam(required = false) Integer limit
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(quarantineRecordService.listQuarantines(
                    activeOnly == null || activeOnly,
                    normalizeLimit(limit)
            )));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(exception.getMessage()));
        }
    }

    @GetMapping("/{id}/evidence")
    public ResponseEntity<ApiResponse<TriggerQuarantineEvidenceVo>> getTriggerQuarantineEvidence(
            @PathVariable String id,
            @RequestParam(required = false) Integer limit
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(triggerQuarantineEvidenceService.getEvidence(
                    id,
                    normalizeLimit(limit)
            )));
        } catch (TriggerQuarantineEvidenceService.QuarantineNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.fail(exception.getMessage()));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(exception.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TriggerQuarantineVo>> createManualTriggerQuarantine(
            @RequestBody ManualTriggerQuarantineRequest request
    ) {
        try {
            TriggerQuarantineVo quarantine = quarantineRecordService.createManualQuarantine(
                    request.scope(),
                    request.scopeKey(),
                    request.reason(),
                    request.durationMs() == null ? 0 : request.durationMs(),
                    request.operator()
            );
            operatorSafetyAuditService.recordSafetyAudit(new RecordOperatorSafetyAuditCommand(
                    "MANUAL_QUARANTINE_CREATED",
                    "TRIGGER_QUARANTINE",
                    quarantine.id(),
                    quarantine.scope(),
                    quarantine.scopeKey(),
                    request.operator(),
                    request.reason()
            ));
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(quarantine));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(exception.getMessage()));
        }
    }

    @PostMapping("/{id}/release")
    public ResponseEntity<ApiResponse<TriggerQuarantineVo>> releaseTriggerQuarantine(
            @PathVariable String id,
            @RequestBody ReleaseTriggerQuarantineRequest request
    ) {
        try {
            TriggerQuarantineVo quarantine = quarantineRecordService.releaseQuarantine(
                    id,
                    request.operator(),
                    request.reason()
            );
            operatorSafetyAuditService.recordSafetyAudit(new RecordOperatorSafetyAuditCommand(
                    "TRIGGER_QUARANTINE_RELEASED",
                    "TRIGGER_QUARANTINE",
                    quarantine.id(),
                    quarantine.scope(),
                    quarantine.scopeKey(),
                    request.operator(),
                    request.reason()
            ));
            return ResponseEntity.ok(ApiResponse.ok(quarantine));
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
