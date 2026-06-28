package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCloseoutArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoLaunchAcceptanceCloseoutArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoLaunchAcceptanceCloseoutArchiveServiceTests {

    @Test
    void archives_current_launch_acceptance_closeout() {
        DemoLaunchAcceptanceCloseoutArchiveService service = new DemoLaunchAcceptanceCloseoutArchiveService(
                () -> DemoLaunchEvidenceFixtures.launchAcceptanceCloseout(DemoReadinessStatus.READY),
                new InMemoryDemoLaunchAcceptanceCloseoutArchiveRepository(),
                Clock.fixed(Instant.parse("2026-06-28T08:30:00Z"), ZoneOffset.UTC),
                () -> "launch-closeout-archive-1"
        );

        DemoLaunchAcceptanceCloseoutArchiveVo archive = service.archiveCurrentCloseout();

        assertThat(archive.id()).isEqualTo("launch-closeout-archive-1");
        assertThat(archive.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(archive.accepted()).isTrue();
        assertThat(archive.summary()).isEqualTo("PatchPilot launch acceptance closeout is complete.");
        assertThat(archive.sessionId()).isEqualTo("demo-session-20260624T003000Z");
        assertThat(archive.latestTaskId()).isEqualTo("task-1");
        assertThat(archive.latestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(archive.latestWebhookDeliveryId()).isEqualTo("delivery-1");
        assertThat(archive.evaluationRunId()).isEqualTo("evaluation-run-2");
        assertThat(archive.latestArchiveId()).isEqualTo("launch-evidence-archive-1");
        assertThat(archive.finalHandoffReportPackageArchiveStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(archive.finalHandoffReportPackageArchiveReady()).isTrue();
        assertThat(archive.finalHandoffReportPackageArchiveId()).isEqualTo("final-handoff-report-package-archive-1");
        assertThat(archive.finalHandoffReportPackageArchiveSummary())
                .isEqualTo("Latest final handoff report package archive is download-ready and ready.");
        assertThat(archive.latestDeliveryReceiptId()).isEqualTo("launch-delivery-receipt-1");
        assertThat(archive.latestDeliveryTarget()).isEqualTo("reviewer@example.com");
        assertThat(archive.latestDeliveryChannel()).isEqualTo("email");
        assertThat(archive.deliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(archive.createdAt()).isEqualTo(Instant.parse("2026-06-28T08:30:00Z"));
        assertThat(archive.report())
                .contains("# PatchPilot Launch Acceptance Closeout")
                .contains("launch-delivery-receipt-1");
        assertThat(service.listRecentArchives()).containsExactly(archive);
        assertThat(service.findArchive("launch-closeout-archive-1")).contains(archive);
    }

    @Test
    void keeps_only_twenty_recent_archives() {
        DemoLaunchAcceptanceCloseoutArchiveService service = new DemoLaunchAcceptanceCloseoutArchiveService(
                () -> DemoLaunchEvidenceFixtures.launchAcceptanceCloseout(DemoReadinessStatus.READY),
                new InMemoryDemoLaunchAcceptanceCloseoutArchiveRepository(),
                Clock.fixed(Instant.parse("2026-06-28T08:30:00Z"), ZoneOffset.UTC),
                new IncrementingIdSupplier()
        );

        for (int index = 1; index <= 22; index++) {
            service.archiveCurrentCloseout();
        }

        List<DemoLaunchAcceptanceCloseoutArchiveVo> archives = service.listRecentArchives();
        assertThat(archives).hasSize(20);
        assertThat(archives)
                .extracting(DemoLaunchAcceptanceCloseoutArchiveVo::id)
                .containsExactly(
                        "launch-closeout-archive-22",
                        "launch-closeout-archive-21",
                        "launch-closeout-archive-20",
                        "launch-closeout-archive-19",
                        "launch-closeout-archive-18",
                        "launch-closeout-archive-17",
                        "launch-closeout-archive-16",
                        "launch-closeout-archive-15",
                        "launch-closeout-archive-14",
                        "launch-closeout-archive-13",
                        "launch-closeout-archive-12",
                        "launch-closeout-archive-11",
                        "launch-closeout-archive-10",
                        "launch-closeout-archive-9",
                        "launch-closeout-archive-8",
                        "launch-closeout-archive-7",
                        "launch-closeout-archive-6",
                        "launch-closeout-archive-5",
                        "launch-closeout-archive-4",
                        "launch-closeout-archive-3"
                );
        assertThat(service.findArchive("launch-closeout-archive-1")).isEmpty();
    }

    private static final class IncrementingIdSupplier implements java.util.function.Supplier<String> {

        private int nextId = 1;

        @Override
        public String get() {
            return "launch-closeout-archive-" + nextId++;
        }
    }
}
