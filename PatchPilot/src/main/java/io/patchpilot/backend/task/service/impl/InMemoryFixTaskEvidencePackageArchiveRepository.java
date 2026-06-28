package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageArchiveVo;
import io.patchpilot.backend.task.service.FixTaskEvidencePackageArchiveRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
@Profile("default")
public class InMemoryFixTaskEvidencePackageArchiveRepository implements FixTaskEvidencePackageArchiveRepository {

    private static final int MAX_ARCHIVES = 50;

    private final List<FixTaskEvidencePackageArchiveVo> archives = new CopyOnWriteArrayList<>();

    @Override
    public FixTaskEvidencePackageArchiveVo save(FixTaskEvidencePackageArchiveVo archive) {
        archives.add(0, archive);
        trimArchives();
        return archive;
    }

    @Override
    public List<FixTaskEvidencePackageArchiveVo> listByTaskId(String taskId, int limit) {
        return archives.stream()
                .filter(archive -> archive.taskId().equals(taskId))
                .limit(limit)
                .toList();
    }

    @Override
    public List<FixTaskEvidencePackageArchiveVo> listRecent(int limit) {
        return archives.stream()
                .limit(limit)
                .toList();
    }

    @Override
    public Optional<FixTaskEvidencePackageArchiveVo> findById(String archiveId) {
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
