package io.patchpilot.backend.demo;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import io.patchpilot.backend.demo.domain.DemoScriptVo;
import io.patchpilot.backend.demo.domain.DemoSessionArchiveVo;
import io.patchpilot.backend.demo.domain.DemoSessionSnapshotVo;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistVo;
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
    private final DemoSessionArchiveService demoSessionArchiveService;

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

    @GetMapping(value = "/session-report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadSessionReport() {
        return markdownAttachment(
                "patchpilot-demo-session-report.md",
                demoSessionReportService.getSessionReport()
        );
    }

    @PostMapping("/session-archives")
    public ApiResponse<DemoSessionArchiveVo> archiveCurrentSession() {
        return ApiResponse.ok(demoSessionArchiveService.archiveCurrentSession());
    }

    @GetMapping("/session-archives")
    public ApiResponse<List<DemoSessionArchiveVo>> listSessionArchives() {
        return ApiResponse.ok(demoSessionArchiveService.listRecentArchives());
    }

    @GetMapping(value = "/session-archives/{archiveId}/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadArchivedSessionReport(@PathVariable String archiveId) {
        return demoSessionArchiveService.findArchive(archiveId)
                .map(archive -> markdownAttachment(
                        "patchpilot-demo-session-" + safeFilenamePart(archive.id()) + ".md",
                        archive.report()
                ))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private static ResponseEntity<String> markdownAttachment(String filename, String report) {
        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(filename, StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .contentType(new MediaType("text", "markdown", StandardCharsets.UTF_8))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(report);
    }

    private static String safeFilenamePart(String value) {
        return value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9._-]+", "-");
    }
}
