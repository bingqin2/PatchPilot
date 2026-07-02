package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoReviewerDeliveryCenterArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoReviewerDeliveryCenterVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DemoLiveDemoReviewerDeliveryCenterArchiveServiceTests {

    @Test
    void should_archive_ready_reviewer_delivery_center() {
        DemoLiveDemoReviewerDeliveryCenterArchiveService archiveService = readyArchiveService();

        DemoLiveDemoReviewerDeliveryCenterArchiveVo archive = archiveService.archiveCurrentCenter();

        assertThat(archive.id()).isEqualTo("live-demo-reviewer-delivery-center-archive-1");
        assertThat(archive.status()).isEqualTo("READY");
        assertThat(archive.deliverable()).isTrue();
        assertThat(archive.repository()).isEqualTo("bingqin2/PatchPilot");
        assertThat(archive.issueNumber()).isEqualTo(1);
        assertThat(archive.taskId()).isEqualTo("task-1");
        assertThat(archive.pullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(archive.readinessCards())
                .extracting(DemoLiveDemoReviewerDeliveryCenterVo.ReadinessCard::name)
                .containsExactly("Reviewer handoff package", "Replay package");
        assertThat(archive.blockers()).isEmpty();
        assertThat(archive.evidenceLinks())
                .extracting(DemoLiveDemoReviewerDeliveryCenterVo.EvidenceLink::label)
                .contains("Generated Pull Request");
        assertThat(archive.downloadActions()).contains(
                "Download live demo reviewer delivery center.",
                "Download live demo reviewer delivery center archive report."
        );
        assertThat(archive.sideEffectContract())
                .contains("Archive creation writes only PatchPilot local reviewer delivery center archive records.")
                .contains("GET /api/demo/live-demo-handoff-package/reviewer-delivery-center is read-only");
        assertThat(archive.centerGeneratedAt()).isEqualTo(Instant.parse("2026-07-02T12:00:00Z"));
        assertThat(archive.archivedAt()).isEqualTo(Instant.parse("2026-07-02T13:00:00Z"));
        assertThat(archive.report())
                .contains("# PatchPilot Live Demo Reviewer Delivery Center Archive")
                .contains("PatchPilot live demo reviewer delivery center is ready.")
                .contains("# PatchPilot Live Demo Reviewer Delivery Center");
        assertThat(archiveService.listRecentArchives()).containsExactly(archive);
        assertThat(archiveService.findArchive("live-demo-reviewer-delivery-center-archive-1")).contains(archive);
    }

    @Test
    void should_reject_archive_when_reviewer_delivery_center_is_not_deliverable() {
        DemoLiveDemoReviewerDeliveryCenterArchiveService archiveService =
                new DemoLiveDemoReviewerDeliveryCenterArchiveService(
                        () -> blockedCenter(),
                        new InMemoryDemoLiveDemoReviewerDeliveryCenterArchiveRepository(),
                        () -> "live-demo-reviewer-delivery-center-archive-1",
                        () -> Instant.parse("2026-07-02T13:00:00Z")
                );

        assertThatThrownBy(archiveService::archiveCurrentCenter)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("READY live demo reviewer delivery center is required before archiving");
    }

    @Test
    void should_keep_latest_twenty_reviewer_delivery_center_archives_first() {
        java.util.concurrent.atomic.AtomicInteger nextId = new java.util.concurrent.atomic.AtomicInteger();
        DemoLiveDemoReviewerDeliveryCenterArchiveService archiveService =
                new DemoLiveDemoReviewerDeliveryCenterArchiveService(
                        DemoLiveDemoReviewerDeliveryCenterArchiveServiceTests::readyCenter,
                        new InMemoryDemoLiveDemoReviewerDeliveryCenterArchiveRepository(),
                        () -> "archive-" + nextId.incrementAndGet(),
                        () -> Instant.parse("2026-07-02T13:00:00Z")
                );

        for (int index = 0; index < 21; index++) {
            archiveService.archiveCurrentCenter();
        }

        assertThat(archiveService.listRecentArchives()).hasSize(20);
        assertThat(archiveService.listRecentArchives().get(0).id()).isEqualTo("archive-21");
        assertThat(archiveService.listRecentArchives())
                .extracting(DemoLiveDemoReviewerDeliveryCenterArchiveVo::id)
                .doesNotContain("archive-1");
    }

    private static DemoLiveDemoReviewerDeliveryCenterArchiveService readyArchiveService() {
        return new DemoLiveDemoReviewerDeliveryCenterArchiveService(
                DemoLiveDemoReviewerDeliveryCenterArchiveServiceTests::readyCenter,
                new InMemoryDemoLiveDemoReviewerDeliveryCenterArchiveRepository(),
                () -> "live-demo-reviewer-delivery-center-archive-1",
                () -> Instant.parse("2026-07-02T13:00:00Z")
        );
    }

    private static DemoLiveDemoReviewerDeliveryCenterVo readyCenter() {
        return new DemoLiveDemoReviewerDeliveryCenterVo(
                "READY",
                true,
                "PatchPilot live demo reviewer delivery center is ready.",
                "Send the replay package and final evidence links to reviewers.",
                "bingqin2/PatchPilot",
                1,
                "https://github.com/bingqin2/PatchPilot/issues/1",
                "task-1",
                "COMPLETED",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                java.util.List.of(
                        new DemoLiveDemoReviewerDeliveryCenterVo.ReadinessCard(
                                "Reviewer handoff package",
                                "READY",
                                true,
                                "Live demo handoff package is ready for reviewer handoff.",
                                "Share this handoff package and archived evidence report with the reviewer."
                        ),
                        new DemoLiveDemoReviewerDeliveryCenterVo.ReadinessCard(
                                "Replay package",
                                "READY",
                                true,
                                "PatchPilot live demo replay package is ready for reviewer walkthrough.",
                                "Share the replay package with reviewers."
                        )
                ),
                java.util.List.of(),
                java.util.List.of(
                        new DemoLiveDemoReviewerDeliveryCenterVo.EvidenceLink(
                                "Generated Pull Request",
                                "https://github.com/bingqin2/PatchPilot/pull/42",
                                "Generated code review target."
                        )
                ),
                java.util.List.of(
                        "Download live demo reviewer delivery center.",
                        "Download live demo replay package."
                ),
                "GET /api/demo/live-demo-handoff-package/reviewer-delivery-center is read-only.",
                Instant.parse("2026-07-02T12:00:00Z"),
                "# PatchPilot Live Demo Reviewer Delivery Center"
        );
    }

    private static DemoLiveDemoReviewerDeliveryCenterVo blockedCenter() {
        return new DemoLiveDemoReviewerDeliveryCenterVo(
                "BLOCKED",
                false,
                "PatchPilot live demo reviewer delivery center is blocked.",
                "Archive missing evidence before sharing with reviewers.",
                null,
                0,
                null,
                null,
                null,
                null,
                java.util.List.of(),
                java.util.List.of("Reviewer handoff package is BLOCKED: missing evidence."),
                java.util.List.of(),
                java.util.List.of("Resolve blockers before archiving."),
                "GET /api/demo/live-demo-handoff-package/reviewer-delivery-center is read-only.",
                Instant.parse("2026-07-02T12:00:00Z"),
                "# PatchPilot Live Demo Reviewer Delivery Center"
        );
    }
}
