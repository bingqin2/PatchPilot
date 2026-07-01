# Architecture

## Architectural Style

PatchPilot starts as a modular Spring Boot backend. The first goal is to prove a reliable GitHub issue-to-PR workflow, not to introduce premature microservices.

The MVP should remain a single deployable backend with clear internal boundaries:

```text
GitHub Webhook
      |
PatchPilot API
      |
Fix Task Service
      |
Async Worker
      |
Workspace + Agent + Tools + Test Runner
      |
GitHub Pull Request
```

The system can later split API and worker processes once task execution volume, sandbox isolation, or deployment needs justify it.

## Frontend Role

PatchPilot should use a React frontend for operational visibility and demos. The frontend is not required for the first webhook-to-PR backend loop, but it becomes useful once task records and traces exist.

Recommended first screens:

- Task list.
- Task detail.
- GitHub issue and PR links.
- Status timeline.
- Tool-call trace.
- Model-call summary.
- Verification output.

The frontend should stay work-focused and dense enough for repeated debugging. It should not start as a marketing landing page.

## Runtime Flow

```text
GitHub issue comment created
  -> WebhookController verifies signature
  -> WebhookEventRouter detects /agent fix
  -> CommandSafetyGate rejects unsupported, unauthorized, unsafe, or non-actionable commands
  -> TriggerQuarantineService checks active durable quarantines and rejects repeated abusive rejected-trigger patterns by user or repository
  -> TriggerRateLimitService rejects repeated attempts by issue, trigger user, or repository
  -> TriggerIntentClassifier optionally asks the configured model whether the safe request should execute
  -> RejectedTriggerAuditService records rejected triggering attempts
  -> FixTaskService creates a task
  -> FixTaskTimelineService records accepted trigger decision evidence
  -> TaskWorker runs asynchronously
  -> WorkspaceService clones the repository
  -> LanguageAdapterRegistry rejects unsupported repositories before model patching
  -> IssueAnalyzer gathers issue and repository context
  -> FixIssueAgent creates a fix plan
  -> Tools search code, read files, write files, and inspect diff
  -> GeneratedDiffRiskGate rejects unsafe generated diffs before verification or GitHub writes
  -> PENDING_REVIEW tasks can be cancelled or operator-approved to resume the same workspace after the risk gate
  -> LanguageAdapter supplies an allowlisted verification command
  -> TestRunner runs verification
  -> GitHubClient pushes a branch and opens a Pull Request
  -> GitHubClient comments back on the issue
  -> FixTaskService marks the task completed or failed
```

Manual dashboard task creation follows the same trigger gate sequence. The
`POST /api/tasks/evaluate-trigger` path stops before task creation and returns
the gate decisions as a read-only dry run: it may inspect safety, active-task
state, quarantine state, rate-limit state, and model trigger classification, but
it must not create tasks, queue items, rejected-trigger audit rows, GitHub
comments, webhook delivery diagnostics, or rate-limit records. Evaluation
accepts a source of `MANUAL` or `ISSUE_COMMENT`; `MANUAL` is the default for
older clients, while `ISSUE_COMMENT` previews the same downstream gate source as
GitHub issue-comment handling without replaying a signed webhook request.

Webhook payload diagnostics are separate from real webhook handling.
`POST /api/github/webhook-diagnostics/evaluate-payload` parses a pasted payload
and optional `sha256=...` signature as an admin-protected read-only operator
check. It reports signature status, JSON validity, supported event/action,
`/agent fix` recognition, parsed repository/issue fields, and a next action, but
it must not call `GitHubWebhookService.handle`, create tasks, queue work, record
delivery diagnostics, record rejected triggers, post GitHub comments, consume
rate-limit quota, or call the model.

