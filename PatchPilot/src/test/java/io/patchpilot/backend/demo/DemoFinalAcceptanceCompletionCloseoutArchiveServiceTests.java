package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionCloseoutArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionCloseoutVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoFinalAcceptanceCompletionCloseoutArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DemoFinalAcceptanceCompletionCloseoutArchiveServiceTests {

    @Test
    void archives_ready_final_acceptance_completion_closeout() {
        DemoFinalAcceptanceCompletionCloseoutArchiveService service =
                new DemoFinalAcceptanceCompletionCloseoutArchiveService(
                        DemoFinalAcceptanceCompletionCloseoutArchiveServiceTests::readyCloseout,
                        new InMemoryDemoFinalAcceptanceCompletionCloseoutArchiveRepository(),
                        Clock.fixed(Instant.parse("2026-06-29T06:30:00Z"), ZoneOffset.UTC),
                        () -> "final-acceptance-completion-closeout-archive-1"
                );

        DemoFinalAcceptanceCompletionCloseoutArchiveVo archive = service.archiveCurrentCloseout();

        assertThat(archive.id()).isEqualTo("final-acceptance-completion-closeout-archive-1");
        assertThat(archive.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(archive.closed()).isTrue();
        assertThat(archive.summary()).isEqualTo(
                "PatchPilot final acceptance completion is closed with accepted certificates, finalized sharing, and fresh completion delivery proof."
        );
        assertThat(archive.nextAction()).isEqualTo(
                "Use this closeout report as the final external-review completion record."
        );
        assertThat(archive.latestTaskId()).isEqualTo("task-1");
        assertThat(archive.latestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/8");
        assertThat(archive.latestSharePackageArchiveId()).isEqualTo("final-acceptance-share-package-archive-1");
        assertThat(archive.latestCompletionArchiveId()).isEqualTo("final-acceptance-completion-archive-1");
        assertThat(archive.latestCompletionEvidenceDeliveryReceiptId())
                .isEqualTo("final-acceptance-completion-evidence-delivery-receipt-1");
        assertThat(archive.deliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(archive.evidenceNotes()).contains("Final demo acceptance summary is accepted.");
        assertThat(archive.downloadActions()).contains("Download final acceptance completion closeout report.");
        assertThat(archive.sideEffectContract()).contains("read-only");
        assertThat(archive.report()).contains("# PatchPilot Final Acceptance Completion Closeout");
        assertThat(archive.generatedAt()).isEqualTo(Instant.parse("2026-06-29T06:00:00Z"));
        assertThat(archive.archivedAt()).isEqualTo(Instant.parse("2026-06-29T06:30:00Z"));
        assertThat(service.listRecentArchives()).containsExactly(archive);
        assertThat(service.findArchive("final-acceptance-completion-closeout-archive-1")).contains(archive);
    }

    @Test
    void rejects_archive_when_closeout_is_not_ready_and_closed() {
        DemoFinalAcceptanceCompletionCloseoutArchiveService service =
                new DemoFinalAcceptanceCompletionCloseoutArchiveService(
                        DemoFinalAcceptanceCompletionCloseoutArchiveServiceTests::blockedCloseout,
                        new InMemoryDemoFinalAcceptanceCompletionCloseoutArchiveRepository(),
                        Clock.fixed(Instant.parse("2026-06-29T06:30:00Z"), ZoneOffset.UTC),
                        () -> "final-acceptance-completion-closeout-archive-1"
                );

        assertThatThrownBy(service::archiveCurrentCloseout)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("final acceptance completion closeout is not ready");
    }

    @Test
    void keeps_only_twenty_recent_closeout_archives() {
        DemoFinalAcceptanceCompletionCloseoutArchiveService service =
                new DemoFinalAcceptanceCompletionCloseoutArchiveService(
                        DemoFinalAcceptanceCompletionCloseoutArchiveServiceTests::readyCloseout,
                        new InMemoryDemoFinalAcceptanceCompletionCloseoutArchiveRepository(),
                        Clock.fixed(Instant.parse("2026-06-29T06:30:00Z"), ZoneOffset.UTC),
                        new IncrementingIdSupplier()
                );

        for (int index = 1; index <= 22; index++) {
            service.archiveCurrentCloseout();
        }

        assertThat(service.listRecentArchives())
                .hasSize(20)
                .extracting(DemoFinalAcceptanceCompletionCloseoutArchiveVo::id)
                .containsExactly(
                        "final-acceptance-completion-closeout-archive-22",
                        "final-acceptance-completion-closeout-archive-21",
                        "final-acceptance-completion-closeout-archive-20",
                        "final-acceptance-completion-closeout-archive-19",
                        "final-acceptance-completion-closeout-archive-18",
                        "final-acceptance-completion-closeout-archive-17",
                        "final-acceptance-completion-closeout-archive-16",
                        "final-acceptance-completion-closeout-archive-15",
                        "final-acceptance-completion-closeout-archive-14",
                        "final-acceptance-completion-closeout-archive-13",
                        "final-acceptance-completion-closeout-archive-12",
                        "final-acceptance-completion-closeout-archive-11",
                        "final-acceptance-completion-closeout-archive-10",
                        "final-acceptance-completion-closeout-archive-9",
                        "final-acceptance-completion-closeout-archive-8",
                        "final-acceptance-completion-closeout-archive-7",
                        "final-acceptance-completion-closeout-archive-6",
                        "final-acceptance-completion-closeout-archive-5",
                        "final-acceptance-completion-closeout-archive-4",
                        "final-acceptance-completion-closeout-archive-3"
                );
        assertThat(service.findArchive("final-acceptance-completion-closeout-archive-1")).isEmpty();
    }

    private static DemoFinalAcceptanceCompletionCloseoutVo readyCloseout() {
        return new DemoFinalAcceptanceCompletionCloseoutVo(
                DemoReadinessStatus.READY,
                true,
                "PatchPilot final acceptance completion is closed with accepted certificates, finalized sharing, and fresh completion delivery proof.",
                "Use this closeout report as the final external-review completion record.",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/8",
                "final-acceptance-share-package-archive-1",
                "final-acceptance-completion-archive-1",
                "final-acceptance-completion-evidence-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "2026-06-29T04:25:00Z",
                "FRESH",
                List.of(new DemoFinalAcceptanceCompletionCloseoutVo.Check(
                        "Final acceptance summary",
                        DemoReadinessStatus.READY,
                        "Final demo acceptance summary is accepted.",
                        "No action needed."
                )),
                List.of("Final demo acceptance summary is accepted."),
                List.of("Download final acceptance completion closeout report."),
                "GET /api/demo/final-acceptance-completion-closeout is read-only.",
                "# PatchPilot Final Acceptance Completion Closeout\n\n"
                        + "- Latest completion evidence delivery receipt: `final-acceptance-completion-evidence-delivery-receipt-1`\n",
                Instant.parse("2026-06-29T06:00:00Z")
        );
    }

    private static DemoFinalAcceptanceCompletionCloseoutVo blockedCloseout() {
        DemoFinalAcceptanceCompletionCloseoutVo ready = readyCloseout();
        return new DemoFinalAcceptanceCompletionCloseoutVo(
                DemoReadinessStatus.BLOCKED,
                false,
                "PatchPilot final acceptance completion closeout is blocked.",
                "Resolve final acceptance blockers.",
                ready.latestTaskId(),
                ready.latestPullRequestUrl(),
                ready.latestSharePackageArchiveId(),
                ready.latestCompletionArchiveId(),
                ready.latestCompletionEvidenceDeliveryReceiptId(),
                ready.latestDeliveryTarget(),
                ready.latestDeliveryChannel(),
                ready.latestDeliveredAt(),
                "MISSING",
                ready.checks(),
                ready.evidenceNotes(),
                ready.downloadActions(),
                ready.sideEffectContract(),
                ready.markdownReport(),
                ready.generatedAt()
        );
    }

    private static final class IncrementingIdSupplier implements java.util.function.Supplier<String> {

        private int nextId = 1;

        @Override
        public String get() {
            return "final-acceptance-completion-closeout-archive-" + nextId++;
        }
    }
}
