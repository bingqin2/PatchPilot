package io.patchpilot.backend.security.exposure.service.impl;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureCloseoutArchiveVo;
import io.patchpilot.backend.security.exposure.service.ExternalExposureCloseoutArchiveRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
@Profile("default")
public class InMemoryExternalExposureCloseoutArchiveRepository implements ExternalExposureCloseoutArchiveRepository {

    private static final int MAX_ARCHIVES = 20;

    private final List<ExternalExposureCloseoutArchiveVo> archives = new CopyOnWriteArrayList<>();

    @Override
    public ExternalExposureCloseoutArchiveVo save(ExternalExposureCloseoutArchiveVo archive) {
        archives.add(0, archive);
        trimArchives();
        return archive;
    }

    @Override
    public List<ExternalExposureCloseoutArchiveVo> listRecentArchives(int limit) {
        return archives.stream()
                .limit(limit)
                .toList();
    }

    @Override
    public Optional<ExternalExposureCloseoutArchiveVo> findById(String archiveId) {
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
