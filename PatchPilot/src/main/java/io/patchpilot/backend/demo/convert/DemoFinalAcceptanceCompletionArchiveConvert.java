package io.patchpilot.backend.demo.convert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalAcceptanceCompletionArchiveEntity;

import java.util.List;

public final class DemoFinalAcceptanceCompletionArchiveConvert {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<String>> STRING_LIST = new TypeReference<>() {
    };

    private DemoFinalAcceptanceCompletionArchiveConvert() {
    }

    public static DemoFinalAcceptanceCompletionArchiveEntity toEntity(
            DemoFinalAcceptanceCompletionArchiveVo archive
    ) {
        DemoFinalAcceptanceCompletionArchiveEntity entity = new DemoFinalAcceptanceCompletionArchiveEntity();
        entity.setId(archive.id());
        entity.setStatus(archive.status().name());
        entity.setFinalized(archive.finalized());
        entity.setSummary(archive.summary());
        entity.setNextAction(archive.nextAction());
        entity.setLatestArchiveId(archive.latestArchiveId());
        entity.setLatestTaskId(archive.latestTaskId());
        entity.setLatestDeliveryReceiptId(archive.latestDeliveryReceiptId());
        entity.setLatestDeliveryTarget(archive.latestDeliveryTarget());
        entity.setLatestDeliveryChannel(archive.latestDeliveryChannel());
        entity.setLatestDeliveredAt(archive.latestDeliveredAt());
        entity.setDeliveryReceiptFreshness(archive.deliveryReceiptFreshness());
        entity.setDeliveryReceiptFresh(archive.deliveryReceiptFresh());
        entity.setDeliveryReceiptFreshnessSummary(archive.deliveryReceiptFreshnessSummary());
        entity.setEvidenceNotesJson(toJson(archive.evidenceNotes()));
        entity.setReport(archive.report());
        entity.setGeneratedAt(archive.generatedAt());
        entity.setArchivedAt(archive.archivedAt());
        return entity;
    }

    public static DemoFinalAcceptanceCompletionArchiveVo toVo(
            DemoFinalAcceptanceCompletionArchiveEntity entity
    ) {
        return new DemoFinalAcceptanceCompletionArchiveVo(
                entity.getId(),
                DemoReadinessStatus.valueOf(entity.getStatus()),
                Boolean.TRUE.equals(entity.getFinalized()),
                entity.getSummary(),
                entity.getNextAction(),
                entity.getLatestArchiveId(),
                entity.getLatestTaskId(),
                entity.getLatestDeliveryReceiptId(),
                entity.getLatestDeliveryTarget(),
                entity.getLatestDeliveryChannel(),
                entity.getLatestDeliveredAt(),
                entity.getDeliveryReceiptFreshness(),
                Boolean.TRUE.equals(entity.getDeliveryReceiptFresh()),
                entity.getDeliveryReceiptFreshnessSummary(),
                fromJson(entity.getEvidenceNotesJson()),
                entity.getReport(),
                entity.getGeneratedAt(),
                entity.getArchivedAt()
        );
    }

    private static String toJson(List<String> items) {
        try {
            return OBJECT_MAPPER.writeValueAsString(items);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Failed to serialize final acceptance completion archive evidence notes", exception);
        }
    }

    private static List<String> fromJson(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        try {
            return OBJECT_MAPPER.readValue(value, STRING_LIST);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Failed to deserialize final acceptance completion archive evidence notes", exception);
        }
    }
}
