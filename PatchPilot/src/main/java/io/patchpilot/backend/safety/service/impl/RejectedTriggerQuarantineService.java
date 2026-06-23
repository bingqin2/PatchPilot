package io.patchpilot.backend.safety.service.impl;

import io.patchpilot.backend.safety.config.SafetyProperties;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditVo;
import io.patchpilot.backend.safety.domain.TriggerQuarantineDecision;
import io.patchpilot.backend.safety.domain.TriggerQuarantineRequest;
import io.patchpilot.backend.safety.service.RejectedTriggerAuditService;
import io.patchpilot.backend.safety.service.TriggerQuarantineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Locale;
import java.util.function.Supplier;

@Service
public class RejectedTriggerQuarantineService implements TriggerQuarantineService {

    private static final String TRIGGER_USER_REASON =
            "Unsafe request rejected: trigger user is temporarily quarantined";
    private static final String REPOSITORY_REASON =
            "Unsafe request rejected: repository is temporarily quarantined";
    private static final int RECENT_AUDIT_LIMIT = 500;

    private final SafetyProperties safetyProperties;
    private final RejectedTriggerAuditService auditService;
    private final Supplier<Instant> clock;

    @Autowired
    public RejectedTriggerQuarantineService(
            SafetyProperties safetyProperties,
            RejectedTriggerAuditService auditService
    ) {
        this(safetyProperties, auditService, Instant::now);
    }

    public RejectedTriggerQuarantineService(
            SafetyProperties safetyProperties,
            RejectedTriggerAuditService auditService,
            Supplier<Instant> clock
    ) {
        this.safetyProperties = safetyProperties;
        this.auditService = auditService;
        this.clock = clock;
    }

    @Override
    public TriggerQuarantineDecision check(TriggerQuarantineRequest request) {
        if (!safetyProperties.isRejectedTriggerQuarantineEnabled()) {
            return TriggerQuarantineDecision.accepted();
        }
        if (safetyProperties.getRejectedTriggerQuarantineThreshold() < 1) {
            return TriggerQuarantineDecision.accepted();
        }

        Instant now = clock.get();
        long windowMs = Math.max(1, safetyProperties.getRejectedTriggerQuarantineWindowMs());
        long cooldownMs = Math.max(1, safetyProperties.getRejectedTriggerQuarantineCooldownMs());
        String triggerUserKey = normalized(request.triggerUser());
        String repositoryKey = repositoryKey(request.repositoryOwner(), request.repositoryName());
        var audits = auditService.listRejectedTriggers(RECENT_AUDIT_LIMIT);

        if (StringUtils.hasText(triggerUserKey)
                && isQuarantined(
                audits,
                now,
                windowMs,
                cooldownMs,
                audit -> triggerUserKey.equals(normalized(audit.triggerUser()))
        )) {
            return TriggerQuarantineDecision.rejected(TRIGGER_USER_REASON);
        }
        if (StringUtils.hasText(repositoryKey)
                && isQuarantined(
                audits,
                now,
                windowMs,
                cooldownMs,
                audit -> repositoryKey.equals(repositoryKey(audit.repositoryOwner(), audit.repositoryName()))
        )) {
            return TriggerQuarantineDecision.rejected(REPOSITORY_REASON);
        }
        return TriggerQuarantineDecision.accepted();
    }

    private boolean isQuarantined(
            Iterable<RejectedTriggerAuditVo> audits,
            Instant now,
            long windowMs,
            long cooldownMs,
            java.util.function.Predicate<RejectedTriggerAuditVo> matcher
    ) {
        Instant latestRejection = null;
        for (RejectedTriggerAuditVo audit : audits) {
            if (audit.createdAt() == null || !matcher.test(audit)) {
                continue;
            }
            if (latestRejection == null || audit.createdAt().isAfter(latestRejection)) {
                latestRejection = audit.createdAt();
            }
        }
        if (latestRejection == null || latestRejection.plusMillis(cooldownMs).isBefore(now)) {
            return false;
        }

        Instant thresholdWindowStart = latestRejection.minusMillis(windowMs);
        int rejectionCount = 0;
        for (RejectedTriggerAuditVo audit : audits) {
            if (audit.createdAt() == null || !matcher.test(audit)) {
                continue;
            }
            if (!audit.createdAt().isBefore(thresholdWindowStart) && !audit.createdAt().isAfter(latestRejection)) {
                rejectionCount++;
            }
        }
        return rejectionCount >= safetyProperties.getRejectedTriggerQuarantineThreshold();
    }

    private static String repositoryKey(String repositoryOwner, String repositoryName) {
        String owner = normalized(repositoryOwner);
        String name = normalized(repositoryName);
        if (!StringUtils.hasText(owner) || !StringUtils.hasText(name)) {
            return "";
        }
        return owner + "/" + name;
    }

    private static String normalized(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
