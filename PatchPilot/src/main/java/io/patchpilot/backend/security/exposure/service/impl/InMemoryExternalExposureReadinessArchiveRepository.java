package io.patchpilot.backend.security.exposure.service.impl;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureReadinessArchiveVo;
import io.patchpilot.backend.security.exposure.service.ExternalExposureReadinessArchiveRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
@Profile("default")
public class InMemoryExternalExposureReadinessArchiveRepository implements ExternalExposureReadinessArchiveRepository {

    private static final int MAX_ARCHIVES = 20;

    private final List<ExternalExposureReadinessArchiveVo> archives = new CopyOnWriteArrayList<>();

    @Override
    public ExternalExposureReadinessArchiveVo save(ExternalExposureReadinessArchiveVo archive) {
        archives.add(0, archive);
        trimArchives();
        return archive;
    }

    @Override
    public List<ExternalExposureReadinessArchiveVo> listRecentArchives(int limit) {
        return archives.stream()
                .limit(limit)
                .toList();
    }

    @Override
    public Optional<ExternalExposureReadinessArchiveVo> findById(String archiveId) {
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
