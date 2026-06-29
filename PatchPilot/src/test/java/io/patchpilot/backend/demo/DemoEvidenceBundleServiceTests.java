package io.patchpilot.backend.demo;

import io.patchpilot.backend.configuration.ConfigurationSummaryVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionCloseoutArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceShareFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionCloseoutVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidenceVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleVo;
import io.patchpilot.backend.demo.domain.DemoFinalHandoffReportPackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoHandoffFinalizationCheckVo;
import io.patchpilot.backend.demo.domain.DemoHandoffFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveSummaryVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareCenterVo;
import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCertificateArchiveVo;
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
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageAcceptanceCertificateArchiveVo;
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
                DemoEvidenceBundleServiceTests::finalAcceptanceShareFinalizationReady,
                DemoEvidenceBundleServiceTests::finalAcceptanceCompletionCloseoutReady,
                () -> List.of(launchAcceptanceCloseoutArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(launchAcceptanceCertificateArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(taskEvidenceAcceptanceCertificateArchive("READY", true)),
                () -> List.of(finalHandoffReportPackageArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(finalAcceptanceCompletionCloseoutArchive(DemoReadinessStatus.READY, true)),
                DemoEvidenceBundleServiceTests::finalExternalReviewEvidencePackageReady,
                () -> List.of(finalExternalReviewEvidencePackageArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(finalExternalReviewEvidencePackageDeliveryReceipt()),
                DemoEvidenceBundleServiceTests::finalExternalReviewEvidencePackageDeliveryFinalizationReady,
                () -> List.of(finalExternalReviewEvidencePackageDeliveryFinalizationArchive()),
                DemoEvidenceBundleServiceTests::finalExternalReviewReleaseBundleReady
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
        assertThat(bundle.launchAcceptanceCertificateEvidence().status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(bundle.launchAcceptanceCertificateEvidence().archived()).isTrue();
        assertThat(bundle.launchAcceptanceCertificateEvidence().certified()).isTrue();
        assertThat(bundle.launchAcceptanceCertificateEvidence().summary())
                .isEqualTo("Latest launch acceptance certificate archive is certified and ready.");
        assertThat(bundle.launchAcceptanceCertificateEvidence().nextAction())
                .isEqualTo("Use the archived launch acceptance certificate as the external-review launch record.");
        assertThat(bundle.launchAcceptanceCertificateEvidence().archiveCount()).isEqualTo(1);
        assertThat(bundle.launchAcceptanceCertificateEvidence().latestArchiveId())
                .isEqualTo("launch-certificate-archive-1");
        assertThat(bundle.launchAcceptanceCertificateEvidence().latestCloseoutArchiveId())
                .isEqualTo("launch-closeout-archive-1");
        assertThat(bundle.launchAcceptanceCertificateEvidence().latestEvidenceArchiveId())
                .isEqualTo("launch-evidence-archive-1");
        assertThat(bundle.launchAcceptanceCertificateEvidence().latestDeliveryReceiptId())
                .isEqualTo("launch-delivery-receipt-1");
        assertThat(bundle.launchAcceptanceCertificateEvidence().latestPullRequestUrl())
                .isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(bundle.launchAcceptanceCertificateEvidence().downloadActions()).containsExactly(
                "Download launch acceptance certificate archive launch-certificate-archive-1.",
                "Download linked launch acceptance closeout archive launch-closeout-archive-1.",
                "Download launch evidence delivery receipt launch-delivery-receipt-1."
        );
        assertThat(bundle.taskEvidenceAcceptanceCertificateEvidence().status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(bundle.taskEvidenceAcceptanceCertificateEvidence().archived()).isTrue();
        assertThat(bundle.taskEvidenceAcceptanceCertificateEvidence().certified()).isTrue();
        assertThat(bundle.taskEvidenceAcceptanceCertificateEvidence().summary())
                .isEqualTo("Latest task evidence acceptance certificate archive is certified and ready.");
        assertThat(bundle.taskEvidenceAcceptanceCertificateEvidence().nextAction())
                .isEqualTo("Use the archived task evidence acceptance certificate as task-level review proof.");
        assertThat(bundle.taskEvidenceAcceptanceCertificateEvidence().archiveCount()).isEqualTo(1);
        assertThat(bundle.taskEvidenceAcceptanceCertificateEvidence().latestArchiveId())
                .isEqualTo("task-evidence-certificate-archive-1");
        assertThat(bundle.taskEvidenceAcceptanceCertificateEvidence().latestCloseoutArchiveId())
                .isEqualTo("task-evidence-closeout-archive-1");
        assertThat(bundle.taskEvidenceAcceptanceCertificateEvidence().latestEvidenceArchiveId())
                .isEqualTo("task-evidence-archive-1");
        assertThat(bundle.taskEvidenceAcceptanceCertificateEvidence().latestDeliveryReceiptId())
                .isEqualTo("task-evidence-receipt-1");
        assertThat(bundle.taskEvidenceAcceptanceCertificateEvidence().latestTaskId()).isEqualTo("task-2");
        assertThat(bundle.taskEvidenceAcceptanceCertificateEvidence().latestPullRequestUrl())
                .isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(bundle.taskEvidenceAcceptanceCertificateEvidence().downloadActions()).containsExactly(
                "Download task evidence acceptance certificate archive task-evidence-certificate-archive-1.",
                "Download linked task evidence acceptance closeout archive task-evidence-closeout-archive-1.",
                "Download task evidence delivery receipt task-evidence-receipt-1."
        );
        assertThat(bundle.finalHandoffReportPackageArchiveEvidence().status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(bundle.finalHandoffReportPackageArchiveEvidence().archived()).isTrue();
        assertThat(bundle.finalHandoffReportPackageArchiveEvidence().downloadReady()).isTrue();
        assertThat(bundle.finalHandoffReportPackageArchiveEvidence().summary())
                .isEqualTo("Latest final handoff report package archive is download-ready and ready.");
        assertThat(bundle.finalHandoffReportPackageArchiveEvidence().nextAction())
                .isEqualTo("Use the archived final handoff report package as the post-demo closeout proof.");
        assertThat(bundle.finalHandoffReportPackageArchiveEvidence().archiveCount()).isEqualTo(1);
        assertThat(bundle.finalHandoffReportPackageArchiveEvidence().latestArchiveId())
                .isEqualTo("final-handoff-package-archive-1");
        assertThat(bundle.finalHandoffReportPackageArchiveEvidence().latestHandoffArchiveId())
                .isEqualTo("handoff-archive-1");
        assertThat(bundle.finalHandoffReportPackageArchiveEvidence().latestSessionId())
                .isEqualTo("demo-session-20260624T003000Z");
        assertThat(bundle.finalHandoffReportPackageArchiveEvidence().latestDeliveryReceiptId())
                .isEqualTo("delivery-receipt-1");
        assertThat(bundle.finalHandoffReportPackageArchiveEvidence().taskCertificateArchiveId())
                .isEqualTo("task-evidence-certificate-archive-1");
        assertThat(bundle.finalHandoffReportPackageArchiveEvidence().taskCertificateReady()).isTrue();
        assertThat(bundle.finalHandoffReportPackageArchiveEvidence().downloadActions()).containsExactly(
                "Download final handoff report package archive final-handoff-package-archive-1.",
                "Download linked handoff package archive handoff-archive-1.",
                "Download handoff share delivery receipt delivery-receipt-1.",
                "Download task evidence acceptance certificate archive task-evidence-certificate-archive-1."
        );
        assertThat(bundle.finalAcceptanceShareFinalization().status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(bundle.finalAcceptanceShareFinalization().finalized()).isTrue();
        assertThat(bundle.finalAcceptanceShareFinalization().summary())
                .isEqualTo("Final demo acceptance share package is finalized with a fresh delivery receipt.");
        assertThat(bundle.finalAcceptanceShareFinalization().nextAction())
                .isEqualTo("Use the finalization report as the external-review acceptance delivery record.");
        assertThat(bundle.finalAcceptanceShareFinalization().latestArchiveId())
                .isEqualTo("final-acceptance-share-package-archive-1");
        assertThat(bundle.finalAcceptanceShareFinalization().latestTaskId()).isEqualTo("task-2");
        assertThat(bundle.finalAcceptanceShareFinalization().latestDeliveryReceiptId())
                .isEqualTo("final-acceptance-delivery-receipt-1");
        assertThat(bundle.finalAcceptanceShareFinalization().latestDeliveryTarget())
                .isEqualTo("reviewer@example.com");
        assertThat(bundle.finalAcceptanceShareFinalization().latestDeliveryChannel()).isEqualTo("email");
        assertThat(bundle.finalAcceptanceShareFinalization().deliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(bundle.finalAcceptanceShareFinalization().deliveryReceiptFresh()).isTrue();
        assertThat(bundle.finalAcceptanceCompletionCloseoutEvidence().status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(bundle.finalAcceptanceCompletionCloseoutEvidence().closed()).isTrue();
        assertThat(bundle.finalAcceptanceCompletionCloseoutEvidence().summary())
                .isEqualTo("PatchPilot final acceptance completion is closed with accepted certificates, finalized sharing, and fresh completion delivery proof.");
        assertThat(bundle.finalAcceptanceCompletionCloseoutEvidence().nextAction())
                .isEqualTo("Use this closeout report as the final external-review completion record.");
        assertThat(bundle.finalAcceptanceCompletionCloseoutEvidence().latestTaskId()).isEqualTo("task-2");
        assertThat(bundle.finalAcceptanceCompletionCloseoutEvidence().latestPullRequestUrl())
                .isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(bundle.finalAcceptanceCompletionCloseoutEvidence().latestCompletionArchiveId())
                .isEqualTo("final-acceptance-completion-archive-1");
        assertThat(bundle.finalAcceptanceCompletionCloseoutEvidence().latestCompletionEvidenceDeliveryReceiptId())
                .isEqualTo("final-acceptance-completion-evidence-delivery-receipt-1");
        assertThat(bundle.finalAcceptanceCompletionCloseoutEvidence().deliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(bundle.finalAcceptanceCompletionCloseoutEvidence().downloadActions()).containsExactly(
                "Download final acceptance completion closeout report.",
                "Download final acceptance completion evidence bundle."
        );
        assertThat(bundle.finalAcceptanceCompletionCloseoutArchiveEvidence().status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(bundle.finalAcceptanceCompletionCloseoutArchiveEvidence().archived()).isTrue();
        assertThat(bundle.finalAcceptanceCompletionCloseoutArchiveEvidence().closed()).isTrue();
        assertThat(bundle.finalAcceptanceCompletionCloseoutArchiveEvidence().summary())
                .isEqualTo("Latest final acceptance completion closeout archive is closed and ready.");
        assertThat(bundle.finalAcceptanceCompletionCloseoutArchiveEvidence().nextAction())
                .isEqualTo("Use the archived final acceptance completion closeout as the frozen external-review completion record.");
        assertThat(bundle.finalAcceptanceCompletionCloseoutArchiveEvidence().archiveCount()).isEqualTo(1);
        assertThat(bundle.finalAcceptanceCompletionCloseoutArchiveEvidence().latestArchiveId())
                .isEqualTo("final-acceptance-completion-closeout-archive-1");
        assertThat(bundle.finalAcceptanceCompletionCloseoutArchiveEvidence().latestCompletionArchiveId())
                .isEqualTo("final-acceptance-completion-archive-1");
        assertThat(bundle.finalAcceptanceCompletionCloseoutArchiveEvidence().latestCompletionEvidenceDeliveryReceiptId())
                .isEqualTo("final-acceptance-completion-evidence-delivery-receipt-1");
        assertThat(bundle.finalAcceptanceCompletionCloseoutArchiveEvidence().latestTaskId()).isEqualTo("task-2");
        assertThat(bundle.finalAcceptanceCompletionCloseoutArchiveEvidence().latestPullRequestUrl())
                .isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(bundle.finalAcceptanceCompletionCloseoutArchiveEvidence().downloadActions()).containsExactly(
                "Download final acceptance completion closeout archive final-acceptance-completion-closeout-archive-1.",
                "Download linked final acceptance completion archive final-acceptance-completion-archive-1.",
                "Download final acceptance completion evidence delivery receipt final-acceptance-completion-evidence-delivery-receipt-1."
        );
        assertThat(bundle.finalExternalReviewEvidencePackage().status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(bundle.finalExternalReviewEvidencePackage().readyForExternalReview()).isTrue();
        assertThat(bundle.finalExternalReviewEvidencePackage().summary())
                .isEqualTo("PatchPilot final external-review evidence package is ready.");
        assertThat(bundle.finalExternalReviewEvidencePackage().nextAction())
                .isEqualTo("Share this package with reviewers as the frozen external-review record.");
        assertThat(bundle.finalExternalReviewEvidencePackage().latestTaskId()).isEqualTo("task-2");
        assertThat(bundle.finalExternalReviewEvidencePackage().latestPullRequestUrl())
                .isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(bundle.finalExternalReviewEvidencePackage().completionArchiveId())
                .isEqualTo("final-acceptance-completion-archive-1");
        assertThat(bundle.finalExternalReviewEvidencePackage().completionEvidenceDeliveryReceiptId())
                .isEqualTo("final-acceptance-completion-evidence-delivery-receipt-1");
        assertThat(bundle.finalExternalReviewEvidencePackage().closeoutArchiveId())
                .isEqualTo("final-acceptance-completion-closeout-archive-1");
        assertThat(bundle.finalExternalReviewEvidencePackage().downloadActions()).containsExactly(
                "Download final external-review evidence package.",
                "Download final acceptance completion closeout archive final-acceptance-completion-closeout-archive-1."
        );
        assertThat(bundle.finalExternalReviewEvidencePackageArchiveEvidence().status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(bundle.finalExternalReviewEvidencePackageArchiveEvidence().archived()).isTrue();
        assertThat(bundle.finalExternalReviewEvidencePackageArchiveEvidence().readyForExternalReview()).isTrue();
        assertThat(bundle.finalExternalReviewEvidencePackageArchiveEvidence().summary())
                .isEqualTo("Latest final external-review evidence package archive is ready for external review.");
        assertThat(bundle.finalExternalReviewEvidencePackageArchiveEvidence().nextAction())
                .isEqualTo("Use the archived final external-review evidence package as the frozen reviewer-facing record.");
        assertThat(bundle.finalExternalReviewEvidencePackageArchiveEvidence().archiveCount()).isEqualTo(1);
        assertThat(bundle.finalExternalReviewEvidencePackageArchiveEvidence().latestArchiveId())
                .isEqualTo("final-external-review-package-archive-1");
        assertThat(bundle.finalExternalReviewEvidencePackageArchiveEvidence().latestCloseoutArchiveId())
                .isEqualTo("final-acceptance-completion-closeout-archive-1");
        assertThat(bundle.finalExternalReviewEvidencePackageArchiveEvidence().latestCompletionArchiveId())
                .isEqualTo("final-acceptance-completion-archive-1");
        assertThat(bundle.finalExternalReviewEvidencePackageArchiveEvidence().latestCompletionEvidenceDeliveryReceiptId())
                .isEqualTo("final-acceptance-completion-evidence-delivery-receipt-1");
        assertThat(bundle.finalExternalReviewEvidencePackageArchiveEvidence().latestTaskId()).isEqualTo("task-2");
        assertThat(bundle.finalExternalReviewEvidencePackageArchiveEvidence().latestPullRequestUrl())
                .isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(bundle.finalExternalReviewEvidencePackageArchiveEvidence().downloadActions()).containsExactly(
                "Download final external-review evidence package archive final-external-review-package-archive-1.",
                "Download final acceptance completion closeout archive final-acceptance-completion-closeout-archive-1.",
                "Download final acceptance completion archive final-acceptance-completion-archive-1.",
                "Download final acceptance completion evidence delivery receipt final-acceptance-completion-evidence-delivery-receipt-1."
        );
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence().status())
                .isEqualTo(DemoReadinessStatus.READY);
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence().recorded()).isTrue();
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence().fresh()).isTrue();
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence().freshness()).isEqualTo("FRESH");
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence().summary())
                .isEqualTo("Latest final external-review package delivery receipt is fresh.");
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence().nextAction())
                .isEqualTo("Use the delivery receipt as proof that the frozen final external-review package was shared.");
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence().receiptCount()).isEqualTo(1);
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence().latestReceiptId())
                .isEqualTo("final-external-review-package-delivery-receipt-1");
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence().latestPackageArchiveId())
                .isEqualTo("final-external-review-package-archive-1");
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence().latestCloseoutArchiveId())
                .isEqualTo("final-acceptance-completion-closeout-archive-1");
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence().latestCompletionArchiveId())
                .isEqualTo("final-acceptance-completion-archive-1");
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence()
                .latestCompletionEvidenceDeliveryReceiptId())
                .isEqualTo("final-acceptance-completion-evidence-delivery-receipt-1");
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence().latestTaskId()).isEqualTo("task-2");
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence().latestPullRequestUrl())
                .isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence().latestDeliveryTarget())
                .isEqualTo("reviewer@example.com");
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence().latestDeliveryChannel())
                .isEqualTo("email");
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence().latestDeliveredAt())
                .isEqualTo(Instant.parse("2026-06-29T05:00:00Z"));
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence().downloadActions()).containsExactly(
                "Download final external-review package delivery receipt final-external-review-package-delivery-receipt-1."
        );
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryFinalization().status())
                .isEqualTo(DemoReadinessStatus.READY);
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryFinalization().finalized()).isTrue();
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryFinalization().summary())
                .isEqualTo("Final external-review package delivery is finalized with a fresh package delivery receipt.");
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryFinalization().latestArchiveId())
                .isEqualTo("final-external-review-package-archive-1");
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryFinalization().latestDeliveryReceiptId())
                .isEqualTo("final-external-review-package-delivery-receipt-1");
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryFinalization().deliveryReceiptFreshness())
                .isEqualTo("FRESH");
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryFinalization().downloadActions()).containsExactly(
                "Download final external-review package delivery finalization report.",
                "Download final external-review package archive final-external-review-package-archive-1.",
                "Download final external-review package delivery receipt final-external-review-package-delivery-receipt-1."
        );
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence().status())
                .isEqualTo(DemoReadinessStatus.READY);
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence().archived()).isTrue();
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence().finalized()).isTrue();
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence().latestArchiveId())
                .isEqualTo("final-external-review-package-delivery-finalization-archive-1");
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence()
                .latestPackageArchiveId())
                .isEqualTo("final-external-review-package-archive-1");
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence()
                .latestDeliveryReceiptId())
                .isEqualTo("final-external-review-package-delivery-receipt-1");
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence().downloadActions())
                .containsExactly(
                        "Download final external-review package delivery finalization archive final-external-review-package-delivery-finalization-archive-1.",
                        "Download final external-review package archive final-external-review-package-archive-1.",
                        "Download final external-review package delivery receipt final-external-review-package-delivery-receipt-1."
                );
        assertThat(bundle.finalExternalReviewReleaseBundle().status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(bundle.finalExternalReviewReleaseBundle().releaseReady()).isTrue();
        assertThat(bundle.finalExternalReviewReleaseBundle().latestCertificateArchiveId())
                .isEqualTo("final-external-review-delivery-certificate-archive-1");
        assertThat(bundle.finalExternalReviewReleaseBundle().latestPackageArchiveId())
                .isEqualTo("final-external-review-package-archive-1");
        assertThat(bundle.finalExternalReviewReleaseBundle().latestDeliveryReceiptId())
                .isEqualTo("final-external-review-package-delivery-receipt-1");
        assertThat(bundle.finalExternalReviewReleaseBundle().requiredAttachments()).containsExactly(
                "Final external-review delivery certificate archive final-external-review-delivery-certificate-archive-1.",
                "Final external-review package delivery finalization archive final-external-review-package-delivery-finalization-archive-1.",
                "Final external-review package archive final-external-review-package-archive-1.",
                "Final external-review package delivery receipt final-external-review-package-delivery-receipt-1."
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
                DemoEvidenceBundleServiceTests::finalAcceptanceShareFinalizationReady,
                DemoEvidenceBundleServiceTests::finalAcceptanceCompletionCloseoutReady,
                () -> List.of(launchAcceptanceCloseoutArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(launchAcceptanceCertificateArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(taskEvidenceAcceptanceCertificateArchive("READY", true)),
                () -> List.of(finalHandoffReportPackageArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(finalAcceptanceCompletionCloseoutArchive(DemoReadinessStatus.READY, true)),
                DemoEvidenceBundleServiceTests::finalExternalReviewEvidencePackageReady,
                () -> List.of(finalExternalReviewEvidencePackageArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(finalExternalReviewEvidencePackageDeliveryReceipt()),
                DemoEvidenceBundleServiceTests::finalExternalReviewEvidencePackageDeliveryFinalizationReady,
                () -> List.of(finalExternalReviewEvidencePackageDeliveryFinalizationArchive()),
                DemoEvidenceBundleServiceTests::finalExternalReviewReleaseBundleReady
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
        assertThat(bundle.launchAcceptanceCertificateEvidence().certified()).isTrue();
        assertThat(bundle.taskEvidenceAcceptanceCertificateEvidence().certified()).isTrue();
        assertThat(bundle.finalHandoffReportPackageArchiveEvidence().downloadReady()).isTrue();
        assertThat(bundle.finalAcceptanceShareFinalization().finalized()).isTrue();
        assertThat(bundle.finalAcceptanceShareFinalization().deliveryReceiptFresh()).isTrue();
        assertThat(bundle.finalAcceptanceCompletionCloseoutEvidence().closed()).isTrue();
        assertThat(bundle.finalAcceptanceCompletionCloseoutArchiveEvidence().closed()).isTrue();
        assertThat(bundle.finalExternalReviewEvidencePackage().readyForExternalReview()).isTrue();
        assertThat(bundle.finalExternalReviewEvidencePackageArchiveEvidence().readyForExternalReview()).isTrue();
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence().fresh()).isTrue();
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryFinalization().finalized()).isTrue();
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence().finalized()).isTrue();
        assertThat(bundle.finalExternalReviewReleaseBundle().releaseReady()).isTrue();
        assertThat(bundle.nextActions()).containsExactly("Use this evidence bundle as the live demo baseline.");
    }

    @Test
    void should_require_final_external_review_release_bundle_before_reporting_bundle_ready() {
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
                DemoEvidenceBundleServiceTests::finalAcceptanceShareFinalizationReady,
                DemoEvidenceBundleServiceTests::finalAcceptanceCompletionCloseoutReady,
                () -> List.of(launchAcceptanceCloseoutArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(launchAcceptanceCertificateArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(taskEvidenceAcceptanceCertificateArchive("READY", true)),
                () -> List.of(finalHandoffReportPackageArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(finalAcceptanceCompletionCloseoutArchive(DemoReadinessStatus.READY, true)),
                DemoEvidenceBundleServiceTests::finalExternalReviewEvidencePackageReady,
                () -> List.of(finalExternalReviewEvidencePackageArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(finalExternalReviewEvidencePackageDeliveryReceipt()),
                DemoEvidenceBundleServiceTests::finalExternalReviewEvidencePackageDeliveryFinalizationReady,
                () -> List.of(finalExternalReviewEvidencePackageDeliveryFinalizationArchive()),
                DemoEvidenceBundleServiceTests::finalExternalReviewReleaseBundleMissing
        );

        DemoEvidenceBundleVo bundle = service.getEvidenceBundle();

        assertThat(bundle.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(bundle.summary()).isEqualTo("Demo evidence bundle needs attention.");
        assertThat(bundle.finalExternalReviewReleaseBundle().status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(bundle.finalExternalReviewReleaseBundle().releaseReady()).isFalse();
        assertThat(bundle.finalExternalReviewReleaseBundle().summary())
                .isEqualTo("No final external-review delivery certificate archive is available for release.");
        assertThat(bundle.finalExternalReviewReleaseBundle().nextAction())
                .isEqualTo("Archive the certified final external-review delivery certificate, then download the release bundle.");
        assertThat(bundle.nextActions()).containsExactly(
                "Archive the certified final external-review delivery certificate, then download the release bundle."
        );
    }

    @Test
    void should_require_final_external_review_package_delivery_finalization_archive_before_reporting_bundle_ready() {
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
                DemoEvidenceBundleServiceTests::finalAcceptanceShareFinalizationReady,
                DemoEvidenceBundleServiceTests::finalAcceptanceCompletionCloseoutReady,
                () -> List.of(launchAcceptanceCloseoutArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(launchAcceptanceCertificateArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(taskEvidenceAcceptanceCertificateArchive("READY", true)),
                () -> List.of(finalHandoffReportPackageArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(finalAcceptanceCompletionCloseoutArchive(DemoReadinessStatus.READY, true)),
                DemoEvidenceBundleServiceTests::finalExternalReviewEvidencePackageReady,
                () -> List.of(finalExternalReviewEvidencePackageArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(finalExternalReviewEvidencePackageDeliveryReceipt()),
                DemoEvidenceBundleServiceTests::finalExternalReviewEvidencePackageDeliveryFinalizationReady,
                List::of
        );

        DemoEvidenceBundleVo bundle = service.getEvidenceBundle();

        assertThat(bundle.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(bundle.summary()).isEqualTo("Demo evidence bundle needs attention.");
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryFinalization().status())
                .isEqualTo(DemoReadinessStatus.READY);
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence().status())
                .isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence().archived())
                .isFalse();
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence().finalized())
                .isFalse();
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence().summary())
                .isEqualTo("No final external-review package delivery finalization archive is available.");
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence().nextAction())
                .isEqualTo("Archive the READY final external-review package delivery finalization.");
        assertThat(bundle.nextActions()).containsExactly(
                "Archive the READY final external-review package delivery finalization."
        );
    }

    @Test
    void should_require_final_external_review_package_archive_before_reporting_bundle_ready() {
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
                DemoEvidenceBundleServiceTests::finalAcceptanceShareFinalizationReady,
                DemoEvidenceBundleServiceTests::finalAcceptanceCompletionCloseoutReady,
                () -> List.of(launchAcceptanceCloseoutArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(launchAcceptanceCertificateArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(taskEvidenceAcceptanceCertificateArchive("READY", true)),
                () -> List.of(finalHandoffReportPackageArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(finalAcceptanceCompletionCloseoutArchive(DemoReadinessStatus.READY, true)),
                DemoEvidenceBundleServiceTests::finalExternalReviewEvidencePackageReady,
                List::of,
                () -> List.of(finalExternalReviewEvidencePackageDeliveryReceipt()),
                DemoEvidenceBundleServiceTests::finalExternalReviewEvidencePackageDeliveryFinalizationReady,
                List::of
        );

        DemoEvidenceBundleVo bundle = service.getEvidenceBundle();

        assertThat(bundle.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(bundle.summary()).isEqualTo("Demo evidence bundle needs attention.");
        assertThat(bundle.finalExternalReviewEvidencePackageArchiveEvidence().status())
                .isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(bundle.finalExternalReviewEvidencePackageArchiveEvidence().archived()).isFalse();
        assertThat(bundle.finalExternalReviewEvidencePackageArchiveEvidence().readyForExternalReview()).isFalse();
        assertThat(bundle.finalExternalReviewEvidencePackageArchiveEvidence().summary())
                .isEqualTo("No final external-review evidence package archive is available.");
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence().status())
                .isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence().recorded()).isFalse();
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence().freshness()).isEqualTo("MISSING");
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryReceiptEvidence().summary())
                .isEqualTo("No final external-review package delivery receipt is available.");
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence().status())
                .isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence().archived()).isFalse();
        assertThat(bundle.finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence().summary())
                .isEqualTo("No final external-review package delivery finalization archive is available.");
        assertThat(bundle.nextActions()).containsExactly(
                "Archive the final external-review evidence package after it is READY.",
                "Share the latest final external-review package archive and record a delivery receipt.",
                "Archive the READY final external-review package delivery finalization."
        );
    }

    @Test
    void should_require_final_external_review_package_before_reporting_bundle_ready() {
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
                DemoEvidenceBundleServiceTests::finalAcceptanceShareFinalizationReady,
                DemoEvidenceBundleServiceTests::finalAcceptanceCompletionCloseoutReady,
                () -> List.of(launchAcceptanceCloseoutArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(launchAcceptanceCertificateArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(taskEvidenceAcceptanceCertificateArchive("READY", true)),
                () -> List.of(finalHandoffReportPackageArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(finalAcceptanceCompletionCloseoutArchive(DemoReadinessStatus.READY, true)),
                DemoEvidenceBundleServiceTests::finalExternalReviewEvidencePackageWaitingForArchive,
                () -> List.of(finalExternalReviewEvidencePackageArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(finalExternalReviewEvidencePackageDeliveryReceipt())
        );

        DemoEvidenceBundleVo bundle = service.getEvidenceBundle();

        assertThat(bundle.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(bundle.summary()).isEqualTo("Demo evidence bundle needs attention.");
        assertThat(bundle.finalExternalReviewEvidencePackage().status())
                .isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(bundle.finalExternalReviewEvidencePackage().readyForExternalReview()).isFalse();
        assertThat(bundle.finalExternalReviewEvidencePackage().summary())
                .isEqualTo("PatchPilot final external-review evidence package is waiting for a frozen closeout archive.");
        assertThat(bundle.nextActions()).containsExactly(
                "Archive the READY final acceptance completion closeout before sharing the final external-review package."
        );
    }

    @Test
    void should_require_final_acceptance_completion_closeout_archive_before_reporting_bundle_ready() {
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
                DemoEvidenceBundleServiceTests::finalAcceptanceShareFinalizationReady,
                DemoEvidenceBundleServiceTests::finalAcceptanceCompletionCloseoutReady,
                () -> List.of(launchAcceptanceCloseoutArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(launchAcceptanceCertificateArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(taskEvidenceAcceptanceCertificateArchive("READY", true)),
                () -> List.of(finalHandoffReportPackageArchive(DemoReadinessStatus.READY, true)),
                List::of
        );

        DemoEvidenceBundleVo bundle = service.getEvidenceBundle();

        assertThat(bundle.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(bundle.summary()).isEqualTo("Demo evidence bundle needs attention.");
        assertThat(bundle.finalAcceptanceCompletionCloseoutArchiveEvidence().status())
                .isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(bundle.finalAcceptanceCompletionCloseoutArchiveEvidence().archived()).isFalse();
        assertThat(bundle.finalAcceptanceCompletionCloseoutArchiveEvidence().closed()).isFalse();
        assertThat(bundle.finalAcceptanceCompletionCloseoutArchiveEvidence().summary())
                .isEqualTo("No final acceptance completion closeout archive is available.");
        assertThat(bundle.finalAcceptanceCompletionCloseoutArchiveEvidence().nextAction())
                .isEqualTo("Archive the final acceptance completion closeout after it is READY and closed.");
        assertThat(bundle.nextActions()).containsExactly(
                "Archive the final acceptance completion closeout after it is READY and closed."
        );
    }

    @Test
    void should_require_final_acceptance_completion_closeout_before_reporting_bundle_ready() {
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
                DemoEvidenceBundleServiceTests::finalAcceptanceShareFinalizationReady,
                DemoEvidenceBundleServiceTests::finalAcceptanceCompletionCloseoutNeedsAttention,
                () -> List.of(launchAcceptanceCloseoutArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(launchAcceptanceCertificateArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(taskEvidenceAcceptanceCertificateArchive("READY", true)),
                () -> List.of(finalHandoffReportPackageArchive(DemoReadinessStatus.READY, true))
        );

        DemoEvidenceBundleVo bundle = service.getEvidenceBundle();

        assertThat(bundle.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(bundle.summary()).isEqualTo("Demo evidence bundle needs attention.");
        assertThat(bundle.finalAcceptanceCompletionCloseoutEvidence().status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(bundle.finalAcceptanceCompletionCloseoutEvidence().closed()).isFalse();
        assertThat(bundle.finalAcceptanceCompletionCloseoutEvidence().summary())
                .isEqualTo("PatchPilot final acceptance completion closeout needs a fresh completion evidence delivery finalization.");
        assertThat(bundle.finalAcceptanceCompletionCloseoutEvidence().nextAction())
                .isEqualTo("Record a fresh completion evidence delivery receipt, then download the closeout report again.");
        assertThat(bundle.nextActions()).containsExactly(
                "Record a fresh completion evidence delivery receipt, then download the closeout report again."
        );
    }

    @Test
    void should_require_final_acceptance_share_finalization_before_reporting_bundle_ready() {
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
                DemoEvidenceBundleServiceTests::finalAcceptanceShareFinalizationMissingReceipt,
                DemoEvidenceBundleServiceTests::finalAcceptanceCompletionCloseoutReady,
                () -> List.of(launchAcceptanceCloseoutArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(launchAcceptanceCertificateArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(taskEvidenceAcceptanceCertificateArchive("READY", true)),
                () -> List.of(finalHandoffReportPackageArchive(DemoReadinessStatus.READY, true))
        );

        DemoEvidenceBundleVo bundle = service.getEvidenceBundle();

        assertThat(bundle.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(bundle.summary()).isEqualTo("Demo evidence bundle needs attention.");
        assertThat(bundle.finalAcceptanceShareFinalization().status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(bundle.finalAcceptanceShareFinalization().finalized()).isFalse();
        assertThat(bundle.finalAcceptanceShareFinalization().deliveryReceiptFreshness()).isEqualTo("MISSING");
        assertThat(bundle.finalAcceptanceShareFinalization().latestDeliveryReceiptId()).isNull();
        assertThat(bundle.nextActions()).containsExactly(
                "Send the final acceptance share package, record a delivery receipt, then download the finalization report."
        );
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
                DemoEvidenceBundleServiceTests::finalAcceptanceShareFinalizationReady,
                DemoEvidenceBundleServiceTests::finalAcceptanceCompletionCloseoutReady,
                List::of,
                () -> List.of(launchAcceptanceCertificateArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(taskEvidenceAcceptanceCertificateArchive("READY", true)),
                () -> List.of(finalHandoffReportPackageArchive(DemoReadinessStatus.READY, true))
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

    @Test
    void should_require_final_acceptance_certificate_archive_before_reporting_bundle_ready() {
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
                DemoEvidenceBundleServiceTests::finalAcceptanceShareFinalizationReady,
                DemoEvidenceBundleServiceTests::finalAcceptanceCompletionCloseoutReady,
                () -> List.of(launchAcceptanceCloseoutArchive(DemoReadinessStatus.READY, true)),
                List::of,
                () -> List.of(taskEvidenceAcceptanceCertificateArchive("READY", true)),
                () -> List.of(finalHandoffReportPackageArchive(DemoReadinessStatus.READY, true))
        );

        DemoEvidenceBundleVo bundle = service.getEvidenceBundle();

        assertThat(bundle.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(bundle.summary()).isEqualTo("Demo evidence bundle needs attention.");
        assertThat(bundle.launchAcceptanceCloseoutEvidence().status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(bundle.launchAcceptanceCertificateEvidence().status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(bundle.launchAcceptanceCertificateEvidence().archived()).isFalse();
        assertThat(bundle.launchAcceptanceCertificateEvidence().certified()).isFalse();
        assertThat(bundle.launchAcceptanceCertificateEvidence().summary())
                .isEqualTo("No launch acceptance certificate archive is available.");
        assertThat(bundle.launchAcceptanceCertificateEvidence().nextAction())
                .isEqualTo("Archive the final launch acceptance certificate after the launch acceptance closeout is certified.");
        assertThat(bundle.nextActions()).containsExactly(
                "Archive the final launch acceptance certificate after the launch acceptance closeout is certified."
        );
    }

    @Test
    void should_require_task_evidence_acceptance_certificate_archive_before_reporting_bundle_ready() {
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
                DemoEvidenceBundleServiceTests::finalAcceptanceShareFinalizationReady,
                DemoEvidenceBundleServiceTests::finalAcceptanceCompletionCloseoutReady,
                () -> List.of(launchAcceptanceCloseoutArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(launchAcceptanceCertificateArchive(DemoReadinessStatus.READY, true)),
                List::of,
                () -> List.of(finalHandoffReportPackageArchive(DemoReadinessStatus.READY, true))
        );

        DemoEvidenceBundleVo bundle = service.getEvidenceBundle();

        assertThat(bundle.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(bundle.summary()).isEqualTo("Demo evidence bundle needs attention.");
        assertThat(bundle.taskEvidenceAcceptanceCertificateEvidence().status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(bundle.taskEvidenceAcceptanceCertificateEvidence().archived()).isFalse();
        assertThat(bundle.taskEvidenceAcceptanceCertificateEvidence().certified()).isFalse();
        assertThat(bundle.taskEvidenceAcceptanceCertificateEvidence().summary())
                .isEqualTo("No task evidence acceptance certificate archive is available.");
        assertThat(bundle.taskEvidenceAcceptanceCertificateEvidence().nextAction())
                .isEqualTo("Archive a certified task evidence acceptance certificate after final task evidence closeout.");
        assertThat(bundle.nextActions()).containsExactly(
                "Archive a certified task evidence acceptance certificate after final task evidence closeout."
        );
    }

    @Test
    void should_require_final_handoff_report_package_archive_before_reporting_bundle_ready() {
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
                DemoEvidenceBundleServiceTests::finalAcceptanceShareFinalizationReady,
                DemoEvidenceBundleServiceTests::finalAcceptanceCompletionCloseoutReady,
                () -> List.of(launchAcceptanceCloseoutArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(launchAcceptanceCertificateArchive(DemoReadinessStatus.READY, true)),
                () -> List.of(taskEvidenceAcceptanceCertificateArchive("READY", true)),
                List::of
        );

        DemoEvidenceBundleVo bundle = service.getEvidenceBundle();

        assertThat(bundle.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(bundle.summary()).isEqualTo("Demo evidence bundle needs attention.");
        assertThat(bundle.finalHandoffReportPackageArchiveEvidence().status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(bundle.finalHandoffReportPackageArchiveEvidence().archived()).isFalse();
        assertThat(bundle.finalHandoffReportPackageArchiveEvidence().downloadReady()).isFalse();
        assertThat(bundle.finalHandoffReportPackageArchiveEvidence().summary())
                .isEqualTo("No final handoff report package archive is available.");
        assertThat(bundle.finalHandoffReportPackageArchiveEvidence().nextAction())
                .isEqualTo("Archive the final handoff report package after the post-demo handoff package is finalized.");
        assertThat(bundle.finalHandoffReportPackageArchiveEvidence().downloadActions()).containsExactly(
                "Archive the final handoff report package before using the evidence bundle as post-demo closeout proof."
        );
        assertThat(bundle.nextActions()).containsExactly(
                "Archive the final handoff report package after the post-demo handoff package is finalized."
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
                DemoReadinessStatus.READY,
                true,
                "final-handoff-report-package-archive-1",
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

    private static DemoFinalAcceptanceShareFinalizationVo finalAcceptanceShareFinalizationReady() {
        return new DemoFinalAcceptanceShareFinalizationVo(
                DemoReadinessStatus.READY,
                true,
                "Final demo acceptance share package is finalized with a fresh delivery receipt.",
                "Use the finalization report as the external-review acceptance delivery record.",
                "final-acceptance-share-package-archive-1",
                "task-2",
                "final-acceptance-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "2026-06-29T03:05:00Z",
                "FRESH",
                true,
                "Latest delivery receipt matches the current final acceptance share package archive.",
                List.of(new DemoFinalAcceptanceShareFinalizationVo.Check(
                        "Final acceptance delivery evidence",
                        DemoReadinessStatus.READY,
                        "Finalization report is ready as the external-review acceptance record.",
                        "Download the finalization report."
                )),
                List.of("Latest delivery receipt final-acceptance-delivery-receipt-1 is fresh for final-acceptance-share-package-archive-1."),
                "# PatchPilot Final Demo Acceptance Share Finalization Gate",
                Instant.parse("2026-06-29T03:30:00Z")
        );
    }

    private static DemoFinalAcceptanceShareFinalizationVo finalAcceptanceShareFinalizationMissingReceipt() {
        return new DemoFinalAcceptanceShareFinalizationVo(
                DemoReadinessStatus.NEEDS_ATTENTION,
                false,
                "Final demo acceptance share package is send-ready but final delivery evidence is not current.",
                "Send the final acceptance share package, record a delivery receipt, then download the finalization report.",
                "final-acceptance-share-package-archive-1",
                "task-2",
                null,
                null,
                null,
                null,
                "MISSING",
                false,
                "No delivery receipt has been recorded for the current final acceptance share package.",
                List.of(new DemoFinalAcceptanceShareFinalizationVo.Check(
                        "Delivery receipt freshness",
                        DemoReadinessStatus.NEEDS_ATTENTION,
                        "No fresh final acceptance delivery receipt is available.",
                        "Record a final acceptance delivery receipt."
                )),
                List.of("No fresh delivery receipt is available for final-acceptance-share-package-archive-1."),
                "# PatchPilot Final Demo Acceptance Share Finalization Gate",
                Instant.parse("2026-06-29T03:30:00Z")
        );
    }

    private static DemoFinalAcceptanceCompletionCloseoutVo finalAcceptanceCompletionCloseoutReady() {
        return new DemoFinalAcceptanceCompletionCloseoutVo(
                DemoReadinessStatus.READY,
                true,
                "PatchPilot final acceptance completion is closed with accepted certificates, finalized sharing, and fresh completion delivery proof.",
                "Use this closeout report as the final external-review completion record.",
                "task-2",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "final-acceptance-share-package-archive-1",
                "final-acceptance-completion-archive-1",
                "final-acceptance-completion-evidence-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "2026-06-29T03:45:00Z",
                "FRESH",
                List.of(new DemoFinalAcceptanceCompletionCloseoutVo.Check(
                        "Completion evidence delivery",
                        DemoReadinessStatus.READY,
                        "Completion closeout can be used as the final external-review record.",
                        "No action needed."
                )),
                List.of("Final acceptance completion archive final-acceptance-completion-archive-1 has a fresh evidence delivery receipt."),
                List.of(
                        "Download final acceptance completion closeout report.",
                        "Download final acceptance completion evidence bundle."
                ),
                "Final acceptance completion closeout is read-only and does not mutate tasks, Git, or GitHub.",
                "# PatchPilot Final Acceptance Completion Closeout",
                Instant.parse("2026-06-29T04:00:00Z")
        );
    }

    private static DemoFinalAcceptanceCompletionCloseoutVo finalAcceptanceCompletionCloseoutNeedsAttention() {
        return new DemoFinalAcceptanceCompletionCloseoutVo(
                DemoReadinessStatus.NEEDS_ATTENTION,
                false,
                "PatchPilot final acceptance completion closeout needs a fresh completion evidence delivery finalization.",
                "Record a fresh completion evidence delivery receipt, then download the closeout report again.",
                "task-2",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "final-acceptance-share-package-archive-1",
                "final-acceptance-completion-archive-1",
                null,
                null,
                null,
                null,
                "MISSING",
                List.of(new DemoFinalAcceptanceCompletionCloseoutVo.Check(
                        "Completion evidence delivery",
                        DemoReadinessStatus.NEEDS_ATTENTION,
                        "No fresh completion evidence delivery receipt is available.",
                        "Record a fresh completion evidence delivery receipt."
                )),
                List.of("Final acceptance completion evidence still needs a delivery receipt."),
                List.of("Download the closeout report after the completion evidence delivery receipt is fresh."),
                "Final acceptance completion closeout is read-only and does not mutate tasks, Git, or GitHub.",
                "# PatchPilot Final Acceptance Completion Closeout",
                Instant.parse("2026-06-29T04:00:00Z")
        );
    }

    private static DemoFinalAcceptanceCompletionCloseoutArchiveVo finalAcceptanceCompletionCloseoutArchive(
            DemoReadinessStatus status,
            boolean closed
    ) {
        return new DemoFinalAcceptanceCompletionCloseoutArchiveVo(
                "final-acceptance-completion-closeout-archive-1",
                status,
                closed,
                "PatchPilot final acceptance completion closeout is archived.",
                "Use this archived closeout as the frozen external-review completion record.",
                "task-2",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "final-acceptance-share-package-archive-1",
                "final-acceptance-completion-archive-1",
                "final-acceptance-completion-evidence-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "2026-06-29T03:45:00Z",
                "FRESH",
                List.of("Final acceptance completion closeout archive is ready."),
                List.of(
                        "Download final acceptance completion closeout archive final-acceptance-completion-closeout-archive-1.",
                        "Download linked final acceptance completion archive final-acceptance-completion-archive-1.",
                        "Download final acceptance completion evidence delivery receipt final-acceptance-completion-evidence-delivery-receipt-1."
                ),
                "Final acceptance completion closeout archive evidence is read-only.",
                "# PatchPilot Final Acceptance Completion Closeout Archive",
                Instant.parse("2026-06-29T04:00:00Z"),
                Instant.parse("2026-06-29T04:15:00Z")
        );
    }

    private static DemoFinalExternalReviewEvidencePackageVo finalExternalReviewEvidencePackageReady() {
        return new DemoFinalExternalReviewEvidencePackageVo(
                DemoReadinessStatus.READY,
                true,
                "PatchPilot final external-review evidence package is ready.",
                "Share this package with reviewers as the frozen external-review record.",
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                "task-2",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "final-acceptance-share-package-archive-1",
                "final-acceptance-completion-archive-1",
                "final-acceptance-completion-evidence-delivery-receipt-1",
                "final-acceptance-completion-closeout-archive-1",
                "reviewer@example.com",
                "email",
                "2026-06-29T03:45:00Z",
                "FRESH",
                Instant.parse("2026-06-29T04:15:00Z"),
                Instant.parse("2026-06-29T04:30:00Z"),
                List.of(new DemoFinalExternalReviewEvidencePackageVo.Check(
                        "Frozen closeout archive",
                        DemoReadinessStatus.READY,
                        "Frozen closeout archive final-acceptance-completion-closeout-archive-1 is closed.",
                        "No action needed."
                )),
                List.of("Frozen closeout archive final-acceptance-completion-closeout-archive-1 is READY and closed."),
                List.of(
                        "Download final external-review evidence package.",
                        "Download final acceptance completion closeout archive final-acceptance-completion-closeout-archive-1."
                ),
                "GET /api/demo/final-external-review-evidence-package is read-only.",
                "# PatchPilot Final External Review Evidence Package"
        );
    }

    private static DemoFinalExternalReviewEvidencePackageArchiveVo finalExternalReviewEvidencePackageArchive(
            DemoReadinessStatus status,
            boolean readyForExternalReview
    ) {
        return new DemoFinalExternalReviewEvidencePackageArchiveVo(
                "final-external-review-package-archive-1",
                status,
                readyForExternalReview,
                status == DemoReadinessStatus.READY
                        ? "PatchPilot final external-review evidence package is ready."
                        : "PatchPilot final external-review evidence package archive is blocked.",
                status == DemoReadinessStatus.READY
                        ? "Share this package with reviewers as the frozen external-review record."
                        : "Resolve final external-review package archive blockers.",
                "task-2",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "final-acceptance-share-package-archive-1",
                "final-acceptance-completion-archive-1",
                "final-acceptance-completion-evidence-delivery-receipt-1",
                "final-acceptance-completion-closeout-archive-1",
                "reviewer@example.com",
                "email",
                "2026-06-29T03:45:00Z",
                "FRESH",
                Instant.parse("2026-06-29T04:15:00Z"),
                List.of("Frozen closeout archive final-acceptance-completion-closeout-archive-1 is READY and closed."),
                List.of(
                        "Download final external-review evidence package.",
                        "Download final acceptance completion closeout archive final-acceptance-completion-closeout-archive-1."
                ),
                "GET /api/demo/final-external-review-evidence-package is read-only.",
                "# PatchPilot Final External Review Evidence Package",
                Instant.parse("2026-06-29T04:30:00Z"),
                Instant.parse("2026-06-29T04:45:00Z")
        );
    }

    private static DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo
    finalExternalReviewEvidencePackageDeliveryReceipt() {
        return new DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo(
                "final-external-review-package-delivery-receipt-1",
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                "final-external-review-package-archive-1",
                "final-acceptance-completion-closeout-archive-1",
                "final-acceptance-completion-archive-1",
                "final-acceptance-completion-evidence-delivery-receipt-1",
                "task-2",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "PatchPilot final external-review evidence package archive was delivered.",
                "Use the delivery receipt as proof that the frozen final external-review package was shared.",
                "email",
                "reviewer@example.com",
                "release-captain",
                "Sent to the reviewer mailbox.",
                Instant.parse("2026-06-29T05:00:00Z"),
                Instant.parse("2026-06-29T05:05:00Z"),
                "# PatchPilot Final External Review Package Delivery Receipt"
        );
    }

    private static DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo
    finalExternalReviewEvidencePackageDeliveryFinalizationReady() {
        return new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo(
                DemoReadinessStatus.READY,
                true,
                "Final external-review package delivery is finalized with a fresh package delivery receipt.",
                "Use the finalization report as proof that the frozen external-review package was delivered.",
                "final-external-review-package-archive-1",
                "final-external-review-package-delivery-receipt-1",
                "final-acceptance-completion-closeout-archive-1",
                "final-acceptance-completion-archive-1",
                "final-acceptance-completion-evidence-delivery-receipt-1",
                "task-2",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "reviewer@example.com",
                "email",
                "2026-06-29T05:00:00Z",
                "FRESH",
                true,
                "Latest package delivery receipt matches the current frozen final external-review package.",
                List.of(new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo.Check(
                        "Final external-review package delivery receipt",
                        DemoReadinessStatus.READY,
                        "Final external-review package delivery receipt final-external-review-package-delivery-receipt-1 is fresh.",
                        "No action needed."
                )),
                List.of("Final external-review package delivery receipt final-external-review-package-delivery-receipt-1 is fresh."),
                List.of(
                        "Download final external-review package delivery finalization report.",
                        "Download final external-review package archive final-external-review-package-archive-1.",
                        "Download final external-review package delivery receipt final-external-review-package-delivery-receipt-1."
                ),
                "GET /api/demo/final-external-review-evidence-package/delivery-finalization is read-only.",
                "# PatchPilot Final External Review Package Delivery Finalization",
                Instant.parse("2026-06-29T05:10:00Z")
        );
    }

    private static DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo
    finalExternalReviewEvidencePackageDeliveryFinalizationArchive() {
        return new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo(
                "final-external-review-package-delivery-finalization-archive-1",
                DemoReadinessStatus.READY,
                true,
                "Final external-review package delivery is finalized with a fresh package delivery receipt.",
                "Use the archived final external-review package delivery finalization as the delivery closure record.",
                "final-external-review-package-archive-1",
                "final-external-review-package-delivery-receipt-1",
                "final-acceptance-completion-closeout-archive-1",
                "final-acceptance-completion-archive-1",
                "final-acceptance-completion-evidence-delivery-receipt-1",
                "task-2",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "reviewer@example.com",
                "email",
                "2026-06-29T05:00:00Z",
                "FRESH",
                true,
                "Latest package delivery receipt matches the current frozen final external-review package.",
                List.of(new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo.Check(
                        "Final external-review package delivery receipt",
                        DemoReadinessStatus.READY,
                        "Final external-review package delivery receipt final-external-review-package-delivery-receipt-1 is fresh.",
                        "No action needed."
                )),
                List.of("Final external-review package delivery receipt final-external-review-package-delivery-receipt-1 is fresh."),
                List.of(
                        "Download final external-review package delivery finalization report.",
                        "Download final external-review package archive final-external-review-package-archive-1.",
                        "Download final external-review package delivery receipt final-external-review-package-delivery-receipt-1."
                ),
                "POST /api/demo/final-external-review-evidence-package/delivery-finalization/archives archives a ready finalization.",
                "# PatchPilot Final External Review Package Delivery Finalization",
                Instant.parse("2026-06-29T05:10:00Z"),
                Instant.parse("2026-06-29T05:15:00Z")
        );
    }

    private static DemoFinalExternalReviewReleaseBundleVo finalExternalReviewReleaseBundleReady() {
        return new DemoFinalExternalReviewReleaseBundleVo(
                DemoReadinessStatus.READY,
                true,
                "PatchPilot final external-review release bundle is ready.",
                "Share the release bundle report and listed attachments with external reviewers.",
                "final-external-review-delivery-certificate-archive-1",
                "final-external-review-package-delivery-finalization-archive-1",
                "final-external-review-package-archive-1",
                "final-external-review-package-delivery-receipt-1",
                "task-2",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "reviewer@example.com",
                "email",
                "2026-06-29T05:00:00Z",
                Instant.parse("2026-06-29T05:30:00Z"),
                Instant.parse("2026-06-29T05:35:00Z"),
                List.of(
                        "Final external-review delivery certificate archive final-external-review-delivery-certificate-archive-1.",
                        "Final external-review package delivery finalization archive final-external-review-package-delivery-finalization-archive-1.",
                        "Final external-review package archive final-external-review-package-archive-1.",
                        "Final external-review package delivery receipt final-external-review-package-delivery-receipt-1."
                ),
                List.of(new DemoFinalExternalReviewReleaseBundleVo.ReleaseCheck(
                        "Final external-review delivery certificate archive",
                        DemoReadinessStatus.READY,
                        "Certified delivery certificate archive final-external-review-delivery-certificate-archive-1 is available.",
                        "No action needed."
                )),
                List.of(
                        "Certified delivery certificate archive final-external-review-delivery-certificate-archive-1 is the release root.",
                        "Package delivery finalization archive final-external-review-package-delivery-finalization-archive-1 is included."
                ),
                List.of(
                        "Download final external-review release bundle report.",
                        "Download final external-review delivery certificate archive final-external-review-delivery-certificate-archive-1.",
                        "Download final external-review package delivery finalization archive final-external-review-package-delivery-finalization-archive-1."
                ),
                "GET /api/demo/final-external-review-release-bundle is read-only.",
                "# PatchPilot Final External Review Release Bundle"
        );
    }

    private static DemoFinalExternalReviewReleaseBundleVo finalExternalReviewReleaseBundleMissing() {
        return new DemoFinalExternalReviewReleaseBundleVo(
                DemoReadinessStatus.NEEDS_ATTENTION,
                false,
                "No final external-review delivery certificate archive is available for release.",
                "Archive the certified final external-review delivery certificate, then download the release bundle.",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                Instant.parse("2026-06-29T05:35:00Z"),
                List.of(),
                List.of(new DemoFinalExternalReviewReleaseBundleVo.ReleaseCheck(
                        "Final external-review delivery certificate archive",
                        DemoReadinessStatus.NEEDS_ATTENTION,
                        "No certified final external-review delivery certificate archive is available.",
                        "Archive a certified final external-review delivery certificate."
                )),
                List.of("No certified final external-review delivery certificate archive is available."),
                List.of("Download the final external-review release bundle after a certified certificate archive exists."),
                "GET /api/demo/final-external-review-release-bundle is read-only.",
                "# PatchPilot Final External Review Release Bundle"
        );
    }

    private static DemoFinalExternalReviewEvidencePackageVo finalExternalReviewEvidencePackageWaitingForArchive() {
        return new DemoFinalExternalReviewEvidencePackageVo(
                DemoReadinessStatus.NEEDS_ATTENTION,
                false,
                "PatchPilot final external-review evidence package is waiting for a frozen closeout archive.",
                "Archive the READY final acceptance completion closeout before sharing the final external-review package.",
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                DemoReadinessStatus.NEEDS_ATTENTION,
                "task-2",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "final-acceptance-share-package-archive-1",
                "final-acceptance-completion-archive-1",
                "final-acceptance-completion-evidence-delivery-receipt-1",
                null,
                "reviewer@example.com",
                "email",
                "2026-06-29T03:45:00Z",
                "FRESH",
                null,
                Instant.parse("2026-06-29T04:30:00Z"),
                List.of(new DemoFinalExternalReviewEvidencePackageVo.Check(
                        "Frozen closeout archive",
                        DemoReadinessStatus.NEEDS_ATTENTION,
                        "No frozen final acceptance completion closeout archive is available.",
                        "Archive the READY final acceptance completion closeout."
                )),
                List.of("No frozen final acceptance completion closeout archive is available."),
                List.of("Download final external-review evidence package."),
                "GET /api/demo/final-external-review-evidence-package is read-only.",
                "# PatchPilot Final External Review Evidence Package"
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
                status,
                accepted,
                accepted ? "final-handoff-report-package-archive-1" : null,
                accepted
                        ? "Latest final handoff report package archive is download-ready and ready."
                        : "No final handoff report package archive evidence recorded.",
                "launch-delivery-receipt-1",
                "Demo reviewer",
                "email",
                "FRESH",
                Instant.parse("2026-06-24T08:00:00Z"),
                "# PatchPilot Launch Acceptance Closeout Archive"
        );
    }

    private static DemoLaunchAcceptanceCertificateArchiveVo launchAcceptanceCertificateArchive(
            DemoReadinessStatus status,
            boolean certified
    ) {
        return new DemoLaunchAcceptanceCertificateArchiveVo(
                "launch-certificate-archive-1",
                status,
                certified,
                "PatchPilot launch acceptance is certified from the latest accepted closeout archive.",
                "Share the certificate and archived closeout report with reviewers.",
                1,
                "launch-closeout-archive-1",
                "launch-evidence-archive-1",
                status,
                certified,
                certified ? "final-handoff-report-package-archive-1" : null,
                certified
                        ? "Latest final handoff report package archive is download-ready and ready."
                        : "No final handoff report package archive evidence recorded.",
                "launch-delivery-receipt-1",
                "demo-session-20260624T003000Z",
                "task-2",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "delivery-1",
                "evaluation-run-2",
                "Demo reviewer",
                "email",
                "FRESH",
                Instant.parse("2026-06-24T08:00:00Z"),
                Instant.parse("2026-06-24T08:25:00Z"),
                Instant.parse("2026-06-24T08:30:00Z"),
                List.of(
                        "Download launch acceptance certificate archive launch-certificate-archive-1.",
                        "Download linked launch acceptance closeout archive launch-closeout-archive-1."
                ),
                "# PatchPilot Launch Acceptance Certificate Archive"
        );
    }

    private static FixTaskEvidencePackageAcceptanceCertificateArchiveVo taskEvidenceAcceptanceCertificateArchive(
            String status,
            boolean certified
    ) {
        return new FixTaskEvidencePackageAcceptanceCertificateArchiveVo(
                "task-evidence-certificate-archive-1",
                status,
                certified,
                "Task evidence acceptance is certified from the latest accepted closeout archive.",
                "Share the certificate and archived closeout report with reviewers.",
                1,
                "task-evidence-closeout-archive-1",
                "task-evidence-archive-1",
                "task-evidence-receipt-1",
                "task-2",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "Demo reviewer",
                "email",
                "FRESH",
                Instant.parse("2026-06-24T09:00:00Z"),
                Instant.parse("2026-06-24T09:25:00Z"),
                Instant.parse("2026-06-24T09:30:00Z"),
                List.of(
                        "Download task evidence acceptance certificate archive task-evidence-certificate-archive-1.",
                        "Download linked task evidence acceptance closeout archive task-evidence-closeout-archive-1."
                ),
                "# PatchPilot Task Evidence Acceptance Certificate Archive"
        );
    }

    private static DemoFinalHandoffReportPackageArchiveVo finalHandoffReportPackageArchive(
            DemoReadinessStatus status,
            boolean downloadReady
    ) {
        return new DemoFinalHandoffReportPackageArchiveVo(
                "final-handoff-package-archive-1",
                status,
                downloadReady,
                "Final demo handoff report package is ready to deliver.",
                "Download this final handoff report package and attach the listed evidence files.",
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                "delivery-receipt-1",
                "task-evidence-certificate-archive-1",
                true,
                List.of("Finalization: READY"),
                List.of("Finalization report"),
                List.of("Confirm no handoff share checklist warnings remain."),
                List.of("Latest delivery receipt delivery-receipt-1 is fresh."),
                List.of("Handoff finalization"),
                "# PatchPilot Final Demo Handoff Report Package",
                Instant.parse("2026-06-24T11:00:00Z"),
                Instant.parse("2026-06-24T11:30:00Z")
        );
    }
}
