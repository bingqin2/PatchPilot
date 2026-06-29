package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoEvaluationRunReadinessEvidenceVo;
import io.patchpilot.backend.demo.domain.DemoReadinessCheckVo;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistStepVo;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticVo;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

@Service
public class DemoRunbookService {

    private final Supplier<DemoEvidenceBundleVo> bundleSupplier;

    @Autowired
    public DemoRunbookService(DemoEvidenceBundleService demoEvidenceBundleService) {
        this(demoEvidenceBundleService::getEvidenceBundle);
    }

    DemoRunbookService(Supplier<DemoEvidenceBundleVo> bundleSupplier) {
        this.bundleSupplier = bundleSupplier;
    }

    public String getRunbook() {
        DemoEvidenceBundleVo bundle = bundleSupplier.get();
        StringBuilder runbook = new StringBuilder()
                .append("# PatchPilot Demo Runbook\n\n")
                .append("- Status: `").append(bundle.status()).append("`\n")
                .append("- Summary: ").append(bundle.summary()).append("\n")
                .append("- Generated at: `").append(bundle.generatedAt()).append("`\n")
                .append("- Recent Pull Request: ").append(valueOrNone(bundle.recentPullRequestUrl())).append("\n");

        appendEvidence(runbook, bundle);
        appendReadiness(runbook, bundle.readiness().checks());
        appendSmokeChecklist(runbook, bundle.smokeChecklist().steps());
        appendNextActions(runbook, bundle.nextActions());
        return runbook.toString();
    }

    private static void appendEvidence(StringBuilder runbook, DemoEvidenceBundleVo bundle) {
        FixTaskVo recentTask = bundle.recentTask();
        WebhookDeliveryDiagnosticVo latestDelivery = bundle.latestWebhookDelivery();
        FixTaskQueueSummaryVo queue = bundle.queueSummary();
        RejectedTriggerAuditSummaryVo rejectedTriggers = bundle.rejectedTriggerSummary();

        runbook.append("\n## Evidence Snapshot\n\n")
                .append("- Recent task: ");
        if (recentTask == null) {
            runbook.append("none\n");
        } else {
            runbook.append("`").append(recentTask.id()).append("` (`").append(recentTask.status()).append("`)\n");
        }

        runbook.append("- Latest webhook delivery: ");
        if (latestDelivery == null) {
            runbook.append("none\n");
        } else {
            runbook.append("`").append(latestDelivery.deliveryId()).append("` (`").append(latestDelivery.status()).append("`)\n");
        }

        runbook.append("- Adapter fixtures: ")
                .append(bundle.adapterFixtures().totalCount())
                .append(" total, ")
                .append(bundle.adapterFixtures().failedCount())
                .append(" failed\n");

        appendEvaluationRunReadiness(runbook, bundle.evaluationRunReadiness());

        runbook.append("- Queue: ")
                .append(queue.totalCount())
                .append(" total, ")
                .append(queue.pendingCount())
                .append(" pending, ")
                .append(queue.completedCount())
                .append(" completed, ")
                .append(queue.failedCount())
                .append(" failed\n")
                .append("- Rejected triggers: ")
                .append(rejectedTriggers == null ? 0 : rejectedTriggers.totalCount())
                .append(" recent\n")
                .append("- Active quarantines: ")
                .append(bundle.activeQuarantineCount())
                .append("\n");

        appendLaunchEvidenceFinalization(runbook, bundle);
    }

