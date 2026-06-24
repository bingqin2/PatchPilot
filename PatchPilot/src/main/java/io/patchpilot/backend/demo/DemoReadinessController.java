package io.patchpilot.backend.demo;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import io.patchpilot.backend.demo.domain.DemoScriptVo;
import io.patchpilot.backend.demo.domain.DemoSessionSnapshotVo;
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
    private final DemoRunbookService demoRunbookService;
    private final DemoScriptService demoScriptService;
    private final DemoSessionSnapshotService demoSessionSnapshotService;
    private final DemoSessionReportService demoSessionReportService;

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

    @GetMapping("/runbook")
    public ApiResponse<String> getRunbook() {
        return ApiResponse.ok(demoRunbookService.getRunbook());
    }

    @GetMapping("/script")
    public ApiResponse<DemoScriptVo> getScript() {
        return ApiResponse.ok(demoScriptService.getScript());
    }

    @GetMapping("/session-snapshot")
    public ApiResponse<DemoSessionSnapshotVo> getSessionSnapshot() {
        return ApiResponse.ok(demoSessionSnapshotService.getSessionSnapshot());
    }

    @GetMapping("/session-report")
    public ApiResponse<String> getSessionReport() {
        return ApiResponse.ok(demoSessionReportService.getSessionReport());
    }
}
