package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoAdapterFixtureEvidenceVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleSummaryVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoReadinessCheckVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistStatus;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistStepVo;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistVo;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticStatus;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticVo;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditSummaryVo;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoRunbookServiceTests {

    @Test
    void should_format_demo_evidence_bundle_as_markdown_runbook() {
        DemoRunbookService service = new DemoRunbookService(() -> bundle());

        String runbook = service.getRunbook();

        assertThat(runbook)
                .contains("# PatchPilot Demo Runbook")
                .contains("- Status: `NEEDS_ATTENTION`")
                .contains("- Summary: Demo evidence bundle needs attention.")
                .contains("- Recent Pull Request: https://github.com/bingqin2/PatchPilot/pull/42")
                .contains("- Recent task: `task-1` (`COMPLETED`)")
                .contains("- Latest webhook delivery: `delivery-1` (`TASK_CREATED`)")
                .contains("- Adapter fixtures: 2 total, 1 failed")
                .contains("- Queue: 3 total, 1 pending, 2 completed, 0 failed")
                .contains("- Rejected triggers: 4 recent")
                .contains("- Active quarantines: 1")
                .contains("## Readiness")
                .contains("- `Credentials`: `READY` - Required credentials are configured.")
                .contains("## Smoke Checklist")
                .contains("- 1. `Webhook delivery`: `NEEDS_ATTENTION` - Latest delivery needs redelivery.")
                .contains("## Next Actions")
                .contains("- Fix the webhook secret or URL, then use GitHub Redeliver before the live demo.");
    }

    @Test
    void should_render_missing_optional_evidence_as_none() {
        DemoRunbookService service = new DemoRunbookService(() -> new DemoEvidenceBundleVo(
                DemoReadinessStatus.READY,
                "Demo evidence bundle is ready.",
                new DemoEvidenceBundleSummaryVo(1, 0, 0, 0, false),
                new DemoReadinessVo(DemoReadinessStatus.READY, "Ready.", List.of(), List.of()),
                new DemoSmokeChecklistVo(DemoSmokeChecklistStatus.READY, "Ready.", List.of(), List.of()),
                null,
                new DemoAdapterFixtureEvidenceVo(1, 0),
                FixTaskQueueSummaryVo.empty(),
                null,
                null,
                null,
                null,
                List.of(),
                new RejectedTriggerAuditSummaryVo(0, List.of(), List.of(), List.of(), List.of()),
                0,
                DemoReadinessStatus.NEEDS_ATTENTION,
                "No handoff package archive is available for sharing.",
                "Archive a demo handoff package after a completed live run before sharing handoff evidence.",
                Instant.parse("2026-06-24T00:00:00Z"),
                List.of()
        ));

        String runbook = service.getRunbook();

        assertThat(runbook)
                .contains("- Recent Pull Request: none")
                .contains("- Recent task: none")
                .contains("- Latest webhook delivery: none")
                .contains("- No readiness checks recorded.")
                .contains("- No smoke checklist steps recorded.")
                .contains("- No next actions recorded.");
    }

    private static DemoEvidenceBundleVo bundle() {
        return new DemoEvidenceBundleVo(
                DemoReadinessStatus.NEEDS_ATTENTION,
                "Demo evidence bundle needs attention.",
                new DemoEvidenceBundleSummaryVo(2, 1, 2, 1, true),
                new DemoReadinessVo(
                        DemoReadinessStatus.READY,
                        "PatchPilot is ready for a controlled demo.",
                        List.of(new DemoReadinessCheckVo(
                                "Credentials",
                                DemoReadinessStatus.READY,
                                "Required credentials are configured.",
                                "No action needed."
                        )),
                        List.of()
                ),
                new DemoSmokeChecklistVo(
                        DemoSmokeChecklistStatus.NEEDS_ATTENTION,
                        "Live demo smoke checklist needs attention.",
                        List.of(new DemoSmokeChecklistStepVo(
                                1,
                                "Webhook delivery",
                                DemoSmokeChecklistStatus.NEEDS_ATTENTION,
                                "Latest delivery needs redelivery.",
                                "delivery-1",
                                "Fix the webhook secret or URL, then use GitHub Redeliver before the live demo."
                        )),
                        List.of("Fix the webhook secret or URL, then use GitHub Redeliver before the live demo.")
                ),
                null,
                new DemoAdapterFixtureEvidenceVo(2, 1),
                new FixTaskQueueSummaryVo(3, 1, 1, 0, 0, 2, 0, 0),
                task(),
                "https://github.com/bingqin2/PatchPilot/pull/42",
                null,
                webhookDelivery(),
                List.of(webhookDelivery()),
                new RejectedTriggerAuditSummaryVo(4, List.of(), List.of(), List.of(), List.of()),
                1,
                DemoReadinessStatus.READY,
                "Latest handoff archive is ready to share.",
                "Share the latest handoff package summary and archived package with the reviewer.",
                Instant.parse("2026-06-24T00:00:00Z"),
                List.of("Fix the webhook secret or URL, then use GitHub Redeliver before the live demo.")
        );
    }

    private static FixTaskVo task() {
        return new FixTaskVo(
                "task-1",
                "bingqin2",
                "PatchPilot",
                1,
                0,
                "bingqin2",
                "/agent fix replace docs/demo.md demo",
                "delivery-task-1",
                123,
                FixTaskStatus.COMPLETED,
                null,
                Instant.parse("2026-06-24T00:00:00Z"),
                "https://github.com/bingqin2/PatchPilot/pull/42",
                Instant.parse("2026-06-24T00:05:00Z"),
                Instant.parse("2026-06-24T00:05:00Z"),
                "java",
                "maven",
                "mvn test",
                "Detected Maven project",
                456L,
                "https://github.com/bingqin2/PatchPilot/issues/1#issuecomment-456"
        );
    }

    private static WebhookDeliveryDiagnosticVo webhookDelivery() {
        return new WebhookDeliveryDiagnosticVo(
                "webhook-record-1",
                "delivery-1",
                "issue_comment",
                WebhookDeliveryDiagnosticStatus.TASK_CREATED,
                "task-1",
                "bingqin2",
                "PatchPilot",
                1L,
                "bingqin2",
                "/agent fix replace docs/demo.md demo",
                "Task created.",
                Instant.parse("2026-06-24T00:00:00Z")
        );
    }
}