    private static void appendLaunchEvidenceFinalization(StringBuilder runbook, DemoEvidenceBundleVo bundle) {
        runbook.append("- Launch evidence share center: `")
                .append(bundle.launchEvidenceShareCenterStatus())
                .append("` - ")
                .append(bundle.launchEvidenceShareCenterSummary())
                .append("\n")
                .append("- Launch evidence latest archive: ")
                .append(valueOrNoneBackticked(bundle.launchEvidenceShareCenterLatestArchiveId()))
                .append("\n")
                .append("- Launch evidence latest session: ")
                .append(valueOrNoneBackticked(bundle.launchEvidenceShareCenterLatestSessionId()))
                .append("\n")
                .append("- Launch evidence finalization: `")
                .append(bundle.launchEvidenceFinalizationStatus())
                .append("` - ")
                .append(bundle.launchEvidenceFinalizationSummary())
                .append("\n")
                .append("- Launch evidence accepted receipt: ")
                .append(valueOrNoneBackticked(bundle.launchEvidenceFinalizationLatestDeliveryReceiptId()))
                .append("\n")
                .append("- Launch evidence receipt freshness: `")
                .append(bundle.launchEvidenceFinalizationDeliveryReceiptFreshness())
                .append("`\n")
                .append("- Launch evidence finalization next action: ")
                .append(bundle.launchEvidenceFinalizationNextAction())
                .append("\n");

        runbook.append("- Launch acceptance closeout: `")
                .append(bundle.launchAcceptanceCloseoutEvidence().status())
                .append("` - ")
                .append(bundle.launchAcceptanceCloseoutEvidence().summary())
                .append("\n")
                .append("- Launch acceptance closeout archive: ")
                .append(valueOrNoneBackticked(bundle.launchAcceptanceCloseoutEvidence().latestArchiveId()))
                .append("\n")
                .append("- Launch acceptance closeout evidence archive: ")
                .append(valueOrNoneBackticked(bundle.launchAcceptanceCloseoutEvidence().latestEvidenceArchiveId()))
                .append("\n")
                .append("- Launch acceptance closeout receipt: ")
                .append(valueOrNoneBackticked(bundle.launchAcceptanceCloseoutEvidence().latestDeliveryReceiptId()))
                .append("\n")
                .append("- Launch acceptance closeout next action: ")
                .append(bundle.launchAcceptanceCloseoutEvidence().nextAction())
                .append("\n");
        bundle.launchAcceptanceCloseoutEvidence().downloadActions()
                .forEach(action -> runbook
                        .append("- Launch acceptance closeout download: ")
                        .append(action)
                        .append("\n"));

        runbook.append("- Launch acceptance certificate: `")
                .append(bundle.launchAcceptanceCertificateEvidence().status())
                .append("` - ")
                .append(bundle.launchAcceptanceCertificateEvidence().summary())
                .append("\n")
                .append("- Launch acceptance certificate archive: ")
                .append(valueOrNoneBackticked(bundle.launchAcceptanceCertificateEvidence().latestArchiveId()))
                .append("\n")
                .append("- Launch acceptance certificate closeout archive: ")
                .append(valueOrNoneBackticked(bundle.launchAcceptanceCertificateEvidence().latestCloseoutArchiveId()))
                .append("\n")
                .append("- Launch acceptance certificate evidence archive: ")
                .append(valueOrNoneBackticked(bundle.launchAcceptanceCertificateEvidence().latestEvidenceArchiveId()))
                .append("\n")
                .append("- Launch acceptance certificate receipt: ")
                .append(valueOrNoneBackticked(bundle.launchAcceptanceCertificateEvidence().latestDeliveryReceiptId()))
                .append("\n")
                .append("- Launch acceptance certificate Pull Request: ")
                .append(valueOrNone(bundle.launchAcceptanceCertificateEvidence().latestPullRequestUrl()))
                .append("\n")
                .append("- Launch acceptance certificate next action: ")
                .append(bundle.launchAcceptanceCertificateEvidence().nextAction())
                .append("\n");
        bundle.launchAcceptanceCertificateEvidence().downloadActions()
                .forEach(action -> runbook
                        .append("- Launch acceptance certificate download: ")
                        .append(action)
                        .append("\n"));

        runbook.append("- Task evidence acceptance certificate: `")
                .append(bundle.taskEvidenceAcceptanceCertificateEvidence().status())
                .append("` - ")
                .append(bundle.taskEvidenceAcceptanceCertificateEvidence().summary())
                .append("\n")
                .append("- Task evidence acceptance certificate archive: ")
                .append(valueOrNoneBackticked(bundle.taskEvidenceAcceptanceCertificateEvidence().latestArchiveId()))
                .append("\n")
                .append("- Task evidence acceptance certificate closeout archive: ")
                .append(valueOrNoneBackticked(bundle.taskEvidenceAcceptanceCertificateEvidence().latestCloseoutArchiveId()))
                .append("\n")
                .append("- Task evidence acceptance certificate evidence archive: ")
                .append(valueOrNoneBackticked(bundle.taskEvidenceAcceptanceCertificateEvidence().latestEvidenceArchiveId()))
                .append("\n")
                .append("- Task evidence acceptance certificate receipt: ")
                .append(valueOrNoneBackticked(bundle.taskEvidenceAcceptanceCertificateEvidence().latestDeliveryReceiptId()))
                .append("\n")
                .append("- Task evidence acceptance certificate task: ")
                .append(valueOrNoneBackticked(bundle.taskEvidenceAcceptanceCertificateEvidence().latestTaskId()))
                .append("\n")
                .append("- Task evidence acceptance certificate Pull Request: ")
                .append(valueOrNone(bundle.taskEvidenceAcceptanceCertificateEvidence().latestPullRequestUrl()))
                .append("\n")
                .append("- Task evidence acceptance certificate next action: ")
                .append(bundle.taskEvidenceAcceptanceCertificateEvidence().nextAction())
                .append("\n");
        bundle.taskEvidenceAcceptanceCertificateEvidence().downloadActions()
                .forEach(action -> runbook
                        .append("- Task evidence acceptance certificate download: ")
                        .append(action)
                        .append("\n"));

        runbook.append("- Final handoff report package archive: `")
                .append(bundle.finalHandoffReportPackageArchiveEvidence().status())
                .append("` - ")
                .append(bundle.finalHandoffReportPackageArchiveEvidence().summary())
                .append("\n")
                .append("- Final handoff report package archive id: ")
                .append(valueOrNoneBackticked(bundle.finalHandoffReportPackageArchiveEvidence().latestArchiveId()))
                .append("\n")
                .append("- Final handoff report package linked handoff archive: ")
                .append(valueOrNoneBackticked(bundle.finalHandoffReportPackageArchiveEvidence().latestHandoffArchiveId()))
                .append("\n")
                .append("- Final handoff report package session: ")
                .append(valueOrNoneBackticked(bundle.finalHandoffReportPackageArchiveEvidence().latestSessionId()))
                .append("\n")
                .append("- Final handoff report package receipt: ")
                .append(valueOrNoneBackticked(bundle.finalHandoffReportPackageArchiveEvidence().latestDeliveryReceiptId()))
                .append("\n")
                .append("- Final handoff report package task certificate: ")
                .append(valueOrNoneBackticked(bundle.finalHandoffReportPackageArchiveEvidence().taskCertificateArchiveId()))
                .append("\n")
                .append("- Final handoff report package next action: ")
                .append(bundle.finalHandoffReportPackageArchiveEvidence().nextAction())
                .append("\n");
        bundle.finalHandoffReportPackageArchiveEvidence().downloadActions()
                .forEach(action -> runbook
                        .append("- Final handoff report package download: ")
                        .append(action)
                        .append("\n"));

        runbook.append("- Final acceptance share finalization: `")
                .append(bundle.finalAcceptanceShareFinalization().status())
                .append("` - ")
                .append(bundle.finalAcceptanceShareFinalization().summary())
                .append("\n")
                .append("- Final acceptance share archive: ")
                .append(valueOrNoneBackticked(bundle.finalAcceptanceShareFinalization().latestArchiveId()))
                .append("\n")
                .append("- Final acceptance share task: ")
                .append(valueOrNoneBackticked(bundle.finalAcceptanceShareFinalization().latestTaskId()))
                .append("\n")
                .append("- Final acceptance delivery receipt: ")
                .append(valueOrNoneBackticked(bundle.finalAcceptanceShareFinalization().latestDeliveryReceiptId()))
                .append("\n")
                .append("- Final acceptance delivery target: ")
                .append(finalAcceptanceDeliveryTarget(bundle))
                .append("\n")
                .append("- Final acceptance receipt freshness: `")
                .append(bundle.finalAcceptanceShareFinalization().deliveryReceiptFreshness())
                .append("`\n")
                .append("- Final acceptance finalization next action: ")
                .append(bundle.finalAcceptanceShareFinalization().nextAction())
                .append("\n");

        runbook.append("- Final acceptance completion closeout: `")
                .append(bundle.finalAcceptanceCompletionCloseoutEvidence().status())
                .append("` - ")
                .append(bundle.finalAcceptanceCompletionCloseoutEvidence().summary())
                .append("\n")
                .append("- Final acceptance completion closed: `")
                .append(bundle.finalAcceptanceCompletionCloseoutEvidence().closed())
                .append("`\n")
                .append("- Final acceptance completion archive: ")
                .append(valueOrNoneBackticked(bundle.finalAcceptanceCompletionCloseoutEvidence().latestCompletionArchiveId()))
                .append("\n")
                .append("- Final acceptance completion receipt: ")
                .append(valueOrNoneBackticked(bundle.finalAcceptanceCompletionCloseoutEvidence().latestCompletionEvidenceDeliveryReceiptId()))
                .append("\n")
                .append("- Final acceptance completion task: ")
                .append(valueOrNoneBackticked(bundle.finalAcceptanceCompletionCloseoutEvidence().latestTaskId()))
                .append("\n")
                .append("- Final acceptance completion Pull Request: ")
                .append(valueOrNone(bundle.finalAcceptanceCompletionCloseoutEvidence().latestPullRequestUrl()))
                .append("\n")
                .append("- Final acceptance completion delivery target: ")
                .append(finalAcceptanceCompletionDeliveryTarget(bundle))
                .append("\n")
                .append("- Final acceptance completion receipt freshness: `")
                .append(bundle.finalAcceptanceCompletionCloseoutEvidence().deliveryReceiptFreshness())
                .append("`\n")
                .append("- Final acceptance completion closeout next action: ")
                .append(bundle.finalAcceptanceCompletionCloseoutEvidence().nextAction())
                .append("\n");
        bundle.finalAcceptanceCompletionCloseoutEvidence().downloadActions()
                .forEach(action -> runbook
                        .append("- Final acceptance completion closeout download: ")
                        .append(action)
                        .append("\n"));

        runbook.append("- Final acceptance completion closeout archive: `")
                .append(bundle.finalAcceptanceCompletionCloseoutArchiveEvidence().status())
                .append("` - ")
                .append(bundle.finalAcceptanceCompletionCloseoutArchiveEvidence().summary())
                .append("\n")
                .append("- Final acceptance completion closeout archive id: ")
                .append(valueOrNoneBackticked(bundle.finalAcceptanceCompletionCloseoutArchiveEvidence().latestArchiveId()))
                .append("\n")
                .append("- Final acceptance completion closeout archived completion: ")
                .append(valueOrNoneBackticked(bundle.finalAcceptanceCompletionCloseoutArchiveEvidence().latestCompletionArchiveId()))
                .append("\n")
                .append("- Final acceptance completion closeout archived receipt: ")
                .append(valueOrNoneBackticked(bundle.finalAcceptanceCompletionCloseoutArchiveEvidence().latestCompletionEvidenceDeliveryReceiptId()))
                .append("\n")
                .append("- Final acceptance completion closeout archived task: ")
                .append(valueOrNoneBackticked(bundle.finalAcceptanceCompletionCloseoutArchiveEvidence().latestTaskId()))
                .append("\n")
                .append("- Final acceptance completion closeout archive Pull Request: ")
                .append(valueOrNone(bundle.finalAcceptanceCompletionCloseoutArchiveEvidence().latestPullRequestUrl()))
                .append("\n")
                .append("- Final acceptance completion closeout archive next action: ")
                .append(bundle.finalAcceptanceCompletionCloseoutArchiveEvidence().nextAction())
                .append("\n");
        bundle.finalAcceptanceCompletionCloseoutArchiveEvidence().downloadActions()
                .forEach(action -> runbook
                        .append("- Final acceptance completion closeout archive download: ")
                        .append(action)
                        .append("\n"));

        runbook.append("- Final external-review evidence package: `")
                .append(bundle.finalExternalReviewEvidencePackage().status())
                .append("` - ")
                .append(bundle.finalExternalReviewEvidencePackage().summary())
                .append("\n")
                .append("- Final external-review ready: `")
                .append(bundle.finalExternalReviewEvidencePackage().readyForExternalReview())
                .append("`\n")
                .append("- Final external-review closeout archive: ")
                .append(valueOrNoneBackticked(bundle.finalExternalReviewEvidencePackage().closeoutArchiveId()))
                .append("\n")
                .append("- Final external-review completion archive: ")
                .append(valueOrNoneBackticked(bundle.finalExternalReviewEvidencePackage().completionArchiveId()))
                .append("\n")
                .append("- Final external-review completion receipt: ")
                .append(valueOrNoneBackticked(bundle.finalExternalReviewEvidencePackage().completionEvidenceDeliveryReceiptId()))
                .append("\n")
                .append("- Final external-review task: ")
                .append(valueOrNoneBackticked(bundle.finalExternalReviewEvidencePackage().latestTaskId()))
                .append("\n")
                .append("- Final external-review Pull Request: ")
                .append(valueOrNone(bundle.finalExternalReviewEvidencePackage().latestPullRequestUrl()))
                .append("\n")
                .append("- Final external-review delivery target: ")
                .append(finalExternalReviewDeliveryTarget(bundle))
                .append("\n")
                .append("- Final external-review receipt freshness: `")
                .append(bundle.finalExternalReviewEvidencePackage().deliveryReceiptFreshness())
                .append("`\n")
                .append("- Final external-review package next action: ")
                .append(bundle.finalExternalReviewEvidencePackage().nextAction())
                .append("\n");
        bundle.finalExternalReviewEvidencePackage().downloadActions()
                .forEach(action -> runbook
                        .append("- Final external-review package download: ")
                        .append(action)
                        .append("\n"));

        runbook.append("- Final external-review package archive: `")
                .append(bundle.finalExternalReviewEvidencePackageArchiveEvidence().status())
                .append("` - ")
                .append(bundle.finalExternalReviewEvidencePackageArchiveEvidence().summary())
                .append("\n")
                .append("- Final external-review package archive id: ")
                .append(valueOrNoneBackticked(bundle.finalExternalReviewEvidencePackageArchiveEvidence().latestArchiveId()))
                .append("\n")
                .append("- Final external-review archived closeout: ")
                .append(valueOrNoneBackticked(bundle.finalExternalReviewEvidencePackageArchiveEvidence().latestCloseoutArchiveId()))
                .append("\n")
                .append("- Final external-review archived completion: ")
                .append(valueOrNoneBackticked(bundle.finalExternalReviewEvidencePackageArchiveEvidence().latestCompletionArchiveId()))
                .append("\n")
                .append("- Final external-review archived completion receipt: ")
                .append(valueOrNoneBackticked(bundle.finalExternalReviewEvidencePackageArchiveEvidence()
                        .latestCompletionEvidenceDeliveryReceiptId()))
                .append("\n")
                .append("- Final external-review archived task: ")
                .append(valueOrNoneBackticked(bundle.finalExternalReviewEvidencePackageArchiveEvidence().latestTaskId()))
                .append("\n")
                .append("- Final external-review package archive Pull Request: ")
                .append(valueOrNone(bundle.finalExternalReviewEvidencePackageArchiveEvidence().latestPullRequestUrl()))
                .append("\n")
                .append("- Final external-review package archive next action: ")
                .append(bundle.finalExternalReviewEvidencePackageArchiveEvidence().nextAction())
                .append("\n");
        bundle.finalExternalReviewEvidencePackageArchiveEvidence().downloadActions()
                .forEach(action -> runbook
                        .append("- Final external-review package archive download: ")
                        .append(action)
                        .append("\n"));

        runbook.append("- Final external-review package delivery receipt: `")
                .append(bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence().status())
                .append("` - ")
                .append(bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence().summary())
                .append("\n")
                .append("- Final external-review package delivery receipt id: ")
                .append(valueOrNoneBackticked(bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence()
                        .latestReceiptId()))
                .append("\n")
                .append("- Final external-review delivered package archive: ")
                .append(valueOrNoneBackticked(bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence()
                        .latestPackageArchiveId()))
                .append("\n")
                .append("- Final external-review package delivery target: ")
                .append(finalExternalReviewPackageDeliveryTarget(bundle))
                .append("\n")
                .append("- Final external-review package delivered at: ")
                .append(bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence().latestDeliveredAt() == null
                        ? "none"
                        : "`" + bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence()
                                .latestDeliveredAt() + "`")
                .append("\n")
                .append("- Final external-review package delivery next action: ")
                .append(bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence().nextAction())
                .append("\n");
        bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence().downloadActions()
                .forEach(action -> runbook
                        .append("- Final external-review package delivery download: ")
                        .append(action)
                        .append("\n"));

        runbook.append("- Final external-review package delivery finalization: `")
                .append(bundle.finalExternalReviewEvidencePackageDeliveryFinalization().status())
                .append("` - ")
                .append(bundle.finalExternalReviewEvidencePackageDeliveryFinalization().summary())
                .append("\n")
                .append("- Final external-review package delivery finalized: `")
                .append(bundle.finalExternalReviewEvidencePackageDeliveryFinalization().finalized())
                .append("`\n")
                .append("- Final external-review finalized package archive: ")
                .append(valueOrNoneBackticked(bundle.finalExternalReviewEvidencePackageDeliveryFinalization()
                        .latestArchiveId()))
                .append("\n")
                .append("- Final external-review finalized delivery receipt: ")
                .append(valueOrNoneBackticked(bundle.finalExternalReviewEvidencePackageDeliveryFinalization()
                        .latestDeliveryReceiptId()))
                .append("\n")
                .append("- Final external-review finalization receipt freshness: `")
                .append(bundle.finalExternalReviewEvidencePackageDeliveryFinalization().deliveryReceiptFreshness())
                .append("`\n")
                .append("- Final external-review package delivery finalization next action: ")
                .append(bundle.finalExternalReviewEvidencePackageDeliveryFinalization().nextAction())
                .append("\n");
        bundle.finalExternalReviewEvidencePackageDeliveryFinalization().downloadActions()
                .forEach(action -> runbook
                        .append("- Final external-review package delivery finalization download: ")
                        .append(action)
                        .append("\n"));

        runbook.append("- Final external-review package delivery finalization archive: `")
                .append(bundle.finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence().status())
                .append("` - ")
                .append(bundle.finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence().summary())
                .append("\n")
                .append("- Final external-review package delivery finalization archive id: ")
                .append(valueOrNoneBackticked(bundle.finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence()
                        .latestArchiveId()))
                .append("\n")
                .append("- Final external-review archived finalization package: ")
                .append(valueOrNoneBackticked(bundle.finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence()
                        .latestPackageArchiveId()))
                .append("\n")
                .append("- Final external-review archived finalization receipt: ")
                .append(valueOrNoneBackticked(bundle.finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence()
                        .latestDeliveryReceiptId()))
                .append("\n")
                .append("- Final external-review archived finalization task: ")
                .append(valueOrNoneBackticked(bundle.finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence()
                        .latestTaskId()))
                .append("\n")
                .append("- Final external-review package delivery finalization archive Pull Request: ")
                .append(valueOrNone(bundle.finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence()
                        .latestPullRequestUrl()))
                .append("\n")
                .append("- Final external-review package delivery finalization archived at: ")
                .append(bundle.finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence().latestArchivedAt() == null
                        ? "none"
                        : "`" + bundle.finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence()
                                .latestArchivedAt() + "`")
                .append("\n")
                .append("- Final external-review package delivery finalization archive next action: ")
                .append(bundle.finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence().nextAction())
                .append("\n");
        bundle.finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence().downloadActions()
                .forEach(action -> runbook
                        .append("- Final external-review package delivery finalization archive download: ")
                        .append(action)
                        .append("\n"));

        runbook.append("- Final external-review release bundle: `")
                .append(bundle.finalExternalReviewReleaseBundle().status())
                .append("` - ")
                .append(bundle.finalExternalReviewReleaseBundle().summary())
                .append("\n")
                .append("- Final external-review release ready: `")
                .append(bundle.finalExternalReviewReleaseBundle().releaseReady())
                .append("`\n")
                .append("- Final external-review release certificate archive: ")
                .append(valueOrNoneBackticked(bundle.finalExternalReviewReleaseBundle().latestCertificateArchiveId()))
                .append("\n")
                .append("- Final external-review release finalization archive: ")
                .append(valueOrNoneBackticked(bundle.finalExternalReviewReleaseBundle()
                        .latestDeliveryFinalizationArchiveId()))
                .append("\n")
                .append("- Final external-review release package archive: ")
                .append(valueOrNoneBackticked(bundle.finalExternalReviewReleaseBundle().latestPackageArchiveId()))
                .append("\n")
                .append("- Final external-review release delivery receipt: ")
                .append(valueOrNoneBackticked(bundle.finalExternalReviewReleaseBundle().latestDeliveryReceiptId()))
                .append("\n")
                .append("- Final external-review release task: ")
                .append(valueOrNoneBackticked(bundle.finalExternalReviewReleaseBundle().latestTaskId()))
                .append("\n")
                .append("- Final external-review release Pull Request: ")
                .append(valueOrNone(bundle.finalExternalReviewReleaseBundle().latestPullRequestUrl()))
                .append("\n")
                .append("- Final external-review release delivery target: ")
                .append(finalExternalReviewReleaseDeliveryTarget(bundle))
                .append("\n")
                .append("- Final external-review release delivered at: ")
                .append(valueOrNoneBackticked(bundle.finalExternalReviewReleaseBundle().latestDeliveredAt()))
                .append("\n")
                .append("- Final external-review release certificate archived at: ")
                .append(bundle.finalExternalReviewReleaseBundle().latestCertificateArchivedAt() == null
                        ? "none"
                        : "`" + bundle.finalExternalReviewReleaseBundle().latestCertificateArchivedAt() + "`")
                .append("\n")
                .append("- Final external-review release next action: ")
                .append(bundle.finalExternalReviewReleaseBundle().nextAction())
                .append("\n");
        bundle.finalExternalReviewReleaseBundle().requiredAttachments()
                .forEach(attachment -> runbook
                        .append("- Final external-review release attachment: ")
                        .append(attachment)
                        .append("\n"));
        bundle.finalExternalReviewReleaseBundle().evidenceNotes()
                .forEach(note -> runbook
                        .append("- Final external-review release evidence: ")
                        .append(note)
                        .append("\n"));
        bundle.finalExternalReviewReleaseBundle().downloadActions()
                .forEach(action -> runbook
                        .append("- Final external-review release download: ")
                        .append(action)
                        .append("\n"));
    }

