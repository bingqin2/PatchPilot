package io.patchpilot.backend.demo;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoHandoffReadinessVo;
import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveSummaryVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareCenterVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareChecklistVo;
import io.patchpilot.backend.demo.domain.DemoLaunchCommandVo;
import io.patchpilot.backend.demo.domain.DemoLaunchPreflightVo;
import io.patchpilot.backend.demo.domain.DemoReadinessSnapshotArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessSnapshotTrendVo;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import io.patchpilot.backend.demo.domain.DemoScriptVo;
import io.patchpilot.backend.demo.domain.DemoSessionArchiveVo;
import io.patchpilot.backend.demo.domain.DemoSessionSnapshotVo;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistVo;
import io.patchpilot.backend.safety.domain.RecordOperatorSafetyAuditCommand;
import io.patchpilot.backend.safety.domain.TriggerQuarantineScope;
import io.patchpilot.backend.safety.service.OperatorSafetyAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    private final DemoHandoffPackageArchiveService demoHandoffPackageArchiveService;
    private final DemoHandoffShareChecklistService demoHandoffShareChecklistService;
    private final DemoHandoffShareCenterService demoHandoffShareCenterService;
    private final DemoReadinessSnapshotArchiveService demoReadinessSnapshotArchiveService;
    private final DemoReadinessSnapshotTrendService demoReadinessSnapshotTrendService;
    private final DemoLaunchPreflightService demoLaunchPreflightService;
    private final DemoLaunchCommandService demoLaunchCommandService;
    private final OperatorSafetyAuditService operatorSafetyAuditService;

    @GetMapping("/readiness")
    public ApiResponse<DemoReadinessVo> getReadiness() {
        return ApiResponse.ok(demoReadinessService.getReadiness());
    }

    @GetMapping("/smoke-checklist")
    public ApiResponse<DemoSmokeChecklistVo> getSmokeChecklist() {
        return ApiResponse.ok(demoSmokeChecklistService.getSmokeChecklist());
    }

    @PostMapping("/launch-preflight")
    public ResponseEntity<ApiResponse<DemoLaunchPreflightVo>> preflightLaunch(
            @RequestBody DemoLaunchPreflightRequestDto request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(demoLaunchPreflightService.preflight(request)));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(exception.getMessage()));
        }
    }

    @PostMapping("/launch-command")
    public ResponseEntity<ApiResponse<DemoLaunchCommandVo>> composeLaunchCommand(
            @RequestBody DemoLaunchCommandRequestDto request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(demoLaunchCommandService.compose(request)));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(exception.getMessage()));
        }
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

    @PostMapping("/session-report")
    public ApiResponse<String> getSessionReport(@RequestBody(required = false) DemoSessionReportRequestDto request) {
        return ApiResponse.ok(demoSessionReportService.getSessionReport(normalizeReportRequest(request)));
    }

    @GetMapping(value = "/session-report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadSessionReport() {
        return markdownAttachment(
                "patchpilot-demo-session-report.md",
                demoSessionReportService.getSessionReport()
        );
    }

    @PostMapping(value = "/session-report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadSessionReport(@RequestBody(required = false) DemoSessionReportRequestDto request) {
        return markdownAttachment(
                "patchpilot-demo-session-report.md",
                demoSessionReportService.getSessionReport(normalizeReportRequest(request))
        );
    }

    @PostMapping("/handoff-package")
    public ApiResponse<String> getHandoffPackage(@RequestBody(required = false) DemoSessionReportRequestDto request) {
        return ApiResponse.ok(demoSessionReportService.getHandoffPackage(normalizeReportRequest(request)));
    }

    @GetMapping("/handoff-readiness")
    public ApiResponse<DemoHandoffReadinessVo> getHandoffReadiness() {
        return ApiResponse.ok(demoSessionReportService.getHandoffReadiness(new DemoSessionReportRequestDto(List.of())));
    }

    @PostMapping("/handoff-readiness")
    public ApiResponse<DemoHandoffReadinessVo> getHandoffReadiness(
            @RequestBody(required = false) DemoSessionReportRequestDto request
    ) {
        return ApiResponse.ok(demoSessionReportService.getHandoffReadiness(normalizeReportRequest(request)));
    }

    @PostMapping(value = "/handoff-package/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadHandoffPackage(@RequestBody(required = false) DemoSessionReportRequestDto request) {
        return markdownAttachment(
                "patchpilot-demo-handoff-package.md",
                demoSessionReportService.getHandoffPackage(normalizeReportRequest(request))
        );
    }

    @PostMapping("/session-archives")
    public ApiResponse<DemoSessionArchiveVo> archiveCurrentSession(
            @RequestBody(required = false) DemoSessionReportRequestDto request
    ) {
        DemoSessionArchiveVo archive = demoSessionArchiveService.archiveCurrentSession(normalizeReportRequest(request));
        operatorSafetyAuditService.recordSafetyAudit(new RecordOperatorSafetyAuditCommand(
                "DEMO_SESSION_ARCHIVED",
                "DEMO_SESSION_ARCHIVE",
                archive.id(),
                TriggerQuarantineScope.REPOSITORY,
                "patchpilot/local-demo",
                "admin-api",
                "Archived demo session " + archive.sessionId()
        ));
        return ApiResponse.ok(archive);
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

    @PostMapping("/handoff-package-archives")
    public ApiResponse<DemoHandoffPackageArchiveVo> archiveCurrentHandoffPackage(
            @RequestBody(required = false) DemoSessionReportRequestDto request
    ) {
        DemoHandoffPackageArchiveVo archive = demoHandoffPackageArchiveService.archiveCurrentHandoffPackage(
                normalizeReportRequest(request)
        );
        operatorSafetyAuditService.recordSafetyAudit(new RecordOperatorSafetyAuditCommand(
                "DEMO_HANDOFF_PACKAGE_ARCHIVED",
                "DEMO_HANDOFF_PACKAGE_ARCHIVE",
                archive.id(),
                TriggerQuarantineScope.REPOSITORY,
                "patchpilot/local-demo",
                "admin-api",
                "Archived demo handoff package " + archive.sessionId()
        ));
        return ApiResponse.ok(archive);
    }

    @GetMapping("/handoff-package-archives")
    public ApiResponse<List<DemoHandoffPackageArchiveVo>> listHandoffPackageArchives() {
        return ApiResponse.ok(demoHandoffPackageArchiveService.listRecentArchives());
    }

    @GetMapping("/handoff-package-archives/summary")
    public ApiResponse<DemoHandoffPackageArchiveSummaryVo> getHandoffPackageArchiveSummary() {
        return ApiResponse.ok(demoHandoffPackageArchiveService.getArchiveSummary());
    }

    @GetMapping("/handoff-share-checklist")
    public ApiResponse<DemoHandoffShareChecklistVo> getHandoffShareChecklist() {
        return ApiResponse.ok(demoHandoffShareChecklistService.getShareChecklist());
    }

    @GetMapping("/handoff-share-center")
    public ApiResponse<DemoHandoffShareCenterVo> getHandoffShareCenter() {
        return ApiResponse.ok(demoHandoffShareCenterService.getShareCenter());
    }

    @GetMapping(value = "/handoff-share-center/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadHandoffShareCenterReport() {
        return markdownAttachment(
                "patchpilot-demo-handoff-share-center.md",
                demoHandoffShareCenterService.getShareCenter().markdownReport()
        );
    }

    @GetMapping(value = "/handoff-share-checklist/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadHandoffShareChecklistReport() {
        return markdownAttachment(
                "patchpilot-demo-handoff-share-checklist.md",
                demoHandoffShareChecklistService.getShareChecklist().markdownReport()
        );
    }

    @GetMapping(value = "/handoff-package-archives/summary-report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadHandoffPackageArchiveSummaryReport() {
        return markdownAttachment(
                "patchpilot-demo-handoff-package-archive-summary.md",
                demoHandoffPackageArchiveService.getArchiveSummary().markdownReport()
        );
    }

    @GetMapping(value = "/handoff-package-archives/{archiveId}/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadArchivedHandoffPackage(@PathVariable String archiveId) {
        return demoHandoffPackageArchiveService.findArchive(archiveId)
                .map(archive -> markdownAttachment(
                        "patchpilot-demo-handoff-package-" + safeFilenamePart(archive.id()) + ".md",
                        archive.report()
                ))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/readiness-snapshots")
    public ApiResponse<DemoReadinessSnapshotArchiveVo> archiveCurrentReadinessSnapshot() {
        DemoReadinessSnapshotArchiveVo archive = demoReadinessSnapshotArchiveService.archiveCurrentReadiness();
        operatorSafetyAuditService.recordSafetyAudit(new RecordOperatorSafetyAuditCommand(
                "DEMO_READINESS_SNAPSHOT_ARCHIVED",
                "DEMO_READINESS_SNAPSHOT_ARCHIVE",
                archive.id(),
                TriggerQuarantineScope.REPOSITORY,
                "patchpilot/local-demo",
                "admin-api",
                "Archived demo readiness snapshot " + archive.status()
        ));
        return ApiResponse.ok(archive);
    }

    @GetMapping("/readiness-snapshots")
    public ApiResponse<List<DemoReadinessSnapshotArchiveVo>> listReadinessSnapshotArchives() {
        return ApiResponse.ok(demoReadinessSnapshotArchiveService.listRecentArchives());
    }

    @GetMapping("/readiness-snapshots/summary")
    public ApiResponse<DemoReadinessSnapshotTrendVo> getReadinessSnapshotTrendSummary() {
        return ApiResponse.ok(demoReadinessSnapshotTrendService.getTrendSummary());
    }

    @GetMapping(value = "/readiness-snapshots/{archiveId}/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadReadinessSnapshotReport(@PathVariable String archiveId) {
        return demoReadinessSnapshotArchiveService.findArchive(archiveId)
                .map(archive -> markdownAttachment(
                        "patchpilot-demo-readiness-" + safeFilenamePart(archive.id()) + ".md",
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

    private static DemoSessionReportRequestDto normalizeReportRequest(DemoSessionReportRequestDto request) {
        return request == null ? new DemoSessionReportRequestDto(List.of()) : request;
    }

    private static String safeFilenamePart(String value) {
        return value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9._-]+", "-");
    }
}
