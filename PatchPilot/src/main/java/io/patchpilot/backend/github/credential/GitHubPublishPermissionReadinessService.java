package io.patchpilot.backend.github.credential;

import io.patchpilot.backend.github.config.GitHubProperties;
import io.patchpilot.backend.github.credential.domain.GitHubCredentialReadinessException;
import io.patchpilot.backend.github.credential.domain.GitHubPublishPermissionReadinessCheckVo;
import io.patchpilot.backend.github.credential.domain.GitHubPublishPermissionReadinessVo;
import io.patchpilot.backend.github.credential.domain.GitHubRepositoryPermissionProbeResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

@Service
public class GitHubPublishPermissionReadinessService {

    public static final String READY = "READY";
    public static final String NEEDS_ATTENTION = "NEEDS_ATTENTION";
    public static final String BLOCKED = "BLOCKED";

    private static final String SIDE_EFFECT_CONTRACT = "Read-only permission probe: this endpoint does not run git push, "
            + "does not create branches, does not create Pull Requests, does not write issue comments, and does not expose tokens.";

    private final BiFunction<String, String, GitHubPublishPermissionReadinessVo> readinessSupplier;

    @Autowired
    public GitHubPublishPermissionReadinessService(
            GitHubProperties gitHubProperties,
            GitHubRepositoryPermissionProbe probe
    ) {
        this(gitHubProperties, probe, Instant::now, System::currentTimeMillis);
    }

    GitHubPublishPermissionReadinessService(
            GitHubProperties gitHubProperties,
            GitHubRepositoryPermissionProbe probe,
            Supplier<Instant> clock,
            LongSupplier ticker
    ) {
        this((owner, repository) -> buildReadiness(gitHubProperties, probe, clock, ticker, owner, repository));
    }

    public GitHubPublishPermissionReadinessService(BiFunction<String, String, GitHubPublishPermissionReadinessVo> readinessSupplier) {
        this.readinessSupplier = readinessSupplier;
    }

    public GitHubPublishPermissionReadinessVo getReadiness(String owner, String repository) {
        return readinessSupplier.apply(owner, repository);
    }

    private static GitHubPublishPermissionReadinessVo buildReadiness(
            GitHubProperties gitHubProperties,
            GitHubRepositoryPermissionProbe probe,
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
            return skipped(
                    tokenConfigured(gitHubProperties),
                    false,
                    "",
                    NEEDS_ATTENTION,
                    "Repository owner and name are required for the publish permission probe.",
                    "Select a repository or provide owner and repository query parameters.",
                    checkedAt
            );
        }

        String token = token(gitHubProperties);
        if (!StringUtils.hasText(token)) {
            return skipped(
                    false,
                    true,
                    repositoryFullName,
                    BLOCKED,
                    "GitHub token is not configured.",
                    "Configure PATCHPILOT_GITHUB_TOKEN and restart the backend.",
                    checkedAt
            );
        }