Webhook URL readiness is another read-only operator check. The backend reads
`patchpilot.github.webhook-public-base-url`, derives the GitHub Payload URL as
`<base>/api/github/webhook`, and probes `<base>/health` through
`GitHubWebhookUrlProbe`. `GET /api/github/webhook-url-readiness` reports the
normalized base URL, payload URL, health URL, status, latency, and next operator
action without validating signatures, posting comments, redelivering events,
creating tasks, or mutating GitHub settings. `DemoReadinessService` and the
dashboard setup checklist reuse this readiness state so stale `cloudflared`
quick-tunnel URLs are visible before an operator posts a live `/agent fix`.

Webhook setup readiness composes the URL readiness result with the non-sensitive
configuration summary and latest delivery diagnostic. `GET
/api/github/webhook-setup-readiness` returns `READY`, `NEEDS_ATTENTION`, or
`BLOCKED` with derived payload and health URLs, webhook-secret configured state,
latest delivery status/id, redelivery recommendation, next actions, and a
copyable Markdown report. It is a read model only: it does not expose secret
values, create tasks, redeliver events, record new delivery diagnostics, write
GitHub comments, mutate GitHub webhook settings, or call the model.

Real webhook delivery diagnostics persist an outcome correlation target for each
handled delivery. Task-created, active-task, and duplicate deliveries point to
the task detail route; rejected deliveries point to the rejected-trigger audit
anchor created during the same webhook handling path; ignored deliveries and
errors keep a typed outcome without introducing another task-triggering path.

Demo handoff package readiness consumes the same recent delivery diagnostics as
read-only evidence. A task-created delivery satisfies the webhook evidence
check, missing delivery history needs attention, and a latest delivery that
requires redelivery blocks the handoff with the diagnostic operator action. This
check does not call GitHub, redeliver events, create tasks, mutate queue state,
archive reports, or write new delivery diagnostics.

Evaluation run archives are PatchPilot-local evidence records. `POST
/api/evaluation/runs` combines the current evaluation catalog preview with the
checked-in fixture baseline runner, records language/build-system coverage,
safety-rejection coverage, local execution counts, side-effect contract, next
action, and a Markdown report, then stores the latest runs through the active
archive repository. It may run adapter-selected commands against checked-in
fixtures, but it must not create tasks, call the model, clone repositories,
mutate Git, push branches, open Pull Requests, send GitHub comments, or write to
GitHub. Listing and report-download endpoints are read-only over the same
archive store.

Full evaluation run readiness is a read model over the same archive store.
`GET /api/evaluation/runs/summary` reads recent archived full evaluation runs,
projects latest and previous digests, pass/fail/skip deltas, language and
build-system coverage, safety-rejection categories, next action, and Markdown
evidence, and does not execute fixture commands. `DemoReadinessService`
consumes this summary as the `Evaluation run archive` check: no archived full
run needs attention, a latest failed or safety-incomplete run blocks the live
demo, and a ready latest run with safety coverage satisfies the check. This
keeps demo readiness tied to archived local evidence without creating tasks,
calling the model, mutating Git, opening Pull Requests, or writing to GitHub.

## Worker Runtime Health

The single-process backend records queue worker heartbeat state in memory through
`FixTaskWorkerHealthService`. `FixTaskQueuePoller` updates this read model when
each poll starts, when no queue item is available, when a queue item is claimed,
when execution completes, and when worker execution fails.
`GET /api/task-queue/worker-health` exposes this runtime state for the dashboard
and curl diagnostics.

The worker-health response also derives `lastPollAgeMs`, `readinessStatus`, and
`operatorAction` from the process-local heartbeat. A worker is demo-ready when it
has reported a recent poll and is not in `ERROR`. It needs attention when it has
not started, has most recently failed, or the last poll is older than
`patchpilot.task.queue.worker-heartbeat-stale-ms`. `DemoReadinessService` uses
this same read model as the `Worker heartbeat` readiness check so a live smoke
run is blocked by stale worker state before the operator posts a GitHub trigger.

