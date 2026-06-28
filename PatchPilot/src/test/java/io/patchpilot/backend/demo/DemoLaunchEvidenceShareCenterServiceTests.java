package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceShareCenterVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceShareDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.DemoLaunchEvidencePackageArchiveRepository;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoLaunchEvidenceShareDeliveryReceiptRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DemoLaunchEvidenceShareCenterServiceTests {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-06-28T02:45:00Z"), ZoneOffset.UTC);

    private final DemoLaunchEvidencePackageArchiveRepository archiveRepository =
            mock(DemoLaunchEvidencePackageArchiveRepository.class);
    private final InMemoryDemoLaunchEvidenceShareDeliveryReceiptRepository receiptRepository =
            new InMemoryDemoLaunchEvidenceShareDeliveryReceiptRepository();
    private final DemoLaunchEvidenceShareCenterService service =
            new DemoLaunchEvidenceShareCenterService(archiveRepository, receiptRepository, CLOCK);

    @Test
    void should_build_share_ready_center_from_latest_launch_evidence_archive() {
        when(archiveRepository.listRecentArchives(20)).thenReturn(List.of(
                DemoLaunchEvidenceFixtures.launchEvidencePackageArchive(DemoReadinessStatus.READY)
        ));

        DemoLaunchEvidenceShareCenterVo center = service.getShareCenter();

        assertThat(center.status()).isEqualTo("READY");
        assertThat(center.shareReady()).isTrue();
        assertThat(center.summary()).isEqualTo("Latest archived launch evidence package is READY and can be shared.");
        assertThat(center.nextAction())
                .isEqualTo("Download the archived launch evidence package, share it with reviewers, then record a delivery receipt.");
        assertThat(center.archiveCount()).isEqualTo(1);
        assertThat(center.latestArchiveId()).isEqualTo("launch-evidence-archive-1");
        assertThat(center.latestSessionId()).isEqualTo("demo-session-20260624T003000Z");
        assertThat(center.latestCreatedAt()).isEqualTo("2026-06-28T02:30:00Z");
        assertThat(center.latestTaskId()).isEqualTo("task-1");
        assertThat(center.latestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(center.latestWebhookDeliveryId()).isEqualTo("delivery-1");
        assertThat(center.evaluationRunId()).isEqualTo("evaluation-run-2");
        assertThat(center.finalHandoffReportPackageArchiveStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(center.finalHandoffReportPackageArchiveReady()).isTrue();
        assertThat(center.finalHandoffReportPackageArchiveId())
                .isEqualTo("final-handoff-report-package-archive-1");
        assertThat(center.deliveryReceiptRecorded()).isFalse();
        assertThat(center.deliveryReceiptFreshness()).isEqualTo("MISSING");
        assertThat(center.deliveryReceiptFresh()).isFalse();
        assertThat(center.deliveryReceiptFreshnessSummary())
                .isEqualTo("No delivery receipt has been recorded for the current launch evidence package.");
        assertThat(center.generatedAt()).isEqualTo(Instant.parse("2026-06-28T02:45:00Z"));
        assertThat(center.downloadActions()).containsExactly(
                "Download launch evidence package archive launch-evidence-archive-1.",
                "Download launch evidence share center report.",
                "Download final handoff report package archive final-handoff-report-package-archive-1.",
                "Open Pull Request https://github.com/bingqin2/PatchPilot/pull/42 for review.",
                "Record a launch evidence delivery receipt after sharing the package."
        );
        assertThat(center.evidenceNotes()).contains(
                "Latest launch evidence archive status is READY.",
                "Latest archive session is demo-session-20260624T003000Z.",
                "Latest task task-1 completed before archive.",
                "Latest Pull Request https://github.com/bingqin2/PatchPilot/pull/42 is ready for review.",
                "Latest webhook delivery evidence is delivery-1.",
                "Latest evaluation run evidence is evaluation-run-2.",
                "Final handoff report package archive final-handoff-report-package-archive-1 is READY and download-ready.",
                "No launch evidence delivery receipt has been recorded yet.",
                "No delivery receipt has been recorded for the current launch evidence package."
        );
        assertThat(center.markdownReport())
                .contains("# PatchPilot Demo Launch Evidence Share Center")
                .contains("- Status: `READY`")
                .contains("- Share ready: `true`")
                .contains("- Latest archive: `launch-evidence-archive-1`")
                .contains("- Final handoff report package archive: `final-handoff-report-package-archive-1`")
                .contains("- Final handoff report package archive status: `READY`")
                .contains("- Delivery receipt recorded: `false`")
                .contains("- Delivery receipt freshness: `MISSING`")
                .contains("## Downloads")
                .contains("## Evidence")
                .contains("GET /api/demo/launch-evidence-share-center is read-only");
    }

    @Test
    void should_request_archive_when_no_launch_evidence_archive_exists() {
        when(archiveRepository.listRecentArchives(20)).thenReturn(List.of());

        DemoLaunchEvidenceShareCenterVo center = service.getShareCenter();

        assertThat(center.status()).isEqualTo("NO_ARCHIVE");
        assertThat(center.shareReady()).isFalse();
        assertThat(center.summary()).isEqualTo("No archived launch evidence package is available for sharing.");
        assertThat(center.nextAction())
                .isEqualTo("Archive a final demo launch evidence package after a completed live run before sharing launch evidence.");
        assertThat(center.archiveCount()).isZero();
        assertThat(center.latestArchiveId()).isNull();
        assertThat(center.latestSessionId()).isNull();
        assertThat(center.finalHandoffReportPackageArchiveStatus()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(center.finalHandoffReportPackageArchiveReady()).isFalse();
        assertThat(center.finalHandoffReportPackageArchiveId()).isNull();
        assertThat(center.deliveryReceiptRecorded()).isFalse();
        assertThat(center.deliveryReceiptFreshness()).isEqualTo("MISSING");
        assertThat(center.downloadActions()).containsExactly(
                "Archive a demo launch evidence package before downloading final launch evidence.",
                "Download the current launch evidence package report for review."
        );
        assertThat(center.evidenceNotes()).containsExactly(
                "No launch evidence archive has been captured yet."
        );
        assertThat(center.markdownReport())
                .contains("- Status: `NO_ARCHIVE`")
                .contains("- Latest archive: `none`");
    }

    @Test
    void should_mark_share_center_not_ready_when_latest_archive_needs_attention() {
        when(archiveRepository.listRecentArchives(20)).thenReturn(List.of(
                DemoLaunchEvidenceFixtures.launchEvidencePackageArchive(DemoReadinessStatus.NEEDS_ATTENTION)
        ));

        DemoLaunchEvidenceShareCenterVo center = service.getShareCenter();

        assertThat(center.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(center.shareReady()).isFalse();
        assertThat(center.summary()).isEqualTo("Latest archived launch evidence package needs attention before it is shared.");
        assertThat(center.nextAction()).isEqualTo("Resolve launch evidence blockers, regenerate the package, and archive a new READY package.");
        assertThat(center.downloadActions()).containsExactly(
                "Download launch evidence package archive launch-evidence-archive-1.",
                "Download launch evidence share center report.",
                "Resolve launch evidence blockers and archive a new package.",
                "Record a launch evidence delivery receipt after sharing the package."
        );
        assertThat(center.markdownReport())
                .contains("- Status: `NEEDS_ATTENTION`")
                .contains("- Share ready: `false`");
    }

    @Test
    void should_mark_latest_delivery_receipt_fresh_when_archive_and_session_match() {
        receiptRepository.save(receipt(
                "launch-delivery-receipt-1",
                "launch-evidence-archive-1",
                "demo-session-20260624T003000Z",
                "2026-06-28T06:10:00Z"
        ));
        when(archiveRepository.listRecentArchives(20)).thenReturn(List.of(
                DemoLaunchEvidenceFixtures.launchEvidencePackageArchive(DemoReadinessStatus.READY)
        ));

        DemoLaunchEvidenceShareCenterVo center = service.getShareCenter();

        assertThat(center.latestDeliveryReceiptId()).isEqualTo("launch-delivery-receipt-1");
        assertThat(center.latestDeliveryTarget()).isEqualTo("reviewer@example.com");
        assertThat(center.latestDeliveryChannel()).isEqualTo("email");
        assertThat(center.latestDeliveredAt()).isEqualTo("2026-06-28T06:05:00Z");
        assertThat(center.deliveryReceiptRecorded()).isTrue();
        assertThat(center.deliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(center.deliveryReceiptFresh()).isTrue();
        assertThat(center.deliveryReceiptFreshnessSummary())
                .isEqualTo("Latest delivery receipt matches the current launch evidence archive and session.");
        assertThat(center.downloadActions())
                .contains("Download launch evidence delivery receipt launch-delivery-receipt-1.");
        assertThat(center.evidenceNotes()).contains(
                "Latest delivery receipt launch-delivery-receipt-1 was recorded for reviewer@example.com via email.",
                "Latest delivery receipt matches the current launch evidence archive and session."
        );
    }

    @Test
    void should_mark_latest_delivery_receipt_stale_when_archive_or_session_no_longer_matches() {
        receiptRepository.save(receipt(
                "launch-delivery-receipt-old",
                "old-launch-archive",
                "old-session",
                "2026-06-28T06:10:00Z"
        ));
        when(archiveRepository.listRecentArchives(20)).thenReturn(List.of(
                DemoLaunchEvidenceFixtures.launchEvidencePackageArchive(DemoReadinessStatus.READY)
        ));

        DemoLaunchEvidenceShareCenterVo center = service.getShareCenter();

        assertThat(center.latestDeliveryReceiptId()).isEqualTo("launch-delivery-receipt-old");
        assertThat(center.deliveryReceiptRecorded()).isTrue();
        assertThat(center.deliveryReceiptFreshness()).isEqualTo("STALE");
        assertThat(center.deliveryReceiptFresh()).isFalse();
        assertThat(center.deliveryReceiptFreshnessSummary())
                .isEqualTo("Latest delivery receipt launch-delivery-receipt-old belongs to old-launch-archive/old-session, not current launch-evidence-archive-1/demo-session-20260624T003000Z.");
        assertThat(center.downloadActions())
                .contains("Record a new launch evidence delivery receipt for archive launch-evidence-archive-1.");
    }

    private static DemoLaunchEvidenceShareDeliveryReceiptVo receipt(
            String id,
            String archiveId,
            String sessionId,
            String createdAt
    ) {
        return new DemoLaunchEvidenceShareDeliveryReceiptVo(
                id,
                "READY",
                archiveId,
                sessionId,
                "email",
                "reviewer@example.com",
                "local-operator",
                "Sent after the demo review.",
                "PatchPilot demo launch evidence: " + sessionId,
                Instant.parse("2026-06-28T06:05:00Z"),
                Instant.parse(createdAt),
                "# PatchPilot Demo Launch Evidence Delivery Receipt"
        );
    }
}
