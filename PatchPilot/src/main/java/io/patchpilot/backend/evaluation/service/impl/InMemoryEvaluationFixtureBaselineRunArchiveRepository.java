package io.patchpilot.backend.evaluation.service.impl;

import io.patchpilot.backend.evaluation.domain.EvaluationFixtureBaselineRunArchiveVo;
import io.patchpilot.backend.evaluation.service.EvaluationFixtureBaselineRunArchiveRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
@Profile("default")
public class InMemoryEvaluationFixtureBaselineRunArchiveRepository implements EvaluationFixtureBaselineRunArchiveRepository {

    private static final int MAX_ARCHIVES = 20;

    private final List<EvaluationFixtureBaselineRunArchiveVo> archives = new CopyOnWriteArrayList<>();

    @Override
    public EvaluationFixtureBaselineRunArchiveVo save(EvaluationFixtureBaselineRunArchiveVo archive) {
        archives.add(0, archive);
        trimArchives();
        return archive;
    }

    @Override
    public List<EvaluationFixtureBaselineRunArchiveVo> listRecentArchives(int limit) {
        return archives.stream()
                .limit(limit)
                .toList();
    }

    @Override
    public Optional<EvaluationFixtureBaselineRunArchiveVo> findById(String archiveId) {
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