    private static void appendEvaluationRunReadiness(
            StringBuilder runbook,
            DemoEvaluationRunReadinessEvidenceVo evaluationRunReadiness
    ) {
        runbook.append("- Full evaluation run readiness: `")
                .append(evaluationRunReadiness.status())
                .append("`\n")
                .append("- Latest evaluation run: ")
                .append(valueOrNoneBackticked(evaluationRunReadiness.latestRunId()))
                .append("\n")
                .append("- Previous evaluation run: ")
                .append(valueOrNoneBackticked(evaluationRunReadiness.previousRunId()))
                .append("\n")
                .append("- Evaluation deltas: passed ")
                .append(signed(evaluationRunReadiness.passedDelta()))
                .append(", failed ")
                .append(signed(evaluationRunReadiness.failedDelta()))
                .append(", skipped ")
                .append(signed(evaluationRunReadiness.skippedDelta()))
                .append("\n")
                .append("- Evaluation coverage: ")
                .append(csv(evaluationRunReadiness.coveredLanguages()))
                .append(" / ")
                .append(csv(evaluationRunReadiness.coveredBuildSystems()))
                .append("\n")
                .append("- Safety rejection categories: ")
                .append(csv(evaluationRunReadiness.safetyRejectionCategories()))
                .append("\n")
                .append("- Evaluation next action: ")
                .append(evaluationRunReadiness.nextAction())
                .append("\n");
    }

