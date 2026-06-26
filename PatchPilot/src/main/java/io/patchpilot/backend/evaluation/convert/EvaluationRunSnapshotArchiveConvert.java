package io.patchpilot.backend.evaluation.convert;

import io.patchpilot.backend.evaluation.domain.EvaluationRunSnapshotArchiveVo;
import io.patchpilot.backend.evaluation.domain.entity.EvaluationRunSnapshotArchiveEntity;

import java.util.Arrays;
import java.util.List;

public final class EvaluationRunSnapshotArchiveConvert {

    private EvaluationRunSnapshotArchiveConvert() {
    }

    public static EvaluationRunSnapshotArchiveEntity toEntity(EvaluationRunSnapshotArchiveVo archive) {
        EvaluationRunSnapshotArchiveEntity entity = new EvaluationRunSnapshotArchiveEntity();
        entity.setId(archive.id());
        entity.setPreviewRunId(archive.previewRunId());
        entity.setTitle(archive.title());
        entity.setStatus(archive.status());
        entity.setCaseCount(archive.caseCount());
        entity.setSupportedFixCaseCount(archive.supportedFixCaseCount());
        entity.setSafetyRejectionCaseCount(archive.safetyRejectionCaseCount());
        entity.setCoveredLanguages(joinCsv(archive.coveredLanguages()));
        entity.setCoveredBuildSystems(joinCsv(archive.coveredBuildSystems()));
        entity.setExpectedVerificationCommands(String.join("\n", archive.expectedVerificationCommands()));
        entity.setSafetyRejectionCategories(joinCsv(archive.safetyRejectionCategories()));
        entity.setCreatedAt(archive.createdAt());
        entity.setSideEffectContract(archive.sideEffectContract());
        entity.setReport(archive.report());
        return entity;
    }

    public static EvaluationRunSnapshotArchiveVo toVo(EvaluationRunSnapshotArchiveEntity entity) {
        return new EvaluationRunSnapshotArchiveVo(
                entity.getId(),
                entity.getPreviewRunId(),
                entity.getTitle(),
                entity.getStatus(),
                entity.getCaseCount(),
                entity.getSupportedFixCaseCount(),
                entity.getSafetyRejectionCaseCount(),
                splitCsv(entity.getCoveredLanguages()),
                splitCsv(entity.getCoveredBuildSystems()),
                splitLines(entity.getExpectedVerificationCommands()),
                splitCsv(entity.getSafetyRejectionCategories()),
                entity.getCreatedAt(),
                entity.getSideEffectContract(),
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

    private static List<String> splitLines(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return Arrays.stream(value.split("\\R"))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .toList();
    }
}
