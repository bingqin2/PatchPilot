package io.patchpilot.backend.demo;

import io.patchpilot.backend.configuration.ConfigurationSummaryService;
import io.patchpilot.backend.configuration.ConfigurationSummaryVo;
import io.patchpilot.backend.demo.domain.DemoAdapterFixtureEvidenceVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleSummaryVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoHandoffFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareCenterVo;
import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveSummaryVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistStatus;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistVo;
import io.patchpilot.backend.github.credential.GitHubWebhookSetupReadinessService;
import io.patchpilot.backend.github.credential.domain.GitHubWebhookSetupReadinessVo;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticVo;
import io.patchpilot.backend.github.webhook.service.WebhookDeliveryDiagnosticService;
import io.patchpilot.backend.language.LanguageAdapterFixtureVerificationService;
import io.patchpilot.backend.language.domain.LanguageAdapterFixtureVerificationVo;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditSummaryVo;
import io.patchpilot.backend.safety.domain.TriggerQuarantineVo;
import io.patchpilot.backend.safety.service.RejectedTriggerAuditService;
import io.patchpilot.backend.safety.service.TriggerQuarantineRecordService;
import io.patchpilot.backend.task.domain.bo.FixTaskListQuery;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.service.FixTaskQueueQueryService;
import io.patchpilot.backend.task.service.FixTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
public class DemoEvidenceBundleService {

    private final Supplier<DemoReadinessVo> readinessSupplier;
    private final Supplier<DemoSmokeChecklistVo> smokeChecklistSupplier;
    private final Supplier<ConfigurationSummaryVo> configurationSupplier;
    private final Supplier<List<LanguageAdapterFixtureVerificationVo>> fixtureSupplier;
    private final Supplier<FixTaskQueueSummaryVo> queueSummarySupplier;
    private final Supplier<List<FixTaskVo>> recentTasksSupplier;
    private final Supplier<List<WebhookDeliveryDiagnosticVo>> webhookDeliveriesSupplier;
    private final Supplier<GitHubWebhookSetupReadinessVo> webhookSetupReadinessSupplier;
    private final Supplier<RejectedTriggerAuditSummaryVo> rejectedTriggerSummarySupplier;
    private final Supplier<List<TriggerQuarantineVo>> activeQuarantinesSupplier;
    private final Supplier<DemoHandoffPackageArchiveSummaryVo> handoffPackageArchiveSummarySupplier;
    private final Supplier<DemoHandoffShareCenterVo> handoffShareCenterSupplier;
    private final Supplier<DemoHandoffFinalizationVo> handoffFinalizationSupplier;

    @Autowired
    public DemoEvidenceBundleService(
            DemoReadinessService demoReadinessService,
            DemoSmokeChecklistService demoSmokeChecklistService,
            ConfigurationSummaryService configurationSummaryService,
            LanguageAdapterFixtureVerificationService fixtureVerificationService,
            FixTaskQueueQueryService fixTaskQueueQueryService,
            FixTaskService fixTaskService,
            WebhookDeliveryDiagnosticService webhookDeliveryDiagnosticService,
            GitHubWebhookSetupReadinessService gitHubWebhookSetupReadinessService,
            RejectedTriggerAuditService rejectedTriggerAuditService,
            TriggerQuarantineRecordService triggerQuarantineRecordService,
            DemoHandoffPackageArchiveSummaryService demoHandoffPackageArchiveSummaryService,
            DemoHandoffShareCenterService demoHandoffShareCenterService,
            DemoHandoffFinalizationService demoHandoffFinalizationService
    ) {
        this(
                demoReadinessService::getReadiness,
                demoSmokeChecklistService::getSmokeChecklist,
                configurationSummaryService::getConfigurationSummary,
                fixtureVerificationService::listFixtureVerifications,
                fixTaskQueueQueryService::summary,
                () -> fixTaskService.listTasks(new FixTaskListQuery(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        20,
                        0
                )),
                () -> webhookDeliveryDiagnosticService.listRecent(10),
                gitHubWebhookSetupReadinessService::getReadiness,
                () -> rejectedTriggerAuditService.summarizeRejectedTriggers(100),
                () -> triggerQuarantineRecordService.listQuarantines(true, 20),
                demoHandoffPackageArchiveSummaryService::getArchiveSummary,
                demoHandoffShareCenterService::getShareCenter,
                demoHandoffFinalizationService::getFinalizationGate
        );
    }

