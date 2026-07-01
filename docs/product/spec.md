# Product Specification

## Overview

PatchPilot is an AI software maintenance backend for GitHub repositories. It turns a GitHub Issue into a controlled code-fix workflow: analyze the issue, inspect the repository, generate a patch, run the supported language's verification command, and open a Pull Request for human review.

The product is not a general chatbot. It is a backend system that uses an agent workflow to operate through explicit tools, persistent tasks, and auditable execution records.

The long-term product target is multi-language issue-to-PR automation. Language support should be added through explicit adapters rather than one unrestricted generic runner.

## Users

### Repository Maintainer

A developer who owns or maintains a GitHub repository and wants assistance triaging and fixing well-scoped issues.

### Contributor

A developer who opens or comments on an issue and can request PatchPilot to attempt a fix through a command such as `/agent fix`.

### System Operator

A developer who runs PatchPilot, monitors task health, investigates failed runs, and reviews tool-call traces, test results, and GitHub API failures.

## Product Positioning

PatchPilot should be presented as:

```text
AI GitHub Issue-to-PR Agent Backend
```

The main product promise is:

```text
Comment on a GitHub issue, let PatchPilot analyze the repository, generate a tested patch, and open a reviewable Pull Request.
```

## Deployment Requirements

PatchPilot should be usable in two ways:

### Current Deployment Stage

The project currently targets local self-hosted development. The maintainer runs the backend and MySQL locally, creates a personal GitHub App, and uses a local tunnel for webhook testing when needed.

Public hosted usage is intentionally deferred until the issue-to-PR workflow, task persistence, observability, and safety boundaries are mature.

### Hosted Usage

The project owner deploys PatchPilot as a hosted backend service. External users install the public PatchPilot GitHub App and trigger the agent from their own repositories.

Hosted usage requires:

- Public HTTPS backend URL.
- GitHub App configured with the hosted webhook URL.
- GitHub App install link in the README or project website.
- Backend environment variables for GitHub App credentials, webhook secret, MySQL, and model provider credentials.
- MySQL-backed task and audit records once persistence is implemented.

### Self-Hosted Usage

A user can clone the repository and run their own PatchPilot instance.

Self-hosted usage requires:

- `docker-compose.yml` for backend and MySQL.
- `.env.example` documenting required environment variables.
- Instructions for creating a GitHub App.
- Instructions for setting webhook URL and webhook secret.
- Instructions for installing the app on a repository and triggering `/agent fix`.

The current implementation target is local self-hosted development first. Hosted usage is a later maturity target after the basic workflow is stable, observable, and safe enough for external repositories.

## Functional Requirements

### GitHub App Integration

- The system supports GitHub App based installation.
- The system receives GitHub webhook events.
- The system verifies webhook signatures before processing events.
- The system handles `issue_comment.created` events.
- The MVP trigger command is `/agent fix`.
- The trigger must include an actionable instruction such as a supported patch operation, a file path, or a concrete failure signal.
- Non-triggering webhook events should return success and be ignored without creating work.
- Webhook delivery ids should be tracked to support idempotency.
- Webhook delivery diagnostics should correlate each delivery with its final outcome, including task detail targets, rejected-trigger audit targets, ignored outcomes, duplicate outcomes, and error outcomes.
- Operators should be able to configure a public webhook base URL, see the derived GitHub Payload URL, and run a read-only health probe against that public URL before posting a live `/agent fix` trigger.
- Operators should be able to inspect one read-only webhook setup summary that combines webhook secret configuration, public payload URL readiness, latest delivery outcome, redelivery recommendation, next actions, and copyable Markdown evidence without exposing the secret.
- Demo readiness and the live smoke checklist should consume the same webhook setup summary so a missing secret, unreachable public payload URL, or redelivery-required delivery blocks or warns before an operator posts a live trigger.
- Demo handoff packages should include an explicit webhook delivery evidence check so a recent task-created delivery is visible as handoff-ready evidence, missing delivery history needs attention, and redelivery-required delivery failures block the handoff until setup is fixed.
- Operators should be able to paste a GitHub delivery payload into an admin-protected read-only diagnostic endpoint and see signature status, JSON validity, event/action support, `/agent fix` recognition, parsed repository/issue fields, and the next operator action without creating tasks or delivery records.

### Fix Task Creation

