package io.patchpilot.backend.evaluation.service;

import io.patchpilot.backend.evaluation.domain.EvaluationRunSnapshotArchiveVo;

import java.util.List;
import java.util.Optional;

public interface EvaluationRunSnapshotArchiveRepository {

    EvaluationRunSnapshotArchiveVo save(EvaluationRunSnapshotArchiveVo archive);

    List<EvaluationRunSnapshotArchiveVo> listRecentArchives(int limit);

    Optional<EvaluationRunSnapshotArchiveVo> findById(String archiveId);
}
