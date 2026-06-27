package io.patchpilot.backend.evaluation.service;

import io.patchpilot.backend.evaluation.domain.EvaluationRunArchiveVo;

import java.util.List;
import java.util.Optional;

public interface EvaluationRunArchiveRepository {

    EvaluationRunArchiveVo save(EvaluationRunArchiveVo archive);

    List<EvaluationRunArchiveVo> listRecentArchives(int limit);

    Optional<EvaluationRunArchiveVo> findById(String archiveId);
}
