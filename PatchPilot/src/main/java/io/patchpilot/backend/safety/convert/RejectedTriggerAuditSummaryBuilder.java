package io.patchpilot.backend.safety.convert;

import io.patchpilot.backend.safety.domain.RejectedTriggerAuditSummaryVo;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditVo;
import io.patchpilot.backend.safety.domain.RejectedTriggerCategory;
import io.patchpilot.backend.safety.domain.RejectedTriggerCountVo;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class RejectedTriggerAuditSummaryBuilder {

    private static final int TOP_ENTITY_LIMIT = 5;

    private RejectedTriggerAuditSummaryBuilder() {
    }

    public static RejectedTriggerAuditSummaryVo from(List<RejectedTriggerAuditVo> audits) {
        return new RejectedTriggerAuditSummaryVo(
                audits.size(),
                countBy(audits, audit -> valueOrFallback(audit.category(), RejectedTriggerCategory.UNKNOWN), Integer.MAX_VALUE),
                countBy(audits, audit -> valueOrFallback(audit.source(), "unknown"), Integer.MAX_VALUE),
                countBy(audits, audit -> valueOrFallback(audit.triggerUser(), "unknown user"), TOP_ENTITY_LIMIT),
                countBy(audits, RejectedTriggerAuditSummaryBuilder::repositoryLabel, TOP_ENTITY_LIMIT)
        );
    }

    private static List<RejectedTriggerCountVo> countBy(
            List<RejectedTriggerAuditVo> audits,
            Function<RejectedTriggerAuditVo, String> classifier,
            int limit
    ) {
        return audits.stream()
                .collect(Collectors.groupingBy(classifier, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Comparator
                        .<Map.Entry<String, Long>>comparingLong(Map.Entry::getValue)
                        .reversed()
                        .thenComparing(Map.Entry::getKey))
                .limit(limit)
                .map(entry -> new RejectedTriggerCountVo(entry.getKey(), entry.getValue()))
                .toList();
    }

    private static String repositoryLabel(RejectedTriggerAuditVo audit) {
        if (!StringUtils.hasText(audit.repositoryOwner()) || !StringUtils.hasText(audit.repositoryName())) {
            return "unknown repository";
        }
        return audit.repositoryOwner().trim() + "/" + audit.repositoryName().trim();
    }

    private static String valueOrFallback(String value, String fallback) {
        return StringUtils.hasText(value) ? value.trim() : fallback;
    }
}
