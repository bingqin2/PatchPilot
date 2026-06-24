package io.patchpilot.backend.safety;

import io.patchpilot.backend.safety.domain.TriggerRateLimitDecision;
import io.patchpilot.backend.safety.domain.TriggerRateLimitRequest;
import io.patchpilot.backend.safety.service.TriggerRateLimitService;

public class NoOpTriggerRateLimitService implements TriggerRateLimitService {

    @Override
    public TriggerRateLimitDecision check(TriggerRateLimitRequest request) {
        return TriggerRateLimitDecision.accepted();
    }

    @Override
    public TriggerRateLimitDecision checkAndRecord(TriggerRateLimitRequest request) {
        return TriggerRateLimitDecision.accepted();
    }
}
