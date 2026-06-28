package io.patchpilot.backend.demo.convert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCertificateArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoLaunchAcceptanceCertificateArchiveEntity;

import java.util.List;

public final class DemoLaunchAcceptanceCertificateArchiveConvert {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<String>> STRING_LIST = new TypeReference<>() {
    };

    private DemoLaunchAcceptanceCertificateArchiveConvert() {
    }

    public static DemoLaunchAcceptanceCertificateArchiveEntity toEntity(DemoLaunchAcceptanceCertificateArchiveVo archive) {
        DemoLaunchAcceptanceCertificateArchiveEntity entity = new DemoLaunchAcceptanceCertificateArchiveEntity();
        entity.setId(archive.id());
        entity.setStatus(archive.status().name());
        entity.setCertified(archive.certified());
        entity.setSummary(archive.summary());
        entity.setNextAction(archive.nextAction());
        entity.setArchiveCount(archive.archiveCount());
        entity.setLatestCloseoutArchiveId(archive.latestCloseoutArchiveId());
        entity.setLatestLaunchEvidenceArchiveId(archive.latestLaunchEvidenceArchiveId());
        entity.setLatestDeliveryReceiptId(archive.latestDeliveryReceiptId());
        entity.setLatestSessionId(archive.latestSessionId());
        entity.setLatestTaskId(archive.latestTaskId());
        entity.setLatestPullRequestUrl(archive.latestPullRequestUrl());
        entity.setLatestWebhookDeliveryId(archive.latestWebhookDeliveryId());
        entity.setEvaluationRunId(archive.evaluationRunId());
        entity.setLatestDeliveryTarget(archive.latestDeliveryTarget());
        entity.setLatestDeliveryChannel(archive.latestDeliveryChannel());
        entity.setDeliveryReceiptFreshness(archive.deliveryReceiptFreshness());
        entity.setLatestArchivedAt(archive.latestArchivedAt());
        entity.setGeneratedAt(archive.generatedAt());
        entity.setArchivedAt(archive.archivedAt());
        entity.setDownloadActionsJson(toJson(archive.downloadActions()));
        entity.setReport(archive.report());
        return entity;
    }

    public static DemoLaunchAcceptanceCertificateArchiveVo toVo(DemoLaunchAcceptanceCertificateArchiveEntity entity) {
        return new DemoLaunchAcceptanceCertificateArchiveVo(
                entity.getId(),
                DemoReadinessStatus.valueOf(entity.getStatus()),
                Boolean.TRUE.equals(entity.getCertified()),
                entity.getSummary(),
                entity.getNextAction(),
                entity.getArchiveCount() == null ? 0 : entity.getArchiveCount(),
                entity.getLatestCloseoutArchiveId(),
                entity.getLatestLaunchEvidenceArchiveId(),
                entity.getLatestDeliveryReceiptId(),
                entity.getLatestSessionId(),
                entity.getLatestTaskId(),
                entity.getLatestPullRequestUrl(),
                entity.getLatestWebhookDeliveryId(),
                entity.getEvaluationRunId(),
                entity.getLatestDeliveryTarget(),
                entity.getLatestDeliveryChannel(),
                entity.getDeliveryReceiptFreshness(),
                entity.getLatestArchivedAt(),
                entity.getGeneratedAt(),
                entity.getArchivedAt(),
                fromJson(entity.getDownloadActionsJson()),
                entity.getReport()
        );
    }

    private static String toJson(List<String> items) {
        try {
            return OBJECT_MAPPER.writeValueAsString(items);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Failed to serialize launch acceptance certificate archive actions", exception);
        }
    }

    private static List<String> fromJson(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        try {
            return OBJECT_MAPPER.readValue(value, STRING_LIST);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Failed to deserialize launch acceptance certificate archive actions", exception);
        }
    }
}
