package io.patchpilot.backend.evaluation;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.evaluation.domain.EvaluationCaseVo;
import io.patchpilot.backend.evaluation.domain.EvaluationSummaryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/evaluation")
@RequiredArgsConstructor
public class EvaluationCaseController {

    private final EvaluationCaseCatalogService evaluationCaseCatalogService;

    @GetMapping("/cases")
    public ApiResponse<List<EvaluationCaseVo>> listEvaluationCases() {
        return ApiResponse.ok(evaluationCaseCatalogService.listEvaluationCases());
    }

    @GetMapping("/summary")
    public ApiResponse<EvaluationSummaryVo> getEvaluationSummary() {
        return ApiResponse.ok(evaluationCaseCatalogService.getEvaluationSummary());
    }
}
