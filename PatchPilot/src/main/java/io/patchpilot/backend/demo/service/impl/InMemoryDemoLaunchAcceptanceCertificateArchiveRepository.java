package io.patchpilot.backend.demo.service.impl;

import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCertificateArchiveVo;
import io.patchpilot.backend.demo.service.DemoLaunchAcceptanceCertificateArchiveRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
@Profile("default")
public class InMemoryDemoLaunchAcceptanceCertificateArchiveRepository implements DemoLaunchAcceptanceCertificateArchiveRepository {

    private static final int MAX_ARCHIVES = 20;

    private final List<DemoLaunchAcceptanceCertificateArchiveVo> archives = new CopyOnWriteArrayList<>();

    @Override
    public DemoLaunchAcceptanceCertificateArchiveVo save(DemoLaunchAcceptanceCertificateArchiveVo archive) {
        archives.add(0, archive);
        trimArchives();
        return archive;
    }

    @Override
    public List<DemoLaunchAcceptanceCertificateArchiveVo> listRecentArchives(int limit) {
        return archives.stream()
                .limit(limit)
                .toList();
    }

    @Override
    public Optional<DemoLaunchAcceptanceCertificateArchiveVo> findById(String archiveId) {
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
