package io.patchpilot.backend.demo.service.impl;

import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveVo;
import io.patchpilot.backend.demo.service.DemoHandoffPackageArchiveRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
@Profile("default")
public class InMemoryDemoHandoffPackageArchiveRepository implements DemoHandoffPackageArchiveRepository {

    private static final int MAX_ARCHIVES = 20;

    private final List<DemoHandoffPackageArchiveVo> archives = new CopyOnWriteArrayList<>();

    @Override
    public DemoHandoffPackageArchiveVo save(DemoHandoffPackageArchiveVo archive) {
        archives.add(0, archive);
        trimArchives();
        return archive;
    }

    @Override
    public List<DemoHandoffPackageArchiveVo> listRecentArchives(int limit) {
        return archives.stream()
                .limit(limit)
                .toList();
    }

    @Override
    public Optional<DemoHandoffPackageArchiveVo> findById(String archiveId) {
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
