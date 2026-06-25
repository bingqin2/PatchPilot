package io.patchpilot.backend.github.credential;

import io.patchpilot.backend.github.config.GitHubProperties;
import io.patchpilot.backend.github.credential.domain.GitHubCredentialReadinessException;
import io.patchpilot.backend.github.credential.domain.GitHubCredentialReadinessVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

@Service
public class GitHubCredentialReadinessService {

    public static final String READY = "READY";
    public static final String NEEDS_ATTENTION = "NEEDS_ATTENTION";

    private static final String PERMISSION_ACTION =
            "Check PATCHPILOT_GITHUB_TOKEN and grant Contents: Read and write, Issues: Read and write, "
                    + "Pull requests: Read and write, and Metadata: Read-only; then restart the backend.";

    private final Supplier<GitHubCredentialReadinessVo> readinessSupplier;

    @Autowired
    public GitHubCredentialReadinessService(GitHubProperties gitHubProperties, GitHubCredentialProbe probe) {
        this(gitHubProperties, probe, Instant::now, System::currentTimeMillis);
    }

    GitHubCredentialReadinessService(
            GitHubProperties gitHubProperties,
            GitHubCredentialProbe probe,
            Supplier<Instant> clock,
            LongSupplier ticker
    ) {
        this(() -> buildReadiness(gitHubProperties, probe, clock, ticker));
    }

    public GitHubCredentialReadinessService(Supplier<GitHubCredentialReadinessVo> readinessSupplier) {
        this.readinessSupplier = readinessSupplier;
    }

    public GitHubCredentialReadinessVo getReadiness() {
        return readinessSupplier.get();
    }

    private static GitHubCredentialReadinessVo buildReadiness(
            GitHubProperties gitHubProperties,
            GitHubCredentialProbe probe,
            Supplier<Instant> clock,
            LongSupplier ticker
    ) {
        Instant checkedAt = clock.get();
        String token = token(gitHubProperties);
        if (!StringUtils.hasText(token)) {
            return attention(
                    false,
                    "GitHub token is not configured.",
                    "Configure PATCHPILOT_GITHUB_TOKEN and restart the backend.",
                    0,
                    checkedAt
            );
        }

        long startedAt = ticker.getAsLong();
        try {
            probe.check(token);
            long latencyMs = Math.max(0, ticker.getAsLong() - startedAt);
            return new GitHubCredentialReadinessVo(
                    true,
                    READY,
                    "GitHub API accepted the configured token.",
                    latencyMs,
                    checkedAt,
                    "No action needed."
            );
        } catch (GitHubCredentialReadinessException exception) {
            long latencyMs = Math.max(0, ticker.getAsLong() - startedAt);
            return attention(true, exception.getMessage(), PERMISSION_ACTION, latencyMs, checkedAt);
        }
    }

    private static GitHubCredentialReadinessVo attention(
            boolean tokenConfigured,
            String message,
            String operatorAction,
            long latencyMs,
            Instant checkedAt
    ) {
        return new GitHubCredentialReadinessVo(
                tokenConfigured,
                NEEDS_ATTENTION,
                message,
                latencyMs,
                checkedAt,
                operatorAction
        );
    }

    private static String token(GitHubProperties gitHubProperties) {
        return StringUtils.hasText(gitHubProperties.getToken()) ? gitHubProperties.getToken().trim() : "";
    }
}
