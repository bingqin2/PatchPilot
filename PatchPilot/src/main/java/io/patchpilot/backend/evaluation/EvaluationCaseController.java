package io.patchpilot.backend.evaluation;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.evaluation.domain.EvaluationCaseVo;
import io.patchpilot.backend.evaluation.domain.EvaluationCaseFixtureReadinessSummaryVo;
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
