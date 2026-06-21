package io.patchpilot.backend.safety;

import io.patchpilot.backend.safety.domain.TriggerIntentClassificationRequest;
import io.patchpilot.backend.safety.domain.TriggerIntentDecision;
import io.patchpilot.backend.safety.service.TriggerIntentClassifier;

public class NoOpTriggerIntentClassifier implements TriggerIntentClassifier {

    @Override
    public TriggerIntentDecision classify(TriggerIntentClassificationRequest request) {
        return TriggerIntentDecision.shouldExecute("Model trigger classification is disabled");
    }
}