- A triggering issue comment creates a durable fix task.
- A task records repository owner, repository name, issue number, installation id, trigger user, trigger comment, status, timestamps, and failure reason.
- Task creation must return quickly and must not run repository analysis or model calls inline with webhook handling.
- A task can be queried by id for status and result.
- Task creation must pass authorization, command parsing, actionability, and rate-limit checks before expensive execution begins.
- Operators should be able to dry-run a proposed `/agent fix` trigger as either a manual API source or a GitHub issue-comment source and see whether it would create a task or be blocked without creating tasks, queue work, rejected-trigger audit rows, GitHub comments, webhook delivery diagnostics, or rate-limit records.
- Operators should be able to run an admin-protected live GitHub trigger dry run for the exact issue comment they plan to post, using `ISSUE_COMMENT` trigger gates and returning issue URL, issue-context state, safety, active-task, quarantine, rate-limit, model-classification decisions, side-effect contract, and next action without creating tasks, queue work, rate-limit usage, GitHub comments, branches, Pull Requests, Git mutations, or token exposure.
- Operators should be able to run one final admin-protected live launch gate for the exact issue comment they plan to post, combining self-hosted launch readiness, webhook setup readiness, live GitHub publish preflight, and live trigger dry-run evidence into a single `READY`, `NEEDS_ATTENTION`, or `BLOCKED` result with checks, next actions, side-effect contract, and Markdown report without creating tasks, queue work, rate-limit usage, GitHub comments, branches, Pull Requests, Git mutations, archive records, or token exposure.
- Operators should be able to compose a controlled demo `/agent fix` issue comment from structured repository, issue, operation, target path, and replacement text fields, then copy it, store it in browser-local recent command history, refill the composer from it, or apply it to launch preflight without creating a task or mutating GitHub.
- Operators should be able to run a final demo launch preflight for the exact GitHub issue comment they plan to post, combining current demo readiness with an `ISSUE_COMMENT` trigger dry run without creating a task or mutating GitHub.
- Operators should be able to copy a final demo launch package after launch preflight that includes the GitHub issue URL, exact `/agent fix` comment, readiness and trigger-gate evidence, prepared command history from the current browser, and next actions without creating a task or mutating GitHub.
- Operators should be able to archive the current demo readiness gate as PatchPilot-local evidence with status, summary, check counts, timestamp, and Markdown report without creating tasks, calling the model, cloning repositories, running verification commands, mutating Git, or writing to GitHub.
- Operators should be able to compare the two latest archived demo readiness snapshots and see whether readiness is improving, stable, regressing, or missing a baseline, including check-count deltas, next action, and copyable Markdown evidence without creating tasks, calling the model, cloning repositories, running verification commands, mutating Git, or writing to GitHub.
- Operators should be able to compare recent archived fixture baseline runs and see whether the latest adapter-selected fixture verification baseline is stable, regressed, or improved before using it as demo evidence. This comparison must be read-only and must not run fixture commands, create tasks, call the model, mutate Git, or write to GitHub.
- Operators should be able to track prepared demo launch commands after posting them by correlating browser-local command history with recent webhook delivery, task, and Pull Request evidence without creating tasks or mutating GitHub.
- Operators should be able to copy a Markdown outcome report for a tracked demo launch that includes the exact command, webhook outcome, task result, Pull Request URL, and next action without creating tasks or mutating GitHub.
- Operators should be able to save recent demo launch outcome reports in browser-local history, include those saved outcomes in demo session reports, reopen task and Pull Request links from that archive, copy archived reports again, and clear the archive without creating tasks or mutating GitHub.
- Operators should be able to copy or download a final demo handoff package whose readiness section ties together recent webhook delivery, completed task, Pull Request, prepared command, archived outcome, and readiness trend evidence without creating tasks or mutating GitHub.
- Operators should be able to inspect and download a final handoff share center that combines the latest handoff package archive summary and handoff share checklist into one send/no-send status, concrete download actions, evidence notes, and Markdown report without creating tasks or mutating GitHub. The top-level evidence bundle should repeat the share-center status, summary, next action, and download actions so operators can make the send/no-send decision from the first demo readout.
- Operators should be able to inspect and download a final handoff finalization gate that treats the post-demo package as accepted only when the package is share-ready and the latest delivery receipt is fresh for the current archive/session.
- Operators should be able to inspect, copy, and download final handoff share instructions with recommended recipients, required attachments, pre-send checks, and a prepared message template without sending messages or mutating GitHub.
- Operators should be able to record a local handoff share delivery receipt after the share instructions are send-ready, including delivery channel, target, operator, notes, source archive/session ids, delivered time, and downloadable Markdown evidence. Receipt creation must not send external messages, create tasks, call the model, mutate Git, or write to GitHub.
- The handoff share center and top-level demo evidence bundle should repeat the latest delivery receipt id, target, channel, delivered time, receipt-recorded status, and freshness status so operators can see whether post-demo handoff evidence has actually been delivered for the current archive/session without opening the receipt list first.
- The top-level demo evidence bundle should repeat full evaluation run readiness, including latest and previous run ids, pass/fail/skip deltas, language and build-system coverage, safety rejection categories, side-effect contract, and next action, so operators can see evaluation evidence from the first demo readout.
- Operators should be able to inspect a read-only live GitHub publish preflight that combines publish path readiness, publish permission readiness, and live GitHub branch/Pull Request inventory before posting a live `/agent fix`. It should warn when stale `patchpilot/*` branches or open PatchPilot Pull Requests already exist and should block when the configured token or repository cannot be validated. The preflight must not create tasks, call the model, run tests, mutate Git, create branches, open Pull Requests, write issue comments, expose token values, archive records, or send messages.
- Operators should be able to inspect and download one final self-hosted launch readiness package that combines current demo readiness, top-level evidence bundle, handoff finalization, GitHub publish path readiness, GitHub publish permission readiness, credential, webhook setup, and queue/worker signals before posting a live trigger or sharing demo evidence. It should warn or block when the configured token cannot read the demo repository, publish PatchPilot branches, create Pull Requests, or likely write issue feedback. This package must be read-only and must not create tasks, call the model, run tests, mutate Git, archive records, send messages, or write to GitHub.
- Operators should be able to archive, list, and download recent self-hosted launch readiness packages as PatchPilot-local evidence after the current runtime state changes. Archive creation must store the current package status, summary, check counts, timestamp, and Markdown report, record protected admin audit evidence, and must not create tasks, call the model, run tests, mutate Git, send messages, or write to GitHub.
- Operators should be able to inspect, copy, and download one final demo launch evidence package that combines self-hosted launch readiness, demo session snapshot, pre-launch checks, live task/Pull Request/webhook proof, evaluation coverage, post-demo handoff proof, next actions, and an explicit side-effect contract. This package must be read-only and must not create tasks, call the model, run tests, archive records, mutate Git, send messages, or write to GitHub.
- Operators should be able to archive, list, and download recent final demo launch evidence packages as PatchPilot-local evidence after the current runtime state changes. Archive creation must store the current package status, summary, session id, key live-run identifiers, timestamp, and Markdown report, record protected admin audit evidence, and must not create tasks, call the model, run tests, mutate Git, send messages, or write to GitHub.
- Operators should be able to inspect and download a final launch evidence share center that reads the latest archived launch evidence package and latest local delivery receipt, then reports share/no-share status, archive count, latest live-run identifiers, download actions, evidence notes, receipt-recorded state, receipt freshness, and a Markdown report without creating tasks or mutating GitHub. The top-level demo evidence bundle should repeat the launch share-center status, share-ready flag, summary, next action, archive count, latest archive/session/Pull Request identifiers, and download actions so operators can decide whether final launch evidence is shareable from the first demo readout.
- Operators should be able to record a local launch evidence delivery receipt after externally sharing the archived launch evidence package, including delivery channel, target, operator, notes, source archive/session ids, delivered time, and downloadable Markdown evidence. Receipt creation must not send external messages, create tasks, call the model, run tests, mutate Git, or write to GitHub.
- Operators should be able to inspect and download a final launch evidence finalization gate that treats the launch evidence package as accepted only when the latest archived launch evidence package is share-ready and the latest delivery receipt is fresh for the current archive/session. The top-level demo evidence bundle and copied runbook should repeat that finalization status, accepted receipt id, receipt freshness, summary, and next action so operators can prove final launch acceptance without opening the launch package panel first.
- Operators should be able to inspect and download one final launch acceptance closeout that combines self-hosted launch readiness, launch evidence package, launch share-center delivery proof, final handoff report package archive proof, and launch finalization into one accepted/not-accepted operator report. This closeout must include the key session, task, Pull Request, webhook delivery, evaluation run, archive, final handoff archive, receipt, delivery target, delivery channel, receipt freshness, checks, evidence notes, download actions, and a Markdown report, while remaining read-only and never creating tasks, calling the model, running tests, archiving records, mutating Git, sending messages, recording receipts, or writing to GitHub.
- Operators should be able to archive, list, and download recent final launch acceptance closeouts as PatchPilot-local evidence after the current final closeout changes. Archive creation must store the current closeout status, accepted flag, summary, key evidence identifiers, final handoff report package archive proof, timestamp, and Markdown report, record protected admin audit evidence, and must not create tasks, call the model, run tests, mutate Git, send messages, record receipts, or write to GitHub. The top-level demo evidence bundle and copied runbook should repeat the latest closeout archive status, accepted flag, linked launch evidence archive, delivery receipt, summary, next action, and download actions so final launch acceptance evidence is visible from the first demo readout.
- Operators should be able to inspect and download one final launch acceptance certificate derived from the latest archived closeout. The certificate should report certified only when the latest closeout archive is `READY` and accepted, then include archive count, closeout archive id, linked launch evidence archive id, final handoff report package archive proof, delivery receipt id, session/task/Pull Request/webhook/evaluation evidence, delivery target/channel/freshness, generated time, next action, download actions, and a Markdown report. Certificate generation must remain read-only and must not create tasks, call the model, run tests, archive records, mutate Git, send messages, record receipts, or write to GitHub.
- Operators should be able to archive, list, and download recent final launch acceptance certificates as PatchPilot-local evidence after the current certificate readout changes. Archive creation must store the current certificate status, certified flag, summary, key evidence identifiers, final handoff report package archive proof, generated time, archived time, download actions, and Markdown report, record protected admin audit evidence, and must not create tasks, call the model, run tests, mutate Git, send messages, record receipts, or write to GitHub. The top-level demo evidence bundle and copied runbook should repeat the latest certificate archive status, certified flag, linked closeout archive, linked launch evidence archive, delivery receipt, summary, next action, and download actions so the external-review launch record is visible from the first demo readout.
- Operators should be able to inspect and download one final demo acceptance share package derived from the final demo acceptance summary. The package should report send-ready only when the summary is `READY` and accepted, then include recommended recipients, required attachments, pre-send checks, message subject/body, evidence notes, and a Markdown report. Package generation must remain read-only and must not create tasks, call the model, run tests, archive records, mutate Git, send messages, record receipts, or write to GitHub.
- Operators should be able to archive, list, and download recent final demo acceptance share packages as PatchPilot-local evidence after the current acceptance package changes. Archive creation must store the current package status, send-ready flag, message subject, generated time, archived time, and Markdown report, record protected admin audit evidence, and must not create tasks, call the model, run tests, mutate Git, send messages, record receipts, or write to GitHub.
- Operators should be able to record a local final demo acceptance share delivery receipt after externally sending the archived reviewer-facing package, including delivery channel, target, operator, notes, current package archive id, task id, delivered time, and downloadable Markdown evidence. Receipt creation must record protected admin audit evidence and must not send external messages, create tasks, call the model, run tests, mutate Git, or write to GitHub.
- Operators should be able to inspect and download a final demo acceptance share finalization gate that treats the reviewer-facing final acceptance package as complete only when the latest package archive is send-ready and the latest delivery receipt is fresh for that archive/task. The top-level demo evidence bundle and copied runbook should repeat that finalization status, archive id, task id, delivery receipt id, target/channel, receipt freshness, summary, and next action so external-review acceptance handoff proof is visible from the first demo readout.
- Operators should be able to archive, list, and download recent final acceptance completion records only after the final acceptance share finalization gate is `READY`. Archive creation must store the READY finalization status, finalized flag, package archive id, task id, delivery receipt id, delivery target/channel, receipt freshness, generated time, archived time, evidence notes, and Markdown report, record protected admin audit evidence, and must not create tasks, call the model, run tests, mutate Git, send messages, record receipts, or write to GitHub.
- Operators should be able to inspect and download a final acceptance completion evidence bundle that summarizes the latest finalization and latest completion archive into one reviewer-facing proof package. The bundle must expose READY/NEEDS_ATTENTION/BLOCKED status, share-ready flag, latest completion archive id, share package archive id, delivery receipt id, delivery target/channel, task id, completion archive count, evidence notes, download actions, generated time, and a read-only side-effect contract without creating tasks, calling the model, running tests, mutating Git, sending messages, archiving records, recording receipts, or writing to GitHub.
- Operators should be able to record, list, and download local delivery receipts for the final acceptance completion evidence bundle only after that bundle is ready to share. Receipt creation must capture bundle status, completion archive id, share package archive id, final acceptance delivery receipt id, task id, delivery channel, target, operator, notes, delivered time, created time, protected admin audit evidence, and Markdown report, and it must not send messages, create tasks, call the model, run tests, mutate Git, archive records, or write to GitHub.
- Operators should be able to inspect and download a read-only finalization gate for final acceptance completion evidence delivery. The gate must compare the latest completion evidence bundle with the latest completion evidence delivery receipt, report READY/NEEDS_ATTENTION/BLOCKED status, receipt freshness, matching identifiers, checks, next action, evidence notes, download actions, generated time, and a side-effect contract without creating tasks, calling the model, running tests, mutating Git, archiving records, recording receipts, sending messages, or writing to GitHub.
- Operators should be able to inspect and download one read-only final acceptance completion closeout that proves the accepted demo summary, reviewer-package finalization, completion evidence bundle, latest completion archive, latest completion evidence delivery receipt, and completion delivery finalization agree before treating the external-review completion loop as closed. The closeout must report READY/NEEDS_ATTENTION/BLOCKED status, closed flag, latest task, Pull Request, archive, receipt, delivery target/channel, receipt freshness, checks, evidence notes, download actions, generated time, and a Markdown report without creating tasks, calling the model, running tests, mutating Git, archiving records, recording receipts, sending messages, or writing to GitHub. The top-level demo evidence bundle and copied runbook should repeat the closeout status, closed flag, latest task, Pull Request, completion archive, completion evidence delivery receipt, target/channel, freshness, summary, next action, and download actions so the final external-review completion proof is visible from the first demo readout.
- Operators should be able to archive, list, and download final acceptance completion closeout records only after the closeout is `READY` and closed. Archive creation must store the closeout status, closed flag, task, Pull Request, share package archive id, completion archive id, completion evidence delivery receipt id, delivery target/channel, receipt freshness, generated time, archived time, evidence notes, download actions, side-effect contract, and Markdown report, record protected admin audit evidence, and must not create tasks, call the model, run tests, mutate Git, send messages, record receipts, or write to GitHub. The top-level demo evidence bundle and copied runbook should repeat the latest closeout archive status, closed flag, archive id, linked completion archive, completion evidence delivery receipt, task, Pull Request, archived time, summary, next action, and download actions so the frozen external-review completion record is visible from the first demo readout.
- Operators should be able to inspect and download one final external-review evidence package that aggregates final demo acceptance, reviewer-package finalization, final acceptance completion evidence, completion evidence delivery finalization, final completion closeout, and the latest frozen closeout archive. The package must report READY only when the final chain is accepted/finalized/share-ready/closed and the latest closeout archive is `READY` and closed, then expose key archive, receipt, task, Pull Request, delivery, freshness, check, evidence-note, download-action, generated-time, and side-effect-contract fields without creating tasks, calling the model, running tests, mutating Git, archiving records, recording receipts, sending messages, or writing to GitHub. The top-level demo evidence bundle and copied runbook should repeat the package status, ready flag, closeout archive, completion archive, completion evidence delivery receipt, task, Pull Request, delivery target/channel, freshness, summary, next action, and download actions so the final reviewer-facing proof is visible from the first demo readout.
- Operators should be able to record, list, and download local delivery receipts for the latest final external-review evidence package archive. Receipt creation must require a READY frozen package archive, capture archive id, closeout archive id, completion archive id, completion evidence delivery receipt id, task, Pull Request, delivery channel, target, operator, notes, delivered time, created time, protected admin audit evidence, and Markdown report, and it must not send messages, create tasks, call the model, run tests, mutate Git, archive records, or write to GitHub. The top-level demo evidence bundle and copied runbook should repeat whether the latest receipt is missing, stale, or fresh for the latest archive so operators can prove the final external-review package was actually delivered.
- Operators should be able to inspect and download a read-only finalization gate for final external-review package delivery. The gate must compare the latest frozen package archive with the latest package delivery receipt, report READY/NEEDS_ATTENTION/BLOCKED status, receipt freshness, matching identifiers, checks, next action, evidence notes, download actions, generated time, and a side-effect contract without creating tasks, calling the model, running tests, mutating Git, archiving records, recording receipts, sending messages, or writing to GitHub. The top-level demo evidence bundle and copied runbook should repeat that finalization status, archive id, receipt id, target/channel, freshness, summary, next action, and download actions so the final external-review package handoff is visible from the first demo readout.
- Operators should be able to archive, list, and download final external-review package delivery finalization records only after that finalization gate is `READY`. Archive creation must store the finalized status, package archive id, delivery receipt id, closeout archive id, completion archive id, completion evidence delivery receipt id, task, Pull Request, delivery target/channel, receipt freshness, checks, evidence notes, generated time, archived time, side-effect contract, and Markdown report, record protected admin audit evidence, and must not create tasks, call the model, run tests, mutate Git, send messages, record receipts, or write to GitHub. The top-level demo evidence bundle and copied runbook should repeat the latest delivery finalization archive status, finalized flag, archive id, linked package archive, linked delivery receipt, task, Pull Request, archived time, summary, next action, and download actions so the final external-review package delivery closure is visible from the first demo readout.
- Operators should be able to inspect and download one final external-review delivery certificate derived from the latest archived delivery finalization. The certificate must report certified/not-certified state, latest finalization archive, package archive, delivery receipt, task, Pull Request, target/channel, receipt freshness, checks, evidence notes, generated time, Markdown report, and a read-only side-effect contract without creating tasks, calling the model, running tests, mutating Git, archiving records, recording receipts, sending messages, or writing to GitHub.
- Operators should be able to archive, list, and download final external-review delivery certificate records only after the current certificate is `READY` and certified. Archive creation must store the certificate status, certified flag, delivery finalization archive id, package archive id, delivery receipt id, task, Pull Request, delivery target/channel, receipt freshness, checks, evidence notes, generated time, archived time, side-effect contract, and Markdown report, record protected admin audit evidence, and must not create tasks, call the model, run tests, mutate Git, send messages, record receipts, or write to GitHub.
- Operators should be able to inspect, download, and locally archive one final external-review release bundle derived from the latest certified certificate archive. The live bundle must report release-ready status, certificate archive id, delivery finalization archive id, package archive id, delivery receipt id, task, Pull Request, delivery target/channel, required attachments, checks, evidence notes, generated time, Markdown report, and a read-only side-effect contract without creating tasks, calling the model, running tests, mutating Git, archiving records, recording receipts, sending messages, or writing to GitHub. Archive creation must require a READY release bundle, store a frozen Markdown report with linked source evidence, list recent archives, download archived reports, and record protected audit evidence as a PatchPilot-local write only. The top-level demo evidence bundle and copied runbook should repeat the live release status plus latest release-bundle archive status, archive id, certificate archive, delivery finalization archive, package archive, delivery receipt, task, Pull Request, target/channel, required attachments, evidence notes, next action, and download actions so the terminal reviewer handoff proof is visible from the first demo readout.
- Operators should be able to record, list, and download local delivery receipts for the latest final external-review release bundle archive. Receipt creation must require a READY frozen release-bundle archive, capture release-bundle archive id, certificate archive id, package delivery finalization archive id, package archive id, package delivery receipt id, task, Pull Request, delivery channel, target, operator, notes, delivered time, created time, protected admin audit evidence, and Markdown report, and it must not send messages, create tasks, call the model, run tests, mutate Git, archive records, or write to GitHub.
- Operators should be able to inspect and download a read-only finalization gate for final external-review release bundle delivery. The gate must compare the latest frozen release-bundle archive with the latest release-bundle delivery receipt, report READY/NEEDS_ATTENTION/BLOCKED status, receipt freshness, matching identifiers, checks, next action, evidence notes, download actions, generated time, and a side-effect contract without creating tasks, calling the model, running tests, mutating Git, archiving records, recording receipts, sending messages, or writing to GitHub. The top-level demo evidence bundle and copied runbook should repeat that finalization status, release-bundle archive id, receipt id, target/channel, freshness, summary, next action, and download actions so the terminal external-review handoff delivery proof is visible from the first demo readout.
- Operators should be able to archive, list, and download final external-review release bundle delivery finalization records only after that finalization gate is `READY`. Archive creation must store the finalized status, release-bundle archive id, release-bundle delivery receipt id, certificate archive id, package delivery finalization archive id, package archive id, package delivery receipt id, task, Pull Request, delivery target/channel, receipt freshness, checks, evidence notes, generated time, archived time, side-effect contract, and Markdown report, record protected admin audit evidence, and must not create tasks, call the model, run tests, mutate Git, send messages, record receipts, or write to GitHub. The top-level demo evidence bundle should repeat the latest release-bundle delivery finalization archive status, finalized flag, archive id, linked release-bundle archive, linked delivery receipt, certificate archive, task, Pull Request, archived time, summary, next action, and download actions so the terminal external-review handoff proof remains available after live read models change.
- Operators should be able to inspect and download one final external-review release bundle delivery certificate derived from the latest archived release-bundle delivery finalization. The certificate must report certified/not-certified state, latest release-bundle delivery finalization archive, release-bundle archive, release-bundle delivery receipt, certificate archive, package archive, package delivery receipt, task, Pull Request, target/channel, receipt freshness, checks, evidence notes, generated time, Markdown report, and a read-only side-effect contract without creating tasks, calling the model, running tests, mutating Git, archiving records, recording receipts, sending messages, or writing to GitHub. The final demo acceptance dashboard should show this certificate as the terminal reviewer handoff proof and download it through `GET /api/demo/final-external-review-release-bundle/delivery-certificate/report/download`.
- Operators should be able to archive, list, and download final external-review release bundle delivery certificate records only after the current certificate is `READY` and certified. Archive creation must store the certificate status, certified flag, release-bundle delivery finalization archive id, release-bundle archive id, release-bundle delivery receipt id, package certificate archive id, package archive id, package delivery receipt id, task, Pull Request, delivery target/channel, receipt freshness, checks, evidence notes, generated time, archived time, side-effect contract, and Markdown report, record protected admin audit evidence, and must not create tasks, call the model, run tests, mutate Git, send messages, record receipts, or write to GitHub. The top-level demo evidence bundle and copied runbook should repeat the latest certificate archive status, certified flag, archive id, linked release-bundle delivery finalization archive, linked release-bundle archive, delivery receipt, task, Pull Request, archived time, summary, next action, and download actions so the durable terminal reviewer handoff proof remains visible from the first demo readout.
- Operators should be able to inspect and download one final reviewer handoff package derived from the latest archived terminal release-bundle delivery certificate. The package must report READY only when the terminal certificate archive is `READY`, certified, tied to a frozen release bundle archive, and backed by fresh release-bundle delivery proof. It must expose the terminal certificate archive, delivery finalization archive, release bundle archive, delivery receipt, package-level certificate/package/receipt ids, task, Pull Request, target/channel, archived time, required attachments, checks, evidence notes, download actions, Markdown report, and a read-only side-effect contract through `GET /api/demo/final-reviewer-handoff-package` and `GET /api/demo/final-reviewer-handoff-package/report/download`. The top-level demo evidence bundle, copied runbook, and operations dashboard should repeat the package so the final reviewer send/no-send state is visible without opening archive history.
- Task execution must pass a repository language-adapter preflight after workspace preparation and before model patch generation.
- Task detail APIs, copied task reports, and the dashboard must expose structured adapter execution evidence. Supported tasks should show the selected language/build system, allowlisted verification command, detection reason, operator action, and the safety boundary that commands come from registered adapters rather than issue comments. Pending tasks should state that adapter evidence has not been recorded yet. Unsupported tasks should state that PatchPilot stopped before model patch generation, verification, Git mutation, push, or Pull Request creation and list supported adapter options.
- Operators should be able to run a local repository preflight diagnostic that uses the same language adapter registry without creating a task, running tests, mutating Git, or opening a Pull Request.
- Local repository preflight diagnostics must reject paths outside configured allowed roots before adapter detection.

