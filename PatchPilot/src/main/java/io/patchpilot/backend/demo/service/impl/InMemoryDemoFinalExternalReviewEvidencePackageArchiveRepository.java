package io.patchpilot.backend.demo.service.impl;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageArchiveVo;
import io.patchpilot.backend.demo.service.DemoFinalExternalReviewEvidencePackageArchiveRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
@Profile("default")
public class InMemoryDemoFinalExternalReviewEvidencePackageArchiveRepository
        implements DemoFinalExternalReviewEvidencePackageArchiveRepository {

    private static final int MAX_ARCHIVES = 20;

    private final List<DemoFinalExternalReviewEvidencePackageArchiveVo> archives = new CopyOnWriteArrayList<>();

    @Override
    public DemoFinalExternalReviewEvidencePackageArchiveVo save(
            DemoFinalExternalReviewEvidencePackageArchiveVo archive
    ) {
        archives.add(0, archive);
        trimArchives();
        return archive;
    }

    @Override
    public List<DemoFinalExternalReviewEvidencePackageArchiveVo> listRecentArchives(int limit) {
        return archives.stream()
                .limit(limit)
                .toList();
    }

    @Override
    public Optional<DemoFinalExternalReviewEvidencePackageArchiveVo> findById(String archiveId) {
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
