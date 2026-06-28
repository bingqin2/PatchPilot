package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoFinalAcceptanceCompletionArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryDemoFinalAcceptanceCompletionArchiveRepositoryTests {

    @Test
    void stores_recent_archives_newest_first_and_trims_old_entries() {
        InMemoryDemoFinalAcceptanceCompletionArchiveRepository repository =
                new InMemoryDemoFinalAcceptanceCompletionArchiveRepository();

        for (int index = 1; index <= 22; index++) {
            repository.save(archive("archive-" + index, Instant.parse("2026-06-29T04:00:00Z").plusSeconds(index)));
        }

        assertThat(repository.listRecentArchives(5))
                .extracting(DemoFinalAcceptanceCompletionArchiveVo::id)
                .containsExactly("archive-22", "archive-21", "archive-20", "archive-19", "archive-18");
        assertThat(repository.findById("archive-22")).isPresent();
        assertThat(repository.findById("archive-1")).isEmpty();
    }

    private static DemoFinalAcceptanceCompletionArchiveVo archive(String id, Instant archivedAt) {
        return new DemoFinalAcceptanceCompletionArchiveVo(
                id,
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
                List.of("Latest final acceptance share package archive is send-ready."),
                "# PatchPilot Final Demo Acceptance Share Finalization Gate",
                Instant.parse("2026-06-29T03:30:00Z"),
                archivedAt
        );
    }
}
