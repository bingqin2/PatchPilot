package io.patchpilot.backend.github.credential;

import io.patchpilot.backend.demo.config.DemoProperties;
import io.patchpilot.backend.github.config.GitHubProperties;
import io.patchpilot.backend.github.credential.domain.GitHubCredentialReadinessException;
import io.patchpilot.backend.github.credential.domain.GitHubLivePublishPreflightCheckVo;
import io.patchpilot.backend.github.credential.domain.GitHubLivePublishPreflightProbeResult;
import io.patchpilot.backend.github.credential.domain.GitHubLivePublishPreflightVo;
import io.patchpilot.backend.github.credential.domain.GitHubPublishPermissionReadinessVo;
import io.patchpilot.backend.github.credential.domain.GitHubPublishReadinessVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

@Service
public class GitHubLivePublishPreflightService {

    public static final String READY = "READY";
    public static final String NEEDS_ATTENTION = "NEEDS_ATTENTION";
    public static final String BLOCKED = "BLOCKED";

    private static final String PATCHPILOT_BRANCH_PREFIX = "patchpilot/";
    private static final String SIDE_EFFECT_CONTRACT = "Read-only live publish preflight: this endpoint does not run git push, "
            + "does not create branches, does not open Pull Requests, does not write issue comments, and does not expose tokens.";

    private final BiFunction<String, String, GitHubLivePublishPreflightVo> preflightSupplier;

    @Autowired
    public GitHubLivePublishPreflightService(
            GitHubProperties gitHubProperties,
            DemoProperties demoProperties,
            GitHubPublishReadinessService publishReadinessService,
            GitHubPublishPermissionReadinessService publishPermissionReadinessService,
            GitHubLivePublishPreflightProbe livePublishPreflightProbe
    ) {
        this(
                gitHubProperties,
                demoProperties,
                publishReadinessService,
                publishPermissionReadinessService,
                livePublishPreflightProbe,
                Instant::now,
                System::currentTimeMillis
        );
    }

    GitHubLivePublishPreflightService(
            GitHubProperties gitHubProperties,
            DemoProperties demoProperties,
            GitHubPublishReadinessService publishReadinessService,
            GitHubPublishPermissionReadinessService publishPermissionReadinessService,
            GitHubLivePublishPreflightProbe livePublishPreflightProbe,
            Supplier<Instant> clock,
            LongSupplier ticker
    ) {
        this((owner, repository) -> buildPreflight(
                gitHubProperties,
                demoProperties,
                publishReadinessService,
                publishPermissionReadinessService,
                livePublishPreflightProbe,
                clock,
                ticker,
                owner,
                repository
        ));
    }

    public GitHubLivePublishPreflightService(BiFunction<String, String, GitHubLivePublishPreflightVo> preflightSupplier) {
        this.preflightSupplier = preflightSupplier;
    }

    public GitHubLivePublishPreflightVo getPreflight(String owner, String repository) {
        return preflightSupplier.apply(owner, repository);
    }

