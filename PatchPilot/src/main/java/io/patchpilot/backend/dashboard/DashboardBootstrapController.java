package io.patchpilot.backend.dashboard;

import io.patchpilot.backend.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardBootstrapController {

    private final DashboardBootstrapService dashboardBootstrapService;

    @GetMapping("/bootstrap")
    public ApiResponse<DashboardBootstrapVo> getBootstrap() {
        return ApiResponse.ok(dashboardBootstrapService.getBootstrap());
    }
}
