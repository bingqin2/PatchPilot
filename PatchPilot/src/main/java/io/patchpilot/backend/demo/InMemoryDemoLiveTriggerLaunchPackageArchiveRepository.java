package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveTriggerLaunchPackageArchiveVo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryDemoLiveTriggerLaunchPackageArchiveRepository
        implements DemoLiveTriggerLaunchPackageArchiveRepository {

    private static final int MAX_ARCHIVES = 20;

    private final List<DemoLiveTriggerLaunchPackageArchiveVo> archives = new CopyOnWriteArrayList<>();

    @Override
    public DemoLiveTriggerLaunchPackageArchiveVo save(DemoLiveTriggerLaunchPackageArchiveVo archive) {
        archives.add(0, archive);
        trimArchives();
        return archive;
    }

    @Override
    public List<DemoLiveTriggerLaunchPackageArchiveVo> listRecentArchives(int limit) {
        return archives.stream()
                .limit(limit)
                .toList();
    }

    @Override
    public Optional<DemoLiveTriggerLaunchPackageArchiveVo> findById(String archiveId) {
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