### Safety Gate

- The system must distinguish executable commands from vague comments, jokes, prompt injection attempts, and destructive requests.
- Empty or vague trigger bodies such as `/agent fix`, `/agent fix help`, and `/agent fix make it better` must be rejected before task creation.
- Operators may enable model-assisted trigger classification after deterministic safety checks to reject vague, non-maintenance, or unclear requests before task creation.
- Model-assisted classification must not override deterministic safety rejections.
- If model-assisted classification fails or returns malformed output, the system must reject the trigger conservatively.
- The system should reject or ignore comments from unauthorized users and repositories.
- Operators may configure trigger-user and repository allowlists for self-hosted demos and private deployments.
- Operators may configure trigger rate limits by trigger user, repository, and issue to reject repeated `/agent fix` attempts before model calls or task creation.
- Operators may enable rejected-trigger quarantine so repeated rejected attempts from the same trigger user or repository create or extend a durable quarantine record and are refused with `ABUSE_QUARANTINED` before rate-limit checks, model calls, task creation, workspace cloning, or queueing.
- Trigger dry runs should use the same safety, active-task, quarantine, rate-limit, and model-classification order as task creation, but rate-limit checks must be read-only and rejected dry runs must not create rejected-trigger audit rows.
- Operators should be able to inspect one quarantine and see the rejected-trigger audit rows and manual safety actions that explain it.
- The system should reject unsupported repositories before model execution, patch generation, test execution, Git mutation, or Pull Request creation.
- Unsupported repository task failures should post issue-facing feedback that says execution stopped before model patch generation, tests, commits, pushes, or Pull Request creation, then lists supported language/build shapes and a safe next action.
- The local repository preflight diagnostic should return supported status, selected language/build system, verification command, detection reason, and next operator action so unsupported repository shapes can be fixed before a live `/agent fix`.
- The local repository preflight diagnostic should expose its configured allowed roots through non-sensitive configuration summary APIs and the dashboard so operators can verify scope before using it.
- Demo readiness and the operator setup checklist should warn when the configured demo repository or recent demo trigger user does not match enabled safety allowlists.
- Demo launch preflight should block posting guidance when either demo readiness is not ready or the exact tested issue comment would be rejected by task-creation gates.
- Demo readiness and the operator setup checklist should warn when repository-preflight allowed roots do not cover checked-in demo fixture paths.
- Demo readiness and the operator setup checklist should warn when the configured OpenAI-compatible model provider cannot answer a minimal health probe, even if model credentials are present.
- Demo readiness and the operator setup checklist should warn when `PATCHPILOT_GITHUB_WEBHOOK_PUBLIC_BASE_URL` is missing or its public `/health` endpoint is unreachable, because GitHub webhook deliveries will not reach the backend even if localhost is healthy.
- Demo readiness, the smoke checklist, the demo script, the session snapshot checklist, and the operator setup checklist should warn when a supported adapter's selected verification executable is not available on the backend process `PATH`.
- Demo readiness and the operator setup checklist should warn when the queue worker has not started, has most recently errored, or has stopped polling within the configured stale threshold.
- Demo readiness should include a read-only evaluation baseline gate that needs attention when archived fixture baseline evidence is insufficient and blocks the live demo when the latest archived baseline regressed or still contains failed fixture cases.
- Demo launch command composition should reject unsupported operations, protected repository metadata paths, absolute paths, parent-directory segments, empty path segments, whitespace in target paths, and blank replacement text for `replace` commands before the operator copies the command.
- If project detection is possible from webhook or repository metadata before cloning, the system may reject even earlier.
- The system must never follow user instructions that request secret exfiltration, destructive repository changes, arbitrary shell execution, or permission escalation.
- The system should record rejected trigger decisions with clear operator-facing reasons.
- GitHub webhook trigger rejections should post a safe issue comment with the rejection category, reason, and next action, without echoing unsafe trigger text.
- GitHub refusal comment failures must not create a task or hide the rejected-trigger audit record.
- Rejected-trigger retry should be preflighted before task creation. Only actionability or model-classification rejections may be retried directly; dangerous instructions, unauthorized users, unauthorized repositories, rate limits, active abuse quarantines, unsupported commands, unknown categories, and already-retried audit rows must return a clear blocked reason.
- Accepted trigger decisions should record concise task timeline evidence that explains the safety-gate result, whether issue context was loaded, and the model trigger-classification outcome.
- Accepted trigger decisions should be exposed as structured task detail and copied report evidence so operators can inspect why a task was allowed to execute without parsing raw timeline text.
- Accepted tasks should persist a pre-execution safety snapshot and expose it in task detail, copied reports, and the dashboard with source, final allow decision, safety-gate result, active-task check, quarantine state, rate-limit state, issue-context state, model trigger-classification result, and evidence timestamp.
- Accepted trigger decisions should also be queryable as a recent audit stream with task context, so operators can review allowed triggers across tasks without selecting each task individually.
- The system should summarize recent rejected trigger decisions by category, source, trigger user, and repository so operators can detect abuse patterns and tune safety configuration.
- The system should expose active and historical trigger quarantine records with scope, scope key, reason, category, evidence count, window, start time, expiry time, and timestamps.
- The system should record manual safety mutations, including trigger quarantine creation and release, with operator, reason, target, and timestamp.
- Operators should be able to filter protected admin mutation audit events by action, operator, resource, and scope key, then copy the visible rows as a Markdown evidence report.
- Non-triggering comments may be ignored without creating task or rejection audit records.


