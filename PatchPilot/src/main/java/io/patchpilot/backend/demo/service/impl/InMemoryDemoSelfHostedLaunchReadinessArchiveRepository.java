package io.patchpilot.backend.demo.service.impl;

import io.patchpilot.backend.demo.domain.DemoSelfHostedLaunchReadinessArchiveVo;
import io.patchpilot.backend.demo.service.DemoSelfHostedLaunchReadinessArchiveRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
@Profile("default")
public class InMemoryDemoSelfHostedLaunchReadinessArchiveRepository implements DemoSelfHostedLaunchReadinessArchiveRepository {

    private static final int MAX_ARCHIVES = 20;

    private final List<DemoSelfHostedLaunchReadinessArchiveVo> archives = new CopyOnWriteArrayList<>();

    @Override
    public DemoSelfHostedLaunchReadinessArchiveVo save(DemoSelfHostedLaunchReadinessArchiveVo archive) {
        archives.add(0, archive);
        trimArchives();
        return archive;
    }

    @Override
    public List<DemoSelfHostedLaunchReadinessArchiveVo> listRecentArchives(int limit) {
        return archives.stream()
                .limit(limit)
                .toList();
    }

    @Override
    public Optional<DemoSelfHostedLaunchReadinessArchiveVo> findById(String archiveId) {
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
