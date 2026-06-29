package io.patchpilot.backend.demo;

import io.patchpilot.backend.configuration.ConfigurationSummaryService;
import io.patchpilot.backend.configuration.ConfigurationSummaryVo;
import io.patchpilot.backend.demo.domain.DemoAdapterFixtureEvidenceVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleSummaryVo;
import io.patchpilot.backend.demo.domain.DemoEvaluationRunReadinessEvidenceVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionCloseoutArchiveEvidenceVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionCloseoutArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionCloseoutVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceShareFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageArchiveEvidenceVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidenceVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryReceiptEvidenceVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageVo;
import io.patchpilot.backend.demo.domain.DemoFinalHandoffReportPackageArchiveEvidenceVo;
import io.patchpilot.backend.demo.domain.DemoFinalHandoffReportPackageArchiveVo;
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
import io.patchpilot.backend.demo.domain.DemoTaskEvidenceAcceptanceCertificateEvidenceVo;
import io.patchpilot.backend.demo.service.DemoFinalAcceptanceCompletionCloseoutArchiveRepository;
import io.patchpilot.backend.demo.service.DemoFinalExternalReviewEvidencePackageArchiveRepository;
import io.patchpilot.backend.demo.service.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveRepository;
import io.patchpilot.backend.demo.service.DemoFinalExternalReviewEvidencePackageDeliveryReceiptRepository;
import io.patchpilot.backend.demo.service.DemoLaunchAcceptanceCertificateArchiveRepository;
import io.patchpilot.backend.demo.service.DemoLaunchAcceptanceCloseoutArchiveRepository;
import io.patchpilot.backend.demo.service.DemoFinalHandoffReportPackageArchiveRepository;
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
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageAcceptanceCertificateArchiveVo;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.service.FixTaskEvidencePackageAcceptanceCertificateArchiveRepository;
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
    private final Supplier<DemoFinalAcceptanceShareFinalizationVo> finalAcceptanceShareFinalizationSupplier;
    private final Supplier<DemoFinalAcceptanceCompletionCloseoutVo> finalAcceptanceCompletionCloseoutSupplier;
    private final Supplier<List<DemoFinalAcceptanceCompletionCloseoutArchiveVo>>
            finalAcceptanceCompletionCloseoutArchiveSupplier;
    private final Supplier<DemoFinalExternalReviewEvidencePackageVo> finalExternalReviewEvidencePackageSupplier;
    private final Supplier<List<DemoFinalExternalReviewEvidencePackageArchiveVo>>
            finalExternalReviewEvidencePackageArchiveSupplier;
    private final Supplier<List<DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo>>
            finalExternalReviewEvidencePackageDeliveryReceiptSupplier;
    private final Supplier<DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo>
            finalExternalReviewEvidencePackageDeliveryFinalizationSupplier;
    private final Supplier<List<DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo>>
            finalExternalReviewEvidencePackageDeliveryFinalizationArchiveSupplier;
    private final Supplier<List<DemoLaunchAcceptanceCloseoutArchiveVo>> launchAcceptanceCloseoutArchiveSupplier;
    private final Supplier<List<DemoLaunchAcceptanceCertificateArchiveVo>> launchAcceptanceCertificateArchiveSupplier;
    private final Supplier<List<FixTaskEvidencePackageAcceptanceCertificateArchiveVo>>
            taskEvidenceAcceptanceCertificateArchiveSupplier;
    private final Supplier<List<DemoFinalHandoffReportPackageArchiveVo>> finalHandoffReportPackageArchiveSupplier;

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
            DemoFinalAcceptanceShareFinalizationService demoFinalAcceptanceShareFinalizationService,
            DemoFinalAcceptanceCompletionCloseoutService demoFinalAcceptanceCompletionCloseoutService,
            DemoFinalAcceptanceCompletionCloseoutArchiveRepository finalAcceptanceCompletionCloseoutArchiveRepository,
            DemoFinalExternalReviewEvidencePackageService demoFinalExternalReviewEvidencePackageService,
            DemoFinalExternalReviewEvidencePackageArchiveRepository finalExternalReviewEvidencePackageArchiveRepository,
            DemoFinalExternalReviewEvidencePackageDeliveryReceiptRepository
                    finalExternalReviewEvidencePackageDeliveryReceiptRepository,
            DemoFinalExternalReviewEvidencePackageDeliveryFinalizationService
                    demoFinalExternalReviewEvidencePackageDeliveryFinalizationService,
            DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveRepository
                    finalExternalReviewEvidencePackageDeliveryFinalizationArchiveRepository,
            DemoLaunchAcceptanceCloseoutArchiveRepository launchAcceptanceCloseoutArchiveRepository,
            DemoLaunchAcceptanceCertificateArchiveRepository launchAcceptanceCertificateArchiveRepository,
            FixTaskEvidencePackageAcceptanceCertificateArchiveRepository taskEvidenceAcceptanceCertificateArchiveRepository,
            DemoFinalHandoffReportPackageArchiveRepository finalHandoffReportPackageArchiveRepository
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
                demoFinalAcceptanceShareFinalizationService::getFinalizationGate,
                demoFinalAcceptanceCompletionCloseoutService::getCloseout,
                () -> launchAcceptanceCloseoutArchiveRepository.listRecentArchives(20),
                () -> launchAcceptanceCertificateArchiveRepository.listRecentArchives(20),
                () -> taskEvidenceAcceptanceCertificateArchiveRepository.listRecentArchives(20),
                () -> finalHandoffReportPackageArchiveRepository.listRecentArchives(20),
                () -> finalAcceptanceCompletionCloseoutArchiveRepository.listRecentArchives(20),
                demoFinalExternalReviewEvidencePackageService::getPackage,
                () -> finalExternalReviewEvidencePackageArchiveRepository.listRecentArchives(20),
                () -> finalExternalReviewEvidencePackageDeliveryReceiptRepository.listRecentReceipts(20),
                demoFinalExternalReviewEvidencePackageDeliveryFinalizationService::getFinalizationGate,
                () -> finalExternalReviewEvidencePackageDeliveryFinalizationArchiveRepository.listRecentArchives(20)
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
            Supplier<DemoFinalAcceptanceShareFinalizationVo> finalAcceptanceShareFinalizationSupplier,
            Supplier<DemoFinalAcceptanceCompletionCloseoutVo> finalAcceptanceCompletionCloseoutSupplier,
            Supplier<List<DemoLaunchAcceptanceCloseoutArchiveVo>> launchAcceptanceCloseoutArchiveSupplier,
            Supplier<List<DemoLaunchAcceptanceCertificateArchiveVo>> launchAcceptanceCertificateArchiveSupplier,
            Supplier<List<FixTaskEvidencePackageAcceptanceCertificateArchiveVo>>
                    taskEvidenceAcceptanceCertificateArchiveSupplier,
            Supplier<List<DemoFinalHandoffReportPackageArchiveVo>> finalHandoffReportPackageArchiveSupplier,
            Supplier<List<DemoFinalAcceptanceCompletionCloseoutArchiveVo>>
                    finalAcceptanceCompletionCloseoutArchiveSupplier,
            Supplier<DemoFinalExternalReviewEvidencePackageVo> finalExternalReviewEvidencePackageSupplier,
            Supplier<List<DemoFinalExternalReviewEvidencePackageArchiveVo>>
                    finalExternalReviewEvidencePackageArchiveSupplier,
            Supplier<List<DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo>>
                    finalExternalReviewEvidencePackageDeliveryReceiptSupplier,
            Supplier<DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo>
                    finalExternalReviewEvidencePackageDeliveryFinalizationSupplier,
            Supplier<List<DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo>>
                    finalExternalReviewEvidencePackageDeliveryFinalizationArchiveSupplier
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
        this.finalAcceptanceShareFinalizationSupplier = finalAcceptanceShareFinalizationSupplier;
        this.finalAcceptanceCompletionCloseoutSupplier = finalAcceptanceCompletionCloseoutSupplier;
        this.finalAcceptanceCompletionCloseoutArchiveSupplier = finalAcceptanceCompletionCloseoutArchiveSupplier;
        this.launchAcceptanceCloseoutArchiveSupplier = launchAcceptanceCloseoutArchiveSupplier;
        this.launchAcceptanceCertificateArchiveSupplier = launchAcceptanceCertificateArchiveSupplier;
        this.taskEvidenceAcceptanceCertificateArchiveSupplier = taskEvidenceAcceptanceCertificateArchiveSupplier;
        this.finalHandoffReportPackageArchiveSupplier = finalHandoffReportPackageArchiveSupplier;
        this.finalExternalReviewEvidencePackageSupplier = finalExternalReviewEvidencePackageSupplier;
        this.finalExternalReviewEvidencePackageArchiveSupplier = finalExternalReviewEvidencePackageArchiveSupplier;
        this.finalExternalReviewEvidencePackageDeliveryReceiptSupplier =
                finalExternalReviewEvidencePackageDeliveryReceiptSupplier;
        this.finalExternalReviewEvidencePackageDeliveryFinalizationSupplier =
                finalExternalReviewEvidencePackageDeliveryFinalizationSupplier;
        this.finalExternalReviewEvidencePackageDeliveryFinalizationArchiveSupplier =
                finalExternalReviewEvidencePackageDeliveryFinalizationArchiveSupplier;
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
            Supplier<DemoFinalAcceptanceShareFinalizationVo> finalAcceptanceShareFinalizationSupplier,
            Supplier<DemoFinalAcceptanceCompletionCloseoutVo> finalAcceptanceCompletionCloseoutSupplier,
            Supplier<List<DemoLaunchAcceptanceCloseoutArchiveVo>> launchAcceptanceCloseoutArchiveSupplier,
            Supplier<List<DemoLaunchAcceptanceCertificateArchiveVo>> launchAcceptanceCertificateArchiveSupplier,
            Supplier<List<FixTaskEvidencePackageAcceptanceCertificateArchiveVo>>
                    taskEvidenceAcceptanceCertificateArchiveSupplier,
            Supplier<List<DemoFinalHandoffReportPackageArchiveVo>> finalHandoffReportPackageArchiveSupplier,
            Supplier<List<DemoFinalAcceptanceCompletionCloseoutArchiveVo>>
                    finalAcceptanceCompletionCloseoutArchiveSupplier,
            Supplier<DemoFinalExternalReviewEvidencePackageVo> finalExternalReviewEvidencePackageSupplier,
            Supplier<List<DemoFinalExternalReviewEvidencePackageArchiveVo>>
                    finalExternalReviewEvidencePackageArchiveSupplier,
            Supplier<List<DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo>>
                    finalExternalReviewEvidencePackageDeliveryReceiptSupplier
    ) {
        this(
                readinessSupplier,
                smokeChecklistSupplier,
                configurationSupplier,
                fixtureSupplier,
                queueSummarySupplier,
                recentTasksSupplier,
                webhookDeliveriesSupplier,
                webhookSetupReadinessSupplier,
                rejectedTriggerSummarySupplier,
                activeQuarantinesSupplier,
                evaluationRunReadinessSupplier,
                handoffPackageArchiveSummarySupplier,
                handoffShareCenterSupplier,
                handoffFinalizationSupplier,
                launchEvidenceShareCenterSupplier,
                launchEvidenceFinalizationSupplier,
                finalAcceptanceShareFinalizationSupplier,
                finalAcceptanceCompletionCloseoutSupplier,
                launchAcceptanceCloseoutArchiveSupplier,
                launchAcceptanceCertificateArchiveSupplier,
                taskEvidenceAcceptanceCertificateArchiveSupplier,
                finalHandoffReportPackageArchiveSupplier,
                finalAcceptanceCompletionCloseoutArchiveSupplier,
                finalExternalReviewEvidencePackageSupplier,
                finalExternalReviewEvidencePackageArchiveSupplier,
                finalExternalReviewEvidencePackageDeliveryReceiptSupplier,
                DemoEvidenceBundleService::compatibilityFinalExternalReviewEvidencePackageDeliveryFinalization,
                DemoEvidenceBundleService::compatibilityFinalExternalReviewEvidencePackageDeliveryFinalizationArchives
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
            Supplier<DemoFinalAcceptanceShareFinalizationVo> finalAcceptanceShareFinalizationSupplier,
            Supplier<DemoFinalAcceptanceCompletionCloseoutVo> finalAcceptanceCompletionCloseoutSupplier,
            Supplier<List<DemoLaunchAcceptanceCloseoutArchiveVo>> launchAcceptanceCloseoutArchiveSupplier,
            Supplier<List<DemoLaunchAcceptanceCertificateArchiveVo>> launchAcceptanceCertificateArchiveSupplier,
            Supplier<List<FixTaskEvidencePackageAcceptanceCertificateArchiveVo>>
                    taskEvidenceAcceptanceCertificateArchiveSupplier,
            Supplier<List<DemoFinalHandoffReportPackageArchiveVo>> finalHandoffReportPackageArchiveSupplier,
            Supplier<List<DemoFinalAcceptanceCompletionCloseoutArchiveVo>>
                    finalAcceptanceCompletionCloseoutArchiveSupplier
    ) {
        this(
                readinessSupplier,
                smokeChecklistSupplier,
                configurationSupplier,
                fixtureSupplier,
                queueSummarySupplier,
                recentTasksSupplier,
                webhookDeliveriesSupplier,
                webhookSetupReadinessSupplier,
                rejectedTriggerSummarySupplier,
                activeQuarantinesSupplier,
                evaluationRunReadinessSupplier,
                handoffPackageArchiveSummarySupplier,
                handoffShareCenterSupplier,
                handoffFinalizationSupplier,
                launchEvidenceShareCenterSupplier,
                launchEvidenceFinalizationSupplier,
                finalAcceptanceShareFinalizationSupplier,
                finalAcceptanceCompletionCloseoutSupplier,
                launchAcceptanceCloseoutArchiveSupplier,
                launchAcceptanceCertificateArchiveSupplier,
                taskEvidenceAcceptanceCertificateArchiveSupplier,
                finalHandoffReportPackageArchiveSupplier,
                finalAcceptanceCompletionCloseoutArchiveSupplier,
                DemoEvidenceBundleService::compatibilityFinalExternalReviewEvidencePackage,
                DemoEvidenceBundleService::compatibilityFinalExternalReviewEvidencePackageArchives,
                DemoEvidenceBundleService::compatibilityFinalExternalReviewEvidencePackageDeliveryReceipts,
                DemoEvidenceBundleService::compatibilityFinalExternalReviewEvidencePackageDeliveryFinalization,
                DemoEvidenceBundleService::compatibilityFinalExternalReviewEvidencePackageDeliveryFinalizationArchives
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
            Supplier<DemoFinalAcceptanceShareFinalizationVo> finalAcceptanceShareFinalizationSupplier,
            Supplier<DemoFinalAcceptanceCompletionCloseoutVo> finalAcceptanceCompletionCloseoutSupplier,
            Supplier<List<DemoLaunchAcceptanceCloseoutArchiveVo>> launchAcceptanceCloseoutArchiveSupplier,
            Supplier<List<DemoLaunchAcceptanceCertificateArchiveVo>> launchAcceptanceCertificateArchiveSupplier,
            Supplier<List<FixTaskEvidencePackageAcceptanceCertificateArchiveVo>>
                    taskEvidenceAcceptanceCertificateArchiveSupplier,
            Supplier<List<DemoFinalHandoffReportPackageArchiveVo>> finalHandoffReportPackageArchiveSupplier
    ) {
        this(
                readinessSupplier,
                smokeChecklistSupplier,
                configurationSupplier,
                fixtureSupplier,
                queueSummarySupplier,
                recentTasksSupplier,
                webhookDeliveriesSupplier,
                webhookSetupReadinessSupplier,
                rejectedTriggerSummarySupplier,
                activeQuarantinesSupplier,
                evaluationRunReadinessSupplier,
                handoffPackageArchiveSummarySupplier,
                handoffShareCenterSupplier,
                handoffFinalizationSupplier,
                launchEvidenceShareCenterSupplier,
                launchEvidenceFinalizationSupplier,
                finalAcceptanceShareFinalizationSupplier,
                finalAcceptanceCompletionCloseoutSupplier,
                launchAcceptanceCloseoutArchiveSupplier,
                launchAcceptanceCertificateArchiveSupplier,
                taskEvidenceAcceptanceCertificateArchiveSupplier,
                finalHandoffReportPackageArchiveSupplier,
                DemoEvidenceBundleService::compatibilityFinalAcceptanceCompletionCloseoutArchives,
                DemoEvidenceBundleService::compatibilityFinalExternalReviewEvidencePackage,
                DemoEvidenceBundleService::compatibilityFinalExternalReviewEvidencePackageArchives,
                DemoEvidenceBundleService::compatibilityFinalExternalReviewEvidencePackageDeliveryReceipts,
                DemoEvidenceBundleService::compatibilityFinalExternalReviewEvidencePackageDeliveryFinalization,
                DemoEvidenceBundleService::compatibilityFinalExternalReviewEvidencePackageDeliveryFinalizationArchives
        );
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
        DemoFinalAcceptanceShareFinalizationVo finalAcceptanceShareFinalization =
                finalAcceptanceShareFinalizationSupplier.get();
        DemoFinalAcceptanceCompletionCloseoutVo finalAcceptanceCompletionCloseout =
                finalAcceptanceCompletionCloseoutSupplier.get();
        DemoFinalAcceptanceCompletionCloseoutArchiveEvidenceVo finalAcceptanceCompletionCloseoutArchiveEvidence =
                finalAcceptanceCompletionCloseoutArchiveEvidence(finalAcceptanceCompletionCloseoutArchiveSupplier.get());
        DemoLaunchAcceptanceCloseoutEvidenceVo launchAcceptanceCloseoutEvidence =
                launchAcceptanceCloseoutEvidence(launchAcceptanceCloseoutArchiveSupplier.get());
        DemoLaunchAcceptanceCertificateEvidenceVo launchAcceptanceCertificateEvidence =
                launchAcceptanceCertificateEvidence(launchAcceptanceCertificateArchiveSupplier.get());
        DemoTaskEvidenceAcceptanceCertificateEvidenceVo taskEvidenceAcceptanceCertificateEvidence =
                taskEvidenceAcceptanceCertificateEvidence(taskEvidenceAcceptanceCertificateArchiveSupplier.get());
        DemoFinalHandoffReportPackageArchiveEvidenceVo finalHandoffReportPackageArchiveEvidence =
                finalHandoffReportPackageArchiveEvidence(finalHandoffReportPackageArchiveSupplier.get());
        DemoFinalExternalReviewEvidencePackageVo finalExternalReviewEvidencePackage =
                finalExternalReviewEvidencePackageSupplier.get();
        DemoFinalExternalReviewEvidencePackageArchiveEvidenceVo finalExternalReviewEvidencePackageArchiveEvidence =
                finalExternalReviewEvidencePackageArchiveEvidence(finalExternalReviewEvidencePackageArchiveSupplier.get());
        DemoFinalExternalReviewEvidencePackageDeliveryReceiptEvidenceVo
                finalExternalReviewEvidencePackageDeliveryReceiptEvidence =
                finalExternalReviewEvidencePackageDeliveryReceiptEvidence(
                        finalExternalReviewEvidencePackageArchiveEvidence,
                        finalExternalReviewEvidencePackageDeliveryReceiptSupplier.get()
                );
        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo
                finalExternalReviewEvidencePackageDeliveryFinalization =
                finalExternalReviewEvidencePackageDeliveryFinalizationSupplier.get();
        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidenceVo
                finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence =
                finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence(
                        finalExternalReviewEvidencePackageDeliveryFinalizationArchiveSupplier.get()
                );

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
                finalAcceptanceShareFinalization,
                finalAcceptanceCompletionCloseout,
                finalAcceptanceCompletionCloseoutArchiveEvidence,
                launchAcceptanceCloseoutEvidence,
                launchAcceptanceCertificateEvidence,
                taskEvidenceAcceptanceCertificateEvidence,
                finalHandoffReportPackageArchiveEvidence,
                finalExternalReviewEvidencePackage,
                finalExternalReviewEvidencePackageArchiveEvidence,
                finalExternalReviewEvidencePackageDeliveryReceiptEvidence,
                finalExternalReviewEvidencePackageDeliveryFinalization,
                finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence
        );
        DemoReadinessStatus status = aggregateStatus(
                readiness,
                smokeChecklist,
                adapterFixtureEvidence,
                evaluationRunReadinessEvidence,
                activeQuarantines.size(),
                handoffFinalization,
                launchEvidenceFinalization,
                finalAcceptanceShareFinalization,
                finalAcceptanceCompletionCloseout,
                finalAcceptanceCompletionCloseoutArchiveEvidence,
                launchAcceptanceCloseoutEvidence,
                launchAcceptanceCertificateEvidence,
                taskEvidenceAcceptanceCertificateEvidence,
                finalHandoffReportPackageArchiveEvidence,
                finalExternalReviewEvidencePackage,
                finalExternalReviewEvidencePackageArchiveEvidence,
                finalExternalReviewEvidencePackageDeliveryReceiptEvidence,
                finalExternalReviewEvidencePackageDeliveryFinalization,
                finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence
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
                taskEvidenceAcceptanceCertificateEvidence,
                finalHandoffReportPackageArchiveEvidence,
                finalAcceptanceShareFinalization,
                finalAcceptanceCompletionCloseout,
                finalAcceptanceCompletionCloseoutArchiveEvidence,
                finalExternalReviewEvidencePackage,
                finalExternalReviewEvidencePackageArchiveEvidence,
                finalExternalReviewEvidencePackageDeliveryReceiptEvidence,
                finalExternalReviewEvidencePackageDeliveryFinalization,
                finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence,
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

    private static DemoTaskEvidenceAcceptanceCertificateEvidenceVo taskEvidenceAcceptanceCertificateEvidence(
            List<FixTaskEvidencePackageAcceptanceCertificateArchiveVo> archives
    ) {
        FixTaskEvidencePackageAcceptanceCertificateArchiveVo latestArchive = archives.isEmpty() ? null : archives.get(0);
        if (latestArchive == null) {
            return new DemoTaskEvidenceAcceptanceCertificateEvidenceVo(
                    DemoReadinessStatus.NEEDS_ATTENTION,
                    false,
                    false,
                    "No task evidence acceptance certificate archive is available.",
                    "Archive a certified task evidence acceptance certificate after final task evidence closeout.",
                    0,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    List.of("Archive a task evidence acceptance certificate before using the evidence bundle as task-level review proof.")
            );
        }

        boolean certified = "READY".equals(latestArchive.status()) && latestArchive.certified();
        DemoReadinessStatus status = taskCertificateEvidenceStatus(latestArchive);
        String nextAction = certified
                ? "Use the archived task evidence acceptance certificate as task-level review proof."
                : "Resolve task evidence acceptance certificate blockers, then archive a new certified certificate.";
        List<String> downloadActions = new ArrayList<>();
        downloadActions.add("Download task evidence acceptance certificate archive " + latestArchive.id() + ".");
        if (hasText(latestArchive.latestCloseoutArchiveId())) {
            downloadActions.add("Download linked task evidence acceptance closeout archive " + latestArchive.latestCloseoutArchiveId() + ".");
        }
        if (hasText(latestArchive.latestDeliveryReceiptId())) {
            downloadActions.add("Download task evidence delivery receipt " + latestArchive.latestDeliveryReceiptId() + ".");
        }

        return new DemoTaskEvidenceAcceptanceCertificateEvidenceVo(
                status,
                true,
                certified,
                taskCertificateEvidenceSummary(latestArchive, certified),
                nextAction,
                archives.size(),
                latestArchive.id(),
                latestArchive.latestCloseoutArchiveId(),
                latestArchive.latestEvidenceArchiveId(),
                latestArchive.latestDeliveryReceiptId(),
                latestArchive.latestTaskId(),
                latestArchive.latestPullRequestUrl(),
                latestArchive.archivedAt(),
                List.copyOf(downloadActions)
        );
    }

    private static DemoReadinessStatus taskCertificateEvidenceStatus(
            FixTaskEvidencePackageAcceptanceCertificateArchiveVo archive
    ) {
        if ("BLOCKED".equals(archive.status())) {
            return DemoReadinessStatus.BLOCKED;
        }
        return "READY".equals(archive.status()) && archive.certified()
                ? DemoReadinessStatus.READY
                : DemoReadinessStatus.NEEDS_ATTENTION;
    }

    private static String taskCertificateEvidenceSummary(
            FixTaskEvidencePackageAcceptanceCertificateArchiveVo archive,
            boolean certified
    ) {
        if (certified) {
            return "Latest task evidence acceptance certificate archive is certified and ready.";
        }
        if ("BLOCKED".equals(archive.status())) {
            return "Latest task evidence acceptance certificate archive is blocked.";
        }
        return "Latest task evidence acceptance certificate archive is not certified yet.";
    }

    private static DemoFinalHandoffReportPackageArchiveEvidenceVo finalHandoffReportPackageArchiveEvidence(
            List<DemoFinalHandoffReportPackageArchiveVo> archives
    ) {
        DemoFinalHandoffReportPackageArchiveVo latestArchive = archives.isEmpty() ? null : archives.get(0);
        if (latestArchive == null) {
            return new DemoFinalHandoffReportPackageArchiveEvidenceVo(
                    DemoReadinessStatus.NEEDS_ATTENTION,
                    false,
                    false,
                    "No final handoff report package archive is available.",
                    "Archive the final handoff report package after the post-demo handoff package is finalized.",
                    0,
                    null,
                    null,
                    null,
                    null,
                    null,
                    false,
                    null,
                    List.of("Archive the final handoff report package before using the evidence bundle as post-demo closeout proof.")
            );
        }

        DemoReadinessStatus status = finalHandoffReportPackageArchiveEvidenceStatus(latestArchive);
        String nextAction = status == DemoReadinessStatus.READY
                ? "Use the archived final handoff report package as the post-demo closeout proof."
                : "Resolve final handoff report package blockers, then archive a new download-ready package.";
        List<String> downloadActions = new ArrayList<>();
        downloadActions.add("Download final handoff report package archive " + latestArchive.id() + ".");
        if (hasText(latestArchive.latestArchiveId())) {
            downloadActions.add("Download linked handoff package archive " + latestArchive.latestArchiveId() + ".");
        }
        if (hasText(latestArchive.latestDeliveryReceiptId())) {
            downloadActions.add("Download handoff share delivery receipt " + latestArchive.latestDeliveryReceiptId() + ".");
        }
        if (hasText(latestArchive.taskCertificateArchiveId())) {
            downloadActions.add("Download task evidence acceptance certificate archive " + latestArchive.taskCertificateArchiveId() + ".");
        }

        return new DemoFinalHandoffReportPackageArchiveEvidenceVo(
                status,
                true,
                latestArchive.downloadReady(),
                finalHandoffReportPackageArchiveEvidenceSummary(latestArchive, status),
                nextAction,
                archives.size(),
                latestArchive.id(),
                latestArchive.latestArchiveId(),
                latestArchive.latestSessionId(),
                latestArchive.latestDeliveryReceiptId(),
                latestArchive.taskCertificateArchiveId(),
                latestArchive.taskCertificateReady(),
                latestArchive.archivedAt(),
                List.copyOf(downloadActions)
        );
    }

    private static DemoReadinessStatus finalHandoffReportPackageArchiveEvidenceStatus(
            DemoFinalHandoffReportPackageArchiveVo archive
    ) {
        if (archive.status() == DemoReadinessStatus.BLOCKED) {
            return DemoReadinessStatus.BLOCKED;
        }
        return archive.status() == DemoReadinessStatus.READY && archive.downloadReady()
                ? DemoReadinessStatus.READY
                : DemoReadinessStatus.NEEDS_ATTENTION;
    }

    private static String finalHandoffReportPackageArchiveEvidenceSummary(
            DemoFinalHandoffReportPackageArchiveVo archive,
            DemoReadinessStatus status
    ) {
        if (status == DemoReadinessStatus.READY) {
            return "Latest final handoff report package archive is download-ready and ready.";
        }
        if (archive.status() == DemoReadinessStatus.BLOCKED) {
            return "Latest final handoff report package archive is blocked.";
        }
        return "Latest final handoff report package archive is not download-ready yet.";
    }

    private static DemoFinalAcceptanceCompletionCloseoutArchiveEvidenceVo finalAcceptanceCompletionCloseoutArchiveEvidence(
            List<DemoFinalAcceptanceCompletionCloseoutArchiveVo> archives
    ) {
        DemoFinalAcceptanceCompletionCloseoutArchiveVo latestArchive = archives.isEmpty() ? null : archives.get(0);
        if (latestArchive == null) {
            return new DemoFinalAcceptanceCompletionCloseoutArchiveEvidenceVo(
                    DemoReadinessStatus.NEEDS_ATTENTION,
                    false,
                    false,
                    "No final acceptance completion closeout archive is available.",
                    "Archive the final acceptance completion closeout after it is READY and closed.",
                    0,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    List.of("Archive the final acceptance completion closeout after it is READY and closed.")
            );
        }

        DemoReadinessStatus status = finalAcceptanceCompletionCloseoutArchiveEvidenceStatus(latestArchive);
        String nextAction = status == DemoReadinessStatus.READY
                ? "Use the archived final acceptance completion closeout as the frozen external-review completion record."
                : "Resolve final acceptance completion closeout archive blockers, then archive a new closed closeout.";
        List<String> downloadActions = new ArrayList<>();
        downloadActions.add("Download final acceptance completion closeout archive " + latestArchive.id() + ".");
        if (hasText(latestArchive.latestCompletionArchiveId())) {
            downloadActions.add("Download linked final acceptance completion archive " + latestArchive.latestCompletionArchiveId() + ".");
        }
        if (hasText(latestArchive.latestCompletionEvidenceDeliveryReceiptId())) {
            downloadActions.add("Download final acceptance completion evidence delivery receipt "
                    + latestArchive.latestCompletionEvidenceDeliveryReceiptId() + ".");
        }

        return new DemoFinalAcceptanceCompletionCloseoutArchiveEvidenceVo(
                status,
                true,
                latestArchive.closed(),
                finalAcceptanceCompletionCloseoutArchiveEvidenceSummary(latestArchive, status),
                nextAction,
                archives.size(),
                latestArchive.id(),
                latestArchive.latestCompletionArchiveId(),
                latestArchive.latestCompletionEvidenceDeliveryReceiptId(),
                latestArchive.latestTaskId(),
                latestArchive.latestPullRequestUrl(),
                latestArchive.archivedAt(),
                List.copyOf(downloadActions)
        );
    }

    private static DemoReadinessStatus finalAcceptanceCompletionCloseoutArchiveEvidenceStatus(
            DemoFinalAcceptanceCompletionCloseoutArchiveVo archive
    ) {
        if (archive.status() == DemoReadinessStatus.BLOCKED) {
            return DemoReadinessStatus.BLOCKED;
        }
        return archive.status() == DemoReadinessStatus.READY && archive.closed()
                ? DemoReadinessStatus.READY
                : DemoReadinessStatus.NEEDS_ATTENTION;
    }

    private static String finalAcceptanceCompletionCloseoutArchiveEvidenceSummary(
            DemoFinalAcceptanceCompletionCloseoutArchiveVo archive,
            DemoReadinessStatus status
    ) {
        if (status == DemoReadinessStatus.READY) {
            return "Latest final acceptance completion closeout archive is closed and ready.";
        }
        if (archive.status() == DemoReadinessStatus.BLOCKED) {
            return "Latest final acceptance completion closeout archive is blocked.";
        }
        return "Latest final acceptance completion closeout archive is not closed yet.";
    }

    private static List<DemoFinalAcceptanceCompletionCloseoutArchiveVo>
    compatibilityFinalAcceptanceCompletionCloseoutArchives() {
        return List.of(new DemoFinalAcceptanceCompletionCloseoutArchiveVo(
                "final-acceptance-completion-closeout-archive-compat",
                DemoReadinessStatus.READY,
                true,
                "PatchPilot final acceptance completion closeout is archived.",
                "Use this archived closeout as the frozen external-review completion record.",
                null,
                null,
                null,
                "final-acceptance-completion-archive-compat",
                "final-acceptance-completion-evidence-delivery-receipt-compat",
                null,
                null,
                null,
                "FRESH",
                List.of(),
                List.of(),
                "Compatibility evidence is read-only.",
                "# PatchPilot Final Acceptance Completion Closeout Archive",
                Instant.EPOCH,
                Instant.EPOCH
        ));
    }

    private static DemoFinalExternalReviewEvidencePackageArchiveEvidenceVo finalExternalReviewEvidencePackageArchiveEvidence(
            List<DemoFinalExternalReviewEvidencePackageArchiveVo> archives
    ) {
        DemoFinalExternalReviewEvidencePackageArchiveVo latestArchive = archives.isEmpty() ? null : archives.get(0);
        if (latestArchive == null) {
            return new DemoFinalExternalReviewEvidencePackageArchiveEvidenceVo(
                    DemoReadinessStatus.NEEDS_ATTENTION,
                    false,
                    false,
                    "No final external-review evidence package archive is available.",
                    "Archive the final external-review evidence package after it is READY.",
                    0,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    List.of("Archive the final external-review evidence package after it is READY.")
            );
        }

        DemoReadinessStatus status = finalExternalReviewEvidencePackageArchiveEvidenceStatus(latestArchive);
        String nextAction = status == DemoReadinessStatus.READY
                ? "Use the archived final external-review evidence package as the frozen reviewer-facing record."
                : "Resolve final external-review evidence package archive blockers, then archive a new READY package.";
        List<String> downloadActions = new ArrayList<>();
        downloadActions.add("Download final external-review evidence package archive " + latestArchive.id() + ".");
        if (hasText(latestArchive.closeoutArchiveId())) {
            downloadActions.add("Download final acceptance completion closeout archive " + latestArchive.closeoutArchiveId() + ".");
        }
        if (hasText(latestArchive.completionArchiveId())) {
            downloadActions.add("Download final acceptance completion archive " + latestArchive.completionArchiveId() + ".");
        }
        if (hasText(latestArchive.completionEvidenceDeliveryReceiptId())) {
            downloadActions.add("Download final acceptance completion evidence delivery receipt "
                    + latestArchive.completionEvidenceDeliveryReceiptId() + ".");
        }

        return new DemoFinalExternalReviewEvidencePackageArchiveEvidenceVo(
                status,
                true,
                latestArchive.readyForExternalReview(),
                finalExternalReviewEvidencePackageArchiveEvidenceSummary(latestArchive, status),
                nextAction,
                archives.size(),
                latestArchive.id(),
                latestArchive.closeoutArchiveId(),
                latestArchive.completionArchiveId(),
                latestArchive.completionEvidenceDeliveryReceiptId(),
                latestArchive.latestTaskId(),
                latestArchive.latestPullRequestUrl(),
                latestArchive.archivedAt(),
                List.copyOf(downloadActions)
        );
    }

    private static DemoReadinessStatus finalExternalReviewEvidencePackageArchiveEvidenceStatus(
            DemoFinalExternalReviewEvidencePackageArchiveVo archive
    ) {
        if (archive.status() == DemoReadinessStatus.BLOCKED) {
            return DemoReadinessStatus.BLOCKED;
        }
        return archive.status() == DemoReadinessStatus.READY && archive.readyForExternalReview()
                ? DemoReadinessStatus.READY
                : DemoReadinessStatus.NEEDS_ATTENTION;
    }

    private static String finalExternalReviewEvidencePackageArchiveEvidenceSummary(
            DemoFinalExternalReviewEvidencePackageArchiveVo archive,
            DemoReadinessStatus status
    ) {
        if (status == DemoReadinessStatus.READY) {
            return "Latest final external-review evidence package archive is ready for external review.";
        }
        if (archive.status() == DemoReadinessStatus.BLOCKED) {
            return "Latest final external-review evidence package archive is blocked.";
        }
        return "Latest final external-review evidence package archive is not ready for external review yet.";
    }

    private static DemoFinalExternalReviewEvidencePackageDeliveryReceiptEvidenceVo
    finalExternalReviewEvidencePackageDeliveryReceiptEvidence(
            DemoFinalExternalReviewEvidencePackageArchiveEvidenceVo archiveEvidence,
            List<DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo> receipts
    ) {
        DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo latestReceipt =
                receipts.isEmpty() ? null : receipts.get(0);
        if (latestReceipt == null || archiveEvidence.status() != DemoReadinessStatus.READY) {
            return new DemoFinalExternalReviewEvidencePackageDeliveryReceiptEvidenceVo(
                    DemoReadinessStatus.NEEDS_ATTENTION,
                    false,
                    false,
                    "MISSING",
                    "No final external-review package delivery receipt is available.",
                    "Share the latest final external-review package archive and record a delivery receipt.",
                    receipts.size(),
                    null,
                    archiveEvidence.latestArchiveId(),
                    archiveEvidence.latestCloseoutArchiveId(),
                    archiveEvidence.latestCompletionArchiveId(),
                    archiveEvidence.latestCompletionEvidenceDeliveryReceiptId(),
                    archiveEvidence.latestTaskId(),
                    archiveEvidence.latestPullRequestUrl(),
                    null,
                    null,
                    null,
                    List.of("Record a final external-review package delivery receipt after sharing the archive.")
            );
        }

        boolean fresh = hasText(archiveEvidence.latestArchiveId())
                && archiveEvidence.latestArchiveId().equals(latestReceipt.finalExternalReviewPackageArchiveId())
                && latestReceipt.status() == DemoReadinessStatus.READY
                && latestReceipt.finalExternalReviewPackageArchiveStatus() == DemoReadinessStatus.READY;
        DemoReadinessStatus status = fresh ? DemoReadinessStatus.READY : DemoReadinessStatus.NEEDS_ATTENTION;
        String freshness = fresh ? "FRESH" : "STALE";
        String summary = fresh
                ? "Latest final external-review package delivery receipt is fresh."
                : "Latest final external-review package delivery receipt does not match the current package archive.";
        String nextAction = fresh
                ? "Use the delivery receipt as proof that the frozen final external-review package was shared."
                : "Record a fresh delivery receipt for the latest final external-review package archive.";

        return new DemoFinalExternalReviewEvidencePackageDeliveryReceiptEvidenceVo(
                status,
                true,
                fresh,
                freshness,
                summary,
                nextAction,
                receipts.size(),
                latestReceipt.id(),
                latestReceipt.finalExternalReviewPackageArchiveId(),
                latestReceipt.closeoutArchiveId(),
                latestReceipt.completionArchiveId(),
                latestReceipt.completionEvidenceDeliveryReceiptId(),
                latestReceipt.latestTaskId(),
                latestReceipt.latestPullRequestUrl(),
                latestReceipt.deliveryTarget(),
                latestReceipt.deliveryChannel(),
                latestReceipt.deliveredAt(),
                List.of("Download final external-review package delivery receipt " + latestReceipt.id() + ".")
        );
    }

    private static List<DemoFinalExternalReviewEvidencePackageArchiveVo>
    compatibilityFinalExternalReviewEvidencePackageArchives() {
        return List.of(new DemoFinalExternalReviewEvidencePackageArchiveVo(
                "final-external-review-package-archive-compat",
                DemoReadinessStatus.READY,
                true,
                "PatchPilot final external-review evidence package archive is ready.",
                "Use this archived package as the frozen reviewer-facing record.",
                null,
                null,
                null,
                "final-acceptance-completion-archive-compat",
                "final-acceptance-completion-evidence-delivery-receipt-compat",
                "final-acceptance-completion-closeout-archive-compat",
                null,
                null,
                null,
                "FRESH",
                Instant.EPOCH,
                List.of("Compatibility final external-review package archive is ready."),
                List.of("Download final external-review evidence package archive."),
                "Compatibility final external-review package archive is read-only.",
                "# PatchPilot Final External Review Evidence Package Archive",
                Instant.EPOCH,
                Instant.EPOCH
        ));
    }

    private static DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidenceVo
    finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence(
            List<DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo> archives
    ) {
        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo latestArchive =
                archives.isEmpty() ? null : archives.get(0);
        if (latestArchive == null) {
            return new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidenceVo(
                    DemoReadinessStatus.NEEDS_ATTENTION,
                    false,
                    false,
                    "No final external-review package delivery finalization archive is available.",
                    "Archive the READY final external-review package delivery finalization.",
                    0,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    List.of("Archive the READY final external-review package delivery finalization.")
            );
        }

        DemoReadinessStatus status =
                finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidenceStatus(latestArchive);
        String nextAction = status == DemoReadinessStatus.READY
                ? "Use the archived final external-review package delivery finalization as the delivery closure record."
                : "Resolve final external-review package delivery finalization archive blockers, then archive a new finalized record.";
        List<String> downloadActions = new ArrayList<>();
        downloadActions.add(
                "Download final external-review package delivery finalization archive " + latestArchive.id() + "."
        );
        if (hasText(latestArchive.latestArchiveId())) {
            downloadActions.add("Download final external-review package archive " + latestArchive.latestArchiveId() + ".");
        }
        if (hasText(latestArchive.latestDeliveryReceiptId())) {
            downloadActions.add("Download final external-review package delivery receipt "
                    + latestArchive.latestDeliveryReceiptId() + ".");
        }

        return new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidenceVo(
                status,
                true,
                latestArchive.finalized(),
                finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidenceSummary(latestArchive, status),
                nextAction,
                archives.size(),
                latestArchive.id(),
                latestArchive.latestArchiveId(),
                latestArchive.latestDeliveryReceiptId(),
                latestArchive.latestTaskId(),
                latestArchive.latestPullRequestUrl(),
                latestArchive.archivedAt(),
                List.copyOf(downloadActions)
        );
    }

    private static DemoReadinessStatus
    finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidenceStatus(
            DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo archive
    ) {
        if (archive.status() == DemoReadinessStatus.BLOCKED) {
            return DemoReadinessStatus.BLOCKED;
        }
        return archive.status() == DemoReadinessStatus.READY && archive.finalized()
                ? DemoReadinessStatus.READY
                : DemoReadinessStatus.NEEDS_ATTENTION;
    }

    private static String finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidenceSummary(
            DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo archive,
            DemoReadinessStatus status
    ) {
        if (status == DemoReadinessStatus.READY) {
            return "Latest final external-review package delivery finalization archive is finalized and ready.";
        }
        if (archive.status() == DemoReadinessStatus.BLOCKED) {
            return "Latest final external-review package delivery finalization archive is blocked.";
        }
        return "Latest final external-review package delivery finalization archive is not finalized yet.";
    }

    private static DemoFinalExternalReviewEvidencePackageVo compatibilityFinalExternalReviewEvidencePackage() {
        return new DemoFinalExternalReviewEvidencePackageVo(
                DemoReadinessStatus.READY,
                true,
                "PatchPilot final external-review evidence package is ready.",
                "Share this package with reviewers as the frozen external-review record.",
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                null,
                null,
                null,
                "final-acceptance-completion-archive-compat",
                "final-acceptance-completion-evidence-delivery-receipt-compat",
                "final-acceptance-completion-closeout-archive-compat",
                null,
                null,
                null,
                "FRESH",
                Instant.EPOCH,
                Instant.EPOCH,
                List.of(new DemoFinalExternalReviewEvidencePackageVo.Check(
                        "Final external-review evidence package",
                        DemoReadinessStatus.READY,
                        "Compatibility final external-review package is ready.",
                        "No action needed."
                )),
                List.of("Compatibility final external-review evidence is ready."),
                List.of("Download final external-review evidence package."),
                "Compatibility final external-review package is read-only.",
                "# PatchPilot Final External Review Evidence Package"
        );
    }

    private static List<DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo>
    compatibilityFinalExternalReviewEvidencePackageDeliveryReceipts() {
        return List.of(new DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo(
                "final-external-review-package-delivery-receipt-compat",
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                "final-external-review-package-archive-compat",
                "final-acceptance-completion-closeout-archive-compat",
                "final-acceptance-completion-archive-compat",
                "final-acceptance-completion-evidence-delivery-receipt-compat",
                null,
                null,
                "PatchPilot final external-review evidence package archive is ready.",
                "Use this archived package as the frozen reviewer-facing record.",
                "email",
                "reviewer@example.com",
                "compatibility",
                "Compatibility receipt.",
                Instant.EPOCH,
                Instant.EPOCH,
                "# PatchPilot Final External Review Package Delivery Receipt"
        ));
    }

    private static DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo
    compatibilityFinalExternalReviewEvidencePackageDeliveryFinalization() {
        return new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo(
                DemoReadinessStatus.READY,
                true,
                "Final external-review package delivery is finalized with a fresh package delivery receipt.",
                "Use the finalization report as proof that the frozen external-review package was delivered.",
                "final-external-review-package-archive-compat",
                "final-external-review-package-delivery-receipt-compat",
                "final-acceptance-completion-closeout-archive-compat",
                "final-acceptance-completion-archive-compat",
                "final-acceptance-completion-evidence-delivery-receipt-compat",
                null,
                null,
                "reviewer@example.com",
                "email",
                Instant.EPOCH.toString(),
                "FRESH",
                true,
                "Latest package delivery receipt matches the current frozen final external-review package.",
                List.of(new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo.Check(
                        "Final external-review package delivery receipt",
                        DemoReadinessStatus.READY,
                        "Compatibility final external-review package delivery receipt is fresh.",
                        "No action needed."
                )),
                List.of("Compatibility final external-review package delivery is finalized."),
                List.of("Download final external-review package delivery finalization report."),
                "Compatibility final external-review package delivery finalization is read-only.",
                "# PatchPilot Final External Review Package Delivery Finalization",
                Instant.EPOCH
        );
    }

    private static List<DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo>
    compatibilityFinalExternalReviewEvidencePackageDeliveryFinalizationArchives() {
        return List.of(new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo(
                "final-external-review-package-delivery-finalization-archive-compat",
                DemoReadinessStatus.READY,
                true,
                "Final external-review package delivery finalization archive is ready.",
                "Use the archived delivery finalization as proof that the frozen package was delivered.",
                "final-external-review-package-archive-compat",
                "final-external-review-package-delivery-receipt-compat",
                "final-acceptance-completion-closeout-archive-compat",
                "final-acceptance-completion-archive-compat",
                "final-acceptance-completion-evidence-delivery-receipt-compat",
                null,
                null,
                "reviewer@example.com",
                "email",
                Instant.EPOCH.toString(),
                "FRESH",
                true,
                "Latest package delivery receipt matches the current frozen final external-review package.",
                List.of(new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo.Check(
                        "Final external-review package delivery finalization archive",
                        DemoReadinessStatus.READY,
                        "Compatibility final external-review package delivery finalization archive is ready.",
                        "No action needed."
                )),
                List.of("Compatibility final external-review package delivery finalization archive is ready."),
                List.of("Download final external-review package delivery finalization archive."),
                "Compatibility final external-review package delivery finalization archive is read-only.",
                "# PatchPilot Final External Review Package Delivery Finalization Archive",
                Instant.EPOCH,
                Instant.EPOCH
        ));
    }

    private static DemoReadinessStatus aggregateStatus(
            DemoReadinessVo readiness,
            DemoSmokeChecklistVo smokeChecklist,
            DemoAdapterFixtureEvidenceVo adapterFixtures,
            DemoEvaluationRunReadinessEvidenceVo evaluationRunReadiness,
            long activeQuarantineCount,
            DemoHandoffFinalizationVo handoffFinalization,
            DemoLaunchEvidenceFinalizationVo launchEvidenceFinalization,
            DemoFinalAcceptanceShareFinalizationVo finalAcceptanceShareFinalization,
            DemoFinalAcceptanceCompletionCloseoutVo finalAcceptanceCompletionCloseout,
            DemoFinalAcceptanceCompletionCloseoutArchiveEvidenceVo finalAcceptanceCompletionCloseoutArchiveEvidence,
            DemoLaunchAcceptanceCloseoutEvidenceVo launchAcceptanceCloseoutEvidence,
            DemoLaunchAcceptanceCertificateEvidenceVo launchAcceptanceCertificateEvidence,
            DemoTaskEvidenceAcceptanceCertificateEvidenceVo taskEvidenceAcceptanceCertificateEvidence,
            DemoFinalHandoffReportPackageArchiveEvidenceVo finalHandoffReportPackageArchiveEvidence,
            DemoFinalExternalReviewEvidencePackageVo finalExternalReviewEvidencePackage,
            DemoFinalExternalReviewEvidencePackageArchiveEvidenceVo finalExternalReviewEvidencePackageArchiveEvidence,
            DemoFinalExternalReviewEvidencePackageDeliveryReceiptEvidenceVo
                    finalExternalReviewEvidencePackageDeliveryReceiptEvidence,
            DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo
                    finalExternalReviewEvidencePackageDeliveryFinalization,
            DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidenceVo
                    finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence
    ) {
        if (readiness.status() == DemoReadinessStatus.BLOCKED
                || smokeChecklist.status() == DemoSmokeChecklistStatus.BLOCKED
                || evaluationRunReadiness.status() == DemoReadinessStatus.BLOCKED
                || handoffFinalization.status() == DemoReadinessStatus.BLOCKED
                || launchEvidenceFinalization.status() == DemoReadinessStatus.BLOCKED
                || finalAcceptanceShareFinalization.status() == DemoReadinessStatus.BLOCKED
                || finalAcceptanceCompletionCloseout.status() == DemoReadinessStatus.BLOCKED
                || finalAcceptanceCompletionCloseoutArchiveEvidence.status() == DemoReadinessStatus.BLOCKED
                || launchAcceptanceCloseoutEvidence.status() == DemoReadinessStatus.BLOCKED
                || launchAcceptanceCertificateEvidence.status() == DemoReadinessStatus.BLOCKED
                || taskEvidenceAcceptanceCertificateEvidence.status() == DemoReadinessStatus.BLOCKED
                || finalHandoffReportPackageArchiveEvidence.status() == DemoReadinessStatus.BLOCKED
                || finalExternalReviewEvidencePackage.status() == DemoReadinessStatus.BLOCKED
                || finalExternalReviewEvidencePackageArchiveEvidence.status() == DemoReadinessStatus.BLOCKED
                || finalExternalReviewEvidencePackageDeliveryReceiptEvidence.status() == DemoReadinessStatus.BLOCKED
                || finalExternalReviewEvidencePackageDeliveryFinalization.status() == DemoReadinessStatus.BLOCKED
                || finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence.status()
                == DemoReadinessStatus.BLOCKED) {
            return DemoReadinessStatus.BLOCKED;
        }
        if (readiness.status() == DemoReadinessStatus.NEEDS_ATTENTION
                || smokeChecklist.status() == DemoSmokeChecklistStatus.NEEDS_ATTENTION
                || adapterFixtures.failedCount() > 0
                || evaluationRunReadiness.status() == DemoReadinessStatus.NEEDS_ATTENTION
                || activeQuarantineCount > 0
                || handoffFinalization.status() == DemoReadinessStatus.NEEDS_ATTENTION
                || launchEvidenceFinalization.status() == DemoReadinessStatus.NEEDS_ATTENTION
                || finalAcceptanceShareFinalization.status() == DemoReadinessStatus.NEEDS_ATTENTION
                || finalAcceptanceCompletionCloseout.status() == DemoReadinessStatus.NEEDS_ATTENTION
                || finalAcceptanceCompletionCloseoutArchiveEvidence.status() == DemoReadinessStatus.NEEDS_ATTENTION
                || launchAcceptanceCloseoutEvidence.status() == DemoReadinessStatus.NEEDS_ATTENTION
                || launchAcceptanceCertificateEvidence.status() == DemoReadinessStatus.NEEDS_ATTENTION
                || taskEvidenceAcceptanceCertificateEvidence.status() == DemoReadinessStatus.NEEDS_ATTENTION
                || finalHandoffReportPackageArchiveEvidence.status() == DemoReadinessStatus.NEEDS_ATTENTION
                || finalExternalReviewEvidencePackage.status() == DemoReadinessStatus.NEEDS_ATTENTION
                || finalExternalReviewEvidencePackageArchiveEvidence.status() == DemoReadinessStatus.NEEDS_ATTENTION
                || finalExternalReviewEvidencePackageDeliveryReceiptEvidence.status()
                == DemoReadinessStatus.NEEDS_ATTENTION
                || finalExternalReviewEvidencePackageDeliveryFinalization.status()
                == DemoReadinessStatus.NEEDS_ATTENTION
                || finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence.status()
                == DemoReadinessStatus.NEEDS_ATTENTION) {
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
            DemoFinalAcceptanceShareFinalizationVo finalAcceptanceShareFinalization,
            DemoFinalAcceptanceCompletionCloseoutVo finalAcceptanceCompletionCloseout,
            DemoFinalAcceptanceCompletionCloseoutArchiveEvidenceVo finalAcceptanceCompletionCloseoutArchiveEvidence,
            DemoLaunchAcceptanceCloseoutEvidenceVo launchAcceptanceCloseoutEvidence,
            DemoLaunchAcceptanceCertificateEvidenceVo launchAcceptanceCertificateEvidence,
            DemoTaskEvidenceAcceptanceCertificateEvidenceVo taskEvidenceAcceptanceCertificateEvidence,
            DemoFinalHandoffReportPackageArchiveEvidenceVo finalHandoffReportPackageArchiveEvidence,
            DemoFinalExternalReviewEvidencePackageVo finalExternalReviewEvidencePackage,
            DemoFinalExternalReviewEvidencePackageArchiveEvidenceVo finalExternalReviewEvidencePackageArchiveEvidence,
            DemoFinalExternalReviewEvidencePackageDeliveryReceiptEvidenceVo
                    finalExternalReviewEvidencePackageDeliveryReceiptEvidence,
            DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo
                    finalExternalReviewEvidencePackageDeliveryFinalization,
            DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidenceVo
                    finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence
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
        if (finalAcceptanceShareFinalization.status() != DemoReadinessStatus.READY) {
            actions.add(finalAcceptanceShareFinalization.nextAction());
        }
        if (finalAcceptanceCompletionCloseout.status() != DemoReadinessStatus.READY) {
            actions.add(finalAcceptanceCompletionCloseout.nextAction());
        }
        if (finalAcceptanceCompletionCloseoutArchiveEvidence.status() != DemoReadinessStatus.READY) {
            actions.add(finalAcceptanceCompletionCloseoutArchiveEvidence.nextAction());
        }
        if (launchAcceptanceCloseoutEvidence.status() != DemoReadinessStatus.READY) {
            actions.add(launchAcceptanceCloseoutEvidence.nextAction());
        }
        if (launchAcceptanceCertificateEvidence.status() != DemoReadinessStatus.READY) {
            actions.add(launchAcceptanceCertificateEvidence.nextAction());
        }
        if (taskEvidenceAcceptanceCertificateEvidence.status() != DemoReadinessStatus.READY) {
            actions.add(taskEvidenceAcceptanceCertificateEvidence.nextAction());
        }
        if (finalHandoffReportPackageArchiveEvidence.status() != DemoReadinessStatus.READY) {
            actions.add(finalHandoffReportPackageArchiveEvidence.nextAction());
        }
        if (finalExternalReviewEvidencePackage.status() != DemoReadinessStatus.READY) {
            actions.add(finalExternalReviewEvidencePackage.nextAction());
        }
        if (finalExternalReviewEvidencePackageArchiveEvidence.status() != DemoReadinessStatus.READY) {
            actions.add(finalExternalReviewEvidencePackageArchiveEvidence.nextAction());
        }
        if (finalExternalReviewEvidencePackageDeliveryReceiptEvidence.status() != DemoReadinessStatus.READY) {
            actions.add(finalExternalReviewEvidencePackageDeliveryReceiptEvidence.nextAction());
        }
        if (finalExternalReviewEvidencePackageDeliveryFinalization.status() != DemoReadinessStatus.READY) {
            actions.add(finalExternalReviewEvidencePackageDeliveryFinalization.nextAction());
        }
        if (finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence.status()
                != DemoReadinessStatus.READY) {
            actions.add(finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence.nextAction());
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
