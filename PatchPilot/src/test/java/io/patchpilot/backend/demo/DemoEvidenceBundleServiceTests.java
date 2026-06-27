package io.patchpilot.backend.demo;

import io.patchpilot.backend.configuration.ConfigurationSummaryVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveSummaryVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareCenterVo;
import io.patchpilot.backend.demo.domain.DemoReadinessCheckVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistStatus;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistStepVo;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistVo;
import io.patchpilot.backend.github.credential.domain.GitHubWebhookSetupReadinessVo;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticStatus;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticVo;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryOutcomeType;
import io.patchpilot.backend.language.domain.LanguageAdapterFixtureVerificationVo;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditSummaryVo;
import io.patchpilot.backend.safety.domain.RejectedTriggerCountVo;
import io.patchpilot.backend.safety.domain.TriggerQuarantineScope;
import io.patchpilot.backend.safety.domain.TriggerQuarantineVo;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoEvidenceBundleServiceTests {

    @Test
    void should_build_demo_evidence_bundle_from_existing_read_models() {
        DemoEvidenceBundleService service = new DemoEvidenceBundleService(
                () -> readiness(DemoReadinessStatus.READY, List.of()),
                () -> smokeChecklist(DemoSmokeChecklistStatus.READY, List.of()),
                DemoEvidenceBundleServiceTests::configuration,
                () -> List.of(fixture("java-maven", "PASS"), fixture("python-pytest", "FAIL")),
                () -> new FixTaskQueueSummaryVo(3, 1, 1, 0, 0, 2, 0, 0),
                () -> List.of(
                        task("task-2", FixTaskStatus.COMPLETED, "https://github.com/bingqin2/PatchPilot/pull/42"),
                        task("task-1", FixTaskStatus.FAILED, null)
                ),
                () -> List.of(
                        webhookDelivery("delivery-1", WebhookDeliveryDiagnosticStatus.TASK_CREATED, "task-2"),
                        webhookDelivery("delivery-2", WebhookDeliveryDiagnosticStatus.REJECTED, "rejected-1")
                ),
                DemoEvidenceBundleServiceTests::webhookSetupReadiness,
                () -> rejectedTriggerSummary(4),
                () -> List.of(quarantine("quarantine-1")),
                DemoEvidenceBundleServiceTests::handoffPackageArchiveSummary,
                DemoEvidenceBundleServiceTests::handoffShareCenter
        );

        DemoEvidenceBundleVo bundle = service.getEvidenceBundle();

        assertThat(bundle.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(bundle.summary()).isEqualTo("Demo evidence bundle needs attention.");
        assertThat(bundle.readiness().status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(bundle.smokeChecklist().status()).isEqualTo(DemoSmokeChecklistStatus.READY);
        assertThat(bundle.configuration().githubTokenConfigured()).isTrue();
        assertThat(bundle.adapterFixtures().totalCount()).isEqualTo(2);
        assertThat(bundle.adapterFixtures().failedCount()).isEqualTo(1);
        assertThat(bundle.queueSummary().totalCount()).isEqualTo(3);
        assertThat(bundle.recentTask().id()).isEqualTo("task-2");
        assertThat(bundle.recentPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(bundle.webhookSetupReadiness().status()).isEqualTo("READY");
        assertThat(bundle.webhookSetupReadiness().payloadUrl()).isEqualTo("https://demo.trycloudflare.com/api/github/webhook");
        assertThat(bundle.webhookSetupReadiness().latestDeliveryId()).isEqualTo("delivery-1");
        assertThat(bundle.latestWebhookDelivery().deliveryId()).isEqualTo("delivery-1");
        assertThat(bundle.recentWebhookDeliveries())
                .extracting(WebhookDeliveryDiagnosticVo::deliveryId)
                .containsExactly("delivery-1", "delivery-2");
        assertThat(bundle.recentWebhookDeliveries())
                .extracting(WebhookDeliveryDiagnosticVo::status)
                .containsExactly(WebhookDeliveryDiagnosticStatus.TASK_CREATED, WebhookDeliveryDiagnosticStatus.REJECTED);
        assertThat(bundle.recentWebhookDeliveries())
                .extracting(WebhookDeliveryDiagnosticVo::outcomeType)
                .containsExactly(WebhookDeliveryOutcomeType.TASK, WebhookDeliveryOutcomeType.REJECTED_TRIGGER);
        assertThat(bundle.rejectedTriggerSummary().totalCount()).isEqualTo(4);
        assertThat(bundle.activeQuarantineCount()).isEqualTo(1);
        assertThat(bundle.handoffShareChecklistStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(bundle.handoffShareChecklistSummary()).isEqualTo("Latest handoff archive is ready to share.");
        assertThat(bundle.handoffShareChecklistNextAction())
                .isEqualTo("Share the latest handoff package summary and archived package with the reviewer.");
        assertThat(bundle.handoffShareCenterStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(bundle.handoffShareCenterSummary()).isEqualTo("Post-demo handoff package is ready to share.");
        assertThat(bundle.handoffShareCenterNextAction())
                .isEqualTo("Download the package, archive summary, and share checklist before sending handoff evidence.");
        assertThat(bundle.handoffShareCenterDownloadActions()).containsExactly(
                "Download handoff package archive handoff-archive-1.",
                "Download handoff package archive summary.",
                "Download handoff share checklist."
        );
        assertThat(bundle.nextActions()).containsExactly(
                "Fix failing adapter fixtures before a live demo.",
                "Inspect active trigger quarantines before a live demo."
        );
    }

    @Test
    void should_report_ready_when_all_evidence_is_healthy() {
        DemoEvidenceBundleService service = new DemoEvidenceBundleService(
                () -> readiness(DemoReadinessStatus.READY, List.of()),
                () -> smokeChecklist(DemoSmokeChecklistStatus.READY, List.of()),
                DemoEvidenceBundleServiceTests::configuration,
                () -> List.of(fixture("java-maven", "PASS")),
                FixTaskQueueSummaryVo::empty,
                () -> List.of(task("task-1", FixTaskStatus.COMPLETED, "https://github.com/bingqin2/PatchPilot/pull/42")),
                () -> List.of(webhookDelivery("delivery-1", WebhookDeliveryDiagnosticStatus.TASK_CREATED, "task-1")),
                DemoEvidenceBundleServiceTests::webhookSetupReadiness,
                () -> rejectedTriggerSummary(0),
                List::of,
                DemoEvidenceBundleServiceTests::handoffPackageArchiveSummary,
                DemoEvidenceBundleServiceTests::handoffShareCenter
        );

        DemoEvidenceBundleVo bundle = service.getEvidenceBundle();

        assertThat(bundle.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(bundle.summary()).isEqualTo("Demo evidence bundle is ready.");
        assertThat(bundle.handoffShareChecklistStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(bundle.handoffShareChecklistSummary()).isEqualTo("Latest handoff archive is ready to share.");
        assertThat(bundle.handoffShareChecklistNextAction())
                .isEqualTo("Share the latest handoff package summary and archived package with the reviewer.");
        assertThat(bundle.handoffShareCenterStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(bundle.handoffShareCenterSummary()).isEqualTo("Post-demo handoff package is ready to share.");
        assertThat(bundle.handoffShareCenterNextAction())
                .isEqualTo("Download the package, archive summary, and share checklist before sending handoff evidence.");
        assertThat(bundle.nextActions()).containsExactly("Use this evidence bundle as the live demo baseline.");
    }

    private static DemoReadinessVo readiness(DemoReadinessStatus status, List<String> nextActions) {
        return new DemoReadinessVo(
                status,
                status == DemoReadinessStatus.READY ? "PatchPilot is ready for a controlled demo." : "PatchPilot needs attention before a live demo.",
                List.of(new DemoReadinessCheckVo("Credentials", DemoReadinessStatus.READY, "Configured.", "No action needed.")),
                nextActions
        );
    }

    private static DemoSmokeChecklistVo smokeChecklist(DemoSmokeChecklistStatus status, List<String> nextActions) {
        return new DemoSmokeChecklistVo(
                status,
                status == DemoSmokeChecklistStatus.READY ? "Live demo smoke checklist is ready." : "Live demo smoke checklist needs attention.",
                List.of(new DemoSmokeChecklistStepVo(1, "Readiness gate", status, "Ready.", "Evidence", "No action needed.")),
                nextActions
        );
    }

    private static GitHubWebhookSetupReadinessVo webhookSetupReadiness() {
        return new GitHubWebhookSetupReadinessVo(
                "READY",
                true,
                true,
                "https://demo.trycloudflare.com",
                "https://demo.trycloudflare.com/api/github/webhook",
                "https://demo.trycloudflare.com/health",
                "TASK_CREATED",
                "delivery-1",
                false,
                "Webhook setup is ready for GitHub deliveries.",
                List.of("Use the payload URL in GitHub Webhooks and continue the live demo."),
                Instant.parse("2026-06-27T01:00:00Z"),
                "# PatchPilot Webhook Setup Readiness"
        );
    }

    private static ConfigurationSummaryVo configuration() {
        return new ConfigurationSummaryVo(
                "openai-compatible",
                "gpt-5.5",
                "https://api.example.test/v1",
                true,
                true,
                true,
                true,
                "https://demo.trycloudflare.com",
                "https://demo.trycloudflare.com/api/github/webhook",
                true,
                true,
                "/tmp/patchpilot/workspaces",
                3,
                1000,
                30000,
                25000,
                true,
                true,
                true,
                900000,
                10,
                20,
                5,
                true,
                900000,
                5,
                1800000,
                true,
                true,
                true,
                true,
                15,
                List.of("bingqin2"),
                List.of("bingqin2/PatchPilot"),
                List.of("release-captain"),
                List.of("/tmp/patchpilot/workspaces", "docs/demo-repositories")
        );
    }

    private static LanguageAdapterFixtureVerificationVo fixture(String name, String status) {
        return new LanguageAdapterFixtureVerificationVo(
                name,
                "docs/demo-repositories/" + name,
                "java",
                "maven",
                List.of("mvn", "test"),
                "java",
                "maven",
                List.of("mvn", "test"),
                "Detected fixture",
                status
        );
    }

    private static FixTaskVo task(String id, FixTaskStatus status, String pullRequestUrl) {
        return new FixTaskVo(
                id,
                "bingqin2",
                "PatchPilot",
                1,
                0,
                "bingqin2",
                "/agent fix replace docs/demo.md demo",
                "delivery-" + id,
                123,
                status,
                null,
                Instant.parse("2026-06-24T00:00:00Z"),
                pullRequestUrl,
                status == FixTaskStatus.COMPLETED ? Instant.parse("2026-06-24T00:05:00Z") : null,
                Instant.parse("2026-06-24T00:05:00Z"),
                "java",
                "maven",
                "mvn test",
                "Detected Maven project",
                456L,
                "https://github.com/bingqin2/PatchPilot/issues/1#issuecomment-456"
        );
    }

    private static WebhookDeliveryDiagnosticVo webhookDelivery(
            String deliveryId,
            WebhookDeliveryDiagnosticStatus status,
            String outcomeId
    ) {
        boolean taskCreated = status == WebhookDeliveryDiagnosticStatus.TASK_CREATED;
        String message = status == WebhookDeliveryDiagnosticStatus.TASK_CREATED
                ? "Webhook created a task."
                : "Webhook trigger was rejected before task creation.";
        return new WebhookDeliveryDiagnosticVo(
                deliveryId,
                deliveryId,
                "issue_comment.created",
                status,
                taskCreated ? outcomeId : null,
                "bingqin2",
                "PatchPilot",
                1L,
                "bingqin2",
                "/agent fix replace docs/demo.md demo",
                message,
                false,
                taskCreated ? "Task was created." : "Rejected trigger was recorded.",
                taskCreated ? WebhookDeliveryOutcomeType.TASK : WebhookDeliveryOutcomeType.REJECTED_TRIGGER,
                outcomeId,
                taskCreated ? "/tasks/" + outcomeId : "/rejected-triggers/" + outcomeId,
                Instant.parse("2026-06-24T00:00:00Z")
        );
    }

    private static RejectedTriggerAuditSummaryVo rejectedTriggerSummary(long totalCount) {
        return new RejectedTriggerAuditSummaryVo(
                totalCount,
                List.of(new RejectedTriggerCountVo("MODEL_REJECTED", totalCount)),
                List.of(),
                List.of(),
                List.of()
        );
    }

    private static TriggerQuarantineVo quarantine(String id) {
        return new TriggerQuarantineVo(
                id,
                TriggerQuarantineScope.TRIGGER_USER,
                "noisy-user",
                "Repeated rejected triggers",
                "ABUSE_QUARANTINED",
                5,
                600000,
                Instant.parse("2026-06-24T00:00:00Z"),
                Instant.parse("2026-06-24T00:30:00Z"),
                Instant.parse("2026-06-24T00:00:00Z"),
                Instant.parse("2026-06-24T00:00:00Z"),
                null,
                null,
                null,
                null,
                true
        );
    }

    private static DemoHandoffPackageArchiveSummaryVo handoffPackageArchiveSummary() {
        return new DemoHandoffPackageArchiveSummaryVo(
                "READY",
                true,
                1,
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                DemoReadinessStatus.READY,
                Instant.parse("2026-06-24T04:00:00Z"),
                "Latest archived handoff package is READY and can be shared.",
                "No missing handoff evidence.",
                "# PatchPilot Handoff Package Archive Summary\n\n- Status: `READY`"
        );
    }

    private static DemoHandoffShareCenterVo handoffShareCenter() {
        return new DemoHandoffShareCenterVo(
                DemoReadinessStatus.READY,
                true,
                "Post-demo handoff package is ready to share.",
                "Download the package, archive summary, and share checklist before sending handoff evidence.",
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                "2026-06-24T04:00:00Z",
                List.of(
                        "Download handoff package archive handoff-archive-1.",
                        "Download handoff package archive summary.",
                        "Download handoff share checklist."
                ),
                List.of("Latest package archive status is READY."),
                "# PatchPilot Demo Handoff Share Center",
                Instant.parse("2026-06-24T05:30:00Z")
        );
    }
}
