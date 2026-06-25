package io.patchpilot.backend.safety.domain;

import io.patchpilot.backend.safety.convert.TriggerQuarantineConvert;

import java.util.Locale;

public record OperatorSafetyAuditQuery(
        int limit,
        String action,
        String resourceType,
        String resourceId,
        TriggerQuarantineScope scope,
        String scopeKey,
        String operator
) {

    public OperatorSafetyAuditQuery {
        action = normalizedAction(action);
        resourceType = normalizedResourceType(resourceType);
        resourceId = trimmed(resourceId);
        scopeKey = TriggerQuarantineConvert.normalizedScopeKey(scopeKey);
        operator = trimmed(operator);
    }

    public boolean hasAction() {
        return !action.isBlank();
    }

    public boolean hasResourceType() {
        return !resourceType.isBlank();
    }

    public boolean hasResourceId() {
        return !resourceId.isBlank();
    }

    public boolean hasScope() {
        return scope != null;
    }

    public boolean hasScopeKey() {
        return !scopeKey.isBlank();
    }

    public boolean hasOperator() {
        return !operator.isBlank();
    }

    private static String normalizedAction(String value) {
        return trimmed(value).toUpperCase(Locale.ROOT);
    }

    private static String normalizedResourceType(String value) {
        return trimmed(value).toUpperCase(Locale.ROOT);
    }

    private static String trimmed(String value) {
        return value == null ? "" : value.trim();
    }
}
