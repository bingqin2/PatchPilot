package io.patchpilot.backend.evaluation;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.evaluation.domain.EvaluationCaseVo;
import io.patchpilot.backend.evaluation.domain.EvaluationCaseFixtureReadinessSummaryVo;
import io.patchpilot.backend.evaluation.domain.EvaluationFixtureBaselineRunArchiveVo;
import io.patchpilot.backend.evaluation.domain.EvaluationFixtureBaselineRunRegressionSummaryVo;
import io.patchpilot.backend.evaluation.domain.EvaluationFixtureBaselineSummaryVo;
import io.patchpilot.backend.evaluation.domain.EvaluationRunArchiveVo;
import io.patchpilot.backend.evaluation.domain.EvaluationRunPreviewVo;
import io.patchpilot.backend.evaluation.domain.EvaluationRunSnapshotArchiveVo;
import io.patchpilot.backend.evaluation.domain.EvaluationSummaryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/evaluation")
@RequiredArgsConstructor
public class EvaluationCaseController {

    private final EvaluationCaseCatalogService evaluationCaseCatalogService;
    private final EvaluationRunSnapshotArchiveService evaluationRunSnapshotArchiveService;
    private final EvaluationCaseFixtureReadinessService evaluationCaseFixtureReadinessService;
    private final EvaluationFixtureBaselineService evaluationFixtureBaselineService;
    private final EvaluationFixtureBaselineRunArchiveService evaluationFixtureBaselineRunArchiveService;
    private final EvaluationFixtureBaselineRunRegressionSummaryService evaluationFixtureBaselineRunRegressionSummaryService;
    private final EvaluationRunArchiveService evaluationRunArchiveService;

    @GetMapping("/cases")
    public ApiResponse<List<EvaluationCaseVo>> listEvaluationCases() {
        return ApiResponse.ok(evaluationCaseCatalogService.listEvaluationCases());
    }

    @GetMapping("/summary")
    public ApiResponse<EvaluationSummaryVo> getEvaluationSummary() {
        return ApiResponse.ok(evaluationCaseCatalogService.getEvaluationSummary());
    }

    @GetMapping("/run-preview")
    public ApiResponse<EvaluationRunPreviewVo> getEvaluationRunPreview() {
        return ApiResponse.ok(evaluationCaseCatalogService.getEvaluationRunPreview());
    }

    @GetMapping("/case-readiness")
    public ApiResponse<EvaluationCaseFixtureReadinessSummaryVo> getEvaluationCaseFixtureReadiness() {
        return ApiResponse.ok(evaluationCaseFixtureReadinessService.getReadinessSummary());
    }

    @PostMapping("/fixture-baseline")
    public ApiResponse<EvaluationFixtureBaselineSummaryVo> runEvaluationFixtureBaseline() {
        return ApiResponse.ok(evaluationFixtureBaselineService.runBaseline());
    }

    @PostMapping("/fixture-baseline-runs")
    public ApiResponse<EvaluationFixtureBaselineRunArchiveVo> runAndArchiveEvaluationFixtureBaseline() {
        return ApiResponse.ok(evaluationFixtureBaselineRunArchiveService.runAndArchiveBaseline());
    }

    @GetMapping("/fixture-baseline-runs")
    public ApiResponse<List<EvaluationFixtureBaselineRunArchiveVo>> listEvaluationFixtureBaselineRuns() {
        return ApiResponse.ok(evaluationFixtureBaselineRunArchiveService.listRecentArchives());
    }

    @GetMapping("/fixture-baseline-runs/summary")
    public ApiResponse<EvaluationFixtureBaselineRunRegressionSummaryVo> getEvaluationFixtureBaselineRunRegressionSummary() {
        return ApiResponse.ok(evaluationFixtureBaselineRunRegressionSummaryService.getRegressionSummary());
    }

    @GetMapping(value = "/fixture-baseline-runs/{runId}/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadEvaluationFixtureBaselineRunReport(@PathVariable String runId) {
        return evaluationFixtureBaselineRunArchiveService.findArchive(runId)
                .map(archive -> markdownAttachment(
                        "patchpilot-evaluation-fixture-baseline-run-" + safeFilenamePart(archive.id()) + ".md",
                        archive.report()
                ))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/run-snapshots")
    public ApiResponse<EvaluationRunSnapshotArchiveVo> archiveCurrentEvaluationRunPreview() {
        return ApiResponse.ok(evaluationRunSnapshotArchiveService.archiveCurrentPreview());
    }

    @GetMapping("/run-snapshots")
    public ApiResponse<List<EvaluationRunSnapshotArchiveVo>> listEvaluationRunSnapshotArchives() {
        return ApiResponse.ok(evaluationRunSnapshotArchiveService.listRecentArchives());
    }

    @GetMapping(value = "/run-snapshots/{snapshotId}/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadEvaluationRunSnapshotReport(@PathVariable String snapshotId) {
        return evaluationRunSnapshotArchiveService.findArchive(snapshotId)
                .map(archive -> markdownAttachment(
                        "patchpilot-evaluation-run-snapshot-" + safeFilenamePart(archive.id()) + ".md",
                        archive.report()
                ))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/runs")
    public ApiResponse<EvaluationRunArchiveVo> runAndArchiveEvaluation() {
        return ApiResponse.ok(evaluationRunArchiveService.runAndArchiveEvaluation());
    }

    @GetMapping("/runs")
    public ApiResponse<List<EvaluationRunArchiveVo>> listEvaluationRuns() {
        return ApiResponse.ok(evaluationRunArchiveService.listRecentArchives());
    }

    @GetMapping(value = "/runs/{runId}/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadEvaluationRunReport(@PathVariable String runId) {
        return evaluationRunArchiveService.findArchive(runId)
                .map(archive -> markdownAttachment(
                        "patchpilot-evaluation-run-" + safeFilenamePart(archive.id()) + ".md",
                        archive.report()
                ))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private static ResponseEntity<String> markdownAttachment(String filename, String body) {
        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(filename, StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/markdown;charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(body);
    }

    private static String safeFilenamePart(String value) {
        return value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9._-]", "-");
    }
}
