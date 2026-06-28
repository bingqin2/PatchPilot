package io.patchpilot.backend.demo;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.demo.domain.DemoAcceptanceSummaryVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceShareDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceShareFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceSharePackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceSharePackageVo;
import io.patchpilot.backend.demo.domain.DemoFinalHandoffReportPackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalHandoffReportPackageVo;
import io.patchpilot.backend.demo.domain.DemoHandoffFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoHandoffReadinessVo;
import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveSummaryVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareCenterVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareChecklistVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareInstructionsVo;
import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCloseoutArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCertificateArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCertificateVo;
import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCloseoutVo;
import io.patchpilot.backend.demo.domain.DemoLaunchCommandVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidencePackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidencePackageVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceShareCenterVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceShareDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoLaunchPreflightVo;
import io.patchpilot.backend.demo.domain.DemoReadinessSnapshotArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessSnapshotTrendVo;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import io.patchpilot.backend.demo.domain.DemoScriptVo;
import io.patchpilot.backend.demo.domain.DemoSelfHostedLaunchReadinessArchiveVo;
import io.patchpilot.backend.demo.domain.DemoSelfHostedLaunchReadinessVo;
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
    private final DemoHandoffShareDeliveryReceiptService demoHandoffShareDeliveryReceiptService;
    private final DemoHandoffFinalizationService demoHandoffFinalizationService;
    private final DemoFinalHandoffReportPackageService demoFinalHandoffReportPackageService;
    private final DemoFinalHandoffReportPackageArchiveService demoFinalHandoffReportPackageArchiveService;
    private final SelfHostedLaunchReadinessService selfHostedLaunchReadinessService;
    private final SelfHostedLaunchReadinessArchiveService selfHostedLaunchReadinessArchiveService;
    private final DemoLaunchEvidencePackageService demoLaunchEvidencePackageService;
    private final DemoLaunchEvidencePackageArchiveService demoLaunchEvidencePackageArchiveService;
    private final DemoLaunchEvidenceShareCenterService demoLaunchEvidenceShareCenterService;
    private final DemoLaunchEvidenceShareDeliveryReceiptService demoLaunchEvidenceShareDeliveryReceiptService;
    private final DemoLaunchEvidenceFinalizationService demoLaunchEvidenceFinalizationService;
    private final DemoLaunchAcceptanceCloseoutService demoLaunchAcceptanceCloseoutService;
    private final DemoLaunchAcceptanceCloseoutArchiveService demoLaunchAcceptanceCloseoutArchiveService;
    private final DemoLaunchAcceptanceCertificateService demoLaunchAcceptanceCertificateService;
    private final DemoLaunchAcceptanceCertificateArchiveService demoLaunchAcceptanceCertificateArchiveService;
    private final DemoAcceptanceSummaryService demoAcceptanceSummaryService;
    private final DemoFinalAcceptanceSharePackageService demoFinalAcceptanceSharePackageService;
    private final DemoFinalAcceptanceSharePackageArchiveService demoFinalAcceptanceSharePackageArchiveService;
    private final DemoFinalAcceptanceShareDeliveryReceiptService demoFinalAcceptanceShareDeliveryReceiptService;
    private final DemoFinalAcceptanceShareFinalizationService demoFinalAcceptanceShareFinalizationService;
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

    @GetMapping("/handoff-share-instructions")
    public ApiResponse<DemoHandoffShareInstructionsVo> getHandoffShareInstructions() {
        return ApiResponse.ok(demoHandoffShareCenterService.getShareInstructions());
    }

    @GetMapping("/handoff-finalization")
    public ApiResponse<DemoHandoffFinalizationVo> getHandoffFinalization() {
        return ApiResponse.ok(demoHandoffFinalizationService.getFinalizationGate());
    }

    @GetMapping("/final-handoff-report-package")
    public ApiResponse<DemoFinalHandoffReportPackageVo> getFinalHandoffReportPackage() {
        return ApiResponse.ok(demoFinalHandoffReportPackageService.getReportPackage());
    }

    @PostMapping("/final-handoff-report-package/archives")
    public ApiResponse<DemoFinalHandoffReportPackageArchiveVo> archiveFinalHandoffReportPackage() {
        DemoFinalHandoffReportPackageArchiveVo archive =
                demoFinalHandoffReportPackageArchiveService.archiveCurrentReportPackage();
        operatorSafetyAuditService.recordSafetyAudit(new RecordOperatorSafetyAuditCommand(
                "DEMO_FINAL_HANDOFF_REPORT_PACKAGE_ARCHIVED",
                "DEMO_FINAL_HANDOFF_REPORT_PACKAGE_ARCHIVE",
                archive.id(),
                TriggerQuarantineScope.REPOSITORY,
                "patchpilot/local-demo",
                "admin-api",
                "Archived demo final handoff report package " + archive.status()
        ));
        return ApiResponse.ok(archive);
    }

    @GetMapping("/final-handoff-report-package/archives")
    public ApiResponse<List<DemoFinalHandoffReportPackageArchiveVo>> listFinalHandoffReportPackageArchives() {
        return ApiResponse.ok(demoFinalHandoffReportPackageArchiveService.listRecentArchives());
    }

    @GetMapping("/self-hosted-launch-readiness")
    public ApiResponse<DemoSelfHostedLaunchReadinessVo> getSelfHostedLaunchReadiness() {
        return ApiResponse.ok(selfHostedLaunchReadinessService.getReadinessPackage());
    }

    @GetMapping("/launch-evidence-package")
    public ApiResponse<DemoLaunchEvidencePackageVo> getLaunchEvidencePackage() {
        return ApiResponse.ok(demoLaunchEvidencePackageService.getPackage());
    }

    @GetMapping("/launch-evidence-share-center")
    public ApiResponse<DemoLaunchEvidenceShareCenterVo> getLaunchEvidenceShareCenter() {
        return ApiResponse.ok(demoLaunchEvidenceShareCenterService.getShareCenter());
    }

    @GetMapping("/launch-evidence-finalization")
    public ApiResponse<DemoLaunchEvidenceFinalizationVo> getLaunchEvidenceFinalization() {
        return ApiResponse.ok(demoLaunchEvidenceFinalizationService.getFinalizationGate());
    }

    @GetMapping("/launch-acceptance-closeout")
    public ApiResponse<DemoLaunchAcceptanceCloseoutVo> getLaunchAcceptanceCloseout() {
        return ApiResponse.ok(demoLaunchAcceptanceCloseoutService.getCloseout());
    }

    @GetMapping("/launch-acceptance-certificate")
    public ApiResponse<DemoLaunchAcceptanceCertificateVo> getLaunchAcceptanceCertificate() {
        return ApiResponse.ok(demoLaunchAcceptanceCertificateService.getCertificate());
    }

    @GetMapping("/acceptance-summary")
    public ApiResponse<DemoAcceptanceSummaryVo> getAcceptanceSummary() {
        return ApiResponse.ok(demoAcceptanceSummaryService.getSummary());
    }

    @GetMapping("/final-acceptance-share-package")
    public ApiResponse<DemoFinalAcceptanceSharePackageVo> getFinalAcceptanceSharePackage() {
        return ApiResponse.ok(demoFinalAcceptanceSharePackageService.getSharePackage());
    }

    @PostMapping("/final-acceptance-share-package/archives")
    public ApiResponse<DemoFinalAcceptanceSharePackageArchiveVo> archiveFinalAcceptanceSharePackage() {
        DemoFinalAcceptanceSharePackageArchiveVo archive =
                demoFinalAcceptanceSharePackageArchiveService.archiveCurrentSharePackage();
        operatorSafetyAuditService.recordSafetyAudit(new RecordOperatorSafetyAuditCommand(
                "DEMO_FINAL_ACCEPTANCE_SHARE_PACKAGE_ARCHIVED",
                "DEMO_FINAL_ACCEPTANCE_SHARE_PACKAGE_ARCHIVE",
                archive.id(),
                TriggerQuarantineScope.REPOSITORY,
                "patchpilot/local-demo",
                "admin-api",
                "Archived demo final acceptance share package " + archive.status()
        ));
        return ApiResponse.ok(archive);
    }

    @GetMapping("/final-acceptance-share-package/archives")
    public ApiResponse<List<DemoFinalAcceptanceSharePackageArchiveVo>> listFinalAcceptanceSharePackageArchives() {
        return ApiResponse.ok(demoFinalAcceptanceSharePackageArchiveService.listRecentArchives());
    }

    @GetMapping("/final-acceptance-share-finalization")
    public ApiResponse<DemoFinalAcceptanceShareFinalizationVo> getFinalAcceptanceShareFinalization() {
        return ApiResponse.ok(demoFinalAcceptanceShareFinalizationService.getFinalizationGate());
    }

    @GetMapping(value = "/handoff-share-center/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadHandoffShareCenterReport() {
        return markdownAttachment(
                "patchpilot-demo-handoff-share-center.md",
                demoHandoffShareCenterService.getShareCenter().markdownReport()
        );
    }

    @GetMapping(value = "/handoff-share-instructions/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadHandoffShareInstructionsReport() {
        return markdownAttachment(
                "patchpilot-demo-handoff-share-instructions.md",
                demoHandoffShareCenterService.getShareInstructions().markdownReport()
        );
    }

    @GetMapping(value = "/handoff-finalization/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadHandoffFinalizationReport() {
        return markdownAttachment(
                "patchpilot-demo-handoff-finalization.md",
                demoHandoffFinalizationService.getFinalizationGate().markdownReport()
        );
    }

    @GetMapping(value = "/final-handoff-report-package/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadFinalHandoffReportPackage() {
        return markdownAttachment(
                "patchpilot-demo-final-handoff-report-package.md",
                demoFinalHandoffReportPackageService.getReportPackage().markdownReport()
        );
    }

    @GetMapping(value = "/final-handoff-report-package/archives/{archiveId}/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadArchivedFinalHandoffReportPackage(@PathVariable String archiveId) {
        return demoFinalHandoffReportPackageArchiveService.findArchive(archiveId)
                .map(archive -> markdownAttachment(
                        "patchpilot-demo-final-handoff-report-package-" + safeFilenamePart(archive.id()) + ".md",
                        archive.report()
                ))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/self-hosted-launch-readiness/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadSelfHostedLaunchReadinessReport() {
        return markdownAttachment(
                "patchpilot-self-hosted-launch-readiness.md",
                selfHostedLaunchReadinessService.getReadinessPackage().markdownReport()
        );
    }

    @GetMapping(value = "/launch-evidence-package/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadLaunchEvidencePackageReport() {
        return markdownAttachment(
                "patchpilot-demo-launch-evidence-package.md",
                demoLaunchEvidencePackageService.getPackage().markdownReport()
        );
    }

    @GetMapping(value = "/launch-evidence-share-center/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadLaunchEvidenceShareCenterReport() {
        return markdownAttachment(
                "patchpilot-demo-launch-evidence-share-center.md",
                demoLaunchEvidenceShareCenterService.getShareCenter().markdownReport()
        );
    }

    @GetMapping(value = "/launch-evidence-finalization/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadLaunchEvidenceFinalizationReport() {
        return markdownAttachment(
                "patchpilot-demo-launch-evidence-finalization.md",
                demoLaunchEvidenceFinalizationService.getFinalizationGate().markdownReport()
        );
    }

    @GetMapping(value = "/launch-acceptance-closeout/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadLaunchAcceptanceCloseoutReport() {
        return markdownAttachment(
                "patchpilot-launch-acceptance-closeout.md",
                demoLaunchAcceptanceCloseoutService.getCloseout().markdownReport()
        );
    }

    @GetMapping(value = "/launch-acceptance-certificate/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadLaunchAcceptanceCertificateReport() {
        return markdownAttachment(
                "patchpilot-launch-acceptance-certificate.md",
                demoLaunchAcceptanceCertificateService.getCertificate().markdownReport()
        );
    }

    @GetMapping(value = "/acceptance-summary/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadAcceptanceSummaryReport() {
        return markdownAttachment(
                "patchpilot-final-demo-acceptance-summary.md",
                demoAcceptanceSummaryService.getSummary().markdownReport()
        );
    }

    @GetMapping(value = "/final-acceptance-share-package/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadFinalAcceptanceSharePackageReport() {
        return markdownAttachment(
                "patchpilot-final-demo-acceptance-share-package.md",
                demoFinalAcceptanceSharePackageService.getSharePackage().markdownReport()
        );
    }

    @GetMapping(value = "/final-acceptance-share-package/archives/{archiveId}/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadArchivedFinalAcceptanceSharePackageReport(@PathVariable String archiveId) {
        return demoFinalAcceptanceSharePackageArchiveService.findArchive(archiveId)
                .map(archive -> markdownAttachment(
                        "patchpilot-final-demo-acceptance-share-package-" + safeFilenamePart(archive.id()) + ".md",
                        archive.report()
                ))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/final-acceptance-share-finalization/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadFinalAcceptanceShareFinalizationReport() {
        return markdownAttachment(
                "patchpilot-final-demo-acceptance-share-finalization.md",
                demoFinalAcceptanceShareFinalizationService.getFinalizationGate().markdownReport()
        );
    }

    @PostMapping("/launch-acceptance-certificate/archives")
    public ApiResponse<DemoLaunchAcceptanceCertificateArchiveVo> archiveLaunchAcceptanceCertificate() {
        DemoLaunchAcceptanceCertificateArchiveVo archive =
                demoLaunchAcceptanceCertificateArchiveService.archiveCurrentCertificate();
        operatorSafetyAuditService.recordSafetyAudit(new RecordOperatorSafetyAuditCommand(
                "DEMO_LAUNCH_ACCEPTANCE_CERTIFICATE_ARCHIVED",
                "DEMO_LAUNCH_ACCEPTANCE_CERTIFICATE_ARCHIVE",
                archive.id(),
                TriggerQuarantineScope.REPOSITORY,
                "patchpilot/local-demo",
                "admin-api",
                "Archived demo launch acceptance certificate " + archive.status()
        ));
        return ApiResponse.ok(archive);
    }

    @GetMapping("/launch-acceptance-certificate/archives")
    public ApiResponse<List<DemoLaunchAcceptanceCertificateArchiveVo>> listLaunchAcceptanceCertificateArchives() {
        return ApiResponse.ok(demoLaunchAcceptanceCertificateArchiveService.listRecentArchives());
    }

    @GetMapping(value = "/launch-acceptance-certificate/archives/{archiveId}/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadArchivedLaunchAcceptanceCertificateReport(@PathVariable String archiveId) {
        return demoLaunchAcceptanceCertificateArchiveService.findArchive(archiveId)
                .map(archive -> markdownAttachment(
                        "patchpilot-launch-acceptance-certificate-" + safeFilenamePart(archive.id()) + ".md",
                        archive.report()
                ))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/launch-evidence-package/archives")
    public ApiResponse<DemoLaunchEvidencePackageArchiveVo> archiveLaunchEvidencePackage() {
        DemoLaunchEvidencePackageArchiveVo archive = demoLaunchEvidencePackageArchiveService.archiveCurrentPackage();
        operatorSafetyAuditService.recordSafetyAudit(new RecordOperatorSafetyAuditCommand(
                "DEMO_LAUNCH_EVIDENCE_PACKAGE_ARCHIVED",
                "DEMO_LAUNCH_EVIDENCE_PACKAGE_ARCHIVE",
                archive.id(),
                TriggerQuarantineScope.REPOSITORY,
                "patchpilot/local-demo",
                "admin-api",
                "Archived demo launch evidence package " + archive.status()
        ));
        return ApiResponse.ok(archive);
    }

    @GetMapping("/launch-evidence-package/archives")
    public ApiResponse<List<DemoLaunchEvidencePackageArchiveVo>> listLaunchEvidencePackageArchives() {
        return ApiResponse.ok(demoLaunchEvidencePackageArchiveService.listRecentArchives());
    }

    @GetMapping(value = "/launch-evidence-package/archives/{archiveId}/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadArchivedLaunchEvidencePackageReport(@PathVariable String archiveId) {
        return demoLaunchEvidencePackageArchiveService.findArchive(archiveId)
                .map(archive -> markdownAttachment(
                        "patchpilot-demo-launch-evidence-package-" + safeFilenamePart(archive.id()) + ".md",
                        archive.report()
                ))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/launch-acceptance-closeout/archives")
    public ApiResponse<DemoLaunchAcceptanceCloseoutArchiveVo> archiveLaunchAcceptanceCloseout() {
        DemoLaunchAcceptanceCloseoutArchiveVo archive =
                demoLaunchAcceptanceCloseoutArchiveService.archiveCurrentCloseout();
        operatorSafetyAuditService.recordSafetyAudit(new RecordOperatorSafetyAuditCommand(
                "DEMO_LAUNCH_ACCEPTANCE_CLOSEOUT_ARCHIVED",
                "DEMO_LAUNCH_ACCEPTANCE_CLOSEOUT_ARCHIVE",
                archive.id(),
                TriggerQuarantineScope.REPOSITORY,
                "patchpilot/local-demo",
                "admin-api",
                "Archived demo launch acceptance closeout " + archive.status()
        ));
        return ApiResponse.ok(archive);
    }

    @GetMapping("/launch-acceptance-closeout/archives")
    public ApiResponse<List<DemoLaunchAcceptanceCloseoutArchiveVo>> listLaunchAcceptanceCloseoutArchives() {
        return ApiResponse.ok(demoLaunchAcceptanceCloseoutArchiveService.listRecentArchives());
    }

    @GetMapping(value = "/launch-acceptance-closeout/archives/{archiveId}/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadArchivedLaunchAcceptanceCloseoutReport(@PathVariable String archiveId) {
        return demoLaunchAcceptanceCloseoutArchiveService.findArchive(archiveId)
                .map(archive -> markdownAttachment(
                        "patchpilot-launch-acceptance-closeout-" + safeFilenamePart(archive.id()) + ".md",
                        archive.report()
                ))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/launch-evidence-share-delivery-receipts")
    public ResponseEntity<ApiResponse<DemoLaunchEvidenceShareDeliveryReceiptVo>> recordLaunchEvidenceDeliveryReceipt(
            @RequestBody DemoLaunchEvidenceShareDeliveryReceiptRequestDto request
    ) {
        try {
            DemoLaunchEvidenceShareDeliveryReceiptVo receipt =
                    demoLaunchEvidenceShareDeliveryReceiptService.recordDeliveryReceipt(request);
            operatorSafetyAuditService.recordSafetyAudit(new RecordOperatorSafetyAuditCommand(
                    "DEMO_LAUNCH_EVIDENCE_DELIVERY_RECEIPT_RECORDED",
                    "DEMO_LAUNCH_EVIDENCE_DELIVERY_RECEIPT",
                    receipt.id(),
                    TriggerQuarantineScope.REPOSITORY,
                    "patchpilot/local-demo",
                    receipt.operator(),
                    "Recorded demo launch evidence delivery receipt for " + receipt.launchEvidenceArchiveId()
            ));
            return ResponseEntity.ok(ApiResponse.ok(receipt));
        } catch (IllegalArgumentException | IllegalStateException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(exception.getMessage()));
        }
    }

    @GetMapping("/launch-evidence-share-delivery-receipts")
    public ApiResponse<List<DemoLaunchEvidenceShareDeliveryReceiptVo>> listLaunchEvidenceDeliveryReceipts() {
        return ApiResponse.ok(demoLaunchEvidenceShareDeliveryReceiptService.listRecentReceipts());
    }

    @GetMapping(value = "/launch-evidence-share-delivery-receipts/{receiptId}/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadLaunchEvidenceDeliveryReceiptReport(@PathVariable String receiptId) {
        return demoLaunchEvidenceShareDeliveryReceiptService.findReceipt(receiptId)
                .map(receipt -> markdownAttachment(
                        "patchpilot-demo-launch-evidence-delivery-receipt-" + safeFilenamePart(receipt.id()) + ".md",
                        receipt.markdownReport()
                ))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/final-acceptance-share-delivery-receipts")
    public ResponseEntity<ApiResponse<DemoFinalAcceptanceShareDeliveryReceiptVo>> recordFinalAcceptanceShareDeliveryReceipt(
            @RequestBody DemoFinalAcceptanceShareDeliveryReceiptRequestDto request
    ) {
        try {
            DemoFinalAcceptanceShareDeliveryReceiptVo receipt =
                    demoFinalAcceptanceShareDeliveryReceiptService.recordDeliveryReceipt(request);
            operatorSafetyAuditService.recordSafetyAudit(new RecordOperatorSafetyAuditCommand(
                    "DEMO_FINAL_ACCEPTANCE_SHARE_DELIVERY_RECEIPT_RECORDED",
                    "DEMO_FINAL_ACCEPTANCE_SHARE_DELIVERY_RECEIPT",
                    receipt.id(),
                    TriggerQuarantineScope.REPOSITORY,
                    "patchpilot/local-demo",
                    receipt.operator(),
                    "Recorded final acceptance share delivery receipt for "
                            + receipt.finalAcceptanceSharePackageArchiveId()
            ));
            return ResponseEntity.ok(ApiResponse.ok(receipt));
        } catch (IllegalArgumentException | IllegalStateException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(exception.getMessage()));
        }
    }

    @GetMapping("/final-acceptance-share-delivery-receipts")
    public ApiResponse<List<DemoFinalAcceptanceShareDeliveryReceiptVo>> listFinalAcceptanceShareDeliveryReceipts() {
        return ApiResponse.ok(demoFinalAcceptanceShareDeliveryReceiptService.listRecentReceipts());
    }

    @GetMapping(value = "/final-acceptance-share-delivery-receipts/{receiptId}/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadFinalAcceptanceShareDeliveryReceiptReport(@PathVariable String receiptId) {
        return demoFinalAcceptanceShareDeliveryReceiptService.findReceipt(receiptId)
                .map(receipt -> markdownAttachment(
                        "patchpilot-final-demo-acceptance-share-delivery-receipt-"
                                + safeFilenamePart(receipt.id()) + ".md",
                        receipt.markdownReport()
                ))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/self-hosted-launch-readiness/archives")
    public ApiResponse<DemoSelfHostedLaunchReadinessArchiveVo> archiveSelfHostedLaunchReadiness() {
        DemoSelfHostedLaunchReadinessArchiveVo archive =
                selfHostedLaunchReadinessArchiveService.archiveCurrentReadinessPackage();
        operatorSafetyAuditService.recordSafetyAudit(new RecordOperatorSafetyAuditCommand(
                "DEMO_SELF_HOSTED_LAUNCH_READINESS_ARCHIVED",
                "DEMO_SELF_HOSTED_LAUNCH_READINESS_ARCHIVE",
                archive.id(),
                TriggerQuarantineScope.REPOSITORY,
                "patchpilot/local-demo",
                "admin-api",
                "Archived self-hosted launch readiness " + archive.status()
        ));
        return ApiResponse.ok(archive);
    }

    @GetMapping("/self-hosted-launch-readiness/archives")
    public ApiResponse<List<DemoSelfHostedLaunchReadinessArchiveVo>> listSelfHostedLaunchReadinessArchives() {
        return ApiResponse.ok(selfHostedLaunchReadinessArchiveService.listRecentArchives());
    }

    @GetMapping(value = "/self-hosted-launch-readiness/archives/{archiveId}/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadArchivedSelfHostedLaunchReadinessReport(@PathVariable String archiveId) {
        return selfHostedLaunchReadinessArchiveService.findArchive(archiveId)
                .map(archive -> markdownAttachment(
                        "patchpilot-self-hosted-launch-readiness-" + safeFilenamePart(archive.id()) + ".md",
                        archive.report()
                ))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/handoff-share-delivery-receipts")
    public ResponseEntity<ApiResponse<DemoHandoffShareDeliveryReceiptVo>> recordHandoffShareDeliveryReceipt(
            @RequestBody DemoHandoffShareDeliveryReceiptRequestDto request
    ) {
        try {
            DemoHandoffShareDeliveryReceiptVo receipt =
                    demoHandoffShareDeliveryReceiptService.recordDeliveryReceipt(request);
            operatorSafetyAuditService.recordSafetyAudit(new RecordOperatorSafetyAuditCommand(
                    "DEMO_HANDOFF_SHARE_DELIVERY_RECEIPT_RECORDED",
                    "DEMO_HANDOFF_SHARE_DELIVERY_RECEIPT",
                    receipt.id(),
                    TriggerQuarantineScope.REPOSITORY,
                    "patchpilot/local-demo",
                    receipt.operator(),
                    "Recorded demo handoff share delivery receipt for " + receipt.handoffArchiveId()
            ));
            return ResponseEntity.ok(ApiResponse.ok(receipt));
        } catch (IllegalArgumentException | IllegalStateException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(exception.getMessage()));
        }
    }

    @GetMapping("/handoff-share-delivery-receipts")
    public ApiResponse<List<DemoHandoffShareDeliveryReceiptVo>> listHandoffShareDeliveryReceipts() {
        return ApiResponse.ok(demoHandoffShareDeliveryReceiptService.listRecentReceipts());
    }

    @GetMapping(value = "/handoff-share-delivery-receipts/{receiptId}/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadHandoffShareDeliveryReceiptReport(@PathVariable String receiptId) {
        return demoHandoffShareDeliveryReceiptService.findReceipt(receiptId)
                .map(receipt -> markdownAttachment(
                        "patchpilot-demo-handoff-share-delivery-receipt-" + safeFilenamePart(receipt.id()) + ".md",
                        receipt.markdownReport()
                ))
                .orElseGet(() -> ResponseEntity.notFound().build());
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