    private static void appendReadiness(StringBuilder runbook, List<DemoReadinessCheckVo> checks) {
        runbook.append("\n## Readiness\n\n");
        if (checks.isEmpty()) {
            runbook.append("- No readiness checks recorded.\n");
            return;
        }
        checks.forEach(check -> runbook
                .append("- `").append(check.name()).append("`: `")
                .append(check.status()).append("` - ")
                .append(check.message()).append("\n"));
    }

    private static void appendSmokeChecklist(StringBuilder runbook, List<DemoSmokeChecklistStepVo> steps) {
        runbook.append("\n## Smoke Checklist\n\n");
        if (steps.isEmpty()) {
            runbook.append("- No smoke checklist steps recorded.\n");
            return;
        }
        steps.forEach(step -> runbook
                .append("- ").append(step.order()).append(". `")
                .append(step.name()).append("`: `")
                .append(step.status()).append("` - ")
                .append(step.message()).append("\n"));
    }

    private static void appendNextActions(StringBuilder runbook, List<String> nextActions) {
        runbook.append("\n## Next Actions\n\n");
        if (nextActions.isEmpty()) {
            runbook.append("- No next actions recorded.\n");
            return;
        }
        nextActions.forEach(action -> runbook.append("- ").append(action).append("\n"));
    }

