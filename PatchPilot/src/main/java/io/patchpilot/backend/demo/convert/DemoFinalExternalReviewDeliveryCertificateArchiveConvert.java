package io.patchpilot.backend.demo.convert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewDeliveryCertificateArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewDeliveryCertificateVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalExternalReviewDeliveryCertificateArchiveEntity;

import java.time.Instant;
import java.util.List;

public final class DemoFinalExternalReviewDeliveryCertificateArchiveConvert {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<String>> STRING_LIST = new TypeReference<>() {
    };
    private static final TypeReference<List<DemoFinalExternalReviewDeliveryCertificateArchiveVo.Check>>
            CHECK_LIST = new TypeReference<>() {
    };

    private DemoFinalExternalReviewDeliveryCertificateArchiveConvert() {
    }

    public static DemoFinalExternalReviewDeliveryCertificateArchiveEntity toEntity(
            DemoFinalExternalReviewDeliveryCertificateArchiveVo archive
    ) {
        DemoFinalExternalReviewDeliveryCertificateArchiveEntity entity =
                new DemoFinalExternalReviewDeliveryCertificateArchiveEntity();
        entity.setId(archive.id());
        entity.setStatus(archive.status().name());
        entity.setCertified(archive.certified());
        entity.setSummary(archive.summary());
        entity.setNextAction(archive.nextAction());
        entity.setLatestDeliveryFinalizationArchiveId(archive.latestDeliveryFinalizationArchiveId());
        entity.setLatestPackageArchiveId(archive.latestPackageArchiveId());
        entity.setLatestDeliveryReceiptId(archive.latestDeliveryReceiptId());
        entity.setLatestTaskId(archive.latestTaskId());
        entity.setLatestPullRequestUrl(archive.latestPullRequestUrl());
        entity.setLatestDeliveryTarget(archive.latestDeliveryTarget());
        entity.setLatestDeliveryChannel(archive.latestDeliveryChannel());
        entity.setLatestDeliveredAt(archive.latestDeliveredAt());
        entity.setLatestArchivedAt(archive.latestArchivedAt());
        entity.setDeliveryReceiptFreshness(archive.deliveryReceiptFreshness());
        entity.setDeliveryReceiptFresh(archive.deliveryReceiptFresh());
        entity.setChecksJson(toJson(archive.checks(), "checks"));
        entity.setEvidenceNotesJson(toJson(archive.evidenceNotes(), "evidence notes"));
        entity.setDownloadActionsJson(toJson(archive.downloadActions(), "download actions"));
        entity.setSideEffectContract(archive.sideEffectContract());
        entity.setReport(archive.report());
        entity.setGeneratedAt(archive.generatedAt());
        entity.setArchivedAt(archive.archivedAt());
        return entity;
    }

    public static DemoFinalExternalReviewDeliveryCertificateArchiveVo toVo(
            DemoFinalExternalReviewDeliveryCertificateArchiveEntity entity
    ) {
        return new DemoFinalExternalReviewDeliveryCertificateArchiveVo(
                entity.getId(),
                DemoReadinessStatus.valueOf(entity.getStatus()),
                Boolean.TRUE.equals(entity.getCertified()),
                entity.getSummary(),
                entity.getNextAction(),
                entity.getLatestDeliveryFinalizationArchiveId(),
                entity.getLatestPackageArchiveId(),
                entity.getLatestDeliveryReceiptId(),
                entity.getLatestTaskId(),
                entity.getLatestPullRequestUrl(),
                entity.getLatestDeliveryTarget(),
                entity.getLatestDeliveryChannel(),
                entity.getLatestDeliveredAt(),
                entity.getLatestArchivedAt(),
                entity.getDeliveryReceiptFreshness(),
                Boolean.TRUE.equals(entity.getDeliveryReceiptFresh()),
                fromChecksJson(entity.getChecksJson(), "checks"),
                fromStringJson(entity.getEvidenceNotesJson(), "evidence notes"),
                fromStringJson(entity.getDownloadActionsJson(), "download actions"),
                entity.getSideEffectContract(),
                entity.getReport(),
                entity.getGeneratedAt(),
                entity.getArchivedAt()
        );
    }

    public static DemoFinalExternalReviewDeliveryCertificateArchiveVo fromCertificate(
            String id,
            DemoFinalExternalReviewDeliveryCertificateVo certificate,
            Instant archivedAt
    ) {
        return new DemoFinalExternalReviewDeliveryCertificateArchiveVo(
                id,
                certificate.status(),
                certificate.certified(),
                certificate.summary(),
                certificate.nextAction(),
                certificate.latestDeliveryFinalizationArchiveId(),
                certificate.latestPackageArchiveId(),
                certificate.latestDeliveryReceiptId(),
                certificate.latestTaskId(),
                certificate.latestPullRequestUrl(),
                certificate.latestDeliveryTarget(),
                certificate.latestDeliveryChannel(),
                certificate.latestDeliveredAt(),
                certificate.latestArchivedAt(),
                certificate.deliveryReceiptFreshness(),
                certificate.deliveryReceiptFresh(),
                certificate.checks().stream()
                        .map(check -> new DemoFinalExternalReviewDeliveryCertificateArchiveVo.Check(
                                check.name(),
                                check.status(),
                                check.summary(),
                                check.nextAction()
                        ))
                        .toList(),
                certificate.evidenceNotes(),
                certificate.downloadActions(),
                certificate.sideEffectContract(),
                certificate.markdownReport(),
                certificate.generatedAt(),
                archivedAt
        );
    }

    private static String toJson(Object value, String fieldName) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException(
                    "Failed to serialize final external-review delivery certificate archive " + fieldName,
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
                    "Failed to deserialize final external-review delivery certificate archive " + fieldName,
                    exception
            );
        }
    }

    private static List<DemoFinalExternalReviewDeliveryCertificateArchiveVo.Check> fromChecksJson(
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
                    "Failed to deserialize final external-review delivery certificate archive " + fieldName,
                    exception
            );
        }
    }
}
