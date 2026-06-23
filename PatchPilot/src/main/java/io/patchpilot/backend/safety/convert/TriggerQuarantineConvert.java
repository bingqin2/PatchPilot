package io.patchpilot.backend.safety.convert;

import io.patchpilot.backend.safety.domain.RecordTriggerQuarantineCommand;
import io.patchpilot.backend.safety.domain.TriggerQuarantineEntity;
import io.patchpilot.backend.safety.domain.TriggerQuarantineScope;
import io.patchpilot.backend.safety.domain.TriggerQuarantineVo;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Locale;

public final class TriggerQuarantineConvert {

    private TriggerQuarantineConvert() {
    }

    public static TriggerQuarantineEntity newEntity(
            String id,
            RecordTriggerQuarantineCommand command,
            Instant now
    ) {
        TriggerQuarantineEntity entity = new TriggerQuarantineEntity();
        entity.setId(id);
        entity.setScope(command.scope().name());
        entity.setScopeKey(normalizedScopeKey(command.scopeKey()));
        entity.setReason(command.reason());
        entity.setCategory(command.category());
        entity.setEvidenceCount(command.evidenceCount());
        entity.setWindowMs(command.windowMs());
        entity.setStartedAt(now);
        entity.setExpiresAt(command.expiresAt());
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setCreatedBy(null);
        entity.setReleasedAt(null);
        entity.setReleasedBy(null);
        entity.setReleaseReason(null);
        return entity;
    }

    public static TriggerQuarantineVo toVo(TriggerQuarantineEntity entity, Instant now) {
        Instant expiresAt = entity.getExpiresAt();
        Instant releasedAt = entity.getReleasedAt();
        return new TriggerQuarantineVo(
                entity.getId(),
                TriggerQuarantineScope.valueOf(entity.getScope()),
                entity.getScopeKey(),
                entity.getReason(),
                entity.getCategory(),
                entity.getEvidenceCount() == null ? 0 : entity.getEvidenceCount(),
                entity.getWindowMs() == null ? 0 : entity.getWindowMs(),
                entity.getStartedAt(),
                expiresAt,
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getCreatedBy(),
                releasedAt,
                entity.getReleasedBy(),
                entity.getReleaseReason(),
                releasedAt == null && expiresAt != null && expiresAt.isAfter(now)
        );
    }

    public static String normalizedScopeKey(String scopeKey) {
        if (!StringUtils.hasText(scopeKey)) {
            return "";
        }
        return scopeKey.trim().toLowerCase(Locale.ROOT);
    }
}