This heartbeat is intentionally process-local. Durable task and queue facts
remain in MySQL-backed task and queue records; the heartbeat only answers
whether this backend process's poller is active, idle, not started, or most
recently errored. If PatchPilot later splits API and worker processes or runs
multiple workers, this signal should move to durable or instance-scoped worker
telemetry.

## Backend Modules

Recommended package layout:

```text
io.patchpilot.backend
  common
    config
    error
    response
    time
  github
    auth
    client
    webhook
  task
    controller
    service
      impl
    mapper
    domain
      entity
      dto
      vo
      bo
      query
      enums
    convert
  workspace
    service
      impl
    domain
  agent
    workflow
    prompt
    tool
    domain
  runner
    service
    domain
  audit
    service
    domain
  observability
```

Package-by-domain is preferred. Layers are created inside each domain only when needed.

## Module Responsibilities

### GitHub Webhook

Responsibilities:

- Receive GitHub webhook requests.
- Verify `X-Hub-Signature-256`.
- Expose the configured public webhook base URL and derived payload URL through a read-only readiness probe.
- Route supported events.
- Detect `/agent fix`.
- Reject unsafe, unauthorized, or non-actionable `/agent fix` commands before task creation.
- Optionally ask the configured model to classify safe triggers as executable, needing clarification, or rejected.
- Record rejected triggering attempts for operator inspection, including a stable category such as `NOT_ACTIONABLE`, `DANGEROUS_INSTRUCTION`, `TRIGGER_USER_NOT_ALLOWED`, `REPOSITORY_NOT_ALLOWED`, `RATE_LIMITED`, or model-classifier refusal categories.
- List rejected triggering attempts with optional category filtering for operator diagnosis.
- Summarize recent rejected triggering attempts by category, source, trigger user, and repository so operators can spot abuse or bad prompt patterns without reading every row.
- Apply rejected-trigger quarantine before rate limiting, model classification, task creation, queueing, or workspace work when recent rejected attempts from the same trigger user or repository exceed the configured threshold. Quarantined attempts are recorded as `ABUSE_QUARANTINED`.
- Persist trigger-user and repository quarantine records through `TriggerQuarantineRecordService` so active safety state survives worker restarts in MySQL-backed profiles.
- Expose active or historical quarantine records through `GET /api/trigger-quarantines` for operator diagnosis.
- Expose `GET /api/trigger-quarantines/{id}/evidence` so operators can inspect the rejected-trigger audit rows and manual safety actions behind one quarantine.
- Allow operators to create and release trigger quarantines through admin-token-protected `/api/trigger-quarantines` write endpoints.
- Record manual safety mutations through `OperatorSafetyAuditService` and expose recent rows through `GET /api/operator-safety-audits`.
- Deduplicate delivery ids.
- Record accepted trigger evidence on task timelines, including safety-gate outcome, issue-context load status, and model trigger-classification outcome.
- Submit work to task services.
- Expose a read-only trigger evaluation path for manual/dashboard checks that reuses the task-creation gates without mutating tasks, GitHub, rejected-trigger audits, queue state, or rate-limit counters.

The webhook layer must not run model calls, clone repositories, or execute tests.

### GitHub Client

Responsibilities:

- Generate GitHub App JWTs.
- Fetch and cache installation tokens.
- Read issue metadata.
- Create issue comments.
- Create branches, commits, and Pull Requests through controlled operations.
- Normalize GitHub API errors.

This layer must not contain agent reasoning logic.

### Task

Responsibilities:

- Create fix tasks.
- Store task status and result.
- Manage status transitions.
- Expose task status APIs.
- Coordinate async execution.

Recommended status values:

```text
PENDING
PREPARING_WORKSPACE
ANALYZING_ISSUE
PLANNING_FIX
APPLYING_PATCH
RUNNING_TESTS
CREATING_PR
COMPLETED
FAILED
CANCELLED
```

Status changes should go through a dedicated service instead of direct field mutation across modules.

### Workspace

Responsibilities:

- Create task-scoped workspaces.
- Clone repositories.
- Checkout base branches.
- Create patch branches.
- Read and write files safely.
- Produce git diff summaries.
- Clean up local workspaces when configured.

