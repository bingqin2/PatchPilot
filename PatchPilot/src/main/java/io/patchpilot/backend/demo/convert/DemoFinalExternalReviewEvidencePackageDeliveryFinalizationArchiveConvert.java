package io.patchpilot.backend.demo.convert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEntity;

import java.util.List;

public final class DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveConvert {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<String>> STRING_LIST = new TypeReference<>() {
    };
    private static final TypeReference<List<DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo.Check>>
            CHECK_LIST = new TypeReference<>() {
    };

    private DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveConvert() {
    }

    public static DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEntity toEntity(
            DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo archive
    ) {
        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEntity entity =
                new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEntity();
        entity.setId(archive.id());
        entity.setStatus(archive.status().name());
        entity.setFinalized(archive.finalized());
        entity.setSummary(archive.summary());
        entity.setNextAction(archive.nextAction());
        entity.setLatestArchiveId(archive.latestArchiveId());
        entity.setLatestDeliveryReceiptId(archive.latestDeliveryReceiptId());
        entity.setLatestCloseoutArchiveId(archive.latestCloseoutArchiveId());
        entity.setLatestCompletionArchiveId(archive.latestCompletionArchiveId());
        entity.setLatestCompletionEvidenceDeliveryReceiptId(archive.latestCompletionEvidenceDeliveryReceiptId());
        entity.setLatestTaskId(archive.latestTaskId());
        entity.setLatestPullRequestUrl(archive.latestPullRequestUrl());
        entity.setLatestDeliveryTarget(archive.latestDeliveryTarget());
        entity.setLatestDeliveryChannel(archive.latestDeliveryChannel());
        entity.setLatestDeliveredAt(archive.latestDeliveredAt());
        entity.setDeliveryReceiptFreshness(archive.deliveryReceiptFreshness());
        entity.setDeliveryReceiptFresh(archive.deliveryReceiptFresh());
        entity.setDeliveryReceiptFreshnessSummary(archive.deliveryReceiptFreshnessSummary());
        entity.setChecksJson(toJson(archive.checks(), "checks"));
        entity.setEvidenceNotesJson(toJson(archive.evidenceNotes(), "evidence notes"));
        entity.setDownloadActionsJson(toJson(archive.downloadActions(), "download actions"));
        entity.setSideEffectContract(archive.sideEffectContract());
        entity.setReport(archive.report());
        entity.setGeneratedAt(archive.generatedAt());
        entity.setArchivedAt(archive.archivedAt());
        return entity;
    }

    public static DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo toVo(
            DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEntity entity
    ) {
        return new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo(
                entity.getId(),
                DemoReadinessStatus.valueOf(entity.getStatus()),
                Boolean.TRUE.equals(entity.getFinalized()),
                entity.getSummary(),
                entity.getNextAction(),
                entity.getLatestArchiveId(),
                entity.getLatestDeliveryReceiptId(),
                entity.getLatestCloseoutArchiveId(),
                entity.getLatestCompletionArchiveId(),
                entity.getLatestCompletionEvidenceDeliveryReceiptId(),
                entity.getLatestTaskId(),
                entity.getLatestPullRequestUrl(),
                entity.getLatestDeliveryTarget(),
                entity.getLatestDeliveryChannel(),
                entity.getLatestDeliveredAt(),
                entity.getDeliveryReceiptFreshness(),
                Boolean.TRUE.equals(entity.getDeliveryReceiptFresh()),
                entity.getDeliveryReceiptFreshnessSummary(),
                fromChecksJson(entity.getChecksJson(), "checks"),
                fromStringJson(entity.getEvidenceNotesJson(), "evidence notes"),
                fromStringJson(entity.getDownloadActionsJson(), "download actions"),
                entity.getSideEffectContract(),
                entity.getReport(),
                entity.getGeneratedAt(),
                entity.getArchivedAt()
        );
    }

    public static DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo fromFinalization(
            String id,
            DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo finalization,
            java.time.Instant archivedAt
    ) {
        return new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo(
                id,
                finalization.status(),
                finalization.finalized(),
                finalization.summary(),
                finalization.nextAction(),
                finalization.latestArchiveId(),
                finalization.latestDeliveryReceiptId(),
                finalization.latestCloseoutArchiveId(),
                finalization.latestCompletionArchiveId(),
                finalization.latestCompletionEvidenceDeliveryReceiptId(),
                finalization.latestTaskId(),
                finalization.latestPullRequestUrl(),
                finalization.latestDeliveryTarget(),
                finalization.latestDeliveryChannel(),
                finalization.latestDeliveredAt(),
                finalization.deliveryReceiptFreshness(),
                finalization.deliveryReceiptFresh(),
                finalization.deliveryReceiptFreshnessSummary(),
                finalization.checks().stream()
                        .map(check -> new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo.Check(
                                check.name(),
                                check.status(),
                                check.summary(),
                                check.nextAction()
                        ))
                        .toList(),
                finalization.evidenceNotes(),
                finalization.downloadActions(),
                finalization.sideEffectContract(),
                finalization.markdownReport(),
                finalization.generatedAt(),
                archivedAt
        );
    }

    private static String toJson(Object value, String fieldName) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException(
                    "Failed to serialize final external-review package delivery finalization archive " + fieldName,
                    exception
            );
        }
    }

    private static List<String> fromStringJson(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        try {
            return OBJECT_MAPPER.readValue(value, STRING_LIST);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException(
                    "Failed to deserialize final external-review package delivery finalization archive " + fieldName,
                    exception
            );
        }
    }

    private static List<DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo.Check> fromChecksJson(
            String value,
            String fieldName
    ) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        try {
            return OBJECT_MAPPER.readValue(value, CHECK_LIST);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException(
                    "Failed to deserialize final external-review package delivery finalization archive " + fieldName,
                    exception
            );
        }
    }
}