### AI Infrastructure Requirements

- Model calls must go through a single internal provider boundary instead of direct provider calls from workflow code.
- The first provider path should be OpenAI-compatible so the project can connect to common hosted model providers and local OpenAI-compatible gateways.
- Model requests and responses should record provider, model, prompt version, input summary, output summary, token usage, duration, estimated cost when configured, success state, and stable error category.
- Prompts that affect execution should have explicit names and version ids.
- Model outputs used for trigger classification, fix planning, file edits, patch review, PR summaries, and failure summaries must be parsed as structured output and validated before use.
- Malformed, incomplete, low-confidence, or over-budget model outputs must stop safely with a clear task or rejection category.
- The agent workflow should enforce per-task limits for model calls, tool calls, changed files, changed lines, and runtime duration.
- Tool definitions should have stable names, typed inputs, typed outputs, risk levels, timeout policy, and audit summaries.
- Repository retrieval should start with deterministic tree inspection and lexical search; optional embeddings or vector search are future additions behind a pluggable retrieval boundary.
- Any repository indexing or retrieval step must exclude secrets, dependency directories, generated artifacts, and files outside the task workspace.
- Evaluation cases should define repository fixture, issue text, expected changed files, expected verification command, and success criteria.
- Operators should be able to inspect a read-only evaluation case catalog that covers supported language fixes and safety rejections before benchmark execution exists.
- Operators should be able to inspect a read-only evaluation readiness summary with case counts, covered languages, covered build systems, rejection categories, health contract, and next action.
- Operators should be able to inspect read-only evaluation fixture readiness that verifies checked-in fixture directories, expected changed files, and adapter metadata for supported cases while marking safety rejections as no-fixture-required.
- Operators should be able to inspect and copy a read-only evaluation run preview report with expected commands, known gaps, side-effect contract, and next action alongside executable local run archives.
- Operators should be able to execute and archive local fixture baseline runs for supported checked-in fixtures, then list, copy, or download the archived Markdown reports without creating tasks, calling the model, mutating Git, or writing to GitHub.
- Local evaluation runs should combine fixture baseline execution output with catalog language/build-system and safety-rejection coverage, record the side-effect contract and next action, and expose a copyable/downloadable Markdown report without creating tasks, calling the model, mutating Git, or writing to GitHub.
- Operators should be able to inspect a read-only full evaluation run readiness summary that compares the latest two archived full evaluation runs, reports pass/fail/skip deltas, coverage, safety categories, side-effect contract, next action, and copyable Markdown evidence without creating tasks, calling the model, mutating Git, or writing to GitHub.
- Future model benchmark runs should record model, prompt version, repository revision, success metrics, failure categories, cost, latency, and a copyable Markdown report.
- Dashboard and API surfaces should make model usage, tool usage, retrieval evidence, evaluation results, budget state, and safety decisions inspectable without exposing secrets.

