package io.patchpilot.backend.demo.convert;

import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCloseoutArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoLaunchAcceptanceCloseoutArchiveEntity;

public final class DemoLaunchAcceptanceCloseoutArchiveConvert {

    private DemoLaunchAcceptanceCloseoutArchiveConvert() {
    }

    public static DemoLaunchAcceptanceCloseoutArchiveEntity toEntity(DemoLaunchAcceptanceCloseoutArchiveVo archive) {
        DemoLaunchAcceptanceCloseoutArchiveEntity entity = new DemoLaunchAcceptanceCloseoutArchiveEntity();
        entity.setId(archive.id());
        entity.setStatus(archive.status().name());
        entity.setAccepted(archive.accepted());
        entity.setSummary(archive.summary());
        entity.setSessionId(archive.sessionId());
        entity.setLatestTaskId(archive.latestTaskId());
        entity.setLatestPullRequestUrl(archive.latestPullRequestUrl());
        entity.setLatestWebhookDeliveryId(archive.latestWebhookDeliveryId());
        entity.setEvaluationRunId(archive.evaluationRunId());
        entity.setLatestArchiveId(archive.latestArchiveId());
        entity.setLatestDeliveryReceiptId(archive.latestDeliveryReceiptId());
        entity.setLatestDeliveryTarget(archive.latestDeliveryTarget());
        entity.setLatestDeliveryChannel(archive.latestDeliveryChannel());
        entity.setDeliveryReceiptFreshness(archive.deliveryReceiptFreshness());
        entity.setCreatedAt(archive.createdAt());
        entity.setReport(archive.report());
        return entity;
    }

    public static DemoLaunchAcceptanceCloseoutArchiveVo toVo(DemoLaunchAcceptanceCloseoutArchiveEntity entity) {
        return new DemoLaunchAcceptanceCloseoutArchiveVo(
                entity.getId(),
                DemoReadinessStatus.valueOf(entity.getStatus()),
                Boolean.TRUE.equals(entity.getAccepted()),
                entity.getSummary(),
                entity.getSessionId(),
                entity.getLatestTaskId(),
                entity.getLatestPullRequestUrl(),
                entity.getLatestWebhookDeliveryId(),
                entity.getEvaluationRunId(),
                entity.getLatestArchiveId(),
                entity.getLatestDeliveryReceiptId(),
                entity.getLatestDeliveryTarget(),
                entity.getLatestDeliveryChannel(),
                entity.getDeliveryReceiptFreshness(),
                entity.getCreatedAt(),
                entity.getReport()
        );
    }
}
