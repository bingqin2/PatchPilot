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

    private static String signed(int value) {
        return value > 0 ? "+" + value : String.valueOf(value);
    }

    private static String csv(List<String> values) {
        return values.isEmpty() ? "none" : String.join(", ", values);
    }
}
