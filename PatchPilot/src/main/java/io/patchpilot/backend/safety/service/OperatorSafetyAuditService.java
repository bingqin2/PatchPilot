package io.patchpilot.backend.safety.service;

import io.patchpilot.backend.safety.domain.OperatorSafetyAuditVo;
import io.patchpilot.backend.safety.domain.RecordOperatorSafetyAuditCommand;

import java.util.List;
import java.util.Locale;

public interface OperatorSafetyAuditService {

    OperatorSafetyAuditVo recordSafetyAudit(RecordOperatorSafetyAuditCommand command);

    List<OperatorSafetyAuditVo> listSafetyAudits(int limit);

    default List<OperatorSafetyAuditVo> listSafetyAuditsForResource(String resourceType, String resourceId, int limit) {
        String normalizedResourceType = normalized(resourceType);
        String normalizedResourceId = normalized(resourceId);
        return listSafetyAudits(100).stream()
                .filter(audit -> normalized(audit.resourceType()).equals(normalizedResourceType))
                .filter(audit -> normalized(audit.resourceId()).equals(normalizedResourceId))
                .limit(limit)
                .toList();
    }

    private static String normalized(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
