package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoReadinessCheckVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import io.patchpilot.backend.demo.domain.DemoAcceptanceSummaryVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleSummaryVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoEvaluationRunReadinessEvidenceVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionCloseoutArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionCloseoutVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceSharePackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceShareDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceShareFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceSharePackageVo;
import io.patchpilot.backend.demo.domain.DemoFinalHandoffReportPackageArchiveEvidenceVo;
import io.patchpilot.backend.demo.domain.DemoFinalHandoffReportPackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalHandoffReportPackageVo;
import io.patchpilot.backend.demo.domain.DemoAdapterFixtureEvidenceVo;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistStatus;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistStepVo;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistVo;
import io.patchpilot.backend.demo.domain.DemoLaunchCommandVo;
import io.patchpilot.backend.demo.domain.DemoLaunchPreflightVo;
import io.patchpilot.backend.demo.domain.DemoHandoffReadinessCheckVo;
import io.patchpilot.backend.demo.domain.DemoHandoffReadinessVo;
import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveSummaryVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareChecklistItemVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareChecklistVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareCenterVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareInstructionsVo;
import io.patchpilot.backend.demo.domain.DemoHandoffFinalizationCheckVo;
import io.patchpilot.backend.demo.domain.DemoHandoffFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCloseoutCheckVo;
import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCertificateArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCertificateVo;
import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCloseoutArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCloseoutEvidenceVo;
import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCloseoutVo;
import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCertificateEvidenceVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidencePackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceFinalizationCheckVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceShareCenterVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceShareDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidencePackageVo;
import io.patchpilot.backend.demo.domain.DemoTaskEvidenceAcceptanceCertificateEvidenceVo;
import io.patchpilot.backend.task.domain.vo.TriggerEvaluationDecisionVo;
import io.patchpilot.backend.task.domain.vo.TriggerEvaluationResultVo;
import io.patchpilot.backend.demo.domain.DemoScriptStepVo;
import io.patchpilot.backend.demo.domain.DemoScriptVo;
import io.patchpilot.backend.demo.domain.DemoSelfHostedLaunchCheckVo;
import io.patchpilot.backend.demo.domain.DemoSelfHostedLaunchReadinessArchiveVo;
import io.patchpilot.backend.demo.domain.DemoSelfHostedLaunchReadinessVo;
import io.patchpilot.backend.demo.domain.DemoReadinessSnapshotArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessSnapshotTrendStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessSnapshotTrendVo;
import io.patchpilot.backend.demo.domain.DemoSessionArchiveVo;
import io.patchpilot.backend.demo.domain.DemoSessionSnapshotVo;
import io.patchpilot.backend.safety.domain.RecordOperatorSafetyAuditCommand;
import io.patchpilot.backend.safety.domain.TriggerQuarantineScope;
import io.patchpilot.backend.safety.service.OperatorSafetyAuditService;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueSummaryVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DemoReadinessController.class)
class DemoReadinessControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DemoReadinessService demoReadinessService;

    @MockitoBean
    private DemoSmokeChecklistService demoSmokeChecklistService;

    @MockitoBean
    private DemoEvidenceBundleService demoEvidenceBundleService;

    @MockitoBean
    private DemoRunbookService demoRunbookService;

    @MockitoBean
    private DemoScriptService demoScriptService;

    @MockitoBean
    private DemoSessionSnapshotService demoSessionSnapshotService;

    @MockitoBean
    private DemoSessionReportService demoSessionReportService;

    @MockitoBean
    private DemoSessionArchiveService demoSessionArchiveService;

    @MockitoBean
    private DemoHandoffPackageArchiveService demoHandoffPackageArchiveService;

    @MockitoBean
    private DemoHandoffShareChecklistService demoHandoffShareChecklistService;

    @MockitoBean
    private DemoHandoffShareCenterService demoHandoffShareCenterService;

    @MockitoBean
    private DemoHandoffShareDeliveryReceiptService demoHandoffShareDeliveryReceiptService;

    @MockitoBean
    private DemoHandoffFinalizationService demoHandoffFinalizationService;

    @MockitoBean
    private DemoFinalHandoffReportPackageService demoFinalHandoffReportPackageService;

    @MockitoBean
    private DemoFinalHandoffReportPackageArchiveService demoFinalHandoffReportPackageArchiveService;

    @MockitoBean
    private SelfHostedLaunchReadinessService selfHostedLaunchReadinessService;

    @MockitoBean
    private SelfHostedLaunchReadinessArchiveService selfHostedLaunchReadinessArchiveService;

    @MockitoBean
    private DemoLaunchEvidencePackageService demoLaunchEvidencePackageService;

    @MockitoBean
    private DemoLaunchEvidencePackageArchiveService demoLaunchEvidencePackageArchiveService;

    @MockitoBean
    private DemoLaunchEvidenceShareCenterService demoLaunchEvidenceShareCenterService;

    @MockitoBean
    private DemoLaunchEvidenceShareDeliveryReceiptService demoLaunchEvidenceShareDeliveryReceiptService;

    @MockitoBean
    private DemoLaunchEvidenceFinalizationService demoLaunchEvidenceFinalizationService;

    @MockitoBean
    private DemoLaunchAcceptanceCloseoutService demoLaunchAcceptanceCloseoutService;

    @MockitoBean
    private DemoLaunchAcceptanceCloseoutArchiveService demoLaunchAcceptanceCloseoutArchiveService;

    @MockitoBean
    private DemoLaunchAcceptanceCertificateService demoLaunchAcceptanceCertificateService;

    @MockitoBean
    private DemoLaunchAcceptanceCertificateArchiveService demoLaunchAcceptanceCertificateArchiveService;

    @MockitoBean
    private DemoAcceptanceSummaryService demoAcceptanceSummaryService;

    @MockitoBean
    private DemoFinalAcceptanceSharePackageService demoFinalAcceptanceSharePackageService;

    @MockitoBean
    private DemoFinalAcceptanceSharePackageArchiveService demoFinalAcceptanceSharePackageArchiveService;

    @MockitoBean
    private DemoFinalAcceptanceShareDeliveryReceiptService demoFinalAcceptanceShareDeliveryReceiptService;

    @MockitoBean
    private DemoFinalAcceptanceShareFinalizationService demoFinalAcceptanceShareFinalizationService;

    @MockitoBean
    private DemoFinalAcceptanceCompletionArchiveService demoFinalAcceptanceCompletionArchiveService;

    @MockitoBean
    private DemoFinalAcceptanceCompletionEvidenceBundleService demoFinalAcceptanceCompletionEvidenceBundleService;

    @MockitoBean
    private DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptService demoFinalAcceptanceCompletionEvidenceDeliveryReceiptService;

    @MockitoBean
    private DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationService demoFinalAcceptanceCompletionEvidenceDeliveryFinalizationService;

    @MockitoBean
    private DemoFinalAcceptanceCompletionCloseoutService demoFinalAcceptanceCompletionCloseoutService;

    @MockitoBean
    private DemoFinalAcceptanceCompletionCloseoutArchiveService demoFinalAcceptanceCompletionCloseoutArchiveService;

    @MockitoBean
    private DemoReadinessSnapshotArchiveService demoReadinessSnapshotArchiveService;

    @MockitoBean
    private DemoReadinessSnapshotTrendService demoReadinessSnapshotTrendService;

    @MockitoBean
    private DemoLaunchPreflightService demoLaunchPreflightService;

    @MockitoBean
    private DemoLaunchCommandService demoLaunchCommandService;

    @MockitoBean
    private OperatorSafetyAuditService operatorSafetyAuditService;

    @Test
    void should_return_self_hosted_launch_readiness_package() throws Exception {
        when(selfHostedLaunchReadinessService.getReadinessPackage()).thenReturn(selfHostedLaunchReadiness());

        mockMvc.perform(get("/api/demo/self-hosted-launch-readiness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.readyToLaunch").value(true))
                .andExpect(jsonPath("$.data.summary").value("Self-hosted PatchPilot is ready for a controlled issue-to-PR launch."))
                .andExpect(jsonPath("$.data.checks.length()").value(2))
                .andExpect(jsonPath("$.data.checks[0].name").value("Demo readiness"))
                .andExpect(jsonPath("$.data.checks[0].status").value("READY"))
                .andExpect(jsonPath("$.data.checks[1].name").value("Evidence bundle"))
                .andExpect(jsonPath("$.data.nextActions[0]").value("Post the tested /agent fix comment, watch the task reach COMPLETED, then use the generated Pull Request for review."))
                .andExpect(jsonPath("$.data.markdownReport").value(containsString("# PatchPilot Self-Hosted Launch Readiness")));
    }

    @Test
    void should_download_self_hosted_launch_readiness_report() throws Exception {
        when(selfHostedLaunchReadinessService.getReadinessPackage()).thenReturn(selfHostedLaunchReadiness());

        mockMvc.perform(get("/api/demo/self-hosted-launch-readiness/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-self-hosted-launch-readiness.md")))
                .andExpect(content().contentType("text/markdown;charset=UTF-8"))
                .andExpect(content().string(containsString("# PatchPilot Self-Hosted Launch Readiness")))
                .andExpect(content().string(containsString("Ready to launch: `true`")));
    }

    @Test
    void should_return_demo_launch_evidence_package() throws Exception {
        when(demoLaunchEvidencePackageService.getPackage()).thenReturn(launchEvidencePackage());

        mockMvc.perform(get("/api/demo/launch-evidence-package"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.readyToShare").value(true))
                .andExpect(jsonPath("$.data.summary").value("PatchPilot launch evidence package is ready to share."))
                .andExpect(jsonPath("$.data.sessionId").value("demo-session-20260624T003000Z"))
                .andExpect(jsonPath("$.data.launchReadinessStatus").value("READY"))
                .andExpect(jsonPath("$.data.evidenceBundleStatus").value("READY"))
                .andExpect(jsonPath("$.data.handoffFinalizationStatus").value("READY"))
                .andExpect(jsonPath("$.data.latestTaskId").value("task-1"))
                .andExpect(jsonPath("$.data.latestPullRequestUrl").value("https://github.com/bingqin2/PatchPilot/pull/42"))
                .andExpect(jsonPath("$.data.latestWebhookDeliveryId").value("delivery-1"))
                .andExpect(jsonPath("$.data.evaluationRunId").value("evaluation-run-2"))
                .andExpect(jsonPath("$.data.evaluationCoverage[0]").value("java"))
                .andExpect(jsonPath("$.data.preLaunchChecks[0].name").value("Demo readiness"))
                .andExpect(jsonPath("$.data.liveRunProof[0]").value("Recent task task-1 reached COMPLETED."))
                .andExpect(jsonPath("$.data.postDemoProof[1]").value("Latest delivery receipt delivery-receipt-1 is fresh."))
                .andExpect(jsonPath("$.data.healthContract[0]")
                        .value("GET /api/demo/launch-evidence-package is read-only: it does not create tasks, call the model, run tests, archive records, mutate Git, send messages, or write to GitHub."))
                .andExpect(jsonPath("$.data.markdownReport").value(containsString("# PatchPilot Demo Launch Evidence Package")));
    }

    @Test
    void should_download_demo_launch_evidence_package_report() throws Exception {
        when(demoLaunchEvidencePackageService.getPackage()).thenReturn(launchEvidencePackage());

        mockMvc.perform(get("/api/demo/launch-evidence-package/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-demo-launch-evidence-package.md")))
                .andExpect(content().contentType("text/markdown;charset=UTF-8"))
                .andExpect(content().string(containsString("# PatchPilot Demo Launch Evidence Package")))
                .andExpect(content().string(containsString("Recent Pull Request https://github.com/bingqin2/PatchPilot/pull/42 is available.")));
    }

    @Test
    void should_archive_demo_launch_evidence_package() throws Exception {
        when(demoLaunchEvidencePackageArchiveService.archiveCurrentPackage()).thenReturn(launchEvidencePackageArchive());

        mockMvc.perform(post("/api/demo/launch-evidence-package/archives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("launch-evidence-archive-1"))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.readyToShare").value(true))
                .andExpect(jsonPath("$.data.sessionId").value("demo-session-20260624T003000Z"))
                .andExpect(jsonPath("$.data.latestPullRequestUrl").value("https://github.com/bingqin2/PatchPilot/pull/42"))
                .andExpect(jsonPath("$.data.report").value(containsString("# PatchPilot Demo Launch Evidence Package")));

        verify(operatorSafetyAuditService).recordSafetyAudit(argThat(this::isLaunchEvidencePackageArchiveAudit));
    }

    private boolean isLaunchEvidencePackageArchiveAudit(RecordOperatorSafetyAuditCommand command) {
        return command != null
                && "DEMO_LAUNCH_EVIDENCE_PACKAGE_ARCHIVED".equals(command.action())
                && "DEMO_LAUNCH_EVIDENCE_PACKAGE_ARCHIVE".equals(command.resourceType())
                && "launch-evidence-archive-1".equals(command.resourceId())
                && command.scope() == TriggerQuarantineScope.REPOSITORY
                && "patchpilot/local-demo".equals(command.scopeKey())
                && "admin-api".equals(command.operator())
                && "Archived demo launch evidence package READY".equals(command.reason());
    }

    @Test
    void should_return_recent_demo_launch_evidence_package_archives() throws Exception {
        when(demoLaunchEvidencePackageArchiveService.listRecentArchives())
                .thenReturn(List.of(launchEvidencePackageArchive()));

        mockMvc.perform(get("/api/demo/launch-evidence-package/archives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("launch-evidence-archive-1"))
                .andExpect(jsonPath("$.data[0].status").value("READY"))
                .andExpect(jsonPath("$.data[0].summary").value("PatchPilot launch evidence package is ready to share."));
    }

    @Test
    void should_download_archived_demo_launch_evidence_package_report_as_markdown_attachment() throws Exception {
        when(demoLaunchEvidencePackageArchiveService.findArchive("launch-evidence-archive-1"))
                .thenReturn(Optional.of(launchEvidencePackageArchive()));

        mockMvc.perform(get("/api/demo/launch-evidence-package/archives/launch-evidence-archive-1/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment;")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-demo-launch-evidence-package-launch-evidence-archive-1.md")))
                .andExpect(content().contentTypeCompatibleWith("text/markdown"))
                .andExpect(content().string(containsString("# PatchPilot Demo Launch Evidence Package")))
                .andExpect(content().string(containsString("`READY`")));
    }

    @Test
    void should_return_not_found_when_archived_demo_launch_evidence_package_report_is_missing() throws Exception {
        when(demoLaunchEvidencePackageArchiveService.findArchive("missing-archive")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/demo/launch-evidence-package/archives/missing-archive/report/download"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_demo_launch_evidence_share_center() throws Exception {
        when(demoLaunchEvidenceShareCenterService.getShareCenter()).thenReturn(launchEvidenceShareCenter());

        mockMvc.perform(get("/api/demo/launch-evidence-share-center"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.shareReady").value(true))
                .andExpect(jsonPath("$.data.archiveCount").value(1))
                .andExpect(jsonPath("$.data.latestArchiveId").value("launch-evidence-archive-1"))
                .andExpect(jsonPath("$.data.latestSessionId").value("demo-session-20260624T003000Z"))
                .andExpect(jsonPath("$.data.latestTaskId").value("task-1"))
                .andExpect(jsonPath("$.data.latestPullRequestUrl").value("https://github.com/bingqin2/PatchPilot/pull/42"))
                .andExpect(jsonPath("$.data.latestWebhookDeliveryId").value("delivery-1"))
                .andExpect(jsonPath("$.data.evaluationRunId").value("evaluation-run-2"))
                .andExpect(jsonPath("$.data.latestDeliveryReceiptId").value("launch-delivery-receipt-1"))
                .andExpect(jsonPath("$.data.deliveryReceiptRecorded").value(true))
                .andExpect(jsonPath("$.data.deliveryReceiptFreshness").value("FRESH"))
                .andExpect(jsonPath("$.data.deliveryReceiptFresh").value(true))
                .andExpect(jsonPath("$.data.downloadActions[0]").value("Download launch evidence package archive launch-evidence-archive-1."))
                .andExpect(jsonPath("$.data.evidenceNotes[0]").value("Latest launch evidence archive status is READY."))
                .andExpect(jsonPath("$.data.markdownReport").value(containsString("# PatchPilot Demo Launch Evidence Share Center")));
    }

    @Test
    void should_download_demo_launch_evidence_share_center_report() throws Exception {
        when(demoLaunchEvidenceShareCenterService.getShareCenter()).thenReturn(launchEvidenceShareCenter());

        mockMvc.perform(get("/api/demo/launch-evidence-share-center/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-demo-launch-evidence-share-center.md")))
                .andExpect(content().contentType("text/markdown;charset=UTF-8"))
                .andExpect(content().string(containsString("# PatchPilot Demo Launch Evidence Share Center")))
                .andExpect(content().string(containsString("launch-evidence-archive-1")));
    }

    @Test
    void should_return_demo_launch_evidence_finalization() throws Exception {
        when(demoLaunchEvidenceFinalizationService.getFinalizationGate()).thenReturn(launchEvidenceFinalization());

        mockMvc.perform(get("/api/demo/launch-evidence-finalization"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.finalized").value(true))
                .andExpect(jsonPath("$.data.latestArchiveId").value("launch-evidence-archive-1"))
                .andExpect(jsonPath("$.data.latestSessionId").value("demo-session-20260624T003000Z"))
                .andExpect(jsonPath("$.data.latestDeliveryReceiptId").value("launch-delivery-receipt-1"))
                .andExpect(jsonPath("$.data.deliveryReceiptFreshness").value("FRESH"))
                .andExpect(jsonPath("$.data.checks[0].name").value("Launch evidence share readiness"))
                .andExpect(jsonPath("$.data.markdownReport").value(containsString("# PatchPilot Demo Launch Evidence Finalization Gate")));
    }

    @Test
    void should_download_demo_launch_evidence_finalization_report() throws Exception {
        when(demoLaunchEvidenceFinalizationService.getFinalizationGate()).thenReturn(launchEvidenceFinalization());

        mockMvc.perform(get("/api/demo/launch-evidence-finalization/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment;")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-demo-launch-evidence-finalization.md")))
                .andExpect(content().contentTypeCompatibleWith("text/markdown"))
                .andExpect(content().string(containsString("# PatchPilot Demo Launch Evidence Finalization Gate")))
                .andExpect(content().string(containsString("`READY`")));
    }

    @Test
    void should_return_demo_launch_acceptance_closeout() throws Exception {
        when(demoLaunchAcceptanceCloseoutService.getCloseout()).thenReturn(launchAcceptanceCloseout());

        mockMvc.perform(get("/api/demo/launch-acceptance-closeout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.accepted").value(true))
                .andExpect(jsonPath("$.data.sessionId").value("demo-session-20260624T003000Z"))
                .andExpect(jsonPath("$.data.latestPullRequestUrl").value("https://github.com/bingqin2/PatchPilot/pull/42"))
                .andExpect(jsonPath("$.data.latestArchiveId").value("launch-evidence-archive-1"))
                .andExpect(jsonPath("$.data.finalHandoffReportPackageArchiveStatus").value("READY"))
                .andExpect(jsonPath("$.data.finalHandoffReportPackageArchiveReady").value(true))
                .andExpect(jsonPath("$.data.finalHandoffReportPackageArchiveId").value("final-handoff-report-package-archive-1"))
                .andExpect(jsonPath("$.data.finalHandoffReportPackageArchiveSummary")
                        .value("Latest final handoff report package archive is download-ready and ready."))
                .andExpect(jsonPath("$.data.latestDeliveryReceiptId").value("launch-delivery-receipt-1"))
                .andExpect(jsonPath("$.data.deliveryReceiptFreshness").value("FRESH"))
                .andExpect(jsonPath("$.data.checks[0].name").value("Self-hosted launch readiness"))
                .andExpect(jsonPath("$.data.downloadActions").value(hasItem("Download final handoff report package archive final-handoff-report-package-archive-1.")))
                .andExpect(jsonPath("$.data.downloadActions").value(hasItem("Download launch acceptance closeout report.")))
                .andExpect(jsonPath("$.data.markdownReport").value(containsString("# PatchPilot Launch Acceptance Closeout")));
    }

    @Test
    void should_download_demo_launch_acceptance_closeout_report() throws Exception {
        when(demoLaunchAcceptanceCloseoutService.getCloseout()).thenReturn(launchAcceptanceCloseout());

        mockMvc.perform(get("/api/demo/launch-acceptance-closeout/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment;")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-launch-acceptance-closeout.md")))
                .andExpect(content().contentTypeCompatibleWith("text/markdown"))
                .andExpect(content().string(containsString("# PatchPilot Launch Acceptance Closeout")))
                .andExpect(content().string(containsString("launch-delivery-receipt-1")));
    }

    @Test
    void should_archive_demo_launch_acceptance_closeout() throws Exception {
        when(demoLaunchAcceptanceCloseoutArchiveService.archiveCurrentCloseout())
                .thenReturn(launchAcceptanceCloseoutArchive());

        mockMvc.perform(post("/api/demo/launch-acceptance-closeout/archives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("launch-closeout-archive-1"))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.accepted").value(true))
                .andExpect(jsonPath("$.data.sessionId").value("demo-session-20260624T003000Z"))
                .andExpect(jsonPath("$.data.latestPullRequestUrl").value("https://github.com/bingqin2/PatchPilot/pull/42"))
                .andExpect(jsonPath("$.data.latestDeliveryReceiptId").value("launch-delivery-receipt-1"))
                .andExpect(jsonPath("$.data.report").value(containsString("# PatchPilot Launch Acceptance Closeout")));

        verify(operatorSafetyAuditService).recordSafetyAudit(argThat(this::isLaunchAcceptanceCloseoutArchiveAudit));
    }

    private boolean isLaunchAcceptanceCloseoutArchiveAudit(RecordOperatorSafetyAuditCommand command) {
        return command != null
                && "DEMO_LAUNCH_ACCEPTANCE_CLOSEOUT_ARCHIVED".equals(command.action())
                && "DEMO_LAUNCH_ACCEPTANCE_CLOSEOUT_ARCHIVE".equals(command.resourceType())
                && "launch-closeout-archive-1".equals(command.resourceId())
                && command.scope() == TriggerQuarantineScope.REPOSITORY
                && "patchpilot/local-demo".equals(command.scopeKey())
                && "admin-api".equals(command.operator())
                && "Archived demo launch acceptance closeout READY".equals(command.reason());
    }

    @Test
    void should_return_recent_demo_launch_acceptance_closeout_archives() throws Exception {
        when(demoLaunchAcceptanceCloseoutArchiveService.listRecentArchives())
                .thenReturn(List.of(launchAcceptanceCloseoutArchive()));

        mockMvc.perform(get("/api/demo/launch-acceptance-closeout/archives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("launch-closeout-archive-1"))
                .andExpect(jsonPath("$.data[0].status").value("READY"))
                .andExpect(jsonPath("$.data[0].accepted").value(true))
                .andExpect(jsonPath("$.data[0].summary").value("PatchPilot launch acceptance closeout is complete."));
    }

    @Test
    void should_download_archived_demo_launch_acceptance_closeout_report_as_markdown_attachment() throws Exception {
        when(demoLaunchAcceptanceCloseoutArchiveService.findArchive("launch-closeout-archive-1"))
                .thenReturn(Optional.of(launchAcceptanceCloseoutArchive()));

        mockMvc.perform(get("/api/demo/launch-acceptance-closeout/archives/launch-closeout-archive-1/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment;")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-launch-acceptance-closeout-launch-closeout-archive-1.md")))
                .andExpect(content().contentTypeCompatibleWith("text/markdown"))
                .andExpect(content().string(containsString("# PatchPilot Launch Acceptance Closeout")))
                .andExpect(content().string(containsString("launch-delivery-receipt-1")));
    }

    @Test
    void should_return_not_found_when_archived_demo_launch_acceptance_closeout_report_is_missing() throws Exception {
        when(demoLaunchAcceptanceCloseoutArchiveService.findArchive("missing-archive")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/demo/launch-acceptance-closeout/archives/missing-archive/report/download"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_launch_acceptance_certificate() throws Exception {
        when(demoLaunchAcceptanceCertificateService.getCertificate()).thenReturn(launchAcceptanceCertificate());

        mockMvc.perform(get("/api/demo/launch-acceptance-certificate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.certified").value(true))
                .andExpect(jsonPath("$.data.summary")
                        .value("PatchPilot launch acceptance is certified from the latest accepted closeout archive."))
                .andExpect(jsonPath("$.data.nextAction")
                        .value("Share the certificate and archived closeout report with reviewers."))
                .andExpect(jsonPath("$.data.latestCloseoutArchiveId").value("launch-closeout-archive-1"))
                .andExpect(jsonPath("$.data.latestLaunchEvidenceArchiveId").value("launch-evidence-archive-1"))
                .andExpect(jsonPath("$.data.finalHandoffReportPackageArchiveStatus").value("READY"))
                .andExpect(jsonPath("$.data.finalHandoffReportPackageArchiveReady").value(true))
                .andExpect(jsonPath("$.data.finalHandoffReportPackageArchiveId").value("final-handoff-report-package-archive-1"))
                .andExpect(jsonPath("$.data.finalHandoffReportPackageArchiveSummary")
                        .value("Latest final handoff report package archive is download-ready and ready."))
                .andExpect(jsonPath("$.data.latestDeliveryReceiptId").value("launch-delivery-receipt-1"))
                .andExpect(jsonPath("$.data.latestPullRequestUrl").value("https://github.com/bingqin2/PatchPilot/pull/42"))
                .andExpect(jsonPath("$.data.downloadActions").value(hasItem("Download launch acceptance certificate.")))
                .andExpect(jsonPath("$.data.downloadActions")
                        .value(hasItem("Download final handoff report package archive final-handoff-report-package-archive-1.")));
    }

    @Test
    void should_download_launch_acceptance_certificate_report() throws Exception {
        when(demoLaunchAcceptanceCertificateService.getCertificate()).thenReturn(launchAcceptanceCertificate());

        mockMvc.perform(get("/api/demo/launch-acceptance-certificate/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment;")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-launch-acceptance-certificate.md")))
                .andExpect(content().contentTypeCompatibleWith("text/markdown"))
                .andExpect(content().string(containsString("# PatchPilot Launch Acceptance Certificate")))
                .andExpect(content().string(containsString("launch-closeout-archive-1")));
    }

    @Test
    void should_archive_demo_launch_acceptance_certificate() throws Exception {
        when(demoLaunchAcceptanceCertificateArchiveService.archiveCurrentCertificate())
                .thenReturn(launchAcceptanceCertificateArchive());

        mockMvc.perform(post("/api/demo/launch-acceptance-certificate/archives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("launch-certificate-archive-1"))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.certified").value(true))
                .andExpect(jsonPath("$.data.latestCloseoutArchiveId").value("launch-closeout-archive-1"))
                .andExpect(jsonPath("$.data.latestLaunchEvidenceArchiveId").value("launch-evidence-archive-1"))
                .andExpect(jsonPath("$.data.finalHandoffReportPackageArchiveStatus").value("READY"))
                .andExpect(jsonPath("$.data.finalHandoffReportPackageArchiveReady").value(true))
                .andExpect(jsonPath("$.data.finalHandoffReportPackageArchiveId").value("final-handoff-report-package-archive-1"))
                .andExpect(jsonPath("$.data.latestDeliveryReceiptId").value("launch-delivery-receipt-1"))
                .andExpect(jsonPath("$.data.latestPullRequestUrl").value("https://github.com/bingqin2/PatchPilot/pull/42"))
                .andExpect(jsonPath("$.data.report").value(containsString("# PatchPilot Launch Acceptance Certificate")));

        verify(operatorSafetyAuditService).recordSafetyAudit(argThat(this::isLaunchAcceptanceCertificateArchiveAudit));
    }

    private boolean isLaunchAcceptanceCertificateArchiveAudit(RecordOperatorSafetyAuditCommand command) {
        return command != null
                && "DEMO_LAUNCH_ACCEPTANCE_CERTIFICATE_ARCHIVED".equals(command.action())
                && "DEMO_LAUNCH_ACCEPTANCE_CERTIFICATE_ARCHIVE".equals(command.resourceType())
                && "launch-certificate-archive-1".equals(command.resourceId())
                && command.scope() == TriggerQuarantineScope.REPOSITORY
                && "patchpilot/local-demo".equals(command.scopeKey())
                && "admin-api".equals(command.operator())
                && "Archived demo launch acceptance certificate READY".equals(command.reason());
    }

    @Test
    void should_return_recent_demo_launch_acceptance_certificate_archives() throws Exception {
        when(demoLaunchAcceptanceCertificateArchiveService.listRecentArchives())
                .thenReturn(List.of(launchAcceptanceCertificateArchive()));

        mockMvc.perform(get("/api/demo/launch-acceptance-certificate/archives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("launch-certificate-archive-1"))
                .andExpect(jsonPath("$.data[0].status").value("READY"))
                .andExpect(jsonPath("$.data[0].certified").value(true))
                .andExpect(jsonPath("$.data[0].latestCloseoutArchiveId").value("launch-closeout-archive-1"));
    }

    @Test
    void should_download_archived_demo_launch_acceptance_certificate_report_as_markdown_attachment() throws Exception {
        when(demoLaunchAcceptanceCertificateArchiveService.findArchive("launch-certificate-archive-1"))
                .thenReturn(Optional.of(launchAcceptanceCertificateArchive()));

        mockMvc.perform(get("/api/demo/launch-acceptance-certificate/archives/launch-certificate-archive-1/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment;")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-launch-acceptance-certificate-launch-certificate-archive-1.md")))
                .andExpect(content().contentTypeCompatibleWith("text/markdown"))
                .andExpect(content().string(containsString("# PatchPilot Launch Acceptance Certificate")))
                .andExpect(content().string(containsString("launch-closeout-archive-1")));
    }

    @Test
    void should_return_not_found_when_archived_demo_launch_acceptance_certificate_report_is_missing() throws Exception {
        when(demoLaunchAcceptanceCertificateArchiveService.findArchive("missing-archive")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/demo/launch-acceptance-certificate/archives/missing-archive/report/download"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_final_demo_acceptance_summary() throws Exception {
        when(demoAcceptanceSummaryService.getSummary()).thenReturn(acceptanceSummary());

        mockMvc.perform(get("/api/demo/acceptance-summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.accepted").value(true))
                .andExpect(jsonPath("$.data.summary").value("PatchPilot final demo acceptance is ready for external review."))
                .andExpect(jsonPath("$.data.launchCertificateArchiveId").value("launch-certificate-archive-1"))
                .andExpect(jsonPath("$.data.taskCertificateArchiveId").value("task-evidence-certificate-archive-1"))
                .andExpect(jsonPath("$.data.latestPullRequestUrl").value("https://github.com/bingqin2/PatchPilot/pull/42"))
                .andExpect(jsonPath("$.data.checks.length()").value(2))
                .andExpect(jsonPath("$.data.checks[0].name").value("Launch acceptance certificate"))
                .andExpect(jsonPath("$.data.checks[1].name").value("Task evidence acceptance certificate"))
                .andExpect(jsonPath("$.data.downloadActions[0]").value("Download launch acceptance certificate archive launch-certificate-archive-1."))
                .andExpect(jsonPath("$.data.markdownReport").value(containsString("# PatchPilot Final Demo Acceptance Summary")));
    }

    @Test
    void should_download_final_demo_acceptance_summary_report() throws Exception {
        when(demoAcceptanceSummaryService.getSummary()).thenReturn(acceptanceSummary());

        mockMvc.perform(get("/api/demo/acceptance-summary/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-final-demo-acceptance-summary.md")))
                .andExpect(content().contentType("text/markdown;charset=UTF-8"))
                .andExpect(content().string(containsString("# PatchPilot Final Demo Acceptance Summary")))
                .andExpect(content().string(containsString("Accepted: `true`")));
    }

    @Test
    void should_return_final_demo_acceptance_share_package() throws Exception {
        when(demoFinalAcceptanceSharePackageService.getSharePackage()).thenReturn(finalAcceptanceSharePackage());

        mockMvc.perform(get("/api/demo/final-acceptance-share-package"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.sendReady").value(true))
                .andExpect(jsonPath("$.data.summary").value("PatchPilot final demo acceptance package is ready to send."))
                .andExpect(jsonPath("$.data.launchCertificateArchiveId").value("launch-certificate-archive-1"))
                .andExpect(jsonPath("$.data.taskCertificateArchiveId").value("task-evidence-certificate-archive-1"))
                .andExpect(jsonPath("$.data.messageSubject").value("PatchPilot final demo acceptance: task-1"))
                .andExpect(jsonPath("$.data.requiredAttachments", hasItem("Final demo acceptance summary report")))
                .andExpect(jsonPath("$.data.preSendChecks", hasItem("Confirm final demo acceptance status is READY and accepted.")))
                .andExpect(jsonPath("$.data.markdownReport").value(containsString("# PatchPilot Final Demo Acceptance Share Package")));
    }

    @Test
    void should_download_final_demo_acceptance_share_package_report() throws Exception {
        when(demoFinalAcceptanceSharePackageService.getSharePackage()).thenReturn(finalAcceptanceSharePackage());

        mockMvc.perform(get("/api/demo/final-acceptance-share-package/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-final-demo-acceptance-share-package.md")))
                .andExpect(content().contentType("text/markdown;charset=UTF-8"))
                .andExpect(content().string(containsString("# PatchPilot Final Demo Acceptance Share Package")))
                .andExpect(content().string(containsString("Subject: PatchPilot final demo acceptance: task-1")));
    }

    @Test
    void should_archive_final_demo_acceptance_share_package_and_record_audit() throws Exception {
        when(demoFinalAcceptanceSharePackageArchiveService.archiveCurrentSharePackage())
                .thenReturn(finalAcceptanceSharePackageArchive());

        mockMvc.perform(post("/api/demo/final-acceptance-share-package/archives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("final-acceptance-share-package-archive-1"))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.sendReady").value(true))
                .andExpect(jsonPath("$.data.messageSubject").value("PatchPilot final demo acceptance: task-1"))
                .andExpect(jsonPath("$.data.requiredAttachments", hasItem("Final demo acceptance summary report")))
                .andExpect(jsonPath("$.data.report").value(containsString("# PatchPilot Final Demo Acceptance Share Package")));

        verify(operatorSafetyAuditService).recordSafetyAudit(argThat(command ->
                command.action().equals("DEMO_FINAL_ACCEPTANCE_SHARE_PACKAGE_ARCHIVED")
                        && command.resourceType().equals("DEMO_FINAL_ACCEPTANCE_SHARE_PACKAGE_ARCHIVE")
                        && command.resourceId().equals("final-acceptance-share-package-archive-1")
                        && command.reason().contains("READY")
        ));
    }

    @Test
    void should_list_final_demo_acceptance_share_package_archives() throws Exception {
        when(demoFinalAcceptanceSharePackageArchiveService.listRecentArchives())
                .thenReturn(List.of(finalAcceptanceSharePackageArchive()));

        mockMvc.perform(get("/api/demo/final-acceptance-share-package/archives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value("final-acceptance-share-package-archive-1"))
                .andExpect(jsonPath("$.data[0].archivedAt").value("2026-06-29T02:00:00Z"));
    }

    @Test
    void should_download_archived_final_demo_acceptance_share_package_report() throws Exception {
        when(demoFinalAcceptanceSharePackageArchiveService.findArchive("final-acceptance-share-package-archive-1"))
                .thenReturn(Optional.of(finalAcceptanceSharePackageArchive()));

        mockMvc.perform(get("/api/demo/final-acceptance-share-package/archives/final-acceptance-share-package-archive-1/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-final-demo-acceptance-share-package-final-acceptance-share-package-archive-1.md")))
                .andExpect(content().contentType("text/markdown;charset=UTF-8"))
                .andExpect(content().string(containsString("# PatchPilot Final Demo Acceptance Share Package")))
                .andExpect(content().string(containsString("Subject: PatchPilot final demo acceptance: task-1")));
    }

    @Test
    void should_return_not_found_when_final_demo_acceptance_share_package_archive_is_missing() throws Exception {
        when(demoFinalAcceptanceSharePackageArchiveService.findArchive("missing")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/demo/final-acceptance-share-package/archives/missing/report/download"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_record_final_demo_acceptance_share_delivery_receipt_and_record_audit() throws Exception {
        when(demoFinalAcceptanceShareDeliveryReceiptService.recordDeliveryReceipt(argThat(request ->
                request.deliveryChannel().equals("email")
                        && request.deliveryTarget().equals("reviewer@example.com")
                        && request.operator().equals("local-operator")
                        && request.notes().equals("Sent final acceptance share package to the reviewer.")
                        && request.deliveredAt().equals(Instant.parse("2026-06-29T03:05:00Z"))
        ))).thenReturn(finalAcceptanceShareDeliveryReceipt());

        mockMvc.perform(post("/api/demo/final-acceptance-share-delivery-receipts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "deliveryChannel": "email",
                                  "deliveryTarget": "reviewer@example.com",
                                  "operator": "local-operator",
                                  "notes": "Sent final acceptance share package to the reviewer.",
                                  "deliveredAt": "2026-06-29T03:05:00Z"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("final-acceptance-delivery-receipt-1"))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.finalAcceptanceSharePackageArchiveId").value("final-acceptance-share-package-archive-1"))
                .andExpect(jsonPath("$.data.deliveryChannel").value("email"))
                .andExpect(jsonPath("$.data.deliveryTarget").value("reviewer@example.com"))
                .andExpect(jsonPath("$.data.messageSubject").value("PatchPilot final demo acceptance: task-1"))
                .andExpect(jsonPath("$.data.markdownReport").value(containsString("# PatchPilot Final Demo Acceptance Share Delivery Receipt")));

        verify(operatorSafetyAuditService).recordSafetyAudit(argThat(command ->
                command.action().equals("DEMO_FINAL_ACCEPTANCE_SHARE_DELIVERY_RECEIPT_RECORDED")
                        && command.resourceType().equals("DEMO_FINAL_ACCEPTANCE_SHARE_DELIVERY_RECEIPT")
                        && command.resourceId().equals("final-acceptance-delivery-receipt-1")
                        && command.operator().equals("local-operator")
                        && command.reason().contains("final-acceptance-share-package-archive-1")
        ));
    }

    @Test
    void should_reject_final_demo_acceptance_share_delivery_receipt_when_package_is_not_share_ready() throws Exception {
        when(demoFinalAcceptanceShareDeliveryReceiptService.recordDeliveryReceipt(any(DemoFinalAcceptanceShareDeliveryReceiptRequestDto.class)))
                .thenThrow(new IllegalStateException("final acceptance share package archive is not send-ready"));

        mockMvc.perform(post("/api/demo/final-acceptance-share-delivery-receipts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "deliveryChannel": "email",
                                  "deliveryTarget": "reviewer@example.com",
                                  "operator": "local-operator"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("final acceptance share package archive is not send-ready"));
    }

    @Test
    void should_list_final_demo_acceptance_share_delivery_receipts() throws Exception {
        when(demoFinalAcceptanceShareDeliveryReceiptService.listRecentReceipts())
                .thenReturn(List.of(finalAcceptanceShareDeliveryReceipt()));

        mockMvc.perform(get("/api/demo/final-acceptance-share-delivery-receipts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value("final-acceptance-delivery-receipt-1"))
                .andExpect(jsonPath("$.data[0].finalAcceptanceSharePackageArchiveId").value("final-acceptance-share-package-archive-1"));
    }

    @Test
    void should_download_final_demo_acceptance_share_delivery_receipt_report() throws Exception {
        when(demoFinalAcceptanceShareDeliveryReceiptService.findReceipt("final-acceptance-delivery-receipt-1"))
                .thenReturn(Optional.of(finalAcceptanceShareDeliveryReceipt()));

        mockMvc.perform(get("/api/demo/final-acceptance-share-delivery-receipts/final-acceptance-delivery-receipt-1/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-final-demo-acceptance-share-delivery-receipt-final-acceptance-delivery-receipt-1.md")))
                .andExpect(content().contentType("text/markdown;charset=UTF-8"))
                .andExpect(content().string(containsString("# PatchPilot Final Demo Acceptance Share Delivery Receipt")))
                .andExpect(content().string(containsString("reviewer@example.com")));
    }

    @Test
    void should_return_not_found_when_final_demo_acceptance_share_delivery_receipt_is_missing() throws Exception {
        when(demoFinalAcceptanceShareDeliveryReceiptService.findReceipt("missing-receipt")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/demo/final-acceptance-share-delivery-receipts/missing-receipt/report/download"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_final_demo_acceptance_share_finalization() throws Exception {
        when(demoFinalAcceptanceShareFinalizationService.getFinalizationGate())
                .thenReturn(finalAcceptanceShareFinalization());

        mockMvc.perform(get("/api/demo/final-acceptance-share-finalization"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.finalized").value(true))
                .andExpect(jsonPath("$.data.latestArchiveId").value("final-acceptance-share-package-archive-1"))
                .andExpect(jsonPath("$.data.latestDeliveryReceiptId").value("final-acceptance-delivery-receipt-1"))
                .andExpect(jsonPath("$.data.deliveryReceiptFreshness").value("FRESH"))
                .andExpect(jsonPath("$.data.markdownReport").value(containsString("# PatchPilot Final Demo Acceptance Share Finalization Gate")));
    }

    @Test
    void should_download_final_demo_acceptance_share_finalization_report() throws Exception {
        when(demoFinalAcceptanceShareFinalizationService.getFinalizationGate())
                .thenReturn(finalAcceptanceShareFinalization());

        mockMvc.perform(get("/api/demo/final-acceptance-share-finalization/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-final-demo-acceptance-share-finalization.md")))
                .andExpect(content().contentType("text/markdown;charset=UTF-8"))
                .andExpect(content().string(containsString("# PatchPilot Final Demo Acceptance Share Finalization Gate")))
                .andExpect(content().string(containsString("final-acceptance-delivery-receipt-1")));
    }

    @Test
    void should_return_final_acceptance_completion_evidence_bundle() throws Exception {
        when(demoFinalAcceptanceCompletionEvidenceBundleService.getBundle())
                .thenReturn(finalAcceptanceCompletionEvidenceBundle());

        mockMvc.perform(get("/api/demo/final-acceptance-completion-evidence-bundle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.readyToShare").value(true))
                .andExpect(jsonPath("$.data.latestCompletionArchiveId").value("final-acceptance-completion-archive-1"))
                .andExpect(jsonPath("$.data.latestSharePackageArchiveId").value("final-acceptance-share-package-archive-1"))
                .andExpect(jsonPath("$.data.latestDeliveryReceiptId").value("final-acceptance-delivery-receipt-1"))
                .andExpect(jsonPath("$.data.latestDeliveryTarget").value("reviewer@example.com"))
                .andExpect(jsonPath("$.data.downloadActions[0]").value("Download final acceptance completion evidence bundle."))
                .andExpect(jsonPath("$.data.sideEffectContract").value(containsString("read-only")))
                .andExpect(jsonPath("$.data.markdownReport").value(containsString("# PatchPilot Final Acceptance Completion Evidence Bundle")));
    }

    @Test
    void should_download_final_acceptance_completion_evidence_bundle_report() throws Exception {
        when(demoFinalAcceptanceCompletionEvidenceBundleService.getBundle())
                .thenReturn(finalAcceptanceCompletionEvidenceBundle());

        mockMvc.perform(get("/api/demo/final-acceptance-completion-evidence-bundle/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-final-acceptance-completion-evidence-bundle.md")))
                .andExpect(content().contentType("text/markdown;charset=UTF-8"))
                .andExpect(content().string(containsString("# PatchPilot Final Acceptance Completion Evidence Bundle")))
                .andExpect(content().string(containsString("final-acceptance-completion-archive-1")));
    }

    @Test
    void should_return_final_acceptance_completion_evidence_delivery_finalization() throws Exception {
        when(demoFinalAcceptanceCompletionEvidenceDeliveryFinalizationService.getFinalizationGate())
                .thenReturn(finalAcceptanceCompletionEvidenceDeliveryFinalization());

        mockMvc.perform(get("/api/demo/final-acceptance-completion-evidence-delivery-finalization"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.finalized").value(true))
                .andExpect(jsonPath("$.data.latestCompletionArchiveId").value("final-acceptance-completion-archive-1"))
                .andExpect(jsonPath("$.data.latestCompletionEvidenceDeliveryReceiptId")
                        .value("final-acceptance-completion-evidence-delivery-receipt-1"))
                .andExpect(jsonPath("$.data.deliveryReceiptFreshness").value("FRESH"))
                .andExpect(jsonPath("$.data.deliveryReceiptFresh").value(true))
                .andExpect(jsonPath("$.data.checks[0].name").value("Completion evidence bundle"))
                .andExpect(jsonPath("$.data.downloadActions[0]")
                        .value("Download final acceptance completion evidence delivery finalization report."))
                .andExpect(jsonPath("$.data.sideEffectContract").value(containsString("read-only")))
                .andExpect(jsonPath("$.data.markdownReport").value(containsString(
                        "# PatchPilot Final Acceptance Completion Evidence Delivery Finalization"
                )));
    }

    @Test
    void should_download_final_acceptance_completion_evidence_delivery_finalization_report() throws Exception {
        when(demoFinalAcceptanceCompletionEvidenceDeliveryFinalizationService.getFinalizationGate())
                .thenReturn(finalAcceptanceCompletionEvidenceDeliveryFinalization());

        mockMvc.perform(get("/api/demo/final-acceptance-completion-evidence-delivery-finalization/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString(
                        "patchpilot-final-acceptance-completion-evidence-delivery-finalization.md"
                )))
                .andExpect(content().contentType("text/markdown;charset=UTF-8"))
                .andExpect(content().string(containsString(
                        "# PatchPilot Final Acceptance Completion Evidence Delivery Finalization"
                )))
                .andExpect(content().string(containsString(
                        "final-acceptance-completion-evidence-delivery-receipt-1"
                )));
    }

    @Test
    void should_return_final_acceptance_completion_closeout() throws Exception {
        when(demoFinalAcceptanceCompletionCloseoutService.getCloseout())
                .thenReturn(finalAcceptanceCompletionCloseout());

        mockMvc.perform(get("/api/demo/final-acceptance-completion-closeout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.closed").value(true))
                .andExpect(jsonPath("$.data.latestTaskId").value("task-1"))
                .andExpect(jsonPath("$.data.latestPullRequestUrl").value("https://github.com/bingqin2/PatchPilot/pull/8"))
                .andExpect(jsonPath("$.data.latestSharePackageArchiveId").value("final-acceptance-share-package-archive-1"))
                .andExpect(jsonPath("$.data.latestCompletionArchiveId").value("final-acceptance-completion-archive-1"))
                .andExpect(jsonPath("$.data.latestCompletionEvidenceDeliveryReceiptId")
                        .value("final-acceptance-completion-evidence-delivery-receipt-1"))
                .andExpect(jsonPath("$.data.deliveryReceiptFreshness").value("FRESH"))
                .andExpect(jsonPath("$.data.checks[0].name").value("Final acceptance summary"))
                .andExpect(jsonPath("$.data.evidenceNotes[0]").value("Final demo acceptance summary is accepted."))
                .andExpect(jsonPath("$.data.downloadActions").value(hasItem(
                        "Download final acceptance completion closeout report."
                )))
                .andExpect(jsonPath("$.data.sideEffectContract").value(containsString("read-only")))
                .andExpect(jsonPath("$.data.markdownReport").value(containsString(
                        "# PatchPilot Final Acceptance Completion Closeout"
                )));
    }

    @Test
    void should_download_final_acceptance_completion_closeout_report() throws Exception {
        when(demoFinalAcceptanceCompletionCloseoutService.getCloseout())
                .thenReturn(finalAcceptanceCompletionCloseout());

        mockMvc.perform(get("/api/demo/final-acceptance-completion-closeout/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString(
                        "patchpilot-final-acceptance-completion-closeout.md"
                )))
                .andExpect(content().contentType("text/markdown;charset=UTF-8"))
                .andExpect(content().string(containsString(
                        "# PatchPilot Final Acceptance Completion Closeout"
                )))
                .andExpect(content().string(containsString(
                        "final-acceptance-completion-evidence-delivery-receipt-1"
                )));
    }

    @Test
    void should_archive_final_acceptance_completion_closeout_and_record_audit() throws Exception {
        when(demoFinalAcceptanceCompletionCloseoutArchiveService.archiveCurrentCloseout())
                .thenReturn(finalAcceptanceCompletionCloseoutArchive());

        mockMvc.perform(post("/api/demo/final-acceptance-completion-closeout/archives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("final-acceptance-completion-closeout-archive-1"))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.closed").value(true))
                .andExpect(jsonPath("$.data.latestTaskId").value("task-1"))
                .andExpect(jsonPath("$.data.latestPullRequestUrl").value("https://github.com/bingqin2/PatchPilot/pull/8"))
                .andExpect(jsonPath("$.data.latestCompletionArchiveId").value("final-acceptance-completion-archive-1"))
                .andExpect(jsonPath("$.data.latestCompletionEvidenceDeliveryReceiptId")
                        .value("final-acceptance-completion-evidence-delivery-receipt-1"))
                .andExpect(jsonPath("$.data.deliveryReceiptFreshness").value("FRESH"))
                .andExpect(jsonPath("$.data.report").value(containsString(
                        "# PatchPilot Final Acceptance Completion Closeout"
                )));

        verify(operatorSafetyAuditService).recordSafetyAudit(argThat(command ->
                command.action().equals("DEMO_FINAL_ACCEPTANCE_COMPLETION_CLOSEOUT_ARCHIVED")
                        && command.resourceType().equals("DEMO_FINAL_ACCEPTANCE_COMPLETION_CLOSEOUT_ARCHIVE")
                        && command.resourceId().equals("final-acceptance-completion-closeout-archive-1")
                        && command.reason().contains("final-acceptance-completion-evidence-delivery-receipt-1")
        ));
    }

    @Test
    void should_reject_final_acceptance_completion_closeout_archive_when_closeout_is_not_ready() throws Exception {
        when(demoFinalAcceptanceCompletionCloseoutArchiveService.archiveCurrentCloseout())
                .thenThrow(new IllegalStateException("final acceptance completion closeout is not ready"));

        mockMvc.perform(post("/api/demo/final-acceptance-completion-closeout/archives"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("final acceptance completion closeout is not ready"));
    }

    @Test
    void should_list_final_acceptance_completion_closeout_archives() throws Exception {
        when(demoFinalAcceptanceCompletionCloseoutArchiveService.listRecentArchives())
                .thenReturn(List.of(finalAcceptanceCompletionCloseoutArchive()));

        mockMvc.perform(get("/api/demo/final-acceptance-completion-closeout/archives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value("final-acceptance-completion-closeout-archive-1"))
                .andExpect(jsonPath("$.data[0].archivedAt").value("2026-06-29T06:30:00Z"));
    }

    @Test
    void should_download_archived_final_acceptance_completion_closeout_report() throws Exception {
        when(demoFinalAcceptanceCompletionCloseoutArchiveService.findArchive(
                "final-acceptance-completion-closeout-archive-1"
        )).thenReturn(Optional.of(finalAcceptanceCompletionCloseoutArchive()));

        mockMvc.perform(get("/api/demo/final-acceptance-completion-closeout/archives/final-acceptance-completion-closeout-archive-1/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString(
                        "patchpilot-final-acceptance-completion-closeout-final-acceptance-completion-closeout-archive-1.md"
                )))
                .andExpect(content().contentType("text/markdown;charset=UTF-8"))
                .andExpect(content().string(containsString(
                        "# PatchPilot Final Acceptance Completion Closeout"
                )))
                .andExpect(content().string(containsString(
                        "final-acceptance-completion-evidence-delivery-receipt-1"
                )));
    }

    @Test
    void should_return_not_found_when_final_acceptance_completion_closeout_archive_is_missing() throws Exception {
        when(demoFinalAcceptanceCompletionCloseoutArchiveService.findArchive("missing"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/demo/final-acceptance-completion-closeout/archives/missing/report/download"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_record_final_acceptance_completion_evidence_delivery_receipt_and_record_audit() throws Exception {
        when(demoFinalAcceptanceCompletionEvidenceDeliveryReceiptService.recordDeliveryReceipt(argThat(request ->
                request.deliveryChannel().equals("email")
                        && request.deliveryTarget().equals("reviewer@example.com")
                        && request.operator().equals("local-operator")
                        && request.notes().equals("Sent final completion evidence bundle to the reviewer.")
                        && request.deliveredAt().equals(Instant.parse("2026-06-29T04:25:00Z"))
        ))).thenReturn(finalAcceptanceCompletionEvidenceDeliveryReceipt());

        mockMvc.perform(post("/api/demo/final-acceptance-completion-evidence-delivery-receipts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "deliveryChannel": "email",
                                  "deliveryTarget": "reviewer@example.com",
                                  "operator": "local-operator",
                                  "notes": "Sent final completion evidence bundle to the reviewer.",
                                  "deliveredAt": "2026-06-29T04:25:00Z"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("final-acceptance-completion-evidence-delivery-receipt-1"))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.readyToShare").value(true))
                .andExpect(jsonPath("$.data.latestCompletionArchiveId").value("final-acceptance-completion-archive-1"))
                .andExpect(jsonPath("$.data.latestSharePackageArchiveId").value("final-acceptance-share-package-archive-1"))
                .andExpect(jsonPath("$.data.latestDeliveryReceiptId").value("final-acceptance-delivery-receipt-1"))
                .andExpect(jsonPath("$.data.deliveryTarget").value("reviewer@example.com"))
                .andExpect(jsonPath("$.data.markdownReport").value(containsString("# PatchPilot Final Acceptance Completion Evidence Delivery Receipt")));

        verify(operatorSafetyAuditService).recordSafetyAudit(argThat(command ->
                command.action().equals("DEMO_FINAL_ACCEPTANCE_COMPLETION_EVIDENCE_DELIVERY_RECEIPT_RECORDED")
                        && command.resourceType().equals("DEMO_FINAL_ACCEPTANCE_COMPLETION_EVIDENCE_DELIVERY_RECEIPT")
                        && command.resourceId().equals("final-acceptance-completion-evidence-delivery-receipt-1")
                        && command.operator().equals("local-operator")
                        && command.reason().contains("final-acceptance-completion-archive-1")
        ));
    }

    @Test
    void should_reject_final_acceptance_completion_evidence_delivery_receipt_when_bundle_is_not_ready() throws Exception {
        when(demoFinalAcceptanceCompletionEvidenceDeliveryReceiptService.recordDeliveryReceipt(
                any(DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptRequestDto.class)
        )).thenThrow(new IllegalStateException("final acceptance completion evidence bundle is not ready to share"));

        mockMvc.perform(post("/api/demo/final-acceptance-completion-evidence-delivery-receipts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "deliveryChannel": "email",
                                  "deliveryTarget": "reviewer@example.com",
                                  "operator": "local-operator"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("final acceptance completion evidence bundle is not ready to share"));
    }

    @Test
    void should_list_final_acceptance_completion_evidence_delivery_receipts() throws Exception {
        when(demoFinalAcceptanceCompletionEvidenceDeliveryReceiptService.listRecentReceipts())
                .thenReturn(List.of(finalAcceptanceCompletionEvidenceDeliveryReceipt()));

        mockMvc.perform(get("/api/demo/final-acceptance-completion-evidence-delivery-receipts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value("final-acceptance-completion-evidence-delivery-receipt-1"))
                .andExpect(jsonPath("$.data[0].latestCompletionArchiveId").value("final-acceptance-completion-archive-1"));
    }

    @Test
    void should_download_final_acceptance_completion_evidence_delivery_receipt_report() throws Exception {
        when(demoFinalAcceptanceCompletionEvidenceDeliveryReceiptService.findReceipt(
                "final-acceptance-completion-evidence-delivery-receipt-1"
        )).thenReturn(Optional.of(finalAcceptanceCompletionEvidenceDeliveryReceipt()));

        mockMvc.perform(get("/api/demo/final-acceptance-completion-evidence-delivery-receipts/final-acceptance-completion-evidence-delivery-receipt-1/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-final-acceptance-completion-evidence-delivery-receipt-final-acceptance-completion-evidence-delivery-receipt-1.md")))
                .andExpect(content().contentType("text/markdown;charset=UTF-8"))
                .andExpect(content().string(containsString("# PatchPilot Final Acceptance Completion Evidence Delivery Receipt")))
                .andExpect(content().string(containsString("reviewer@example.com")));
    }

    @Test
    void should_return_not_found_when_final_acceptance_completion_evidence_delivery_receipt_is_missing() throws Exception {
        when(demoFinalAcceptanceCompletionEvidenceDeliveryReceiptService.findReceipt("missing-receipt"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/demo/final-acceptance-completion-evidence-delivery-receipts/missing-receipt/report/download"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_archive_final_acceptance_completion_and_record_audit() throws Exception {
        when(demoFinalAcceptanceCompletionArchiveService.archiveCurrentCompletion())
                .thenReturn(finalAcceptanceCompletionArchive());

        mockMvc.perform(post("/api/demo/final-acceptance-completion-archives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("final-acceptance-completion-archive-1"))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.finalized").value(true))
                .andExpect(jsonPath("$.data.latestArchiveId").value("final-acceptance-share-package-archive-1"))
                .andExpect(jsonPath("$.data.latestDeliveryReceiptId").value("final-acceptance-delivery-receipt-1"))
                .andExpect(jsonPath("$.data.deliveryReceiptFreshness").value("FRESH"))
                .andExpect(jsonPath("$.data.report").value(containsString("# PatchPilot Final Demo Acceptance Share Finalization Gate")));

        verify(operatorSafetyAuditService).recordSafetyAudit(argThat(command ->
                command.action().equals("DEMO_FINAL_ACCEPTANCE_COMPLETION_ARCHIVED")
                        && command.resourceType().equals("DEMO_FINAL_ACCEPTANCE_COMPLETION_ARCHIVE")
                        && command.resourceId().equals("final-acceptance-completion-archive-1")
                        && command.reason().contains("final-acceptance-delivery-receipt-1")
        ));
    }

    @Test
    void should_reject_final_acceptance_completion_archive_when_finalization_is_not_ready() throws Exception {
        when(demoFinalAcceptanceCompletionArchiveService.archiveCurrentCompletion())
                .thenThrow(new IllegalStateException("final acceptance share finalization is not ready"));

        mockMvc.perform(post("/api/demo/final-acceptance-completion-archives"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("final acceptance share finalization is not ready"));
    }

    @Test
    void should_list_final_acceptance_completion_archives() throws Exception {
        when(demoFinalAcceptanceCompletionArchiveService.listRecentArchives())
                .thenReturn(List.of(finalAcceptanceCompletionArchive()));

        mockMvc.perform(get("/api/demo/final-acceptance-completion-archives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value("final-acceptance-completion-archive-1"))
                .andExpect(jsonPath("$.data[0].archivedAt").value("2026-06-29T04:00:00Z"));
    }

    @Test
    void should_download_archived_final_acceptance_completion_report() throws Exception {
        when(demoFinalAcceptanceCompletionArchiveService.findArchive("final-acceptance-completion-archive-1"))
                .thenReturn(Optional.of(finalAcceptanceCompletionArchive()));

        mockMvc.perform(get("/api/demo/final-acceptance-completion-archives/final-acceptance-completion-archive-1/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-final-acceptance-completion-final-acceptance-completion-archive-1.md")))
                .andExpect(content().contentType("text/markdown;charset=UTF-8"))
                .andExpect(content().string(containsString("# PatchPilot Final Demo Acceptance Share Finalization Gate")))
                .andExpect(content().string(containsString("final-acceptance-delivery-receipt-1")));
    }

    @Test
    void should_return_not_found_when_final_acceptance_completion_archive_is_missing() throws Exception {
        when(demoFinalAcceptanceCompletionArchiveService.findArchive("missing")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/demo/final-acceptance-completion-archives/missing/report/download"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_record_demo_launch_evidence_share_delivery_receipt() throws Exception {
        when(demoLaunchEvidenceShareDeliveryReceiptService.recordDeliveryReceipt(argThat(request ->
                request.deliveryChannel().equals("email")
                        && request.deliveryTarget().equals("reviewer@example.com")
                        && request.operator().equals("local-operator")
                        && request.notes().equals("Sent final launch evidence after the smoke demo.")
                        && request.deliveredAt().equals(Instant.parse("2026-06-28T06:05:00Z"))
        ))).thenReturn(launchEvidenceDeliveryReceipt());

        mockMvc.perform(post("/api/demo/launch-evidence-share-delivery-receipts")
                        .contentType("application/json")
                        .content("""
                                {
                                  "deliveryChannel": "email",
                                  "deliveryTarget": "reviewer@example.com",
                                  "operator": "local-operator",
                                  "notes": "Sent final launch evidence after the smoke demo.",
                                  "deliveredAt": "2026-06-28T06:05:00Z"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("launch-delivery-receipt-1"))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.launchEvidenceArchiveId").value("launch-evidence-archive-1"))
                .andExpect(jsonPath("$.data.sessionId").value("demo-session-20260624T003000Z"))
                .andExpect(jsonPath("$.data.deliveryChannel").value("email"))
                .andExpect(jsonPath("$.data.deliveryTarget").value("reviewer@example.com"))
                .andExpect(jsonPath("$.data.operator").value("local-operator"))
                .andExpect(jsonPath("$.data.messageSubject").value("PatchPilot demo launch evidence: demo-session-20260624T003000Z"))
                .andExpect(jsonPath("$.data.markdownReport")
                        .value(containsString("# PatchPilot Demo Launch Evidence Delivery Receipt")));

        verify(operatorSafetyAuditService).recordSafetyAudit(argThat(this::isDemoLaunchEvidenceShareDeliveryReceiptAudit));
    }

    private boolean isDemoLaunchEvidenceShareDeliveryReceiptAudit(RecordOperatorSafetyAuditCommand command) {
        return command != null
                && "DEMO_LAUNCH_EVIDENCE_DELIVERY_RECEIPT_RECORDED".equals(command.action())
                && "DEMO_LAUNCH_EVIDENCE_DELIVERY_RECEIPT".equals(command.resourceType())
                && "launch-delivery-receipt-1".equals(command.resourceId())
                && command.scope() == TriggerQuarantineScope.REPOSITORY
                && "patchpilot/local-demo".equals(command.scopeKey())
                && "local-operator".equals(command.operator())
                && "Recorded demo launch evidence delivery receipt for launch-evidence-archive-1".equals(command.reason());
    }

    @Test
    void should_reject_demo_launch_evidence_share_delivery_receipt_when_not_share_ready() throws Exception {
        when(demoLaunchEvidenceShareDeliveryReceiptService.recordDeliveryReceipt(any(DemoLaunchEvidenceShareDeliveryReceiptRequestDto.class)))
                .thenThrow(new IllegalStateException("launch evidence share center is not share-ready"));

        mockMvc.perform(post("/api/demo/launch-evidence-share-delivery-receipts")
                        .contentType("application/json")
                        .content("""
                                {
                                  "deliveryChannel": "email",
                                  "deliveryTarget": "reviewer@example.com",
                                  "operator": "local-operator",
                                  "notes": "Sent final launch evidence after the smoke demo.",
                                  "deliveredAt": "2026-06-28T06:05:00Z"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("launch evidence share center is not share-ready"));
    }

    @Test
    void should_return_recent_demo_launch_evidence_share_delivery_receipts() throws Exception {
        when(demoLaunchEvidenceShareDeliveryReceiptService.listRecentReceipts())
                .thenReturn(List.of(launchEvidenceDeliveryReceipt()));

        mockMvc.perform(get("/api/demo/launch-evidence-share-delivery-receipts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("launch-delivery-receipt-1"))
                .andExpect(jsonPath("$.data[0].launchEvidenceArchiveId").value("launch-evidence-archive-1"))
                .andExpect(jsonPath("$.data[0].deliveryChannel").value("email"))
                .andExpect(jsonPath("$.data[0].deliveryTarget").value("reviewer@example.com"));
    }

    @Test
    void should_download_demo_launch_evidence_share_delivery_receipt_as_markdown_attachment() throws Exception {
        when(demoLaunchEvidenceShareDeliveryReceiptService.findReceipt("launch-delivery-receipt-1"))
                .thenReturn(Optional.of(launchEvidenceDeliveryReceipt()));

        mockMvc.perform(get("/api/demo/launch-evidence-share-delivery-receipts/launch-delivery-receipt-1/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment;")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-demo-launch-evidence-delivery-receipt-launch-delivery-receipt-1.md")))
                .andExpect(content().contentTypeCompatibleWith("text/markdown"))
                .andExpect(content().string(containsString("# PatchPilot Demo Launch Evidence Delivery Receipt")))
                .andExpect(content().string(containsString("`READY`")));
    }

    @Test
    void should_return_not_found_when_demo_launch_evidence_share_delivery_receipt_is_missing() throws Exception {
        when(demoLaunchEvidenceShareDeliveryReceiptService.findReceipt("missing-receipt")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/demo/launch-evidence-share-delivery-receipts/missing-receipt/report/download"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_archive_self_hosted_launch_readiness_package() throws Exception {
        when(selfHostedLaunchReadinessArchiveService.archiveCurrentReadinessPackage())
                .thenReturn(selfHostedLaunchReadinessArchive());

        mockMvc.perform(post("/api/demo/self-hosted-launch-readiness/archives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("launch-readiness-archive-1"))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.readyToLaunch").value(true))
                .andExpect(jsonPath("$.data.readyCheckCount").value(2))
                .andExpect(jsonPath("$.data.report").value(containsString("# PatchPilot Self-Hosted Launch Readiness")));

        verify(operatorSafetyAuditService).recordSafetyAudit(argThat(this::isSelfHostedLaunchReadinessArchiveAudit));
    }

    private boolean isSelfHostedLaunchReadinessArchiveAudit(RecordOperatorSafetyAuditCommand command) {
        return command != null
                && "DEMO_SELF_HOSTED_LAUNCH_READINESS_ARCHIVED".equals(command.action())
                && "DEMO_SELF_HOSTED_LAUNCH_READINESS_ARCHIVE".equals(command.resourceType())
                && "launch-readiness-archive-1".equals(command.resourceId())
                && command.scope() == TriggerQuarantineScope.REPOSITORY
                && "patchpilot/local-demo".equals(command.scopeKey())
                && "admin-api".equals(command.operator())
                && "Archived self-hosted launch readiness READY".equals(command.reason());
    }

    @Test
    void should_return_recent_self_hosted_launch_readiness_archives() throws Exception {
        when(selfHostedLaunchReadinessArchiveService.listRecentArchives())
                .thenReturn(List.of(selfHostedLaunchReadinessArchive()));

        mockMvc.perform(get("/api/demo/self-hosted-launch-readiness/archives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("launch-readiness-archive-1"))
                .andExpect(jsonPath("$.data[0].status").value("READY"))
                .andExpect(jsonPath("$.data[0].summary").value("Self-hosted PatchPilot is ready for a controlled issue-to-PR launch."));
    }

    @Test
    void should_download_archived_self_hosted_launch_readiness_report_as_markdown_attachment() throws Exception {
        when(selfHostedLaunchReadinessArchiveService.findArchive("launch-readiness-archive-1"))
                .thenReturn(Optional.of(selfHostedLaunchReadinessArchive()));

        mockMvc.perform(get("/api/demo/self-hosted-launch-readiness/archives/launch-readiness-archive-1/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment;")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-self-hosted-launch-readiness-launch-readiness-archive-1.md")))
                .andExpect(content().contentTypeCompatibleWith("text/markdown"))
                .andExpect(content().string(containsString("# PatchPilot Self-Hosted Launch Readiness")))
                .andExpect(content().string(containsString("`READY`")));
    }

    @Test
    void should_return_not_found_when_archived_self_hosted_launch_readiness_report_is_missing() throws Exception {
        when(selfHostedLaunchReadinessArchiveService.findArchive("missing-archive")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/demo/self-hosted-launch-readiness/archives/missing-archive/report/download"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_demo_readiness_summary() throws Exception {
        when(demoReadinessService.getReadiness()).thenReturn(new DemoReadinessVo(
                DemoReadinessStatus.NEEDS_ATTENTION,
                "PatchPilot needs attention before a live demo.",
                List.of(
                        new DemoReadinessCheckVo(
                                "Credentials",
                                DemoReadinessStatus.READY,
                                "Required credentials are configured.",
                                "No action needed."
                        ),
                        new DemoReadinessCheckVo(
                                "Recent Pull Request",
                                DemoReadinessStatus.NEEDS_ATTENTION,
                                "No completed task with a Pull Request URL was found.",
                                "Run one controlled issue-to-PR smoke task before a live demo."
                        ),
                        new DemoReadinessCheckVo(
                                "Evaluation baseline",
                                DemoReadinessStatus.BLOCKED,
                                "Latest fixture baseline regressed. Newly failed cases: node-npm-basic-fix.",
                                "Investigate newly failed fixture cases before using the baseline as demo evidence."
                        )
                ),
                List.of(
                        "Run one controlled issue-to-PR smoke task before a live demo.",
                        "Investigate newly failed fixture cases before using the baseline as demo evidence."
                )
        ));

        mockMvc.perform(get("/api/demo/readiness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("NEEDS_ATTENTION"))
                .andExpect(jsonPath("$.data.summary").value("PatchPilot needs attention before a live demo."))
                .andExpect(jsonPath("$.data.checks.length()").value(3))
                .andExpect(jsonPath("$.data.checks[0].name").value("Credentials"))
                .andExpect(jsonPath("$.data.checks[0].status").value("READY"))
                .andExpect(jsonPath("$.data.checks[1].name").value("Recent Pull Request"))
                .andExpect(jsonPath("$.data.checks[2].name").value("Evaluation baseline"))
                .andExpect(jsonPath("$.data.checks[2].status").value("BLOCKED"))
                .andExpect(jsonPath("$.data.checks[2].message").value("Latest fixture baseline regressed. Newly failed cases: node-npm-basic-fix."))
                .andExpect(jsonPath("$.data.checks[2].action").value("Investigate newly failed fixture cases before using the baseline as demo evidence."))
                .andExpect(jsonPath("$.data.nextActions[0]").value("Run one controlled issue-to-PR smoke task before a live demo."))
                .andExpect(jsonPath("$.data.nextActions[1]").value("Investigate newly failed fixture cases before using the baseline as demo evidence."));
    }

    @Test
    void should_return_demo_smoke_checklist() throws Exception {
        when(demoSmokeChecklistService.getSmokeChecklist()).thenReturn(new DemoSmokeChecklistVo(
                DemoSmokeChecklistStatus.NEEDS_ATTENTION,
                "Live demo smoke checklist needs attention.",
                List.of(new DemoSmokeChecklistStepVo(
                        2,
                        "Webhook delivery",
                        DemoSmokeChecklistStatus.NEEDS_ATTENTION,
                        "Latest delivery needs redelivery.",
                        "delivery-invalid",
                        "Fix the webhook secret or URL, then use GitHub Redeliver before the live demo."
                )),
                List.of("Fix the webhook secret or URL, then use GitHub Redeliver before the live demo.")
        ));

        mockMvc.perform(get("/api/demo/smoke-checklist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("NEEDS_ATTENTION"))
                .andExpect(jsonPath("$.data.summary").value("Live demo smoke checklist needs attention."))
                .andExpect(jsonPath("$.data.steps[0].order").value(2))
                .andExpect(jsonPath("$.data.steps[0].name").value("Webhook delivery"))
                .andExpect(jsonPath("$.data.steps[0].status").value("NEEDS_ATTENTION"))
                .andExpect(jsonPath("$.data.steps[0].evidence").value("delivery-invalid"))
                .andExpect(jsonPath("$.data.nextActions[0]").value("Fix the webhook secret or URL, then use GitHub Redeliver before the live demo."));
    }

    @Test
    void should_return_demo_launch_preflight() throws Exception {
        when(demoLaunchPreflightService.preflight(argThat(request ->
                request.repositoryOwner().equals("bingqin2")
                        && request.repositoryName().equals("PatchPilot")
                        && request.issueNumber().equals(12L)
                        && request.triggerUser().equals("bingqin2")
                        && request.triggerComment().equals("/agent fix update docs/demo.md")
        ))).thenReturn(new DemoLaunchPreflightVo(
                DemoReadinessStatus.READY,
                true,
                "Demo launch preflight is ready to post the tested /agent fix comment.",
                new DemoReadinessVo(
                        DemoReadinessStatus.READY,
                        "PatchPilot is ready for a controlled demo.",
                        List.of(),
                        List.of()
                ),
                new TriggerEvaluationResultVo(
                        "WOULD_CREATE_TASK",
                        "ISSUE_COMMENT",
                        true,
                        null,
                        null,
                        new TriggerEvaluationDecisionVo(true, "Accepted", "UNKNOWN"),
                        new TriggerEvaluationDecisionVo(true, "No active task exists for this issue", "UNKNOWN"),
                        new TriggerEvaluationDecisionVo(true, "not blocked before task creation", "UNKNOWN"),
                        new TriggerEvaluationDecisionVo(true, "not rate limited before task creation", "UNKNOWN"),
                        new TriggerEvaluationDecisionVo(true, "model accepted trigger: concrete request", "UNKNOWN"),
                        true,
                        "Create task is allowed for this trigger."
                ),
                List.of("Post the tested /agent fix comment on the controlled GitHub issue.")
        ));

        mockMvc.perform(post("/api/demo/launch-preflight")
                        .contentType("application/json")
                        .content("""
                                {
                                  "repositoryOwner": "bingqin2",
                                  "repositoryName": "PatchPilot",
                                  "issueNumber": 12,
                                  "triggerUser": "bingqin2",
                                  "triggerComment": "/agent fix update docs/demo.md"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.readyToPost").value(true))
                .andExpect(jsonPath("$.data.summary").value("Demo launch preflight is ready to post the tested /agent fix comment."))
                .andExpect(jsonPath("$.data.readiness.status").value("READY"))
                .andExpect(jsonPath("$.data.triggerEvaluation.source").value("ISSUE_COMMENT"))
                .andExpect(jsonPath("$.data.triggerEvaluation.status").value("WOULD_CREATE_TASK"))
                .andExpect(jsonPath("$.data.nextActions[0]").value("Post the tested /agent fix comment on the controlled GitHub issue."));
    }

    @Test
    void should_return_demo_launch_command() throws Exception {
        DemoLaunchPreflightRequestDto preflightInput = new DemoLaunchPreflightRequestDto(
                "bingqin2",
                "PatchPilot",
                12L,
                "bingqin2",
                "/agent fix replace docs/demo.md PatchPilot smoke test"
        );
        when(demoLaunchCommandService.compose(argThat(request ->
                request.repositoryOwner().equals("bingqin2")
                        && request.repositoryName().equals("PatchPilot")
                        && request.issueNumber().equals(12L)
                        && request.triggerUser().equals("bingqin2")
                        && request.operation().equals("replace")
                        && request.targetPath().equals("docs/demo.md")
                        && request.replacementText().equals("PatchPilot smoke test")
        ))).thenReturn(new DemoLaunchCommandVo(
                "/agent fix replace docs/demo.md PatchPilot smoke test",
                preflightInput,
                "https://github.com/bingqin2/PatchPilot/issues/12",
                "Prepared a demo /agent fix replace command for bingqin2/PatchPilot#12.",
                List.of("Run launch preflight with the generated command before posting it on GitHub.")
        ));

        mockMvc.perform(post("/api/demo/launch-command")
                        .contentType("application/json")
                        .content("""
                                {
                                  "repositoryOwner": "bingqin2",
                                  "repositoryName": "PatchPilot",
                                  "issueNumber": 12,
                                  "triggerUser": "bingqin2",
                                  "operation": "replace",
                                  "targetPath": "docs/demo.md",
                                  "replacementText": "PatchPilot smoke test"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.triggerComment").value("/agent fix replace docs/demo.md PatchPilot smoke test"))
                .andExpect(jsonPath("$.data.preflightInput.repositoryOwner").value("bingqin2"))
                .andExpect(jsonPath("$.data.preflightInput.repositoryName").value("PatchPilot"))
                .andExpect(jsonPath("$.data.preflightInput.issueNumber").value(12))
                .andExpect(jsonPath("$.data.preflightInput.triggerUser").value("bingqin2"))
                .andExpect(jsonPath("$.data.preflightInput.triggerComment").value("/agent fix replace docs/demo.md PatchPilot smoke test"))
                .andExpect(jsonPath("$.data.githubIssueUrl").value("https://github.com/bingqin2/PatchPilot/issues/12"))
                .andExpect(jsonPath("$.data.summary").value("Prepared a demo /agent fix replace command for bingqin2/PatchPilot#12."))
                .andExpect(jsonPath("$.data.nextActions[0]").value("Run launch preflight with the generated command before posting it on GitHub."));
    }

    @Test
    void should_return_bad_request_for_invalid_demo_launch_command_request() throws Exception {
        when(demoLaunchCommandService.compose(argThat(request -> request.targetPath().equals(".git/config"))))
                .thenThrow(new IllegalArgumentException("targetPath must not target protected repository metadata"));

        mockMvc.perform(post("/api/demo/launch-command")
                        .contentType("application/json")
                        .content("""
                                {
                                  "repositoryOwner": "bingqin2",
                                  "repositoryName": "PatchPilot",
                                  "issueNumber": 12,
                                  "triggerUser": "bingqin2",
                                  "operation": "touch",
                                  "targetPath": ".git/config"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("targetPath must not target protected repository metadata"));
    }

    @Test
    void should_return_bad_request_for_invalid_demo_launch_preflight_request() throws Exception {
        when(demoLaunchPreflightService.preflight(argThat(request -> request.issueNumber().equals(0L))))
                .thenThrow(new IllegalArgumentException("issueNumber must be positive"));

        mockMvc.perform(post("/api/demo/launch-preflight")
                        .contentType("application/json")
                        .content("""
                                {
                                  "repositoryOwner": "bingqin2",
                                  "repositoryName": "PatchPilot",
                                  "issueNumber": 0,
                                  "triggerUser": "bingqin2",
                                  "triggerComment": "/agent fix update docs/demo.md"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("issueNumber must be positive"));
    }

    @Test
    void should_return_demo_evidence_bundle() throws Exception {
        when(demoEvidenceBundleService.getEvidenceBundle()).thenReturn(new DemoEvidenceBundleVo(
                DemoReadinessStatus.NEEDS_ATTENTION,
                "Demo evidence bundle needs attention.",
                new DemoEvidenceBundleSummaryVo(
                        2,
                        1,
                        1,
                        1,
                        true
                ),
                new DemoReadinessVo(
                        DemoReadinessStatus.READY,
                        "PatchPilot is ready for a controlled demo.",
                        List.of(),
                        List.of()
                ),
                new DemoSmokeChecklistVo(
                        DemoSmokeChecklistStatus.NEEDS_ATTENTION,
                        "Live demo smoke checklist needs attention.",
                        List.of(),
                        List.of("Run one controlled issue-to-PR smoke task before a live demo.")
                ),
                null,
                new DemoAdapterFixtureEvidenceVo(2, 1),
                new DemoEvaluationRunReadinessEvidenceVo(
                        DemoReadinessStatus.READY,
                        "evaluation-run-2",
                        "evaluation-run-1",
                        1,
                        0,
                        0,
                        List.of("java", "python"),
                        List.of("maven", "pytest"),
                        List.of("DANGEROUS_REQUEST", "SECRET_EXFILTRATION"),
                        "Evaluation run readiness summary reads archived full evaluation runs only; it does not create tasks, call the model, mutate Git, or write to GitHub.",
                        "Full evaluation run archive is ready; use it as current demo evidence."
                ),
                new FixTaskQueueSummaryVo(1, 0, 0, 0, 0, 1, 0, 0),
                null,
                "https://github.com/bingqin2/PatchPilot/pull/42",
                null,
                null,
                List.of(),
                null,
                1,
                DemoReadinessStatus.READY,
                "Latest handoff archive is ready to share.",
                "Share the latest handoff package summary and archived package with the reviewer.",
                DemoReadinessStatus.READY,
                "Post-demo handoff package is ready to share.",
                "Download the package, archive summary, and share checklist before sending handoff evidence.",
                List.of(
                        "Download handoff package archive handoff-archive-1.",
                        "Download handoff package archive summary.",
                        "Download handoff share checklist."
                ),
                "NO_ARCHIVE",
                false,
                "No archived launch evidence package is available for sharing.",
                "Archive a final demo launch evidence package after a completed live run before sharing launch evidence.",
                0,
                null,
                null,
                null,
                List.of(),
                DemoReadinessStatus.NEEDS_ATTENTION,
                false,
                "Demo launch evidence package is not finalized for accepted delivery evidence.",
                "Archive launch evidence, share it, record a delivery receipt, then download the finalization report.",
                "MISSING",
                false,
                null,
                new DemoLaunchAcceptanceCloseoutEvidenceVo(
                        DemoReadinessStatus.NEEDS_ATTENTION,
                        false,
                        false,
                        "No launch acceptance closeout archive is available.",
                        "Archive the final launch acceptance closeout after launch evidence is accepted.",
                        0,
                        null,
                        null,
                        null,
                        null,
                        null,
                        List.of("Archive the final launch acceptance closeout before using the evidence bundle as the launch record.")
                ),
                new DemoLaunchAcceptanceCertificateEvidenceVo(
                        DemoReadinessStatus.NEEDS_ATTENTION,
                        false,
                        false,
                        "No launch acceptance certificate archive is available.",
                        "Archive the final launch acceptance certificate after the launch acceptance closeout is certified.",
                        0,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        List.of("Archive the final launch acceptance certificate before using the evidence bundle as the external-review launch record.")
                ),
                new DemoTaskEvidenceAcceptanceCertificateEvidenceVo(
                        DemoReadinessStatus.NEEDS_ATTENTION,
                        false,
                        false,
                        "No task evidence acceptance certificate archive is available.",
                        "Archive a certified task evidence acceptance certificate after final task evidence closeout.",
                        0,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        List.of("Archive a task evidence acceptance certificate before using the evidence bundle as task-level review proof.")
                ),
                new DemoFinalHandoffReportPackageArchiveEvidenceVo(
                        DemoReadinessStatus.NEEDS_ATTENTION,
                        false,
                        false,
                        "No final handoff report package archive is available.",
                        "Archive the final handoff report package after the post-demo handoff package is finalized.",
                        0,
                        null,
                        null,
                        null,
                        null,
                        null,
                        false,
                        null,
                        List.of("Archive the final handoff report package before using the evidence bundle as post-demo closeout proof.")
                ),
                finalAcceptanceShareFinalization(),
                false,
                null,
                null,
                null,
                null,
                "MISSING",
                false,
                "No delivery receipt has been recorded for the current handoff package.",
                DemoReadinessStatus.NEEDS_ATTENTION,
                false,
                "Demo handoff package is send-ready but final delivery evidence is not current.",
                "Send the current handoff package, record a delivery receipt, then download the finalization report.",
                "MISSING",
                false,
                null,
                Instant.parse("2026-06-24T00:00:00Z"),
                List.of("Run one controlled issue-to-PR smoke task before a live demo.")
        ));

        mockMvc.perform(get("/api/demo/evidence-bundle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("NEEDS_ATTENTION"))
                .andExpect(jsonPath("$.data.summary").value("Demo evidence bundle needs attention."))
                .andExpect(jsonPath("$.data.summaryCounts.adapterFixtureCount").value(2))
                .andExpect(jsonPath("$.data.summaryCounts.failedAdapterFixtureCount").value(1))
                .andExpect(jsonPath("$.data.summaryCounts.activeQuarantineCount").value(1))
                .andExpect(jsonPath("$.data.recentPullRequestUrl").value("https://github.com/bingqin2/PatchPilot/pull/42"))
                .andExpect(jsonPath("$.data.recentWebhookDeliveries").isArray())
                .andExpect(jsonPath("$.data.evaluationRunReadiness.status").value("READY"))
                .andExpect(jsonPath("$.data.evaluationRunReadiness.latestRunId").value("evaluation-run-2"))
                .andExpect(jsonPath("$.data.evaluationRunReadiness.previousRunId").value("evaluation-run-1"))
                .andExpect(jsonPath("$.data.evaluationRunReadiness.passedDelta").value(1))
                .andExpect(jsonPath("$.data.evaluationRunReadiness.failedDelta").value(0))
                .andExpect(jsonPath("$.data.evaluationRunReadiness.coveredLanguages[0]").value("java"))
                .andExpect(jsonPath("$.data.evaluationRunReadiness.coveredBuildSystems[1]").value("pytest"))
                .andExpect(jsonPath("$.data.evaluationRunReadiness.safetyRejectionCategories[0]").value("DANGEROUS_REQUEST"))
                .andExpect(jsonPath("$.data.evaluationRunReadiness.nextAction")
                        .value("Full evaluation run archive is ready; use it as current demo evidence."))
                .andExpect(jsonPath("$.data.handoffShareChecklistStatus").value("READY"))
                .andExpect(jsonPath("$.data.handoffShareChecklistSummary").value("Latest handoff archive is ready to share."))
                .andExpect(jsonPath("$.data.handoffShareChecklistNextAction")
                        .value("Share the latest handoff package summary and archived package with the reviewer."))
                .andExpect(jsonPath("$.data.handoffShareCenterStatus").value("READY"))
                .andExpect(jsonPath("$.data.handoffShareCenterSummary").value("Post-demo handoff package is ready to share."))
                .andExpect(jsonPath("$.data.handoffShareCenterNextAction")
                        .value("Download the package, archive summary, and share checklist before sending handoff evidence."))
                .andExpect(jsonPath("$.data.handoffShareCenterDownloadActions[0]")
                        .value("Download handoff package archive handoff-archive-1."))
                .andExpect(jsonPath("$.data.handoffFinalizationStatus").value("NEEDS_ATTENTION"))
                .andExpect(jsonPath("$.data.handoffFinalizationSummary")
                        .value("Demo handoff package is send-ready but final delivery evidence is not current."))
                .andExpect(jsonPath("$.data.handoffFinalizationNextAction")
                        .value("Send the current handoff package, record a delivery receipt, then download the finalization report."))
                .andExpect(jsonPath("$.data.handoffFinalizationDeliveryReceiptFreshness").value("MISSING"))
                .andExpect(jsonPath("$.data.handoffFinalizationDeliveryReceiptFresh").value(false))
                .andExpect(jsonPath("$.data.launchAcceptanceCloseoutEvidence.status").value("NEEDS_ATTENTION"))
                .andExpect(jsonPath("$.data.launchAcceptanceCloseoutEvidence.archived").value(false))
                .andExpect(jsonPath("$.data.launchAcceptanceCloseoutEvidence.accepted").value(false))
                .andExpect(jsonPath("$.data.launchAcceptanceCloseoutEvidence.summary")
                        .value("No launch acceptance closeout archive is available."))
                .andExpect(jsonPath("$.data.launchAcceptanceCloseoutEvidence.nextAction")
                        .value("Archive the final launch acceptance closeout after launch evidence is accepted."))
                .andExpect(jsonPath("$.data.taskEvidenceAcceptanceCertificateEvidence.status").value("NEEDS_ATTENTION"))
                .andExpect(jsonPath("$.data.taskEvidenceAcceptanceCertificateEvidence.archived").value(false))
                .andExpect(jsonPath("$.data.taskEvidenceAcceptanceCertificateEvidence.certified").value(false))
                .andExpect(jsonPath("$.data.taskEvidenceAcceptanceCertificateEvidence.summary")
                        .value("No task evidence acceptance certificate archive is available."))
                .andExpect(jsonPath("$.data.taskEvidenceAcceptanceCertificateEvidence.nextAction")
                        .value("Archive a certified task evidence acceptance certificate after final task evidence closeout."))
                .andExpect(jsonPath("$.data.finalHandoffReportPackageArchiveEvidence.status").value("NEEDS_ATTENTION"))
                .andExpect(jsonPath("$.data.finalHandoffReportPackageArchiveEvidence.archived").value(false))
                .andExpect(jsonPath("$.data.finalHandoffReportPackageArchiveEvidence.downloadReady").value(false))
                .andExpect(jsonPath("$.data.finalHandoffReportPackageArchiveEvidence.summary")
                        .value("No final handoff report package archive is available."))
                .andExpect(jsonPath("$.data.finalHandoffReportPackageArchiveEvidence.nextAction")
                        .value("Archive the final handoff report package after the post-demo handoff package is finalized."))
                .andExpect(jsonPath("$.data.finalAcceptanceShareFinalization.status").value("READY"))
                .andExpect(jsonPath("$.data.finalAcceptanceShareFinalization.finalized").value(true))
                .andExpect(jsonPath("$.data.finalAcceptanceShareFinalization.summary")
                        .value("Final demo acceptance share package is finalized with a fresh delivery receipt."))
                .andExpect(jsonPath("$.data.finalAcceptanceShareFinalization.nextAction")
                        .value("Use the finalization report as the external-review acceptance delivery record."))
                .andExpect(jsonPath("$.data.finalAcceptanceShareFinalization.latestArchiveId")
                        .value("final-acceptance-share-package-archive-1"))
                .andExpect(jsonPath("$.data.finalAcceptanceShareFinalization.latestTaskId").value("task-1"))
                .andExpect(jsonPath("$.data.finalAcceptanceShareFinalization.latestDeliveryReceiptId")
                        .value("final-acceptance-delivery-receipt-1"))
                .andExpect(jsonPath("$.data.finalAcceptanceShareFinalization.latestDeliveryTarget")
                        .value("reviewer@example.com"))
                .andExpect(jsonPath("$.data.finalAcceptanceShareFinalization.latestDeliveryChannel").value("email"))
                .andExpect(jsonPath("$.data.finalAcceptanceShareFinalization.deliveryReceiptFreshness").value("FRESH"))
                .andExpect(jsonPath("$.data.finalAcceptanceShareFinalization.deliveryReceiptFresh").value(true))
                .andExpect(jsonPath("$.data.nextActions[0]").value("Run one controlled issue-to-PR smoke task before a live demo."));
    }

    @Test
    void should_return_demo_runbook_markdown() throws Exception {
        when(demoRunbookService.getRunbook()).thenReturn("""
                # PatchPilot Demo Runbook

                - Status: `NEEDS_ATTENTION`
                - Recent Pull Request: https://github.com/bingqin2/PatchPilot/pull/42
                """);

        mockMvc.perform(get("/api/demo/runbook"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("# PatchPilot Demo Runbook")))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("`NEEDS_ATTENTION`")))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("https://github.com/bingqin2/PatchPilot/pull/42")));
    }

    @Test
    void should_return_demo_script() throws Exception {
        when(demoScriptService.getScript()).thenReturn(new DemoScriptVo(
                DemoReadinessStatus.READY,
                "Demo script is ready.",
                List.of(new DemoScriptStepVo(
                        1,
                        "Confirm backend and dashboard access",
                        DemoReadinessStatus.READY,
                        "Open the dashboard and confirm protected APIs load.",
                        "curl http://127.0.0.1:8080/health",
                        "Backend reports UP and dashboard data loads.",
                        "Connectivity panel",
                        "Backend readiness endpoint is reachable."
                )),
                List.of("The script endpoint is read-only."),
                List.of("Follow the script from step 1 through Pull Request review."),
                Instant.parse("2026-06-24T00:00:00Z")
        ));

        mockMvc.perform(get("/api/demo/script"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.steps[0].order").value(1))
                .andExpect(jsonPath("$.data.steps[0].name").value("Confirm backend and dashboard access"))
                .andExpect(jsonPath("$.data.steps[0].verificationCommand").value("curl http://127.0.0.1:8080/health"))
                .andExpect(jsonPath("$.data.healthContract[0]").value("The script endpoint is read-only."));
    }

    @Test
    void should_return_demo_session_snapshot() throws Exception {
        DemoEvidenceBundleVo bundle = new DemoEvidenceBundleVo(
                DemoReadinessStatus.READY,
                "Demo evidence bundle is ready.",
                new DemoEvidenceBundleSummaryVo(
                        12,
                        0,
                        2,
                        0,
                        true
                ),
                new DemoReadinessVo(
                        DemoReadinessStatus.READY,
                        "PatchPilot is ready for a controlled demo.",
                        List.of(),
                        List.of()
                ),
                new DemoSmokeChecklistVo(
                        DemoSmokeChecklistStatus.READY,
                        "Live demo smoke checklist is ready.",
                        List.of(),
                        List.of()
                ),
                null,
                new DemoAdapterFixtureEvidenceVo(12, 0),
                new FixTaskQueueSummaryVo(2, 0, 0, 0, 0, 2, 0, 0),
                null,
                "https://github.com/bingqin2/PatchPilot/pull/42",
                null,
                null,
                List.of(),
                null,
                0,
                DemoReadinessStatus.READY,
                "Latest handoff archive is ready to share.",
                "Share the latest handoff package summary and archived package with the reviewer.",
                DemoReadinessStatus.READY,
                "Post-demo handoff package is ready to share.",
                "Download the package, archive summary, and share checklist before sending handoff evidence.",
                List.of(
                        "Download handoff package archive handoff-archive-1.",
                        "Download handoff package archive summary.",
                        "Download handoff share checklist."
                ),
                false,
                null,
                null,
                null,
                null,
                Instant.parse("2026-06-24T00:00:00Z"),
                List.of("Follow the script from step 1 through Pull Request review.")
        );
        DemoScriptVo script = new DemoScriptVo(
                DemoReadinessStatus.READY,
                "Demo script is ready.",
                List.of(new DemoScriptStepVo(
                        1,
                        "Confirm backend and dashboard access",
                        DemoReadinessStatus.READY,
                        "Open the dashboard and confirm protected APIs load.",
                        "curl http://127.0.0.1:8080/health",
                        "Backend reports UP and dashboard data loads.",
                        "Connectivity panel",
                        "Backend readiness endpoint is reachable."
                )),
                List.of("The script endpoint is read-only."),
                List.of("Follow the script from step 1 through Pull Request review."),
                Instant.parse("2026-06-24T00:30:00Z")
        );
        DemoReadinessSnapshotTrendVo trend = new DemoReadinessSnapshotTrendVo(
                DemoReadinessSnapshotTrendStatus.IMPROVING,
                "Demo readiness improved from BLOCKED to READY.",
                "readiness-snapshot-new",
                "readiness-snapshot-old",
                DemoReadinessStatus.READY,
                DemoReadinessStatus.BLOCKED,
                4,
                -2,
                -2,
                "Use the latest readiness snapshot as demo evidence or archive one more snapshot immediately before the live run.",
                "# PatchPilot Demo Readiness Snapshot Trend\n\n- Status: `IMPROVING`"
        );
        when(demoSessionSnapshotService.getSessionSnapshot()).thenReturn(new DemoSessionSnapshotVo(
                "demo-session-20260624T003000Z",
                DemoReadinessStatus.READY,
                "Demo session snapshot is ready.",
                Instant.parse("2026-06-24T00:30:00Z"),
                bundle,
                script,
                "# PatchPilot Demo Runbook\n\n- Status: `READY`",
                trend,
                List.of("Open the dashboard and confirm the demo session snapshot status."),
                List.of("GET /api/demo/session-snapshot is read-only: it does not create tasks, call the model, run tests, mutate Git, or write to GitHub."),
                "Status READY; recent PR https://github.com/bingqin2/PatchPilot/pull/42.",
                List.of("Follow the script from step 1 through Pull Request review.")
        ));

        mockMvc.perform(get("/api/demo/session-snapshot"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.sessionId").value("demo-session-20260624T003000Z"))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.summary").value("Demo session snapshot is ready."))
                .andExpect(jsonPath("$.data.evidenceBundle.recentPullRequestUrl").value("https://github.com/bingqin2/PatchPilot/pull/42"))
                .andExpect(jsonPath("$.data.script.steps[0].name").value("Confirm backend and dashboard access"))
                .andExpect(jsonPath("$.data.runbook").value(org.hamcrest.Matchers.containsString("# PatchPilot Demo Runbook")))
                .andExpect(jsonPath("$.data.readinessSnapshotTrend.status").value("IMPROVING"))
                .andExpect(jsonPath("$.data.readinessSnapshotTrend.latestSnapshotId").value("readiness-snapshot-new"))
                .andExpect(jsonPath("$.data.readinessSnapshotTrend.readyCheckDelta").value(4))
                .andExpect(jsonPath("$.data.operatorChecklist[0]").value("Open the dashboard and confirm the demo session snapshot status."))
                .andExpect(jsonPath("$.data.healthContract[0]").value("GET /api/demo/session-snapshot is read-only: it does not create tasks, call the model, run tests, mutate Git, or write to GitHub."))
                .andExpect(jsonPath("$.data.shareSummary").value(org.hamcrest.Matchers.containsString("READY")))
                .andExpect(jsonPath("$.data.nextActions[0]").value("Follow the script from step 1 through Pull Request review."));
    }

    @Test
    void should_return_demo_session_report_markdown() throws Exception {
        when(demoSessionReportService.getSessionReport()).thenReturn("""
                # PatchPilot Demo Session Report

                - Session: `demo-session-20260624T003000Z`
                - Status: `READY`
                """);

        mockMvc.perform(get("/api/demo/session-report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("# PatchPilot Demo Session Report")))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("demo-session-20260624T003000Z")))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("`READY`")));
    }

    @Test
    void should_download_demo_session_report_as_markdown_attachment() throws Exception {
        when(demoSessionReportService.getSessionReport()).thenReturn("""
                # PatchPilot Demo Session Report

                - Session: `demo-session-20260624T003000Z`
                - Status: `READY`
                """);

        mockMvc.perform(get("/api/demo/session-report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment;")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-demo-session-report.md")))
                .andExpect(content().contentTypeCompatibleWith("text/markdown"))
                .andExpect(content().string(containsString("# PatchPilot Demo Session Report")))
                .andExpect(content().string(containsString("demo-session-20260624T003000Z")));
    }

    @Test
    void should_return_demo_session_report_markdown_with_prepared_launch_commands() throws Exception {
        when(demoSessionReportService.getSessionReport(argThat(request ->
                request.preparedLaunchCommands().size() == 1
                        && request.preparedLaunchCommands().get(0).triggerComment().equals("/agent fix replace docs/demo.md PatchPilot smoke test")
        ))).thenReturn("""
                # PatchPilot Demo Session Report

                ## Prepared Launch Commands
                """);

        mockMvc.perform(post("/api/demo/session-report")
                        .contentType("application/json")
                        .content("""
                                {
                                  "preparedLaunchCommands": [
                                    {
                                      "triggerComment": "/agent fix replace docs/demo.md PatchPilot smoke test",
                                      "repositoryOwner": "bingqin2",
                                      "repositoryName": "PatchPilot",
                                      "issueNumber": 1,
                                      "triggerUser": "bingqin2",
                                      "operation": "replace",
                                      "targetPath": "docs/demo.md",
                                      "replacementText": "PatchPilot smoke test",
                                      "savedAt": "2026-06-26T01:00:00Z"
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("Prepared Launch Commands")));
    }

    @Test
    void should_return_demo_session_report_markdown_with_archived_launch_outcomes() throws Exception {
        when(demoSessionReportService.getSessionReport(argThat(request ->
                request.archivedLaunchOutcomes().size() == 1
                        && request.archivedLaunchOutcomes().get(0).taskId().equals("task-1")
                        && request.archivedLaunchOutcomes().get(0).pullRequestUrl().equals("https://github.com/bingqin2/PatchPilot/pull/42")
        ))).thenReturn("""
                # PatchPilot Demo Session Report

                ## Archived Launch Outcomes
                """);

        mockMvc.perform(post("/api/demo/session-report")
                        .contentType("application/json")
                        .content("""
                                {
                                  "preparedLaunchCommands": [],
                                  "archivedLaunchOutcomes": [
                                    {
                                      "triggerComment": "/agent fix replace docs/demo.md PatchPilot smoke test",
                                      "repositoryOwner": "bingqin2",
                                      "repositoryName": "PatchPilot",
                                      "issueNumber": 1,
                                      "triggerUser": "bingqin2",
                                      "taskId": "task-1",
                                      "taskStatus": "COMPLETED",
                                      "pullRequestUrl": "https://github.com/bingqin2/PatchPilot/pull/42",
                                      "archivedAt": "2026-06-26T01:10:00Z",
                                      "report": "# PatchPilot Demo Launch Outcome Report"
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("Archived Launch Outcomes")));
    }

    @Test
    void should_download_demo_session_report_with_prepared_launch_commands() throws Exception {
        when(demoSessionReportService.getSessionReport(any(DemoSessionReportRequestDto.class))).thenReturn("""
                # PatchPilot Demo Session Report

                ## Prepared Launch Commands
                """);

        mockMvc.perform(post("/api/demo/session-report/download")
                        .contentType("application/json")
                        .content("""
                                {
                                  "preparedLaunchCommands": [
                                    {
                                      "triggerComment": "/agent fix touch docs/history.md",
                                      "repositoryOwner": "bingqin2",
                                      "repositoryName": "PatchPilot",
                                      "issueNumber": 2,
                                      "triggerUser": "bingqin2",
                                      "operation": "touch",
                                      "targetPath": "docs/history.md",
                                      "replacementText": null,
                                      "savedAt": "2026-06-26T01:05:00Z"
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment;")))
                .andExpect(content().contentTypeCompatibleWith("text/markdown"))
                .andExpect(content().string(containsString("Prepared Launch Commands")));
    }

    @Test
    void should_return_demo_handoff_package_with_browser_context() throws Exception {
        when(demoSessionReportService.getHandoffPackage(argThat(request ->
                request.preparedLaunchCommands().size() == 1
                        && request.archivedLaunchOutcomes().size() == 1
                        && request.archivedLaunchOutcomes().get(0).taskId().equals("task-1")
        ))).thenReturn("""
                # PatchPilot Demo Handoff Package

                ## Handoff Summary
                """);

        mockMvc.perform(post("/api/demo/handoff-package")
                        .contentType("application/json")
                        .content("""
                                {
                                  "preparedLaunchCommands": [
                                    {
                                      "triggerComment": "/agent fix replace docs/demo.md PatchPilot smoke test",
                                      "repositoryOwner": "bingqin2",
                                      "repositoryName": "PatchPilot",
                                      "issueNumber": 1,
                                      "triggerUser": "bingqin2",
                                      "operation": "replace",
                                      "targetPath": "docs/demo.md",
                                      "replacementText": "PatchPilot smoke test",
                                      "savedAt": "2026-06-26T01:00:00Z"
                                    }
                                  ],
                                  "archivedLaunchOutcomes": [
                                    {
                                      "triggerComment": "/agent fix replace docs/demo.md PatchPilot smoke test",
                                      "repositoryOwner": "bingqin2",
                                      "repositoryName": "PatchPilot",
                                      "issueNumber": 1,
                                      "triggerUser": "bingqin2",
                                      "taskId": "task-1",
                                      "taskStatus": "COMPLETED",
                                      "pullRequestUrl": "https://github.com/bingqin2/PatchPilot/pull/42",
                                      "archivedAt": "2026-06-26T01:10:00Z",
                                      "report": "# PatchPilot Demo Launch Outcome Report"
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("# PatchPilot Demo Handoff Package")));
    }

    @Test
    void should_return_structured_handoff_readiness_with_browser_context() throws Exception {
        when(demoSessionReportService.getHandoffReadiness(argThat(request ->
                request.preparedLaunchCommands().size() == 1
                        && request.archivedLaunchOutcomes().size() == 1
                        && request.archivedLaunchOutcomes().get(0).taskId().equals("task-1")
        ))).thenReturn(new DemoHandoffReadinessVo(
                DemoReadinessStatus.READY,
                "Handoff package has current webhook delivery, PR, command, outcome, and readiness trend evidence.",
                "No missing handoff evidence.",
                List.of(
                        new DemoHandoffReadinessCheckVo(
                                "Webhook delivery evidence",
                                DemoReadinessStatus.READY,
                                "delivery-1 created task task-1.",
                                "No action needed."
                        ),
                        new DemoHandoffReadinessCheckVo(
                                "Prepared command context",
                                DemoReadinessStatus.READY,
                                "1 prepared command recorded.",
                                "No action needed."
                        )
                )
        ));

        mockMvc.perform(post("/api/demo/handoff-readiness")
                        .contentType("application/json")
                        .content("""
                                {
                                  "preparedLaunchCommands": [
                                    {
                                      "triggerComment": "/agent fix replace docs/demo.md PatchPilot smoke test",
                                      "repositoryOwner": "bingqin2",
                                      "repositoryName": "PatchPilot",
                                      "issueNumber": 1,
                                      "triggerUser": "bingqin2",
                                      "operation": "replace",
                                      "targetPath": "docs/demo.md",
                                      "replacementText": "PatchPilot smoke test",
                                      "savedAt": "2026-06-26T01:00:00Z"
                                    }
                                  ],
                                  "archivedLaunchOutcomes": [
                                    {
                                      "triggerComment": "/agent fix replace docs/demo.md PatchPilot smoke test",
                                      "repositoryOwner": "bingqin2",
                                      "repositoryName": "PatchPilot",
                                      "issueNumber": 1,
                                      "triggerUser": "bingqin2",
                                      "taskId": "task-1",
                                      "taskStatus": "COMPLETED",
                                      "pullRequestUrl": "https://github.com/bingqin2/PatchPilot/pull/42",
                                      "archivedAt": "2026-06-26T01:10:00Z",
                                      "report": "# PatchPilot Demo Launch Outcome Report"
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.summary").value("Handoff package has current webhook delivery, PR, command, outcome, and readiness trend evidence."))
                .andExpect(jsonPath("$.data.nextAction").value("No missing handoff evidence."))
                .andExpect(jsonPath("$.data.checks[0].name").value("Webhook delivery evidence"))
                .andExpect(jsonPath("$.data.checks[0].status").value("READY"))
                .andExpect(jsonPath("$.data.checks[0].summary").value("delivery-1 created task task-1."))
                .andExpect(jsonPath("$.data.checks[0].nextAction").value("No action needed."))
                .andExpect(jsonPath("$.data.checks[1].name").value("Prepared command context"));
    }

    @Test
    void should_download_demo_handoff_package_with_browser_context() throws Exception {
        when(demoSessionReportService.getHandoffPackage(any(DemoSessionReportRequestDto.class))).thenReturn("""
                # PatchPilot Demo Handoff Package

                ## Handoff Summary
                """);

        mockMvc.perform(post("/api/demo/handoff-package/download")
                        .contentType("application/json")
                        .content("""
                                {
                                  "preparedLaunchCommands": [],
                                  "archivedLaunchOutcomes": []
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment;")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-demo-handoff-package.md")))
                .andExpect(content().contentTypeCompatibleWith("text/markdown"))
                .andExpect(content().string(containsString("# PatchPilot Demo Handoff Package")));
    }

    @Test
    void should_archive_current_demo_session_report() throws Exception {
        when(demoSessionArchiveService.archiveCurrentSession(any(DemoSessionReportRequestDto.class))).thenReturn(new DemoSessionArchiveVo(
                "archive-1",
                "demo-session-20260624T003000Z",
                DemoReadinessStatus.READY,
                "Demo session snapshot is ready.",
                "Status READY; recent PR https://github.com/bingqin2/PatchPilot/pull/42.",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                Instant.parse("2026-06-24T04:00:00Z"),
                "# PatchPilot Demo Session Report\n\n- Status: `READY`"
        ));

        mockMvc.perform(post("/api/demo/session-archives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("archive-1"))
                .andExpect(jsonPath("$.data.sessionId").value("demo-session-20260624T003000Z"))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.recentPullRequestUrl").value("https://github.com/bingqin2/PatchPilot/pull/42"))
                .andExpect(jsonPath("$.data.report").value(org.hamcrest.Matchers.containsString("# PatchPilot Demo Session Report")));

        verify(operatorSafetyAuditService).recordSafetyAudit(argThat(this::isDemoSessionArchiveAudit));
    }

    private boolean isDemoSessionArchiveAudit(RecordOperatorSafetyAuditCommand command) {
        return command != null
                && "DEMO_SESSION_ARCHIVED".equals(command.action())
                && "DEMO_SESSION_ARCHIVE".equals(command.resourceType())
                && "archive-1".equals(command.resourceId())
                && command.scope() == TriggerQuarantineScope.REPOSITORY
                && "patchpilot/local-demo".equals(command.scopeKey())
                && "admin-api".equals(command.operator())
                && "Archived demo session demo-session-20260624T003000Z".equals(command.reason());
    }

    @Test
    void should_return_recent_demo_session_archives() throws Exception {
        when(demoSessionArchiveService.listRecentArchives()).thenReturn(List.of(new DemoSessionArchiveVo(
                "archive-1",
                "demo-session-20260624T003000Z",
                DemoReadinessStatus.READY,
                "Demo session snapshot is ready.",
                "Status READY; recent PR https://github.com/bingqin2/PatchPilot/pull/42.",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                Instant.parse("2026-06-24T04:00:00Z"),
                "# PatchPilot Demo Session Report\n\n- Status: `READY`"
        )));

        mockMvc.perform(get("/api/demo/session-archives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("archive-1"))
                .andExpect(jsonPath("$.data[0].sessionId").value("demo-session-20260624T003000Z"))
                .andExpect(jsonPath("$.data[0].shareSummary").value(org.hamcrest.Matchers.containsString("READY")));
    }

    @Test
    void should_download_archived_demo_session_report_as_markdown_attachment() throws Exception {
        when(demoSessionArchiveService.findArchive("archive-1")).thenReturn(Optional.of(new DemoSessionArchiveVo(
                "archive-1",
                "demo-session-20260624T003000Z",
                DemoReadinessStatus.READY,
                "Demo session snapshot is ready.",
                "Status READY; recent PR https://github.com/bingqin2/PatchPilot/pull/42.",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                Instant.parse("2026-06-24T04:00:00Z"),
                "# PatchPilot Demo Session Report\n\n- Status: `READY`"
        )));

        mockMvc.perform(get("/api/demo/session-archives/archive-1/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment;")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-demo-session-archive-1.md")))
                .andExpect(content().contentTypeCompatibleWith("text/markdown"))
                .andExpect(content().string(containsString("# PatchPilot Demo Session Report")))
                .andExpect(content().string(containsString("`READY`")));
    }

    @Test
    void should_return_not_found_when_archived_demo_session_report_is_missing() throws Exception {
        when(demoSessionArchiveService.findArchive("missing-archive")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/demo/session-archives/missing-archive/report/download"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_archive_current_demo_handoff_package() throws Exception {
        when(demoHandoffPackageArchiveService.archiveCurrentHandoffPackage(any(DemoSessionReportRequestDto.class)))
                .thenReturn(new DemoHandoffPackageArchiveVo(
                        "handoff-archive-1",
                        "demo-session-20260624T003000Z",
                        DemoReadinessStatus.READY,
                        "Demo session snapshot is ready.",
                        DemoReadinessStatus.READY,
                        "Handoff package has current webhook delivery, PR, command, outcome, and readiness trend evidence.",
                        "No missing handoff evidence.",
                        7,
                        0,
                        0,
                        "Status READY; recent PR https://github.com/bingqin2/PatchPilot/pull/42.",
                        "https://github.com/bingqin2/PatchPilot/pull/42",
                        Instant.parse("2026-06-24T04:00:00Z"),
                        "# PatchPilot Demo Handoff Package\n\n- Status: `READY`"
                ));

        mockMvc.perform(post("/api/demo/handoff-package-archives")
                        .contentType("application/json")
                        .content("""
                                {
                                  "preparedLaunchCommands": [],
                                  "archivedLaunchOutcomes": []
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("handoff-archive-1"))
                .andExpect(jsonPath("$.data.sessionId").value("demo-session-20260624T003000Z"))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.handoffReadinessStatus").value("READY"))
                .andExpect(jsonPath("$.data.handoffReadinessNextAction").value("No missing handoff evidence."))
                .andExpect(jsonPath("$.data.handoffReadyCheckCount").value(7))
                .andExpect(jsonPath("$.data.handoffNeedsAttentionCheckCount").value(0))
                .andExpect(jsonPath("$.data.handoffBlockedCheckCount").value(0))
                .andExpect(jsonPath("$.data.recentPullRequestUrl").value("https://github.com/bingqin2/PatchPilot/pull/42"))
                .andExpect(jsonPath("$.data.report").value(org.hamcrest.Matchers.containsString("# PatchPilot Demo Handoff Package")));

        verify(operatorSafetyAuditService).recordSafetyAudit(argThat(this::isDemoHandoffPackageArchiveAudit));
    }

    private boolean isDemoHandoffPackageArchiveAudit(RecordOperatorSafetyAuditCommand command) {
        return command != null
                && "DEMO_HANDOFF_PACKAGE_ARCHIVED".equals(command.action())
                && "DEMO_HANDOFF_PACKAGE_ARCHIVE".equals(command.resourceType())
                && "handoff-archive-1".equals(command.resourceId())
                && command.scope() == TriggerQuarantineScope.REPOSITORY
                && "patchpilot/local-demo".equals(command.scopeKey())
                && "admin-api".equals(command.operator())
                && "Archived demo handoff package demo-session-20260624T003000Z".equals(command.reason());
    }

    @Test
    void should_return_recent_demo_handoff_package_archives() throws Exception {
        when(demoHandoffPackageArchiveService.listRecentArchives()).thenReturn(List.of(new DemoHandoffPackageArchiveVo(
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                DemoReadinessStatus.READY,
                "Demo session snapshot is ready.",
                DemoReadinessStatus.READY,
                "Handoff package has current webhook delivery, PR, command, outcome, and readiness trend evidence.",
                "No missing handoff evidence.",
                7,
                0,
                0,
                "Status READY; recent PR https://github.com/bingqin2/PatchPilot/pull/42.",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                Instant.parse("2026-06-24T04:00:00Z"),
                "# PatchPilot Demo Handoff Package\n\n- Status: `READY`"
        )));

        mockMvc.perform(get("/api/demo/handoff-package-archives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("handoff-archive-1"))
                .andExpect(jsonPath("$.data[0].sessionId").value("demo-session-20260624T003000Z"))
                .andExpect(jsonPath("$.data[0].handoffReadinessStatus").value("READY"))
                .andExpect(jsonPath("$.data[0].handoffReadinessNextAction").value("No missing handoff evidence."))
                .andExpect(jsonPath("$.data[0].shareSummary").value(org.hamcrest.Matchers.containsString("READY")));
    }

    @Test
    void should_return_demo_handoff_package_archive_summary() throws Exception {
        when(demoHandoffPackageArchiveService.getArchiveSummary()).thenReturn(new DemoHandoffPackageArchiveSummaryVo(
                "READY",
                true,
                1,
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                DemoReadinessStatus.READY,
                Instant.parse("2026-06-24T04:00:00Z"),
                "Latest archived handoff package is READY and can be shared.",
                "No missing handoff evidence.",
                "# PatchPilot Handoff Package Archive Summary\n\n- Status: `READY`"
        ));

        mockMvc.perform(get("/api/demo/handoff-package-archives/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.shareReady").value(true))
                .andExpect(jsonPath("$.data.archiveCount").value(1))
                .andExpect(jsonPath("$.data.latestArchiveId").value("handoff-archive-1"))
                .andExpect(jsonPath("$.data.latestSessionId").value("demo-session-20260624T003000Z"))
                .andExpect(jsonPath("$.data.latestHandoffReadinessStatus").value("READY"))
                .andExpect(jsonPath("$.data.summary").value("Latest archived handoff package is READY and can be shared."))
                .andExpect(jsonPath("$.data.nextAction").value("No missing handoff evidence."))
                .andExpect(jsonPath("$.data.markdownReport").value(containsString("# PatchPilot Handoff Package Archive Summary")));
    }

    @Test
    void should_download_demo_handoff_package_archive_summary_as_markdown_attachment() throws Exception {
        when(demoHandoffPackageArchiveService.getArchiveSummary()).thenReturn(new DemoHandoffPackageArchiveSummaryVo(
                "READY",
                true,
                1,
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                DemoReadinessStatus.READY,
                Instant.parse("2026-06-24T04:00:00Z"),
                "Latest archived handoff package is READY and can be shared.",
                "No missing handoff evidence.",
                "# PatchPilot Handoff Package Archive Summary\n\n- Status: `READY`\n- Latest archive: `handoff-archive-1`"
        ));

        mockMvc.perform(get("/api/demo/handoff-package-archives/summary-report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment;")))
                .andExpect(header().string(
                        HttpHeaders.CONTENT_DISPOSITION,
                        containsString("patchpilot-demo-handoff-package-archive-summary.md")
                ))
                .andExpect(content().contentTypeCompatibleWith("text/markdown"))
                .andExpect(content().string(containsString("# PatchPilot Handoff Package Archive Summary")))
                .andExpect(content().string(containsString("`READY`")))
                .andExpect(content().string(containsString("`handoff-archive-1`")));
    }

    @Test
    void should_return_demo_handoff_share_checklist() throws Exception {
        when(demoHandoffShareChecklistService.getShareChecklist()).thenReturn(new DemoHandoffShareChecklistVo(
                DemoReadinessStatus.READY,
                "Latest handoff archive is ready to share.",
                "Share the latest handoff package summary and archived package with the reviewer.",
                List.of(
                        new DemoHandoffShareChecklistItemVo(
                                "Handoff package archive",
                                DemoReadinessStatus.READY,
                                "1 archived handoff package is available.",
                                "Use archive handoff-archive-1 as the latest package."
                        ),
                        new DemoHandoffShareChecklistItemVo(
                                "Portable evidence",
                                DemoReadinessStatus.READY,
                                "Markdown evidence is available for the latest handoff package.",
                                "Copy or download the handoff share checklist before handoff."
                        )
                ),
                "# PatchPilot Demo Handoff Share Checklist\n\n- Status: `READY`",
                Instant.parse("2026-06-24T05:00:00Z")
        ));

        mockMvc.perform(get("/api/demo/handoff-share-checklist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.summary").value("Latest handoff archive is ready to share."))
                .andExpect(jsonPath("$.data.nextAction")
                        .value("Share the latest handoff package summary and archived package with the reviewer."))
                .andExpect(jsonPath("$.data.checks[0].name").value("Handoff package archive"))
                .andExpect(jsonPath("$.data.checks[0].status").value("READY"))
                .andExpect(jsonPath("$.data.checks[1].name").value("Portable evidence"))
                .andExpect(jsonPath("$.data.markdownReport")
                        .value(containsString("# PatchPilot Demo Handoff Share Checklist")));
    }

    @Test
    void should_download_demo_handoff_share_checklist_as_markdown_attachment() throws Exception {
        when(demoHandoffShareChecklistService.getShareChecklist()).thenReturn(new DemoHandoffShareChecklistVo(
                DemoReadinessStatus.READY,
                "Latest handoff archive is ready to share.",
                "Share the latest handoff package summary and archived package with the reviewer.",
                List.of(new DemoHandoffShareChecklistItemVo(
                        "Handoff package archive",
                        DemoReadinessStatus.READY,
                        "1 archived handoff package is available.",
                        "Use archive handoff-archive-1 as the latest package."
                )),
                "# PatchPilot Demo Handoff Share Checklist\n\n- Status: `READY`",
                Instant.parse("2026-06-24T05:00:00Z")
        ));

        mockMvc.perform(get("/api/demo/handoff-share-checklist/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment;")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-demo-handoff-share-checklist.md")))
                .andExpect(content().contentTypeCompatibleWith("text/markdown"))
                .andExpect(content().string(containsString("# PatchPilot Demo Handoff Share Checklist")))
                .andExpect(content().string(containsString("`READY`")));
    }

    @Test
    void should_return_demo_handoff_share_center() throws Exception {
        when(demoHandoffShareCenterService.getShareCenter()).thenReturn(new DemoHandoffShareCenterVo(
                DemoReadinessStatus.READY,
                true,
                "Post-demo handoff package is ready to share.",
                "Download the package, archive summary, and share checklist before sending handoff evidence.",
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                "2026-06-24T04:00:00Z",
                null,
                null,
                null,
                null,
                false,
                "MISSING",
                false,
                "No delivery receipt has been recorded for the current handoff package.",
                List.of(
                        "Download handoff package archive handoff-archive-1.",
                        "Download handoff package archive summary.",
                        "Download handoff share checklist."
                ),
                List.of(
                        "Latest package archive is READY.",
                        "Share checklist has 4 checks."
                ),
                "# PatchPilot Demo Handoff Share Center\n\n- Status: `READY`",
                Instant.parse("2026-06-24T05:30:00Z")
        ));

        mockMvc.perform(get("/api/demo/handoff-share-center"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.shareReady").value(true))
                .andExpect(jsonPath("$.data.summary").value("Post-demo handoff package is ready to share."))
                .andExpect(jsonPath("$.data.nextAction")
                        .value("Download the package, archive summary, and share checklist before sending handoff evidence."))
                .andExpect(jsonPath("$.data.latestArchiveId").value("handoff-archive-1"))
                .andExpect(jsonPath("$.data.latestSessionId").value("demo-session-20260624T003000Z"))
                .andExpect(jsonPath("$.data.deliveryReceiptFreshness").value("MISSING"))
                .andExpect(jsonPath("$.data.deliveryReceiptFresh").value(false))
                .andExpect(jsonPath("$.data.deliveryReceiptFreshnessSummary")
                        .value("No delivery receipt has been recorded for the current handoff package."))
                .andExpect(jsonPath("$.data.downloadActions[0]").value("Download handoff package archive handoff-archive-1."))
                .andExpect(jsonPath("$.data.evidenceNotes[1]").value("Share checklist has 4 checks."))
                .andExpect(jsonPath("$.data.markdownReport")
                        .value(containsString("# PatchPilot Demo Handoff Share Center")));
    }

    @Test
    void should_download_demo_handoff_share_center_as_markdown_attachment() throws Exception {
        when(demoHandoffShareCenterService.getShareCenter()).thenReturn(new DemoHandoffShareCenterVo(
                DemoReadinessStatus.READY,
                true,
                "Post-demo handoff package is ready to share.",
                "Download the package, archive summary, and share checklist before sending handoff evidence.",
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                "2026-06-24T04:00:00Z",
                null,
                null,
                null,
                null,
                false,
                "MISSING",
                false,
                "No delivery receipt has been recorded for the current handoff package.",
                List.of("Download handoff package archive handoff-archive-1."),
                List.of("Latest package archive is READY."),
                "# PatchPilot Demo Handoff Share Center\n\n- Status: `READY`",
                Instant.parse("2026-06-24T05:30:00Z")
        ));

        mockMvc.perform(get("/api/demo/handoff-share-center/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment;")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-demo-handoff-share-center.md")))
                .andExpect(content().contentTypeCompatibleWith("text/markdown"))
                .andExpect(content().string(containsString("# PatchPilot Demo Handoff Share Center")))
                .andExpect(content().string(containsString("`READY`")));
    }

    @Test
    void should_return_demo_handoff_share_instructions() throws Exception {
        when(demoHandoffShareCenterService.getShareInstructions()).thenReturn(new DemoHandoffShareInstructionsVo(
                DemoReadinessStatus.READY,
                true,
                "Share the current handoff package with repository maintainers and demo reviewers.",
                "Send the prepared handoff message with all required attachments.",
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                List.of("Repository owner or maintainer", "Demo reviewer"),
                List.of(
                        "Handoff package archive handoff-archive-1",
                        "Handoff package archive summary",
                        "Handoff share checklist",
                        "Handoff share center report"
                ),
                List.of(
                        "Confirm the Pull Request link in the handoff package opens correctly.",
                        "Confirm no handoff share checklist warnings remain."
                ),
                "PatchPilot demo handoff: demo-session-20260624T003000Z",
                "The PatchPilot demo handoff package is ready to share.",
                "# PatchPilot Demo Handoff Share Instructions\n\n- Status: `READY`",
                Instant.parse("2026-06-24T05:45:00Z")
        ));

        mockMvc.perform(get("/api/demo/handoff-share-instructions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.sendReady").value(true))
                .andExpect(jsonPath("$.data.recommendedRecipients[0]").value("Repository owner or maintainer"))
                .andExpect(jsonPath("$.data.requiredAttachments[0]").value("Handoff package archive handoff-archive-1"))
                .andExpect(jsonPath("$.data.messageSubject").value("PatchPilot demo handoff: demo-session-20260624T003000Z"))
                .andExpect(jsonPath("$.data.markdownReport")
                        .value(containsString("# PatchPilot Demo Handoff Share Instructions")));
    }

    @Test
    void should_download_demo_handoff_share_instructions_as_markdown_attachment() throws Exception {
        when(demoHandoffShareCenterService.getShareInstructions()).thenReturn(new DemoHandoffShareInstructionsVo(
                DemoReadinessStatus.READY,
                true,
                "Share the current handoff package with repository maintainers and demo reviewers.",
                "Send the prepared handoff message with all required attachments.",
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                List.of("Repository owner or maintainer"),
                List.of("Handoff share center report"),
                List.of("Confirm no handoff share checklist warnings remain."),
                "PatchPilot demo handoff: demo-session-20260624T003000Z",
                "The PatchPilot demo handoff package is ready to share.",
                "# PatchPilot Demo Handoff Share Instructions\n\n- Status: `READY`",
                Instant.parse("2026-06-24T05:45:00Z")
        ));

        mockMvc.perform(get("/api/demo/handoff-share-instructions/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment;")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-demo-handoff-share-instructions.md")))
                .andExpect(content().contentTypeCompatibleWith("text/markdown"))
                .andExpect(content().string(containsString("# PatchPilot Demo Handoff Share Instructions")))
                .andExpect(content().string(containsString("`READY`")));
    }

    @Test
    void should_return_demo_handoff_finalization_gate() throws Exception {
        when(demoHandoffFinalizationService.getFinalizationGate()).thenReturn(new DemoHandoffFinalizationVo(
                DemoReadinessStatus.READY,
                true,
                "Demo handoff is finalized with a fresh delivery receipt for the current archive.",
                "Use the finalization report as the post-demo delivery acceptance record.",
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                "receipt-1",
                "Demo reviewer",
                "email",
                "2026-06-24T05:20:00Z",
                "FRESH",
                true,
                "Latest delivery receipt matches the current handoff archive and session.",
                List.of(
                        new DemoHandoffFinalizationCheckVo(
                                "Handoff package share readiness",
                                DemoReadinessStatus.READY,
                                "Share center is ready.",
                                "No action needed."
                        ),
                        new DemoHandoffFinalizationCheckVo(
                                "Delivery receipt freshness",
                                DemoReadinessStatus.READY,
                                "Latest delivery receipt matches the current handoff archive and session.",
                                "No action needed."
                        )
                ),
                List.of("Finalization report can be downloaded as the acceptance record."),
                "# PatchPilot Demo Handoff Finalization Gate\n\n- Status: `READY`",
                Instant.parse("2026-06-24T06:00:00Z")
        ));

        mockMvc.perform(get("/api/demo/handoff-finalization"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.finalized").value(true))
                .andExpect(jsonPath("$.data.summary")
                        .value("Demo handoff is finalized with a fresh delivery receipt for the current archive."))
                .andExpect(jsonPath("$.data.nextAction")
                        .value("Use the finalization report as the post-demo delivery acceptance record."))
                .andExpect(jsonPath("$.data.latestArchiveId").value("handoff-archive-1"))
                .andExpect(jsonPath("$.data.latestDeliveryReceiptId").value("receipt-1"))
                .andExpect(jsonPath("$.data.deliveryReceiptFreshness").value("FRESH"))
                .andExpect(jsonPath("$.data.deliveryReceiptFresh").value(true))
                .andExpect(jsonPath("$.data.checks[0].name").value("Handoff package share readiness"))
                .andExpect(jsonPath("$.data.evidenceNotes[0]")
                        .value("Finalization report can be downloaded as the acceptance record."))
                .andExpect(jsonPath("$.data.markdownReport")
                        .value(containsString("# PatchPilot Demo Handoff Finalization Gate")));
    }

    @Test
    void should_download_demo_handoff_finalization_as_markdown_attachment() throws Exception {
        when(demoHandoffFinalizationService.getFinalizationGate()).thenReturn(new DemoHandoffFinalizationVo(
                DemoReadinessStatus.READY,
                true,
                "Demo handoff is finalized with a fresh delivery receipt for the current archive.",
                "Use the finalization report as the post-demo delivery acceptance record.",
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                "receipt-1",
                "Demo reviewer",
                "email",
                "2026-06-24T05:20:00Z",
                "FRESH",
                true,
                "Latest delivery receipt matches the current handoff archive and session.",
                List.of(),
                List.of("Finalization report can be downloaded as the acceptance record."),
                "# PatchPilot Demo Handoff Finalization Gate\n\n- Status: `READY`",
                Instant.parse("2026-06-24T06:00:00Z")
        ));

        mockMvc.perform(get("/api/demo/handoff-finalization/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment;")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-demo-handoff-finalization.md")))
                .andExpect(content().contentTypeCompatibleWith("text/markdown"))
                .andExpect(content().string(containsString("# PatchPilot Demo Handoff Finalization Gate")))
                .andExpect(content().string(containsString("`READY`")));
    }

    @Test
    void should_return_demo_final_handoff_report_package() throws Exception {
        when(demoFinalHandoffReportPackageService.getReportPackage()).thenReturn(new DemoFinalHandoffReportPackageVo(
                DemoReadinessStatus.READY,
                true,
                "Final demo handoff report package is ready to deliver.",
                "Download this final handoff report package and attach the listed evidence files.",
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                "receipt-1",
                "task-certificate-archive-1",
                true,
                List.of(
                        "Archive summary: READY",
                        "Share checklist: READY",
                        "Share center: READY",
                        "Task evidence certificate: READY",
                        "Finalization: READY"
                ),
                List.of(
                        "Handoff package archive handoff-archive-1",
                        "Task evidence acceptance certificate archive task-certificate-archive-1",
                        "Finalization report"
                ),
                List.of("Confirm task evidence acceptance certificate task-certificate-archive-1 is attached."),
                List.of("Latest delivery receipt receipt-1 is fresh for handoff-archive-1/demo-session-20260624T003000Z."),
                List.of("Handoff package archive summary", "Handoff share center", "Handoff finalization"),
                "# PatchPilot Final Demo Handoff Report Package\n\n- Status: `READY`",
                Instant.parse("2026-06-24T07:00:00Z")
        ));

        mockMvc.perform(get("/api/demo/final-handoff-report-package"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.downloadReady").value(true))
                .andExpect(jsonPath("$.data.summary").value("Final demo handoff report package is ready to deliver."))
                .andExpect(jsonPath("$.data.latestArchiveId").value("handoff-archive-1"))
                .andExpect(jsonPath("$.data.latestDeliveryReceiptId").value("receipt-1"))
                .andExpect(jsonPath("$.data.taskCertificateArchiveId").value("task-certificate-archive-1"))
                .andExpect(jsonPath("$.data.taskCertificateReady").value(true))
                .andExpect(jsonPath("$.data.readinessChecks[4]").value("Finalization: READY"))
                .andExpect(jsonPath("$.data.requiredAttachments[1]")
                        .value("Task evidence acceptance certificate archive task-certificate-archive-1"))
                .andExpect(jsonPath("$.data.sourceReports[2]").value("Handoff finalization"))
                .andExpect(jsonPath("$.data.markdownReport")
                        .value(containsString("# PatchPilot Final Demo Handoff Report Package")));
    }

    @Test
    void should_download_demo_final_handoff_report_package_as_markdown_attachment() throws Exception {
        when(demoFinalHandoffReportPackageService.getReportPackage()).thenReturn(new DemoFinalHandoffReportPackageVo(
                DemoReadinessStatus.READY,
                true,
                "Final demo handoff report package is ready to deliver.",
                "Download this final handoff report package and attach the listed evidence files.",
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                "receipt-1",
                "task-certificate-archive-1",
                true,
                List.of("Finalization: READY"),
                List.of("Finalization report"),
                List.of("Confirm no handoff share checklist warnings remain."),
                List.of("Finalization report can be downloaded as the acceptance record."),
                List.of("Handoff finalization"),
                "# PatchPilot Final Demo Handoff Report Package\n\n- Status: `READY`",
                Instant.parse("2026-06-24T07:00:00Z")
        ));

        mockMvc.perform(get("/api/demo/final-handoff-report-package/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment;")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-demo-final-handoff-report-package.md")))
                .andExpect(content().contentTypeCompatibleWith("text/markdown"))
                .andExpect(content().string(containsString("# PatchPilot Final Demo Handoff Report Package")))
                .andExpect(content().string(containsString("`READY`")));
    }

    @Test
    void should_archive_demo_final_handoff_report_package() throws Exception {
        when(demoFinalHandoffReportPackageArchiveService.archiveCurrentReportPackage())
                .thenReturn(finalHandoffReportPackageArchive());

        mockMvc.perform(post("/api/demo/final-handoff-report-package/archives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("final-handoff-package-archive-1"))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.downloadReady").value(true))
                .andExpect(jsonPath("$.data.latestArchiveId").value("handoff-archive-1"))
                .andExpect(jsonPath("$.data.latestSessionId").value("demo-session-20260624T003000Z"))
                .andExpect(jsonPath("$.data.latestDeliveryReceiptId").value("delivery-receipt-1"))
                .andExpect(jsonPath("$.data.taskCertificateArchiveId").value("task-evidence-certificate-archive-1"))
                .andExpect(jsonPath("$.data.taskCertificateReady").value(true))
                .andExpect(jsonPath("$.data.readinessChecks[0]").value("Finalization: READY"))
                .andExpect(jsonPath("$.data.report")
                        .value(containsString("# PatchPilot Final Demo Handoff Report Package")));

        verify(operatorSafetyAuditService).recordSafetyAudit(argThat(this::isFinalHandoffReportPackageArchiveAudit));
    }

    private boolean isFinalHandoffReportPackageArchiveAudit(RecordOperatorSafetyAuditCommand command) {
        return command != null
                && "DEMO_FINAL_HANDOFF_REPORT_PACKAGE_ARCHIVED".equals(command.action())
                && "DEMO_FINAL_HANDOFF_REPORT_PACKAGE_ARCHIVE".equals(command.resourceType())
                && "final-handoff-package-archive-1".equals(command.resourceId())
                && command.scope() == TriggerQuarantineScope.REPOSITORY
                && "patchpilot/local-demo".equals(command.scopeKey())
                && "admin-api".equals(command.operator())
                && "Archived demo final handoff report package READY".equals(command.reason());
    }

    @Test
    void should_return_recent_demo_final_handoff_report_package_archives() throws Exception {
        when(demoFinalHandoffReportPackageArchiveService.listRecentArchives())
                .thenReturn(List.of(finalHandoffReportPackageArchive()));

        mockMvc.perform(get("/api/demo/final-handoff-report-package/archives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("final-handoff-package-archive-1"))
                .andExpect(jsonPath("$.data[0].status").value("READY"))
                .andExpect(jsonPath("$.data[0].downloadReady").value(true))
                .andExpect(jsonPath("$.data[0].latestArchiveId").value("handoff-archive-1"))
                .andExpect(jsonPath("$.data[0].latestDeliveryReceiptId").value("delivery-receipt-1"));
    }

    @Test
    void should_download_archived_demo_final_handoff_report_package_as_markdown_attachment() throws Exception {
        when(demoFinalHandoffReportPackageArchiveService.findArchive("final-handoff-package-archive-1"))
                .thenReturn(Optional.of(finalHandoffReportPackageArchive()));

        mockMvc.perform(get("/api/demo/final-handoff-report-package/archives/final-handoff-package-archive-1/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment;")))
                .andExpect(header().string(
                        HttpHeaders.CONTENT_DISPOSITION,
                        containsString("patchpilot-demo-final-handoff-report-package-final-handoff-package-archive-1.md")
                ))
                .andExpect(content().contentTypeCompatibleWith("text/markdown"))
                .andExpect(content().string(containsString("# PatchPilot Final Demo Handoff Report Package")))
                .andExpect(content().string(containsString("handoff-archive-1")));
    }

    @Test
    void should_return_not_found_when_archived_demo_final_handoff_report_package_is_missing() throws Exception {
        when(demoFinalHandoffReportPackageArchiveService.findArchive("missing-archive")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/demo/final-handoff-report-package/archives/missing-archive/report/download"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_record_demo_handoff_share_delivery_receipt() throws Exception {
        when(demoHandoffShareDeliveryReceiptService.recordDeliveryReceipt(argThat(request ->
                request.deliveryChannel().equals("email")
                        && request.deliveryTarget().equals("maintainer@example.com")
                        && request.operator().equals("local-operator")
                        && request.notes().equals("Sent after the demo review.")
                        && request.deliveredAt().equals(Instant.parse("2026-06-24T06:05:00Z"))
        ))).thenReturn(new DemoHandoffShareDeliveryReceiptVo(
                "delivery-receipt-1",
                DemoReadinessStatus.READY,
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                "email",
                "maintainer@example.com",
                "local-operator",
                "Sent after the demo review.",
                "PatchPilot demo handoff: demo-session-20260624T003000Z",
                Instant.parse("2026-06-24T06:05:00Z"),
                Instant.parse("2026-06-24T06:10:00Z"),
                "# PatchPilot Demo Handoff Share Delivery Receipt\n\n- Status: `READY`"
        ));

        mockMvc.perform(post("/api/demo/handoff-share-delivery-receipts")
                        .contentType("application/json")
                        .content("""
                                {
                                  "deliveryChannel": "email",
                                  "deliveryTarget": "maintainer@example.com",
                                  "operator": "local-operator",
                                  "notes": "Sent after the demo review.",
                                  "deliveredAt": "2026-06-24T06:05:00Z"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("delivery-receipt-1"))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.handoffArchiveId").value("handoff-archive-1"))
                .andExpect(jsonPath("$.data.sessionId").value("demo-session-20260624T003000Z"))
                .andExpect(jsonPath("$.data.deliveryChannel").value("email"))
                .andExpect(jsonPath("$.data.deliveryTarget").value("maintainer@example.com"))
                .andExpect(jsonPath("$.data.operator").value("local-operator"))
                .andExpect(jsonPath("$.data.messageSubject").value("PatchPilot demo handoff: demo-session-20260624T003000Z"))
                .andExpect(jsonPath("$.data.markdownReport")
                        .value(containsString("# PatchPilot Demo Handoff Share Delivery Receipt")));

        verify(operatorSafetyAuditService).recordSafetyAudit(argThat(this::isDemoHandoffShareDeliveryReceiptAudit));
    }

    private boolean isDemoHandoffShareDeliveryReceiptAudit(RecordOperatorSafetyAuditCommand command) {
        return command != null
                && "DEMO_HANDOFF_SHARE_DELIVERY_RECEIPT_RECORDED".equals(command.action())
                && "DEMO_HANDOFF_SHARE_DELIVERY_RECEIPT".equals(command.resourceType())
                && "delivery-receipt-1".equals(command.resourceId())
                && command.scope() == TriggerQuarantineScope.REPOSITORY
                && "patchpilot/local-demo".equals(command.scopeKey())
                && "local-operator".equals(command.operator())
                && "Recorded demo handoff share delivery receipt for handoff-archive-1".equals(command.reason());
    }

    @Test
    void should_reject_demo_handoff_share_delivery_receipt_when_not_send_ready() throws Exception {
        when(demoHandoffShareDeliveryReceiptService.recordDeliveryReceipt(any(DemoHandoffShareDeliveryReceiptRequestDto.class)))
                .thenThrow(new IllegalStateException("handoff share instructions are not send-ready"));

        mockMvc.perform(post("/api/demo/handoff-share-delivery-receipts")
                        .contentType("application/json")
                        .content("""
                                {
                                  "deliveryChannel": "email",
                                  "deliveryTarget": "maintainer@example.com",
                                  "operator": "local-operator",
                                  "notes": "Sent after the demo review.",
                                  "deliveredAt": "2026-06-24T06:05:00Z"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("handoff share instructions are not send-ready"));
    }

    @Test
    void should_return_recent_demo_handoff_share_delivery_receipts() throws Exception {
        when(demoHandoffShareDeliveryReceiptService.listRecentReceipts()).thenReturn(List.of(new DemoHandoffShareDeliveryReceiptVo(
                "delivery-receipt-1",
                DemoReadinessStatus.READY,
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                "email",
                "maintainer@example.com",
                "local-operator",
                "Sent after the demo review.",
                "PatchPilot demo handoff: demo-session-20260624T003000Z",
                Instant.parse("2026-06-24T06:05:00Z"),
                Instant.parse("2026-06-24T06:10:00Z"),
                "# PatchPilot Demo Handoff Share Delivery Receipt\n\n- Status: `READY`"
        )));

        mockMvc.perform(get("/api/demo/handoff-share-delivery-receipts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("delivery-receipt-1"))
                .andExpect(jsonPath("$.data[0].handoffArchiveId").value("handoff-archive-1"))
                .andExpect(jsonPath("$.data[0].deliveryChannel").value("email"))
                .andExpect(jsonPath("$.data[0].deliveryTarget").value("maintainer@example.com"));
    }

    @Test
    void should_download_demo_handoff_share_delivery_receipt_as_markdown_attachment() throws Exception {
        when(demoHandoffShareDeliveryReceiptService.findReceipt("delivery-receipt-1")).thenReturn(Optional.of(new DemoHandoffShareDeliveryReceiptVo(
                "delivery-receipt-1",
                DemoReadinessStatus.READY,
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                "email",
                "maintainer@example.com",
                "local-operator",
                "Sent after the demo review.",
                "PatchPilot demo handoff: demo-session-20260624T003000Z",
                Instant.parse("2026-06-24T06:05:00Z"),
                Instant.parse("2026-06-24T06:10:00Z"),
                "# PatchPilot Demo Handoff Share Delivery Receipt\n\n- Status: `READY`"
        )));

        mockMvc.perform(get("/api/demo/handoff-share-delivery-receipts/delivery-receipt-1/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment;")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-demo-handoff-share-delivery-receipt-delivery-receipt-1.md")))
                .andExpect(content().contentTypeCompatibleWith("text/markdown"))
                .andExpect(content().string(containsString("# PatchPilot Demo Handoff Share Delivery Receipt")))
                .andExpect(content().string(containsString("`READY`")));
    }

    @Test
    void should_return_not_found_when_demo_handoff_share_delivery_receipt_is_missing() throws Exception {
        when(demoHandoffShareDeliveryReceiptService.findReceipt("missing-receipt")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/demo/handoff-share-delivery-receipts/missing-receipt/report/download"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_download_archived_demo_handoff_package_as_markdown_attachment() throws Exception {
        when(demoHandoffPackageArchiveService.findArchive("handoff-archive-1")).thenReturn(Optional.of(new DemoHandoffPackageArchiveVo(
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                DemoReadinessStatus.READY,
                "Demo session snapshot is ready.",
                DemoReadinessStatus.READY,
                "Handoff package has current webhook delivery, PR, command, outcome, and readiness trend evidence.",
                "No missing handoff evidence.",
                7,
                0,
                0,
                "Status READY; recent PR https://github.com/bingqin2/PatchPilot/pull/42.",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                Instant.parse("2026-06-24T04:00:00Z"),
                "# PatchPilot Demo Handoff Package\n\n- Status: `READY`"
        )));

        mockMvc.perform(get("/api/demo/handoff-package-archives/handoff-archive-1/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment;")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-demo-handoff-package-handoff-archive-1.md")))
                .andExpect(content().contentTypeCompatibleWith("text/markdown"))
                .andExpect(content().string(containsString("# PatchPilot Demo Handoff Package")))
                .andExpect(content().string(containsString("`READY`")));
    }

    @Test
    void should_return_not_found_when_archived_demo_handoff_package_is_missing() throws Exception {
        when(demoHandoffPackageArchiveService.findArchive("missing-archive")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/demo/handoff-package-archives/missing-archive/report/download"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_archive_current_demo_readiness_snapshot() throws Exception {
        when(demoReadinessSnapshotArchiveService.archiveCurrentReadiness()).thenReturn(new DemoReadinessSnapshotArchiveVo(
                "readiness-snapshot-1",
                DemoReadinessStatus.BLOCKED,
                "PatchPilot is blocked before a live demo.",
                1,
                1,
                1,
                Instant.parse("2026-06-27T04:00:00Z"),
                "# PatchPilot Demo Readiness Snapshot\n\n- Status: `BLOCKED`"
        ));

        mockMvc.perform(post("/api/demo/readiness-snapshots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("readiness-snapshot-1"))
                .andExpect(jsonPath("$.data.status").value("BLOCKED"))
                .andExpect(jsonPath("$.data.readyCheckCount").value(1))
                .andExpect(jsonPath("$.data.needsAttentionCheckCount").value(1))
                .andExpect(jsonPath("$.data.blockedCheckCount").value(1))
                .andExpect(jsonPath("$.data.report").value(org.hamcrest.Matchers.containsString("# PatchPilot Demo Readiness Snapshot")));

        verify(operatorSafetyAuditService).recordSafetyAudit(argThat(this::isDemoReadinessSnapshotArchiveAudit));
    }

    private boolean isDemoReadinessSnapshotArchiveAudit(RecordOperatorSafetyAuditCommand command) {
        return command != null
                && "DEMO_READINESS_SNAPSHOT_ARCHIVED".equals(command.action())
                && "DEMO_READINESS_SNAPSHOT_ARCHIVE".equals(command.resourceType())
                && "readiness-snapshot-1".equals(command.resourceId())
                && command.scope() == TriggerQuarantineScope.REPOSITORY
                && "patchpilot/local-demo".equals(command.scopeKey())
                && "admin-api".equals(command.operator())
                && "Archived demo readiness snapshot BLOCKED".equals(command.reason());
    }

    @Test
    void should_return_recent_demo_readiness_snapshot_archives() throws Exception {
        when(demoReadinessSnapshotArchiveService.listRecentArchives()).thenReturn(List.of(new DemoReadinessSnapshotArchiveVo(
                "readiness-snapshot-1",
                DemoReadinessStatus.NEEDS_ATTENTION,
                "PatchPilot needs attention before a live demo.",
                7,
                2,
                0,
                Instant.parse("2026-06-27T04:00:00Z"),
                "# PatchPilot Demo Readiness Snapshot\n\n- Status: `NEEDS_ATTENTION`"
        )));

        mockMvc.perform(get("/api/demo/readiness-snapshots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("readiness-snapshot-1"))
                .andExpect(jsonPath("$.data[0].status").value("NEEDS_ATTENTION"))
                .andExpect(jsonPath("$.data[0].summary").value("PatchPilot needs attention before a live demo."))
                .andExpect(jsonPath("$.data[0].readyCheckCount").value(7));
    }

    @Test
    void should_download_archived_demo_readiness_snapshot_report_as_markdown_attachment() throws Exception {
        when(demoReadinessSnapshotArchiveService.findArchive("readiness-snapshot-1")).thenReturn(Optional.of(new DemoReadinessSnapshotArchiveVo(
                "readiness-snapshot-1",
                DemoReadinessStatus.READY,
                "PatchPilot is ready for a controlled demo.",
                9,
                0,
                0,
                Instant.parse("2026-06-27T04:00:00Z"),
                "# PatchPilot Demo Readiness Snapshot\n\n- Status: `READY`"
        )));

        mockMvc.perform(get("/api/demo/readiness-snapshots/readiness-snapshot-1/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment;")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-demo-readiness-readiness-snapshot-1.md")))
                .andExpect(content().contentTypeCompatibleWith("text/markdown"))
                .andExpect(content().string(containsString("# PatchPilot Demo Readiness Snapshot")))
                .andExpect(content().string(containsString("`READY`")));
    }

    @Test
    void should_return_not_found_when_archived_demo_readiness_snapshot_report_is_missing() throws Exception {
        when(demoReadinessSnapshotArchiveService.findArchive("missing-snapshot")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/demo/readiness-snapshots/missing-snapshot/report/download"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_demo_readiness_snapshot_trend_summary() throws Exception {
        when(demoReadinessSnapshotTrendService.getTrendSummary()).thenReturn(new DemoReadinessSnapshotTrendVo(
                DemoReadinessSnapshotTrendStatus.IMPROVING,
                "Demo readiness improved from BLOCKED to READY.",
                "readiness-snapshot-new",
                "readiness-snapshot-old",
                DemoReadinessStatus.READY,
                DemoReadinessStatus.BLOCKED,
                4,
                -2,
                -2,
                "Use the latest readiness snapshot as demo evidence or archive one more snapshot immediately before the live run.",
                "# PatchPilot Demo Readiness Snapshot Trend\n\n- Status: `IMPROVING`"
        ));

        mockMvc.perform(get("/api/demo/readiness-snapshots/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("IMPROVING"))
                .andExpect(jsonPath("$.data.summary").value("Demo readiness improved from BLOCKED to READY."))
                .andExpect(jsonPath("$.data.latestSnapshotId").value("readiness-snapshot-new"))
                .andExpect(jsonPath("$.data.previousSnapshotId").value("readiness-snapshot-old"))
                .andExpect(jsonPath("$.data.latestReadinessStatus").value("READY"))
                .andExpect(jsonPath("$.data.previousReadinessStatus").value("BLOCKED"))
                .andExpect(jsonPath("$.data.readyCheckDelta").value(4))
                .andExpect(jsonPath("$.data.needsAttentionCheckDelta").value(-2))
                .andExpect(jsonPath("$.data.blockedCheckDelta").value(-2))
                .andExpect(jsonPath("$.data.nextAction").value("Use the latest readiness snapshot as demo evidence or archive one more snapshot immediately before the live run."))
                .andExpect(jsonPath("$.data.markdownReport").value(org.hamcrest.Matchers.containsString("# PatchPilot Demo Readiness Snapshot Trend")));
    }

    private static DemoSelfHostedLaunchReadinessVo selfHostedLaunchReadiness() {
        return new DemoSelfHostedLaunchReadinessVo(
                DemoReadinessStatus.READY,
                true,
                "Self-hosted PatchPilot is ready for a controlled issue-to-PR launch.",
                List.of(
                        new DemoSelfHostedLaunchCheckVo(
                                "Demo readiness",
                                DemoReadinessStatus.READY,
                                "PatchPilot is ready for a controlled demo.",
                                "No action needed."
                        ),
                        new DemoSelfHostedLaunchCheckVo(
                                "Evidence bundle",
                                DemoReadinessStatus.READY,
                                "Demo evidence bundle is ready.",
                                "No action needed."
                        )
                ),
                List.of("Post the tested /agent fix comment, watch the task reach COMPLETED, then use the generated Pull Request for review."),
                Instant.parse("2026-06-27T01:00:00Z"),
                "# PatchPilot Self-Hosted Launch Readiness\n\n- Status: `READY`\n- Ready to launch: `true`\n"
        );
    }

    private static DemoSelfHostedLaunchReadinessArchiveVo selfHostedLaunchReadinessArchive() {
        return new DemoSelfHostedLaunchReadinessArchiveVo(
                "launch-readiness-archive-1",
                DemoReadinessStatus.READY,
                true,
                "Self-hosted PatchPilot is ready for a controlled issue-to-PR launch.",
                2,
                0,
                0,
                Instant.parse("2026-06-28T01:30:00Z"),
                "# PatchPilot Self-Hosted Launch Readiness\n\n- Status: `READY`\n"
        );
    }

    private static DemoLaunchEvidencePackageVo launchEvidencePackage() {
        return new DemoLaunchEvidencePackageVo(
                DemoReadinessStatus.READY,
                true,
                "PatchPilot launch evidence package is ready to share.",
                "demo-session-20260624T003000Z",
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                true,
                "final-handoff-report-package-archive-1",
                "Latest final handoff report package archive is download-ready and ready.",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "delivery-1",
                "evaluation-run-2",
                List.of("java", "python", "maven", "pytest"),
                List.of(
                        new DemoSelfHostedLaunchCheckVo(
                                "Demo readiness",
                                DemoReadinessStatus.READY,
                                "PatchPilot is ready for a controlled demo.",
                                "No action needed."
                        )
                ),
                List.of(
                        "Recent task task-1 reached COMPLETED.",
                        "Recent Pull Request https://github.com/bingqin2/PatchPilot/pull/42 is available.",
                        "Latest webhook delivery delivery-1 created task task-1."
                ),
                List.of(
                        "Handoff finalization is READY.",
                        "Latest delivery receipt delivery-receipt-1 is fresh."
                ),
                List.of("Post the tested /agent fix comment, watch the task reach COMPLETED, then use the generated Pull Request for review."),
                List.of(
                        "GET /api/demo/launch-evidence-package is read-only: it does not create tasks, call the model, run tests, archive records, mutate Git, send messages, or write to GitHub."
                ),
                "# PatchPilot Demo Launch Evidence Package\n\nRecent Pull Request https://github.com/bingqin2/PatchPilot/pull/42 is available.",
                Instant.parse("2026-06-28T02:00:00Z")
        );
    }

    private static DemoLaunchEvidencePackageArchiveVo launchEvidencePackageArchive() {
        return new DemoLaunchEvidencePackageArchiveVo(
                "launch-evidence-archive-1",
                DemoReadinessStatus.READY,
                true,
                "PatchPilot launch evidence package is ready to share.",
                "demo-session-20260624T003000Z",
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                true,
                "final-handoff-report-package-archive-1",
                "Latest final handoff report package archive is download-ready and ready.",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "delivery-1",
                "evaluation-run-2",
                Instant.parse("2026-06-28T02:30:00Z"),
                "# PatchPilot Demo Launch Evidence Package\n\n- Status: `READY`"
        );
    }

    private static DemoLaunchEvidenceShareCenterVo launchEvidenceShareCenter() {
        return new DemoLaunchEvidenceShareCenterVo(
                "READY",
                true,
                "Latest archived launch evidence package is READY and can be shared.",
                "Download the archived launch evidence package and share it with reviewers.",
                1,
                "launch-evidence-archive-1",
                "demo-session-20260624T003000Z",
                "2026-06-28T02:30:00Z",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "delivery-1",
                "evaluation-run-2",
                DemoReadinessStatus.READY,
                true,
                "final-handoff-report-package-archive-1",
                "launch-delivery-receipt-1",
                "Demo reviewer",
                "email",
                "2026-06-28T06:05:00Z",
                true,
                "FRESH",
                true,
                "Latest delivery receipt matches the current launch evidence archive and session.",
                List.of(
                        "Download launch evidence package archive launch-evidence-archive-1.",
                        "Download launch evidence share center report.",
                        "Download launch evidence delivery receipt launch-delivery-receipt-1."
                ),
                List.of(
                        "Latest launch evidence archive status is READY.",
                        "Latest delivery receipt launch-delivery-receipt-1 was recorded for Demo reviewer via email.",
                        "Latest Pull Request https://github.com/bingqin2/PatchPilot/pull/42 is ready for review."
                ),
                "# PatchPilot Demo Launch Evidence Share Center\n\n- Status: `READY`\n- Latest archive: `launch-evidence-archive-1`",
                Instant.parse("2026-06-28T02:45:00Z")
        );
    }

    private static DemoLaunchEvidenceShareDeliveryReceiptVo launchEvidenceDeliveryReceipt() {
        return new DemoLaunchEvidenceShareDeliveryReceiptVo(
                "launch-delivery-receipt-1",
                "READY",
                "launch-evidence-archive-1",
                "demo-session-20260624T003000Z",
                "email",
                "reviewer@example.com",
                "local-operator",
                "Sent final launch evidence after the smoke demo.",
                "PatchPilot demo launch evidence: demo-session-20260624T003000Z",
                Instant.parse("2026-06-28T06:05:00Z"),
                Instant.parse("2026-06-28T06:10:00Z"),
                "# PatchPilot Demo Launch Evidence Delivery Receipt\n\n- Status: `READY`"
        );
    }

    private static DemoLaunchEvidenceFinalizationVo launchEvidenceFinalization() {
        return new DemoLaunchEvidenceFinalizationVo(
                DemoReadinessStatus.READY,
                true,
                "Demo launch evidence is finalized with a fresh delivery receipt for the current archive.",
                "Use the finalization report as the launch evidence delivery acceptance record.",
                "launch-evidence-archive-1",
                "demo-session-20260624T003000Z",
                "launch-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "2026-06-28T06:05:00Z",
                "FRESH",
                true,
                "Latest delivery receipt matches the current launch evidence archive and session.",
                List.of(
                        new DemoLaunchEvidenceFinalizationCheckVo(
                                "Launch evidence share readiness",
                                DemoReadinessStatus.READY,
                                "Latest archived launch evidence package is READY and can be shared.",
                                "No action needed."
                        ),
                        new DemoLaunchEvidenceFinalizationCheckVo(
                                "Delivery receipt freshness",
                                DemoReadinessStatus.READY,
                                "Latest delivery receipt matches the current launch evidence archive and session.",
                                "No action needed."
                        ),
                        new DemoLaunchEvidenceFinalizationCheckVo(
                                "Launch acceptance evidence",
                                DemoReadinessStatus.READY,
                                "Finalization report is ready as the launch acceptance record.",
                                "Download the finalization report."
                        )
                ),
                List.of("Latest delivery receipt launch-delivery-receipt-1 is fresh."),
                "# PatchPilot Demo Launch Evidence Finalization Gate\n\n- Status: `READY`",
                Instant.parse("2026-06-28T06:30:00Z")
        );
    }

    private static DemoLaunchAcceptanceCloseoutVo launchAcceptanceCloseout() {
        return new DemoLaunchAcceptanceCloseoutVo(
                DemoReadinessStatus.READY,
                true,
                "PatchPilot launch acceptance closeout is complete.",
                "Use this closeout report as the final self-hosted launch acceptance record.",
                "demo-session-20260624T003000Z",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "delivery-1",
                "evaluation-run-2",
                "launch-evidence-archive-1",
                DemoReadinessStatus.READY,
                true,
                "final-handoff-report-package-archive-1",
                "Latest final handoff report package archive is download-ready and ready.",
                "launch-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "2026-06-28T06:05:00Z",
                "FRESH",
                Instant.parse("2026-06-28T07:15:00Z"),
                List.of(
                        new DemoLaunchAcceptanceCloseoutCheckVo(
                                "Self-hosted launch readiness",
                                DemoReadinessStatus.READY,
                                "Self-hosted PatchPilot is ready for a controlled issue-to-PR launch.",
                                "No action needed."
                        )
                ),
                List.of("Delivery receipt launch-delivery-receipt-1 is fresh for demo-session-20260624T003000Z."),
                List.of(
                        "Download self-hosted launch readiness report.",
                        "Download launch evidence package report.",
                        "Download launch evidence share center report.",
                        "Download launch evidence finalization report.",
                        "Download final handoff report package archive final-handoff-report-package-archive-1.",
                        "Download launch acceptance closeout report."
                ),
                "# PatchPilot Launch Acceptance Closeout\n\n- Final handoff archive: `final-handoff-report-package-archive-1`\n- Receipt: `launch-delivery-receipt-1`"
        );
    }

    private static DemoLaunchAcceptanceCloseoutArchiveVo launchAcceptanceCloseoutArchive() {
        return new DemoLaunchAcceptanceCloseoutArchiveVo(
                "launch-closeout-archive-1",
                DemoReadinessStatus.READY,
                true,
                "PatchPilot launch acceptance closeout is complete.",
                "demo-session-20260624T003000Z",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "delivery-1",
                "evaluation-run-2",
                "launch-evidence-archive-1",
                DemoReadinessStatus.READY,
                true,
                "final-handoff-report-package-archive-1",
                "Latest final handoff report package archive is download-ready and ready.",
                "launch-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "FRESH",
                Instant.parse("2026-06-28T08:30:00Z"),
                "# PatchPilot Launch Acceptance Closeout\n\n- Receipt: `launch-delivery-receipt-1`"
        );
    }

    private static DemoLaunchAcceptanceCertificateVo launchAcceptanceCertificate() {
        return new DemoLaunchAcceptanceCertificateVo(
                DemoReadinessStatus.READY,
                true,
                "PatchPilot launch acceptance is certified from the latest accepted closeout archive.",
                "Share the certificate and archived closeout report with reviewers.",
                1,
                "launch-closeout-archive-1",
                "launch-evidence-archive-1",
                DemoReadinessStatus.READY,
                true,
                "final-handoff-report-package-archive-1",
                "Latest final handoff report package archive is download-ready and ready.",
                "launch-delivery-receipt-1",
                "demo-session-20260624T003000Z",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "delivery-1",
                "evaluation-run-2",
                "reviewer@example.com",
                "email",
                "FRESH",
                Instant.parse("2026-06-28T08:30:00Z"),
                Instant.parse("2026-06-28T09:00:00Z"),
                List.of(
                        "Download launch acceptance certificate.",
                        "Download launch acceptance closeout archive launch-closeout-archive-1.",
                        "Download final handoff report package archive final-handoff-report-package-archive-1."
                ),
                "# PatchPilot Launch Acceptance Certificate\n\n"
                        + "- Closeout archive: `launch-closeout-archive-1`\n"
                        + "- Final handoff archive: `final-handoff-report-package-archive-1`\n"
        );
    }

    private static DemoLaunchAcceptanceCertificateArchiveVo launchAcceptanceCertificateArchive() {
        return new DemoLaunchAcceptanceCertificateArchiveVo(
                "launch-certificate-archive-1",
                DemoReadinessStatus.READY,
                true,
                "PatchPilot launch acceptance is certified from the latest accepted closeout archive.",
                "Share the certificate and archived closeout report with reviewers.",
                1,
                "launch-closeout-archive-1",
                "launch-evidence-archive-1",
                DemoReadinessStatus.READY,
                true,
                "final-handoff-report-package-archive-1",
                "Latest final handoff report package archive is download-ready and ready.",
                "launch-delivery-receipt-1",
                "demo-session-20260624T003000Z",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "delivery-1",
                "evaluation-run-2",
                "reviewer@example.com",
                "email",
                "FRESH",
                Instant.parse("2026-06-28T08:30:00Z"),
                Instant.parse("2026-06-28T09:00:00Z"),
                Instant.parse("2026-06-28T10:30:00Z"),
                List.of(
                        "Download launch acceptance certificate.",
                        "Download launch acceptance closeout archive launch-closeout-archive-1.",
                        "Download final handoff report package archive final-handoff-report-package-archive-1."
                ),
                "# PatchPilot Launch Acceptance Certificate\n\n"
                        + "- Closeout archive: `launch-closeout-archive-1`\n"
                        + "- Final handoff archive: `final-handoff-report-package-archive-1`\n"
        );
    }

    private static DemoFinalHandoffReportPackageArchiveVo finalHandoffReportPackageArchive() {
        return new DemoFinalHandoffReportPackageArchiveVo(
                "final-handoff-package-archive-1",
                DemoReadinessStatus.READY,
                true,
                "Final demo handoff report package is ready to deliver.",
                "Download this final handoff report package and attach the listed evidence files.",
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                "delivery-receipt-1",
                "task-evidence-certificate-archive-1",
                true,
                List.of("Finalization: READY"),
                List.of("Finalization report"),
                List.of("Confirm no handoff share checklist warnings remain."),
                List.of("Latest delivery receipt delivery-receipt-1 is fresh."),
                List.of("Handoff finalization"),
                "# PatchPilot Final Demo Handoff Report Package\n\n- Latest archive: `handoff-archive-1`\n",
                Instant.parse("2026-06-28T11:00:00Z"),
                Instant.parse("2026-06-28T11:30:00Z")
        );
    }

    private static DemoAcceptanceSummaryVo acceptanceSummary() {
        return new DemoAcceptanceSummaryVo(
                DemoReadinessStatus.READY,
                true,
                "PatchPilot final demo acceptance is ready for external review.",
                "Share the launch and task evidence certificates with reviewers.",
                DemoReadinessStatus.READY,
                true,
                true,
                "launch-certificate-archive-1",
                "launch-closeout-archive-1",
                "launch-evidence-archive-1",
                "launch-delivery-receipt-1",
                DemoReadinessStatus.READY,
                true,
                true,
                "task-evidence-certificate-archive-1",
                "task-evidence-closeout-archive-1",
                "task-evidence-archive-1",
                "task-evidence-receipt-1",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                Instant.parse("2026-06-28T14:00:00Z"),
                List.of(
                        new DemoAcceptanceSummaryVo.Check(
                                "Launch acceptance certificate",
                                DemoReadinessStatus.READY,
                                "Latest launch acceptance certificate archive is certified.",
                                "Use the archived launch acceptance certificate for launch-level review proof."
                        ),
                        new DemoAcceptanceSummaryVo.Check(
                                "Task evidence acceptance certificate",
                                DemoReadinessStatus.READY,
                                "Latest task evidence acceptance certificate archive is certified.",
                                "Use the archived task evidence acceptance certificate for task-level review proof."
                        )
                ),
                List.of(
                        "Launch certificate archive launch-certificate-archive-1 is certified.",
                        "Task evidence certificate archive task-evidence-certificate-archive-1 is certified."
                ),
                List.of(
                        "Download launch acceptance certificate archive launch-certificate-archive-1.",
                        "Download task evidence acceptance certificate archive task-evidence-certificate-archive-1."
                ),
                "GET /api/demo/acceptance-summary is read-only: it does not create tasks, call the model, run tests, archive records, mutate Git, send messages, record receipts, or write to GitHub.",
                "# PatchPilot Final Demo Acceptance Summary\n\n- Accepted: `true`\n"
        );
    }

    private static DemoFinalAcceptanceSharePackageVo finalAcceptanceSharePackage() {
        return new DemoFinalAcceptanceSharePackageVo(
                DemoReadinessStatus.READY,
                true,
                "PatchPilot final demo acceptance package is ready to send.",
                "Send the prepared final acceptance message with all required attachments.",
                "launch-certificate-archive-1",
                "task-evidence-certificate-archive-1",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                List.of("Repository owner or maintainer", "Demo reviewer"),
                List.of(
                        "Final demo acceptance summary report",
                        "Launch acceptance certificate archive launch-certificate-archive-1",
                        "Task evidence acceptance certificate archive task-evidence-certificate-archive-1",
                        "Pull Request https://github.com/bingqin2/PatchPilot/pull/42"
                ),
                List.of(
                        "Confirm final demo acceptance status is READY and accepted.",
                        "Confirm launch acceptance certificate archive launch-certificate-archive-1 is attached.",
                        "Confirm task evidence acceptance certificate archive task-evidence-certificate-archive-1 is attached.",
                        "Confirm Pull Request https://github.com/bingqin2/PatchPilot/pull/42 opens correctly."
                ),
                "PatchPilot final demo acceptance: task-1",
                "PatchPilot final demo acceptance is ready for external review.",
                List.of("Final acceptance status is READY."),
                "GET /api/demo/final-acceptance-share-package is read-only: it does not create tasks, call the model, run tests, mutate Git, archive records, record receipts, send messages, or write to GitHub.",
                "# PatchPilot Final Demo Acceptance Share Package\n\nSubject: PatchPilot final demo acceptance: task-1\n",
                Instant.parse("2026-06-28T15:00:00Z")
        );
    }

    private static DemoFinalAcceptanceSharePackageArchiveVo finalAcceptanceSharePackageArchive() {
        return new DemoFinalAcceptanceSharePackageArchiveVo(
                "final-acceptance-share-package-archive-1",
                DemoReadinessStatus.READY,
                true,
                "PatchPilot final demo acceptance package is ready to send.",
                "Send the prepared final acceptance message with all required attachments.",
                "launch-certificate-archive-1",
                "task-evidence-certificate-archive-1",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                List.of("Repository owner or maintainer", "Demo reviewer"),
                List.of(
                        "Final demo acceptance summary report",
                        "Launch acceptance certificate archive launch-certificate-archive-1",
                        "Task evidence acceptance certificate archive task-evidence-certificate-archive-1",
                        "Pull Request https://github.com/bingqin2/PatchPilot/pull/42"
                ),
                List.of("Confirm final demo acceptance status is READY and accepted."),
                "PatchPilot final demo acceptance: task-1",
                "PatchPilot final demo acceptance is ready for external review.",
                List.of("Final acceptance status is READY."),
                "POST /api/demo/final-acceptance-share-package/archives archives a read-only snapshot and does not create tasks, call the model, run tests, mutate Git, send messages, or write to GitHub.",
                "# PatchPilot Final Demo Acceptance Share Package\n\nSubject: PatchPilot final demo acceptance: task-1\n",
                Instant.parse("2026-06-29T01:30:00Z"),
                Instant.parse("2026-06-29T02:00:00Z")
        );
    }

    private static DemoFinalAcceptanceShareDeliveryReceiptVo finalAcceptanceShareDeliveryReceipt() {
        return new DemoFinalAcceptanceShareDeliveryReceiptVo(
                "final-acceptance-delivery-receipt-1",
                DemoReadinessStatus.READY,
                "final-acceptance-share-package-archive-1",
                "task-1",
                "email",
                "reviewer@example.com",
                "local-operator",
                "Sent final acceptance share package to the reviewer.",
                "PatchPilot final demo acceptance: task-1",
                Instant.parse("2026-06-29T03:05:00Z"),
                Instant.parse("2026-06-29T03:10:00Z"),
                "# PatchPilot Final Demo Acceptance Share Delivery Receipt\n\n- Delivery target: `reviewer@example.com`"
        );
    }

    private static DemoFinalAcceptanceShareFinalizationVo finalAcceptanceShareFinalization() {
        return new DemoFinalAcceptanceShareFinalizationVo(
                DemoReadinessStatus.READY,
                true,
                "Final demo acceptance share package is finalized with a fresh delivery receipt.",
                "Use the finalization report as the external-review acceptance delivery record.",
                "final-acceptance-share-package-archive-1",
                "task-1",
                "final-acceptance-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "2026-06-29T03:05:00Z",
                "FRESH",
                true,
                "Latest delivery receipt matches the current final acceptance share package archive.",
                List.of(
                        new DemoFinalAcceptanceShareFinalizationVo.Check(
                                "Final acceptance package archive",
                                DemoReadinessStatus.READY,
                                "Latest final acceptance share package archive is send-ready.",
                                "No action needed."
                        ),
                        new DemoFinalAcceptanceShareFinalizationVo.Check(
                                "Delivery receipt freshness",
                                DemoReadinessStatus.READY,
                                "Latest delivery receipt matches the current final acceptance share package archive.",
                                "No action needed."
                        ),
                        new DemoFinalAcceptanceShareFinalizationVo.Check(
                                "Final acceptance delivery evidence",
                                DemoReadinessStatus.READY,
                                "Finalization report is ready as the external-review acceptance record.",
                                "Download the finalization report."
                        )
                ),
                List.of(
                        "Latest final acceptance archive final-acceptance-share-package-archive-1 is send-ready.",
                        "Latest delivery receipt final-acceptance-delivery-receipt-1 is fresh for final-acceptance-share-package-archive-1."
                ),
                "# PatchPilot Final Demo Acceptance Share Finalization Gate\n\n- Latest delivery receipt: `final-acceptance-delivery-receipt-1`",
                Instant.parse("2026-06-29T03:30:00Z")
        );
    }

    private static DemoFinalAcceptanceCompletionArchiveVo finalAcceptanceCompletionArchive() {
        return new DemoFinalAcceptanceCompletionArchiveVo(
                "final-acceptance-completion-archive-1",
                DemoReadinessStatus.READY,
                true,
                "Final demo acceptance share package is finalized with a fresh delivery receipt.",
                "Use the finalization report as the external-review acceptance delivery record.",
                "final-acceptance-share-package-archive-1",
                "task-1",
                "final-acceptance-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "2026-06-29T03:05:00Z",
                "FRESH",
                true,
                "Latest delivery receipt matches the current final acceptance share package archive.",
                List.of(
                        "Latest final acceptance archive final-acceptance-share-package-archive-1 is send-ready.",
                        "Latest delivery receipt final-acceptance-delivery-receipt-1 is fresh for final-acceptance-share-package-archive-1."
                ),
                "# PatchPilot Final Demo Acceptance Share Finalization Gate\n\n- Latest delivery receipt: `final-acceptance-delivery-receipt-1`",
                Instant.parse("2026-06-29T03:30:00Z"),
                Instant.parse("2026-06-29T04:00:00Z")
        );
    }

    private static DemoFinalAcceptanceCompletionEvidenceBundleVo finalAcceptanceCompletionEvidenceBundle() {
        return new DemoFinalAcceptanceCompletionEvidenceBundleVo(
                DemoReadinessStatus.READY,
                true,
                "PatchPilot final acceptance completion evidence bundle is ready to share.",
                "Share this final acceptance completion evidence bundle with reviewers.",
                "final-acceptance-completion-archive-1",
                "final-acceptance-share-package-archive-1",
                "final-acceptance-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "task-1",
                1,
                Instant.parse("2026-06-29T04:00:00Z"),
                Instant.parse("2026-06-29T04:05:00Z"),
                List.of(
                        "Latest completion archive final-acceptance-completion-archive-1 is finalized.",
                        "Latest delivery receipt final-acceptance-delivery-receipt-1 is fresh for final-acceptance-share-package-archive-1."
                ),
                List.of(
                        "Download final acceptance completion evidence bundle.",
                        "Download final acceptance completion archive final-acceptance-completion-archive-1.",
                        "Download final acceptance share finalization report."
                ),
                "GET /api/demo/final-acceptance-completion-evidence-bundle is read-only: it does not create tasks, call the model, run tests, archive records, record receipts, mutate Git, send messages, or write to GitHub.",
                "# PatchPilot Final Acceptance Completion Evidence Bundle\n\n- Latest completion archive: `final-acceptance-completion-archive-1`\n"
        );
    }

    private static DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo finalAcceptanceCompletionEvidenceDeliveryReceipt() {
        return new DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo(
                "final-acceptance-completion-evidence-delivery-receipt-1",
                DemoReadinessStatus.READY,
                true,
                DemoReadinessStatus.READY,
                "PatchPilot final acceptance completion evidence bundle is ready to share.",
                "Share this final acceptance completion evidence bundle with reviewers.",
                "final-acceptance-completion-archive-1",
                "final-acceptance-share-package-archive-1",
                "final-acceptance-delivery-receipt-1",
                "task-1",
                "email",
                "reviewer@example.com",
                "local-operator",
                "Sent final completion evidence bundle to the reviewer.",
                Instant.parse("2026-06-29T04:25:00Z"),
                Instant.parse("2026-06-29T04:30:00Z"),
                "# PatchPilot Final Acceptance Completion Evidence Delivery Receipt\n\n- Delivery target: `reviewer@example.com`"
        );
    }

    private static DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo finalAcceptanceCompletionEvidenceDeliveryFinalization() {
        return new DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo(
                DemoReadinessStatus.READY,
                true,
                "Final acceptance completion evidence delivery is finalized with a fresh delivery receipt.",
                "Use the finalization report as the reviewer-facing completion delivery record.",
                "final-acceptance-completion-archive-1",
                "final-acceptance-share-package-archive-1",
                "final-acceptance-delivery-receipt-1",
                "task-1",
                "final-acceptance-completion-evidence-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "2026-06-29T04:25:00Z",
                "FRESH",
                true,
                "Latest completion evidence delivery receipt matches the current completion evidence bundle.",
                List.of(new DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo.Check(
                        "Completion evidence bundle",
                        DemoReadinessStatus.READY,
                        "Completion evidence bundle is ready to share.",
                        "No action needed."
                )),
                List.of("Completion evidence delivery receipt final-acceptance-completion-evidence-delivery-receipt-1 is fresh."),
                List.of("Download final acceptance completion evidence delivery finalization report."),
                "GET /api/demo/final-acceptance-completion-evidence-delivery-finalization is read-only.",
                "# PatchPilot Final Acceptance Completion Evidence Delivery Finalization\n\n"
                        + "- Latest completion evidence delivery receipt: `final-acceptance-completion-evidence-delivery-receipt-1`\n"
                        + "- Delivery receipt freshness: `FRESH`\n",
                Instant.parse("2026-06-29T05:00:00Z")
        );
    }

    private static DemoFinalAcceptanceCompletionCloseoutVo finalAcceptanceCompletionCloseout() {
        return new DemoFinalAcceptanceCompletionCloseoutVo(
                DemoReadinessStatus.READY,
                true,
                "PatchPilot final acceptance completion is closed with accepted certificates, finalized sharing, and fresh completion delivery proof.",
                "Use this closeout report as the final external-review completion record.",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/8",
                "final-acceptance-share-package-archive-1",
                "final-acceptance-completion-archive-1",
                "final-acceptance-completion-evidence-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "2026-06-29T04:25:00Z",
                "FRESH",
                List.of(new DemoFinalAcceptanceCompletionCloseoutVo.Check(
                        "Final acceptance summary",
                        DemoReadinessStatus.READY,
                        "Final demo acceptance summary is accepted.",
                        "No action needed."
                )),
                List.of("Final demo acceptance summary is accepted."),
                List.of("Download final acceptance completion closeout report."),
                "GET /api/demo/final-acceptance-completion-closeout is read-only.",
                "# PatchPilot Final Acceptance Completion Closeout\n\n"
                        + "- Latest completion evidence delivery receipt: `final-acceptance-completion-evidence-delivery-receipt-1`\n",
                Instant.parse("2026-06-29T06:00:00Z")
        );
    }

    private static DemoFinalAcceptanceCompletionCloseoutArchiveVo finalAcceptanceCompletionCloseoutArchive() {
        return new DemoFinalAcceptanceCompletionCloseoutArchiveVo(
                "final-acceptance-completion-closeout-archive-1",
                DemoReadinessStatus.READY,
                true,
                "PatchPilot final acceptance completion is closed with accepted certificates, finalized sharing, and fresh completion delivery proof.",
                "Use this closeout report as the final external-review completion record.",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/8",
                "final-acceptance-share-package-archive-1",
                "final-acceptance-completion-archive-1",
                "final-acceptance-completion-evidence-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "2026-06-29T04:25:00Z",
                "FRESH",
                List.of("Final demo acceptance summary is accepted."),
                List.of("Download final acceptance completion closeout report."),
                "GET /api/demo/final-acceptance-completion-closeout is read-only.",
                "# PatchPilot Final Acceptance Completion Closeout\n\n"
                        + "- Latest completion evidence delivery receipt: `final-acceptance-completion-evidence-delivery-receipt-1`\n",
                Instant.parse("2026-06-29T06:00:00Z"),
                Instant.parse("2026-06-29T06:30:00Z")
        );
    }
}
