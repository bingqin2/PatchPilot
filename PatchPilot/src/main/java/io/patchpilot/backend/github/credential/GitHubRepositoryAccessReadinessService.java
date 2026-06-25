package io.patchpilot.backend.github.credential;

import io.patchpilot.backend.github.config.GitHubProperties;
import io.patchpilot.backend.github.credential.domain.GitHubCredentialReadinessException;
import io.patchpilot.backend.github.credential.domain.GitHubRepositoryAccessReadinessVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.function.BiFunction;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

@Service
public class GitHubRepositoryAccessReadinessService {

    public static final String READY = "READY";
    public static final String NEEDS_ATTENTION = "NEEDS_ATTENTION";

    private final BiFunction<String, String, GitHubRepositoryAccessReadinessVo> readinessSupplier;

    @Autowired
    public GitHubRepositoryAccessReadinessService(GitHubProperties gitHubProperties, GitHubRepositoryAccessProbe probe) {
        this(gitHubProperties, probe, Instant::now, System::currentTimeMillis);
    }

    GitHubRepositoryAccessReadinessService(
            GitHubProperties gitHubProperties,
            GitHubRepositoryAccessProbe probe,
            Supplier<Instant> clock,
            LongSupplier ticker
    ) {
        this((owner, repository) -> buildReadiness(gitHubProperties, probe, clock, ticker, owner, repository));
    }

    public GitHubRepositoryAccessReadinessService(BiFunction<String, String, GitHubRepositoryAccessReadinessVo> readinessSupplier) {
        this.readinessSupplier = readinessSupplier;
    }

    public GitHubRepositoryAccessReadinessVo getReadiness(String owner, String repository) {
        return readinessSupplier.apply(owner, repository);
    }

    private static GitHubRepositoryAccessReadinessVo buildReadiness(
            GitHubProperties gitHubProperties,
            GitHubRepositoryAccessProbe probe,
            Supplier<Instant> clock,
            LongSupplier ticker,
            String owner,
            String repository
    ) {
        Instant checkedAt = clock.get();
        String normalizedOwner = normalize(owner);
        String normalizedRepository = normalize(repository);
        String repositoryFullName = repositoryFullName(normalizedOwner, normalizedRepository);
        boolean repositoryConfigured = StringUtils.hasText(normalizedOwner) && StringUtils.hasText(normalizedRepository);
        if (!repositoryConfigured) {
            return attention(
                    tokenConfigured(gitHubProperties),
                    false,
                    "",
                    "Repository owner and name are required for the access probe.",
                    null,
                    0,
                    checkedAt,
                    "Select a repository or provide owner and repository query parameters."
            );
        }

        String token = token(gitHubProperties);
        if (!StringUtils.hasText(token)) {
            return attention(
                    false,
                    true,
                    repositoryFullName,
                    "GitHub token is not configured.",
                    null,
                    0,
                    checkedAt,
                    "Configure PATCHPILOT_GITHUB_TOKEN and restart the backend."
            );
        }

        long startedAt = ticker.getAsLong();
        try {
            String defaultBranch = probe.check(token, normalizedOwner, normalizedRepository);
            long latencyMs = Math.max(0, ticker.getAsLong() - startedAt);
            return new GitHubRepositoryAccessReadinessVo(
                    true,
                    true,
                    repositoryFullName,
                    READY,
                    "GitHub token can read repository " + repositoryFullName + ".",
                    defaultBranch,
                    latencyMs,
                    checkedAt,
                    "No action needed."
            );
        } catch (GitHubCredentialReadinessException exception) {
            long latencyMs = Math.max(0, ticker.getAsLong() - startedAt);
            return attention(
                    true,
                    true,
                    repositoryFullName,
                    exception.getMessage(),
                    null,
                    latencyMs,
                    checkedAt,
                    "Check PATCHPILOT_GITHUB_TOKEN permissions, GitHub App installation access, and repository allowlist for "
                            + repositoryFullName + "; then retry the readiness check."
            );
        }
    }

    private static GitHubRepositoryAccessReadinessVo attention(
            boolean tokenConfigured,
            boolean repositoryConfigured,
            String repository,
            String message,
            String defaultBranch,
            long latencyMs,
            Instant checkedAt,
            String operatorAction
    ) {
        return new GitHubRepositoryAccessReadinessVo(
                tokenConfigured,
                repositoryConfigured,
                repository,
                NEEDS_ATTENTION,
                message,
                defaultBranch,
                latencyMs,
                checkedAt,
                operatorAction
        );
    }

    private static String token(GitHubProperties gitHubProperties) {
        return StringUtils.hasText(gitHubProperties.getToken()) ? gitHubProperties.getToken().trim() : "";
    }

    private static boolean tokenConfigured(GitHubProperties gitHubProperties) {
        return StringUtils.hasText(token(gitHubProperties));
    }

    private static String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : "";
    }

    private static String repositoryFullName(String owner, String repository) {
        return StringUtils.hasText(owner) && StringUtils.hasText(repository) ? owner + "/" + repository : "";
    }
}
