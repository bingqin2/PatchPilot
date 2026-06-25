package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoReadinessCheckVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistStatus;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistVo;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticStatus;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticVo;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoSmokeChecklistServiceTests {

    @Test
    void should_report_ready_smoke_checklist_with_recent_webhook_and_pull_request_evidence() {
        DemoSmokeChecklistService service = new DemoSmokeChecklistService(
                () -> new DemoReadinessVo(
                        DemoReadinessStatus.READY,
                        "PatchPilot is ready for a controlled demo.",
                        List.of(
                                check("Credentials", DemoReadinessStatus.READY),
                                check("Adapter runtimes", DemoReadinessStatus.READY)
                        ),
                        List.of("Open a controlled GitHub issue and comment /agent fix with a concrete change request.")
                ),
                () -> List.of(delivery(
                        "delivery-created",
                        WebhookDeliveryDiagnosticStatus.TASK_CREATED,
                        "task-1",
                        "Task created from /agent fix"
                )),
                () -> List.of(task("task-1", FixTaskStatus.COMPLETED, "https://github.com/bingqin2/PatchPilot/pull/12"))
        );

        DemoSmokeChecklistVo checklist = service.getSmokeChecklist();

        assertThat(checklist.status()).isEqualTo(DemoSmokeChecklistStatus.READY);
        assertThat(checklist.summary()).isEqualTo("Live demo smoke checklist is ready.");
        assertThat(checklist.steps())
                .extracting("name")
                .containsExactly(
                        "Readiness gate",
                        "Adapter runtime gate",
                        "Webhook delivery",
                        "Task execution",
                        "Pull Request evidence"
                );
        assertThat(checklist.steps())
                .filteredOn(step -> step.name().equals("Webhook delivery"))
                .singleElement()
                .satisfies(step -> {
                    assertThat(step.status()).isEqualTo(DemoSmokeChecklistStatus.READY);
                    assertThat(step.evidence()).contains("delivery-created");
                    assertThat(step.action()).isEqualTo("Post the live /agent fix comment only after confirming the webhook URL is current.");
                });
        assertThat(checklist.steps())
                .filteredOn(step -> step.name().equals("Pull Request evidence"))
                .singleElement()
                .satisfies(step -> {
                    assertThat(step.status()).isEqualTo(DemoSmokeChecklistStatus.READY);
                    assertThat(step.evidence()).contains("https://github.com/bingqin2/PatchPilot/pull/12");
                });
        assertThat(checklist.nextActions()).containsExactly("Post a concrete /agent fix comment on the controlled GitHub issue.");
    }

    @Test
    void should_block_smoke_checklist_when_demo_readiness_is_blocked() {
        DemoSmokeChecklistService service = new DemoSmokeChecklistService(
                () -> new DemoReadinessVo(
                        DemoReadinessStatus.BLOCKED,
                        "PatchPilot is blocked for demo use.",
                        List.of(check("Credentials", DemoReadinessStatus.BLOCKED)),
                        List.of("Configure missing credentials in .env and restart the backend.")
                ),
                List::of,
                List::of
        );

        DemoSmokeChecklistVo checklist = service.getSmokeChecklist();

        assertThat(checklist.status()).isEqualTo(DemoSmokeChecklistStatus.BLOCKED);
        assertThat(checklist.summary()).isEqualTo("Live demo smoke checklist is blocked.");
        assertThat(checklist.nextActions()).containsExactly("Configure missing credentials in .env and restart the backend.");
        assertThat(checklist.steps())
                .filteredOn(step -> step.name().equals("Readiness gate"))
                .singleElement()
                .satisfies(step -> assertThat(step.status()).isEqualTo(DemoSmokeChecklistStatus.BLOCKED));
    }

    @Test
    void should_surface_adapter_runtime_gate_from_demo_readiness() {
        DemoSmokeChecklistService service = new DemoSmokeChecklistService(
                () -> new DemoReadinessVo(
                        DemoReadinessStatus.NEEDS_ATTENTION,
                        "PatchPilot needs attention before a live demo.",
                        List.of(
                                check("Credentials", DemoReadinessStatus.READY),
                                new DemoReadinessCheckVo(
                                        "Adapter runtimes",
                                        DemoReadinessStatus.NEEDS_ATTENTION,
                                        "1 adapter runtime executable is missing: python-hatch requires `python`.",
                                        "Install missing adapter executables on the backend PATH before demonstrating affected languages."
                                )
                        ),
                        List.of("Install missing adapter executables on the backend PATH before demonstrating affected languages.")
                ),
                () -> List.of(delivery(
                        "delivery-created",
                        WebhookDeliveryDiagnosticStatus.TASK_CREATED,
                        "task-1",
                        "Task created from /agent fix"
                )),
                () -> List.of(task("task-1", FixTaskStatus.COMPLETED, "https://github.com/bingqin2/PatchPilot/pull/12"))
        );

        DemoSmokeChecklistVo checklist = service.getSmokeChecklist();

        assertThat(checklist.status()).isEqualTo(DemoSmokeChecklistStatus.NEEDS_ATTENTION);
        assertThat(checklist.steps())
                .extracting("name")
                .containsExactly(
                        "Readiness gate",
                        "Adapter runtime gate",
                        "Webhook delivery",
                        "Task execution",
                        "Pull Request evidence"
                );
        assertThat(checklist.steps())
                .filteredOn(step -> step.name().equals("Adapter runtime gate"))
                .singleElement()
                .satisfies(step -> {
                    assertThat(step.status()).isEqualTo(DemoSmokeChecklistStatus.NEEDS_ATTENTION);
                    assertThat(step.message()).isEqualTo("1 adapter runtime executable is missing: python-hatch requires `python`.");
                    assertThat(step.action()).isEqualTo("Install missing adapter executables on the backend PATH before demonstrating affected languages.");
                });
    }

    @Test
    void should_request_attention_when_latest_webhook_delivery_needs_redelivery() {
        DemoSmokeChecklistService service = new DemoSmokeChecklistService(
                () -> new DemoReadinessVo(
                        DemoReadinessStatus.READY,
                        "PatchPilot is ready for a controlled demo.",
                        List.of(
                                check("Credentials", DemoReadinessStatus.READY),
                                check("Adapter runtimes", DemoReadinessStatus.READY)
                        ),
                        List.of("Open a controlled GitHub issue and comment /agent fix with a concrete change request.")
                ),
                () -> List.of(delivery(
                        "delivery-invalid",
                        WebhookDeliveryDiagnosticStatus.INVALID_SIGNATURE,
                        null,
                        "Invalid GitHub webhook signature"
                )),
                () -> List.of(task("task-1", FixTaskStatus.COMPLETED, "https://github.com/bingqin2/PatchPilot/pull/12"))
        );

        DemoSmokeChecklistVo checklist = service.getSmokeChecklist();

        assertThat(checklist.status()).isEqualTo(DemoSmokeChecklistStatus.NEEDS_ATTENTION);
        assertThat(checklist.steps())
                .filteredOn(step -> step.name().equals("Webhook delivery"))
                .singleElement()
                .satisfies(step -> {
                    assertThat(step.status()).isEqualTo(DemoSmokeChecklistStatus.NEEDS_ATTENTION);
                    assertThat(step.evidence()).contains("delivery-invalid");
                    assertThat(step.action()).isEqualTo("Fix the webhook secret or URL, then use GitHub Redeliver before the live demo.");
                });
        assertThat(checklist.nextActions()).contains("Fix the webhook secret or URL, then use GitHub Redeliver before the live demo.");
    }

    @Test
    void should_not_treat_duplicate_delivery_as_ready_smoke_evidence() {
        DemoSmokeChecklistService service = new DemoSmokeChecklistService(
                () -> new DemoReadinessVo(
                        DemoReadinessStatus.READY,
                        "PatchPilot is ready for a controlled demo.",
                        List.of(
                                check("Credentials", DemoReadinessStatus.READY),
                                check("Adapter runtimes", DemoReadinessStatus.READY)
                        ),
                        List.of("Open a controlled GitHub issue and comment /agent fix with a concrete change request.")
                ),
                () -> List.of(delivery(
                        "delivery-duplicate",
                        WebhookDeliveryDiagnosticStatus.DUPLICATE_DELIVERY,
                        "task-1",
                        "Duplicate delivery was ignored"
                )),
                () -> List.of(task("task-1", FixTaskStatus.COMPLETED, "https://github.com/bingqin2/PatchPilot/pull/12"))
        );

        DemoSmokeChecklistVo checklist = service.getSmokeChecklist();

        assertThat(checklist.status()).isEqualTo(DemoSmokeChecklistStatus.NEEDS_ATTENTION);
        assertThat(checklist.steps())
                .filteredOn(step -> step.name().equals("Webhook delivery"))
                .singleElement()
                .satisfies(step -> {
                    assertThat(step.status()).isEqualTo(DemoSmokeChecklistStatus.NEEDS_ATTENTION);
                    assertThat(step.evidence()).contains("delivery-duplicate");
                    assertThat(step.action()).contains("already handled");
                });
    }

    private static DemoReadinessCheckVo check(String name, DemoReadinessStatus status) {
        return new DemoReadinessCheckVo(name, status, name + " message", name + " action");
    }

    private static WebhookDeliveryDiagnosticVo delivery(
            String deliveryId,
            WebhookDeliveryDiagnosticStatus status,
            String taskId,
            String message
    ) {
        return new WebhookDeliveryDiagnosticVo(
                "diagnostic-" + deliveryId,
                deliveryId,
                "issue_comment",
                status,
                taskId,
                "bingqin2",
                "PatchPilot",
                1L,
                "bingqin2",
                "/agent fix touch docs/demo.md",
                message,
                Instant.parse("2026-06-23T01:00:00Z")
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
                "/agent fix touch docs/demo.md",
                "delivery-" + id,
                123,
                status,
                null,
                Instant.parse("2026-06-23T01:00:00Z"),
                pullRequestUrl,
                Instant.parse("2026-06-23T01:05:00Z"),
                Instant.parse("2026-06-23T01:05:00Z"),
                "java",
                "maven",
                "mvn test",
                456L,
                "https://github.com/bingqin2/PatchPilot/issues/1#issuecomment-456"
        );
    }
}
