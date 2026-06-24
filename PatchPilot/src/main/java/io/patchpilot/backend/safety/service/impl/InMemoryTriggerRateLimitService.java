package io.patchpilot.backend.safety.service.impl;

import io.patchpilot.backend.safety.config.SafetyProperties;
import io.patchpilot.backend.safety.domain.TriggerRateLimitDecision;
import io.patchpilot.backend.safety.domain.TriggerRateLimitRequest;
import io.patchpilot.backend.safety.service.TriggerRateLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.function.Supplier;

@Service
public class InMemoryTriggerRateLimitService implements TriggerRateLimitService {

    private static final String ISSUE_LIMIT_REASON = "Unsafe request rejected: trigger rate limit exceeded for issue";
    private static final String TRIGGER_USER_LIMIT_REASON = "Unsafe request rejected: trigger rate limit exceeded for trigger user";
    private static final String REPOSITORY_LIMIT_REASON = "Unsafe request rejected: trigger rate limit exceeded for repository";

    private final SafetyProperties safetyProperties;
    private final Supplier<Instant> clock;
    private final Map<String, Queue<Instant>> bucketHits = new HashMap<>();

    @Autowired
    public InMemoryTriggerRateLimitService(SafetyProperties safetyProperties) {
        this(safetyProperties, Instant::now);
    }

    public InMemoryTriggerRateLimitService(SafetyProperties safetyProperties, Supplier<Instant> clock) {
        this.safetyProperties = safetyProperties;
        this.clock = clock;
    }

    @Override
    public synchronized TriggerRateLimitDecision checkAndRecord(TriggerRateLimitRequest request) {
        if (!safetyProperties.isTriggerRateLimitEnabled()) {
            return TriggerRateLimitDecision.accepted();
        }

        Instant now = clock.get();
        TriggerRateLimitDecision decision = evaluate(request, now);
        if (!decision.allowed()) {
            return decision;
        }

        record(issueKey(request), now);
        record(triggerUserKey(request), now);
        record(repositoryKey(request), now);
        return TriggerRateLimitDecision.accepted();
    }

    @Override
    public synchronized TriggerRateLimitDecision check(TriggerRateLimitRequest request) {
        if (!safetyProperties.isTriggerRateLimitEnabled()) {
            return TriggerRateLimitDecision.accepted();
        }
        return evaluate(request, clock.get());
    }

    private TriggerRateLimitDecision evaluate(TriggerRateLimitRequest request, Instant now) {
        if (limitExceeded(issueKey(request), safetyProperties.getTriggerRateLimitMaxPerIssue(), now)) {
            return TriggerRateLimitDecision.rejected(ISSUE_LIMIT_REASON);
        }
        if (limitExceeded(triggerUserKey(request), safetyProperties.getTriggerRateLimitMaxPerTriggerUser(), now)) {
            return TriggerRateLimitDecision.rejected(TRIGGER_USER_LIMIT_REASON);
        }
        if (limitExceeded(repositoryKey(request), safetyProperties.getTriggerRateLimitMaxPerRepository(), now)) {
            return TriggerRateLimitDecision.rejected(REPOSITORY_LIMIT_REASON);
        }
        return TriggerRateLimitDecision.accepted();
    }

    private boolean limitExceeded(String key, int maxRequests, Instant now) {
        if (maxRequests <= 0) {
            return true;
        }
        Queue<Instant> hits = bucketHits.computeIfAbsent(key, ignored -> new ArrayDeque<>());
        pruneExpired(hits, now);
        return hits.size() >= maxRequests;
    }

    private void record(String key, Instant now) {
        Queue<Instant> hits = bucketHits.computeIfAbsent(key, ignored -> new ArrayDeque<>());
        pruneExpired(hits, now);
        hits.add(now);
    }

    private void pruneExpired(Queue<Instant> hits, Instant now) {
        Instant oldestAllowed = now.minusMillis(Math.max(1, safetyProperties.getTriggerRateLimitWindowMs()));
        while (!hits.isEmpty() && hits.peek().isBefore(oldestAllowed)) {
            hits.poll();
        }
    }

    private static String issueKey(TriggerRateLimitRequest request) {
        return repositoryKey(request) + "#issue:" + request.issueNumber();
    }

    private static String repositoryKey(TriggerRateLimitRequest request) {
        return "repository:" + normalized(request.repositoryOwner()) + "/" + normalized(request.repositoryName());
    }

    private static String triggerUserKey(TriggerRateLimitRequest request) {
        return "trigger-user:" + normalized(request.triggerUser());
    }

    private static String normalized(String value) {
        if (!StringUtils.hasText(value)) {
            return "-";
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
