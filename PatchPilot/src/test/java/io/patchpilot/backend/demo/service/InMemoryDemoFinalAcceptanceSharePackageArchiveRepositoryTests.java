package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceSharePackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoFinalAcceptanceSharePackageArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryDemoFinalAcceptanceSharePackageArchiveRepositoryTests {

    @Test
    void stores_recent_archives_newest_first_and_trims_old_entries() {
        InMemoryDemoFinalAcceptanceSharePackageArchiveRepository repository =
                new InMemoryDemoFinalAcceptanceSharePackageArchiveRepository();

        for (int index = 1; index <= 22; index++) {
            repository.save(archive("archive-" + index, Instant.parse("2026-06-29T02:00:00Z").plusSeconds(index)));
        }

        List<DemoFinalAcceptanceSharePackageArchiveVo> archives = repository.listRecentArchives(5);

        assertThat(archives)
                .extracting(DemoFinalAcceptanceSharePackageArchiveVo::id)
                .containsExactly("archive-22", "archive-21", "archive-20", "archive-19", "archive-18");
        assertThat(repository.findById("archive-22")).isPresent();
        assertThat(repository.findById("archive-1")).isEmpty();
    }

    private static DemoFinalAcceptanceSharePackageArchiveVo archive(String id, Instant archivedAt) {
        return new DemoFinalAcceptanceSharePackageArchiveVo(
                id,
                DemoReadinessStatus.READY,
                true,
                "PatchPilot final demo acceptance package is ready to send.",
                "Send the prepared final acceptance message with all required attachments.",
                "launch-certificate-archive-1",
                "task-evidence-certificate-archive-1",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                List.of("Repository owner or maintainer", "Demo reviewer"),
                List.of("Final demo acceptance summary report"),
                List.of("Confirm final demo acceptance status is READY and accepted."),
                "PatchPilot final demo acceptance: task-1",
                "PatchPilot final demo acceptance is ready for external review.",
                List.of("Final acceptance status is READY."),
                "POST /api/demo/final-acceptance-share-package/archives archives a read-only snapshot and does not create tasks, call the model, run tests, mutate Git, send messages, or write to GitHub.",
                "# PatchPilot Final Demo Acceptance Share Package",
                Instant.parse("2026-06-29T01:30:00Z"),
                archivedAt
        );
    }
}