All file operations must be restricted to the task workspace.

### Agent

Responsibilities:

- Convert issue and repository context into model inputs.
- Produce a structured fix plan.
- Decide which registered tools to call.
- Interpret tool outputs.
- Produce a patch proposal, PR summary, or failure summary.

The agent must not directly access GitHub APIs, the database, or the file system.

### Tools

MVP tools:

- `RepoTreeTool`
- `CodeSearchTool`
- `FileReadTool`
- `FileWriteTool`
- `DiffTool`
- `MavenTestTool`
- `GitTool`
- `GitHubPullRequestTool`
- `GitHubCommentTool`

Tools are the only way for agent reasoning to affect the external world.

### Runner

Responsibilities:

- Use language adapters to detect supported build systems immediately after workspace preparation.
- Fail unsupported repositories before patch generation, test execution, Git mutation, or Pull Request creation.
- Run the generated diff through `GeneratedDiffRiskGate` after `DiffTool` and before verification, commit, push, or Pull Request creation.
- Resume an operator-approved `PENDING_REVIEW` task from the existing task workspace without re-running model patch generation or diff generation.
- Execute the allowlisted verification command returned by the selected adapter.
- Capture test output.
- Enforce timeouts.
- Register running verification processes for cancellation.
- Remove PatchPilot secrets from the child-process environment.
- Return structured test results.

The adapter registry selects the first supported adapter and returns a clear unsupported result when none match. `VerificationRunner` executes the selected adapter command. The first adapters are `JavaMavenLanguageAdapter`, `JavaGradleLanguageAdapter`, `GoLanguageAdapter`, `NodeBunLanguageAdapter`, `NodePnpmLanguageAdapter`, `NodeYarnLanguageAdapter`, `NodeNpmLanguageAdapter`, `PythonToxLanguageAdapter`, `PythonNoxLanguageAdapter`, `PythonHatchLanguageAdapter`, `PythonPoetryLanguageAdapter`, `PythonUvLanguageAdapter`, and `PythonPytestLanguageAdapter`. MVP supported commands:

```bash
./mvnw test
mvn test
./gradlew test
gradle test
go test ./...
bun test
npm test
pnpm test
yarn test
tox
nox
hatch test
python3 -m pytest
poetry run pytest
uv run pytest
```

Future adapters should add their own detection and allowlisted verification commands without allowing arbitrary user-supplied shell.

`GET /api/language-adapters` exposes the supported adapter catalog for operators and demos. `GET /api/language-adapters/fixtures` runs each checked-in demo fixture through the same registry and returns expected versus actual language, build system, command, detection reason, and pass/fail status. Fixture failures are reported as rows, not controller failures, so one missing fixture does not hide the rest of the support matrix. `GET /api/language-adapters/runtime-readiness` checks whether the first executable in each allowlisted verification command is available on the backend process `PATH`. It does not run adapter verification commands, create tasks, mutate repositories, write to GitHub, or call the model.

`POST /api/repository-preflight` accepts a local repository path, resolves it from the backend working directory when relative, verifies that the resolved path is under `patchpilot.repository-preflight.allowed-root-dirs`, and runs only adapter detection. It returns supported status, language, build system, verification command, detection reason, operator action, and the supported adapter catalog for unsupported results. This endpoint is an operator diagnostic for local fixtures and workspaces; it does not create a task, run verification commands, mutate Git, call the model, or write to GitHub. `GET /api/configuration/summary` exposes the normalized allowed roots and queue worker heartbeat stale threshold without exposing secrets so operators can see which local paths the diagnostic may inspect and how quickly stale worker polling is flagged.

