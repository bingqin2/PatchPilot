package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoEvidenceBundleArchiveVo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryDemoLiveDemoEvidenceBundleArchiveRepository
        implements DemoLiveDemoEvidenceBundleArchiveRepository {

    private static final int MAX_ARCHIVES = 20;

    private final List<DemoLiveDemoEvidenceBundleArchiveVo> archives = new CopyOnWriteArrayList<>();

    @Override
    public DemoLiveDemoEvidenceBundleArchiveVo save(DemoLiveDemoEvidenceBundleArchiveVo archive) {
        archives.add(0, archive);
        trimArchives();
        return archive;
    }

    @Override
    public List<DemoLiveDemoEvidenceBundleArchiveVo> listRecentArchives(int limit) {
        return archives.stream()
                .limit(limit)
                .toList();
    }

    @Override
    public Optional<DemoLiveDemoEvidenceBundleArchiveVo> findById(String archiveId) {
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
