package io.patchpilot.backend.safety.service;

import io.patchpilot.backend.safety.domain.RecordTriggerQuarantineCommand;
import io.patchpilot.backend.safety.domain.TriggerQuarantineScope;
import io.patchpilot.backend.safety.domain.TriggerQuarantineVo;

import java.util.List;
import java.util.Optional;

public interface TriggerQuarantineRecordService {

    TriggerQuarantineVo recordQuarantine(RecordTriggerQuarantineCommand command);

    TriggerQuarantineVo createManualQuarantine(
            TriggerQuarantineScope scope,
            String scopeKey,
            String reason,
            long durationMs,
            String operator
    );

    TriggerQuarantineVo releaseQuarantine(String id, String operator, String reason);

    Optional<TriggerQuarantineVo> findActiveQuarantine(TriggerQuarantineScope scope, String scopeKey);

    Optional<TriggerQuarantineVo> findQuarantine(TriggerQuarantineScope scope, String scopeKey);

    List<TriggerQuarantineVo> listQuarantines(boolean activeOnly, int limit);
}