        long startedAt = ticker.getAsLong();
        try {
            GitHubRepositoryPermissionProbeResult result = probe.check(token, normalizedOwner, normalizedRepository);
            long latencyMs = Math.max(0, ticker.getAsLong() - startedAt);
            boolean writeCapable = result.push() || result.admin() || result.maintain();
            List<GitHubPublishPermissionReadinessCheckVo> checks = permissionChecks(result.pull(), writeCapable);
            String status = writeCapable && result.pull() ? READY : NEEDS_ATTENTION;
            return new GitHubPublishPermissionReadinessVo(
                    status,
                    READY.equals(status),
                    true,
                    true,
                    repositoryFullName,
                    result.defaultBranch(),
                    result.pull(),
                    writeCapable,
                    writeCapable,
                    writeCapable,
                    summary(status, result.pull()),
                    nextAction(status),
                    SIDE_EFFECT_CONTRACT,
                    checks,
                    evidenceNotes(repositoryFullName, result, writeCapable),
                    latencyMs,
                    checkedAt
            );
        } catch (GitHubCredentialReadinessException exception) {
            long latencyMs = Math.max(0, ticker.getAsLong() - startedAt);
            return new GitHubPublishPermissionReadinessVo(
                    NEEDS_ATTENTION,
                    false,
                    true,
                    true,
                    repositoryFullName,
                    null,
                    false,
                    false,
                    false,
                    false,
                    "GitHub repository permission probe could not confirm publish permissions.",
                    "Check PATCHPILOT_GITHUB_TOKEN permissions, GitHub App installation access, and repository allowlist for "
                            + repositoryFullName + "; then retry the permission readiness check.",
                    SIDE_EFFECT_CONTRACT,
                    permissionChecks(false, false),
                    List.of("Repository: " + repositoryFullName, "Probe message: " + exception.getMessage()),
                    latencyMs,
                    checkedAt
            );
        }
    }

    private static GitHubPublishPermissionReadinessVo skipped(
            boolean tokenConfigured,
            boolean repositoryConfigured,
            String repository,
            String status,
            String summary,
            String nextAction,
            Instant checkedAt
    ) {
        return new GitHubPublishPermissionReadinessVo(
                status,
                false,
                tokenConfigured,
                repositoryConfigured,
                repository,
                null,
                false,
                false,
                false,
                false,
                summary,
                nextAction,
                SIDE_EFFECT_CONTRACT,
                permissionChecks(false, false),
                List.of("Token configured: " + tokenConfigured, "Repository configured: " + repositoryConfigured),
                0,
                checkedAt
        );
    }

    private static List<GitHubPublishPermissionReadinessCheckVo> permissionChecks(boolean canRead, boolean writeCapable) {
        return List.of(
                check(
                        "Repository read",
                        canRead,
                        canRead ? "Token can read repository metadata." : "Token read permission was not confirmed.",
                        canRead ? "No action needed." : "Grant repository Metadata read access and Contents read access."
                ),
                check(
                        "Branch push",
                        writeCapable,
                        writeCapable ? "Token can publish PatchPilot branches." : "Token does not expose branch write permission.",
                        writeCapable ? "No action needed." : "Grant Contents: Read and write for the demo repository."
                ),
                check(
                        "Pull Request creation",
                        writeCapable,
                        writeCapable ? "Token can create Pull Requests for pushed branches." : "Pull Request creation permission was not confirmed.",
                        writeCapable ? "No action needed." : "Grant Pull requests: Read and write for the demo repository."
                ),
                check(
                        "Issue feedback",
                        writeCapable,
                        writeCapable ? "Token is likely able to write task status feedback on issues." : "Issue feedback permission was not confirmed.",
                        writeCapable ? "No action needed." : "Grant Issues: Read and write if PatchPilot should comment on issues."
                )
        );
    }

    private static GitHubPublishPermissionReadinessCheckVo check(
            String name,
            boolean ready,
            String summary,
            String nextAction
    ) {
        return new GitHubPublishPermissionReadinessCheckVo(name, ready ? READY : NEEDS_ATTENTION, summary, nextAction);
    }

    private static String summary(String status, boolean canRead) {
        if (READY.equals(status)) {
            return "GitHub token has repository publish permissions for PatchPilot push and Pull Request creation.";
        }
        if (canRead) {
            return "GitHub token can read the repository but does not expose write permissions required for publish.";
        }
        return "GitHub repository permissions are not sufficient for PatchPilot publish.";
    }

    private static String nextAction(String status) {
        if (READY.equals(status)) {
            return "Continue with the live /agent fix demo.";
        }
        return "Grant Contents: Read and write, Pull requests: Read and write, and Issues: Read and write on the demo repository; then restart PatchPilot if the token changed.";
    }

    private static List<String> evidenceNotes(
            String repository,
            GitHubRepositoryPermissionProbeResult result,
            boolean writeCapable
    ) {
        return List.of(
                "Repository: " + repository,
                "Default branch: " + result.defaultBranch(),
                "Permission pull: " + result.pull(),
                "Permission push: " + result.push(),
                "Permission maintain: " + result.maintain(),
                "Permission admin: " + result.admin(),
                "Write capable: " + writeCapable
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