    DemoEvidenceBundleService(
            Supplier<DemoReadinessVo> readinessSupplier,
            Supplier<DemoSmokeChecklistVo> smokeChecklistSupplier,
            Supplier<ConfigurationSummaryVo> configurationSupplier,
            Supplier<List<LanguageAdapterFixtureVerificationVo>> fixtureSupplier,
            Supplier<FixTaskQueueSummaryVo> queueSummarySupplier,
            Supplier<List<FixTaskVo>> recentTasksSupplier,
            Supplier<List<WebhookDeliveryDiagnosticVo>> webhookDeliveriesSupplier,
            Supplier<GitHubWebhookSetupReadinessVo> webhookSetupReadinessSupplier,
            Supplier<RejectedTriggerAuditSummaryVo> rejectedTriggerSummarySupplier,
            Supplier<List<TriggerQuarantineVo>> activeQuarantinesSupplier,
            Supplier<DemoHandoffPackageArchiveSummaryVo> handoffPackageArchiveSummarySupplier,
            Supplier<DemoHandoffShareCenterVo> handoffShareCenterSupplier,
            Supplier<DemoHandoffFinalizationVo> handoffFinalizationSupplier
    ) {
        this.readinessSupplier = readinessSupplier;
        this.smokeChecklistSupplier = smokeChecklistSupplier;
        this.configurationSupplier = configurationSupplier;
        this.fixtureSupplier = fixtureSupplier;
        this.queueSummarySupplier = queueSummarySupplier;
        this.recentTasksSupplier = recentTasksSupplier;
        this.webhookDeliveriesSupplier = webhookDeliveriesSupplier;
        this.webhookSetupReadinessSupplier = webhookSetupReadinessSupplier;
        this.rejectedTriggerSummarySupplier = rejectedTriggerSummarySupplier;
        this.activeQuarantinesSupplier = activeQuarantinesSupplier;
        this.handoffPackageArchiveSummarySupplier = handoffPackageArchiveSummarySupplier;
        this.handoffShareCenterSupplier = handoffShareCenterSupplier;
        this.handoffFinalizationSupplier = handoffFinalizationSupplier;
    }

    public DemoEvidenceBundleVo getEvidenceBundle() {
        DemoReadinessVo readiness = readinessSupplier.get();
        DemoSmokeChecklistVo smokeChecklist = smokeChecklistSupplier.get();
        ConfigurationSummaryVo configuration = configurationSupplier.get();
        List<LanguageAdapterFixtureVerificationVo> fixtures = fixtureSupplier.get();
        FixTaskQueueSummaryVo queueSummary = queueSummarySupplier.get();
        List<FixTaskVo> recentTasks = recentTasksSupplier.get();
        List<WebhookDeliveryDiagnosticVo> webhookDeliveries = webhookDeliveriesSupplier.get();
        GitHubWebhookSetupReadinessVo webhookSetupReadiness = webhookSetupReadinessSupplier.get();
        RejectedTriggerAuditSummaryVo rejectedTriggerSummary = rejectedTriggerSummarySupplier.get();
        List<TriggerQuarantineVo> activeQuarantines = activeQuarantinesSupplier.get();
        DemoHandoffPackageArchiveSummaryVo handoffPackageArchiveSummary = handoffPackageArchiveSummarySupplier.get();
        DemoHandoffShareCenterVo handoffShareCenter = handoffShareCenterSupplier.get();
        DemoHandoffFinalizationVo handoffFinalization = handoffFinalizationSupplier.get();

        DemoAdapterFixtureEvidenceVo adapterFixtureEvidence = adapterFixtureEvidence(fixtures);
        FixTaskVo recentTask = recentTasks.isEmpty() ? null : recentTasks.get(0);
        String recentPullRequestUrl = recentTasks.stream()
                .filter(task -> task.status() == FixTaskStatus.COMPLETED && hasText(task.pullRequestUrl()))
                .map(FixTaskVo::pullRequestUrl)
                .findFirst()
                .orElse(null);
        WebhookDeliveryDiagnosticVo latestWebhookDelivery = webhookDeliveries.isEmpty() ? null : webhookDeliveries.get(0);

        List<String> nextActions = nextActions(
                readiness,
                smokeChecklist,
                adapterFixtureEvidence,
                activeQuarantines.size(),
                recentPullRequestUrl,
                handoffFinalization
        );
        DemoReadinessStatus status = aggregateStatus(
                readiness,
                smokeChecklist,
                adapterFixtureEvidence,
                activeQuarantines.size(),
                handoffFinalization
        );

        return new DemoEvidenceBundleVo(
                status,
                summary(status),
                new DemoEvidenceBundleSummaryVo(
                        adapterFixtureEvidence.totalCount(),
                        adapterFixtureEvidence.failedCount(),
                        recentTasks.size(),
                        activeQuarantines.size(),
                        recentPullRequestUrl != null
                ),
                readiness,
                smokeChecklist,
                configuration,
                adapterFixtureEvidence,
                queueSummary,
                recentTask,
                recentPullRequestUrl,
                webhookSetupReadiness,
                latestWebhookDelivery,
                List.copyOf(webhookDeliveries),
                rejectedTriggerSummary,
                activeQuarantines.size(),
                handoffShareChecklistStatus(handoffPackageArchiveSummary),
                handoffShareChecklistSummary(handoffPackageArchiveSummary),
                handoffShareChecklistNextAction(handoffPackageArchiveSummary),
                handoffShareCenter.status(),
                handoffShareCenter.summary(),
                handoffShareCenter.nextAction(),
                handoffShareCenter.downloadActions(),
                handoffShareCenter.deliveryReceiptRecorded(),
                handoffShareCenter.latestDeliveryReceiptId(),
                handoffShareCenter.latestDeliveryTarget(),
                handoffShareCenter.latestDeliveryChannel(),
                handoffShareCenter.latestDeliveredAt(),
                handoffShareCenter.deliveryReceiptFreshness(),
                handoffShareCenter.deliveryReceiptFresh(),
                handoffShareCenter.deliveryReceiptFreshnessSummary(),
                handoffFinalization.status(),
                handoffFinalization.finalized(),
                handoffFinalization.summary(),
                handoffFinalization.nextAction(),
                handoffFinalization.deliveryReceiptFreshness(),
                handoffFinalization.deliveryReceiptFresh(),
                handoffFinalization.latestDeliveryReceiptId(),
                Instant.now(),
                nextActions
        );
    }

