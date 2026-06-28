package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageAcceptanceCloseoutArchiveVo;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskEvidencePackageAcceptanceCloseoutArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryFixTaskEvidencePackageAcceptanceCloseoutArchiveRepositoryTests {

    @Test
    void should_list_recent_archives_and_find_by_id() {
        InMemoryFixTaskEvidencePackageAcceptanceCloseoutArchiveRepository repository =
                new InMemoryFixTaskEvidencePackageAcceptanceCloseoutArchiveRepository();
        FixTaskEvidencePackageAcceptanceCloseoutArchiveVo older =
                archive("task-evidence-closeout-archive-old", "2026-06-28T06:55:00Z");
        FixTaskEvidencePackageAcceptanceCloseoutArchiveVo newer =
                archive("task-evidence-closeout-archive-new", "2026-06-28T07:00:00Z");

        repository.save(older);
        repository.save(newer);

        assertThat(repository.listRecentArchives(1)).containsExactly(newer);
        assertThat(repository.listRecentArchives(20)).containsExactly(newer, older);
        assertThat(repository.findById("task-evidence-closeout-archive-old")).contains(older);
        assertThat(repository.findById("missing-closeout-archive")).isEmpty();
    }

    private static FixTaskEvidencePackageAcceptanceCloseoutArchiveVo archive(String id, String createdAt) {
        return new FixTaskEvidencePackageAcceptanceCloseoutArchiveVo(
                id,
                "READY",
                true,
                "Task evidence is finalized with a fresh delivery receipt for the current shareable archive.",
                "task-evidence-archive-1",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/8",
                "task-evidence-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "FRESH",
                Instant.parse(createdAt),
                "# PatchPilot Task Evidence Acceptance Closeout Archive"
        );
    }
}
