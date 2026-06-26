package io.patchpilot.backend.evaluation.service;

import io.patchpilot.backend.evaluation.domain.EvaluationFixtureBaselineRunArchiveVo;

import java.util.List;
import java.util.Optional;

public interface EvaluationFixtureBaselineRunArchiveRepository {

    EvaluationFixtureBaselineRunArchiveVo save(EvaluationFixtureBaselineRunArchiveVo archive);

    List<EvaluationFixtureBaselineRunArchiveVo> listRecentArchives(int limit);

    Optional<EvaluationFixtureBaselineRunArchiveVo> findById(String archiveId);
}
