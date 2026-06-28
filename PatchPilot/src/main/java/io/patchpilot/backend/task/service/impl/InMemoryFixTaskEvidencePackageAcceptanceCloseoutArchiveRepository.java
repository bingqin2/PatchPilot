package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageAcceptanceCloseoutArchiveVo;
import io.patchpilot.backend.task.service.FixTaskEvidencePackageAcceptanceCloseoutArchiveRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
@Profile("default")
public class InMemoryFixTaskEvidencePackageAcceptanceCloseoutArchiveRepository
        implements FixTaskEvidencePackageAcceptanceCloseoutArchiveRepository {

    private static final int MAX_ARCHIVES = 20;

    private final List<FixTaskEvidencePackageAcceptanceCloseoutArchiveVo> archives = new CopyOnWriteArrayList<>();

    @Override
    public FixTaskEvidencePackageAcceptanceCloseoutArchiveVo save(
            FixTaskEvidencePackageAcceptanceCloseoutArchiveVo archive
    ) {
        archives.add(0, archive);
        trimArchives();
        return archive;
    }

    @Override
    public List<FixTaskEvidencePackageAcceptanceCloseoutArchiveVo> listRecentArchives(int limit) {
        return archives.stream()
                .limit(limit)
                .toList();
    }

    @Override
    public Optional<FixTaskEvidencePackageAcceptanceCloseoutArchiveVo> findById(String archiveId) {
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
