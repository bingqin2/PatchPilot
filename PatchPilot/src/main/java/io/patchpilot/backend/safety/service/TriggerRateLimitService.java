package io.patchpilot.backend.safety.service;

import io.patchpilot.backend.safety.domain.TriggerRateLimitDecision;
import io.patchpilot.backend.safety.domain.TriggerRateLimitRequest;

public interface TriggerRateLimitService {

    default TriggerRateLimitDecision check(TriggerRateLimitRequest request) {
        return checkAndRecord(request);
    }

    TriggerRateLimitDecision checkAndRecord(TriggerRateLimitRequest request);
}
