package io.patchpilot.backend.demo;

import io.patchpilot.backend.configuration.ConfigurationSummaryService;
import io.patchpilot.backend.configuration.ConfigurationSummaryVo;
import io.patchpilot.backend.demo.domain.DemoAdapterFixtureEvidenceVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleSummaryVo;
import io.patchpilot.backend.demo.domain.DemoEvaluationRunReadinessEvidenceVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoHandoffFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareCenterVo;
import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveSummaryVo;
import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCertificateArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCertificateEvidenceVo;
import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCloseoutArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCloseoutEvidenceVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceShareCenterVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistStatus;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistVo;
import io.patchpilot.backend.demo.service.DemoLaunchAcceptanceCertificateArchiveRepository;
import io.patchpilot.backend.demo.service.DemoLaunchAcceptanceCloseoutArchiveRepository;
import io.patchpilot.backend.evaluation.EvaluationRunArchiveReadinessSummaryService;
import io.patchpilot.backend.evaluation.domain.EvaluationRunArchiveReadinessSummaryVo;
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
    private final Supplier<EvaluationRunArchiveReadinessSummaryVo> evaluationRunReadinessSupplier;
    private final Supplier<DemoHandoffPackageArchiveSummaryVo> handoffPackageArchiveSummarySupplier;
    private final Supplier<DemoHandoffShareCenterVo> handoffShareCenterSupplier;
    private final Supplier<DemoHandoffFinalizationVo> handoffFinalizationSupplier;
    private final Supplier<DemoLaunchEvidenceShareCenterVo> launchEvidenceShareCenterSupplier;
    private final Supplier<DemoLaunchEvidenceFinalizationVo> launchEvidenceFinalizationSupplier;
    private final Supplier<List<DemoLaunchAcceptanceCloseoutArchiveVo>> launchAcceptanceCloseoutArchiveSupplier;
    private final Supplier<List<DemoLaunchAcceptanceCertificateArchiveVo>> launchAcceptanceCertificateArchiveSupplier;

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
            EvaluationRunArchiveReadinessSummaryService evaluationRunArchiveReadinessSummaryService,
            DemoHandoffPackageArchiveSummaryService demoHandoffPackageArchiveSummaryService,
            DemoHandoffShareCenterService demoHandoffShareCenterService,
            DemoHandoffFinalizationService demoHandoffFinalizationService,
            DemoLaunchEvidenceShareCenterService demoLaunchEvidenceShareCenterService,
            DemoLaunchEvidenceFinalizationService demoLaunchEvidenceFinalizationService,
            DemoLaunchAcceptanceCloseoutArchiveRepository launchAcceptanceCloseoutArchiveRepository,
            DemoLaunchAcceptanceCertificateArchiveRepository launchAcceptanceCertificateArchiveRepository
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
                evaluationRunArchiveReadinessSummaryService::getSummary,
                demoHandoffPackageArchiveSummaryService::getArchiveSummary,
                demoHandoffShareCenterService::getShareCenter,
                demoHandoffFinalizationService::getFinalizationGate,
                demoLaunchEvidenceShareCenterService::getShareCenter,
                demoLaunchEvidenceFinalizationService::getFinalizationGate,
                () -> launchAcceptanceCloseoutArchiveRepository.listRecentArchives(20),
                () -> launchAcceptanceCertificateArchiveRepository.listRecentArchives(20)
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
            Supplier<EvaluationRunArchiveReadinessSummaryVo> evaluationRunReadinessSupplier,
            Supplier<DemoHandoffPackageArchiveSummaryVo> handoffPackageArchiveSummarySupplier,
            Supplier<DemoHandoffShareCenterVo> handoffShareCenterSupplier,
            Supplier<DemoHandoffFinalizationVo> handoffFinalizationSupplier,
            Supplier<DemoLaunchEvidenceShareCenterVo> launchEvidenceShareCenterSupplier,
            Supplier<DemoLaunchEvidenceFinalizationVo> launchEvidenceFinalizationSupplier,
            Supplier<List<DemoLaunchAcceptanceCloseoutArchiveVo>> launchAcceptanceCloseoutArchiveSupplier,
            Supplier<List<DemoLaunchAcceptanceCertificateArchiveVo>> launchAcceptanceCertificateArchiveSupplier
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
        this.evaluationRunReadinessSupplier = evaluationRunReadinessSupplier;
        this.handoffPackageArchiveSummarySupplier = handoffPackageArchiveSummarySupplier;
        this.handoffShareCenterSupplier = handoffShareCenterSupplier;
        this.handoffFinalizationSupplier = handoffFinalizationSupplier;
        this.launchEvidenceShareCenterSupplier = launchEvidenceShareCenterSupplier;
        this.launchEvidenceFinalizationSupplier = launchEvidenceFinalizationSupplier;
        this.launchAcceptanceCloseoutArchiveSupplier = launchAcceptanceCloseoutArchiveSupplier;
        this.launchAcceptanceCertificateArchiveSupplier = launchAcceptanceCertificateArchiveSupplier;
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
        EvaluationRunArchiveReadinessSummaryVo evaluationRunReadiness = evaluationRunReadinessSupplier.get();
        DemoHandoffPackageArchiveSummaryVo handoffPackageArchiveSummary = handoffPackageArchiveSummarySupplier.get();
        DemoHandoffShareCenterVo handoffShareCenter = handoffShareCenterSupplier.get();
        DemoHandoffFinalizationVo handoffFinalization = handoffFinalizationSupplier.get();
        DemoLaunchEvidenceShareCenterVo launchEvidenceShareCenter = launchEvidenceShareCenterSupplier.get();
        DemoLaunchEvidenceFinalizationVo launchEvidenceFinalization = launchEvidenceFinalizationSupplier.get();
        DemoLaunchAcceptanceCloseoutEvidenceVo launchAcceptanceCloseoutEvidence =
                launchAcceptanceCloseoutEvidence(launchAcceptanceCloseoutArchiveSupplier.get());
        DemoLaunchAcceptanceCertificateEvidenceVo launchAcceptanceCertificateEvidence =
                launchAcceptanceCertificateEvidence(launchAcceptanceCertificateArchiveSupplier.get());

        DemoAdapterFixtureEvidenceVo adapterFixtureEvidence = adapterFixtureEvidence(fixtures);
        DemoEvaluationRunReadinessEvidenceVo evaluationRunReadinessEvidence = evaluationRunReadinessEvidence(evaluationRunReadiness);
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
                evaluationRunReadinessEvidence,
                activeQuarantines.size(),
                recentPullRequestUrl,
                handoffFinalization,
                launchEvidenceFinalization,
                launchAcceptanceCloseoutEvidence,
                launchAcceptanceCertificateEvidence
        );
        DemoReadinessStatus status = aggregateStatus(
                readiness,
                smokeChecklist,
                adapterFixtureEvidence,
                evaluationRunReadinessEvidence,
                activeQuarantines.size(),
                handoffFinalization,
                launchEvidenceFinalization,
                launchAcceptanceCloseoutEvidence,
                launchAcceptanceCertificateEvidence
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
                evaluationRunReadinessEvidence,
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
                launchEvidenceShareCenter.status(),
                launchEvidenceShareCenter.shareReady(),
                launchEvidenceShareCenter.summary(),
                launchEvidenceShareCenter.nextAction(),
                launchEvidenceShareCenter.archiveCount(),
                launchEvidenceShareCenter.latestArchiveId(),
                launchEvidenceShareCenter.latestSessionId(),
                launchEvidenceShareCenter.latestPullRequestUrl(),
                launchEvidenceShareCenter.downloadActions(),
                launchEvidenceFinalization.status(),
                launchEvidenceFinalization.finalized(),
                launchEvidenceFinalization.summary(),
                launchEvidenceFinalization.nextAction(),
                launchEvidenceFinalization.deliveryReceiptFreshness(),
                launchEvidenceFinalization.deliveryReceiptFresh(),
                launchEvidenceFinalization.latestDeliveryReceiptId(),
                launchAcceptanceCloseoutEvidence,
                launchAcceptanceCertificateEvidence,
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

    private static DemoEvaluationRunReadinessEvidenceVo evaluationRunReadinessEvidence(
            EvaluationRunArchiveReadinessSummaryVo summary
    ) {
        return new DemoEvaluationRunReadinessEvidenceVo(
                evaluationReadinessStatus(summary.status()),
                summary.latestRun() == null ? null : summary.latestRun().id(),
                summary.previousRun() == null ? null : summary.previousRun().id(),
                summary.passedDelta(),
                summary.failedDelta(),
                summary.skippedDelta(),
                summary.coveredLanguages(),
                summary.coveredBuildSystems(),
                summary.safetyRejectionCategories(),
                summary.sideEffectContract(),
                summary.nextAction()
        );
    }

    private static DemoAdapterFixtureEvidenceVo adapterFixtureEvidence(List<LanguageAdapterFixtureVerificationVo> fixtures) {
        long failedCount = fixtures.stream()
                .filter(fixture -> !"PASS".equals(fixture.status()))
                .count();
        return new DemoAdapterFixtureEvidenceVo(fixtures.size(), failedCount);
    }

    private static DemoLaunchAcceptanceCloseoutEvidenceVo launchAcceptanceCloseoutEvidence(
            List<DemoLaunchAcceptanceCloseoutArchiveVo> archives
    ) {
        DemoLaunchAcceptanceCloseoutArchiveVo latestArchive = archives.isEmpty() ? null : archives.get(0);
        if (latestArchive == null) {
            return new DemoLaunchAcceptanceCloseoutEvidenceVo(
                    DemoReadinessStatus.NEEDS_ATTENTION,
                    false,
                    false,
                    "No launch acceptance closeout archive is available.",
                    "Archive the final launch acceptance closeout after launch evidence is accepted.",
                    0,
                    null,
                    null,
                    null,
                    null,
                    null,
                    List.of("Archive the final launch acceptance closeout before using the evidence bundle as the launch record.")
            );
        }

        boolean accepted = latestArchive.status() == DemoReadinessStatus.READY && latestArchive.accepted();
        DemoReadinessStatus status = closeoutEvidenceStatus(latestArchive);
        String nextAction = accepted
                ? "Use the archived launch acceptance closeout as the final launch evidence record."
                : "Resolve launch acceptance closeout blockers, then archive a new accepted closeout.";
        List<String> downloadActions = new ArrayList<>();
        downloadActions.add("Download launch acceptance closeout archive " + latestArchive.id() + ".");
        if (hasText(latestArchive.latestArchiveId())) {
            downloadActions.add("Download linked launch evidence archive " + latestArchive.latestArchiveId() + ".");
        }
        if (hasText(latestArchive.latestDeliveryReceiptId())) {
            downloadActions.add("Download launch evidence delivery receipt " + latestArchive.latestDeliveryReceiptId() + ".");
        }

        return new DemoLaunchAcceptanceCloseoutEvidenceVo(
                status,
                true,
                accepted,
                closeoutEvidenceSummary(latestArchive, accepted),
                nextAction,
                archives.size(),
                latestArchive.id(),
                latestArchive.latestArchiveId(),
                latestArchive.latestDeliveryReceiptId(),
                latestArchive.latestPullRequestUrl(),
                latestArchive.createdAt(),
                List.copyOf(downloadActions)
        );
    }

    private static DemoReadinessStatus closeoutEvidenceStatus(DemoLaunchAcceptanceCloseoutArchiveVo archive) {
        if (archive.status() == DemoReadinessStatus.BLOCKED) {
            return DemoReadinessStatus.BLOCKED;
        }
        return archive.status() == DemoReadinessStatus.READY && archive.accepted()
                ? DemoReadinessStatus.READY
                : DemoReadinessStatus.NEEDS_ATTENTION;
    }

    private static String closeoutEvidenceSummary(
            DemoLaunchAcceptanceCloseoutArchiveVo archive,
            boolean accepted
    ) {
        if (accepted) {
            return "Latest launch acceptance closeout archive is accepted and ready.";
        }
        if (archive.status() == DemoReadinessStatus.BLOCKED) {
            return "Latest launch acceptance closeout archive is blocked.";
        }
        return "Latest launch acceptance closeout archive is not accepted yet.";
    }

    private static DemoLaunchAcceptanceCertificateEvidenceVo launchAcceptanceCertificateEvidence(
            List<DemoLaunchAcceptanceCertificateArchiveVo> archives
    ) {
        DemoLaunchAcceptanceCertificateArchiveVo latestArchive = archives.isEmpty() ? null : archives.get(0);
        if (latestArchive == null) {
            return new DemoLaunchAcceptanceCertificateEvidenceVo(
                    DemoReadinessStatus.NEEDS_ATTENTION,
                    false,
                    false,
                    "No launch acceptance certificate archive is available.",
                    "Archive the final launch acceptance certificate after the launch acceptance closeout is certified.",
                    0,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    List.of("Archive the final launch acceptance certificate before using the evidence bundle as the external-review launch record.")
            );
        }

        boolean certified = latestArchive.status() == DemoReadinessStatus.READY && latestArchive.certified();
        DemoReadinessStatus status = certificateEvidenceStatus(latestArchive);
        String nextAction = certified
                ? "Use the archived launch acceptance certificate as the external-review launch record."
                : "Resolve launch acceptance certificate blockers, then archive a new certified certificate.";
        List<String> downloadActions = new ArrayList<>();
        downloadActions.add("Download launch acceptance certificate archive " + latestArchive.id() + ".");
        if (hasText(latestArchive.latestCloseoutArchiveId())) {
            downloadActions.add("Download linked launch acceptance closeout archive " + latestArchive.latestCloseoutArchiveId() + ".");
        }
        if (hasText(latestArchive.latestDeliveryReceiptId())) {
            downloadActions.add("Download launch evidence delivery receipt " + latestArchive.latestDeliveryReceiptId() + ".");
        }

        return new DemoLaunchAcceptanceCertificateEvidenceVo(
                status,
                true,
                certified,
                certificateEvidenceSummary(latestArchive, certified),
                nextAction,
                archives.size(),
                latestArchive.id(),
                latestArchive.latestCloseoutArchiveId(),
                latestArchive.latestLaunchEvidenceArchiveId(),
                latestArchive.latestDeliveryReceiptId(),
                latestArchive.latestPullRequestUrl(),
                latestArchive.archivedAt(),
                List.copyOf(downloadActions)
        );
    }

    private static DemoReadinessStatus certificateEvidenceStatus(DemoLaunchAcceptanceCertificateArchiveVo archive) {
        if (archive.status() == DemoReadinessStatus.BLOCKED) {
            return DemoReadinessStatus.BLOCKED;
        }
        return archive.status() == DemoReadinessStatus.READY && archive.certified()
                ? DemoReadinessStatus.READY
                : DemoReadinessStatus.NEEDS_ATTENTION;
    }

    private static String certificateEvidenceSummary(
            DemoLaunchAcceptanceCertificateArchiveVo archive,
            boolean certified
    ) {
        if (certified) {
            return "Latest launch acceptance certificate archive is certified and ready.";
        }
        if (archive.status() == DemoReadinessStatus.BLOCKED) {
            return "Latest launch acceptance certificate archive is blocked.";
        }
        return "Latest launch acceptance certificate archive is not certified yet.";
    }

    private static DemoReadinessStatus aggregateStatus(
            DemoReadinessVo readiness,
            DemoSmokeChecklistVo smokeChecklist,
            DemoAdapterFixtureEvidenceVo adapterFixtures,
            DemoEvaluationRunReadinessEvidenceVo evaluationRunReadiness,
            long activeQuarantineCount,
            DemoHandoffFinalizationVo handoffFinalization,
            DemoLaunchEvidenceFinalizationVo launchEvidenceFinalization,
            DemoLaunchAcceptanceCloseoutEvidenceVo launchAcceptanceCloseoutEvidence,
            DemoLaunchAcceptanceCertificateEvidenceVo launchAcceptanceCertificateEvidence
    ) {
        if (readiness.status() == DemoReadinessStatus.BLOCKED
                || smokeChecklist.status() == DemoSmokeChecklistStatus.BLOCKED
                || evaluationRunReadiness.status() == DemoReadinessStatus.BLOCKED
                || handoffFinalization.status() == DemoReadinessStatus.BLOCKED
                || launchEvidenceFinalization.status() == DemoReadinessStatus.BLOCKED
                || launchAcceptanceCloseoutEvidence.status() == DemoReadinessStatus.BLOCKED
                || launchAcceptanceCertificateEvidence.status() == DemoReadinessStatus.BLOCKED) {
            return DemoReadinessStatus.BLOCKED;
        }
        if (readiness.status() == DemoReadinessStatus.NEEDS_ATTENTION
                || smokeChecklist.status() == DemoSmokeChecklistStatus.NEEDS_ATTENTION
                || adapterFixtures.failedCount() > 0
                || evaluationRunReadiness.status() == DemoReadinessStatus.NEEDS_ATTENTION
                || activeQuarantineCount > 0
                || handoffFinalization.status() == DemoReadinessStatus.NEEDS_ATTENTION
                || launchEvidenceFinalization.status() == DemoReadinessStatus.NEEDS_ATTENTION
                || launchAcceptanceCloseoutEvidence.status() == DemoReadinessStatus.NEEDS_ATTENTION
                || launchAcceptanceCertificateEvidence.status() == DemoReadinessStatus.NEEDS_ATTENTION) {
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
            DemoEvaluationRunReadinessEvidenceVo evaluationRunReadiness,
            long activeQuarantineCount,
            String recentPullRequestUrl,
            DemoHandoffFinalizationVo handoffFinalization,
            DemoLaunchEvidenceFinalizationVo launchEvidenceFinalization,
            DemoLaunchAcceptanceCloseoutEvidenceVo launchAcceptanceCloseoutEvidence,
            DemoLaunchAcceptanceCertificateEvidenceVo launchAcceptanceCertificateEvidence
    ) {
        List<String> actions = new ArrayList<>();
        actions.addAll(readiness.nextActions());
        actions.addAll(smokeChecklist.nextActions());
        if (adapterFixtures.failedCount() > 0) {
            actions.add("Fix failing adapter fixtures before a live demo.");
        }
        if (evaluationRunReadiness.status() != DemoReadinessStatus.READY) {
            actions.add(evaluationRunReadiness.nextAction());
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
        if (launchEvidenceFinalization.status() != DemoReadinessStatus.READY) {
            actions.add(launchEvidenceFinalization.nextAction());
        }
        if (launchAcceptanceCloseoutEvidence.status() != DemoReadinessStatus.READY) {
            actions.add(launchAcceptanceCloseoutEvidence.nextAction());
        }
        if (launchAcceptanceCertificateEvidence.status() != DemoReadinessStatus.READY) {
            actions.add(launchAcceptanceCertificateEvidence.nextAction());
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

    private static DemoReadinessStatus evaluationReadinessStatus(String status) {
        return switch (status) {
            case "READY" -> DemoReadinessStatus.READY;
            case "BLOCKED" -> DemoReadinessStatus.BLOCKED;
            default -> DemoReadinessStatus.NEEDS_ATTENTION;
        };
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
