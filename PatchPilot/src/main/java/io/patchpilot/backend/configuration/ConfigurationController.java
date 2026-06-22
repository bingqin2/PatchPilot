package io.patchpilot.backend.configuration;

import io.patchpilot.backend.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/configuration")
@RequiredArgsConstructor
public class ConfigurationController {

    private final ConfigurationSummaryService configurationSummaryService;

    @GetMapping("/summary")
    public ApiResponse<ConfigurationSummaryVo> getConfigurationSummary() {
        return ApiResponse.ok(configurationSummaryService.getConfigurationSummary());
    }
}
