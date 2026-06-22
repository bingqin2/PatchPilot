package io.patchpilot.backend.demo;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
@RequiredArgsConstructor
public class DemoReadinessController {

    private final DemoReadinessService demoReadinessService;

    @GetMapping("/readiness")
    public ApiResponse<DemoReadinessVo> getReadiness() {
        return ApiResponse.ok(demoReadinessService.getReadiness());
    }
}
