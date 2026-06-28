package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageAcceptanceCertificateArchiveVo;
import io.patchpilot.backend.task.service.FixTaskEvidencePackageAcceptanceCertificateArchiveRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
@Profile("default")
public class InMemoryFixTaskEvidencePackageAcceptanceCertificateArchiveRepository
        implements FixTaskEvidencePackageAcceptanceCertificateArchiveRepository {

    private static final int MAX_ARCHIVES = 20;

    private final List<FixTaskEvidencePackageAcceptanceCertificateArchiveVo> archives =
            new CopyOnWriteArrayList<>();

    @Override
    public FixTaskEvidencePackageAcceptanceCertificateArchiveVo save(
            FixTaskEvidencePackageAcceptanceCertificateArchiveVo archive
    ) {
        archives.add(0, archive);
        trimArchives();
        return archive;
    }

    @Override
    public List<FixTaskEvidencePackageAcceptanceCertificateArchiveVo> listRecentArchives(int limit) {
        return archives.stream()
                .limit(limit)
                .toList();
    }

    @Override
    public Optional<FixTaskEvidencePackageAcceptanceCertificateArchiveVo> findById(String archiveId) {
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
