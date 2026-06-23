package io.patchpilot.backend.safety.service;

import io.patchpilot.backend.safety.domain.RecordTriggerQuarantineCommand;
import io.patchpilot.backend.safety.domain.TriggerQuarantineScope;
import io.patchpilot.backend.safety.domain.TriggerQuarantineVo;

import java.util.List;
import java.util.Optional;

public interface TriggerQuarantineRecordService {

    TriggerQuarantineVo recordQuarantine(RecordTriggerQuarantineCommand command);

    Optional<TriggerQuarantineVo> findActiveQuarantine(TriggerQuarantineScope scope, String scopeKey);

    List<TriggerQuarantineVo> listQuarantines(boolean activeOnly, int limit);
}
