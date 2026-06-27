package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoSelfHostedLaunchCheckVo;
import io.patchpilot.backend.demo.domain.DemoSelfHostedLaunchReadinessArchiveVo;
import io.patchpilot.backend.demo.domain.DemoSelfHostedLaunchReadinessVo;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoSelfHostedLaunchReadinessArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SelfHostedLaunchReadinessArchiveServiceTests {

    @Test
    void archives_current_self_hosted_launch_readiness_package() {
        SelfHostedLaunchReadinessArchiveService service = new SelfHostedLaunchReadinessArchiveService(
                SelfHostedLaunchReadinessArchiveServiceTests::launchReadiness,
                new InMemoryDemoSelfHostedLaunchReadinessArchiveRepository(),
                Clock.fixed(Instant.parse("2026-06-28T01:30:00Z"), ZoneOffset.UTC),
                () -> "launch-readiness-archive-1"
        );

        DemoSelfHostedLaunchReadinessArchiveVo archive = service.archiveCurrentReadinessPackage();

        assertThat(archive.id()).isEqualTo("launch-readiness-archive-1");
        assertThat(archive.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(archive.readyToLaunch()).isFalse();
        assertThat(archive.summary()).isEqualTo("Self-hosted PatchPilot needs attention before launch.");
        assertThat(archive.readyCheckCount()).isEqualTo(1);
        assertThat(archive.needsAttentionCheckCount()).isEqualTo(1);
        assertThat(archive.blockedCheckCount()).isZero();
        assertThat(archive.createdAt()).isEqualTo(Instant.parse("2026-06-28T01:30:00Z"));
        assertThat(archive.report())
                .contains("# PatchPilot Self-Hosted Launch Readiness")
                .contains("## Side Effect Contract");
        assertThat(service.listRecentArchives()).containsExactly(archive);
        assertThat(service.findArchive("launch-readiness-archive-1")).contains(archive);
    }

    @Test
    void keeps_only_twenty_recent_archives() {
        SelfHostedLaunchReadinessArchiveService service = new SelfHostedLaunchReadinessArchiveService(
                SelfHostedLaunchReadinessArchiveServiceTests::launchReadiness,
                new InMemoryDemoSelfHostedLaunchReadinessArchiveRepository(),
                Clock.fixed(Instant.parse("2026-06-28T01:30:00Z"), ZoneOffset.UTC),
                new IncrementingIdSupplier()
        );

        for (int index = 1; index <= 22; index++) {
            service.archiveCurrentReadinessPackage();
        }

        List<DemoSelfHostedLaunchReadinessArchiveVo> archives = service.listRecentArchives();
        assertThat(archives).hasSize(20);
        assertThat(archives)
                .extracting(DemoSelfHostedLaunchReadinessArchiveVo::id)
                .containsExactly(
                        "launch-readiness-archive-22",
                        "launch-readiness-archive-21",
                        "launch-readiness-archive-20",
                        "launch-readiness-archive-19",
                        "launch-readiness-archive-18",
                        "launch-readiness-archive-17",
                        "launch-readiness-archive-16",
                        "launch-readiness-archive-15",
                        "launch-readiness-archive-14",
                        "launch-readiness-archive-13",
                        "launch-readiness-archive-12",
                        "launch-readiness-archive-11",
                        "launch-readiness-archive-10",
                        "launch-readiness-archive-9",
                        "launch-readiness-archive-8",
                        "launch-readiness-archive-7",
                        "launch-readiness-archive-6",
                        "launch-readiness-archive-5",
                        "launch-readiness-archive-4",
                        "launch-readiness-archive-3"
                );
        assertThat(service.findArchive("launch-readiness-archive-1")).isEmpty();
    }

    private static DemoSelfHostedLaunchReadinessVo launchReadiness() {
        return new DemoSelfHostedLaunchReadinessVo(
                DemoReadinessStatus.NEEDS_ATTENTION,
                false,
                "Self-hosted PatchPilot needs attention before launch.",
                List.of(
                        new DemoSelfHostedLaunchCheckVo(
                                "Demo readiness",
                                DemoReadinessStatus.READY,
                                "Demo readiness is ready.",
                                "Post the tested /agent fix comment."
                        ),
                        new DemoSelfHostedLaunchCheckVo(
                                "Handoff finalization",
                                DemoReadinessStatus.NEEDS_ATTENTION,
                                "Delivery receipt is missing.",
                                "Record a delivery receipt."
                        )
                ),
                List.of("Record a delivery receipt.", "Rerun launch readiness."),
                Instant.parse("2026-06-28T01:29:00Z"),
                "# PatchPilot Self-Hosted Launch Readiness\n\n## Side Effect Contract\n\nRead-only.\n"
        );
    }

    private static final class IncrementingIdSupplier implements java.util.function.Supplier<String> {

        private int nextId = 1;

        @Override
        public String get() {
            return "launch-readiness-archive-" + nextId++;
        }
    }
}
