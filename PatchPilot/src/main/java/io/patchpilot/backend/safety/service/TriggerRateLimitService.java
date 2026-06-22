package io.patchpilot.backend.safety.service;

import io.patchpilot.backend.safety.domain.TriggerRateLimitDecision;
import io.patchpilot.backend.safety.domain.TriggerRateLimitRequest;

public interface TriggerRateLimitService {

    TriggerRateLimitDecision checkAndRecord(TriggerRateLimitRequest request);
}
