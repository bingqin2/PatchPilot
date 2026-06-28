package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceSharePackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceSharePackageVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoFinalAcceptanceSharePackageArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalAcceptanceSharePackageArchiveServiceTests {

    @Test
    void archives_current_final_acceptance_share_package() {
        DemoFinalAcceptanceSharePackageArchiveService service = new DemoFinalAcceptanceSharePackageArchiveService(
                DemoFinalAcceptanceSharePackageArchiveServiceTests::sharePackage,
                new InMemoryDemoFinalAcceptanceSharePackageArchiveRepository(),
                Clock.fixed(Instant.parse("2026-06-29T02:00:00Z"), ZoneOffset.UTC),
                () -> "final-acceptance-share-package-archive-1"
        );

        DemoFinalAcceptanceSharePackageArchiveVo archive = service.archiveCurrentSharePackage();

        assertThat(archive.id()).isEqualTo("final-acceptance-share-package-archive-1");
        assertThat(archive.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(archive.sendReady()).isTrue();
        assertThat(archive.summary()).isEqualTo("PatchPilot final demo acceptance package is ready to send.");
        assertThat(archive.nextAction()).isEqualTo("Send the prepared final acceptance message with all required attachments.");
        assertThat(archive.launchCertificateArchiveId()).isEqualTo("launch-certificate-archive-1");
        assertThat(archive.taskCertificateArchiveId()).isEqualTo("task-evidence-certificate-archive-1");
        assertThat(archive.latestTaskId()).isEqualTo("task-1");
        assertThat(archive.latestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(archive.recommendedRecipients()).containsExactly("Repository owner or maintainer", "Demo reviewer");
        assertThat(archive.requiredAttachments()).contains("Final demo acceptance summary report");
        assertThat(archive.preSendChecks()).contains("Confirm final demo acceptance status is READY and accepted.");
        assertThat(archive.messageSubject()).isEqualTo("PatchPilot final demo acceptance: task-1");
        assertThat(archive.messageBody()).contains("PatchPilot final demo acceptance is ready for external review.");
        assertThat(archive.evidenceNotes()).contains("Final acceptance status is READY.");
        assertThat(archive.generatedAt()).isEqualTo(Instant.parse("2026-06-29T01:30:00Z"));
        assertThat(archive.archivedAt()).isEqualTo(Instant.parse("2026-06-29T02:00:00Z"));
        assertThat(archive.report())
                .contains("# PatchPilot Final Demo Acceptance Share Package")
                .contains("PatchPilot final demo acceptance: task-1");
        assertThat(archive.sideEffectContract())
                .contains("read-only snapshot")
                .contains("write to GitHub");
        assertThat(service.listRecentArchives()).containsExactly(archive);
        assertThat(service.findArchive("final-acceptance-share-package-archive-1")).contains(archive);
    }

    @Test
    void keeps_only_twenty_recent_archives() {
        DemoFinalAcceptanceSharePackageArchiveService service = new DemoFinalAcceptanceSharePackageArchiveService(
                DemoFinalAcceptanceSharePackageArchiveServiceTests::sharePackage,
                new InMemoryDemoFinalAcceptanceSharePackageArchiveRepository(),
                Clock.fixed(Instant.parse("2026-06-29T02:00:00Z"), ZoneOffset.UTC),
                new IncrementingIdSupplier()
        );

        for (int index = 1; index <= 22; index++) {
            service.archiveCurrentSharePackage();
        }

        List<DemoFinalAcceptanceSharePackageArchiveVo> archives = service.listRecentArchives();
        assertThat(archives).hasSize(20);
        assertThat(archives)
                .extracting(DemoFinalAcceptanceSharePackageArchiveVo::id)
                .containsExactly(
                        "final-acceptance-share-package-archive-22",
                        "final-acceptance-share-package-archive-21",
                        "final-acceptance-share-package-archive-20",
                        "final-acceptance-share-package-archive-19",
                        "final-acceptance-share-package-archive-18",
                        "final-acceptance-share-package-archive-17",
                        "final-acceptance-share-package-archive-16",
                        "final-acceptance-share-package-archive-15",
                        "final-acceptance-share-package-archive-14",
                        "final-acceptance-share-package-archive-13",
                        "final-acceptance-share-package-archive-12",
                        "final-acceptance-share-package-archive-11",
                        "final-acceptance-share-package-archive-10",
                        "final-acceptance-share-package-archive-9",
                        "final-acceptance-share-package-archive-8",
                        "final-acceptance-share-package-archive-7",
                        "final-acceptance-share-package-archive-6",
                        "final-acceptance-share-package-archive-5",
                        "final-acceptance-share-package-archive-4",
                        "final-acceptance-share-package-archive-3"
                );
        assertThat(service.findArchive("final-acceptance-share-package-archive-1")).isEmpty();
    }

    private static DemoFinalAcceptanceSharePackageVo sharePackage() {
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
                        "Task evidence acceptance certificate archive task-evidence-certificate-archive-1"
                ),
                List.of("Confirm final demo acceptance status is READY and accepted."),
                "PatchPilot final demo acceptance: task-1",
                "PatchPilot final demo acceptance is ready for external review.",
                List.of("Final acceptance status is READY."),
                "POST /api/demo/final-acceptance-share-package/archives archives a read-only snapshot and does not create tasks, call the model, run tests, mutate Git, send messages, or write to GitHub.",
                "# PatchPilot Final Demo Acceptance Share Package\n\nSubject: PatchPilot final demo acceptance: task-1\n",
                Instant.parse("2026-06-29T01:30:00Z")
        );
    }

    private static final class IncrementingIdSupplier implements java.util.function.Supplier<String> {

        private int nextId = 1;

        @Override
        public String get() {
            return "final-acceptance-share-package-archive-" + nextId++;
        }
    }
}
