package io.patchpilot.backend.safety;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.safety.domain.ManualTriggerQuarantineRequest;
import io.patchpilot.backend.safety.domain.ReleaseTriggerQuarantineRequest;
import io.patchpilot.backend.safety.domain.TriggerQuarantineVo;
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

    @PostMapping
    public ResponseEntity<ApiResponse<TriggerQuarantineVo>> createManualTriggerQuarantine(
            @RequestBody ManualTriggerQuarantineRequest request
    ) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(
                    quarantineRecordService.createManualQuarantine(
                            request.scope(),
                            request.scopeKey(),
                            request.reason(),
                            request.durationMs() == null ? 0 : request.durationMs(),
                            request.operator()
                    )
            ));
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
            return ResponseEntity.ok(ApiResponse.ok(quarantineRecordService.releaseQuarantine(
                    id,
                    request.operator(),
                    request.reason()
            )));
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
