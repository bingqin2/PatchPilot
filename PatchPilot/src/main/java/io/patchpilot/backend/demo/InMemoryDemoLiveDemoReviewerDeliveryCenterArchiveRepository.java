package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoReviewerDeliveryCenterArchiveVo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryDemoLiveDemoReviewerDeliveryCenterArchiveRepository
        implements DemoLiveDemoReviewerDeliveryCenterArchiveRepository {

    private static final int MAX_ARCHIVES = 20;

    private final List<DemoLiveDemoReviewerDeliveryCenterArchiveVo> archives =
            new CopyOnWriteArrayList<>();

    @Override
    public DemoLiveDemoReviewerDeliveryCenterArchiveVo save(
            DemoLiveDemoReviewerDeliveryCenterArchiveVo archive
    ) {
        archives.add(0, archive);
        trimArchives();
        return archive;
    }

    @Override
    public List<DemoLiveDemoReviewerDeliveryCenterArchiveVo> listRecentArchives(int limit) {
        return archives.stream()
                .limit(limit)
                .toList();
    }

    @Override
    public Optional<DemoLiveDemoReviewerDeliveryCenterArchiveVo> findById(String archiveId) {
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