### Agent Workflow

- The agent receives issue title, issue body, relevant comments, repository metadata, repository tree, and selected file contents.
- The agent creates a fix plan before editing files.
- The agent may call only registered tools.
- The agent must not execute arbitrary shell commands generated by the model.
- The agent must produce structured outputs for fix plans, patch proposals, PR summaries, and failure summaries.

### Repository Workspace

- Each task gets an isolated workspace.
- The system clones the target repository into the task workspace.
- The system checks out the base branch and creates a patch branch.
- The system runs language-adapter detection immediately after workspace preparation.
- Unsupported repositories must fail at this preflight and must not reach agent patching or Git mutation.
- File reads and writes are restricted to the task workspace.
- The system records changed files and the final diff.

### Code Search And Editing Tools

- The system provides controlled tools for repository tree inspection, code search, file reading, file writing, and diff inspection.
- Tool inputs and outputs are structured.
- Tool calls are audited with task id, tool name, input summary, output summary, duration, and success state.

### Test Execution

- The MVP supports Java Maven, Java Gradle, Go modules, Node/Bun, Node/npm, Node/pnpm, Node/yarn, Python/tox, Python/nox, Python/hatch, Python/Poetry, Python/uv, and Python/pytest repositories first.
- The long-term system supports multiple language adapters, starting with Java/Maven, Java/Gradle, Go, Bun, npm, pnpm, yarn, Python/tox, nox, hatch, Poetry, uv, pytest, and additional explicit runners.
- Each adapter defines project detection, allowed verification commands, test output capture, timeout policy, and unsupported-repository failure reasons.
- The adapter registry selects the first adapter that supports the repository and returns a clear unsupported result when none match.
- The verification runner executes only the selected adapter's allowlisted verification command.
- The adapter runtime readiness API reports whether the executable for each allowlisted verification command is available on the backend process `PATH` without running the command.
- The selected adapter metadata is stored on the task as `language`, `buildSystem`, and `verificationCommand`.
- The Java/Maven adapter detects `mvnw` and `pom.xml`.
- The Java/Maven adapter runs `./mvnw test` when a Maven wrapper exists.
- The Java/Maven adapter runs `mvn test` when no wrapper exists.
- The Java/Gradle adapter detects `gradlew`, `build.gradle`, and `build.gradle.kts`.
- The Java/Gradle adapter runs `./gradlew test` when a Gradle wrapper exists.
- The Java/Gradle adapter runs `gradle test` when no wrapper exists.
- The Go adapter detects `go.mod`.
- The Go adapter runs `go test ./...`.
- The Node/Bun adapter detects `package.json`, `bun.lockb` or `bun.lock`, and a non-empty `scripts.test`.
- The Node/Bun adapter runs `bun test`.
- The Node/npm adapter detects `package.json` files with a non-empty `scripts.test`.
- The Node/npm adapter runs `npm test`.
- The Node/pnpm adapter detects `package.json`, `pnpm-lock.yaml`, and a non-empty `scripts.test`.
- The Node/pnpm adapter runs `pnpm test`.
- The Node/yarn adapter detects `package.json`, `yarn.lock`, and a non-empty `scripts.test`.
- The Node/yarn adapter runs `yarn test`.
- The Python/tox adapter detects `tox.ini` or `[tool.tox]` in `pyproject.toml`.
- The Python/tox adapter runs `tox`.
- The Python/nox adapter detects `noxfile.py`.
- The Python/nox adapter runs `nox`.
- The Python/hatch adapter detects a Hatch test script in `pyproject.toml`.
- The Python/hatch adapter runs `hatch test`.
- The Python/Poetry adapter detects `[tool.poetry]` in `pyproject.toml` plus pytest configuration or dependency.
- The Python/Poetry adapter runs `poetry run pytest`.
- The Python/uv adapter detects `uv.lock` plus pytest configuration or dependency in `pyproject.toml`.
- The Python/uv adapter runs `uv run pytest`.
- The Python/pytest adapter detects `pytest.ini`, `[tool.pytest.ini_options]` in `pyproject.toml`, or pytest in `requirements.txt`.
- The Python/pytest adapter runs `python3 -m pytest`.
- The system captures exit code, stdout, stderr, duration, and a short test summary.
- Test failure must not be reported as a successful fix.

