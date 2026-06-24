package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoReadinessCheckVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoScriptStepVo;
import io.patchpilot.backend.demo.domain.DemoScriptVo;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistStatus;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistStepVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;

@Service
public class DemoScriptService {

    private final Supplier<DemoEvidenceBundleVo> bundleSupplier;

    @Autowired
    public DemoScriptService(DemoEvidenceBundleService demoEvidenceBundleService) {
        this(demoEvidenceBundleService::getEvidenceBundle);
    }

    DemoScriptService(Supplier<DemoEvidenceBundleVo> bundleSupplier) {
        this.bundleSupplier = bundleSupplier;
    }

    public DemoScriptVo getScript() {
        DemoEvidenceBundleVo bundle = bundleSupplier.get();
        List<DemoScriptStepVo> steps = List.of(
                backendAccessStep(bundle),
                configurationStep(bundle),
                repositorySupportStep(bundle),
                triggerStep(bundle),
                taskExecutionStep(bundle),
                pullRequestStep(bundle)
        );
        return new DemoScriptVo(
                bundle.status(),
                summary(bundle.status()),
                steps,
                healthContract(),
                bundle.nextActions(),
                Instant.now()
        );
    }

    private static DemoScriptStepVo backendAccessStep(DemoEvidenceBundleVo bundle) {
        DemoReadinessCheckVo backendCheck = readinessCheck(bundle, "Backend");
        return new DemoScriptStepVo(
                1,
                "Confirm backend and dashboard access",
                backendCheck == null ? bundle.status() : backendCheck.status(),
                "Open the dashboard and confirm protected APIs load.",
                "curl http://127.0.0.1:8080/health",
                "Backend reports UP and dashboard data loads.",
                "Connectivity panel",
                backendCheck == null ? bundle.summary() : backendCheck.message()
        );
    }

    private static DemoScriptStepVo configurationStep(DemoEvidenceBundleVo bundle) {
        DemoReadinessStatus status = worstStatus(readinessCheck(bundle, "Credentials"), readinessCheck(bundle, "Safety policy"));
        return new DemoScriptStepVo(
                2,
                "Confirm configuration and safety posture",
                status,
                "Review credentials, admin token, allowlists, rate limits, quarantine, and generated-diff safety policy.",
                "curl ${ADMIN_HEADER[@]} http://127.0.0.1:8080/api/configuration/summary",
                "Required credentials and safety controls are configured for a private demo.",
                "Configuration panel",
                "Readiness: " + bundle.readiness().summary()
        );
    }

    private static DemoScriptStepVo repositorySupportStep(DemoEvidenceBundleVo bundle) {
        DemoReadinessCheckVo adapterCheck = readinessCheck(bundle, "Adapter fixtures");
        DemoReadinessCheckVo preflightCheck = readinessCheck(bundle, "Repository preflight scope");
        return new DemoScriptStepVo(
                3,
                "Verify repository support",
                worstStatus(adapterCheck, preflightCheck),
                "Confirm adapter fixtures and local repository preflight before using a controlled target repository.",
                "curl ${ADMIN_HEADER[@]} http://127.0.0.1:8080/api/language-adapters/fixtures",
                "Demo fixture paths detect the expected adapter and verification command.",
                "Supported adapters and repository preflight panels",
                bundle.adapterFixtures().totalCount() + " adapter fixtures, " + bundle.adapterFixtures().failedCount() + " failed"
        );
    }

    private static DemoScriptStepVo triggerStep(DemoEvidenceBundleVo bundle) {
        DemoSmokeChecklistStepVo webhookStep = smokeStep(bundle, "Webhook delivery");
        return new DemoScriptStepVo(
                4,
                "Create controlled /agent fix trigger",
                webhookStep == null ? bundle.status() : toReadinessStatus(webhookStep.status()),
                "Post `/agent fix replace docs/demo.md PatchPilot smoke test` on the controlled GitHub issue.",
                "curl ${ADMIN_HEADER[@]} http://127.0.0.1:8080/api/github/webhook-deliveries?limit=10",
                "Latest webhook delivery reaches PatchPilot and creates a task.",
                "Webhook delivery panel",
                webhookStep == null ? "No webhook evidence" : webhookStep.evidence()
        );
    }

