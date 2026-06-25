package io.patchpilot.backend.github.credential;

import io.patchpilot.backend.github.config.GitHubProperties;
import io.patchpilot.backend.github.credential.domain.GitHubCredentialReadinessException;
import io.patchpilot.backend.github.credential.domain.GitHubCredentialReadinessVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class GitHubCredentialReadinessServiceTests {

    @Test
    void should_report_ready_when_github_accepts_configured_token() {
        GitHubCredentialReadinessService service = new GitHubCredentialReadinessService(
                properties(" github-token "),
                token -> assertThat(token).isEqualTo("github-token"),
                () -> Instant.parse("2026-06-25T03:00:00Z"),
                new FixedTicker(100, 175)
        );

        GitHubCredentialReadinessVo readiness = service.getReadiness();

        assertThat(readiness.tokenConfigured()).isTrue();
        assertThat(readiness.status()).isEqualTo("READY");
        assertThat(readiness.message()).isEqualTo("GitHub API accepted the configured token.");
        assertThat(readiness.latencyMs()).isEqualTo(75);
        assertThat(readiness.checkedAt()).isEqualTo(Instant.parse("2026-06-25T03:00:00Z"));
        assertThat(readiness.operatorAction()).isEqualTo("No action needed.");
    }

    @Test
    void should_report_attention_without_probe_when_token_is_missing() {
        AtomicBoolean called = new AtomicBoolean(false);
        GitHubCredentialReadinessService service = new GitHubCredentialReadinessService(
                properties(" "),
                token -> called.set(true),
                () -> Instant.parse("2026-06-25T03:00:00Z"),
                new FixedTicker(100, 175)
        );

        GitHubCredentialReadinessVo readiness = service.getReadiness();

        assertThat(called).isFalse();
        assertThat(readiness.tokenConfigured()).isFalse();
        assertThat(readiness.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(readiness.message()).isEqualTo("GitHub token is not configured.");
        assertThat(readiness.latencyMs()).isZero();
        assertThat(readiness.operatorAction()).isEqualTo("Configure PATCHPILOT_GITHUB_TOKEN and restart the backend.");
    }

    @Test
    void should_report_attention_when_github_rejects_token() {
        GitHubCredentialReadinessService service = new GitHubCredentialReadinessService(
                properties("github-token"),
                token -> {
                    throw new GitHubCredentialReadinessException("GitHub credential probe failed: HTTP 401");
                },
                () -> Instant.parse("2026-06-25T03:00:00Z"),
                new FixedTicker(100, 140)
        );

        GitHubCredentialReadinessVo readiness = service.getReadiness();

        assertThat(readiness.tokenConfigured()).isTrue();
        assertThat(readiness.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(readiness.message()).isEqualTo("GitHub credential probe failed: HTTP 401");
        assertThat(readiness.latencyMs()).isEqualTo(40);
        assertThat(readiness.operatorAction()).contains("PATCHPILOT_GITHUB_TOKEN");
        assertThat(readiness.operatorAction()).contains("Contents: Read and write");
        assertThat(readiness.operatorAction()).contains("Pull requests: Read and write");
    }

    private static GitHubProperties properties(String token) {
        GitHubProperties properties = new GitHubProperties();
        properties.setToken(token);
        return properties;
    }

    private static final class FixedTicker implements java.util.function.LongSupplier {

        private final long[] values;
        private int index;

        private FixedTicker(long... values) {
            this.values = values;
        }

        @Override
        public long getAsLong() {
            return values[Math.min(index++, values.length - 1)];
        }
    }
}