### Pull Request Creation

- When patch generation and tests succeed, the system pushes a patch branch.
- The system creates a Pull Request.
- The PR body includes the linked issue, summary of changes, files changed, and test result.
- The PR body includes task id, trigger user, patch branch, detected language adapter, selected build system, allowlisted verification command, and adapter detection reason when available.
- The PR body includes a Dashboard task deep link when a public Dashboard base URL is configured.
- The PR body includes the actual verification result summary when available, including command, exit code, and duration.
- The PR body includes the latest model patch-review decision, reason, confidence, required follow-up, edited files, and review time when available.
- The PR body includes risk-review approval operator, time, and reason when a task resumed after generated-diff risk review approval.
- The PR body states that verification commands come from repository adapters rather than arbitrary issue text, and that PatchPilot does not auto-merge Pull Requests.
- The system comments on the original issue with the PR link.
- Issue status comments include a Dashboard task deep link when a public Dashboard base URL is configured.
- Completed issue comments include the detected adapter, allowlisted verification command, detection reason, and review boundary when available.
- Completed issue comments include the actual verification result summary when available.
- Completed issue comments include the latest model patch-review decision, reason, confidence, required follow-up, edited files, and review time when available.
- Completed issue comments include risk-review approval operator, time, and reason when a task resumed after generated-diff risk review approval.
- The system does not merge Pull Requests automatically.

### Failure Reporting

- Any task can move to `FAILED`.
- User-actionable failures should be posted as issue comments.
- Failed-task issue comments should include a failure category, next action, and a sanitized reason.
- Failed-task issue comments should include detected language, selected build system, allowlisted verification command, and adapter detection reason when that repository evidence is available.
- Failed-task issue comments should include the actual verification result summary when a test run exists.
- Failed-task issue comments should include the latest model patch-review decision, reason, confidence, required follow-up, edited files, and review time when available.
- `PENDING_REVIEW` issue comments should include the same adapter evidence when available, so the issue author can distinguish a risk-gate pause from an unsupported repository or test failure.
- `PENDING_REVIEW` issue comments should state that verification has not run when the task paused before verification.
- Non-success issue comments with adapter evidence should state that PatchPilot selects verification commands from repository adapter allowlists and does not run arbitrary shell commands from issue comments.
- Failure metrics should reuse the same stable failure categories and next-action guidance as failed-task issue comments, so dashboard summaries and GitHub feedback stay consistent.
- Task detail APIs and copied task reports should expose accepted-trigger intent audit, failure category, next action, and sanitized reason for failed tasks, so per-task investigation and aggregate metrics use one taxonomy.
- Failed and cancelled tasks should expose retry preflight that returns retry eligibility, stable category, sanitized reason, and next operator action before an operator queues another attempt.
- Retry preflight should block blind retries when the failure category indicates setup or repository support work is required first, such as GitHub credential/permission failures or unsupported repository shapes.
- The retry API should enforce the same retry-preflight policy used by the dashboard.
- The retry API must require an explicit operator reason, store it with retry lineage, include it in requeue timeline evidence, and expose it in task reports and task detail responses.
- If a task has an accepted-task status comment, failure reporting should update that same comment.
- If the accepted-task status comment is missing, failure reporting should create a new issue comment and store its id and URL on the task.
- Failure feedback comment creation or update failures must not change the durable task status.
- Internal failures must be recorded in task logs without exposing secrets.
- Failure reasons should distinguish GitHub permission errors, workspace errors, model errors, test failures, and unsupported repository types.

## Non-Goals

The MVP does not:

- Automatically merge Pull Requests.
- Push directly to the default branch.
- Support every programming language through the first adapter.
- Support every build system.
- Build a full admin dashboard.
- Require a browser extension.
- Let the model execute arbitrary shell commands.
- Claim that a fix succeeded without running the configured verification step.

## MVP Scope

The first production-like MVP supports:

- GitHub App webhook integration.
- `/agent fix` issue comment trigger.
- Java repositories using Maven or Gradle.
- Go modules using `go test ./...`.
- Node.js repositories using Bun, npm, pnpm, or yarn with `scripts.test`.
- Python repositories using tox, nox, hatch, Poetry, uv, or pytest directly.
- One repository per task.
- One generated Pull Request per successful task.
- Local workspace execution.
- Adapter-selected test verification.
- Audited model calls and tool calls.
- Runtime queue worker heartbeat and readiness status for local operator visibility.

## Frontend Requirements

PatchPilot uses React for the frontend. The frontend is primarily an operational dashboard for developers and maintainers.

MVP frontend scope:

