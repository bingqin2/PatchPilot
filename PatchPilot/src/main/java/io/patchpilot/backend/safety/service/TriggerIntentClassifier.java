package io.patchpilot.backend.safety.service;

import io.patchpilot.backend.safety.domain.TriggerIntentClassificationRequest;
import io.patchpilot.backend.safety.domain.TriggerIntentDecision;

public interface TriggerIntentClassifier {

    TriggerIntentDecision classify(TriggerIntentClassificationRequest request);
}
