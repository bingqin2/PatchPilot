package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoHandoffPackageArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryDemoHandoffPackageArchiveRepositoryTests {

    @Test
    void should_save_find_and_list_recent_handoff_package_archives() {
        InMemoryDemoHandoffPackageArchiveRepository repository = new InMemoryDemoHandoffPackageArchiveRepository();
        DemoHandoffPackageArchiveVo first = archive("handoff-archive-1", "2026-06-24T04:00:00Z");
        DemoHandoffPackageArchiveVo second = archive("handoff-archive-2", "2026-06-24T04:01:00Z");

        repository.save(first);
        repository.save(second);

        assertThat(repository.findById("handoff-archive-1")).contains(first);
        assertThat(repository.listRecentArchives(10)).containsExactly(second, first);
        assertThat(repository.listRecentArchives(1)).containsExactly(second);
    }

    private static DemoHandoffPackageArchiveVo archive(String id, String createdAt) {
        return new DemoHandoffPackageArchiveVo(
                id,
                "demo-session-1",
                DemoReadinessStatus.READY,
                "Demo session is ready.",
                "Status READY; recent PR available.",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                Instant.parse(createdAt),
                "# PatchPilot Demo Handoff Package"
        );
    }
}
