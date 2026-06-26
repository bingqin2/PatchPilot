package io.patchpilot.backend.evaluation.service.impl;

import io.patchpilot.backend.evaluation.domain.EvaluationRunSnapshotArchiveVo;
import io.patchpilot.backend.evaluation.service.EvaluationRunSnapshotArchiveRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
@Profile("default")
public class InMemoryEvaluationRunSnapshotArchiveRepository implements EvaluationRunSnapshotArchiveRepository {

    private static final int MAX_ARCHIVES = 20;

    private final List<EvaluationRunSnapshotArchiveVo> archives = new CopyOnWriteArrayList<>();

    @Override
    public EvaluationRunSnapshotArchiveVo save(EvaluationRunSnapshotArchiveVo archive) {
        archives.add(0, archive);
        trimArchives();
        return archive;
    }

    @Override
    public List<EvaluationRunSnapshotArchiveVo> listRecentArchives(int limit) {
        return archives.stream()
                .limit(limit)
                .toList();
    }

    @Override
    public Optional<EvaluationRunSnapshotArchiveVo> findById(String archiveId) {
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
