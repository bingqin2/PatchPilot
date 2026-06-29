package io.patchpilot.backend.demo.convert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalExternalReviewEvidencePackageArchiveEntity;

import java.util.List;

public final class DemoFinalExternalReviewEvidencePackageArchiveConvert {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<String>> STRING_LIST = new TypeReference<>() {
    };

    private DemoFinalExternalReviewEvidencePackageArchiveConvert() {
    }

    public static DemoFinalExternalReviewEvidencePackageArchiveEntity toEntity(
            DemoFinalExternalReviewEvidencePackageArchiveVo archive
    ) {
        DemoFinalExternalReviewEvidencePackageArchiveEntity entity =
                new DemoFinalExternalReviewEvidencePackageArchiveEntity();
        entity.setId(archive.id());
        entity.setStatus(archive.status().name());
        entity.setReadyForExternalReview(archive.readyForExternalReview());
        entity.setSummary(archive.summary());
        entity.setNextAction(archive.nextAction());
        entity.setLatestTaskId(archive.latestTaskId());
        entity.setLatestPullRequestUrl(archive.latestPullRequestUrl());
        entity.setFinalAcceptanceSharePackageArchiveId(archive.finalAcceptanceSharePackageArchiveId());
        entity.setCompletionArchiveId(archive.completionArchiveId());
        entity.setCompletionEvidenceDeliveryReceiptId(archive.completionEvidenceDeliveryReceiptId());
        entity.setCloseoutArchiveId(archive.closeoutArchiveId());
        entity.setDeliveryTarget(archive.deliveryTarget());
        entity.setDeliveryChannel(archive.deliveryChannel());
        entity.setDeliveredAt(archive.deliveredAt());
        entity.setDeliveryReceiptFreshness(archive.deliveryReceiptFreshness());
        entity.setCloseoutArchivedAt(archive.closeoutArchivedAt());
        entity.setEvidenceNotesJson(toJson(archive.evidenceNotes(), "evidence notes"));
        entity.setDownloadActionsJson(toJson(archive.downloadActions(), "download actions"));
        entity.setSideEffectContract(archive.sideEffectContract());
        entity.setReport(archive.report());
        entity.setGeneratedAt(archive.generatedAt());
        entity.setArchivedAt(archive.archivedAt());
        return entity;
    }

    public static DemoFinalExternalReviewEvidencePackageArchiveVo toVo(
            DemoFinalExternalReviewEvidencePackageArchiveEntity entity
    ) {
        return new DemoFinalExternalReviewEvidencePackageArchiveVo(
                entity.getId(),
                DemoReadinessStatus.valueOf(entity.getStatus()),
                Boolean.TRUE.equals(entity.getReadyForExternalReview()),
                entity.getSummary(),
                entity.getNextAction(),
                entity.getLatestTaskId(),
                entity.getLatestPullRequestUrl(),
                entity.getFinalAcceptanceSharePackageArchiveId(),
                entity.getCompletionArchiveId(),
                entity.getCompletionEvidenceDeliveryReceiptId(),
                entity.getCloseoutArchiveId(),
                entity.getDeliveryTarget(),
                entity.getDeliveryChannel(),
                entity.getDeliveredAt(),
                entity.getDeliveryReceiptFreshness(),
                entity.getCloseoutArchivedAt(),
                fromJson(entity.getEvidenceNotesJson(), "evidence notes"),
                fromJson(entity.getDownloadActionsJson(), "download actions"),
                entity.getSideEffectContract(),
                entity.getReport(),
                entity.getGeneratedAt(),
                entity.getArchivedAt()
        );
    }

    private static String toJson(List<String> items, String fieldName) {
        try {
            return OBJECT_MAPPER.writeValueAsString(items);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException(
                    "Failed to serialize final external-review package archive " + fieldName,
                    exception
            );
        }
    }

    private static List<String> fromJson(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        try {
            return OBJECT_MAPPER.readValue(value, STRING_LIST);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException(
                    "Failed to deserialize final external-review package archive " + fieldName,
                    exception
            );
        }
    }
}