Real task records also persist the selected adapter metadata and nullable detection reason. The executor stores the `LanguageDetectionResult.reason()` alongside `language`, `buildSystem`, and `verificationCommand` after workspace preflight. Task list and detail APIs return those fields so operators can explain which repository signal selected the verification path without replaying detection or reading logs. Task detail and Markdown reports additionally derive an adapter execution evidence block for supported, pending, and unsupported states, including the safe-command boundary and supported adapter options when execution stopped before model generation, tests, Git mutation, push, or Pull Request creation.

Generated-diff risk checks run after patch generation and `DiffTool` output, but before adapter verification or any GitHub write. `GeneratedDiffRiskGate` uses `GeneratedDiffSafetyPolicy` to block sensitive paths, secret-like added lines, binary patches, and patches that exceed changed-file or changed-line thresholds. The same policy also protects planned patch targets before model edits or direct replacement instructions can write files, so `.env`, `.git/`, workflow files, package-manager credential files, key stores, and private key material are guarded consistently. The worker maps deterministic generated-diff rejections to `PENDING_REVIEW`, records a `PENDING_REVIEW` timeline event, and updates the same GitHub status comment instead of treating the task as an ordinary verification failure. The executor still records the gate as an audited tool call, so the exact rejection is visible in task detail APIs, copied task reports, and the dashboard. Approval requires an operator from `patchpilot.review-approval.allowed-operators` and a reason, persists that audit metadata on the task, records it in the timeline, and then resumes the same workspace after the already-reviewed risk gate. If the approver allowlist is empty, approval is disabled instead of falling back to arbitrary operator text.

### Demo Readiness

Responsibilities:

