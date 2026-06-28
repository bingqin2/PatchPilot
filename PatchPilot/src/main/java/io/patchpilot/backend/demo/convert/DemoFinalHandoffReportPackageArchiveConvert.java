package io.patchpilot.backend.demo.convert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.demo.domain.DemoFinalHandoffReportPackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalHandoffReportPackageArchiveEntity;

import java.util.List;

public final class DemoFinalHandoffReportPackageArchiveConvert {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<String>> STRING_LIST = new TypeReference<>() {
    };

    private DemoFinalHandoffReportPackageArchiveConvert() {
    }

    public static DemoFinalHandoffReportPackageArchiveEntity toEntity(DemoFinalHandoffReportPackageArchiveVo archive) {
        DemoFinalHandoffReportPackageArchiveEntity entity = new DemoFinalHandoffReportPackageArchiveEntity();
        entity.setId(archive.id());
        entity.setStatus(archive.status().name());
        entity.setDownloadReady(archive.downloadReady());
        entity.setSummary(archive.summary());
        entity.setNextAction(archive.nextAction());
        entity.setLatestArchiveId(archive.latestArchiveId());
        entity.setLatestSessionId(archive.latestSessionId());
        entity.setLatestDeliveryReceiptId(archive.latestDeliveryReceiptId());
        entity.setTaskCertificateArchiveId(archive.taskCertificateArchiveId());
        entity.setTaskCertificateReady(archive.taskCertificateReady());
        entity.setReadinessChecksJson(toJson(archive.readinessChecks()));
        entity.setRequiredAttachmentsJson(toJson(archive.requiredAttachments()));
        entity.setPreSendChecksJson(toJson(archive.preSendChecks()));
        entity.setEvidenceNotesJson(toJson(archive.evidenceNotes()));
        entity.setSourceReportsJson(toJson(archive.sourceReports()));
        entity.setReport(archive.report());
        entity.setGeneratedAt(archive.generatedAt());
        entity.setArchivedAt(archive.archivedAt());
        return entity;
    }

    public static DemoFinalHandoffReportPackageArchiveVo toVo(DemoFinalHandoffReportPackageArchiveEntity entity) {
        return new DemoFinalHandoffReportPackageArchiveVo(
                entity.getId(),
                DemoReadinessStatus.valueOf(entity.getStatus()),
                Boolean.TRUE.equals(entity.getDownloadReady()),
                entity.getSummary(),
                entity.getNextAction(),
                entity.getLatestArchiveId(),
                entity.getLatestSessionId(),
                entity.getLatestDeliveryReceiptId(),
                entity.getTaskCertificateArchiveId(),
                Boolean.TRUE.equals(entity.getTaskCertificateReady()),
                fromJson(entity.getReadinessChecksJson()),
                fromJson(entity.getRequiredAttachmentsJson()),
                fromJson(entity.getPreSendChecksJson()),
                fromJson(entity.getEvidenceNotesJson()),
                fromJson(entity.getSourceReportsJson()),
                entity.getReport(),
                entity.getGeneratedAt(),
                entity.getArchivedAt()
        );
    }

    private static String toJson(List<String> items) {
        try {
            return OBJECT_MAPPER.writeValueAsString(items);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Failed to serialize final handoff report package archive list", exception);
        }
    }

    private static List<String> fromJson(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        try {
            return OBJECT_MAPPER.readValue(value, STRING_LIST);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Failed to deserialize final handoff report package archive list", exception);
        }
    }
}