    private static String valueOrNone(String value) {
        return value == null || value.isBlank() ? "none" : value;
    }

    private static String valueOrNoneBackticked(String value) {
        return value == null || value.isBlank() ? "none" : "`" + value + "`";
    }

    private static String finalAcceptanceDeliveryTarget(DemoEvidenceBundleVo bundle) {
        String target = bundle.finalAcceptanceShareFinalization().latestDeliveryTarget();
        String channel = bundle.finalAcceptanceShareFinalization().latestDeliveryChannel();
        if (target == null || target.isBlank()) {
            return "none";
        }
        if (channel == null || channel.isBlank()) {
            return target;
        }
        return target + " via " + channel;
    }

    private static String finalAcceptanceCompletionDeliveryTarget(DemoEvidenceBundleVo bundle) {
        String target = bundle.finalAcceptanceCompletionCloseoutEvidence().latestDeliveryTarget();
        String channel = bundle.finalAcceptanceCompletionCloseoutEvidence().latestDeliveryChannel();
        if (target == null || target.isBlank()) {
            return "none";
        }
        if (channel == null || channel.isBlank()) {
            return target;
        }
        return target + " via " + channel;
    }

    private static String finalExternalReviewDeliveryTarget(DemoEvidenceBundleVo bundle) {
        String target = bundle.finalExternalReviewEvidencePackage().deliveryTarget();
        String channel = bundle.finalExternalReviewEvidencePackage().deliveryChannel();
        if (target == null || target.isBlank()) {
            return "none";
        }
        if (channel == null || channel.isBlank()) {
            return target;
        }
        return target + " via " + channel;
    }

    private static String finalExternalReviewPackageDeliveryTarget(DemoEvidenceBundleVo bundle) {
        String target = bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence().latestDeliveryTarget();
        String channel = bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence().latestDeliveryChannel();
        if (target == null || target.isBlank()) {
            return "none";
        }
        if (channel == null || channel.isBlank()) {
            return target;
        }
        return target + " via " + channel;
    }

    private static String finalExternalReviewReleaseDeliveryTarget(DemoEvidenceBundleVo bundle) {
        String target = bundle.finalExternalReviewReleaseBundle().latestDeliveryTarget();
        String channel = bundle.finalExternalReviewReleaseBundle().latestDeliveryChannel();
        if (target == null || target.isBlank()) {
            return "none";
        }
        if (channel == null || channel.isBlank()) {
            return target;
        }
        return target + " via " + channel;
    }

    private static String signed(int value) {
        return value > 0 ? "+" + value : String.valueOf(value);
    }

    private static String csv(List<String> values) {
        return values.isEmpty() ? "none" : String.join(", ", values);
    }
}