    private static GitHubLivePublishPreflightVo buildPreflight(
            GitHubProperties gitHubProperties,
            DemoProperties demoProperties,
            GitHubPublishReadinessService publishReadinessService,
            GitHubPublishPermissionReadinessService publishPermissionReadinessService,
            GitHubLivePublishPreflightProbe livePublishPreflightProbe,
            Supplier<Instant> clock,
            LongSupplier ticker,
            String owner,
            String repository
    ) {
        Instant checkedAt = clock.get();
        String normalizedOwner = firstNonBlank(owner, demoProperties.getRepositoryOwner());
        String normalizedRepository = firstNonBlank(repository, demoProperties.getRepositoryName());
        String repositoryFullName = repositoryFullName(normalizedOwner, normalizedRepository);
        boolean tokenConfigured = StringUtils.hasText(token(gitHubProperties));
        boolean repositoryConfigured = StringUtils.hasText(normalizedOwner) && StringUtils.hasText(normalizedRepository);

        if (!repositoryConfigured) {
            return skipped(
                    tokenConfigured,
                    false,
                    "",
                    NEEDS_ATTENTION,
                    "Repository owner and name are required for live GitHub publish preflight.",
                    "Set PATCHPILOT_DEMO_REPOSITORY_OWNER and PATCHPILOT_DEMO_REPOSITORY_NAME or pass owner and repository query parameters.",
                    checkedAt
            );
        }
        if (!tokenConfigured) {
            return skipped(
                    false,
                    true,
                    repositoryFullName,
                    BLOCKED,
                    "GitHub token is required for live GitHub publish preflight.",
                    "Configure PATCHPILOT_GITHUB_TOKEN and restart the backend.",
                    checkedAt
            );
        }

        GitHubPublishReadinessVo publishReadiness = publishReadinessService.getReadiness(normalizedOwner, normalizedRepository);
        GitHubPublishPermissionReadinessVo permissionReadiness = publishPermissionReadinessService.getReadiness(
                normalizedOwner,
                normalizedRepository
        );

        List<GitHubLivePublishPreflightCheckVo> baseChecks = new ArrayList<>();
        baseChecks.add(new GitHubLivePublishPreflightCheckVo(
                "Publish path readiness",
                normalizeStatus(publishReadiness.status()),
                publishReadiness.summary(),
                publishReadiness.nextAction()
        ));
        baseChecks.add(new GitHubLivePublishPreflightCheckVo(
                "Publish permission readiness",
                normalizeStatus(permissionReadiness.status()),
                permissionReadiness.summary(),
                permissionReadiness.nextAction()
        ));

        if (baseChecks.stream().anyMatch(check -> BLOCKED.equals(check.status()))) {
            return response(
                    BLOCKED,
                    false,
                    true,
                    true,
                    repositoryFullName,
                    firstNonBlank(permissionReadiness.defaultBranch(), publishReadiness.defaultBranch()),
                    List.of(),
                    List.of(),
                    "Live GitHub publish preflight is blocked by publish readiness.",
                    blockedAction(baseChecks),
                    baseChecks,
                    evidenceNotes(repositoryFullName, List.of(), List.of(), "Live probe skipped because publish readiness is blocked."),
                    0,
                    checkedAt
            );
        }

        long startedAt = ticker.getAsLong();
        try {
            GitHubLivePublishPreflightProbeResult probeResult = livePublishPreflightProbe.check(
                    token(gitHubProperties),
                    normalizedOwner,
                    normalizedRepository
            );
            long latencyMs = Math.max(0, ticker.getAsLong() - startedAt);
            List<String> patchpilotBranches = patchpilotBranches(probeResult.branchNames());
            List<String> openPullRequests = probeResult.openPatchpilotPullRequestUrls();
            List<GitHubLivePublishPreflightCheckVo> checks = new ArrayList<>(baseChecks);
            checks.add(branchInventoryCheck(patchpilotBranches));
            checks.add(openPullRequestCheck(openPullRequests));
            String status = aggregateStatus(checks);
            return response(
                    status,
                    READY.equals(status),
                    true,
                    true,
                    repositoryFullName,
                    firstNonBlank(probeResult.defaultBranch(), firstNonBlank(permissionReadiness.defaultBranch(), publishReadiness.defaultBranch())),
                    patchpilotBranches,
                    openPullRequests,
                    summary(status),
                    nextAction(status),
                    checks,
                    evidenceNotes(repositoryFullName, patchpilotBranches, openPullRequests, "Live probe completed."),
                    latencyMs,
                    checkedAt
            );
        } catch (GitHubCredentialReadinessException exception) {
            long latencyMs = Math.max(0, ticker.getAsLong() - startedAt);
            List<GitHubLivePublishPreflightCheckVo> checks = new ArrayList<>(baseChecks);
            checks.add(new GitHubLivePublishPreflightCheckVo(
                    "Live GitHub metadata probe",
                    NEEDS_ATTENTION,
                    "Could not confirm branch and Pull Request metadata from GitHub.",
                    "Check repository access, token permissions, and GitHub API availability; then rerun live publish preflight."
            ));
            return response(
                    NEEDS_ATTENTION,
                    false,
                    true,
                    true,
                    repositoryFullName,
                    firstNonBlank(permissionReadiness.defaultBranch(), publishReadiness.defaultBranch()),
                    List.of(),
                    List.of(),
                    "Live GitHub publish preflight could not confirm branch and Pull Request state.",
                    "Resolve GitHub repository access and API probe errors, then rerun live publish preflight.",
                    checks,
                    List.of("Repository: " + repositoryFullName, "Probe message: " + exception.getMessage()),
                    latencyMs,
                    checkedAt
            );
        }
    }

    private static GitHubLivePublishPreflightVo skipped(
            boolean tokenConfigured,
            boolean repositoryConfigured,
            String repository,
            String status,
            String summary,
            String nextAction,
            Instant checkedAt
    ) {
        return response(
                status,
                false,
                tokenConfigured,
                repositoryConfigured,
                repository,
                null,
                List.of(),
                List.of(),
                summary,
                nextAction,
                List.of(new GitHubLivePublishPreflightCheckVo("Configuration", status, summary, nextAction)),
                List.of("Token configured: " + tokenConfigured, "Repository configured: " + repositoryConfigured),
                0,
                checkedAt
        );
    }

