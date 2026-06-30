package io.patchpilot.backend.github.credential;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.github.credential.domain.GitHubCredentialReadinessVo;
import io.patchpilot.backend.github.credential.domain.GitHubPublishPermissionReadinessVo;
import io.patchpilot.backend.github.credential.domain.GitHubPublishReadinessVo;
import io.patchpilot.backend.github.credential.domain.GitHubRepositoryAccessReadinessVo;
import io.patchpilot.backend.github.credential.domain.GitHubWebhookSetupReadinessVo;
import io.patchpilot.backend.github.credential.domain.GitHubWebhookUrlReadinessVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/github")
@RequiredArgsConstructor
public class GitHubCredentialReadinessController {

    private final GitHubCredentialReadinessService gitHubCredentialReadinessService;
    private final GitHubRepositoryAccessReadinessService gitHubRepositoryAccessReadinessService;
    private final GitHubWebhookUrlReadinessService gitHubWebhookUrlReadinessService;
    private final GitHubWebhookSetupReadinessService gitHubWebhookSetupReadinessService;
    private final GitHubPublishReadinessService gitHubPublishReadinessService;
    private final GitHubPublishPermissionReadinessService gitHubPublishPermissionReadinessService;

    @GetMapping("/credential-readiness")
    public ApiResponse<GitHubCredentialReadinessVo> getReadiness() {
        return ApiResponse.ok(gitHubCredentialReadinessService.getReadiness());
    }

    @GetMapping("/repository-access-readiness")
    public ApiResponse<GitHubRepositoryAccessReadinessVo> getRepositoryAccessReadiness(
            @RequestParam(required = false) String owner,
            @RequestParam(required = false) String repository
    ) {
        return ApiResponse.ok(gitHubRepositoryAccessReadinessService.getReadiness(owner, repository));
    }

    @GetMapping("/webhook-url-readiness")
    public ApiResponse<GitHubWebhookUrlReadinessVo> getWebhookUrlReadiness() {
        return ApiResponse.ok(gitHubWebhookUrlReadinessService.getReadiness());
    }

    @GetMapping("/webhook-setup-readiness")
    public ApiResponse<GitHubWebhookSetupReadinessVo> getWebhookSetupReadiness() {
        return ApiResponse.ok(gitHubWebhookSetupReadinessService.getReadiness());
    }

    @GetMapping("/publish-readiness")
    public ApiResponse<GitHubPublishReadinessVo> getPublishReadiness(
            @RequestParam(required = false) String owner,
            @RequestParam(required = false) String repository
    ) {
        return ApiResponse.ok(gitHubPublishReadinessService.getReadiness(owner, repository));
    }

    @GetMapping("/publish-permission-readiness")
    public ApiResponse<GitHubPublishPermissionReadinessVo> getPublishPermissionReadiness(
            @RequestParam(required = false) String owner,
            @RequestParam(required = false) String repository
    ) {
        return ApiResponse.ok(gitHubPublishPermissionReadinessService.getReadiness(owner, repository));
    }
}
