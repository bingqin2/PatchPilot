package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLaunchEvidencePackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoLaunchEvidencePackageArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoLaunchEvidencePackageArchiveServiceTests {

    @Test
    void archives_current_launch_evidence_package() {
        DemoLaunchEvidencePackageArchiveService service = new DemoLaunchEvidencePackageArchiveService(
                () -> DemoLaunchEvidenceFixtures.launchEvidencePackage(DemoReadinessStatus.READY),
                new InMemoryDemoLaunchEvidencePackageArchiveRepository(),
                Clock.fixed(Instant.parse("2026-06-28T02:30:00Z"), ZoneOffset.UTC),
                () -> "launch-evidence-archive-1"
        );

        DemoLaunchEvidencePackageArchiveVo archive = service.archiveCurrentPackage();

        assertThat(archive.id()).isEqualTo("launch-evidence-archive-1");
        assertThat(archive.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(archive.readyToShare()).isTrue();
        assertThat(archive.summary()).isEqualTo("PatchPilot launch evidence package is ready to share.");
        assertThat(archive.sessionId()).isEqualTo("demo-session-20260624T003000Z");
        assertThat(archive.launchReadinessStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(archive.evidenceBundleStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(archive.handoffFinalizationStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(archive.finalHandoffReportPackageArchiveStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(archive.finalHandoffReportPackageArchiveReady()).isTrue();
        assertThat(archive.finalHandoffReportPackageArchiveId())
                .isEqualTo("final-handoff-report-package-archive-1");
        assertThat(archive.finalHandoffReportPackageArchiveSummary())
                .isEqualTo("Latest final handoff report package archive is download-ready and ready.");
        assertThat(archive.latestTaskId()).isEqualTo("task-1");
        assertThat(archive.latestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(archive.latestWebhookDeliveryId()).isEqualTo("delivery-1");
        assertThat(archive.evaluationRunId()).isEqualTo("evaluation-run-2");
        assertThat(archive.createdAt()).isEqualTo(Instant.parse("2026-06-28T02:30:00Z"));
        assertThat(archive.report())
                .contains("# PatchPilot Demo Launch Evidence Package")
                .contains("## Side Effect Contract");
        assertThat(service.listRecentArchives()).containsExactly(archive);
        assertThat(service.findArchive("launch-evidence-archive-1")).contains(archive);
    }

    @Test
    void keeps_only_twenty_recent_archives() {
        DemoLaunchEvidencePackageArchiveService service = new DemoLaunchEvidencePackageArchiveService(
                () -> DemoLaunchEvidenceFixtures.launchEvidencePackage(DemoReadinessStatus.READY),
                new InMemoryDemoLaunchEvidencePackageArchiveRepository(),
                Clock.fixed(Instant.parse("2026-06-28T02:30:00Z"), ZoneOffset.UTC),
                new IncrementingIdSupplier()
        );

        for (int index = 1; index <= 22; index++) {
            service.archiveCurrentPackage();
        }

        List<DemoLaunchEvidencePackageArchiveVo> archives = service.listRecentArchives();
        assertThat(archives).hasSize(20);
        assertThat(archives)
                .extracting(DemoLaunchEvidencePackageArchiveVo::id)
                .containsExactly(
                        "launch-evidence-archive-22",
                        "launch-evidence-archive-21",
                        "launch-evidence-archive-20",
                        "launch-evidence-archive-19",
                        "launch-evidence-archive-18",
                        "launch-evidence-archive-17",
                        "launch-evidence-archive-16",
                        "launch-evidence-archive-15",
                        "launch-evidence-archive-14",
                        "launch-evidence-archive-13",
                        "launch-evidence-archive-12",
                        "launch-evidence-archive-11",
                        "launch-evidence-archive-10",
                        "launch-evidence-archive-9",
                        "launch-evidence-archive-8",
                        "launch-evidence-archive-7",
                        "launch-evidence-archive-6",
                        "launch-evidence-archive-5",
                        "launch-evidence-archive-4",
                        "launch-evidence-archive-3"
                );
        assertThat(service.findArchive("launch-evidence-archive-1")).isEmpty();
    }

    private static final class IncrementingIdSupplier implements java.util.function.Supplier<String> {

        private int nextId = 1;

        @Override
        public String get() {
            return "launch-evidence-archive-" + nextId++;
        }
    }
}
