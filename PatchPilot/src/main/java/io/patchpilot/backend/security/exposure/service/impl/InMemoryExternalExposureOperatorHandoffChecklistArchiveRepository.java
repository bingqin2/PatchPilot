package io.patchpilot.backend.security.exposure.service.impl;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureOperatorHandoffChecklistArchiveVo;
import io.patchpilot.backend.security.exposure.service.ExternalExposureOperatorHandoffChecklistArchiveRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryExternalExposureOperatorHandoffChecklistArchiveRepository
        implements ExternalExposureOperatorHandoffChecklistArchiveRepository {

    private static final int MAX_ARCHIVES = 20;

    private final List<ExternalExposureOperatorHandoffChecklistArchiveVo> archives = new CopyOnWriteArrayList<>();

    @Override
    public ExternalExposureOperatorHandoffChecklistArchiveVo save(
            ExternalExposureOperatorHandoffChecklistArchiveVo archive
    ) {
        archives.add(0, archive);
        trimArchives();
        return archive;
    }

    @Override
    public List<ExternalExposureOperatorHandoffChecklistArchiveVo> listRecentArchives(int limit) {
        return archives.stream()
                .limit(limit)
                .toList();
    }

    @Override
    public Optional<ExternalExposureOperatorHandoffChecklistArchiveVo> findById(String archiveId) {
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
