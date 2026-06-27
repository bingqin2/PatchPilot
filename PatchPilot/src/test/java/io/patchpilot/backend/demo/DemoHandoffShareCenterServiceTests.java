package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveSummaryVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareCenterVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareChecklistItemVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareChecklistVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
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
    private final DemoHandoffShareCenterService service = new DemoHandoffShareCenterService(
            archiveSummaryService,
            shareChecklistService,
            CLOCK
    );

    @Test
    void should_build_share_ready_center_from_archive_summary_and_checklist() {
        when(archiveSummaryService.getArchiveSummary()).thenReturn(readyArchiveSummary());
        when(shareChecklistService.getShareChecklist()).thenReturn(readyChecklist());

        DemoHandoffShareCenterVo center = service.getShareCenter();

        assertThat(center.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(center.shareReady()).isTrue();
        assertThat(center.summary()).isEqualTo("Post-demo handoff package is ready to share.");
        assertThat(center.nextAction())
                .isEqualTo("Download the package, archive summary, and share checklist before sending handoff evidence.");
        assertThat(center.latestArchiveId()).isEqualTo("handoff-archive-1");
        assertThat(center.latestSessionId()).isEqualTo("demo-session-20260624T003000Z");
        assertThat(center.latestCreatedAt()).isEqualTo("2026-06-24T04:05:00Z");
        assertThat(center.generatedAt()).isEqualTo(Instant.parse("2026-06-24T05:30:00Z"));
        assertThat(center.downloadActions()).containsExactly(
                "Download handoff package archive handoff-archive-1.",
                "Download handoff package archive summary.",
                "Download handoff share checklist."
        );
        assertThat(center.evidenceNotes())
                .contains(
                        "Latest package archive status is READY.",
                        "Share checklist has 2 checks.",
                        "Archive summary: Latest handoff package archive is ready to share.",
                        "Checklist summary: Latest handoff archive is ready to share."
                );
        assertThat(center.markdownReport())
                .contains("# PatchPilot Demo Handoff Share Center")
                .contains("- Status: `READY`")
                .contains("- Share ready: `true`")
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

        DemoHandoffShareCenterVo center = service.getShareCenter();

        assertThat(center.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(center.shareReady()).isFalse();
        assertThat(center.summary()).isEqualTo("No archived handoff package is available for sharing.");
        assertThat(center.nextAction())
                .isEqualTo("Archive a demo handoff package before sharing handoff evidence.");
        assertThat(center.latestArchiveId()).isNull();
        assertThat(center.latestSessionId()).isNull();
        assertThat(center.latestCreatedAt()).isNull();
        assertThat(center.downloadActions()).containsExactly(
                "Archive a demo handoff package before downloading final handoff evidence.",
                "Download handoff package archive summary.",
                "Download handoff share checklist.",
                "Resolve checklist warnings before sending the package."
        );
        assertThat(center.markdownReport())
                .contains("- Latest archive: `none`")
                .contains("- Latest session: `none`")
                .contains("Resolve checklist warnings before sending the package.");
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
}
