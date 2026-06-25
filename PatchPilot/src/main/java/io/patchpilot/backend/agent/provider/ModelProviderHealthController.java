package io.patchpilot.backend.agent.provider;

import io.patchpilot.backend.agent.provider.domain.ModelProviderHealthVo;
import io.patchpilot.backend.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/model-provider")
@RequiredArgsConstructor
public class ModelProviderHealthController {

    private final ModelProviderHealthService modelProviderHealthService;

    @GetMapping("/health")
    public ApiResponse<ModelProviderHealthVo> getHealth() {
        return ApiResponse.ok(modelProviderHealthService.getHealth());
    }
}
