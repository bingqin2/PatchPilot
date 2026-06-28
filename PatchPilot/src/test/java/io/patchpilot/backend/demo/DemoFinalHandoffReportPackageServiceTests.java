package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalHandoffReportPackageVo;
import io.patchpilot.backend.demo.domain.DemoHandoffFinalizationCheckVo;
import io.patchpilot.backend.demo.domain.DemoHandoffFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveSummaryVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareCenterVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareChecklistItemVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareChecklistVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareInstructionsVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DemoFinalHandoffReportPackageServiceTests {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-06-24T07:00:00Z"), ZoneOffset.UTC);

    private final DemoHandoffPackageArchiveService archiveService = mock(DemoHandoffPackageArchiveService.class);
    private final DemoHandoffShareChecklistService checklistService = mock(DemoHandoffShareChecklistService.class);
    private final DemoHandoffShareCenterService shareCenterService = mock(DemoHandoffShareCenterService.class);
    private final DemoHandoffFinalizationService finalizationService = mock(DemoHandoffFinalizationService.class);
    private final DemoFinalHandoffReportPackageService service = new DemoFinalHandoffReportPackageService(
            archiveService,
            checklistService,
            shareCenterService,
            finalizationService,
            CLOCK
    );

    @Test
    void should_build_final_handoff_report_package_from_current_handoff_evidence() {
        when(archiveService.getArchiveSummary()).thenReturn(archiveSummary());
        when(checklistService.getShareChecklist()).thenReturn(shareChecklist());
        when(shareCenterService.getShareCenter()).thenReturn(shareCenter());
        when(shareCenterService.getShareInstructions()).thenReturn(shareInstructions());
        when(finalizationService.getFinalizationGate()).thenReturn(finalization());

        DemoFinalHandoffReportPackageVo reportPackage = service.getReportPackage();

        assertThat(reportPackage.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(reportPackage.downloadReady()).isTrue();
        assertThat(reportPackage.summary())
                .isEqualTo("Final demo handoff report package is ready to deliver.");
        assertThat(reportPackage.nextAction())
                .isEqualTo("Download this final handoff report package and attach the listed evidence files.");
        assertThat(reportPackage.latestArchiveId()).isEqualTo("handoff-archive-1");
        assertThat(reportPackage.latestSessionId()).isEqualTo("demo-session-20260624T003000Z");
        assertThat(reportPackage.latestDeliveryReceiptId()).isEqualTo("receipt-1");
        assertThat(reportPackage.taskCertificateArchiveId()).isEqualTo("task-certificate-archive-1");
        assertThat(reportPackage.taskCertificateReady()).isTrue();
        assertThat(reportPackage.generatedAt()).isEqualTo(Instant.parse("2026-06-24T07:00:00Z"));
        assertThat(reportPackage.readinessChecks())
                .containsExactly(
                        "Archive summary: READY",
                        "Share checklist: READY",
                        "Share center: READY",
                        "Task evidence certificate: READY",
                        "Finalization: READY"
                );
        assertThat(reportPackage.requiredAttachments())
                .contains(
                        "Handoff package archive handoff-archive-1",
                        "Task evidence acceptance certificate archive task-certificate-archive-1",
                        "Finalization report"
                );
        assertThat(reportPackage.evidenceNotes())
                .contains(
                        "Latest delivery receipt receipt-1 is fresh for handoff-archive-1/demo-session-20260624T003000Z.",
                        "Task evidence acceptance certificate task-certificate-archive-1 is READY for task task-2."
                );
        assertThat(reportPackage.sourceReports())
                .contains("Handoff package archive summary", "Handoff share center", "Handoff finalization");
        assertThat(reportPackage.markdownReport())
                .contains("# PatchPilot Final Demo Handoff Report Package")
                .contains("- Status: `READY`")
                .contains("- Download ready: `true`")
                .contains("- Task certificate archive: `task-certificate-archive-1`")
                .contains("## Required Attachments")
                .contains("## Source Reports")
                .contains("GET /api/demo/final-handoff-report-package is read-only");
    }

    @Test
    void should_need_attention_when_finalization_is_not_ready() {
        when(archiveService.getArchiveSummary()).thenReturn(archiveSummary());
        when(checklistService.getShareChecklist()).thenReturn(shareChecklist());
        when(shareCenterService.getShareCenter()).thenReturn(shareCenter());
        when(shareCenterService.getShareInstructions()).thenReturn(shareInstructions());
        when(finalizationService.getFinalizationGate()).thenReturn(missingReceiptFinalization());

        DemoFinalHandoffReportPackageVo reportPackage = service.getReportPackage();

        assertThat(reportPackage.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(reportPackage.downloadReady()).isFalse();
        assertThat(reportPackage.summary())
                .isEqualTo("Final demo handoff report package needs current finalization evidence before delivery.");
        assertThat(reportPackage.nextAction())
                .isEqualTo("Send the current handoff package, record a delivery receipt, then download the finalization report.");
        assertThat(reportPackage.readinessChecks())
                .contains("Finalization: NEEDS_ATTENTION");
        assertThat(reportPackage.markdownReport())
                .contains("- Status: `NEEDS_ATTENTION`")
                .contains("- Download ready: `false`")
                .contains("Record a handoff share delivery receipt after sending the package.");
    }

    private static DemoHandoffPackageArchiveSummaryVo archiveSummary() {
        return new DemoHandoffPackageArchiveSummaryVo(
                "READY",
                true,
                2,
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                DemoReadinessStatus.READY,
                Instant.parse("2026-06-24T04:05:00Z"),
                "Latest archived handoff package is READY and can be shared.",
                "No missing handoff evidence.",
                "# PatchPilot Handoff Package Archive Summary"
        );
    }

    private static DemoHandoffShareChecklistVo shareChecklist() {
        return new DemoHandoffShareChecklistVo(
                DemoReadinessStatus.READY,
                "Latest handoff archive is ready to share.",
                "Share the latest handoff package summary and archived package with the reviewer.",
                List.of(new DemoHandoffShareChecklistItemVo(
                        "Handoff package archive",
                        DemoReadinessStatus.READY,
                        "1 archived handoff package is available.",
                        "Use archive handoff-archive-1 as the latest package."
                )),
                "# PatchPilot Demo Handoff Share Checklist",
                Instant.parse("2026-06-24T05:00:00Z")
        );
    }

    private static DemoHandoffShareCenterVo shareCenter() {
        return new DemoHandoffShareCenterVo(
                DemoReadinessStatus.READY,
                true,
                "Post-demo handoff package is ready to share.",
                "Download the package, archive summary, and share checklist before sending handoff evidence.",
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                "2026-06-24T04:05:00Z",
                "receipt-1",
                "maintainer@example.com",
                "email",
                "2026-06-24T06:05:00Z",
                true,
                "FRESH",
                true,
                "Latest delivery receipt matches the current handoff archive and session.",
                DemoReadinessStatus.READY,
                true,
                "Latest task evidence acceptance certificate archive is certified and ready.",
                "Use the archived task evidence acceptance certificate as task-level review proof.",
                "task-certificate-archive-1",
                "task-2",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                List.of(
                        "Download handoff package archive handoff-archive-1.",
                        "Download task evidence acceptance certificate archive task-certificate-archive-1.",
                        "Download handoff share delivery receipt receipt-1."
                ),
                List.of(
                        "Task evidence acceptance certificate task-certificate-archive-1 is READY for task task-2.",
                        "Latest delivery receipt receipt-1 was recorded for maintainer@example.com via email."
                ),
                "# PatchPilot Demo Handoff Share Center",
                Instant.parse("2026-06-24T05:30:00Z")
        );
    }

    private static DemoHandoffShareInstructionsVo shareInstructions() {
        return new DemoHandoffShareInstructionsVo(
                DemoReadinessStatus.READY,
                true,
                "Share the current handoff package with repository maintainers and demo reviewers.",
                "Send the prepared handoff message with all required attachments.",
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                List.of("Repository owner or maintainer", "Demo reviewer"),
                List.of(
                        "Handoff package archive handoff-archive-1",
                        "Task evidence acceptance certificate archive task-certificate-archive-1",
                        "Finalization report"
                ),
                List.of(
                        "Confirm task evidence acceptance certificate task-certificate-archive-1 is attached.",
                        "Confirm no handoff share checklist warnings remain."
                ),
                "PatchPilot demo handoff: demo-session-20260624T003000Z",
                "The PatchPilot demo handoff package is ready to share.",
                "# PatchPilot Demo Handoff Share Instructions",
                Instant.parse("2026-06-24T05:45:00Z")
        );
    }

    private static DemoHandoffFinalizationVo finalization() {
        return new DemoHandoffFinalizationVo(
                DemoReadinessStatus.READY,
                true,
                "Demo handoff is finalized with a fresh delivery receipt for the current archive.",
                "Use the finalization report as the post-demo delivery acceptance record.",
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                "receipt-1",
                "maintainer@example.com",
                "email",
                "2026-06-24T06:05:00Z",
                "FRESH",
                true,
                "Latest delivery receipt matches the current handoff archive and session.",
                List.of(new DemoHandoffFinalizationCheckVo(
                        "Final acceptance evidence",
                        DemoReadinessStatus.READY,
                        "Finalization report is ready as the acceptance record.",
                        "Download the finalization report."
                )),
                List.of(
                        "Latest delivery receipt receipt-1 is fresh for handoff-archive-1/demo-session-20260624T003000Z.",
                        "Finalization report can be downloaded as the acceptance record."
                ),
                "# PatchPilot Demo Handoff Finalization Gate",
                Instant.parse("2026-06-24T06:15:00Z")
        );
    }

    private static DemoHandoffFinalizationVo missingReceiptFinalization() {
        return new DemoHandoffFinalizationVo(
                DemoReadinessStatus.NEEDS_ATTENTION,
                false,
                "Demo handoff package is send-ready but final delivery evidence is not current.",
                "Send the current handoff package, record a delivery receipt, then download the finalization report.",
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                null,
                null,
                null,
                null,
                "MISSING",
                false,
                "No delivery receipt has been recorded for the current handoff package.",
                List.of(new DemoHandoffFinalizationCheckVo(
                        "Final acceptance evidence",
                        DemoReadinessStatus.NEEDS_ATTENTION,
                        "Finalization report is not yet acceptable as delivery evidence.",
                        "Record a handoff share delivery receipt after sending the package."
                )),
                List.of("No fresh delivery receipt is available for handoff-archive-1/demo-session-20260624T003000Z."),
                "# PatchPilot Demo Handoff Finalization Gate",
                Instant.parse("2026-06-24T06:15:00Z")
        );
    }
}
