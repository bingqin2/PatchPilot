package io.patchpilot.backend.security.exposure.convert;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureCloseoutArchiveVo;
import io.patchpilot.backend.security.exposure.domain.entity.ExternalExposureCloseoutArchiveEntity;

import java.util.Arrays;
import java.util.List;

public final class ExternalExposureCloseoutArchiveConvert {

    private ExternalExposureCloseoutArchiveConvert() {
    }

    public static ExternalExposureCloseoutArchiveEntity toEntity(ExternalExposureCloseoutArchiveVo archive) {
        ExternalExposureCloseoutArchiveEntity entity = new ExternalExposureCloseoutArchiveEntity();
        entity.setId(archive.id());
        entity.setStatus(archive.status());
        entity.setCloseoutReady(archive.closeoutReady());
        entity.setSummary(archive.summary());
        entity.setNextAction(archive.nextAction());
        entity.setLatestSessionId(archive.latestSessionId());
        entity.setLatestSessionStatus(archive.latestSessionStatus());
        entity.setPublicUrl(archive.publicUrl());
        entity.setWebhookUrl(archive.webhookUrl());
        entity.setPurpose(archive.purpose());
        entity.setOperator(archive.operator());
        entity.setStartedAt(archive.startedAt());
        entity.setClosedBy(archive.closedBy());
        entity.setClosedAt(archive.closedAt());
        entity.setCloseNotes(archive.closeNotes());
        entity.setLinkedReadinessArchiveId(archive.linkedReadinessArchiveId());
        entity.setHandoffStatus(archive.handoffStatus());
        entity.setArchiveFreshness(archive.archiveFreshness());
        entity.setReadyCount(archive.readyCount());
        entity.setNeedsAttentionCount(archive.needsAttentionCount());
        entity.setBlockedCount(archive.blockedCount());
        entity.setTotalCount(archive.totalCount());
        entity.setNextActions(joinLines(archive.nextActions()));
        entity.setEvidenceNotes(joinLines(archive.evidenceNotes()));
        entity.setDownloadActions(joinLines(archive.downloadActions()));
        entity.setSideEffectContract(archive.sideEffectContract());
        entity.setGeneratedAt(archive.generatedAt());
        entity.setArchivedAt(archive.archivedAt());
        entity.setReport(archive.report());
        return entity;
    }

    public static ExternalExposureCloseoutArchiveVo toVo(ExternalExposureCloseoutArchiveEntity entity) {
        return new ExternalExposureCloseoutArchiveVo(
                entity.getId(),
                entity.getStatus(),
                Boolean.TRUE.equals(entity.getCloseoutReady()),
                entity.getSummary(),
                entity.getNextAction(),
                entity.getLatestSessionId(),
                entity.getLatestSessionStatus(),
                entity.getPublicUrl(),
                entity.getWebhookUrl(),
                entity.getPurpose(),
                entity.getOperator(),
                entity.getStartedAt(),
                entity.getClosedBy(),
                entity.getClosedAt(),
                entity.getCloseNotes(),
                entity.getLinkedReadinessArchiveId(),
                entity.getHandoffStatus(),
                entity.getArchiveFreshness(),
                countOrZero(entity.getReadyCount()),
                countOrZero(entity.getNeedsAttentionCount()),
                countOrZero(entity.getBlockedCount()),
                countOrZero(entity.getTotalCount()),
                splitLines(entity.getNextActions()),
                splitLines(entity.getEvidenceNotes()),
                splitLines(entity.getDownloadActions()),
                entity.getSideEffectContract(),
                entity.getGeneratedAt(),
                entity.getArchivedAt(),
                entity.getReport()
        );
    }

    private static String joinLines(List<String> values) {
        return values == null ? "" : String.join("\n", values);
    }

    private static List<String> splitLines(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return Arrays.stream(value.split("\\R"))
                .filter(line -> !line.isBlank())
                .toList();
    }

    private static int countOrZero(Integer value) {
        return value == null ? 0 : value;
    }
}
