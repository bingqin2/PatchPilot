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
- Submit work to task services.

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

`GET /api/language-adapters` exposes the supported adapter catalog for operators and demos. `GET /api/language-adapters/fixtures` runs each checked-in demo fixture through the same registry and returns expected versus actual language, build system, command, detection reason, and pass/fail status. Fixture failures are reported as rows, not controller failures, so one missing fixture does not hide the rest of the support matrix.

`POST /api/repository-preflight` accepts a local repository path, resolves it from the backend working directory when relative, verifies that the resolved path is under `patchpilot.repository-preflight.allowed-root-dirs`, and runs only adapter detection. It returns supported status, language, build system, verification command, detection reason, operator action, and the supported adapter catalog for unsupported results. This endpoint is an operator diagnostic for local fixtures and workspaces; it does not create a task, run verification commands, mutate Git, call the model, or write to GitHub. `GET /api/configuration/summary` exposes the normalized allowed roots and queue worker heartbeat stale threshold without exposing secrets so operators can see which local paths the diagnostic may inspect and how quickly stale worker polling is flagged.

Real task records also persist the selected adapter metadata and nullable detection reason. The executor stores the `LanguageDetectionResult.reason()` alongside `language`, `buildSystem`, and `verificationCommand` after workspace preflight. Task list and detail APIs return those fields so operators can explain which repository signal selected the verification path without replaying detection or reading logs.

Generated-diff risk checks run after patch generation and `DiffTool` output, but before adapter verification or any GitHub write. `GeneratedDiffRiskGate` uses `GeneratedDiffSafetyPolicy` to block sensitive paths, secret-like added lines, binary patches, and patches that exceed changed-file or changed-line thresholds. The same policy also protects planned patch targets before model edits or direct replacement instructions can write files, so `.env`, `.git/`, workflow files, package-manager credential files, key stores, and private key material are guarded consistently. The worker maps deterministic generated-diff rejections to `PENDING_REVIEW`, records a `PENDING_REVIEW` timeline event, and updates the same GitHub status comment instead of treating the task as an ordinary verification failure. The executor still records the gate as an audited tool call, so the exact rejection is visible in task detail APIs, copied task reports, and the dashboard. Approval requires an operator from `patchpilot.review-approval.allowed-operators` and a reason, persists that audit metadata on the task, records it in the timeline, and then resumes the same workspace after the already-reviewed risk gate. If the approver allowlist is empty, approval is disabled instead of falling back to arbitrary operator text.

### Demo Readiness

Responsibilities:

- Aggregate non-mutating setup and health evidence before a live issue-to-PR demo.
- Report a single `READY`, `NEEDS_ATTENTION`, or `BLOCKED` status.
- Surface required credential gaps without exposing secret values.
- Surface safety policy gaps for trigger-user allowlists, repository allowlists, and review-approval approvers.
- Surface generated-diff policy state without exposing secrets or editable policy internals.
- Expose a demo evidence bundle that joins readiness, smoke-check, configuration, adapter fixture, queue, webhook, rejected-trigger, quarantine, and recent Pull Request evidence.
- Expose a demo session snapshot that combines one current evidence bundle, script, runbook, operator checklist, health contract, share summary, and next actions.
- Expose a demo script that turns the current evidence bundle into ordered operator actions, verification commands, success criteria, troubleshooting panels, and a health contract.
- Format the evidence bundle as copyable Markdown for operator handoff without adding side effects.
- Format the session snapshot as copyable or downloadable Markdown for review notes, issue comments, saved files, or handoff messages without adding side effects.
- Store a capped archive of recent demo session reports so an operator can keep copyable and downloadable handoff records during a live demo. The default profile may keep archives in memory, while database-backed profiles persist them through MySQL.
- Treat adapter fixture drift as blocking because repository support may be misdetected.
- Treat queue failures, delayed work, running work, missing model cost configuration, or missing recent PR evidence as operator attention items.
- Return concrete next actions for the dashboard and curl users.

This layer must not create tasks, call the model, clone repositories, execute tests, mutate queue state, write comments, or weaken safety gates. Its evidence bundle, session snapshot, session report, session report download, archived report download, demo script, and runbook endpoints are read models over configuration, fixture verification, queue state, rejected-trigger safety state, webhook diagnostics, recent task history, and stored demo archives.
Creating a session archive is the only local write in this layer. It captures the generated session report in the configured archive store and still does not create tasks, call the model, run tests, mutate Git, write GitHub comments, or open Pull Requests.

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
