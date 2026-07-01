package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveTriggerOutcomeCloseoutArchiveVo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryDemoLiveTriggerOutcomeCloseoutArchiveRepository
        implements DemoLiveTriggerOutcomeCloseoutArchiveRepository {

    private static final int MAX_ARCHIVES = 20;

    private final List<DemoLiveTriggerOutcomeCloseoutArchiveVo> archives = new CopyOnWriteArrayList<>();

    @Override
    public DemoLiveTriggerOutcomeCloseoutArchiveVo save(DemoLiveTriggerOutcomeCloseoutArchiveVo archive) {
        archives.add(0, archive);
        trimArchives();
        return archive;
    }

    @Override
    public List<DemoLiveTriggerOutcomeCloseoutArchiveVo> listRecentArchives(int limit) {
        return archives.stream()
                .limit(limit)
                .toList();
    }

    @Override
    public Optional<DemoLiveTriggerOutcomeCloseoutArchiveVo> findById(String archiveId) {
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