- View fix tasks.
- View task status and failure reason.
- Open linked GitHub issue and Pull Request.
- Inspect tool-call summaries.
- Inspect verification output.
- Inspect a single demo evidence bundle before posting a live `/agent fix` comment.
- Inspect a single demo session snapshot that combines evidence, readiness snapshot trend, prepared launch commands from browser-local history, archived launch outcomes from browser-local history, script, runbook, checklist, health contract, share summary, and next actions.
- Follow a read-only demo script that gives ordered operator actions, verification commands, troubleshooting targets, and health-contract guarantees before and during a live smoke run.
- Copy a Markdown demo runbook generated from the current evidence bundle.
- Copy or download a Markdown demo session report generated from the current session snapshot, readiness trend, handoff readiness checks, current browser's prepared demo launch commands, and current browser's archived launch outcomes.
- Copy or download a Markdown demo handoff package that summarizes current demo evidence, handoff readiness, readiness trend, prepared command count, archived outcome count, recent task and Pull Request evidence, next actions, and the embedded session report.
- Archive the current demo handoff package into a separate recent list so the final post-demo package can be copied or downloaded later without overwriting the lower-level session report archive.
- Copy or download a Markdown handoff share checklist that turns the latest archived package summary into explicit share/no-share evidence, and inspect the same status in the top-level evidence bundle.
- Inspect or download a Markdown handoff share center that combines the latest archive summary and checklist into one final sharing view with send/no-send status, next action, downloads, and evidence notes.
- Record and download local handoff share delivery receipts after sending the prepared package through an external channel, without making PatchPilot send the message itself.
- Inspect and download the final self-hosted launch readiness package so demo readiness, evidence bundle status, handoff finalization, credentials, webhook setup, queue/worker state, and next actions are visible in one dashboard panel.
- Inspect, copy, and download the final demo launch evidence package so pre-launch readiness, live-run proof, evaluation proof, and post-demo handoff proof are available as one shareable artifact.
- Archive the final demo launch evidence package into a recent list, then download archived Markdown reports so final launch proof can be preserved across refreshes and, in database-backed local profiles, backend restarts.
- Record and download local launch evidence delivery receipts after sending the archived launch evidence through an external channel, without making PatchPilot send the message itself.
- Inspect and download a final launch evidence finalization report so the final launch package is only treated as accepted delivery evidence when the latest receipt matches the current archive/session.
- Inspect and download a final launch acceptance closeout report so pre-launch readiness, launch evidence, share delivery proof, and finalization are visible in one dashboard section before treating the launch evidence as complete.
- Archive the final launch acceptance closeout into a recent list, then download archived Markdown reports so the final accepted/not-accepted decision can be preserved across refreshes and, in database-backed local profiles, backend restarts.
- Inspect and download a final launch acceptance certificate so only an accepted READY closeout archive can be used as the certified external-review evidence record.
- Archive the final self-hosted launch readiness package into a recent list, then download archived Markdown reports so pre-launch decisions can be preserved across refreshes and, in database-backed local profiles, backend restarts.
- Archive the current demo readiness gate into a recent list, then copy or download the archived Markdown report as point-in-time readiness evidence. Database-backed local profiles should persist these readiness snapshots across backend restarts.
- Inspect whether recent demo readiness snapshots are improving, stable, or regressing, and copy the Markdown trend report before a live run.
- Archive the current demo session report into a recent list, including prepared demo launch command and archived outcome context when supplied by the dashboard, and copy or download archived Markdown reports during or after a live demo. Database-backed local profiles should persist these archives across backend restarts.
- Inspect queue worker readiness, last poll age, and operator action before a live issue-to-PR demo.
- Evaluate a manual `/agent fix` trigger before creating a task and see the gate decisions plus next operator action.
- Evaluate a pasted GitHub webhook payload before redelivery and see whether the temporary URL, webhook secret, event type, action, and `/agent fix` comment shape look correct.
- Inspect webhook setup readiness before a live trigger and see whether the secret, public payload URL, and latest delivery state are ready, blocked, or need redelivery attention.
- Run the live launch gate before posting a real GitHub issue comment and see the final aggregated `READY`, `NEEDS_ATTENTION`, or `BLOCKED` decision, with self-hosted launch readiness, webhook setup, live publish preflight, live trigger dry-run checks, next actions, and a copyable Markdown report.
- Inspect and copy a single adapter readiness report covering supported languages, allowlisted verification commands, fixture pass rate, and fixture failures.
- Inspect and copy a read-only evaluation case catalog, readiness summary, fixture-readiness report, and run preview covering supported Java/Maven, Node/npm, Python/pytest, Go, unsafe-trigger rejection, and vague-trigger rejection scenarios.
- Copy a Markdown repository preflight report after checking a local path so supported and unsupported repository evidence can be shared before task creation.

The frontend does not need to trigger the first backend workflow. GitHub issue comments remain the first trigger.

## Future Scope

Planned follow-up capabilities:

- Label trigger such as `ai-fix`.
- Chrome extension button on GitHub issue pages.
- Command safety gate for authorization, actionability, unsupported repositories, and unsafe requests.
- Language adapter foundation.
- Additional custom runner support.
- Docker sandbox execution.
- MySQL-backed durable task history.
- Redis or queue-backed async execution.
- RAG over repository code and previous fixes.
- Provider-neutral model gateway with prompt versioning, structured output validation, retries, fallback policy, and model capability metadata.
- Evaluation harness for issue-to-PR benchmark cases across supported repository adapters, starting with a case catalog, fixture baseline, local run archive, and safety-rejection coverage before hosted model benchmark execution.
- Retrieval audit records and optional embedding-backed code search behind a pluggable vector-store boundary.
- Prompt regression tests and model/prompt comparison reports.
- Per-task, per-repository, and per-instance model budget controls.
- Cost, latency, success-rate, and test-pass-rate dashboards.
- Human approval for high-risk actions.
- Durable multi-instance worker telemetry.

## Success Criteria

PatchPilot MVP is successful when:

