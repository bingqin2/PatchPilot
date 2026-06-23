package io.patchpilot.backend.safety;

import io.patchpilot.backend.safety.domain.TriggerQuarantineDecision;
import io.patchpilot.backend.safety.domain.TriggerQuarantineRequest;
import io.patchpilot.backend.safety.service.TriggerQuarantineService;

public class NoOpTriggerQuarantineService implements TriggerQuarantineService {

    @Override
    public TriggerQuarantineDecision check(TriggerQuarantineRequest request) {
        return TriggerQuarantineDecision.accepted();
    }
}
