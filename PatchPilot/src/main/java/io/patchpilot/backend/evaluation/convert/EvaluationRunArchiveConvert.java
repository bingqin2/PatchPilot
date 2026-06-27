package io.patchpilot.backend.evaluation.convert;

import io.patchpilot.backend.evaluation.domain.EvaluationRunArchiveVo;
import io.patchpilot.backend.evaluation.domain.entity.EvaluationRunArchiveEntity;

import java.util.Arrays;
import java.util.List;

public final class EvaluationRunArchiveConvert {

    private EvaluationRunArchiveConvert() {
    }

    public static EvaluationRunArchiveEntity toEntity(EvaluationRunArchiveVo archive) {
        EvaluationRunArchiveEntity entity = new EvaluationRunArchiveEntity();
        entity.setId(archive.id());
        entity.setStatus(archive.status());
        entity.setTotalCaseCount(archive.totalCaseCount());
        entity.setSupportedFixCaseCount(archive.supportedFixCaseCount());
        entity.setSafetyRejectionCaseCount(archive.safetyRejectionCaseCount());
        entity.setExecutedFixCaseCount(archive.executedFixCaseCount());
        entity.setPassedFixCaseCount(archive.passedFixCaseCount());
        entity.setFailedFixCaseCount(archive.failedFixCaseCount());
        entity.setSkippedCaseCount(archive.skippedCaseCount());
        entity.setCoveredLanguages(joinCsv(archive.coveredLanguages()));
        entity.setCoveredBuildSystems(joinCsv(archive.coveredBuildSystems()));
        entity.setSafetyRejectionCategories(joinCsv(archive.safetyRejectionCategories()));
        entity.setCreatedAt(archive.createdAt());
        entity.setSideEffectContract(archive.sideEffectContract());
        entity.setNextAction(archive.nextAction());
        entity.setReport(archive.report());
        return entity;
    }

    public static EvaluationRunArchiveVo toVo(EvaluationRunArchiveEntity entity) {
        return new EvaluationRunArchiveVo(
                entity.getId(),
                entity.getStatus(),
                entity.getTotalCaseCount(),
                entity.getSupportedFixCaseCount(),
                entity.getSafetyRejectionCaseCount(),
                entity.getExecutedFixCaseCount(),
                entity.getPassedFixCaseCount(),
                entity.getFailedFixCaseCount(),
                entity.getSkippedCaseCount(),
                splitCsv(entity.getCoveredLanguages()),
                splitCsv(entity.getCoveredBuildSystems()),
                splitCsv(entity.getSafetyRejectionCategories()),
                entity.getCreatedAt(),
                entity.getSideEffectContract(),
                entity.getNextAction(),
                entity.getReport()
        );
    }

    private static String joinCsv(List<String> values) {
        return String.join(",", values);
    }

    private static List<String> splitCsv(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .toList();
    }
}
