package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoSessionArchiveVo;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoSessionArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryDemoSessionArchiveRepositoryTests {

    private final InMemoryDemoSessionArchiveRepository repository = new InMemoryDemoSessionArchiveRepository();

    @Test
    void should_save_archives_newest_first_and_cap_to_twenty() {
        for (int index = 1; index <= 25; index++) {
            repository.save(archive("archive-" + index, "session-" + index, Instant.parse("2026-06-24T04:00:00Z")));
        }

        List<DemoSessionArchiveVo> archives = repository.listRecentArchives(20);

        assertThat(archives).hasSize(20);
        assertThat(archives.get(0).id()).isEqualTo("archive-25");
        assertThat(archives.get(0).sessionId()).isEqualTo("session-25");
        assertThat(archives.get(19).id()).isEqualTo("archive-6");
    }

    private static DemoSessionArchiveVo archive(String id, String sessionId, Instant createdAt) {
        return new DemoSessionArchiveVo(
                id,
                sessionId,
                DemoReadinessStatus.READY,
                "Demo session " + sessionId + " is ready.",
                "Status READY; session " + sessionId + ".",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                createdAt,
                "# PatchPilot Demo Session Report\n\n- Session: `" + sessionId + "`"
        );
    }
}
