package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceShareFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoFinalAcceptanceCompletionArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DemoFinalAcceptanceCompletionArchiveServiceTests {

    @Test
    void archives_ready_final_acceptance_completion() {
        DemoFinalAcceptanceCompletionArchiveService service = new DemoFinalAcceptanceCompletionArchiveService(
                DemoFinalAcceptanceCompletionArchiveServiceTests::readyFinalization,
                new InMemoryDemoFinalAcceptanceCompletionArchiveRepository(),
                Clock.fixed(Instant.parse("2026-06-29T04:00:00Z"), ZoneOffset.UTC),
                () -> "final-acceptance-completion-archive-1"
        );

        DemoFinalAcceptanceCompletionArchiveVo archive = service.archiveCurrentCompletion();

        assertThat(archive.id()).isEqualTo("final-acceptance-completion-archive-1");
        assertThat(archive.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(archive.finalized()).isTrue();
        assertThat(archive.summary()).isEqualTo("Final demo acceptance share package is finalized with a fresh delivery receipt.");
        assertThat(archive.nextAction()).isEqualTo("Use the finalization report as the external-review acceptance delivery record.");
        assertThat(archive.latestArchiveId()).isEqualTo("final-acceptance-share-package-archive-1");
        assertThat(archive.latestTaskId()).isEqualTo("task-1");
        assertThat(archive.latestDeliveryReceiptId()).isEqualTo("final-acceptance-delivery-receipt-1");
        assertThat(archive.latestDeliveryTarget()).isEqualTo("reviewer@example.com");
        assertThat(archive.latestDeliveryChannel()).isEqualTo("email");
        assertThat(archive.deliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(archive.deliveryReceiptFresh()).isTrue();
        assertThat(archive.evidenceNotes()).contains("Latest final acceptance share package archive is send-ready.");
        assertThat(archive.report()).contains("# PatchPilot Final Demo Acceptance Share Finalization Gate");
        assertThat(archive.generatedAt()).isEqualTo(Instant.parse("2026-06-29T03:30:00Z"));
        assertThat(archive.archivedAt()).isEqualTo(Instant.parse("2026-06-29T04:00:00Z"));
        assertThat(service.listRecentArchives()).containsExactly(archive);
        assertThat(service.findArchive("final-acceptance-completion-archive-1")).contains(archive);
    }

    @Test
    void rejects_archive_when_final_acceptance_is_not_finalized() {
        DemoFinalAcceptanceCompletionArchiveService service = new DemoFinalAcceptanceCompletionArchiveService(
                DemoFinalAcceptanceCompletionArchiveServiceTests::missingReceiptFinalization,
                new InMemoryDemoFinalAcceptanceCompletionArchiveRepository(),
                Clock.fixed(Instant.parse("2026-06-29T04:00:00Z"), ZoneOffset.UTC),
                () -> "final-acceptance-completion-archive-1"
        );

        assertThatThrownBy(service::archiveCurrentCompletion)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("final acceptance share finalization is not ready");
    }

    @Test
    void keeps_only_twenty_recent_completion_archives() {
        DemoFinalAcceptanceCompletionArchiveService service = new DemoFinalAcceptanceCompletionArchiveService(
                DemoFinalAcceptanceCompletionArchiveServiceTests::readyFinalization,
                new InMemoryDemoFinalAcceptanceCompletionArchiveRepository(),
                Clock.fixed(Instant.parse("2026-06-29T04:00:00Z"), ZoneOffset.UTC),
                new IncrementingIdSupplier()
        );

        for (int index = 1; index <= 22; index++) {
            service.archiveCurrentCompletion();
        }

        assertThat(service.listRecentArchives())
                .hasSize(20)
                .extracting(DemoFinalAcceptanceCompletionArchiveVo::id)
                .containsExactly(
                        "final-acceptance-completion-archive-22",
                        "final-acceptance-completion-archive-21",
                        "final-acceptance-completion-archive-20",
                        "final-acceptance-completion-archive-19",
                        "final-acceptance-completion-archive-18",
                        "final-acceptance-completion-archive-17",
                        "final-acceptance-completion-archive-16",
                        "final-acceptance-completion-archive-15",
                        "final-acceptance-completion-archive-14",
                        "final-acceptance-completion-archive-13",
                        "final-acceptance-completion-archive-12",
                        "final-acceptance-completion-archive-11",
                        "final-acceptance-completion-archive-10",
                        "final-acceptance-completion-archive-9",
                        "final-acceptance-completion-archive-8",
                        "final-acceptance-completion-archive-7",
                        "final-acceptance-completion-archive-6",
                        "final-acceptance-completion-archive-5",
                        "final-acceptance-completion-archive-4",
                        "final-acceptance-completion-archive-3"
                );
        assertThat(service.findArchive("final-acceptance-completion-archive-1")).isEmpty();
    }

    private static DemoFinalAcceptanceShareFinalizationVo readyFinalization() {
        return new DemoFinalAcceptanceShareFinalizationVo(
                DemoReadinessStatus.READY,
                true,
                "Final demo acceptance share package is finalized with a fresh delivery receipt.",
                "Use the finalization report as the external-review acceptance delivery record.",
                "final-acceptance-share-package-archive-1",
                "task-1",
                "final-acceptance-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "2026-06-29T03:05:00Z",
                "FRESH",
                true,
                "Latest delivery receipt matches the current final acceptance share package archive.",
                List.of(new DemoFinalAcceptanceShareFinalizationVo.Check(
                        "Final acceptance delivery evidence",
                        DemoReadinessStatus.READY,
                        "Latest delivery receipt matches the current final acceptance share package archive.",
                        "No action needed."
                )),
                List.of("Latest final acceptance share package archive is send-ready."),
                "# PatchPilot Final Demo Acceptance Share Finalization Gate",
                Instant.parse("2026-06-29T03:30:00Z")
        );
    }

    private static DemoFinalAcceptanceShareFinalizationVo missingReceiptFinalization() {
        return new DemoFinalAcceptanceShareFinalizationVo(
                DemoReadinessStatus.NEEDS_ATTENTION,
                false,
                "Final demo acceptance share package is send-ready but final delivery evidence is not current.",
                "Record a final acceptance share delivery receipt.",
                "final-acceptance-share-package-archive-1",
                "task-1",
                null,
                null,
                null,
                null,
                "MISSING",
                false,
                "No final acceptance share delivery receipt has been recorded for the current archive.",
                List.of(),
                List.of(),
                "# PatchPilot Final Demo Acceptance Share Finalization Gate",
                Instant.parse("2026-06-29T03:30:00Z")
        );
    }

    private static final class IncrementingIdSupplier implements java.util.function.Supplier<String> {

        private int nextId = 1;

        @Override
        public String get() {
            return "final-acceptance-completion-archive-" + nextId++;
        }
    }
}
