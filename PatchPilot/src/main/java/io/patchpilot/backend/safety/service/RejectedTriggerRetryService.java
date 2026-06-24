package io.patchpilot.backend.safety.service;

import io.patchpilot.backend.task.domain.vo.FixTaskVo;

public interface RejectedTriggerRetryService {

    FixTaskVo retryRejectedTrigger(String rejectedTriggerId);

    class RejectedTriggerNotFoundException extends RuntimeException {

        public RejectedTriggerNotFoundException(String message) {
            super(message);
        }
    }

    class RejectedTriggerRetryNotAllowedException extends RuntimeException {

        public RejectedTriggerRetryNotAllowedException(String message) {
            super(message);
        }
    }
}
