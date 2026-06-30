package io.patchpilot.backend.demo.convert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveEntity;

import java.time.Instant;
import java.util.List;

public final class DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveConvert {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<String>> STRING_LIST = new TypeReference<>() {
    };
    private static final TypeReference<List<DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo.Check>>
            CHECK_LIST = new TypeReference<>() {
    };

    private DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveConvert() {
    }

    public static DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveEntity toEntity(
            DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo archive
    ) {
        DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveEntity entity =
                new DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveEntity();
        entity.setId(archive.id());
        entity.setStatus(archive.status().name());
        entity.setCertified(archive.certified());
        entity.setSummary(archive.summary());
        entity.setNextAction(archive.nextAction());
        entity.setLatestDeliveryFinalizationArchiveId(archive.latestDeliveryFinalizationArchiveId());
        entity.setLatestReleaseBundleArchiveId(archive.latestReleaseBundleArchiveId());
        entity.setLatestDeliveryReceiptId(archive.latestDeliveryReceiptId());
        entity.setLatestCertificateArchiveId(archive.latestCertificateArchiveId());
        entity.setLatestPackageArchiveId(archive.latestPackageArchiveId());
        entity.setLatestPackageDeliveryReceiptId(archive.latestPackageDeliveryReceiptId());
        entity.setLatestTaskId(archive.latestTaskId());
        entity.setLatestPullRequestUrl(archive.latestPullRequestUrl());
        entity.setLatestDeliveryTarget(archive.latestDeliveryTarget());
        entity.setLatestDeliveryChannel(archive.latestDeliveryChannel());
        entity.setLatestDeliveredAt(archive.latestDeliveredAt());
        entity.setLatestArchivedAt(archive.latestArchivedAt());
        entity.setReleaseBundleDeliveryReceiptFreshness(archive.releaseBundleDeliveryReceiptFreshness());
        entity.setReleaseBundleDeliveryReceiptFresh(archive.releaseBundleDeliveryReceiptFresh());
        entity.setChecksJson(toJson(archive.checks(), "checks"));
        entity.setEvidenceNotesJson(toJson(archive.evidenceNotes(), "evidence notes"));
        entity.setDownloadActionsJson(toJson(archive.downloadActions(), "download actions"));
        entity.setSideEffectContract(archive.sideEffectContract());
        entity.setReport(archive.report());
        entity.setGeneratedAt(archive.generatedAt());
        entity.setArchivedAt(archive.archivedAt());
        return entity;
    }

    public static DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo toVo(
            DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveEntity entity
    ) {
        return new DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo(
                entity.getId(),
                DemoReadinessStatus.valueOf(entity.getStatus()),
                Boolean.TRUE.equals(entity.getCertified()),
                entity.getSummary(),
                entity.getNextAction(),
                entity.getLatestDeliveryFinalizationArchiveId(),
                entity.getLatestReleaseBundleArchiveId(),
                entity.getLatestDeliveryReceiptId(),
                entity.getLatestCertificateArchiveId(),
                entity.getLatestPackageArchiveId(),
                entity.getLatestPackageDeliveryReceiptId(),
                entity.getLatestTaskId(),
                entity.getLatestPullRequestUrl(),
                entity.getLatestDeliveryTarget(),
                entity.getLatestDeliveryChannel(),
                entity.getLatestDeliveredAt(),
                entity.getLatestArchivedAt(),
                entity.getReleaseBundleDeliveryReceiptFreshness(),
                Boolean.TRUE.equals(entity.getReleaseBundleDeliveryReceiptFresh()),
                fromChecksJson(entity.getChecksJson(), "checks"),
                fromStringJson(entity.getEvidenceNotesJson(), "evidence notes"),
                fromStringJson(entity.getDownloadActionsJson(), "download actions"),
                entity.getSideEffectContract(),
                entity.getReport(),
                entity.getGeneratedAt(),
                entity.getArchivedAt()
        );
    }

    public static DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo fromCertificate(
            String id,
            DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo certificate,
            Instant archivedAt
    ) {
        return new DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo(
                id,
                certificate.status(),
                certificate.certified(),
                certificate.summary(),
                certificate.nextAction(),
                certificate.latestDeliveryFinalizationArchiveId(),
                certificate.latestReleaseBundleArchiveId(),
                certificate.latestDeliveryReceiptId(),
                certificate.latestCertificateArchiveId(),
                certificate.latestPackageArchiveId(),
                certificate.latestPackageDeliveryReceiptId(),
                certificate.latestTaskId(),
                certificate.latestPullRequestUrl(),
                certificate.latestDeliveryTarget(),
                certificate.latestDeliveryChannel(),
                certificate.latestDeliveredAt(),
                certificate.latestArchivedAt(),
                certificate.releaseBundleDeliveryReceiptFreshness(),
                certificate.releaseBundleDeliveryReceiptFresh(),
                certificate.checks().stream()
                        .map(check -> new DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo.Check(
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
                    "Failed to serialize final external-review release bundle delivery certificate archive "
                            + fieldName,
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
                    "Failed to deserialize final external-review release bundle delivery certificate archive "
                            + fieldName,
                    exception
            );
        }
    }

    private static List<DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo.Check> fromChecksJson(
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
                    "Failed to deserialize final external-review release bundle delivery certificate archive "
                            + fieldName,
                    exception
            );
        }
    }
}