- Aggregate non-mutating setup and health evidence before a live issue-to-PR demo.
- Report a single `READY`, `NEEDS_ATTENTION`, or `BLOCKED` status.
- Surface required credential gaps without exposing secret values.
- Surface safety policy gaps for trigger-user allowlists, repository allowlists, and review-approval approvers.
- Surface generated-diff policy state without exposing secrets or editable policy internals.
- Expose a demo evidence bundle that joins readiness, smoke-check, configuration, adapter fixture, full evaluation run readiness, queue, webhook setup, recent webhook delivery trail, rejected-trigger, quarantine, and recent Pull Request evidence.
- Expose a demo session snapshot that combines one current evidence bundle, script, runbook, operator checklist, health contract, share summary, and next actions.
- Expose a demo script that turns the current evidence bundle into ordered operator actions, verification commands, success criteria, troubleshooting panels, and a health contract.
- Format the evidence bundle as copyable Markdown for operator handoff without adding side effects.
- Format the session snapshot as copyable or downloadable Markdown for review notes, issue comments, saved files, or handoff messages without adding side effects. Dashboard clients may supply browser-local prepared launch commands and archived launch outcomes as report context; the backend includes that bounded context plus a computed handoff readiness check in the generated Markdown but does not persist browser history separately.
- Format a handoff package as copyable or downloadable Markdown that wraps the session report with concise status, handoff readiness, task, Pull Request, prepared-command, archived-outcome, and next-action evidence for post-demo sharing.
- Expose the handoff readiness calculation as structured JSON through `/api/demo/handoff-readiness` so dashboards and generated Markdown share one backend source of truth while still accepting bounded browser-local report context.
- Store a capped archive of recent demo session reports, including supplied prepared launch command and archived outcome context, so an operator can keep copyable and downloadable handoff records during a live demo. The default profile may keep archives in memory, while database-backed profiles persist them through MySQL.
- Store a separate capped archive of recent demo handoff packages so the final post-demo package is preserved independently from session reports. Each package archive also stores the computed handoff readiness status, summary, next action, and ready/warning/blocked check counts from archive time. A read-only archive summary reports whether the latest package archive is share-ready, which archive is latest, the archived readiness status, the next action, and portable Markdown evidence without creating another archive; the dashboard can copy the loaded report or download the same summary through a read-only Markdown attachment endpoint. A read-only handoff share checklist converts that latest archive summary into explicit share-readiness checks and copyable/downloadable Markdown evidence so operators can tell whether the package should be sent, fixed, or blocked. A read-only handoff share center combines the latest archive summary, share checklist, and latest local delivery receipt into one final send/no-send status, download action list, delivery evidence notes, and Markdown attachment for post-demo sharing; it marks receipt evidence as `MISSING`, `FRESH`, or `STALE` by comparing the receipt archive/session ids with the current latest archive summary. A read-only handoff finalization gate reuses that share center and reports `READY` only when the package is share-ready and the latest delivery receipt is fresh for the current archive/session; it also exports a Markdown acceptance report. A read-only handoff share instructions view derives recommended recipients, required attachments, pre-send checks, source archive/session ids, and a message template from that center without sending anything externally. A local handoff share delivery receipt store records operator-entered evidence that the prepared package was delivered through an external channel, links the receipt to the current archive/session ids, records protected admin audit evidence, and exposes recent receipts plus Markdown receipt downloads. The top-level demo evidence bundle includes the checklist status plus the share-center current status, summary, next action, download actions, latest delivery receipt summary and freshness status, and finalization status so operators can see final share, delivery, and acceptance readiness without opening the session snapshot first. The same profile split applies: the default profile keeps package archives and delivery receipts in memory, and `local`, `docker`, and `idea` profiles persist them through MySQL.
- Expose a final self-hosted launch readiness package through `/api/demo/self-hosted-launch-readiness` and `/api/demo/self-hosted-launch-readiness/report/download`. This read model composes `DemoReadinessService` and `DemoEvidenceBundleService` output, then projects the handoff finalization, credential, webhook setup, and queue/worker launch checks into one `READY`, `NEEDS_ATTENTION`, or `BLOCKED` status with Markdown evidence.
- Store a capped archive of recent self-hosted launch readiness packages so the exact pre-launch decision can be reopened after runtime state changes. The archive stores status, launch-ready flag, summary, ready/warning/blocked check counts, created time, and Markdown report. The default profile may keep archives in memory, while `local`, `docker`, and `idea` profiles persist them through MySQL.
- Expose a final demo launch evidence package through `/api/demo/launch-evidence-package` and `/api/demo/launch-evidence-package/report/download`. This read model composes self-hosted launch readiness and the current demo session snapshot, then projects pre-launch checks, live task/Pull Request/webhook proof, full evaluation run coverage, post-demo handoff proof, next actions, and a read-only side-effect contract into one shareable status and Markdown artifact.
- Store a capped archive of recent final demo launch evidence packages so the exact shareable package can be reopened after webhook, task, handoff, or evaluation state changes. The archive stores status, share-ready flag, summary, session id, launch/evidence/finalization statuses, key live-run identifiers, created time, and Markdown report. The default profile may keep archives in memory, while `local`, `docker`, and `idea` profiles persist them through MySQL.
- Expose a read-only demo launch evidence share center through `/api/demo/launch-evidence-share-center` and `/api/demo/launch-evidence-share-center/report/download`. This read model derives from the latest launch evidence package archive plus the latest local launch delivery receipt, reports final share/no-share status, archive count, latest task/Pull Request/webhook/evaluation identifiers, download actions, evidence notes, receipt-recorded state, receipt freshness, and a portable Markdown report. The top-level demo evidence bundle repeats the launch share-center status, share-ready flag, summary, next action, archive count, latest archive/session/Pull Request identifiers, and download actions so the first dashboard readout can answer whether final launch evidence is shareable. It does not create or refresh archives, so operators can validate exactly what has already been preserved before sharing final launch evidence.
- Expose a read-only demo launch evidence finalization gate through `/api/demo/launch-evidence-finalization` and `/api/demo/launch-evidence-finalization/report/download`. This gate reuses the launch evidence share center and reports `READY` only when the current archived launch package is share-ready and the latest local delivery receipt is fresh for the same archive/session; it exports a Markdown acceptance report for final demo delivery evidence. The top-level demo evidence bundle reuses the same finalization service and repeats status, finalized flag, accepted receipt id, receipt freshness, summary, and next action so the first dashboard/readbook readout has the same launch acceptance source of truth.
- Store a capped list of local demo launch evidence delivery receipts through in-memory storage by default and MySQL-backed repositories for `local`, `docker`, and `idea` profiles. Receipt creation records operator-entered channel, target, operator, notes, delivered time, source archive/session ids, protected admin audit evidence, and a Markdown receipt report without sending anything externally.
- Store a capped list of local external exposure sessions through in-memory storage by default and MySQL-backed repositories for `local`, `docker`, and `idea` profiles. Session creation is allowed only after the external exposure handoff package is ready, then records operator-entered public URL, webhook URL, purpose, operator, optional shutdown target, notes, linked handoff status, linked readiness archive id, and a Markdown report. Closing a session updates the same local evidence record with closer, close time, close notes, and a refreshed report without probing the URL or mutating GitHub. A read-only external exposure closeout gate consumes the latest session and current handoff package, reports `BLOCKED` while a public URL session is active, `NEEDS_ATTENTION` when close evidence or handoff evidence is incomplete, and `READY` only when closed session evidence and current handoff readiness are complete. A capped closeout archive store freezes exact closeout snapshots with default in-memory storage and MySQL-backed persistence for `local`, `docker`, and `idea` profiles so shutdown proof survives later live-state changes. The top-level demo evidence bundle and copied runbook project the latest closeout archive into archive status, closeout-ready flag, session/public/webhook URL evidence, linked readiness archive id, freshness, next action, and download actions so temporary public URL shutdown proof is visible without opening the external exposure workspace first.
- Expose a read-only external exposure operator handoff checklist through `/api/security/external-exposure-operator-handoff-checklist` and `/api/security/external-exposure-operator-handoff-checklist/report/download`. This read model composes the latest closeout archive, current exposure handoff package, recent exposure sessions, and live GitHub publish preflight for the configured demo repository into one go/no-go result before the next live `/agent fix` trigger.
- Treat adapter fixture drift as blocking because repository support may be misdetected.
- Treat queue failures, delayed work, running work, missing model cost configuration, or missing recent PR evidence as operator attention items.
- Return concrete next actions for the dashboard and curl users.

