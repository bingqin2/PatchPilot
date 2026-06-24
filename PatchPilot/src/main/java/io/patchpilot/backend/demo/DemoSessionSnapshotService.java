package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoSessionSnapshotVo;
import io.patchpilot.backend.demo.domain.DemoScriptVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
public class DemoSessionSnapshotService {

    private static final DateTimeFormatter SESSION_ID_FORMATTER = DateTimeFormatter
            .ofPattern("yyyyMMdd'T'HHmmss'Z'")
            .withZone(ZoneOffset.UTC);

    private final Supplier<DemoEvidenceBundleVo> bundleSupplier;
    private final Supplier<Instant> nowSupplier;

    @Autowired
    public DemoSessionSnapshotService(DemoEvidenceBundleService demoEvidenceBundleService) {
        this(demoEvidenceBundleService::getEvidenceBundle, Instant::now);
    }

    DemoSessionSnapshotService(Supplier<DemoEvidenceBundleVo> bundleSupplier, Supplier<Instant> nowSupplier) {
        this.bundleSupplier = bundleSupplier;
        this.nowSupplier = nowSupplier;
    }

    public DemoSessionSnapshotVo getSessionSnapshot() {
        Instant generatedAt = nowSupplier.get();
        DemoEvidenceBundleVo bundle = bundleSupplier.get();
        DemoScriptVo script = new DemoScriptService(() -> bundle, () -> generatedAt).getScript();
        String runbook = new DemoRunbookService(() -> bundle).getRunbook();

        return new DemoSessionSnapshotVo(
                "demo-session-" + SESSION_ID_FORMATTER.format(generatedAt),
                bundle.status(),
                summary(bundle.status()),
                generatedAt,
                bundle,
                script,
                runbook,
                operatorChecklist(bundle),
                healthContract(),
                shareSummary(bundle),
                bundle.nextActions()
        );
    }

    private static String summary(DemoReadinessStatus status) {
        return switch (status) {
            case READY -> "Demo session snapshot is ready.";
            case NEEDS_ATTENTION -> "Demo session snapshot needs attention before use.";
            case BLOCKED -> "Demo session snapshot is blocked.";
        };
    }

    private static List<String> operatorChecklist(DemoEvidenceBundleVo bundle) {
        List<String> checklist = new ArrayList<>();
        checklist.add("Open the dashboard and confirm the demo session snapshot status.");
        checklist.add("Verify the latest webhook delivery and recent task before posting a live trigger.");
        checklist.add("Copy the runbook after Pull Request evidence is visible.");
        if (bundle.recentPullRequestUrl() == null || bundle.recentPullRequestUrl().isBlank()) {
            checklist.add("Run one controlled issue-to-PR smoke task to produce Pull Request evidence.");
        }
        checklist.addAll(bundle.nextActions());
        return checklist;
    }

    private static List<String> healthContract() {
        return List.of(
                "GET /api/demo/session-snapshot is read-only: it does not create tasks, call the model, run tests, mutate Git, or write to GitHub.",
                "The snapshot only combines existing demo evidence, script, and runbook read models.",
                "Live execution still starts from a controlled GitHub issue comment or manual task creation."
        );
    }

    private static String shareSummary(DemoEvidenceBundleVo bundle) {
        String task = bundle.recentTask() == null ? "none" : bundle.recentTask().id();
        String pullRequest = valueOrNone(bundle.recentPullRequestUrl());
        String delivery = bundle.latestWebhookDelivery() == null ? "none" : bundle.latestWebhookDelivery().deliveryId();
        return "Status " + bundle.status()
                + "; recent task " + task
                + "; latest delivery " + delivery
                + "; recent PR " + pullRequest
                + ".";
    }

    private static String valueOrNone(String value) {
        return value == null || value.isBlank() ? "none" : value;
    }
}
