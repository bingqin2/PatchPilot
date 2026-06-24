package io.patchpilot.backend.language;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.language.domain.RepositoryPreflightRequest;
import io.patchpilot.backend.language.domain.RepositoryPreflightVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/repository-preflight")
@RequiredArgsConstructor
public class RepositoryPreflightController {

    private final RepositoryPreflightService repositoryPreflightService;

    @PostMapping
    public ResponseEntity<ApiResponse<RepositoryPreflightVo>> preflight(@RequestBody RepositoryPreflightRequest request) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(repositoryPreflightService.preflight(request)));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(exception.getMessage()));
        }
    }
}