This layer must not create tasks, call the model, clone repositories, execute tests, mutate queue state, write comments, or weaken safety gates. Its evidence bundle, session snapshot, session report, session report download, archived report download, handoff package archive summary, handoff package archive summary download, handoff share checklist, handoff share checklist download, handoff share center, handoff share center download, handoff finalization, handoff finalization download, handoff share delivery receipt list/download, self-hosted launch readiness package, launch evidence package, launch evidence share center, launch evidence finalization, launch evidence delivery receipt list/download, self-hosted launch readiness archive list/download, external exposure readiness archive list/download, external exposure handoff package, external exposure session list/download, external exposure closeout, external exposure closeout download, external exposure closeout archive list/download, external exposure operator handoff checklist, launch evidence package archive list/download, final external-review release bundle, final external-review release bundle archive list/download, final external-review release bundle delivery receipt list/download, final external-review release bundle delivery finalization, final external-review release bundle delivery finalization archive list/download, final external-review release bundle delivery certificate, final external-review release bundle delivery certificate archive list/download, final reviewer handoff package, demo script, runbook, and webhook payload diagnostic endpoints are read models over configuration, fixture verification, queue state, rejected-trigger safety state, webhook diagnostics, recent webhook delivery trail, recent task history, pasted payload metadata, and stored demo archives, receipts, or exposure sessions. The external exposure handoff package is read-only over the current exposure readiness gate and latest external exposure readiness archive so missing or stale archive evidence is visible before a temporary URL is shared. The external exposure closeout gate is read-only over the latest exposure session and handoff package so operators can prove a temporary URL was closed without probing the URL or mutating GitHub. The external exposure operator handoff checklist is read-only over closeout archive proof, handoff state, session state, and live publish preflight so operators can decide whether the next live trigger is safe without mutating archive state. The top-level evidence bundle and runbook additionally aggregate the latest external exposure closeout archive, latest release-bundle delivery certificate archive, and final reviewer handoff package so temporary public URL shutdown proof and durable terminal reviewer handoff proof are visible without mutating archive state.
Creating a session archive, handoff package archive, handoff share delivery receipt, self-hosted launch readiness archive, external exposure readiness archive, external exposure session, external exposure closeout archive, launch evidence package archive, launch evidence delivery receipt, final external-review release bundle archive, final external-review release bundle delivery receipt, final external-review release bundle delivery finalization archive, final external-review release bundle delivery certificate archive, or other explicit demo/security evidence archive is a local write in this layer. These actions capture generated or operator-entered Markdown evidence in the configured local store and still do not create tasks, call the model, run tests, mutate Git, send external messages, write GitHub comments, edit GitHub webhook settings, probe public URLs, or open Pull Requests.

