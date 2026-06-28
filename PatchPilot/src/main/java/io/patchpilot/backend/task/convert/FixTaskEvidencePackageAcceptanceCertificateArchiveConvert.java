package io.patchpilot.backend.task.convert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.task.domain.entity.FixTaskEvidencePackageAcceptanceCertificateArchiveEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageAcceptanceCertificateArchiveVo;

import java.util.List;

public final class FixTaskEvidencePackageAcceptanceCertificateArchiveConvert {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<String>> STRING_LIST = new TypeReference<>() {
    };

    private FixTaskEvidencePackageAcceptanceCertificateArchiveConvert() {
    }

    public static FixTaskEvidencePackageAcceptanceCertificateArchiveEntity toEntity(
            FixTaskEvidencePackageAcceptanceCertificateArchiveVo archive
    ) {
        FixTaskEvidencePackageAcceptanceCertificateArchiveEntity entity =
                new FixTaskEvidencePackageAcceptanceCertificateArchiveEntity();
        entity.setId(archive.id());
        entity.setStatus(archive.status());
        entity.setCertified(archive.certified());
        entity.setSummary(archive.summary());
        entity.setNextAction(archive.nextAction());
        entity.setArchiveCount(archive.archiveCount());
        entity.setLatestCloseoutArchiveId(archive.latestCloseoutArchiveId());
        entity.setLatestEvidenceArchiveId(archive.latestEvidenceArchiveId());
        entity.setLatestDeliveryReceiptId(archive.latestDeliveryReceiptId());
        entity.setLatestTaskId(archive.latestTaskId());
        entity.setLatestPullRequestUrl(archive.latestPullRequestUrl());
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

    public static FixTaskEvidencePackageAcceptanceCertificateArchiveVo toVo(
            FixTaskEvidencePackageAcceptanceCertificateArchiveEntity entity
    ) {
        return new FixTaskEvidencePackageAcceptanceCertificateArchiveVo(
                entity.getId(),
                entity.getStatus(),
                Boolean.TRUE.equals(entity.getCertified()),
                entity.getSummary(),
                entity.getNextAction(),
                entity.getArchiveCount() == null ? 0 : entity.getArchiveCount(),
                entity.getLatestCloseoutArchiveId(),
                entity.getLatestEvidenceArchiveId(),
                entity.getLatestDeliveryReceiptId(),
                entity.getLatestTaskId(),
                entity.getLatestPullRequestUrl(),
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
            throw new IllegalArgumentException("Failed to serialize task evidence acceptance certificate archive actions", exception);
        }
    }

    private static List<String> fromJson(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        try {
            return OBJECT_MAPPER.readValue(value, STRING_LIST);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Failed to deserialize task evidence acceptance certificate archive actions", exception);
        }
    }
}