    private static DemoScriptStepVo taskExecutionStep(DemoEvidenceBundleVo bundle) {
        DemoSmokeChecklistStepVo executionStep = smokeStep(bundle, "Task execution");
        return new DemoScriptStepVo(
                5,
                "Track task execution",
                executionStep == null ? bundle.status() : toReadinessStatus(executionStep.status()),
                "Watch the queued task move through execution, verification, commit, push, and PR creation.",
                "curl ${ADMIN_HEADER[@]} http://127.0.0.1:8080/api/tasks?limit=20",
                "A recent task completes with the selected adapter verification command.",
                "Task detail and queue panels",
                bundle.recentTask() == null ? "No recent task evidence" : bundle.recentTask().id()
        );
    }

    private static DemoScriptStepVo pullRequestStep(DemoEvidenceBundleVo bundle) {
        DemoSmokeChecklistStepVo pullRequestStep = smokeStep(bundle, "Pull Request evidence");
        return new DemoScriptStepVo(
                6,
                "Review Pull Request and export evidence",
                pullRequestStep == null ? bundle.status() : toReadinessStatus(pullRequestStep.status()),
                "Open the generated Pull Request, copy the task report, and copy the demo runbook for handoff.",
                "curl ${ADMIN_HEADER[@]} http://127.0.0.1:8080/api/demo/runbook",
                "Recent completed task has a Pull Request URL and copyable evidence.",
                "Demo evidence bundle and task detail panels",
                valueOrFallback(bundle.recentPullRequestUrl(), "No Pull Request evidence")
        );
    }

    private static String summary(DemoReadinessStatus status) {
        return switch (status) {
            case READY -> "Demo script is ready.";
            case NEEDS_ATTENTION -> "Demo script needs attention before use.";
            case BLOCKED -> "Demo script is blocked.";
        };
    }

    private static List<String> healthContract() {
        return List.of(
                "GET /api/demo/script is read-only: it does not create tasks, call the model, run tests, mutate Git, or write to GitHub.",
                "Live execution still starts from a controlled GitHub issue comment or manual task creation.",
                "Every script step points to an existing API or dashboard panel for verification and troubleshooting."
        );
    }

    private static DemoReadinessCheckVo readinessCheck(DemoEvidenceBundleVo bundle, String name) {
        return bundle.readiness().checks().stream()
                .filter(check -> name.equals(check.name()))
                .findFirst()
                .orElse(null);
    }

    private static DemoSmokeChecklistStepVo smokeStep(DemoEvidenceBundleVo bundle, String name) {
        return bundle.smokeChecklist().steps().stream()
                .filter(step -> name.equals(step.name()))
                .findFirst()
                .orElse(null);
    }

    private static DemoReadinessStatus worstStatus(DemoReadinessCheckVo first, DemoReadinessCheckVo second) {
        DemoReadinessStatus firstStatus = first == null ? DemoReadinessStatus.READY : first.status();
        DemoReadinessStatus secondStatus = second == null ? DemoReadinessStatus.READY : second.status();
        if (firstStatus == DemoReadinessStatus.BLOCKED || secondStatus == DemoReadinessStatus.BLOCKED) {
            return DemoReadinessStatus.BLOCKED;
        }
        if (firstStatus == DemoReadinessStatus.NEEDS_ATTENTION || secondStatus == DemoReadinessStatus.NEEDS_ATTENTION) {
            return DemoReadinessStatus.NEEDS_ATTENTION;
        }
        return DemoReadinessStatus.READY;
    }

    private static DemoReadinessStatus toReadinessStatus(DemoSmokeChecklistStatus status) {
        return switch (status) {
            case READY -> DemoReadinessStatus.READY;
            case NEEDS_ATTENTION -> DemoReadinessStatus.NEEDS_ATTENTION;
            case BLOCKED -> DemoReadinessStatus.BLOCKED;
        };
    }

    private static String valueOrFallback(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
