package io.patchpilot.backend.demo.service.impl;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionArchiveVo;
import io.patchpilot.backend.demo.service.DemoFinalAcceptanceCompletionArchiveRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
@Profile("default")
public class InMemoryDemoFinalAcceptanceCompletionArchiveRepository
        implements DemoFinalAcceptanceCompletionArchiveRepository {

    private static final int MAX_ARCHIVES = 20;

    private final List<DemoFinalAcceptanceCompletionArchiveVo> archives = new CopyOnWriteArrayList<>();

    @Override
    public DemoFinalAcceptanceCompletionArchiveVo save(DemoFinalAcceptanceCompletionArchiveVo archive) {
        archives.add(0, archive);
        trimArchives();
        return archive;
    }

    @Override
    public List<DemoFinalAcceptanceCompletionArchiveVo> listRecentArchives(int limit) {
        return archives.stream()
                .limit(limit)
                .toList();
    }

    @Override
    public Optional<DemoFinalAcceptanceCompletionArchiveVo> findById(String archiveId) {
        return archives.stream()
                .filter(archive -> archive.id().equals(archiveId))
                .findFirst();
    }

    private void trimArchives() {
        while (archives.size() > MAX_ARCHIVES) {
            archives.remove(archives.size() - 1);
        }
    }
}