## Data Model

The first durable model should include:

### FixTask

Represents a single issue-to-PR attempt.

Important fields:

- `id`
- `repositoryOwner`
- `repositoryName`
- `issueNumber`
- `installationId`
- `triggerUser`
- `triggerCommentId`
- `status`
- `failureReason`
- `createdAt`
- `startedAt`
- `finishedAt`
- `pullRequestUrl`

### TriggerQuarantine

Represents an explicit rejected-trigger quarantine for a trigger user or repository.

Important fields:

- `id`
- `scope`
- `scopeKey`
- `reason`
- `category`
- `evidenceCount`
- `windowMs`
- `startedAt`
- `expiresAt`
- `createdAt`
- `updatedAt`
- `createdBy`
- `releasedAt`
- `releasedBy`
- `releaseReason`

### ToolCallRecord

Represents an audited tool invocation.

Important fields:

- `id`
- `taskId`
- `toolName`
- `inputSummary`
- `outputSummary`
- `success`
- `durationMs`
- `createdAt`

### ModelCallRecord

Represents an audited model invocation.

Important fields:

- `id`
- `taskId`
- `model`
- `promptSummary`
- `outputSummary`
- `inputTokens`
- `outputTokens`
- `durationMs`
- `success`
- `createdAt`

### TestRunRecord

Represents a test command result.

Important fields:

- `id`
- `taskId`
- `command`
- `exitCode`
- `stdoutSnippet`
- `stderrSnippet`
- `durationMs`
- `createdAt`

## Technology Choices

### Current Baseline

- Java 17
- Spring Boot 3.5.15
- Maven

### Recommended MVP Additions

- Spring Web
- Spring Validation
- Spring Boot Actuator
- MySQL for early durable persistence
- JUnit 5
- Mockito
- GitHub REST client wrapper
- OpenAI Java SDK or Spring AI

### Model Integration Choice

The MVP can start with a direct OpenAI-compatible Java client and a lightweight in-house workflow. This keeps the first implementation understandable and resume-friendly.

Spring AI can be introduced later when the project needs richer tool abstractions, RAG, vector stores, model portability, and observation integration.

## Security Boundaries

- Verify all GitHub webhooks.
- Do not log GitHub private keys, installation tokens, or model API keys.
- Keep secrets in environment-specific configuration.
- Restrict file operations to the task workspace.
- Restrict command execution to an allowlist.
- Do not let the model create arbitrary shell commands.
- Do not auto-merge Pull Requests.

## Observability

Each task should record:

- Status transitions.
- GitHub repository and issue number.
- Trigger user.
- Model name and token usage.
- Tool call count and summaries.
- Changed files.
- Test command and result.
- Created PR URL.
- Failure reason.

## Future Split Path

The first useful runtime split is:

```text
patchpilot-api      webhook and status APIs
patchpilot-worker   repository clone, agent execution, testing, PR creation
```

Do not split earlier unless there is a concrete need for independent scaling, stricter sandboxing, or separate deployment.
