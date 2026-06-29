package io.patchpilot.backend.demo.convert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionCloseoutArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalAcceptanceCompletionCloseoutArchiveEntity;

import java.util.List;

public final class DemoFinalAcceptanceCompletionCloseoutArchiveConvert {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<String>> STRING_LIST = new TypeReference<>() {
    };

    private DemoFinalAcceptanceCompletionCloseoutArchiveConvert() {
    }

    public static DemoFinalAcceptanceCompletionCloseoutArchiveEntity toEntity(
            DemoFinalAcceptanceCompletionCloseoutArchiveVo archive
    ) {
        DemoFinalAcceptanceCompletionCloseoutArchiveEntity entity =
                new DemoFinalAcceptanceCompletionCloseoutArchiveEntity();
        entity.setId(archive.id());
        entity.setStatus(archive.status().name());
        entity.setClosed(archive.closed());
        entity.setSummary(archive.summary());
        entity.setNextAction(archive.nextAction());
        entity.setLatestTaskId(archive.latestTaskId());
        entity.setLatestPullRequestUrl(archive.latestPullRequestUrl());
        entity.setLatestSharePackageArchiveId(archive.latestSharePackageArchiveId());
        entity.setLatestCompletionArchiveId(archive.latestCompletionArchiveId());
        entity.setLatestCompletionEvidenceDeliveryReceiptId(archive.latestCompletionEvidenceDeliveryReceiptId());
        entity.setLatestDeliveryTarget(archive.latestDeliveryTarget());
        entity.setLatestDeliveryChannel(archive.latestDeliveryChannel());
        entity.setLatestDeliveredAt(archive.latestDeliveredAt());
        entity.setDeliveryReceiptFreshness(archive.deliveryReceiptFreshness());
        entity.setEvidenceNotesJson(toJson(archive.evidenceNotes(), "evidence notes"));
        entity.setDownloadActionsJson(toJson(archive.downloadActions(), "download actions"));
        entity.setSideEffectContract(archive.sideEffectContract());
        entity.setReport(archive.report());
        entity.setGeneratedAt(archive.generatedAt());
        entity.setArchivedAt(archive.archivedAt());
        return entity;
    }

    public static DemoFinalAcceptanceCompletionCloseoutArchiveVo toVo(
            DemoFinalAcceptanceCompletionCloseoutArchiveEntity entity
    ) {
        return new DemoFinalAcceptanceCompletionCloseoutArchiveVo(
                entity.getId(),
                DemoReadinessStatus.valueOf(entity.getStatus()),
                Boolean.TRUE.equals(entity.getClosed()),
                entity.getSummary(),
                entity.getNextAction(),
                entity.getLatestTaskId(),
                entity.getLatestPullRequestUrl(),
                entity.getLatestSharePackageArchiveId(),
                entity.getLatestCompletionArchiveId(),
                entity.getLatestCompletionEvidenceDeliveryReceiptId(),
                entity.getLatestDeliveryTarget(),
                entity.getLatestDeliveryChannel(),
                entity.getLatestDeliveredAt(),
                entity.getDeliveryReceiptFreshness(),
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
                    "Failed to serialize final acceptance completion closeout archive " + fieldName,
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
                    "Failed to deserialize final acceptance completion closeout archive " + fieldName,
                    exception
            );
        }
    }
}
