package io.patchpilot.backend.demo.convert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalExternalReviewReleaseBundleArchiveEntity;

import java.time.Instant;
import java.util.List;

public final class DemoFinalExternalReviewReleaseBundleArchiveConvert {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<String>> STRING_LIST = new TypeReference<>() {
    };
    private static final TypeReference<List<DemoFinalExternalReviewReleaseBundleArchiveVo.ReleaseCheck>>
            RELEASE_CHECK_LIST = new TypeReference<>() {
    };

    private DemoFinalExternalReviewReleaseBundleArchiveConvert() {
    }

    public static DemoFinalExternalReviewReleaseBundleArchiveEntity toEntity(
            DemoFinalExternalReviewReleaseBundleArchiveVo archive
    ) {
        DemoFinalExternalReviewReleaseBundleArchiveEntity entity =
                new DemoFinalExternalReviewReleaseBundleArchiveEntity();
        entity.setId(archive.id());
        entity.setStatus(archive.status().name());
        entity.setReleaseReady(archive.releaseReady());
        entity.setSummary(archive.summary());
        entity.setNextAction(archive.nextAction());
        entity.setLatestCertificateArchiveId(archive.latestCertificateArchiveId());
        entity.setLatestDeliveryFinalizationArchiveId(archive.latestDeliveryFinalizationArchiveId());
        entity.setLatestPackageArchiveId(archive.latestPackageArchiveId());
        entity.setLatestDeliveryReceiptId(archive.latestDeliveryReceiptId());
        entity.setLatestTaskId(archive.latestTaskId());
        entity.setLatestPullRequestUrl(archive.latestPullRequestUrl());
        entity.setLatestDeliveryTarget(archive.latestDeliveryTarget());
        entity.setLatestDeliveryChannel(archive.latestDeliveryChannel());
        entity.setLatestDeliveredAt(archive.latestDeliveredAt());
        entity.setLatestCertificateArchivedAt(archive.latestCertificateArchivedAt());
        entity.setRequiredAttachmentsJson(toJson(archive.requiredAttachments(), "required attachments"));
        entity.setReleaseChecksJson(toJson(archive.releaseChecks(), "release checks"));
        entity.setEvidenceNotesJson(toJson(archive.evidenceNotes(), "evidence notes"));
        entity.setDownloadActionsJson(toJson(archive.downloadActions(), "download actions"));
        entity.setSideEffectContract(archive.sideEffectContract());
        entity.setReport(archive.report());
        entity.setGeneratedAt(archive.generatedAt());
        entity.setArchivedAt(archive.archivedAt());
        return entity;
    }

    public static DemoFinalExternalReviewReleaseBundleArchiveVo toVo(
            DemoFinalExternalReviewReleaseBundleArchiveEntity entity
    ) {
        return new DemoFinalExternalReviewReleaseBundleArchiveVo(
                entity.getId(),
                DemoReadinessStatus.valueOf(entity.getStatus()),
                Boolean.TRUE.equals(entity.getReleaseReady()),
                entity.getSummary(),
                entity.getNextAction(),
                entity.getLatestCertificateArchiveId(),
                entity.getLatestDeliveryFinalizationArchiveId(),
                entity.getLatestPackageArchiveId(),
                entity.getLatestDeliveryReceiptId(),
                entity.getLatestTaskId(),
                entity.getLatestPullRequestUrl(),
                entity.getLatestDeliveryTarget(),
                entity.getLatestDeliveryChannel(),
                entity.getLatestDeliveredAt(),
                entity.getLatestCertificateArchivedAt(),
                fromStringJson(entity.getRequiredAttachmentsJson(), "required attachments"),
                fromReleaseChecksJson(entity.getReleaseChecksJson(), "release checks"),
                fromStringJson(entity.getEvidenceNotesJson(), "evidence notes"),
                fromStringJson(entity.getDownloadActionsJson(), "download actions"),
                entity.getSideEffectContract(),
                entity.getReport(),
                entity.getGeneratedAt(),
                entity.getArchivedAt()
        );
    }

    public static DemoFinalExternalReviewReleaseBundleArchiveVo fromReleaseBundle(
            String id,
            DemoFinalExternalReviewReleaseBundleVo bundle,
            Instant archivedAt
    ) {
        return new DemoFinalExternalReviewReleaseBundleArchiveVo(
                id,
                bundle.status(),
                bundle.releaseReady(),
                bundle.summary(),
                bundle.nextAction(),
                bundle.latestCertificateArchiveId(),
                bundle.latestDeliveryFinalizationArchiveId(),
                bundle.latestPackageArchiveId(),
                bundle.latestDeliveryReceiptId(),
                bundle.latestTaskId(),
                bundle.latestPullRequestUrl(),
                bundle.latestDeliveryTarget(),
                bundle.latestDeliveryChannel(),
                bundle.latestDeliveredAt(),
                bundle.latestCertificateArchivedAt(),
                bundle.requiredAttachments(),
                bundle.releaseChecks().stream()
                        .map(check -> new DemoFinalExternalReviewReleaseBundleArchiveVo.ReleaseCheck(
                                check.name(),
                                check.status(),
                                check.summary(),
                                check.nextAction()
                        ))
                        .toList(),
                bundle.evidenceNotes(),
                bundle.downloadActions(),
                bundle.sideEffectContract(),
                bundle.markdownReport(),
                bundle.generatedAt(),
                archivedAt
        );
    }

    private static String toJson(Object value, String fieldName) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException(
                    "Failed to serialize final external-review release bundle archive " + fieldName,
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
                    "Failed to deserialize final external-review release bundle archive " + fieldName,
                    exception
            );
        }
    }

    private static List<DemoFinalExternalReviewReleaseBundleArchiveVo.ReleaseCheck> fromReleaseChecksJson(
            String value,
            String fieldName
    ) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        try {
            return OBJECT_MAPPER.readValue(value, RELEASE_CHECK_LIST);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException(
                    "Failed to deserialize final external-review release bundle archive " + fieldName,
                    exception
            );
        }
    }
}
