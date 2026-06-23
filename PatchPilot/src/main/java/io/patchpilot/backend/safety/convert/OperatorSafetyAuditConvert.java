package io.patchpilot.backend.safety.convert;

import io.patchpilot.backend.safety.domain.OperatorSafetyAuditEntity;
import io.patchpilot.backend.safety.domain.OperatorSafetyAuditVo;
import io.patchpilot.backend.safety.domain.RecordOperatorSafetyAuditCommand;
import io.patchpilot.backend.safety.domain.TriggerQuarantineScope;

import java.time.Instant;

public final class OperatorSafetyAuditConvert {

    private OperatorSafetyAuditConvert() {
    }

    public static OperatorSafetyAuditEntity newEntity(
            String id,
            RecordOperatorSafetyAuditCommand command,
            Instant now
    ) {
        validate(command);
        OperatorSafetyAuditEntity entity = new OperatorSafetyAuditEntity();
        entity.setId(id);
        entity.setAction(trimmed(command.action()));
        entity.setResourceType(trimmed(command.resourceType()));
        entity.setResourceId(trimmed(command.resourceId()));
        entity.setScope(command.scope().name());
        entity.setScopeKey(TriggerQuarantineConvert.normalizedScopeKey(command.scopeKey()));
        entity.setOperator(trimmed(command.operator()));
        entity.setReason(trimmed(command.reason()));
        entity.setCreatedAt(now);
        return entity;
    }

    public static OperatorSafetyAuditVo toVo(OperatorSafetyAuditEntity entity) {
        return new OperatorSafetyAuditVo(
                entity.getId(),
                entity.getAction(),
                entity.getResourceType(),
                entity.getResourceId(),
                TriggerQuarantineScope.valueOf(entity.getScope()),
                entity.getScopeKey(),
                entity.getOperator(),
                entity.getReason(),
                entity.getCreatedAt()
        );
    }

    public static OperatorSafetyAuditVo newVo(
            String id,
            RecordOperatorSafetyAuditCommand command,
            Instant now
    ) {
        validate(command);
        return new OperatorSafetyAuditVo(
                id,
                trimmed(command.action()),
                trimmed(command.resourceType()),
                trimmed(command.resourceId()),
                command.scope(),
                TriggerQuarantineConvert.normalizedScopeKey(command.scopeKey()),
                trimmed(command.operator()),
                trimmed(command.reason()),
                now
        );
    }

    private static void validate(RecordOperatorSafetyAuditCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("audit command is required");
        }
        if (trimmed(command.action()).isBlank()) {
            throw new IllegalArgumentException("action is required");
        }
        if (trimmed(command.resourceType()).isBlank()) {
            throw new IllegalArgumentException("resourceType is required");
        }
        if (trimmed(command.resourceId()).isBlank()) {
            throw new IllegalArgumentException("resourceId is required");
        }
        if (command.scope() == null) {
            throw new IllegalArgumentException("scope is required");
        }
        if (TriggerQuarantineConvert.normalizedScopeKey(command.scopeKey()).isBlank()) {
            throw new IllegalArgumentException("scopeKey is required");
        }
        if (trimmed(command.operator()).isBlank()) {
            throw new IllegalArgumentException("operator is required");
        }
        if (trimmed(command.reason()).isBlank()) {
            throw new IllegalArgumentException("reason is required");
        }
    }

    private static String trimmed(String value) {
        return value == null ? "" : value.trim();
    }
}
