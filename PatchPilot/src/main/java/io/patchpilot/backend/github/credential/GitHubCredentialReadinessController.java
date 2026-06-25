package io.patchpilot.backend.github.credential;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.github.credential.domain.GitHubCredentialReadinessVo;
import io.patchpilot.backend.github.credential.domain.GitHubRepositoryAccessReadinessVo;
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
}
