package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCloseoutArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoLaunchAcceptanceCloseoutArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryDemoLaunchAcceptanceCloseoutArchiveRepositoryTests {

    @Test
    void saves_lists_and_finds_launch_acceptance_closeout_archives() {
        InMemoryDemoLaunchAcceptanceCloseoutArchiveRepository repository =
                new InMemoryDemoLaunchAcceptanceCloseoutArchiveRepository();
        DemoLaunchAcceptanceCloseoutArchiveVo first = archive("archive-1", Instant.parse("2026-06-28T08:00:00Z"));
        DemoLaunchAcceptanceCloseoutArchiveVo second = archive("archive-2", Instant.parse("2026-06-28T08:01:00Z"));

        repository.save(first);
        repository.save(second);

        assertThat(repository.listRecentArchives(1)).containsExactly(second);
        assertThat(repository.findById("archive-1")).contains(first);
        assertThat(repository.findById("missing")).isEmpty();
    }

    private static DemoLaunchAcceptanceCloseoutArchiveVo archive(String id, Instant createdAt) {
        return new DemoLaunchAcceptanceCloseoutArchiveVo(
                id,
                DemoReadinessStatus.READY,
                true,
                "PatchPilot launch acceptance closeout is complete.",
                "demo-session-20260624T003000Z",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "delivery-1",
                "evaluation-run-2",
                "launch-evidence-archive-1",
                DemoReadinessStatus.READY,
                true,
                "final-handoff-report-package-archive-1",
                "Latest final handoff report package archive is download-ready and ready.",
                "launch-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "FRESH",
                createdAt,
                "# PatchPilot Launch Acceptance Closeout"
        );
    }
}
