package io.patchpilot.backend.demo;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
@RequiredArgsConstructor
public class DemoReadinessController {

    private final DemoReadinessService demoReadinessService;
    private final DemoSmokeChecklistService demoSmokeChecklistService;
    private final DemoEvidenceBundleService demoEvidenceBundleService;

    @GetMapping("/readiness")
    public ApiResponse<DemoReadinessVo> getReadiness() {
        return ApiResponse.ok(demoReadinessService.getReadiness());
    }

    @GetMapping("/smoke-checklist")
    public ApiResponse<DemoSmokeChecklistVo> getSmokeChecklist() {
        return ApiResponse.ok(demoSmokeChecklistService.getSmokeChecklist());
    }

    @GetMapping("/evidence-bundle")
    public ApiResponse<DemoEvidenceBundleVo> getEvidenceBundle() {
        return ApiResponse.ok(demoEvidenceBundleService.getEvidenceBundle());
    }
}
