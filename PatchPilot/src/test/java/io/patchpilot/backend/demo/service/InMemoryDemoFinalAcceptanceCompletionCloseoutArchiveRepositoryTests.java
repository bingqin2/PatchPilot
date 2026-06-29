package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionCloseoutArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoFinalAcceptanceCompletionCloseoutArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryDemoFinalAcceptanceCompletionCloseoutArchiveRepositoryTests {

    @Test
    void stores_recent_closeout_archives_newest_first_and_trims_old_entries() {
        InMemoryDemoFinalAcceptanceCompletionCloseoutArchiveRepository repository =
                new InMemoryDemoFinalAcceptanceCompletionCloseoutArchiveRepository();

        for (int index = 1; index <= 22; index++) {
            repository.save(archive("archive-" + index, Instant.parse("2026-06-29T06:00:00Z").plusSeconds(index)));
        }

        assertThat(repository.listRecentArchives(5))
                .extracting(DemoFinalAcceptanceCompletionCloseoutArchiveVo::id)
                .containsExactly("archive-22", "archive-21", "archive-20", "archive-19", "archive-18");
        assertThat(repository.findById("archive-22")).isPresent();
        assertThat(repository.findById("archive-1")).isEmpty();
    }

    private static DemoFinalAcceptanceCompletionCloseoutArchiveVo archive(String id, Instant archivedAt) {
        return new DemoFinalAcceptanceCompletionCloseoutArchiveVo(
                id,
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
                List.of("Final demo acceptance summary is accepted."),
                List.of("Download final acceptance completion closeout report."),
                "GET /api/demo/final-acceptance-completion-closeout is read-only.",
                "# PatchPilot Final Acceptance Completion Closeout",
                Instant.parse("2026-06-29T06:00:00Z"),
                archivedAt
        );
    }
}
