package io.patchpilot.backend.demo.convert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceSharePackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalAcceptanceSharePackageArchiveEntity;

import java.util.List;

public final class DemoFinalAcceptanceSharePackageArchiveConvert {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<String>> STRING_LIST = new TypeReference<>() {
    };

    private DemoFinalAcceptanceSharePackageArchiveConvert() {
    }

    public static DemoFinalAcceptanceSharePackageArchiveEntity toEntity(
            DemoFinalAcceptanceSharePackageArchiveVo archive
    ) {
        DemoFinalAcceptanceSharePackageArchiveEntity entity = new DemoFinalAcceptanceSharePackageArchiveEntity();
        entity.setId(archive.id());
        entity.setStatus(archive.status().name());
        entity.setSendReady(archive.sendReady());
        entity.setSummary(archive.summary());
        entity.setNextAction(archive.nextAction());
        entity.setLaunchCertificateArchiveId(archive.launchCertificateArchiveId());
        entity.setTaskCertificateArchiveId(archive.taskCertificateArchiveId());
        entity.setLatestTaskId(archive.latestTaskId());
        entity.setLatestPullRequestUrl(archive.latestPullRequestUrl());
        entity.setRecommendedRecipientsJson(toJson(archive.recommendedRecipients()));
        entity.setRequiredAttachmentsJson(toJson(archive.requiredAttachments()));
        entity.setPreSendChecksJson(toJson(archive.preSendChecks()));
        entity.setMessageSubject(archive.messageSubject());
        entity.setMessageBody(archive.messageBody());
        entity.setEvidenceNotesJson(toJson(archive.evidenceNotes()));
        entity.setSideEffectContract(archive.sideEffectContract());
        entity.setReport(archive.report());
        entity.setGeneratedAt(archive.generatedAt());
        entity.setArchivedAt(archive.archivedAt());
        return entity;
    }

    public static DemoFinalAcceptanceSharePackageArchiveVo toVo(
            DemoFinalAcceptanceSharePackageArchiveEntity entity
    ) {
        return new DemoFinalAcceptanceSharePackageArchiveVo(
                entity.getId(),
                DemoReadinessStatus.valueOf(entity.getStatus()),
                Boolean.TRUE.equals(entity.getSendReady()),
                entity.getSummary(),
                entity.getNextAction(),
                entity.getLaunchCertificateArchiveId(),
                entity.getTaskCertificateArchiveId(),
                entity.getLatestTaskId(),
                entity.getLatestPullRequestUrl(),
                fromJson(entity.getRecommendedRecipientsJson()),
                fromJson(entity.getRequiredAttachmentsJson()),
                fromJson(entity.getPreSendChecksJson()),
                entity.getMessageSubject(),
                entity.getMessageBody(),
                fromJson(entity.getEvidenceNotesJson()),
                entity.getSideEffectContract(),
                entity.getReport(),
                entity.getGeneratedAt(),
                entity.getArchivedAt()
        );
    }

    private static String toJson(List<String> items) {
        try {
            return OBJECT_MAPPER.writeValueAsString(items);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Failed to serialize final acceptance share package archive list", exception);
        }
    }

    private static List<String> fromJson(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        try {
            return OBJECT_MAPPER.readValue(value, STRING_LIST);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Failed to deserialize final acceptance share package archive list", exception);
        }
    }
}
