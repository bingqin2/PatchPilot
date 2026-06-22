package io.patchpilot.backend.safety.service;

import io.patchpilot.backend.safety.config.SafetyProperties;
import io.patchpilot.backend.safety.domain.TriggerRateLimitDecision;
import io.patchpilot.backend.safety.domain.TriggerRateLimitRequest;
import io.patchpilot.backend.safety.service.impl.InMemoryTriggerRateLimitService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryTriggerRateLimitServiceTests {

    @Test
    void should_reject_when_issue_trigger_limit_is_exceeded_within_window() {
        AtomicReference<Instant> now = new AtomicReference<>(Instant.parse("2026-06-22T00:00:00Z"));
        InMemoryTriggerRateLimitService rateLimitService = new InMemoryTriggerRateLimitService(
                properties(true, 60_000, 100, 100, 2),
                now::get
        );

        assertThat(rateLimitService.checkAndRecord(request()).allowed()).isTrue();
        assertThat(rateLimitService.checkAndRecord(request()).allowed()).isTrue();
        TriggerRateLimitDecision thirdDecision = rateLimitService.checkAndRecord(request());

        assertThat(thirdDecision.allowed()).isFalse();
        assertThat(thirdDecision.reason()).isEqualTo("Unsafe request rejected: trigger rate limit exceeded for issue");

        now.set(Instant.parse("2026-06-22T00:01:01Z"));
        assertThat(rateLimitService.checkAndRecord(request()).allowed()).isTrue();
    }

    @Test
    void should_reject_when_trigger_user_limit_is_exceeded_within_window() {
        InMemoryTriggerRateLimitService rateLimitService = new InMemoryTriggerRateLimitService(
                properties(true, 60_000, 1, 100, 100),
                () -> Instant.parse("2026-06-22T00:00:00Z")
        );

        assertThat(rateLimitService.checkAndRecord(request("octocat", "repo-one", 1)).allowed()).isTrue();
        TriggerRateLimitDecision secondDecision = rateLimitService.checkAndRecord(request("octocat", "repo-two", 2));

        assertThat(secondDecision.allowed()).isFalse();
        assertThat(secondDecision.reason()).isEqualTo("Unsafe request rejected: trigger rate limit exceeded for trigger user");
    }

    @Test
    void should_allow_all_requests_when_rate_limit_is_disabled() {
        InMemoryTriggerRateLimitService rateLimitService = new InMemoryTriggerRateLimitService(
                properties(false, 60_000, 1, 1, 1),
                () -> Instant.parse("2026-06-22T00:00:00Z")
        );

        assertThat(rateLimitService.checkAndRecord(request()).allowed()).isTrue();
        assertThat(rateLimitService.checkAndRecord(request()).allowed()).isTrue();
        assertThat(rateLimitService.checkAndRecord(request()).allowed()).isTrue();
    }

    private static TriggerRateLimitRequest request() {
        return request("octocat", "hello-world", 42);
    }

    private static TriggerRateLimitRequest request(String owner, String repositoryName, long issueNumber) {
        return new TriggerRateLimitRequest(
                "issue_comment",
                owner,
                repositoryName,
                issueNumber,
                "alice"
        );
    }

    private static SafetyProperties properties(
            boolean enabled,
            long windowMs,
            int maxPerTriggerUser,
            int maxPerRepository,
            int maxPerIssue
    ) {
        SafetyProperties properties = new SafetyProperties();
        properties.setTriggerRateLimitEnabled(enabled);
        properties.setTriggerRateLimitWindowMs(windowMs);
        properties.setTriggerRateLimitMaxPerTriggerUser(maxPerTriggerUser);
        properties.setTriggerRateLimitMaxPerRepository(maxPerRepository);
        properties.setTriggerRateLimitMaxPerIssue(maxPerIssue);
        return properties;
    }
}
