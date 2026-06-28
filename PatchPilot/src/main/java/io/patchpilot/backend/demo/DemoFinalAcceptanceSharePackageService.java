package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoAcceptanceSummaryVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceSharePackageVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
public class DemoFinalAcceptanceSharePackageService {

    private static final String SIDE_EFFECT_CONTRACT = "GET /api/demo/final-acceptance-share-package is read-only: it does not create tasks, call the model, run tests, mutate Git, archive records, record receipts, send messages, or write to GitHub.";

    private final Supplier<DemoAcceptanceSummaryVo> acceptanceSummarySupplier;
    private final Clock clock;

    @Autowired
    public DemoFinalAcceptanceSharePackageService(DemoAcceptanceSummaryService acceptanceSummaryService) {
        this(acceptanceSummaryService::getSummary, Clock.systemUTC());
    }

    DemoFinalAcceptanceSharePackageService(
            Supplier<DemoAcceptanceSummaryVo> acceptanceSummarySupplier,
            Clock clock
    ) {
        this.acceptanceSummarySupplier = acceptanceSummarySupplier;
        this.clock = clock;
    }

    public DemoFinalAcceptanceSharePackageVo getSharePackage() {
        DemoAcceptanceSummaryVo summary = acceptanceSummarySupplier.get();
        boolean sendReady = summary.status() == DemoReadinessStatus.READY && summary.accepted();
        Instant generatedAt = Instant.now(clock);
        List<String> recipients = recommendedRecipients();
        List<String> attachments = requiredAttachments(summary, sendReady);
        List<String> checks = preSendChecks(summary, sendReady);
        List<String> evidenceNotes = evidenceNotes(summary);
        String packageSummary = packageSummary(sendReady);
        String nextAction = nextAction(summary, sendReady);
        String subject = messageSubject(summary, sendReady);
        String body = messageBody(summary, sendReady);
        String markdownReport = formatMarkdown(
                summary,
                sendReady,
                packageSummary,
                nextAction,
                recipients,
                attachments,
                checks,
                evidenceNotes,
                subject,
                body,
                generatedAt
        );
        return new DemoFinalAcceptanceSharePackageVo(
                summary.status(),
                sendReady,
                packageSummary,
                nextAction,
                summary.launchCertificateArchiveId(),
                summary.taskCertificateArchiveId(),
                summary.latestTaskId(),
                summary.latestPullRequestUrl(),
                recipients,
                attachments,
                checks,
                subject,
                body,
                evidenceNotes,
                SIDE_EFFECT_CONTRACT,
                markdownReport,
                generatedAt
        );
    }

    private static List<String> recommendedRecipients() {
        return List.of("Repository owner or maintainer", "Demo reviewer");
    }

    private static List<String> requiredAttachments(DemoAcceptanceSummaryVo summary, boolean sendReady) {
        if (!sendReady) {
            return List.of("Resolve final demo acceptance blockers before attaching final evidence.");
        }
        List<String> attachments = new ArrayList<>();
        attachments.add("Final demo acceptance summary report");
        attachments.add("Launch acceptance certificate archive " + summary.launchCertificateArchiveId());
        attachments.add("Task evidence acceptance certificate archive " + summary.taskCertificateArchiveId());
        if (hasText(summary.latestPullRequestUrl())) {
            attachments.add("Pull Request " + summary.latestPullRequestUrl());
        }
        return List.copyOf(attachments);
    }

    private static List<String> preSendChecks(DemoAcceptanceSummaryVo summary, boolean sendReady) {
        List<String> checks = new ArrayList<>();
        if (sendReady) {
            checks.add("Confirm final demo acceptance status is READY and accepted.");
            checks.add("Confirm launch acceptance certificate archive " + summary.launchCertificateArchiveId() + " is attached.");
            checks.add("Confirm task evidence acceptance certificate archive " + summary.taskCertificateArchiveId() + " is attached.");
            if (hasText(summary.latestPullRequestUrl())) {
                checks.add("Confirm Pull Request " + summary.latestPullRequestUrl() + " opens correctly.");
            }
        } else {
            checks.add("Resolve final demo acceptance before sending: " + summary.nextAction());
            checks.add("Do not send the final acceptance package until the summary reports READY and accepted.");
        }
        return List.copyOf(checks);
    }

