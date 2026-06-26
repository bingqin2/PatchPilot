package io.patchpilot.backend.evaluation.convert;

import io.patchpilot.backend.evaluation.domain.EvaluationFixtureBaselineRunArchiveVo;
import io.patchpilot.backend.evaluation.domain.entity.EvaluationFixtureBaselineRunArchiveEntity;

public final class EvaluationFixtureBaselineRunArchiveConvert {

    private EvaluationFixtureBaselineRunArchiveConvert() {
    }

    public static EvaluationFixtureBaselineRunArchiveEntity toEntity(EvaluationFixtureBaselineRunArchiveVo archive) {
        EvaluationFixtureBaselineRunArchiveEntity entity = new EvaluationFixtureBaselineRunArchiveEntity();
        entity.setId(archive.id());
        entity.setStatus(archive.status());
        entity.setTotalCaseCount(archive.totalCaseCount());
        entity.setExecutedCaseCount(archive.executedCaseCount());
        entity.setPassedCaseCount(archive.passedCaseCount());
        entity.setFailedCaseCount(archive.failedCaseCount());
        entity.setSkippedCaseCount(archive.skippedCaseCount());
        entity.setCreatedAt(archive.createdAt());
        entity.setSideEffectContract(archive.sideEffectContract());
        entity.setNextAction(archive.nextAction());
        entity.setReport(archive.report());
        return entity;
    }

    public static EvaluationFixtureBaselineRunArchiveVo toVo(EvaluationFixtureBaselineRunArchiveEntity entity) {
        return new EvaluationFixtureBaselineRunArchiveVo(
                entity.getId(),
                entity.getStatus(),
                entity.getTotalCaseCount(),
                entity.getExecutedCaseCount(),
                entity.getPassedCaseCount(),
                entity.getFailedCaseCount(),
                entity.getSkippedCaseCount(),
                entity.getCreatedAt(),
                entity.getSideEffectContract(),
                entity.getNextAction(),
                entity.getReport()
        );
    }
}
