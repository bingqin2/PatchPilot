package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoLaunchEvidencePackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoLaunchEvidencePackageArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryDemoLaunchEvidencePackageArchiveRepositoryTests {

    @Test
    void saves_lists_and_finds_launch_evidence_archives() {
        InMemoryDemoLaunchEvidencePackageArchiveRepository repository =
                new InMemoryDemoLaunchEvidencePackageArchiveRepository();
        DemoLaunchEvidencePackageArchiveVo first = archive("archive-1", Instant.parse("2026-06-28T02:00:00Z"));
        DemoLaunchEvidencePackageArchiveVo second = archive("archive-2", Instant.parse("2026-06-28T02:01:00Z"));

        repository.save(first);
        repository.save(second);

        assertThat(repository.listRecentArchives(1)).containsExactly(second);
        assertThat(repository.findById("archive-1")).contains(first);
        assertThat(repository.findById("missing")).isEmpty();
    }

    private static DemoLaunchEvidencePackageArchiveVo archive(String id, Instant createdAt) {
        return new DemoLaunchEvidencePackageArchiveVo(
                id,
                DemoReadinessStatus.READY,
                true,
                "PatchPilot launch evidence package is ready to share.",
                "demo-session-20260624T003000Z",
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "delivery-1",
                "evaluation-run-2",
                createdAt,
                "# PatchPilot Demo Launch Evidence Package"
        );
    }
}
