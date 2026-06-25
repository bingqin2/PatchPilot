package io.patchpilot.backend.github.credential;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.github.credential.domain.GitHubCredentialReadinessVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/github")
@RequiredArgsConstructor
public class GitHubCredentialReadinessController {

    private final GitHubCredentialReadinessService gitHubCredentialReadinessService;

    @GetMapping("/credential-readiness")
    public ApiResponse<GitHubCredentialReadinessVo> getReadiness() {
        return ApiResponse.ok(gitHubCredentialReadinessService.getReadiness());
    }
}
