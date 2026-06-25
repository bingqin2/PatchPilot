package io.patchpilot.backend.demo;

import io.patchpilot.backend.configuration.ConfigurationSummaryVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoReadinessCheckVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistStatus;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistStepVo;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistVo;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticStatus;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticVo;
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
                () -> List.of(webhookDelivery("delivery-1", WebhookDeliveryDiagnosticStatus.TASK_CREATED, "task-2")),
                () -> rejectedTriggerSummary(4),
                () -> List.of(quarantine("quarantine-1"))
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
        assertThat(bundle.latestWebhookDelivery().deliveryId()).isEqualTo("delivery-1");
        assertThat(bundle.rejectedTriggerSummary().totalCount()).isEqualTo(4);
        assertThat(bundle.activeQuarantineCount()).isEqualTo(1);
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
                () -> rejectedTriggerSummary(0),
                List::of
        );

        DemoEvidenceBundleVo bundle = service.getEvidenceBundle();

        assertThat(bundle.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(bundle.summary()).isEqualTo("Demo evidence bundle is ready.");
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

    private static ConfigurationSummaryVo configuration() {
        return new ConfigurationSummaryVo(
                "openai-compatible",
                "gpt-5.5",
                "https://api.example.test/v1",
                true,
                true,
                true,
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
            String taskId
    ) {
        return new WebhookDeliveryDiagnosticVo(
                deliveryId,
                deliveryId,
                "issue_comment.created",
                status,
                taskId,
                "bingqin2",
                "PatchPilot",
                1L,
                "bingqin2",
                "/agent fix replace docs/demo.md demo",
                "Webhook created a task.",
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
}