    private static GitHubLivePublishPreflightVo response(
            String status,
            boolean livePublishReady,
            boolean tokenConfigured,
            boolean repositoryConfigured,
            String repository,
            String defaultBranch,
            List<String> patchpilotBranches,
            List<String> openPatchpilotPullRequests,
            String summary,
            String nextAction,
            List<GitHubLivePublishPreflightCheckVo> checks,
            List<String> evidenceNotes,
            long latencyMs,
            Instant checkedAt
    ) {
        return new GitHubLivePublishPreflightVo(
                status,
                livePublishReady,
                tokenConfigured,
                repositoryConfigured,
                repository,
                defaultBranch,
                patchpilotBranches,
                openPatchpilotPullRequests,
                summary,
                nextAction,
                SIDE_EFFECT_CONTRACT,
                checks,
                evidenceNotes,
                latencyMs,
                checkedAt
        );
    }

    private static String normalizeStatus(String status) {
        if (READY.equals(status) || BLOCKED.equals(status)) {
            return status;
        }
        return NEEDS_ATTENTION;
    }

    private static String aggregateStatus(List<GitHubLivePublishPreflightCheckVo> checks) {
        if (checks.stream().anyMatch(check -> BLOCKED.equals(check.status()))) {
            return BLOCKED;
        }
        if (checks.stream().anyMatch(check -> !READY.equals(check.status()))) {
            return NEEDS_ATTENTION;
        }
        return READY;
    }

    private static GitHubLivePublishPreflightCheckVo branchInventoryCheck(List<String> patchpilotBranches) {
        boolean ready = patchpilotBranches.isEmpty();
        return new GitHubLivePublishPreflightCheckVo(
                "PatchPilot branch inventory",
                ready ? READY : NEEDS_ATTENTION,
                ready ? "No existing patchpilot/* branches were found." : "Found " + patchpilotBranches.size() + " existing patchpilot/* branch(es).",
                ready ? "No action needed." : "Delete stale patchpilot/* branches or confirm they are intentionally kept before demo launch."
        );
    }

    private static GitHubLivePublishPreflightCheckVo openPullRequestCheck(List<String> openPullRequests) {
        boolean ready = openPullRequests.isEmpty();
        return new GitHubLivePublishPreflightCheckVo(
                "Open PatchPilot Pull Requests",
                ready ? READY : NEEDS_ATTENTION,
                ready ? "No open PatchPilot Pull Requests were found." : "Found " + openPullRequests.size() + " open PatchPilot Pull Request.",
                ready ? "No action needed." : "Close, merge, or intentionally keep the existing PatchPilot Pull Request before demo launch."
        );
    }

    private static String summary(String status) {
        if (READY.equals(status)) {
            return "Live GitHub publish preflight is ready for a clean PatchPilot branch and Pull Request.";
        }
        return "Live GitHub publish preflight found existing PatchPilot publish artifacts.";
    }

    private static String nextAction(String status) {
        if (READY.equals(status)) {
            return "Post the live /agent fix comment when the rest of launch readiness is READY.";
        }
        return "Review, close or merge stale PatchPilot Pull Requests, and delete old patchpilot/* branches before the live demo.";
    }

    private static String blockedAction(List<GitHubLivePublishPreflightCheckVo> checks) {
        return checks.stream()
                .filter(check -> BLOCKED.equals(check.status()))
                .map(GitHubLivePublishPreflightCheckVo::nextAction)
                .findFirst()
                .orElse("Resolve blocked publish readiness, then rerun live publish preflight.");
    }

    private static List<String> patchpilotBranches(List<String> branchNames) {
        return branchNames.stream()
                .filter(branchName -> branchName.startsWith(PATCHPILOT_BRANCH_PREFIX))
                .toList();
    }

    private static List<String> evidenceNotes(
            String repository,
            List<String> patchpilotBranches,
            List<String> openPullRequests,
            String probeMessage
    ) {
        return List.of(
                "Repository: " + repository,
                "PatchPilot branch count: " + patchpilotBranches.size(),
                "Open PatchPilot Pull Request count: " + openPullRequests.size(),
                probeMessage
        );
    }

    private static String token(GitHubProperties gitHubProperties) {
        return StringUtils.hasText(gitHubProperties.getToken()) ? gitHubProperties.getToken().trim() : "";
    }

    private static String firstNonBlank(String primary, String fallback) {
        if (StringUtils.hasText(primary)) {
            return primary.trim();
        }
        return StringUtils.hasText(fallback) ? fallback.trim() : "";
    }

    private static String repositoryFullName(String owner, String repository) {
        return StringUtils.hasText(owner) && StringUtils.hasText(repository) ? owner + "/" + repository : "";
    }
}
