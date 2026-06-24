package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoEvidenceBundleVo;
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
                .append(" failed\n")
                .append("- Queue: ")
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
}
