package io.patchpilot.backend.github.credential;

import io.patchpilot.backend.demo.config.DemoProperties;
import io.patchpilot.backend.github.credential.domain.GitHubCredentialReadinessVo;
import io.patchpilot.backend.github.credential.domain.GitHubPublishReadinessCheckVo;
import io.patchpilot.backend.github.credential.domain.GitHubPublishReadinessVo;
import io.patchpilot.backend.github.credential.domain.GitHubRepositoryAccessReadinessVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

@Service
public class GitHubPublishReadinessService {

    public static final String READY = "READY";
    public static final String NEEDS_ATTENTION = "NEEDS_ATTENTION";
    public static final String BLOCKED = "BLOCKED";

    private static final String SIDE_EFFECT_CONTRACT = "Read-only readiness probe: this endpoint does not run git push, "
            + "does not create branches, does not create Pull Requests, does not write issue comments, and does not expose tokens.";
    private static final String SAFE_PUBLISH_COMMAND = "git push origin HEAD:<patchpilot-branch>";

    private final BiFunction<String, String, GitHubPublishReadinessVo> readinessSupplier;

    @Autowired
    public GitHubPublishReadinessService(
            DemoProperties demoProperties,
            GitHubCredentialReadinessService credentialReadinessService,
            GitHubRepositoryAccessReadinessService repositoryAccessReadinessService
    ) {
        this((owner, repository) -> buildReadiness(
                demoProperties,
                credentialReadinessService,
                repositoryAccessReadinessService,
                owner,
                repository
        ));
    }

    public GitHubPublishReadinessService(BiFunction<String, String, GitHubPublishReadinessVo> readinessSupplier) {
        this.readinessSupplier = readinessSupplier;
    }

    public GitHubPublishReadinessVo getReadiness(String owner, String repository) {
        return readinessSupplier.apply(owner, repository);
    }

    private static GitHubPublishReadinessVo buildReadiness(
            DemoProperties demoProperties,
            GitHubCredentialReadinessService credentialReadinessService,
            GitHubRepositoryAccessReadinessService repositoryAccessReadinessService,
            String owner,
            String repository
    ) {
        String normalizedOwner = firstNonBlank(owner, demoProperties.getRepositoryOwner());
        String normalizedRepository = firstNonBlank(repository, demoProperties.getRepositoryName());
        GitHubCredentialReadinessVo credential = credentialReadinessService.getReadiness();
        GitHubRepositoryAccessReadinessVo repositoryAccess = repositoryAccessReadinessService.getReadiness(
                normalizedOwner,
                normalizedRepository
        );

        List<GitHubPublishReadinessCheckVo> checks = List.of(
                tokenCheck(credential),
                repositoryAccessCheck(repositoryAccess),
                publishCommandSafetyCheck()
        );
        String status = aggregateStatus(checks);
        boolean publishReady = READY.equals(status);
        String repositoryFullName = repositoryAccess.repository();
        String summary = summary(status);
        String nextAction = nextAction(status, credential, repositoryAccess);
        List<String> evidenceNotes = evidenceNotes(credential, repositoryAccess);

        return new GitHubPublishReadinessVo(
                status,
                publishReady,
                credential.tokenConfigured(),
                repositoryAccess.repositoryConfigured(),
                repositoryFullName,
                repositoryAccess.defaultBranch(),
                summary,
                nextAction,
                SAFE_PUBLISH_COMMAND,
                SIDE_EFFECT_CONTRACT,
                checks,
                evidenceNotes,
                Instant.now()
        );
    }

    private static GitHubPublishReadinessCheckVo tokenCheck(GitHubCredentialReadinessVo credential) {
        String status = credential.tokenConfigured() ? normalizeReadinessStatus(credential.status()) : BLOCKED;
        return new GitHubPublishReadinessCheckVo(
                "GitHub token",
                status,
                credential.message(),
                credential.operatorAction()
        );
    }

    private static GitHubPublishReadinessCheckVo repositoryAccessCheck(GitHubRepositoryAccessReadinessVo repositoryAccess) {
        String status;
        if (!repositoryAccess.tokenConfigured()) {
            status = BLOCKED;
        } else if (!repositoryAccess.repositoryConfigured()) {
            status = NEEDS_ATTENTION;
        } else {
            status = normalizeReadinessStatus(repositoryAccess.status());
        }
        return new GitHubPublishReadinessCheckVo(
                "Repository access",
                status,
                repositoryAccess.message(),
                repositoryAccess.operatorAction()
        );
    }

    private static GitHubPublishReadinessCheckVo publishCommandSafetyCheck() {
        return new GitHubPublishReadinessCheckVo(
                "Publish command safety",
                READY,
                "PatchPilot uses a bounded push shape: " + SAFE_PUBLISH_COMMAND + ".",
                "No action needed."
        );
    }

    private static String aggregateStatus(List<GitHubPublishReadinessCheckVo> checks) {
        if (checks.stream().anyMatch(check -> BLOCKED.equals(check.status()))) {
            return BLOCKED;
        }
        if (checks.stream().anyMatch(check -> !READY.equals(check.status()))) {
            return NEEDS_ATTENTION;
        }
        return READY;
    }

    private static String normalizeReadinessStatus(String status) {
        return READY.equals(status) ? READY : NEEDS_ATTENTION;
    }

    private static String summary(String status) {
        return switch (status) {
            case READY -> "GitHub publish path is ready for PatchPilot push and Pull Request creation.";
            case BLOCKED -> "GitHub publish path is blocked before PatchPilot can push branches or create Pull Requests.";
            default -> "GitHub publish path needs attention before a live issue-to-PR demo.";
        };
    }

    private static String nextAction(
            String status,
            GitHubCredentialReadinessVo credential,
            GitHubRepositoryAccessReadinessVo repositoryAccess
    ) {
        if (READY.equals(status)) {
            return "Continue with the live /agent fix demo.";
        }
        if (!credential.tokenConfigured()) {
            return "Configure PATCHPILOT_GITHUB_TOKEN and restart the backend.";
        }
        if (!repositoryAccess.repositoryConfigured()) {
            return "Set PATCHPILOT_DEMO_REPOSITORY_OWNER and PATCHPILOT_DEMO_REPOSITORY_NAME or pass owner and repository query parameters.";
        }
        return "Resolve GitHub repository access readiness, then rerun this publish readiness check.";
    }

    private static List<String> evidenceNotes(
            GitHubCredentialReadinessVo credential,
            GitHubRepositoryAccessReadinessVo repositoryAccess
    ) {
        List<String> notes = new ArrayList<>();
        notes.add("Token configured: " + credential.tokenConfigured());
        notes.add("Repository configured: " + repositoryAccess.repositoryConfigured());
        if (StringUtils.hasText(repositoryAccess.repository())) {
            notes.add("Repository: " + repositoryAccess.repository());
        }
        if (StringUtils.hasText(repositoryAccess.defaultBranch())) {
            notes.add("Default branch: " + repositoryAccess.defaultBranch());
        }
        notes.add("Credential probe message: " + credential.message());
        notes.add("Repository access probe message: " + repositoryAccess.message());
        return notes;
    }

    private static String firstNonBlank(String primary, String fallback) {
        if (StringUtils.hasText(primary)) {
            return primary.trim();
        }
        return StringUtils.hasText(fallback) ? fallback.trim() : "";
    }
}
