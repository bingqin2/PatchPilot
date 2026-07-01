package io.patchpilot.backend.security.exposure;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureReadinessVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/security")
@RequiredArgsConstructor
public class ExternalExposureReadinessController {

    private final ExternalExposureReadinessProvider readinessProvider;

    @GetMapping("/external-exposure-readiness")
    public ApiResponse<ExternalExposureReadinessVo> getExternalExposureReadiness() {
        return ApiResponse.ok(readinessProvider.getReadiness());
    }
}