    private static DemoAdapterFixtureEvidenceVo adapterFixtureEvidence(List<LanguageAdapterFixtureVerificationVo> fixtures) {
        long failedCount = fixtures.stream()
                .filter(fixture -> !"PASS".equals(fixture.status()))
                .count();
        return new DemoAdapterFixtureEvidenceVo(fixtures.size(), failedCount);
    }

    private static DemoReadinessStatus aggregateStatus(
            DemoReadinessVo readiness,
            DemoSmokeChecklistVo smokeChecklist,
            DemoAdapterFixtureEvidenceVo adapterFixtures,
            long activeQuarantineCount,
            DemoHandoffFinalizationVo handoffFinalization
    ) {
        if (readiness.status() == DemoReadinessStatus.BLOCKED
                || smokeChecklist.status() == DemoSmokeChecklistStatus.BLOCKED
                || handoffFinalization.status() == DemoReadinessStatus.BLOCKED) {
            return DemoReadinessStatus.BLOCKED;
        }
        if (readiness.status() == DemoReadinessStatus.NEEDS_ATTENTION
                || smokeChecklist.status() == DemoSmokeChecklistStatus.NEEDS_ATTENTION
                || adapterFixtures.failedCount() > 0
                || activeQuarantineCount > 0
                || handoffFinalization.status() == DemoReadinessStatus.NEEDS_ATTENTION) {
            return DemoReadinessStatus.NEEDS_ATTENTION;
        }
        return DemoReadinessStatus.READY;
    }

    private static String summary(DemoReadinessStatus status) {
        return switch (status) {
            case READY -> "Demo evidence bundle is ready.";
            case NEEDS_ATTENTION -> "Demo evidence bundle needs attention.";
            case BLOCKED -> "Demo evidence bundle is blocked.";
        };
    }

    private static DemoReadinessStatus handoffShareChecklistStatus(DemoHandoffPackageArchiveSummaryVo archiveSummary) {
        if (archiveSummary.latestHandoffReadinessStatus() == DemoReadinessStatus.BLOCKED) {
            return DemoReadinessStatus.BLOCKED;
        }
        return archiveSummary.shareReady() ? DemoReadinessStatus.READY : DemoReadinessStatus.NEEDS_ATTENTION;
    }

    private static String handoffShareChecklistSummary(DemoHandoffPackageArchiveSummaryVo archiveSummary) {
        if (archiveSummary.archiveCount() == 0) {
            return "No handoff package archive is available for sharing.";
        }
        DemoReadinessStatus status = handoffShareChecklistStatus(archiveSummary);
        if (status == DemoReadinessStatus.READY) {
            return "Latest handoff archive is ready to share.";
        }
        if (status == DemoReadinessStatus.BLOCKED) {
            return "Latest handoff archive is blocked from sharing.";
        }
        return "Latest handoff archive needs attention before sharing.";
    }

    private static String handoffShareChecklistNextAction(DemoHandoffPackageArchiveSummaryVo archiveSummary) {
        if (handoffShareChecklistStatus(archiveSummary) == DemoReadinessStatus.READY) {
            return "Share the latest handoff package summary and archived package with the reviewer.";
        }
        return archiveSummary.nextAction();
    }

    private static List<String> nextActions(
            DemoReadinessVo readiness,
            DemoSmokeChecklistVo smokeChecklist,
            DemoAdapterFixtureEvidenceVo adapterFixtures,
            long activeQuarantineCount,
            String recentPullRequestUrl,
            DemoHandoffFinalizationVo handoffFinalization
    ) {
        List<String> actions = new ArrayList<>();
        actions.addAll(readiness.nextActions());
        actions.addAll(smokeChecklist.nextActions());
        if (adapterFixtures.failedCount() > 0) {
            actions.add("Fix failing adapter fixtures before a live demo.");
        }
        if (activeQuarantineCount > 0) {
            actions.add("Inspect active trigger quarantines before a live demo.");
        }
        if (!hasText(recentPullRequestUrl)) {
            actions.add("Run one controlled issue-to-PR smoke task before a live demo.");
        }
        if (handoffFinalization.status() != DemoReadinessStatus.READY) {
            actions.add(handoffFinalization.nextAction());
        }
        List<String> distinctActions = actions.stream()
                .filter(DemoEvidenceBundleService::hasText)
                .distinct()
                .toList();
        if (!distinctActions.isEmpty()) {
            return distinctActions;
        }
        return List.of("Use this evidence bundle as the live demo baseline.");
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