- A user can install the GitHub App on a test repository.
- A user can comment `/agent fix` on an open issue.
- PatchPilot creates and executes a fix task asynchronously.
- PatchPilot clones the repository and creates a branch.
- PatchPilot generates a patch for a simple supported Java, Node.js, or Python bug.
- Adapter-selected verification runs and the result is recorded.
- A successful task creates a Pull Request.
- GitHub issue comments and Pull Request bodies can link back to the matching dashboard task detail page when the operator configures a public Dashboard URL.
- An operator can verify demo readiness through a single evidence bundle covering setup, safety, queue, webhook setup readiness, latest delivery, recent delivery trail, and recent PR signals.
- An operator can inspect a single demo session snapshot before or after a live run without manually assembling evidence, readiness trend, prepared launch commands, archived launch outcomes, script, runbook, checklist, and health-contract responses.
- An operator can follow an ordered demo script whose endpoint is explicitly read-only and whose steps point to dashboard evidence and curl verification commands.
- An operator can copy a Markdown runbook that explains the current demo status and next actions without manually assembling API responses.
- An operator can copy or download a Markdown session report that includes the snapshot, webhook setup readiness, recent webhook delivery trail, readiness trend status and deltas, handoff readiness checks, prepared launch commands from the current browser, archived launch outcomes from the current browser, script, checklist, health contract, next actions, and runbook.
- An operator can copy or download a Markdown handoff package that combines the session report with a concise summary, handoff readiness, readiness trend evidence, recent task and Pull Request evidence, browser-local prepared commands, browser-local archived outcomes, and next actions.
- An operator can view the same handoff readiness as structured dashboard evidence, including the overall status, summary, and individual check rows from `POST /api/demo/handoff-readiness`, so the preview and Markdown package use one backend rule source.
- An operator can copy or download the latest handoff share checklist and see the same share-readiness summary in the demo evidence bundle before sending the post-demo package.
- An operator can use one final handoff share center to decide whether the archived package should be sent, see which artifacts to download, and export the combined evidence as Markdown before sharing the post-demo package; the same share-center status and download actions are visible in the top-level demo evidence bundle.
- An operator can use one final handoff finalization gate to prove the current package is share-ready, has a fresh delivery receipt, and can be accepted as complete post-demo handoff evidence.
- An operator can copy or download final handoff share instructions that name the intended recipients, attachments, pre-send checks, and message template while keeping actual delivery outside PatchPilot.
- An operator can record and later download a local handoff share delivery receipt that proves who delivered the package, where it was sent, which archive/session it references, and when delivery happened, while PatchPilot still performs no external delivery.
- An operator can verify from the top-level evidence bundle and handoff share center whether the latest handoff package has a recorded delivery receipt, whether that receipt is fresh for the current archive/session, where it was delivered, and which receipt report should be downloaded.
- An operator can use one final self-hosted launch readiness package to decide whether the local demo is ready, needs attention, or is blocked before posting a live trigger or sharing handoff evidence.
- An operator can archive that self-hosted launch readiness package as durable local evidence and retrieve or download it later from the dashboard after refresh or backend restart when using a database-backed profile.
- An operator can use one final demo launch evidence package to share the current demo state, live-run proof, evaluation coverage, post-demo handoff proof, next actions, and read-only side-effect contract without assembling evidence from multiple panels.
- An operator can archive that final launch evidence package as durable local evidence and retrieve or download it from the dashboard after refresh or backend restart when using a database-backed profile.
- An operator can use the launch evidence share center to decide whether the latest archived launch evidence package is ready to share, download the current share-center report, open the archived Pull Request evidence, and see what must be fixed before sharing when the latest archive is missing, not ready, or missing current delivery evidence; the same archive/session/Pull Request/download evidence is visible in the top-level demo evidence bundle.
- An operator can record and later download a local launch evidence delivery receipt that proves who delivered the launch package, where it was sent, which archive/session it references, and when delivery happened, while PatchPilot still performs no external delivery.
- An operator can use one final launch evidence finalization gate to prove the current launch package is share-ready, has a fresh delivery receipt, and can be accepted as complete final launch evidence; the same accepted/not-accepted state is visible in the top-level evidence bundle and copied runbook.
- An operator can use one final launch acceptance closeout to prove launch readiness, evidence package readiness, final handoff report package archive readiness, share-center delivery proof, and finalization all agree before considering the self-hosted issue-to-PR demo evidence complete.
- An operator can archive that final launch acceptance closeout as durable local evidence, including final handoff report package archive proof, see the latest accepted/not-accepted closeout archive in the top-level evidence bundle and copied runbook, and retrieve or download it later from the dashboard after refresh or backend restart when using a database-backed profile.
- An operator can use one final launch acceptance certificate to prove the latest closeout archive is accepted and `READY`, includes final handoff report package archive proof, download the certificate Markdown, and see the blocking next action when no certified archive exists.
- An operator can use one final demo acceptance summary to prove both the latest launch acceptance certificate archive and latest task evidence acceptance certificate archive are certified before sharing the self-hosted issue-to-PR demo externally.
- An operator can use one final demo acceptance share package to copy or download the reviewer-facing message, required attachments, and pre-send checks without PatchPilot sending external messages.
- An operator can archive that final demo acceptance share package as durable local evidence and retrieve or download the exact reviewer-facing package later from the dashboard after refresh or backend restart when using a database-backed profile.
- An operator can record and later download a local final acceptance delivery receipt that proves who delivered the reviewer-facing package, where it was sent, which final acceptance archive/task it references, and when delivery happened, while PatchPilot still performs no external delivery.
- An operator can use one final acceptance share finalization gate to prove the current reviewer-facing package has a fresh delivery receipt before considering the external-review acceptance handoff complete.
- An operator can archive and later download the final acceptance completion proof so the completed external-review handoff survives refreshes and database-backed backend restarts after the live finalization readout changes.
- An operator can download one final acceptance completion evidence bundle that summarizes the latest completion archive, final acceptance package archive, delivery receipt, target/channel, task id, evidence notes, and follow-up downloads as the reviewer-facing completion proof.
- An operator can use one final acceptance completion delivery finalization gate to prove the latest completion evidence delivery receipt matches the current completion evidence bundle before considering the reviewer-facing completion delivery loop closed.
- An operator can use one final acceptance completion closeout report to prove the accepted demo summary, reviewer-package finalization, completion evidence bundle, completion archive, completion evidence delivery receipt, and delivery finalization all agree before calling the self-hosted issue-to-PR demo externally reviewable, and can see that proof from the top-level demo evidence bundle and copied runbook.
- An operator can freeze the READY final acceptance completion closeout as a durable archive and download that exact closeout report later, even after live readiness evidence changes.
- An operator can see the latest frozen final acceptance completion closeout archive from the top-level evidence bundle and copied runbook, including the linked completion archive, delivery receipt, task, Pull Request, archived time, and follow-up download actions.
- An operator can archive the READY final external-review evidence package as the frozen reviewer-facing record, reopen or download recent archived packages after live evidence changes, and see the latest package archive from the top-level evidence bundle and copied runbook.
- An operator can record and download local delivery receipts for the latest frozen final external-review package archive, then see whether the receipt is fresh from the top-level evidence bundle and copied runbook before treating the reviewer package as delivered.
- An operator can archive the READY final external-review package delivery finalization as the frozen delivery-closure record, reopen or download recent archived closure reports after live evidence changes, and see the latest closure archive from the top-level evidence bundle and copied runbook.
- An operator can download a final external-review delivery certificate from the latest archived closure record and use it as the single read-only proof that the reviewer-facing package delivery loop is certified.
- An operator can archive the certified final external-review delivery certificate as the durable terminal proof, reopen recent certificate archives, and download the exact Markdown certificate after live inputs change.
- An operator can see the final external-review release bundle and latest release-bundle archive in the top-level evidence bundle and copied runbook, including release readiness, archive readiness, certificate archive, delivery closure, package archive, delivery receipt, task, Pull Request, required attachments, evidence notes, next action, and download actions before sharing terminal reviewer handoff proof.
- An operator can record and download local delivery receipts for the latest frozen final external-review release-bundle archive, then use one read-only finalization gate to prove that the terminal release bundle was delivered with a fresh receipt before treating the reviewer handoff as complete.
- An operator can download one final external-review release bundle delivery certificate from the latest frozen release-bundle delivery finalization archive and use it as the single read-only terminal reviewer handoff proof.
- An operator can archive the certified final external-review release bundle delivery certificate as the durable terminal proof, reopen recent release-bundle certificate archives, download the exact Markdown certificate after live inputs change, and see the latest terminal certificate archive directly in the top-level evidence bundle and copied runbook.
- An operator can download one final reviewer handoff package from the latest archived terminal release-bundle delivery certificate, see the required attachments and send/no-send checks in the top-level evidence bundle, and use the generated Markdown report as the final reviewer delivery artifact without PatchPilot sending messages or mutating GitHub.
- An operator can archive the current demo readiness gate as a timestamped Markdown report and later prove why the system was ready, warned, or blocked before a live run.
- An operator can compare the two latest archived readiness gates and prove whether setup has improved, stayed stable, or regressed before posting a live `/agent fix` comment.
- An operator can copy an adapter readiness report that proves current multi-language coverage and highlights fixture drift before a live run.
- An operator can inspect evaluation cases that prove planned multi-language issue-to-PR coverage and safety rejection coverage without creating tasks, running model calls, executing tests, mutating Git, or writing to GitHub.
- An operator can inspect an evaluation readiness summary that states whether the current catalog is ready for demo evidence and what follow-up is still needed before automated evaluation runs exist.
- An operator can inspect evaluation fixture readiness that confirms supported cases still map to checked-in fixtures, expected adapter metadata, and expected changed files before using the catalog as demo evidence.
- An operator can copy an evaluation run preview report that packages expected benchmark coverage and known gaps without cloning repositories, running verification commands, or recording a real benchmark run.
- An operator can execute and archive a full local evaluation run that combines checked-in fixture baseline output, supported language/build-system coverage, safety rejection coverage, side-effect contract, next action, and a downloadable Markdown report without creating tasks, calling the model, mutating Git, or writing to GitHub.
- An operator can see missing, failed, or safety-incomplete full evaluation run archive evidence reflected in the demo readiness gate instead of only in the evaluation catalog panel.
- An operator can see whether each adapter's verification executable is available in the current backend runtime before a live run.
- An operator can see missing adapter verification executables reflected in the demo readiness gate instead of only in the adapter report.
- An operator can see fixture baseline regressions reflected in the demo readiness gate instead of only in the evaluation catalog panel.
- An operator can copy a repository preflight report that shows whether a local path is supported and which allowlisted command would run.
- A failed task records and reports a clear failure reason.
- An unsupported repository failure reports the supported adapter matrix back to the GitHub issue without attempting model, test, Git, or Pull Request work.

The broader product is successful when:

- A supported repository can be fixed through the correct language adapter.
- An unsupported repository fails safely with a clear reason.
- A vague, malicious, or unauthorized `/agent` comment does not start repository execution.
- Each successful PR includes evidence from the adapter's verification command.
- Operators can distinguish "queued but worker idle/not started" from "queued and worker actively polling" without reading backend logs.

## Resume Target

The project should support this resume-level description:

```text
Built PatchPilot, a Spring Boot GitHub App that turns issue comments into automated code-fix workflows. The agent validates commands through a safety gate, clones repositories into isolated workspaces, detects supported language adapters, retrieves relevant code context, generates patches, runs allowlisted tests, and opens Pull Requests with execution traces and test summaries.
```
