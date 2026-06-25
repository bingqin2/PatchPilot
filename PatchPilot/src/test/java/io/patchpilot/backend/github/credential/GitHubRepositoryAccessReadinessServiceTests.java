package io.patchpilot.backend.github.credential;

import io.patchpilot.backend.github.config.GitHubProperties;
import io.patchpilot.backend.github.credential.domain.GitHubCredentialReadinessException;
import io.patchpilot.backend.github.credential.domain.GitHubRepositoryAccessReadinessVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class GitHubRepositoryAccessReadinessServiceTests {

    @Test
    void should_report_ready_when_token_can_access_repository() {
        GitHubRepositoryAccessReadinessService service = new GitHubRepositoryAccessReadinessService(
                properties(" github-token "),
                (token, owner, repository) -> {
                    assertThat(token).isEqualTo("github-token");
                    assertThat(owner).isEqualTo("bingqin2");
                    assertThat(repository).isEqualTo("PatchPilot");
                    return "main";
                },
                () -> Instant.parse("2026-06-25T04:00:00Z"),
                new FixedTicker(100, 148)
        );

        GitHubRepositoryAccessReadinessVo readiness = service.getReadiness(" bingqin2 ", " PatchPilot ");

        assertThat(readiness.tokenConfigured()).isTrue();
        assertThat(readiness.repositoryConfigured()).isTrue();
        assertThat(readiness.repository()).isEqualTo("bingqin2/PatchPilot");
        assertThat(readiness.status()).isEqualTo("READY");
        assertThat(readiness.message()).isEqualTo("GitHub token can read repository bingqin2/PatchPilot.");
        assertThat(readiness.defaultBranch()).isEqualTo("main");
        assertThat(readiness.latencyMs()).isEqualTo(48);
        assertThat(readiness.checkedAt()).isEqualTo(Instant.parse("2026-06-25T04:00:00Z"));
        assertThat(readiness.operatorAction()).isEqualTo("No action needed.");
    }

    @Test
    void should_report_attention_without_probe_when_repository_is_missing() {
        AtomicBoolean called = new AtomicBoolean(false);
        GitHubRepositoryAccessReadinessService service = new GitHubRepositoryAccessReadinessService(
                properties("github-token"),
                (token, owner, repository) -> {
                    called.set(true);
                    return "main";
                },
                () -> Instant.parse("2026-06-25T04:00:00Z"),
                new FixedTicker(100, 148)
        );

        GitHubRepositoryAccessReadinessVo readiness = service.getReadiness("bingqin2", " ");

        assertThat(called).isFalse();
        assertThat(readiness.tokenConfigured()).isTrue();
        assertThat(readiness.repositoryConfigured()).isFalse();
        assertThat(readiness.repository()).isEqualTo("");
        assertThat(readiness.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(readiness.message()).isEqualTo("Repository owner and name are required for the access probe.");
        assertThat(readiness.defaultBranch()).isNull();
        assertThat(readiness.latencyMs()).isZero();
        assertThat(readiness.operatorAction()).isEqualTo("Select a repository or provide owner and repository query parameters.");
    }

    @Test
    void should_report_attention_without_probe_when_token_is_missing() {
        AtomicBoolean called = new AtomicBoolean(false);
        GitHubRepositoryAccessReadinessService service = new GitHubRepositoryAccessReadinessService(
                properties(" "),
                (token, owner, repository) -> {
                    called.set(true);
                    return "main";
                },
                () -> Instant.parse("2026-06-25T04:00:00Z"),
                new FixedTicker(100, 148)
        );

        GitHubRepositoryAccessReadinessVo readiness = service.getReadiness("bingqin2", "PatchPilot");

        assertThat(called).isFalse();
        assertThat(readiness.tokenConfigured()).isFalse();
        assertThat(readiness.repositoryConfigured()).isTrue();
        assertThat(readiness.repository()).isEqualTo("bingqin2/PatchPilot");
        assertThat(readiness.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(readiness.message()).isEqualTo("GitHub token is not configured.");
        assertThat(readiness.operatorAction()).isEqualTo("Configure PATCHPILOT_GITHUB_TOKEN and restart the backend.");
    }

    @Test
    void should_report_attention_when_repository_access_probe_fails() {
        GitHubRepositoryAccessReadinessService service = new GitHubRepositoryAccessReadinessService(
                properties("github-token"),
                (token, owner, repository) -> {
                    throw new GitHubCredentialReadinessException("GitHub repository access probe failed: HTTP 404");
                },
                () -> Instant.parse("2026-06-25T04:00:00Z"),
                new FixedTicker(100, 132)
        );

        GitHubRepositoryAccessReadinessVo readiness = service.getReadiness("bingqin2", "PatchPilot");

        assertThat(readiness.tokenConfigured()).isTrue();
        assertThat(readiness.repositoryConfigured()).isTrue();
        assertThat(readiness.repository()).isEqualTo("bingqin2/PatchPilot");
        assertThat(readiness.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(readiness.message()).isEqualTo("GitHub repository access probe failed: HTTP 404");
        assertThat(readiness.defaultBranch()).isNull();
        assertThat(readiness.latencyMs()).isEqualTo(32);
        assertThat(readiness.operatorAction()).contains("PATCHPILOT_GITHUB_TOKEN");
        assertThat(readiness.operatorAction()).contains("bingqin2/PatchPilot");
        assertThat(readiness.operatorAction()).contains("repository allowlist");
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
