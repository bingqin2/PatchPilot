package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveSummaryVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareCenterVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareChecklistItemVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareChecklistVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.DemoHandoffShareDeliveryReceiptRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DemoHandoffShareCenterServiceTests {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-06-24T05:30:00Z"), ZoneOffset.UTC);

    private final DemoHandoffPackageArchiveSummaryService archiveSummaryService = mock(DemoHandoffPackageArchiveSummaryService.class);
    private final DemoHandoffShareChecklistService shareChecklistService = mock(DemoHandoffShareChecklistService.class);
    private final DemoHandoffShareDeliveryReceiptRepository receiptRepository = mock(DemoHandoffShareDeliveryReceiptRepository.class);
    private final DemoHandoffShareCenterService service = new DemoHandoffShareCenterService(
            archiveSummaryService,
            shareChecklistService,
            receiptRepository,
            CLOCK
    );

    @Test
    void should_build_share_ready_center_from_archive_summary_and_checklist() {
        when(archiveSummaryService.getArchiveSummary()).thenReturn(readyArchiveSummary());
        when(shareChecklistService.getShareChecklist()).thenReturn(readyChecklist());
        when(receiptRepository.listRecentReceipts(1)).thenReturn(List.of(deliveryReceipt()));

        DemoHandoffShareCenterVo center = service.getShareCenter();

        assertThat(center.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(center.shareReady()).isTrue();
        assertThat(center.summary()).isEqualTo("Post-demo handoff package is ready to share.");
        assertThat(center.nextAction())
                .isEqualTo("Download the package, archive summary, and share checklist before sending handoff evidence.");
        assertThat(center.latestArchiveId()).isEqualTo("handoff-archive-1");
        assertThat(center.latestSessionId()).isEqualTo("demo-session-20260624T003000Z");
        assertThat(center.latestCreatedAt()).isEqualTo("2026-06-24T04:05:00Z");
        assertThat(center.latestDeliveryReceiptId()).isEqualTo("receipt-1");
        assertThat(center.latestDeliveryTarget()).isEqualTo("Demo reviewer");
        assertThat(center.latestDeliveryChannel()).isEqualTo("email");
        assertThat(center.latestDeliveredAt()).isEqualTo("2026-06-24T05:20:00Z");
        assertThat(center.deliveryReceiptRecorded()).isTrue();
        assertThat(center.generatedAt()).isEqualTo(Instant.parse("2026-06-24T05:30:00Z"));
        assertThat(center.downloadActions()).containsExactly(
                "Download handoff package archive handoff-archive-1.",
                "Download handoff package archive summary.",
                "Download handoff share checklist.",
                "Download handoff share delivery receipt receipt-1."
        );
        assertThat(center.evidenceNotes())
                .contains(
                        "Latest package archive status is READY.",
                        "Share checklist has 2 checks.",
                        "Latest delivery receipt receipt-1 was recorded for Demo reviewer via email.",
                        "Archive summary: Latest handoff package archive is ready to share.",
                        "Checklist summary: Latest handoff archive is ready to share."
                );
        assertThat(center.markdownReport())
                .contains("# PatchPilot Demo Handoff Share Center")
                .contains("- Status: `READY`")
                .contains("- Share ready: `true`")
                .contains("- Delivery receipt recorded: `true`")
                .contains("- Latest delivery receipt: `receipt-1`")
                .contains("- Delivery target: `Demo reviewer`")
                .contains("## Embedded Archive Summary")
                .contains("# Archive Summary")
                .contains("## Embedded Share Checklist")
                .contains("# Share Checklist")
                .contains("GET /api/demo/handoff-share-center is read-only");
    }

    @Test
    void should_block_sharing_when_no_archive_is_available_and_checklist_needs_attention() {
        when(archiveSummaryService.getArchiveSummary()).thenReturn(emptyArchiveSummary());
        when(shareChecklistService.getShareChecklist()).thenReturn(checklistNeedingAttention());
        when(receiptRepository.listRecentReceipts(1)).thenReturn(List.of());

        DemoHandoffShareCenterVo center = service.getShareCenter();

        assertThat(center.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(center.shareReady()).isFalse();
        assertThat(center.summary()).isEqualTo("No archived handoff package is available for sharing.");
        assertThat(center.nextAction())
                .isEqualTo("Archive a demo handoff package before sharing handoff evidence.");
        assertThat(center.latestArchiveId()).isNull();
        assertThat(center.latestSessionId()).isNull();
        assertThat(center.latestCreatedAt()).isNull();
        assertThat(center.latestDeliveryReceiptId()).isNull();
        assertThat(center.deliveryReceiptRecorded()).isFalse();
        assertThat(center.downloadActions()).containsExactly(
                "Archive a demo handoff package before downloading final handoff evidence.",
                "Download handoff package archive summary.",
                "Download handoff share checklist.",
                "Record a handoff share delivery receipt after sending the package.",
                "Resolve checklist warnings before sending the package."
        );
        assertThat(center.markdownReport())
                .contains("- Latest archive: `none`")
                .contains("- Latest session: `none`")
                .contains("- Delivery receipt recorded: `false`")
                .contains("- Latest delivery receipt: `none`")
                .contains("Resolve checklist warnings before sending the package.");
    }

    @Test
    void should_request_delivery_receipt_when_share_center_is_ready_but_not_delivered() {
        when(archiveSummaryService.getArchiveSummary()).thenReturn(readyArchiveSummary());
        when(shareChecklistService.getShareChecklist()).thenReturn(readyChecklist());
        when(receiptRepository.listRecentReceipts(1)).thenReturn(List.of());

        DemoHandoffShareCenterVo center = service.getShareCenter();

        assertThat(center.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(center.shareReady()).isTrue();
        assertThat(center.deliveryReceiptRecorded()).isFalse();
        assertThat(center.latestDeliveryReceiptId()).isNull();
        assertThat(center.downloadActions()).contains(
                "Record a handoff share delivery receipt after sending the package."
        );
        assertThat(center.evidenceNotes()).contains("No handoff share delivery receipt has been recorded yet.");
        assertThat(center.nextAction())
                .isEqualTo("Download the package, send the prepared handoff message, then record a delivery receipt.");
    }

    @Test
    void should_build_share_instructions_from_share_ready_center() {
        when(archiveSummaryService.getArchiveSummary()).thenReturn(readyArchiveSummary());
        when(shareChecklistService.getShareChecklist()).thenReturn(readyChecklist());
        when(receiptRepository.listRecentReceipts(1)).thenReturn(List.of(deliveryReceipt()));

        var instructions = service.getShareInstructions();

        assertThat(instructions.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(instructions.sendReady()).isTrue();
        assertThat(instructions.recommendedRecipients()).containsExactly(
                "Repository owner or maintainer",
                "Demo reviewer"
        );
        assertThat(instructions.requiredAttachments()).containsExactly(
                "Handoff package archive handoff-archive-1",
                "Handoff package archive summary",
                "Handoff share checklist",
                "Handoff share center report"
        );
        assertThat(instructions.preSendChecks()).containsExactly(
                "Confirm the Pull Request link in the handoff package opens correctly.",
                "Confirm the archived package, archive summary, checklist, and share-center report were downloaded.",
                "Confirm no handoff share checklist warnings remain."
        );
        assertThat(instructions.messageSubject()).isEqualTo("PatchPilot demo handoff: demo-session-20260624T003000Z");
        assertThat(instructions.messageBody())
                .contains("The PatchPilot demo handoff package is ready to share.")
                .contains("handoff-archive-1")
                .contains("demo-session-20260624T003000Z");
        assertThat(instructions.markdownReport())
                .contains("# PatchPilot Demo Handoff Share Instructions")
                .contains("## Message Template")
                .contains("## Required Attachments")
                .contains("GET /api/demo/handoff-share-instructions is read-only");
    }

    @Test
    void should_block_share_instructions_when_share_center_is_not_ready() {
        when(archiveSummaryService.getArchiveSummary()).thenReturn(emptyArchiveSummary());
        when(shareChecklistService.getShareChecklist()).thenReturn(checklistNeedingAttention());
        when(receiptRepository.listRecentReceipts(1)).thenReturn(List.of());

        var instructions = service.getShareInstructions();

        assertThat(instructions.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(instructions.sendReady()).isFalse();
        assertThat(instructions.requiredAttachments()).containsExactly(
                "Create a handoff package archive before attaching final handoff evidence."
        );
        assertThat(instructions.preSendChecks()).contains(
                "Resolve the share center next action before sending: Archive a demo handoff package before sharing handoff evidence."
        );
        assertThat(instructions.messageSubject()).isEqualTo("PatchPilot demo handoff: not ready");
        assertThat(instructions.messageBody())
                .contains("The PatchPilot demo handoff package is not ready to send yet.")
                .contains("Archive a demo handoff package before sharing handoff evidence.");
    }

    private static DemoHandoffPackageArchiveSummaryVo readyArchiveSummary() {
        return new DemoHandoffPackageArchiveSummaryVo(
                "READY",
                true,
                1,
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                DemoReadinessStatus.READY,
                Instant.parse("2026-06-24T04:05:00Z"),
                "Latest handoff package archive is ready to share.",
                "Share the latest handoff package.",
                "# Archive Summary"
        );
    }

    private static DemoHandoffPackageArchiveSummaryVo emptyArchiveSummary() {
        return new DemoHandoffPackageArchiveSummaryVo(
                "NEEDS_ATTENTION",
                false,
                0,
                null,
                null,
                DemoReadinessStatus.NEEDS_ATTENTION,
                null,
                "No handoff package archive is available for sharing.",
                "Archive a demo handoff package after a completed live run before sharing handoff evidence.",
                "# Archive Summary"
        );
    }

    private static DemoHandoffShareChecklistVo readyChecklist() {
        return new DemoHandoffShareChecklistVo(
                DemoReadinessStatus.READY,
                "Latest handoff archive is ready to share.",
                "Share the latest handoff package summary and archived package with the reviewer.",
                List.of(
                        new DemoHandoffShareChecklistItemVo("Handoff package archive", DemoReadinessStatus.READY, "Archive exists.", "No action needed."),
                        new DemoHandoffShareChecklistItemVo("Portable evidence", DemoReadinessStatus.READY, "Evidence is portable.", "No action needed.")
                ),
                "# Share Checklist",
                Instant.parse("2026-06-24T05:00:00Z")
        );
    }

    private static DemoHandoffShareChecklistVo checklistNeedingAttention() {
        return new DemoHandoffShareChecklistVo(
                DemoReadinessStatus.NEEDS_ATTENTION,
                "No handoff package archive is available for sharing.",
                "Archive a demo handoff package before sharing handoff evidence.",
                List.of(new DemoHandoffShareChecklistItemVo(
                        "Handoff package archive",
                        DemoReadinessStatus.NEEDS_ATTENTION,
                        "No archive exists.",
                        "Archive a demo handoff package."
                )),
                "# Share Checklist",
                Instant.parse("2026-06-24T05:00:00Z")
        );
    }

    private static DemoHandoffShareDeliveryReceiptVo deliveryReceipt() {
        return new DemoHandoffShareDeliveryReceiptVo(
                "receipt-1",
                DemoReadinessStatus.READY,
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                "email",
                "Demo reviewer",
                "local-operator",
                "Sent after the demo review.",
                "PatchPilot demo handoff: demo-session-20260624T003000Z",
                Instant.parse("2026-06-24T05:20:00Z"),
                Instant.parse("2026-06-24T05:21:00Z"),
                "# PatchPilot Demo Handoff Share Delivery Receipt"
        );
    }
}
