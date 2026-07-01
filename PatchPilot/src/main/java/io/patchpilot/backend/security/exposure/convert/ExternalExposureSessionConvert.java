package io.patchpilot.backend.security.exposure.convert;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureSessionVo;
import io.patchpilot.backend.security.exposure.domain.entity.ExternalExposureSessionEntity;

public final class ExternalExposureSessionConvert {

    private ExternalExposureSessionConvert() {
    }

    public static ExternalExposureSessionEntity toEntity(ExternalExposureSessionVo session) {
        ExternalExposureSessionEntity entity = new ExternalExposureSessionEntity();
        entity.setId(session.id());
        entity.setStatus(session.status());
        entity.setPublicUrl(session.publicUrl());
        entity.setWebhookUrl(session.webhookUrl());
        entity.setPurpose(session.purpose());
        entity.setOperator(session.operator());
        entity.setExpectedShutdownAt(session.expectedShutdownAt());
        entity.setNotes(session.notes());
        entity.setLinkedHandoffStatus(session.linkedHandoffStatus());
        entity.setLinkedReadinessArchiveId(session.linkedReadinessArchiveId());
        entity.setStartedAt(session.startedAt());
        entity.setClosedBy(session.closedBy());
        entity.setClosedAt(session.closedAt());
        entity.setCloseNotes(session.closeNotes());
        entity.setReport(session.markdownReport());
        return entity;
    }

    public static ExternalExposureSessionVo toVo(ExternalExposureSessionEntity entity) {
        return new ExternalExposureSessionVo(
                entity.getId(),
                entity.getStatus(),
                entity.getPublicUrl(),
                entity.getWebhookUrl(),
                entity.getPurpose(),
                entity.getOperator(),
                entity.getExpectedShutdownAt(),
                entity.getNotes(),
                entity.getLinkedHandoffStatus(),
                entity.getLinkedReadinessArchiveId(),
                entity.getStartedAt(),
                entity.getClosedBy(),
                entity.getClosedAt(),
                entity.getCloseNotes(),
                entity.getReport()
        );
    }
}
