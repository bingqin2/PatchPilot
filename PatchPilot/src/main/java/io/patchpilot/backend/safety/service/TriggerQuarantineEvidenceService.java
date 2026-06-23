package io.patchpilot.backend.safety.service;

import io.patchpilot.backend.safety.domain.TriggerQuarantineEvidenceVo;

public interface TriggerQuarantineEvidenceService {

    TriggerQuarantineEvidenceVo getEvidence(String quarantineId, int limit);

    class QuarantineNotFoundException extends RuntimeException {

        public QuarantineNotFoundException(String message) {
            super(message);
        }
    }
}