    private static List<String> evidenceNotes(DemoAcceptanceSummaryVo summary) {
        List<String> notes = new ArrayList<>();
        notes.add("Final acceptance status is " + summary.status().name() + ".");
        notes.add("Final acceptance accepted flag is " + summary.accepted() + ".");
        notes.addAll(summary.evidenceNotes());
        return List.copyOf(notes);
    }

    private static String packageSummary(boolean sendReady) {
        return sendReady
                ? "PatchPilot final demo acceptance package is ready to send."
                : "PatchPilot final demo acceptance package is not ready to send.";
    }

    private static String nextAction(DemoAcceptanceSummaryVo summary, boolean sendReady) {
        return sendReady
                ? "Send the prepared final acceptance message with all required attachments."
                : summary.nextAction();
    }

    private static String messageSubject(DemoAcceptanceSummaryVo summary, boolean sendReady) {
        if (!sendReady) {
            return "PatchPilot final demo acceptance: not ready";
        }
        return "PatchPilot final demo acceptance: " + valueOrDefault(summary.latestTaskId(), "accepted");
    }

    private static String messageBody(DemoAcceptanceSummaryVo summary, boolean sendReady) {
        if (!sendReady) {
            return "The PatchPilot final demo acceptance package is not ready to send yet.\n\n"
                    + "Current blocker: " + summary.nextAction() + "\n\n"
                    + "Please resolve this before sharing final acceptance evidence.";
        }
        return "PatchPilot final demo acceptance is ready for external review.\n\n"
                + "Launch certificate archive: " + valueOrNone(summary.launchCertificateArchiveId()) + "\n"
                + "Task evidence certificate archive: " + valueOrNone(summary.taskCertificateArchiveId()) + "\n"
                + "Latest task: " + valueOrNone(summary.latestTaskId()) + "\n"
                + "Pull Request: " + valueOrNone(summary.latestPullRequestUrl()) + "\n\n"
                + "Attached evidence should include the final demo acceptance summary, launch acceptance certificate archive, task evidence acceptance certificate archive, and Pull Request link.\n\n"
                + "Next action: " + summary.nextAction();
    }

    private static String formatMarkdown(
            DemoAcceptanceSummaryVo summary,
            boolean sendReady,
            String packageSummary,
            String nextAction,
            List<String> recipients,
            List<String> attachments,
            List<String> checks,
            List<String> evidenceNotes,
            String subject,
            String body,
            Instant generatedAt
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("# PatchPilot Final Demo Acceptance Share Package\n\n");
        builder.append("- Status: `").append(summary.status().name()).append("`\n");
        builder.append("- Send ready: `").append(sendReady).append("`\n");
        builder.append("- Launch certificate archive: `").append(valueOrNone(summary.launchCertificateArchiveId())).append("`\n");
        builder.append("- Task evidence certificate archive: `").append(valueOrNone(summary.taskCertificateArchiveId())).append("`\n");
        builder.append("- Latest task: `").append(valueOrNone(summary.latestTaskId())).append("`\n");
        builder.append("- Pull Request: `").append(valueOrNone(summary.latestPullRequestUrl())).append("`\n");
        builder.append("- Generated at: `").append(generatedAt).append("`\n\n");
        builder.append("## Summary\n\n").append(packageSummary).append("\n\n");
        builder.append("## Next Action\n\n").append(nextAction).append("\n\n");
        appendList(builder, "## Recommended Recipients", recipients);
        appendList(builder, "## Required Attachments", attachments);
        appendList(builder, "## Pre-Send Checks", checks);
        appendList(builder, "## Evidence Notes", evidenceNotes);
        builder.append("## Message Template\n\n");
        builder.append("Subject: ").append(subject).append("\n\n");
        builder.append(body).append("\n\n");
        builder.append("## Embedded Final Acceptance Summary\n\n");
        builder.append(summary.markdownReport()).append("\n\n");
        builder.append("## Side-Effect Contract\n\n");
        builder.append(SIDE_EFFECT_CONTRACT).append('\n');
        return builder.toString();
    }

    private static void appendList(StringBuilder builder, String title, List<String> items) {
        builder.append(title).append("\n\n");
        for (String item : items) {
            builder.append("- ").append(item).append('\n');
        }
        builder.append('\n');
    }

    private static String valueOrDefault(String value, String defaultValue) {
        return hasText(value) ? value : defaultValue;
    }

    private static String valueOrNone(String value) {
        return valueOrDefault(value, "none");
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
