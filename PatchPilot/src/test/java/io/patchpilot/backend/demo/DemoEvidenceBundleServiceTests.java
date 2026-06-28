package io.patchpilot.backend.demo;

import io.patchpilot.backend.configuration.ConfigurationSummaryVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoHandoffFinalizationCheckVo;
import io.patchpilot.backend.demo.domain.DemoHandoffFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveSummaryVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareCenterVo;
import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCloseoutArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceFinalizationCheckVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceShareCenterVo;
import io.patchpilot.backend.demo.domain.DemoReadinessCheckVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistStatus;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistStepVo;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistVo;
import io.patchpilot.backend.evaluation.domain.EvaluationRunArchiveDigestVo;
import io.patchpilot.backend.evaluation.domain.EvaluationRunArchiveReadinessSummaryVo;
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
                DemoEvidenceBundleServiceTests::evaluationRunReadiness,
                DemoEvidenceBundleServiceTests::handoffPackageArchiveSummary,
                DemoEvidenceBundleServiceTests::handoffShareCenter,
                DemoEvidenceBundleServiceTests::handoffFinalizationMissingReceipt,
                DemoEvidenceBundleServiceTests::launchEvidenceShareCenter,
                DemoEvidenceBundleServiceTests::launchEvidenceFinalizationReady,
                () -> List.of(launchAcceptanceCloseoutArchive(DemoReadinessStatus.READY, true))
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
        assertThat(bundle.evaluationRunReadiness().status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(bundle.evaluationRunReadiness().latestRunId()).isEqualTo("evaluation-run-2");
        assertThat(bundle.evaluationRunReadiness().previousRunId()).isEqualTo("evaluation-run-1");
        assertThat(bundle.evaluationRunReadiness().passedDelta()).isEqualTo(1);
        assertThat(bundle.evaluationRunReadiness().failedDelta()).isZero();
        assertThat(bundle.evaluationRunReadiness().skippedDelta()).isZero();
        assertThat(bundle.evaluationRunReadiness().coveredLanguages()).containsExactly("java", "python");
        assertThat(bundle.evaluationRunReadiness().coveredBuildSystems()).containsExactly("maven", "pytest");
        assertThat(bundle.evaluationRunReadiness().safetyRejectionCategories()).containsExactly("DANGEROUS_REQUEST", "SECRET_EXFILTRATION");
        assertThat(bundle.evaluationRunReadiness().nextAction())
                .isEqualTo("Full evaluation run archive is ready; use it as current demo evidence.");
        assertThat(bundle.evaluationRunReadiness().sideEffectContract())
                .contains("does not create tasks, call the model, mutate Git, or write to GitHub");
        assertThat(bundle.handoffShareChecklistStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(bundle.handoffShareChecklistSummary()).isEqualTo("Latest handoff archive is ready to share.");
        assertThat(bundle.handoffShareChecklistNextAction())
                .isEqualTo("Share the latest handoff package summary and archived package with the reviewer.");
        assertThat(bundle.handoffShareCenterStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(bundle.handoffShareCenterSummary()).isEqualTo("Post-demo handoff package is ready to share.");
        assertThat(bundle.handoffShareCenterNextAction())
                .isEqualTo("Download the package, send the prepared handoff message, then record a delivery receipt.");
        assertThat(bundle.handoffShareCenterDownloadActions()).containsExactly(
                "Download handoff package archive handoff-archive-1.",
                "Download handoff package archive summary.",
                "Download handoff share checklist.",
                "Record a handoff share delivery receipt after sending the package."
        );
        assertThat(bundle.launchEvidenceShareCenterStatus()).isEqualTo("READY");
        assertThat(bundle.launchEvidenceShareCenterReady()).isTrue();
        assertThat(bundle.launchEvidenceShareCenterSummary())
                .isEqualTo("Latest archived launch evidence package is READY and can be shared.");
        assertThat(bundle.launchEvidenceShareCenterNextAction())
                .isEqualTo("Download the archived launch evidence package and share it with reviewers.");
        assertThat(bundle.launchEvidenceShareCenterArchiveCount()).isEqualTo(1);
        assertThat(bundle.launchEvidenceShareCenterLatestArchiveId()).isEqualTo("launch-evidence-archive-1");
        assertThat(bundle.launchEvidenceShareCenterLatestSessionId()).isEqualTo("demo-session-20260624T003000Z");
        assertThat(bundle.launchEvidenceShareCenterLatestPullRequestUrl())
                .isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(bundle.launchEvidenceShareCenterDownloadActions()).containsExactly(
                "Download launch evidence package archive launch-evidence-archive-1.",
                "Download launch evidence share center report.",
                "Open Pull Request https://github.com/bingqin2/PatchPilot/pull/42 for review.",
                "Download launch evidence delivery receipt launch-delivery-receipt-1."
        );
        assertThat(bundle.launchEvidenceFinalizationStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(bundle.launchEvidenceFinalized()).isTrue();
        assertThat(bundle.launchEvidenceFinalizationSummary())
                .isEqualTo("Demo launch evidence is finalized with a fresh delivery receipt for the current archive.");
        assertThat(bundle.launchEvidenceFinalizationNextAction())
                .isEqualTo("Use the finalization report as the launch evidence delivery acceptance record.");
        assertThat(bundle.launchEvidenceFinalizationDeliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(bundle.launchEvidenceFinalizationDeliveryReceiptFresh()).isTrue();
        assertThat(bundle.launchEvidenceFinalizationLatestDeliveryReceiptId()).isEqualTo("launch-delivery-receipt-1");
        assertThat(bundle.launchAcceptanceCloseoutEvidence().status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(bundle.launchAcceptanceCloseoutEvidence().archived()).isTrue();
        assertThat(bundle.launchAcceptanceCloseoutEvidence().accepted()).isTrue();
        assertThat(bundle.launchAcceptanceCloseoutEvidence().summary())
                .isEqualTo("Latest launch acceptance closeout archive is accepted and ready.");
        assertThat(bundle.launchAcceptanceCloseoutEvidence().nextAction())
                .isEqualTo("Use the archived launch acceptance closeout as the final launch evidence record.");
        assertThat(bundle.launchAcceptanceCloseoutEvidence().archiveCount()).isEqualTo(1);
        assertThat(bundle.launchAcceptanceCloseoutEvidence().latestArchiveId())
                .isEqualTo("launch-closeout-archive-1");
        assertThat(bundle.launchAcceptanceCloseoutEvidence().latestEvidenceArchiveId())
                .isEqualTo("launch-evidence-archive-1");
        assertThat(bundle.launchAcceptanceCloseoutEvidence().latestDeliveryReceiptId())
                .isEqualTo("launch-delivery-receipt-1");
        assertThat(bundle.launchAcceptanceCloseoutEvidence().latestPullRequestUrl())
                .isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(bundle.launchAcceptanceCloseoutEvidence().downloadActions()).containsExactly(
                "Download launch acceptance closeout archive launch-closeout-archive-1.",
                "Download linked launch evidence archive launch-evidence-archive-1.",
                "Download launch evidence delivery receipt launch-delivery-receipt-1."
        );
        assertThat(bundle.handoffShareDeliveryReceiptRecorded()).isFalse();
        assertThat(bundle.handoffShareLatestDeliveryReceiptId()).isNull();
        assertThat(bundle.handoffShareLatestDeliveryTarget()).isNull();
        assertThat(bundle.handoffShareLatestDeliveryChannel()).isNull();
        assertThat(bundle.handoffShareLatestDeliveredAt()).isNull();
        assertThat(bundle.handoffShareDeliveryReceiptFreshness()).isEqualTo("MISSING");
        assertThat(bundle.handoffShareDeliveryReceiptFresh()).isFalse();
        assertThat(bundle.handoffShareDeliveryReceiptFreshnessSummary())
                .isEqualTo("No delivery receipt has been recorded for the current handoff package.");
        assertThat(bundle.handoffFinalizationStatus()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(bundle.handoffFinalized()).isFalse();
        assertThat(bundle.handoffFinalizationSummary())
                .isEqualTo("Demo handoff package is send-ready but final delivery evidence is not current.");
        assertThat(bundle.handoffFinalizationNextAction())
                .isEqualTo("Send the current handoff package, record a delivery receipt, then download the finalization report.");
        assertThat(bundle.handoffFinalizationDeliveryReceiptFreshness()).isEqualTo("MISSING");
        assertThat(bundle.handoffFinalizationDeliveryReceiptFresh()).isFalse();
        assertThat(bundle.handoffFinalizationLatestDeliveryReceiptId()).isNull();
        assertThat(bundle.nextActions()).containsExactly(
                "Fix failing adapter fixtures before a live demo.",
                "Inspect active trigger quarantines before a live demo.",
                "Send the current handoff package, record a delivery receipt, then download the finalization report."
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
                DemoEvidenceBundleServiceTests::evaluationRunReadiness,
                DemoEvidenceBundleServiceTests::handoffPackageArchiveSummary,
                DemoEvidenceBundleServiceTests::deliveredHandoffShareCenter,
                DemoEvidenceBundleServiceTests::handoffFinalizationReady,
                DemoEvidenceBundleServiceTests::launchEvidenceShareCenter,
                DemoEvidenceBundleServiceTests::launchEvidenceFinalizationReady,
                () -> List.of(launchAcceptanceCloseoutArchive(DemoReadinessStatus.READY, true))
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
        assertThat(bundle.handoffShareDeliveryReceiptRecorded()).isTrue();
        assertThat(bundle.handoffShareLatestDeliveryReceiptId()).isEqualTo("receipt-1");
        assertThat(bundle.handoffShareLatestDeliveryTarget()).isEqualTo("Demo reviewer");
        assertThat(bundle.handoffShareLatestDeliveryChannel()).isEqualTo("email");
        assertThat(bundle.handoffShareLatestDeliveredAt()).isEqualTo("2026-06-24T05:20:00Z");
        assertThat(bundle.handoffShareDeliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(bundle.handoffShareDeliveryReceiptFresh()).isTrue();
        assertThat(bundle.handoffShareDeliveryReceiptFreshnessSummary())
                .isEqualTo("Latest delivery receipt matches the current handoff archive and session.");
        assertThat(bundle.handoffFinalizationStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(bundle.handoffFinalized()).isTrue();
        assertThat(bundle.handoffFinalizationSummary())
                .isEqualTo("Demo handoff is finalized with a fresh delivery receipt for the current archive.");
        assertThat(bundle.handoffFinalizationNextAction())
                .isEqualTo("Use the finalization report as the post-demo delivery acceptance record.");
        assertThat(bundle.handoffFinalizationDeliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(bundle.handoffFinalizationDeliveryReceiptFresh()).isTrue();
        assertThat(bundle.handoffFinalizationLatestDeliveryReceiptId()).isEqualTo("receipt-1");
        assertThat(bundle.launchEvidenceFinalizationStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(bundle.launchEvidenceFinalized()).isTrue();
        assertThat(bundle.launchEvidenceFinalizationLatestDeliveryReceiptId()).isEqualTo("launch-delivery-receipt-1");
        assertThat(bundle.launchAcceptanceCloseoutEvidence().accepted()).isTrue();
        assertThat(bundle.nextActions()).containsExactly("Use this evidence bundle as the live demo baseline.");
    }

    @Test
    void should_require_final_acceptance_closeout_archive_before_reporting_bundle_ready() {
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
                DemoEvidenceBundleServiceTests::evaluationRunReadiness,
                DemoEvidenceBundleServiceTests::handoffPackageArchiveSummary,
                DemoEvidenceBundleServiceTests::deliveredHandoffShareCenter,
                DemoEvidenceBundleServiceTests::handoffFinalizationReady,
                DemoEvidenceBundleServiceTests::launchEvidenceShareCenter,
                DemoEvidenceBundleServiceTests::launchEvidenceFinalizationReady,
                List::of
        );

        DemoEvidenceBundleVo bundle = service.getEvidenceBundle();

        assertThat(bundle.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(bundle.summary()).isEqualTo("Demo evidence bundle needs attention.");
        assertThat(bundle.launchAcceptanceCloseoutEvidence().status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(bundle.launchAcceptanceCloseoutEvidence().archived()).isFalse();
        assertThat(bundle.launchAcceptanceCloseoutEvidence().accepted()).isFalse();
        assertThat(bundle.launchAcceptanceCloseoutEvidence().summary())
                .isEqualTo("No launch acceptance closeout archive is available.");
        assertThat(bundle.launchAcceptanceCloseoutEvidence().nextAction())
                .isEqualTo("Archive the final launch acceptance closeout after launch evidence is accepted.");
        assertThat(bundle.nextActions()).containsExactly(
                "Archive the final launch acceptance closeout after launch evidence is accepted."
        );
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

    private static EvaluationRunArchiveReadinessSummaryVo evaluationRunReadiness() {
        return new EvaluationRunArchiveReadinessSummaryVo(
                "READY",
                new EvaluationRunArchiveDigestVo(
                        "evaluation-run-2",
                        "READY",
                        6,
                        4,
                        2,
                        4,
                        4,
                        0,
                        0,
                        Instant.parse("2026-06-24T03:00:00Z")
                ),
                new EvaluationRunArchiveDigestVo(
                        "evaluation-run-1",
                        "READY",
                        5,
                        3,
                        2,
                        3,
                        3,
                        0,
                        0,
                        Instant.parse("2026-06-24T02:00:00Z")
                ),
                1,
                0,
                0,
                List.of("java", "python"),
                List.of("maven", "pytest"),
                List.of("DANGEROUS_REQUEST", "SECRET_EXFILTRATION"),
                "Evaluation run readiness summary reads archived full evaluation runs only; it does not create tasks, call the model, mutate Git, or write to GitHub.",
                "Full evaluation run archive is ready; use it as current demo evidence.",
                "# PatchPilot Evaluation Run Readiness Summary"
        );
    }

    private static EvaluationRunArchiveReadinessSummaryVo evaluationRunMissingReadiness() {
        return new EvaluationRunArchiveReadinessSummaryVo(
                "NO_ARCHIVES",
                null,
                null,
                0,
                0,
                0,
                List.of(),
                List.of(),
                List.of(),
                "Evaluation run readiness summary reads archived full evaluation runs only; it does not create tasks, call the model, mutate Git, or write to GitHub.",
                "Run and archive a full evaluation before using it as demo readiness evidence.",
                "# PatchPilot Evaluation Run Readiness Summary"
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
                "Download the package, send the prepared handoff message, then record a delivery receipt.",
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                "2026-06-24T04:00:00Z",
                null,
                null,
                null,
                null,
                false,
                "MISSING",
                false,
                "No delivery receipt has been recorded for the current handoff package.",
                List.of(
                        "Download handoff package archive handoff-archive-1.",
                        "Download handoff package archive summary.",
                        "Download handoff share checklist.",
                        "Record a handoff share delivery receipt after sending the package."
                ),
                List.of("Latest package archive status is READY."),
                "# PatchPilot Demo Handoff Share Center",
                Instant.parse("2026-06-24T05:30:00Z")
        );
    }

    private static DemoHandoffShareCenterVo deliveredHandoffShareCenter() {
        return new DemoHandoffShareCenterVo(
                DemoReadinessStatus.READY,
                true,
                "Post-demo handoff package is ready to share.",
                "Download the package, archive summary, and share checklist before sending handoff evidence.",
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                "2026-06-24T04:00:00Z",
                "receipt-1",
                "Demo reviewer",
                "email",
                "2026-06-24T05:20:00Z",
                true,
                "FRESH",
                true,
                "Latest delivery receipt matches the current handoff archive and session.",
                List.of(
                        "Download handoff package archive handoff-archive-1.",
                        "Download handoff package archive summary.",
                        "Download handoff share checklist.",
                        "Download handoff share delivery receipt receipt-1."
                ),
                List.of("Latest delivery receipt receipt-1 was recorded for Demo reviewer via email."),
                "# PatchPilot Demo Handoff Share Center",
                Instant.parse("2026-06-24T05:30:00Z")
        );
    }

    private static DemoLaunchEvidenceShareCenterVo launchEvidenceShareCenter() {
        return new DemoLaunchEvidenceShareCenterVo(
                "READY",
                true,
                "Latest archived launch evidence package is READY and can be shared.",
                "Download the archived launch evidence package and share it with reviewers.",
                1,
                "launch-evidence-archive-1",
                "demo-session-20260624T003000Z",
                "2026-06-24T07:00:00Z",
                "task-2",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "delivery-1",
                "evaluation-run-2",
                "launch-delivery-receipt-1",
                "Demo reviewer",
                "email",
                "2026-06-24T07:10:00Z",
                true,
                "FRESH",
                true,
                "Latest delivery receipt matches the current launch evidence archive and session.",
                List.of(
                        "Download launch evidence package archive launch-evidence-archive-1.",
                        "Download launch evidence share center report.",
                        "Open Pull Request https://github.com/bingqin2/PatchPilot/pull/42 for review.",
                        "Download launch evidence delivery receipt launch-delivery-receipt-1."
                ),
                List.of("Latest launch evidence archive status is READY."),
                "# PatchPilot Demo Launch Evidence Share Center",
                Instant.parse("2026-06-24T07:15:00Z")
        );
    }

    private static DemoHandoffFinalizationVo handoffFinalizationMissingReceipt() {
        return new DemoHandoffFinalizationVo(
                DemoReadinessStatus.NEEDS_ATTENTION,
                false,
                "Demo handoff package is send-ready but final delivery evidence is not current.",
                "Send the current handoff package, record a delivery receipt, then download the finalization report.",
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                null,
                null,
                null,
                null,
                "MISSING",
                false,
                "No delivery receipt has been recorded for the current handoff package.",
                List.of(new DemoHandoffFinalizationCheckVo(
                        "Delivery receipt freshness",
                        DemoReadinessStatus.NEEDS_ATTENTION,
                        "No fresh delivery receipt is available.",
                        "Record a delivery receipt."
                )),
                List.of("No fresh delivery receipt is available for handoff-archive-1/demo-session-20260624T003000Z."),
                "# PatchPilot Demo Handoff Finalization Gate",
                Instant.parse("2026-06-24T06:00:00Z")
        );
    }

    private static DemoHandoffFinalizationVo handoffFinalizationReady() {
        return new DemoHandoffFinalizationVo(
                DemoReadinessStatus.READY,
                true,
                "Demo handoff is finalized with a fresh delivery receipt for the current archive.",
                "Use the finalization report as the post-demo delivery acceptance record.",
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                "receipt-1",
                "Demo reviewer",
                "email",
                "2026-06-24T05:20:00Z",
                "FRESH",
                true,
                "Latest delivery receipt matches the current handoff archive and session.",
                List.of(new DemoHandoffFinalizationCheckVo(
                        "Final acceptance evidence",
                        DemoReadinessStatus.READY,
                        "Finalization evidence is complete.",
                        "No action needed."
                )),
                List.of("Finalization report can be downloaded as the acceptance record."),
                "# PatchPilot Demo Handoff Finalization Gate",
                Instant.parse("2026-06-24T06:00:00Z")
        );
    }

    private static DemoLaunchEvidenceFinalizationVo launchEvidenceFinalizationMissingReceipt() {
        return new DemoLaunchEvidenceFinalizationVo(
                DemoReadinessStatus.NEEDS_ATTENTION,
                false,
                "Demo launch evidence package is share-ready but final delivery evidence is not current.",
                "Share the current launch evidence package, record a delivery receipt, then download the finalization report.",
                "launch-evidence-archive-1",
                "demo-session-20260624T003000Z",
                null,
                null,
                null,
                null,
                "MISSING",
                false,
                "No delivery receipt has been recorded for the current launch evidence package.",
                List.of(new DemoLaunchEvidenceFinalizationCheckVo(
                        "Delivery receipt freshness",
                        DemoReadinessStatus.NEEDS_ATTENTION,
                        "No fresh launch delivery receipt is available.",
                        "Record a launch evidence delivery receipt."
                )),
                List.of("No fresh delivery receipt is available for launch-evidence-archive-1/demo-session-20260624T003000Z."),
                "# PatchPilot Demo Launch Evidence Finalization Gate",
                Instant.parse("2026-06-24T07:20:00Z")
        );
    }

    private static DemoLaunchEvidenceFinalizationVo launchEvidenceFinalizationReady() {
        return new DemoLaunchEvidenceFinalizationVo(
                DemoReadinessStatus.READY,
                true,
                "Demo launch evidence is finalized with a fresh delivery receipt for the current archive.",
                "Use the finalization report as the launch evidence delivery acceptance record.",
                "launch-evidence-archive-1",
                "demo-session-20260624T003000Z",
                "launch-delivery-receipt-1",
                "Demo reviewer",
                "email",
                "2026-06-24T07:10:00Z",
                "FRESH",
                true,
                "Latest delivery receipt matches the current launch evidence archive and session.",
                List.of(new DemoLaunchEvidenceFinalizationCheckVo(
                        "Launch acceptance evidence",
                        DemoReadinessStatus.READY,
                        "Finalization report is ready as the launch acceptance record.",
                        "Download the finalization report."
                )),
                List.of("Finalization report can be downloaded as the launch delivery acceptance record."),
                "# PatchPilot Demo Launch Evidence Finalization Gate",
                Instant.parse("2026-06-24T07:20:00Z")
        );
    }

    private static DemoLaunchAcceptanceCloseoutArchiveVo launchAcceptanceCloseoutArchive(
            DemoReadinessStatus status,
            boolean accepted
    ) {
        return new DemoLaunchAcceptanceCloseoutArchiveVo(
                "launch-closeout-archive-1",
                status,
                accepted,
                "PatchPilot launch acceptance closeout is ready.",
                "demo-session-20260624T003000Z",
                "task-2",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "delivery-1",
                "evaluation-run-2",
                "launch-evidence-archive-1",
                "launch-delivery-receipt-1",
                "Demo reviewer",
                "email",
                "FRESH",
                Instant.parse("2026-06-24T08:00:00Z"),
                "# PatchPilot Launch Acceptance Closeout Archive"
        );
    }
}
