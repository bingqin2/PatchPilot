package io.patchpilot.backend.demo.convert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveEntity;

import java.time.Instant;
import java.util.List;

public final class DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveConvert {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<String>> STRING_LIST = new TypeReference<>() {
    };
    private static final TypeReference<List<DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo.Check>>
            CHECK_LIST = new TypeReference<>() {
    };

    private DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveConvert() {
    }

    public static DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveEntity toEntity(
            DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo archive
    ) {
        DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveEntity entity =
                new DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveEntity();
        entity.setId(archive.id());
        entity.setStatus(archive.status().name());
        entity.setFinalized(archive.finalized());
        entity.setSummary(archive.summary());
        entity.setNextAction(archive.nextAction());
        entity.setLatestArchiveId(archive.latestArchiveId());
        entity.setLatestDeliveryReceiptId(archive.latestDeliveryReceiptId());
        entity.setLatestCertificateArchiveId(archive.latestCertificateArchiveId());
        entity.setLatestDeliveryFinalizationArchiveId(archive.latestDeliveryFinalizationArchiveId());
        entity.setLatestPackageArchiveId(archive.latestPackageArchiveId());
        entity.setLatestPackageDeliveryReceiptId(archive.latestPackageDeliveryReceiptId());
        entity.setLatestTaskId(archive.latestTaskId());
        entity.setLatestPullRequestUrl(archive.latestPullRequestUrl());
        entity.setLatestDeliveryTarget(archive.latestDeliveryTarget());
        entity.setLatestDeliveryChannel(archive.latestDeliveryChannel());
        entity.setLatestDeliveredAt(archive.latestDeliveredAt());
        entity.setReleaseBundleDeliveryReceiptFreshness(archive.releaseBundleDeliveryReceiptFreshness());
        entity.setReleaseBundleDeliveryReceiptFresh(archive.releaseBundleDeliveryReceiptFresh());
        entity.setReleaseBundleDeliveryReceiptFreshnessSummary(
                archive.releaseBundleDeliveryReceiptFreshnessSummary()
        );
        entity.setChecksJson(toJson(archive.checks(), "checks"));
        entity.setEvidenceNotesJson(toJson(archive.evidenceNotes(), "evidence notes"));
        entity.setDownloadActionsJson(toJson(archive.downloadActions(), "download actions"));
        entity.setSideEffectContract(archive.sideEffectContract());
        entity.setReport(archive.report());
        entity.setGeneratedAt(archive.generatedAt());
        entity.setArchivedAt(archive.archivedAt());
        return entity;
    }

    public static DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo toVo(
            DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveEntity entity
    ) {
        return new DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo(
                entity.getId(),
                DemoReadinessStatus.valueOf(entity.getStatus()),
                Boolean.TRUE.equals(entity.getFinalized()),
                entity.getSummary(),
                entity.getNextAction(),
                entity.getLatestArchiveId(),
                entity.getLatestDeliveryReceiptId(),
                entity.getLatestCertificateArchiveId(),
                entity.getLatestDeliveryFinalizationArchiveId(),
                entity.getLatestPackageArchiveId(),
                entity.getLatestPackageDeliveryReceiptId(),
                entity.getLatestTaskId(),
                entity.getLatestPullRequestUrl(),
                entity.getLatestDeliveryTarget(),
                entity.getLatestDeliveryChannel(),
                entity.getLatestDeliveredAt(),
                entity.getReleaseBundleDeliveryReceiptFreshness(),
                Boolean.TRUE.equals(entity.getReleaseBundleDeliveryReceiptFresh()),
                entity.getReleaseBundleDeliveryReceiptFreshnessSummary(),
                fromChecksJson(entity.getChecksJson(), "checks"),
                fromStringJson(entity.getEvidenceNotesJson(), "evidence notes"),
                fromStringJson(entity.getDownloadActionsJson(), "download actions"),
                entity.getSideEffectContract(),
                entity.getReport(),
                entity.getGeneratedAt(),
                entity.getArchivedAt()
        );
    }

    public static DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo fromFinalization(
            String id,
            DemoFinalExternalReviewReleaseBundleDeliveryFinalizationVo finalization,
            Instant archivedAt
    ) {
        return new DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo(
                id,
                finalization.status(),
                finalization.finalized(),
                finalization.summary(),
                finalization.nextAction(),
                finalization.latestArchiveId(),
                finalization.latestDeliveryReceiptId(),
                finalization.latestCertificateArchiveId(),
                finalization.latestDeliveryFinalizationArchiveId(),
                finalization.latestPackageArchiveId(),
                finalization.latestPackageDeliveryReceiptId(),
                finalization.latestTaskId(),
                finalization.latestPullRequestUrl(),
                finalization.latestDeliveryTarget(),
                finalization.latestDeliveryChannel(),
                finalization.latestDeliveredAt(),
                finalization.releaseBundleDeliveryReceiptFreshness(),
                finalization.releaseBundleDeliveryReceiptFresh(),
                finalization.releaseBundleDeliveryReceiptFreshnessSummary(),
                finalization.checks().stream()
                        .map(check -> new DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo.Check(
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
                    "Failed to serialize final external-review release bundle delivery finalization archive "
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
                    "Failed to deserialize final external-review release bundle delivery finalization archive "
                            + fieldName,
                    exception
            );
        }
    }

    private static List<DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo.Check> fromChecksJson(
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
                    "Failed to deserialize final external-review release bundle delivery finalization archive "
                            + fieldName,
                    exception
            );
        }
    }
}
