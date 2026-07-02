package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoArtifactChainReportVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoCompletionCertificateArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoEvidenceBundleArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoHandoffDeliveryFinalizationArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerLaunchPackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerOutcomeCloseoutArchiveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DemoLiveDemoArtifactChainReportService {

    private static final String READY = "READY";
    private static final String BLOCKED = "BLOCKED";
    private static final String NEEDS_ATTENTION = "NEEDS_ATTENTION";
    private static final String READ_ONLY_CONTRACT =
            "GET /api/demo/live-demo-handoff-package/artifact-chain-report is read-only: "
                    + "it does not create tasks, call the model, run tests, archive records, mutate Git, "
                    + "send messages, or write to GitHub.";

    private final DemoLiveTriggerLaunchPackageArchiveRepository launchRepository;
    private final DemoLiveTriggerOutcomeCloseoutArchiveRepository closeoutRepository;
    private final DemoLiveDemoEvidenceBundleArchiveRepository evidenceRepository;
    private final DemoLiveDemoHandoffDeliveryFinalizationArchiveRepository finalizationRepository;
    private final DemoLiveDemoCompletionCertificateArchiveRepository completionRepository;
    private final Clock clock;

    @Autowired
    public DemoLiveDemoArtifactChainReportService(
            DemoLiveTriggerLaunchPackageArchiveRepository launchRepository,
            DemoLiveTriggerOutcomeCloseoutArchiveRepository closeoutRepository,
            DemoLiveDemoEvidenceBundleArchiveRepository evidenceRepository,
            DemoLiveDemoHandoffDeliveryFinalizationArchiveRepository finalizationRepository,
            DemoLiveDemoCompletionCertificateArchiveRepository completionRepository
    ) {
        this(
                launchRepository,
                closeoutRepository,
                evidenceRepository,
                finalizationRepository,
                completionRepository,
                Clock.systemUTC()
        );
    }

    DemoLiveDemoArtifactChainReportService(
            DemoLiveTriggerLaunchPackageArchiveRepository launchRepository,
            DemoLiveTriggerOutcomeCloseoutArchiveRepository closeoutRepository,
            DemoLiveDemoEvidenceBundleArchiveRepository evidenceRepository,
            DemoLiveDemoHandoffDeliveryFinalizationArchiveRepository finalizationRepository,
            DemoLiveDemoCompletionCertificateArchiveRepository completionRepository,
            Clock clock
    ) {
        this.launchRepository = launchRepository;
        this.closeoutRepository = closeoutRepository;
        this.evidenceRepository = evidenceRepository;
        this.finalizationRepository = finalizationRepository;
        this.completionRepository = completionRepository;
        this.clock = clock;
    }

    public DemoLiveDemoArtifactChainReportVo getReport() {
        Optional<DemoLiveTriggerLaunchPackageArchiveVo> launch = latestLaunch();
        Optional<DemoLiveTriggerOutcomeCloseoutArchiveVo> closeout = latestCloseout();
        Optional<DemoLiveDemoEvidenceBundleArchiveVo> evidence = latestEvidence();
        Optional<DemoLiveDemoHandoffDeliveryFinalizationArchiveVo> finalization = latestFinalization();
        Optional<DemoLiveDemoCompletionCertificateArchiveVo> completion = latestCompletion();

        List<DemoLiveDemoArtifactChainReportVo.Step> steps = steps(
                launch,
                closeout,
                evidence,
                finalization,
                completion
        );
        List<DemoLiveDemoArtifactChainReportVo.Check> checks = checks(
                launch,
                closeout,
                evidence,
                finalization,
                completion
        );
        String status = status(checks);
        boolean complete = READY.equals(status);
        Instant generatedAt = Instant.now(clock);
        List<String> evidenceNotes = evidenceNotes(launch, closeout, evidence, finalization, completion);
        List<String> downloadActions = downloadActions(launch, closeout, evidence, finalization, completion);
        String summary = summary(status);
        String nextAction = nextAction(status);
        DemoLiveDemoArtifactChainReportVo report = new DemoLiveDemoArtifactChainReportVo(
                status,
                complete,
                summary,
                nextAction,
                launch.map(DemoLiveTriggerLaunchPackageArchiveVo::id).orElse(null),
                closeout.map(DemoLiveTriggerOutcomeCloseoutArchiveVo::id).orElse(null),
                evidence.map(DemoLiveDemoEvidenceBundleArchiveVo::id).orElse(null),
                finalization.map(DemoLiveDemoHandoffDeliveryFinalizationArchiveVo::id).orElse(null),
                completion.map(DemoLiveDemoCompletionCertificateArchiveVo::id).orElse(null),
                repository(launch, closeout, evidence, finalization, completion),
                issueNumber(launch, closeout, evidence, finalization, completion),
                issueUrl(launch, closeout, evidence, finalization, completion),
                taskId(closeout, evidence, finalization, completion),
                taskStatus(closeout, evidence, finalization, completion),
                pullRequestUrl(closeout, evidence, finalization, completion),
                steps,
                checks,
                evidenceNotes,
                downloadActions,
                READ_ONLY_CONTRACT,
                generatedAt,
                ""
        );
        return withMarkdown(report);
    }

    private Optional<DemoLiveTriggerLaunchPackageArchiveVo> latestLaunch() {
        return launchRepository.listRecentArchives(1).stream().findFirst();
    }

    private Optional<DemoLiveTriggerOutcomeCloseoutArchiveVo> latestCloseout() {
        return closeoutRepository.listRecentArchives(1).stream().findFirst();
    }

    private Optional<DemoLiveDemoEvidenceBundleArchiveVo> latestEvidence() {
        return evidenceRepository.listRecentArchives(1).stream().findFirst();
    }

    private Optional<DemoLiveDemoHandoffDeliveryFinalizationArchiveVo> latestFinalization() {
        return finalizationRepository.listRecentArchives(1).stream().findFirst();
    }

    private Optional<DemoLiveDemoCompletionCertificateArchiveVo> latestCompletion() {
        return completionRepository.listRecentArchives(1).stream().findFirst();
    }

    private static List<DemoLiveDemoArtifactChainReportVo.Step> steps(
            Optional<DemoLiveTriggerLaunchPackageArchiveVo> launch,
            Optional<DemoLiveTriggerOutcomeCloseoutArchiveVo> closeout,
            Optional<DemoLiveDemoEvidenceBundleArchiveVo> evidence,
            Optional<DemoLiveDemoHandoffDeliveryFinalizationArchiveVo> finalization,
            Optional<DemoLiveDemoCompletionCertificateArchiveVo> completion
    ) {
        return List.of(
                step(
                        "Live trigger launch package archive",
                        launch.map(archive -> readyStatus(archive.status(), archive.readyToPost())).orElse(BLOCKED),
                        launch.map(DemoLiveTriggerLaunchPackageArchiveVo::id).orElse(null),
                        launch.map(DemoLiveTriggerLaunchPackageArchiveVo::summary)
                                .orElse("No live trigger launch package archive is available."),
                        launch.isPresent() ? "Continue to outcome closeout." : "Archive a live trigger launch package."
                ),
                step(
                        "Live trigger outcome closeout archive",
                        closeout.map(archive -> readyStatus(archive.status(), archive.successful())).orElse(BLOCKED),
                        closeout.map(DemoLiveTriggerOutcomeCloseoutArchiveVo::id).orElse(null),
                        closeout.map(DemoLiveTriggerOutcomeCloseoutArchiveVo::summary)
                                .orElse("No live trigger outcome closeout archive is available."),
                        closeout.isPresent() ? "Continue to evidence bundle." : "Archive a live trigger outcome closeout."
                ),
                step(
                        "Live demo evidence bundle archive",
                        evidence.map(archive -> readyStatus(archive.status(), archive.readyForHandoff())).orElse(BLOCKED),
                        evidence.map(DemoLiveDemoEvidenceBundleArchiveVo::id).orElse(null),
                        evidence.map(DemoLiveDemoEvidenceBundleArchiveVo::summary)
                                .orElse("No live demo evidence bundle archive is available."),
                        evidence.isPresent() ? "Continue to handoff finalization." : "Archive a live demo evidence bundle."
                ),
                step(
                        "Live demo handoff delivery finalization archive",
                        finalization.map(archive -> readyStatus(
                                archive.status(),
                                archive.finalized() && archive.deliveryReceiptFresh()
                        )).orElse(BLOCKED),
                        finalization.map(DemoLiveDemoHandoffDeliveryFinalizationArchiveVo::id).orElse(null),
                        finalization.map(DemoLiveDemoHandoffDeliveryFinalizationArchiveVo::summary)
                                .orElse("No live demo handoff delivery finalization archive is available."),
                        finalization.isPresent()
                                ? "Continue to completion certificate."
                                : "Archive a live demo handoff delivery finalization."
                ),
                step(
                        "Live demo completion certificate archive",
                        completion.map(archive -> readyStatus(archive.status(), archive.certified())).orElse(BLOCKED),
                        completion.map(DemoLiveDemoCompletionCertificateArchiveVo::id).orElse(null),
                        completion.map(DemoLiveDemoCompletionCertificateArchiveVo::summary)
                                .orElse("No live demo completion certificate archive is available."),
                        completion.isPresent()
                                ? "Share the artifact chain report."
                                : "Archive a live demo completion certificate."
                )
        );
    }

    private static DemoLiveDemoArtifactChainReportVo.Step step(
            String name,
            String status,
            String artifactId,
            String summary,
            String nextAction
    ) {
        return new DemoLiveDemoArtifactChainReportVo.Step(name, status, artifactId, summary, nextAction);
    }

    private static String readyStatus(String status, boolean ready) {
        return READY.equals(status) && ready ? READY : NEEDS_ATTENTION;
    }

    private static List<DemoLiveDemoArtifactChainReportVo.Check> checks(
            Optional<DemoLiveTriggerLaunchPackageArchiveVo> launch,
            Optional<DemoLiveTriggerOutcomeCloseoutArchiveVo> closeout,
            Optional<DemoLiveDemoEvidenceBundleArchiveVo> evidence,
            Optional<DemoLiveDemoHandoffDeliveryFinalizationArchiveVo> finalization,
            Optional<DemoLiveDemoCompletionCertificateArchiveVo> completion
    ) {
        List<DemoLiveDemoArtifactChainReportVo.Check> checks = new ArrayList<>();
        addPresentCheck(checks, "Launch package archive present", launch.isPresent());
        addPresentCheck(checks, "Outcome closeout archive present", closeout.isPresent());
        addPresentCheck(checks, "Evidence bundle archive present", evidence.isPresent());
        addPresentCheck(checks, "Handoff finalization archive present", finalization.isPresent());
        addPresentCheck(checks, "Completion certificate archive present", completion.isPresent());
        if (launch.isPresent() && closeout.isPresent()) {
            checks.add(equalCheck(
                    "Outcome closeout references launch package",
                    launch.get().id(),
                    closeout.get().launchPackageArchiveId()
            ));
        }
        if (launch.isPresent() && evidence.isPresent()) {
            checks.add(equalCheck(
                    "Evidence bundle references launch package",
                    launch.get().id(),
                    evidence.get().launchPackageArchiveId()
            ));
        }
        if (closeout.isPresent() && evidence.isPresent()) {
            checks.add(equalCheck(
                    "Evidence bundle references outcome closeout",
                    closeout.get().id(),
                    evidence.get().outcomeCloseoutArchiveId()
            ));
        }
        if (evidence.isPresent() && finalization.isPresent()) {
            checks.add(equalCheck(
                    "Handoff finalization references evidence bundle",
                    evidence.get().id(),
                    finalization.get().evidenceBundleArchiveId()
            ));
        }
        if (finalization.isPresent() && completion.isPresent()) {
            checks.add(equalCheck(
                    "Completion certificate references finalization",
                    finalization.get().id(),
                    completion.get().latestFinalizationArchiveId()
            ));
        }
        if (evidence.isPresent() && completion.isPresent()) {
            checks.add(equalCheck(
                    "Completion certificate references evidence bundle",
                    evidence.get().id(),
                    completion.get().evidenceBundleArchiveId()
            ));
        }
        if (finalization.isPresent() && completion.isPresent()) {
            checks.add(equalCheck(
                    "Completion certificate references delivery receipt",
                    finalization.get().latestDeliveryReceiptId(),
                    completion.get().latestDeliveryReceiptId()
            ));
        }
        checks.add(readinessCheck("Launch package readiness", launch.map(archive ->
                READY.equals(archive.status()) && archive.readyToPost()).orElse(false)));
        checks.add(readinessCheck("Outcome closeout success", closeout.map(archive ->
                READY.equals(archive.status()) && archive.successful()).orElse(false)));
        checks.add(readinessCheck("Evidence bundle readiness", evidence.map(archive ->
                READY.equals(archive.status()) && archive.readyForHandoff()).orElse(false)));
        checks.add(readinessCheck("Handoff finalization readiness", finalization.map(archive ->
                READY.equals(archive.status()) && archive.finalized() && archive.deliveryReceiptFresh()).orElse(false)));
        checks.add(readinessCheck("Completion certificate readiness", completion.map(archive ->
                READY.equals(archive.status()) && archive.certified()).orElse(false)));
        return checks;
    }

    private static void addPresentCheck(
            List<DemoLiveDemoArtifactChainReportVo.Check> checks,
            String name,
            boolean present
    ) {
        checks.add(new DemoLiveDemoArtifactChainReportVo.Check(
                name,
                present ? READY : BLOCKED,
                present ? name + "." : name + " is missing.",
                present ? "Continue." : "Create the missing archive."
        ));
    }

    private static DemoLiveDemoArtifactChainReportVo.Check equalCheck(
            String name,
            String expected,
            String actual
    ) {
        boolean matches = expected != null && expected.equals(actual);
        return new DemoLiveDemoArtifactChainReportVo.Check(
                name,
                matches ? READY : NEEDS_ATTENTION,
                matches
                        ? name + "."
                        : "Expected " + valueOrMissing(expected) + " but found " + valueOrMissing(actual) + ".",
                matches ? "Continue." : "Regenerate the affected archive from the latest consistent artifact."
        );
    }

    private static DemoLiveDemoArtifactChainReportVo.Check readinessCheck(String name, boolean ready) {
        return new DemoLiveDemoArtifactChainReportVo.Check(
                name,
                ready ? READY : NEEDS_ATTENTION,
                ready ? name + "." : name + " is not READY.",
                ready ? "Continue." : "Recreate or refresh the affected archive."
        );
    }

    private static String status(List<DemoLiveDemoArtifactChainReportVo.Check> checks) {
        if (checks.stream().anyMatch(check -> BLOCKED.equals(check.status()))) {
            return BLOCKED;
        }
        if (checks.stream().anyMatch(check -> NEEDS_ATTENTION.equals(check.status()))) {
            return NEEDS_ATTENTION;
        }
        return READY;
    }

    private static String summary(String status) {
        return switch (status) {
            case READY -> "PatchPilot live demo artifact chain is complete and consistent.";
            case BLOCKED -> "PatchPilot live demo artifact chain is missing required archives.";
            default -> "PatchPilot live demo artifact chain has inconsistent archive references.";
        };
    }

    private static String nextAction(String status) {
        return switch (status) {
            case READY -> "Share the live demo artifact chain report with reviewers.";
            case BLOCKED -> "Create the missing live demo archives before sharing a final demo completion package.";
            default -> "Regenerate and archive the affected live demo artifacts from the latest consistent launch package.";
        };
    }

    private static List<String> evidenceNotes(
            Optional<DemoLiveTriggerLaunchPackageArchiveVo> launch,
            Optional<DemoLiveTriggerOutcomeCloseoutArchiveVo> closeout,
            Optional<DemoLiveDemoEvidenceBundleArchiveVo> evidence,
            Optional<DemoLiveDemoHandoffDeliveryFinalizationArchiveVo> finalization,
            Optional<DemoLiveDemoCompletionCertificateArchiveVo> completion
    ) {
        List<String> notes = new ArrayList<>();
        launch.ifPresent(archive -> notes.add("Launch package archive " + archive.id() + " is the chain root."));
        closeout.ifPresent(archive -> notes.add("Outcome closeout archive " + archive.id() + " records task "
                + valueOrMissing(archive.taskId()) + "."));
        evidence.ifPresent(archive -> notes.add("Evidence bundle archive " + archive.id()
                + " bridges launch and outcome artifacts."));
        finalization.ifPresent(archive -> notes.add("Handoff finalization archive " + archive.id()
                + " records delivery receipt " + valueOrMissing(archive.latestDeliveryReceiptId()) + "."));
        completion.ifPresent(archive -> notes.add("Completion certificate archive " + archive.id()
                + " closes the same evidence bundle."));
        if (notes.isEmpty()) {
            notes.add("No live demo artifacts have been archived yet.");
        }
        return notes;
    }

    private static List<String> downloadActions(
            Optional<DemoLiveTriggerLaunchPackageArchiveVo> launch,
            Optional<DemoLiveTriggerOutcomeCloseoutArchiveVo> closeout,
            Optional<DemoLiveDemoEvidenceBundleArchiveVo> evidence,
            Optional<DemoLiveDemoHandoffDeliveryFinalizationArchiveVo> finalization,
            Optional<DemoLiveDemoCompletionCertificateArchiveVo> completion
    ) {
        List<String> actions = new ArrayList<>();
        actions.add("Download live demo artifact chain report.");
        launch.ifPresent(archive -> actions.add("Download live trigger launch package archive " + archive.id() + "."));
        closeout.ifPresent(archive -> actions.add("Download live trigger outcome closeout archive " + archive.id() + "."));
        evidence.ifPresent(archive -> actions.add("Download live demo evidence bundle archive " + archive.id() + "."));
        finalization.ifPresent(archive -> actions.add(
                "Download live demo handoff delivery finalization archive " + archive.id() + "."
        ));
        completion.ifPresent(archive -> actions.add(
                "Download live demo completion certificate archive " + archive.id() + "."
        ));
        completion.map(DemoLiveDemoCompletionCertificateArchiveVo::pullRequestUrl)
                .or(() -> finalization.map(DemoLiveDemoHandoffDeliveryFinalizationArchiveVo::pullRequestUrl))
                .or(() -> evidence.map(DemoLiveDemoEvidenceBundleArchiveVo::pullRequestUrl))
                .or(() -> closeout.map(DemoLiveTriggerOutcomeCloseoutArchiveVo::pullRequestUrl))
                .filter(value -> !value.isBlank())
                .ifPresent(url -> actions.add("Open Pull Request " + url + " for review."));
        return actions;
    }

    private static DemoLiveDemoArtifactChainReportVo withMarkdown(DemoLiveDemoArtifactChainReportVo report) {
        return new DemoLiveDemoArtifactChainReportVo(
                report.status(),
                report.complete(),
                report.summary(),
                report.nextAction(),
                report.launchPackageArchiveId(),
                report.outcomeCloseoutArchiveId(),
                report.evidenceBundleArchiveId(),
                report.handoffFinalizationArchiveId(),
                report.completionCertificateArchiveId(),
                report.repository(),
                report.issueNumber(),
                report.issueUrl(),
                report.taskId(),
                report.taskStatus(),
                report.pullRequestUrl(),
                report.steps(),
                report.checks(),
                report.evidenceNotes(),
                report.downloadActions(),
                report.sideEffectContract(),
                report.generatedAt(),
                markdownReport(report)
        );
    }

    private static String markdownReport(DemoLiveDemoArtifactChainReportVo report) {
        StringBuilder markdown = new StringBuilder();
        markdown.append("# PatchPilot Live Demo Artifact Chain Report\n\n");
        markdown.append("- Status: `").append(report.status()).append("`\n");
        markdown.append("- Complete: `").append(report.complete()).append("`\n");
        markdown.append("- Summary: ").append(report.summary()).append("\n");
        markdown.append("- Next action: ").append(report.nextAction()).append("\n");
        markdown.append("- Repository: ").append(valueOrMissing(report.repository())).append("\n");
        markdown.append("- Issue: #").append(report.issueNumber()).append("\n");
        markdown.append("- Issue URL: ").append(valueOrMissing(report.issueUrl())).append("\n");
        markdown.append("- Task: `").append(valueOrMissing(report.taskId())).append("`\n");
        markdown.append("- Task status: `").append(valueOrMissing(report.taskStatus())).append("`\n");
        markdown.append("- Pull Request: ").append(valueOrMissing(report.pullRequestUrl())).append("\n");
        markdown.append("- Generated at: `").append(report.generatedAt()).append("`\n\n");
        appendSteps(markdown, report.steps());
        appendChecks(markdown, report.checks());
        appendList(markdown, "Evidence Notes", report.evidenceNotes());
        appendList(markdown, "Download Actions", report.downloadActions());
        markdown.append("## Side Effect Contract\n\n").append(report.sideEffectContract()).append("\n");
        return markdown.toString();
    }

    private static void appendSteps(StringBuilder markdown, List<DemoLiveDemoArtifactChainReportVo.Step> steps) {
        markdown.append("## Artifact Steps\n\n");
        for (DemoLiveDemoArtifactChainReportVo.Step step : steps) {
            markdown.append("- `").append(step.status()).append("` ")
                    .append(step.name()).append(": `")
                    .append(valueOrMissing(step.artifactId())).append("` - ")
                    .append(step.summary())
                    .append(" Next: ")
                    .append(step.nextAction())
                    .append("\n");
        }
        markdown.append("\n");
    }

    private static void appendChecks(StringBuilder markdown, List<DemoLiveDemoArtifactChainReportVo.Check> checks) {
        markdown.append("## Consistency Checks\n\n");
        for (DemoLiveDemoArtifactChainReportVo.Check check : checks) {
            markdown.append("- `").append(check.status()).append("` ")
                    .append(check.name()).append(": ")
                    .append(check.summary())
                    .append(" Next: ")
                    .append(check.nextAction())
                    .append("\n");
        }
        markdown.append("\n");
    }

    private static void appendList(StringBuilder markdown, String heading, List<String> items) {
        markdown.append("## ").append(heading).append("\n\n");
        for (String item : items) {
            markdown.append("- ").append(item).append("\n");
        }
        markdown.append("\n");
    }

    private static String repository(
            Optional<DemoLiveTriggerLaunchPackageArchiveVo> launch,
            Optional<DemoLiveTriggerOutcomeCloseoutArchiveVo> closeout,
            Optional<DemoLiveDemoEvidenceBundleArchiveVo> evidence,
            Optional<DemoLiveDemoHandoffDeliveryFinalizationArchiveVo> finalization,
            Optional<DemoLiveDemoCompletionCertificateArchiveVo> completion
    ) {
        return launch.map(DemoLiveTriggerLaunchPackageArchiveVo::repository)
                .or(() -> closeout.map(DemoLiveTriggerOutcomeCloseoutArchiveVo::repository))
                .or(() -> evidence.map(DemoLiveDemoEvidenceBundleArchiveVo::repository))
                .or(() -> finalization.map(DemoLiveDemoHandoffDeliveryFinalizationArchiveVo::repository))
                .or(() -> completion.map(DemoLiveDemoCompletionCertificateArchiveVo::repository))
                .orElse(null);
    }

    private static long issueNumber(
            Optional<DemoLiveTriggerLaunchPackageArchiveVo> launch,
            Optional<DemoLiveTriggerOutcomeCloseoutArchiveVo> closeout,
            Optional<DemoLiveDemoEvidenceBundleArchiveVo> evidence,
            Optional<DemoLiveDemoHandoffDeliveryFinalizationArchiveVo> finalization,
            Optional<DemoLiveDemoCompletionCertificateArchiveVo> completion
    ) {
        return launch.map(DemoLiveTriggerLaunchPackageArchiveVo::issueNumber)
                .or(() -> closeout.map(DemoLiveTriggerOutcomeCloseoutArchiveVo::issueNumber))
                .or(() -> evidence.map(DemoLiveDemoEvidenceBundleArchiveVo::issueNumber))
                .or(() -> finalization.map(DemoLiveDemoHandoffDeliveryFinalizationArchiveVo::issueNumber))
                .or(() -> completion.map(DemoLiveDemoCompletionCertificateArchiveVo::issueNumber))
                .orElse(0L);
    }

    private static String issueUrl(
            Optional<DemoLiveTriggerLaunchPackageArchiveVo> launch,
            Optional<DemoLiveTriggerOutcomeCloseoutArchiveVo> closeout,
            Optional<DemoLiveDemoEvidenceBundleArchiveVo> evidence,
            Optional<DemoLiveDemoHandoffDeliveryFinalizationArchiveVo> finalization,
            Optional<DemoLiveDemoCompletionCertificateArchiveVo> completion
    ) {
        return launch.map(DemoLiveTriggerLaunchPackageArchiveVo::issueUrl)
                .or(() -> closeout.map(DemoLiveTriggerOutcomeCloseoutArchiveVo::issueUrl))
                .or(() -> evidence.map(DemoLiveDemoEvidenceBundleArchiveVo::issueUrl))
                .or(() -> finalization.map(DemoLiveDemoHandoffDeliveryFinalizationArchiveVo::issueUrl))
                .or(() -> completion.map(DemoLiveDemoCompletionCertificateArchiveVo::issueUrl))
                .orElse(null);
    }

    private static String taskId(
            Optional<DemoLiveTriggerOutcomeCloseoutArchiveVo> closeout,
            Optional<DemoLiveDemoEvidenceBundleArchiveVo> evidence,
            Optional<DemoLiveDemoHandoffDeliveryFinalizationArchiveVo> finalization,
            Optional<DemoLiveDemoCompletionCertificateArchiveVo> completion
    ) {
        return closeout.map(DemoLiveTriggerOutcomeCloseoutArchiveVo::taskId)
                .or(() -> evidence.map(DemoLiveDemoEvidenceBundleArchiveVo::taskId))
                .or(() -> finalization.map(DemoLiveDemoHandoffDeliveryFinalizationArchiveVo::taskId))
                .or(() -> completion.map(DemoLiveDemoCompletionCertificateArchiveVo::taskId))
                .orElse(null);
    }

    private static String taskStatus(
            Optional<DemoLiveTriggerOutcomeCloseoutArchiveVo> closeout,
            Optional<DemoLiveDemoEvidenceBundleArchiveVo> evidence,
            Optional<DemoLiveDemoHandoffDeliveryFinalizationArchiveVo> finalization,
            Optional<DemoLiveDemoCompletionCertificateArchiveVo> completion
    ) {
        return closeout.map(DemoLiveTriggerOutcomeCloseoutArchiveVo::taskStatus)
                .or(() -> evidence.map(DemoLiveDemoEvidenceBundleArchiveVo::taskStatus))
                .or(() -> finalization.map(DemoLiveDemoHandoffDeliveryFinalizationArchiveVo::taskStatus))
                .or(() -> completion.map(DemoLiveDemoCompletionCertificateArchiveVo::taskStatus))
                .orElse(null);
    }

    private static String pullRequestUrl(
            Optional<DemoLiveTriggerOutcomeCloseoutArchiveVo> closeout,
            Optional<DemoLiveDemoEvidenceBundleArchiveVo> evidence,
            Optional<DemoLiveDemoHandoffDeliveryFinalizationArchiveVo> finalization,
            Optional<DemoLiveDemoCompletionCertificateArchiveVo> completion
    ) {
        return closeout.map(DemoLiveTriggerOutcomeCloseoutArchiveVo::pullRequestUrl)
                .or(() -> evidence.map(DemoLiveDemoEvidenceBundleArchiveVo::pullRequestUrl))
                .or(() -> finalization.map(DemoLiveDemoHandoffDeliveryFinalizationArchiveVo::pullRequestUrl))
                .or(() -> completion.map(DemoLiveDemoCompletionCertificateArchiveVo::pullRequestUrl))
                .orElse(null);
    }

    private static String valueOrMissing(Object value) {
        if (value == null) {
            return "missing";
        }
        String text = value.toString();
        return text.isBlank() ? "missing" : text;
    }
}
