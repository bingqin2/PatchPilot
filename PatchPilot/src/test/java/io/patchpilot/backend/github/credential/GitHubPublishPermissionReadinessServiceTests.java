package io.patchpilot.backend.github.credential;

import io.patchpilot.backend.github.config.GitHubProperties;
import io.patchpilot.backend.github.credential.domain.GitHubCredentialReadinessException;
import io.patchpilot.backend.github.credential.domain.GitHubPublishPermissionReadinessVo;
import io.patchpilot.backend.github.credential.domain.GitHubRepositoryPermissionProbeResult;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class GitHubPublishPermissionReadinessServiceTests {

    @Test
    void should_report_ready_when_repository_permissions_allow_publish_path() {
        GitHubPublishPermissionReadinessService service = new GitHubPublishPermissionReadinessService(
                properties(" github-token "),
                (token, owner, repository) -> {
                    assertThat(token).isEqualTo("github-token");
                    assertThat(owner).isEqualTo("bingqin2");
                    assertThat(repository).isEqualTo("PatchPilot");
                    return new GitHubRepositoryPermissionProbeResult("main", true, true, false, false);
                },
                () -> Instant.parse("2026-06-30T06:00:00Z"),
                new FixedTicker(100, 157)
        );

        GitHubPublishPermissionReadinessVo readiness = service.getReadiness(" bingqin2 ", " PatchPilot ");

        assertThat(readiness.status()).isEqualTo("READY");
        assertThat(readiness.publishPermissionReady()).isTrue();
        assertThat(readiness.tokenConfigured()).isTrue();
        assertThat(readiness.repositoryConfigured()).isTrue();
        assertThat(readiness.repository()).isEqualTo("bingqin2/PatchPilot");
        assertThat(readiness.defaultBranch()).isEqualTo("main");
        assertThat(readiness.canReadRepository()).isTrue();
        assertThat(readiness.canPushBranches()).isTrue();
        assertThat(readiness.canCreatePullRequests()).isTrue();
        assertThat(readiness.issueFeedbackPermissionLikely()).isTrue();
        assertThat(readiness.summary()).isEqualTo("GitHub token has repository publish permissions for PatchPilot push and Pull Request creation.");
        assertThat(readiness.nextAction()).isEqualTo("Continue with the live /agent fix demo.");
        assertThat(readiness.latencyMs()).isEqualTo(57);
        assertThat(readiness.checkedAt()).isEqualTo(Instant.parse("2026-06-30T06:00:00Z"));
        assertThat(readiness.sideEffectContract()).contains("does not run git push");
        assertThat(readiness.permissionChecks()).extracting("name")
                .containsExactly("Repository read", "Branch push", "Pull Request creation", "Issue feedback");
        assertThat(readiness.evidenceNotes()).doesNotContain("github-token");
    }

    @Test
    void should_report_attention_when_token_can_only_read_repository() {
        GitHubPublishPermissionReadinessService service = new GitHubPublishPermissionReadinessService(
                properties("github-token"),
                (token, owner, repository) -> new GitHubRepositoryPermissionProbeResult("main", true, false, false, false),
                () -> Instant.parse("2026-06-30T06:00:00Z"),
                new FixedTicker(100, 140)
        );

        GitHubPublishPermissionReadinessVo readiness = service.getReadiness("bingqin2", "PatchPilot");

        assertThat(readiness.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(readiness.publishPermissionReady()).isFalse();
        assertThat(readiness.canReadRepository()).isTrue();
        assertThat(readiness.canPushBranches()).isFalse();
        assertThat(readiness.canCreatePullRequests()).isFalse();
        assertThat(readiness.summary()).isEqualTo("GitHub token can read the repository but does not expose write permissions required for publish.");
        assertThat(readiness.nextAction()).contains("Contents: Read and write");
        assertThat(readiness.nextAction()).contains("Pull requests: Read and write");
        assertThat(readiness.permissionChecks()).extracting("status")
                .containsExactly("READY", "NEEDS_ATTENTION", "NEEDS_ATTENTION", "NEEDS_ATTENTION");
    }

    @Test
    void should_report_attention_without_probe_when_repository_is_missing() {
        AtomicBoolean called = new AtomicBoolean(false);
        GitHubPublishPermissionReadinessService service = new GitHubPublishPermissionReadinessService(
                properties("github-token"),
                (token, owner, repository) -> {
                    called.set(true);
                    return new GitHubRepositoryPermissionProbeResult("main", true, true, false, false);
                },
                () -> Instant.parse("2026-06-30T06:00:00Z"),
                new FixedTicker(100, 140)
        );

        GitHubPublishPermissionReadinessVo readiness = service.getReadiness("bingqin2", " ");

        assertThat(called).isFalse();
        assertThat(readiness.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(readiness.repositoryConfigured()).isFalse();
        assertThat(readiness.repository()).isEqualTo("");
        assertThat(readiness.nextAction()).isEqualTo("Select a repository or provide owner and repository query parameters.");
    }

    @Test
    void should_report_blocked_without_probe_when_token_is_missing() {
        AtomicBoolean called = new AtomicBoolean(false);
        GitHubPublishPermissionReadinessService service = new GitHubPublishPermissionReadinessService(
                properties(" "),
                (token, owner, repository) -> {
                    called.set(true);
                    return new GitHubRepositoryPermissionProbeResult("main", true, true, false, false);
                },
                () -> Instant.parse("2026-06-30T06:00:00Z"),
                new FixedTicker(100, 140)
        );

        GitHubPublishPermissionReadinessVo readiness = service.getReadiness("bingqin2", "PatchPilot");

        assertThat(called).isFalse();
        assertThat(readiness.status()).isEqualTo("BLOCKED");
        assertThat(readiness.tokenConfigured()).isFalse();
        assertThat(readiness.repositoryConfigured()).isTrue();
        assertThat(readiness.nextAction()).isEqualTo("Configure PATCHPILOT_GITHUB_TOKEN and restart the backend.");
    }

    @Test
    void should_report_attention_when_permission_probe_fails() {
        GitHubPublishPermissionReadinessService service = new GitHubPublishPermissionReadinessService(
                properties("github-token"),
                (token, owner, repository) -> {
                    throw new GitHubCredentialReadinessException("GitHub repository permission probe failed: HTTP 403");
                },
                () -> Instant.parse("2026-06-30T06:00:00Z"),
                new FixedTicker(100, 123)
        );

        GitHubPublishPermissionReadinessVo readiness = service.getReadiness("bingqin2", "PatchPilot");

        assertThat(readiness.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(readiness.summary()).isEqualTo("GitHub repository permission probe could not confirm publish permissions.");
        assertThat(readiness.nextAction()).contains("PATCHPILOT_GITHUB_TOKEN");
        assertThat(readiness.evidenceNotes()).contains("Probe message: GitHub repository permission probe failed: HTTP 403");
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
