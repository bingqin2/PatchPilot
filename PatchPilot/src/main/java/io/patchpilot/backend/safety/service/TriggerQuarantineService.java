package io.patchpilot.backend.safety.service;

import io.patchpilot.backend.safety.domain.TriggerQuarantineDecision;
import io.patchpilot.backend.safety.domain.TriggerQuarantineRequest;

public interface TriggerQuarantineService {

    TriggerQuarantineDecision check(TriggerQuarantineRequest request);
}
