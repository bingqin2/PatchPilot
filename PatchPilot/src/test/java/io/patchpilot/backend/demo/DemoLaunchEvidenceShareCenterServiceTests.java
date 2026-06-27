package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceShareCenterVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
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

    private final DemoLaunchEvidencePackageArchiveService archiveService =
            mock(DemoLaunchEvidencePackageArchiveService.class);
    private final DemoLaunchEvidenceShareCenterService service =
            new DemoLaunchEvidenceShareCenterService(archiveService, CLOCK);

    @Test
    void should_build_share_ready_center_from_latest_launch_evidence_archive() {
        when(archiveService.listRecentArchives()).thenReturn(List.of(
                DemoLaunchEvidenceFixtures.launchEvidencePackageArchive(DemoReadinessStatus.READY)
        ));

        DemoLaunchEvidenceShareCenterVo center = service.getShareCenter();

        assertThat(center.status()).isEqualTo("READY");
        assertThat(center.shareReady()).isTrue();
        assertThat(center.summary()).isEqualTo("Latest archived launch evidence package is READY and can be shared.");
        assertThat(center.nextAction()).isEqualTo("Download the archived launch evidence package and share it with reviewers.");
        assertThat(center.archiveCount()).isEqualTo(1);
        assertThat(center.latestArchiveId()).isEqualTo("launch-evidence-archive-1");
        assertThat(center.latestSessionId()).isEqualTo("demo-session-20260624T003000Z");
        assertThat(center.latestCreatedAt()).isEqualTo("2026-06-28T02:30:00Z");
        assertThat(center.latestTaskId()).isEqualTo("task-1");
        assertThat(center.latestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(center.latestWebhookDeliveryId()).isEqualTo("delivery-1");
        assertThat(center.evaluationRunId()).isEqualTo("evaluation-run-2");
        assertThat(center.generatedAt()).isEqualTo(Instant.parse("2026-06-28T02:45:00Z"));
        assertThat(center.downloadActions()).containsExactly(
                "Download launch evidence package archive launch-evidence-archive-1.",
                "Download launch evidence share center report.",
                "Open Pull Request https://github.com/bingqin2/PatchPilot/pull/42 for review."
        );
        assertThat(center.evidenceNotes()).contains(
                "Latest launch evidence archive status is READY.",
                "Latest archive session is demo-session-20260624T003000Z.",
                "Latest task task-1 completed before archive.",
                "Latest Pull Request https://github.com/bingqin2/PatchPilot/pull/42 is ready for review.",
                "Latest webhook delivery evidence is delivery-1.",
                "Latest evaluation run evidence is evaluation-run-2."
        );
        assertThat(center.markdownReport())
                .contains("# PatchPilot Demo Launch Evidence Share Center")
                .contains("- Status: `READY`")
                .contains("- Share ready: `true`")
                .contains("- Latest archive: `launch-evidence-archive-1`")
                .contains("## Downloads")
                .contains("## Evidence")
                .contains("GET /api/demo/launch-evidence-share-center is read-only");
    }

    @Test
    void should_request_archive_when_no_launch_evidence_archive_exists() {
        when(archiveService.listRecentArchives()).thenReturn(List.of());

        DemoLaunchEvidenceShareCenterVo center = service.getShareCenter();

        assertThat(center.status()).isEqualTo("NO_ARCHIVE");
        assertThat(center.shareReady()).isFalse();
        assertThat(center.summary()).isEqualTo("No archived launch evidence package is available for sharing.");
        assertThat(center.nextAction())
                .isEqualTo("Archive a final demo launch evidence package after a completed live run before sharing launch evidence.");
        assertThat(center.archiveCount()).isZero();
        assertThat(center.latestArchiveId()).isNull();
        assertThat(center.latestSessionId()).isNull();
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
        when(archiveService.listRecentArchives()).thenReturn(List.of(
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
                "Resolve launch evidence blockers and archive a new package."
        );
        assertThat(center.markdownReport())
                .contains("- Status: `NEEDS_ATTENTION`")
                .contains("- Share ready: `false`");
    }
}
