# PatchPilot

PatchPilot is an AI GitHub issue-to-PR agent backend. It receives a GitHub issue comment, creates a durable fix task, inspects the repository through controlled tools, applies a focused patch, runs adapter-selected verification, and opens a Pull Request for human review.

PatchPilot is not a chatbot and does not auto-merge code. The current target is local self-hosted development and private demos.

## What Works Now

- GitHub webhook endpoint for `issue_comment.created`.
- `/agent fix` trigger detection.
- Safety gate rejection for vague, unsafe, or non-actionable `/agent fix` instructions before task creation.
- Optional trigger-user and repository allowlists for webhook and manual task creation.
- Trigger rate limiting by trigger user, repository, and issue before model calls or task creation.
- Read-only trigger evaluation for manual/dashboard `/agent fix` checks before task creation.
- Copyable manual trigger evaluation reports that summarize dry-run status, source, gate decisions, issue-context state, and next action.
- Webhook signature verification.
- Webhook delivery diagnostics for recent task-created, ignored, rejected, duplicate, bad-request, and invalid-signature outcomes.
- Safe GitHub issue refusal comments for rejected `/agent fix` triggers, including category, reason, and next action without echoing unsafe trigger text.
- Dashboard visibility for rejected `/agent fix` triggers that were refused before task creation.
- Rejected-trigger retry preflight that disables unsafe or already-consumed retry attempts and explains the required operator action.
- Dashboard trigger-decision visibility that pairs the selected task's accepted trigger evidence with recent rejected trigger decisions.
- Accepted-trigger intent audit in task detail, copied Markdown reports, and the dashboard, including safety-gate result, issue-context load status, and model trigger-classification outcome.
- MySQL-backed task, queue, timeline, test-run, tool-call, and model-call records.
- Runtime worker heartbeat visibility for the queue poller, including poll counts, claimed tasks, latest worker error, readiness status, last poll age, and operator action.
- Local workspace clone, branch, diff, commit, push, and Pull Request creation.
- Java/Maven, Java/Gradle, Go, Node/Bun, Node/npm, Node/pnpm, Node/yarn, Python/tox, Python/nox, Python/hatch, Python/Poetry, Python/uv, and Python/pytest language adapters backed by an adapter-driven verification runner with command allowlists.
- Generated diff risk gate that blocks sensitive files, secret-like added lines, binary patches, and overly broad patches before tests, commits, pushes, or Pull Request creation.
- Human approval flow for generated-diff risk rejections: `PENDING_REVIEW` tasks expose the generated diff for inspection, then can be cancelled or explicitly approved to resume from the already-generated workspace and continue verification, commit, push, and Pull Request creation.
- Unsupported repository preflight that fails before patch generation, test execution, Git mutation, or Pull Request creation.
- OpenAI-compatible model client and plan-driven patch workflow.
- Issue comment status updates for accepted, running, verification, success, and failure states, including best-effort failure feedback creation when the original status comment is missing.
- Failed and cancelled task retry preflight that explains whether retry is safe, shows sanitized failure context, blocks blind retries when GitHub permissions or repository support must be fixed first, and requires an operator reason before requeueing.
- Demo readiness gate that summarizes credentials, adapter fixtures, queue health, worker heartbeat readiness, and recent PR evidence before a live smoke run.
- Live demo smoke checklist that turns readiness, webhook delivery, task execution, and Pull Request evidence into ordered operator steps.
- Demo evidence bundle that combines readiness, smoke-check, configuration, adapter, queue, webhook, rejected-trigger, quarantine, and recent Pull Request evidence for one demo-ready summary.
- Demo script endpoint and dashboard panel that turn the current evidence bundle into ordered live-demo actions, verification commands, troubleshooting panels, and a read-only health contract.
- Copyable demo runbook Markdown that exports the evidence bundle into a concise operator handoff report.
- Copyable and downloadable demo session report Markdown that exports the full session snapshot, script, checklist, health contract, next actions, and embedded runbook.
- Demo session archive that stores the latest copyable and downloadable session reports for live-demo handoff without creating tasks or mutating GitHub; database-backed profiles persist archives across backend restarts.
- Adapter readiness report that summarizes supported languages, allowlisted verification commands, fixture pass rate, and fixture failures, with copyable Markdown for demo handoff.
- Copyable repository preflight report that exports local path support status, selected adapter, allowlisted command, allowed roots, and unsupported-repository guidance before task creation.

## Repository Layout

```text
.
├── PatchPilot/        # Spring Boot backend
├── docs/              # Product docs, plans, progress logs, and operator guides
│   └── demo-repositories/ # Minimal adapter detection fixtures
├── frontend/          # React operations dashboard
├── docker-compose.yml # Local MySQL + backend runtime
└── .env.example       # Self-hosted configuration template
```

Useful docs:

- `docs/agent/temporary-url-webhook.md` - Cloudflare Tunnel webhook setup.
- `docs/agent/idea-local-run.md` - IntelliJ IDEA local run notes.
- `docs/agent/smoke-test-checklist.md` - End-to-end validation checklist.
- `docs/agent/adapter-smoke-checklist.md` - Local adapter fixture smoke checklist.
- `docs/product/spec.md` - Product scope and non-goals.
- `docs/product/architecture.md` - Backend architecture.

## Prerequisites

- Java 17+
- Maven, or the Maven wrapper in `PatchPilot/`
- Docker Desktop
- `cloudflared` for temporary webhook URLs
- A GitHub repository for testing
- A GitHub token with repository contents, issue comment, and Pull Request permissions
- An OpenAI-compatible API key for model-backed planning

## Configuration

Create a local environment file from the template:

```bash
cp .env.example .env
```

Fill in at least:

```bash
PATCHPILOT_GITHUB_WEBHOOK_SECRET=replace-with-a-random-secret
PATCHPILOT_GITHUB_TOKEN=github_pat_or_fine_grained_token
PATCHPILOT_AGENT_API_KEY=your_model_provider_api_key
PATCHPILOT_ADMIN_TOKEN=replace-with-a-random-admin-token
```

The webhook secret must match the GitHub webhook configuration. The GitHub token is used for clone, push, issue comments, and Pull Request creation. The admin token protects operator APIs such as task listing, queue inspection, manual task creation, retry, cancel, and risk-review approval when the backend is exposed through a temporary URL. Do not commit `.env`.

Local repository preflight is limited to configured backend-local root directories:

```bash
PATCHPILOT_REPOSITORY_PREFLIGHT_ALLOWED_ROOT_DIRS=.,docs/demo-repositories,/tmp/patchpilot/workspaces
```

Keep this list narrow. `POST /api/repository-preflight` rejects paths outside these roots before adapter detection.

Optional safety allowlists can restrict who and what repository may create tasks:

```bash
PATCHPILOT_ALLOWED_TRIGGER_USERS=bingqin2,local-operator
PATCHPILOT_ALLOWED_REPOSITORIES=bingqin2/PatchPilot
PATCHPILOT_REVIEW_APPROVAL_ALLOWED_OPERATORS=release-captain,local-operator
```

Leave either trigger value empty to keep that task-creation dimension unrestricted for local development. When configured, both GitHub webhooks and manual dashboard tasks are checked before task creation.
Review approval is stricter: leave `PATCHPILOT_REVIEW_APPROVAL_ALLOWED_OPERATORS` empty to disable `PENDING_REVIEW` approvals until an explicit approver allowlist is configured.

PatchPilot only creates a task when the `/agent fix` comment is actionable. Use a concrete file operation, file path, or failure description:

```text
/agent fix touch docs/demo.md
/agent fix replace docs/demo.md PatchPilot smoke test
/agent fix build fails with NullPointerException in task worker
```

Vague comments such as `/agent fix`, `/agent fix help`, or `/agent fix make it better` are rejected before task creation and appear in `/api/rejected-triggers`. For GitHub webhook triggers, PatchPilot also posts a refusal comment on the issue with the stable rejection category, safe reason, and next action. The refusal comment intentionally does not echo the original trigger body.

You can also enable model-assisted trigger classification after the deterministic safety gate:

```bash
PATCHPILOT_MODEL_TRIGGER_CLASSIFICATION_ENABLED=true
```

When enabled, PatchPilot asks the configured OpenAI-compatible model whether a safe `/agent fix` request is concrete enough to execute. The model cannot override deterministic safety rejections for unauthorized users, unauthorized repositories, destructive instructions, secret exfiltration, or unsupported commands. If the model call fails or returns invalid JSON, PatchPilot rejects the trigger conservatively and records the reason in `/api/rejected-triggers`.

Trigger rate limiting is enabled by default to protect the local agent from repeated `/agent fix` attempts before model calls or task creation:

```bash
PATCHPILOT_TRIGGER_RATE_LIMIT_ENABLED=true
PATCHPILOT_TRIGGER_RATE_LIMIT_WINDOW_MS=600000
PATCHPILOT_TRIGGER_RATE_LIMIT_MAX_PER_TRIGGER_USER=30
PATCHPILOT_TRIGGER_RATE_LIMIT_MAX_PER_REPOSITORY=60
PATCHPILOT_TRIGGER_RATE_LIMIT_MAX_PER_ISSUE=20
```

Rate-limited webhook and manual API requests are rejected and recorded in `/api/rejected-triggers`. The current implementation is an in-memory single-instance guard for self-hosted local runs.

PatchPilot also uses recent rejected-trigger audit records to quarantine repeated abusive or low-quality trigger sources before rate-limit checks, model classification, task creation, cloning, or queueing:

```bash
PATCHPILOT_REJECTED_TRIGGER_QUARANTINE_ENABLED=true
PATCHPILOT_REJECTED_TRIGGER_QUARANTINE_WINDOW_MS=600000
PATCHPILOT_REJECTED_TRIGGER_QUARANTINE_THRESHOLD=5
PATCHPILOT_REJECTED_TRIGGER_QUARANTINE_COOLDOWN_MS=1800000
```

When a trigger user or repository crosses the threshold, PatchPilot stores or extends a quarantine record and later webhook and manual requests are rejected with the `ABUSE_QUARANTINED` category until the cooldown expires. Operators can also manually create or release trigger-user and repository quarantines through `/api/trigger-quarantines` and the dashboard. This is a self-hosted and private-demo safeguard, not a public hosted reputation system.

For a fine-grained GitHub token, grant these repository permissions:

- `Contents`: Read and write
- `Issues`: Read and write, required for PatchPilot status comments on issues
- `Pull requests`: Read and write
- `Metadata`: Read-only

After changing token permissions or replacing `PATCHPILOT_GITHUB_TOKEN`, restart or reload the backend so the process uses the new value. If task execution succeeds but the timeline records `GitHub issue comment creation failed: HTTP 403`, recheck the `Issues: Read and write` permission first.

## Run With Docker Compose

From the repository root:

```bash
docker compose --env-file .env up --build
```

Verify the backend:

```bash
curl http://127.0.0.1:8080/health
curl -H "X-PatchPilot-Admin-Token: $PATCHPILOT_ADMIN_TOKEN" http://127.0.0.1:8080/api/tasks
```

Expected health response includes:

```json
{"success":true,"data":{"status":"UP","service":"patchpilot-backend"}}
```

## Run From IntelliJ IDEA

Open the repository root in IDEA and run:

```text
io.patchpilot.backend.PatchPilotApplication
```

Default IDEA startup uses `application-default.properties`, disables datasource auto-configuration, and runs on port `8080`. Use Docker Compose when testing the full MySQL-backed workflow.

## Expose Local Webhooks

Start the backend first, then open a second terminal:

```bash
cloudflared tunnel --url http://127.0.0.1:8080
```

Copy the generated `https://*.trycloudflare.com` URL and configure the GitHub webhook:

- Payload URL: `https://your-temp-url.trycloudflare.com/api/github/webhook`
- Content type: `application/json`
- Secret: same value as `PATCHPILOT_GITHUB_WEBHOOK_SECRET`
- Events: `Issue comments`

The temporary URL changes whenever the tunnel restarts.

The GitHub webhook endpoint does not use the admin token, but operator API calls should include it when `PATCHPILOT_ADMIN_TOKEN` is configured:

```bash
curl -H "X-PatchPilot-Admin-Token: $PATCHPILOT_ADMIN_TOKEN" \
  https://your-temp-url.trycloudflare.com/api/tasks
```

## Trigger A Task

Open a GitHub issue in the test repository and comment:

```text
/agent fix touch docs/demo.md
```

For deterministic local smoke tests, use an existing file in the target repository:

```text
/agent fix replace docs/demo.md PatchPilot smoke test
```

Check task state:

```bash
ADMIN_HEADER=(-H "X-PatchPilot-Admin-Token: $PATCHPILOT_ADMIN_TOKEN")

curl "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/tasks
curl "${ADMIN_HEADER[@]}" "http://127.0.0.1:8080/api/tasks?status=FAILED&limit=20"
curl "${ADMIN_HEADER[@]}" "http://127.0.0.1:8080/api/tasks?repositoryOwner=bingqin2&repositoryName=PatchPilot"
curl "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/tasks/{taskId}
curl "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/tasks/{taskId}/detail
curl "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/tasks/{taskId}/report
curl "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/tasks/{taskId}/summary
curl "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/tasks/{taskId}/timeline
curl "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/tasks/{taskId}/test-runs
curl "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/tasks/{taskId}/tool-calls
curl "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/tasks/{taskId}/model-calls
curl "${ADMIN_HEADER[@]}" "http://127.0.0.1:8080/api/rejected-triggers?limit=20"
curl "${ADMIN_HEADER[@]}" "http://127.0.0.1:8080/api/rejected-triggers/summary?limit=100"
curl "${ADMIN_HEADER[@]}" "http://127.0.0.1:8080/api/trigger-quarantines?activeOnly=true&limit=20"
curl "${ADMIN_HEADER[@]}" "http://127.0.0.1:8080/api/trigger-quarantines/{quarantineId}/evidence?limit=20"
curl "${ADMIN_HEADER[@]}" "http://127.0.0.1:8080/api/operator-safety-audits?limit=20"
curl "${ADMIN_HEADER[@]}" "http://127.0.0.1:8080/api/admin-audit-events?limit=25&action=TASK_RETRIED&resourceType=TASK&operator=admin-api"
curl "${ADMIN_HEADER[@]}" "http://127.0.0.1:8080/api/github/webhook-deliveries?limit=20"
```

Create or release a manual quarantine when operator intervention is needed before or after automatic thresholds:

```bash
curl -X POST http://127.0.0.1:8080/api/trigger-quarantines \
  -H "X-PatchPilot-Admin-Token: $PATCHPILOT_ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "scope": "REPOSITORY",
    "scopeKey": "bingqin2/PatchPilot",
    "reason": "Operator blocked noisy demo repository",
    "durationMs": 1800000,
    "operator": "local-admin"
  }'

curl -X POST http://127.0.0.1:8080/api/trigger-quarantines/{quarantineId}/release \
  -H "X-PatchPilot-Admin-Token: $PATCHPILOT_ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "operator": "local-admin",
    "reason": "False positive during demo"
  }'
```

Use `/detail` for dashboard-style task inspection. It returns the task audit summary, latest queue item, queue history, accepted-trigger intent audit, latest generated diff, failed-task diagnosis, timeline events, test runs, tool calls, and model calls in one response. Use `/report` to copy a Markdown diagnostic summary for a task, including accepted-trigger intent, failure diagnosis, and generated diff when captured. The narrower endpoints remain available for focused debugging.
Use `/api/task-queue/worker-health` to inspect the current backend process's queue worker heartbeat. It reports whether the poller has started, is polling, is idle, is active, or most recently recorded a worker error, plus poll, claim, completion, failure, and idle counts. It also returns `lastPollAgeMs`, `readinessStatus`, and `operatorAction`, so the dashboard and readiness gate can warn when the worker has not started, has failed, or has stopped polling within the configured stale threshold. This is runtime health state, not durable task history.
Use `/api/rejected-triggers` to inspect rejected `/agent fix` attempts that did not create tasks, including unsafe command text, unauthorized users, unauthorized repositories, rate-limited requests, abuse-quarantined requests, model-classifier rejections, the rejection reason, retry eligibility, retry-blocked guidance, and any linked GitHub refusal comment. Use `/api/rejected-triggers/{id}/retry` only when the row reports `retryable: true`; unsafe, unauthorized, rate-limited, quarantined, unsupported, unknown, or already-retried rows return `409 Conflict` with the required next action. Use `/api/rejected-triggers/summary` to see recent rejection counts by category, source, trigger user, and repository. Use `/api/trigger-quarantines` to inspect active or historical trigger-user and repository quarantine records, including the reason, evidence count, window, expiry, creator, and release metadata. Use `/api/trigger-quarantines/{quarantineId}/evidence` to inspect the rejected-trigger rows and manual safety actions behind one quarantine. Use `/api/admin-audit-events` to inspect protected admin mutations, including manual task creation, task cancel, task retry, risk-review approval, rejected-trigger retry, demo session archive, and quarantine create/release actions with operator and reason. It supports `limit`, `action`, `resourceType`, `resourceId`, `scope`, `scopeKey`, and `operator` query parameters so operators can search for one protected change without reading logs. `/api/operator-safety-audits` remains available as a compatibility alias for the same audit stream. The dashboard's trigger-decision panel pairs the selected task's accepted trigger timeline evidence with recent rejected trigger decisions for quick operator comparison, while selected-task detail shows the accepted trigger as a structured trigger intent audit for per-task investigation and reports. The admin audit trail panel shows the protected mutation stream, supports action/operator/resource filters, and can copy a Markdown evidence report for the visible rows, while the rejected-trigger panel remains the full audit and quarantine operations surface with rejection records, retry eligibility guidance, summary, active quarantines, quarantine evidence drilldown, manual quarantine form, release action, and quarantine-related operator safety audit rows.
Use `/api/github/webhook-deliveries` to inspect recent GitHub delivery outcomes from PatchPilot itself, including temporary URL reachability, invalid signatures, malformed payloads, ignored comments, duplicate deliveries, active-task collisions, rejections, and created task ids. Each row includes an operator action, a `redeliveryRecommended` flag, and an `outcomeType`/`outcomeId`/`outcomeUrl` correlation target such as a task detail route or rejected-trigger audit anchor. For invalid signatures, bad requests, or backend processing failures, fix the underlying URL/secret/backend issue first, then use GitHub's `Redeliver` action on that delivery. Do not redeliver ignored, rejected, duplicate, active-task, or task-created deliveries unless you are intentionally validating that behavior. Raw payloads and signatures are not stored.
Use `POST /api/github/webhook-diagnostics/evaluate-payload` to paste a GitHub delivery payload and optionally its `sha256=...` signature for read-only diagnostics before redelivery. This endpoint reports JSON validity, signature status, supported event/action, `/agent fix` recognition, parsed repository and issue fields, and the next operator action. It is admin-token protected and does not create tasks, queue work, rejected-trigger audits, delivery diagnostics, GitHub comments, rate-limit records, or model calls.

Task retry is an audited operator action. Check `GET /api/tasks/{taskId}/retry-preflight` first, then retry only when it reports `retryable: true`. `POST /api/tasks/{taskId}/retry` requires a JSON reason; blank reasons return `400 Bad Request`, and blocked retry categories return `409 Conflict`.

```bash
curl -X POST http://127.0.0.1:8080/api/tasks/{taskId}/retry \
  -H "X-PatchPilot-Admin-Token: $PATCHPILOT_ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "reason": "Verified failing test output and requested a clean rerun"
  }'
```

The reason is stored with retry lineage, appears in the requeue timeline event, and is included in task detail and copied Markdown reports.

If a generated diff is blocked by the risk gate, the task moves to `PENDING_REVIEW` instead of `FAILED`. Inspect the generated diff in the task detail or copied report first, then check the `GeneratedDiffRiskGate` tool-call output for the concrete rejection reason. If the diff is intentionally allowed, approve it through the dashboard or API with an approver from `PATCHPILOT_REVIEW_APPROVAL_ALLOWED_OPERATORS` and a reason:

```bash
curl -X POST http://127.0.0.1:8080/api/tasks/{taskId}/approve-review \
  -H "X-PatchPilot-Admin-Token: $PATCHPILOT_ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "operator": "release-captain",
    "reason": "Reviewed generated diff and accepted docs-only change"
  }'
```

Approval stores `riskReviewApprovedAt`, `riskReviewApprovedBy`, and `riskReviewApprovalReason` on the task. It does not ask the model to regenerate a patch. It requeues the same task, resumes the existing task workspace, skips only the already-reviewed generated-diff risk gate, and continues with adapter verification, commit, push, and Pull Request creation. If the workspace has been deleted, cancel or retry the task to start a fresh run instead.

For local demos or debugging, you can create the same queued task from the backend API without posting a GitHub comment:

First evaluate the trigger when you want to confirm what would happen without creating a task, consuming trigger rate-limit quota, writing rejected-trigger audits, dispatching queue work, or posting GitHub comments:

```bash
curl -X POST http://127.0.0.1:8080/api/tasks/evaluate-trigger \
  -H "X-PatchPilot-Admin-Token: $PATCHPILOT_ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "source": "ISSUE_COMMENT",
    "repositoryOwner": "bingqin2",
    "repositoryName": "PatchPilot",
    "issueNumber": 1,
    "triggerUser": "local-operator",
    "triggerComment": "/agent fix touch docs/manual-task.md"
  }'
```

The optional `source` is `MANUAL` by default. Use `ISSUE_COMMENT` when you want to preview the same gate source used by GitHub issue-comment triggers without replaying a full webhook payload or signature. The response reports `WOULD_CREATE_TASK` or `BLOCKED`, the evaluated source, the gate decisions that were evaluated, whether issue context was loaded for model classification, and the next operator action. The dashboard manual task form can copy the same dry-run evidence as Markdown, including the evaluated repository, issue, trigger user, command, blocked reason/category, and per-gate allow/block state.

```bash
curl -X POST http://127.0.0.1:8080/api/tasks \
  -H "X-PatchPilot-Admin-Token: $PATCHPILOT_ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "repositoryOwner": "bingqin2",
    "repositoryName": "PatchPilot",
    "issueNumber": 1,
    "triggerUser": "local-operator",
    "triggerComment": "/agent fix touch docs/manual-task.md"
  }'
```

Manual task creation still records a normal task, writes a timeline event, and dispatches work through the queue. It rejects duplicate active work for the same issue.
The manual API and the dry-run evaluation API use the same command safety gate as GitHub webhooks. Unsafe requests such as secret exfiltration, destructive repository changes, or arbitrary shell execution are rejected before task creation.
If safety allowlists are configured, the manual task `triggerUser` and `repositoryOwner/repositoryName` must also match those allowlists.

## Supported Repositories

PatchPilot currently executes fixes for Java repositories with Maven or Gradle build files, Go modules, Node.js repositories with Bun, npm, pnpm, or yarn test scripts, and Python repositories with tox, nox, hatch, Poetry, uv, or pytest test signals. After cloning the task workspace, the backend runs the language adapter preflight:

- supported: `pom.xml` with `mvnw`, using `./mvnw test`
- supported: `pom.xml` without `mvnw`, using `mvn test`
- supported: `build.gradle` or `build.gradle.kts` with `gradlew`, using `./gradlew test`
- supported: `build.gradle` or `build.gradle.kts` without `gradlew`, using `gradle test`
- supported: `go.mod`, using `go test ./...`
- supported: `package.json` with `bun.lockb` or `bun.lock` and non-empty `scripts.test`, using `bun test`
- supported: `package.json` with `pnpm-lock.yaml` and non-empty `scripts.test`, using `pnpm test`
- supported: `package.json` with `yarn.lock` and non-empty `scripts.test`, using `yarn test`
- supported: `package.json` with non-empty `scripts.test`, using `npm test`
- supported: `tox.ini` or `pyproject.toml` with `[tool.tox]`, using `tox`
- supported: `noxfile.py`, using `nox`
- supported: `pyproject.toml` with a Hatch test script, using `hatch test`
- supported: `pyproject.toml` with `[tool.poetry]` plus pytest configuration or dependency, using `poetry run pytest`
- supported: `uv.lock` plus `pyproject.toml` pytest configuration or dependency, using `uv run pytest`
- supported: `pytest.ini`, `pyproject.toml` with `[tool.pytest.ini_options]`, or `requirements.txt` with pytest, using `python3 -m pytest`
- unsupported: no registered adapter detects the repository

Unsupported repositories fail before model patch generation, tests, commit, push, or Pull Request creation.
For supported repositories, the language adapter supplies the verification command and the generic verification runner executes that command under the existing allowlist, timeout, process-registration, and environment-sanitization rules.
After a repository is detected, each task stores the selected `language`, `buildSystem`, `verificationCommand`, and nullable `adapterDetectionReason`. These fields are returned by the task APIs and shown in the dashboard detail view so operators can confirm whether a task used Maven, Gradle, Go, Bun, npm, pnpm, yarn, tox, nox, hatch, Poetry, uv, or pytest, and which repository signal caused that selection, without opening raw tool-call logs.

After patch generation, PatchPilot runs a deterministic generated-diff risk gate before adapter verification or any GitHub write. The gate rejects sensitive files such as `.env`, private keys, and GitHub Actions workflows; secret-like added lines; binary patches; and diffs that exceed the configured file or line thresholds. Rejections move the task to `PENDING_REVIEW`, keep the concrete reason in `failureReason`, and store a failed `GeneratedDiffRiskGate` tool call so task reports and the dashboard can explain why execution stopped. Approval requires an operator from `PATCHPILOT_REVIEW_APPROVAL_ALLOWED_OPERATORS` and a reason, and the task keeps that approval audit metadata when it resumes.

Adapter detection fixtures live in `docs/demo-repositories/`. Each fixture documents the adapter it should trigger and the fixed verification command PatchPilot will run. Backend tests use these fixtures to prevent supported repository shapes from drifting as adapters evolve.

The supported adapter catalog is also available through the backend API:

```bash
curl http://127.0.0.1:8080/api/language-adapters
```

This endpoint returns the same language/build-system matrix, verification commands, detection signals, and fixture paths shown in the dashboard.

The fixture verification matrix is available through:

```bash
curl http://127.0.0.1:8080/api/language-adapters/fixtures
```

This endpoint runs each checked-in demo fixture through the real adapter registry and returns expected versus actual language, build system, verification command, detection reason, and `PASS` or `FAIL`. Missing or drifting fixtures are returned as failed rows instead of hiding the rest of the matrix. The backend Docker image copies `docs/demo-repositories/` into `/app/docs/demo-repositories`, so the same endpoint works in Docker Compose.

The dashboard combines these two adapter APIs into an `Adapter readiness report` panel. Use it before a live demo to confirm language coverage, allowlisted verification commands, fixture pass rate, and any fixture drift in one place. `Copy adapter readiness report` exports the same evidence as Markdown for a handoff note or Pull Request review.

Operators can run a local repository preflight before creating a real task:

```bash
curl -X POST http://127.0.0.1:8080/api/repository-preflight \
  -H "Content-Type: application/json" \
  -d '{"repositoryPath":"docs/demo-repositories/java-maven"}'
```

The preflight checks only whether the configured language adapter registry can detect the local path, and only for paths under `PATCHPILOT_REPOSITORY_PREFLIGHT_ALLOWED_ROOT_DIRS`. It does not create a task, call the model, run tests, mutate Git, or open a Pull Request. The dashboard exposes the same check in the `Repository preflight` panel, shows the configured allowed roots next to the form, and includes repository-preflight scope in demo readiness and the operator setup checklist.
After a preflight run, `Copy preflight report` exports the visible result as Markdown, including supported status, repository path, selected adapter, allowlisted verification command, detection reason, operator action, allowed roots, and supported adapter options for unsupported repositories. Use it to attach repository-support evidence to a handoff note or Pull Request discussion before triggering a real task.

Run the safe local adapter smoke when you want to demonstrate supported repository detection without GitHub, model credentials, Docker, or PR creation:

```bash
scripts/adapter-smoke.sh
scripts/adapter-smoke.sh --backend
```

Runtime configuration summary:

```bash
curl "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/configuration/summary
```

This endpoint returns only non-sensitive values and configured/missing booleans for secrets. It also returns safety policy state such as trigger-user allowlists, repository allowlists, review-approval operators, model trigger classification, trigger rate-limit thresholds, queue worker heartbeat stale threshold, rejected-trigger quarantine thresholds, generated-diff policy state, and repository-preflight allowed roots. It never returns raw API keys, GitHub tokens, or webhook secrets.

Demo readiness:

```bash
curl "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/demo/readiness
curl "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/demo/smoke-checklist
curl "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/demo/evidence-bundle
curl "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/demo/session-snapshot
curl "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/demo/session-report
curl -OJ "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/demo/session-report/download
curl "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/demo/session-archives
curl -X POST "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/demo/session-archives
curl -OJ "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/demo/session-archives/{archiveId}/report/download
curl "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/demo/script
curl "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/demo/runbook
```

The readiness endpoint aggregates backend reachability, required credential flags, safety policy state, model cost configuration, adapter fixture verification, queue state, and recent completed Pull Request evidence into one `READY`, `NEEDS_ATTENTION`, or `BLOCKED` result. Use it before a live `/agent fix` demo to see the next operator action without manually checking every panel or curl endpoint.
The smoke checklist endpoint is the final pre-demo readout. It orders the checks as readiness gate, webhook delivery, task execution, and Pull Request evidence, and includes the current evidence plus the next operator action for each step.
The evidence bundle endpoint is the one-call demo summary. It includes readiness, smoke checklist, non-sensitive configuration, adapter fixture counts, queue summary, recent task and Pull Request evidence, latest webhook delivery, rejected-trigger counts, active quarantine count, generated time, and next actions.
The demo session snapshot endpoint combines the current evidence bundle, script, runbook, operator checklist, health contract, share summary, and next actions into one read-only demo handoff object. Use it before or after a live run when you need one durable summary for the dashboard, terminal, or review notes.
The demo session report endpoint formats that session snapshot as Markdown for copy/paste handoff. It includes session status, share summary, recent task and Pull Request evidence, operator checklist, script steps, health contract, next actions, and the embedded runbook. It is read-only and does not create tasks, call the model, run tests, mutate Git, or write to GitHub.
The demo session archive endpoints store and list the most recent session reports for local demos. `POST /api/demo/session-archives` captures the current snapshot and Markdown report, while `GET /api/demo/session-archives` lists the latest 20 archives. The default database-free profile keeps archives in memory; `local`, `docker`, and `idea` profiles persist them in MySQL through Flyway/MyBatis. Archive creation is PatchPilot-local state only: it does not create tasks, call the model, run tests, mutate Git, or write to GitHub.
The demo script endpoint turns the current evidence bundle into an ordered live-demo script. Each step includes an operator action, a verification command, success criteria, a dashboard troubleshooting panel, and the current evidence. Its health contract states that `GET /api/demo/script` is read-only and does not create tasks, call the model, run tests, mutate Git, or write to GitHub.
The runbook endpoint formats the same evidence bundle as Markdown for demos, handoff notes, or issue comments. It is read-only and does not create tasks, call the model, run tests, or write to GitHub.

Queue state:

```bash
curl "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/task-queue/summary
curl "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/task-queue/items
```

Task metrics:

```bash
curl "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/tasks/metrics/summary
curl "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/tasks/metrics/failure-causes
curl "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/tasks/metrics/model-usage
curl "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/tasks/metrics/latency
```

Failure-cause metrics, failed-task issue feedback, task detail responses, copied task reports, and dashboard failure diagnosis use the same stable categories and next-action guidance, so repeated failures are grouped as `VERIFICATION_FAILED`, `GITHUB_OPERATION_FAILED`, `MODEL_FAILED`, `WORKSPACE_FAILED`, `UNSUPPORTED_REPOSITORY`, `PATCH_REVIEW_REJECTED`, or `TASK_FAILED` instead of ad hoc exception text.

Task list, status count, and metrics APIs accept the same investigation filters:

```bash
curl "${ADMIN_HEADER[@]}" "http://127.0.0.1:8080/api/tasks?language=node&buildSystem=npm"
curl "${ADMIN_HEADER[@]}" "http://127.0.0.1:8080/api/tasks/status-counts?language=node&buildSystem=npm"
curl "${ADMIN_HEADER[@]}" "http://127.0.0.1:8080/api/tasks/metrics/summary?language=node&buildSystem=npm"
```

Model cost estimates default to `0`. Configure per-token prices when needed:

```bash
PATCHPILOT_AGENT_COST_PROMPT_TOKEN_USD=0.000001
PATCHPILOT_AGENT_COST_COMPLETION_TOKEN_USD=0.000002
```

## Frontend Dashboard

The React dashboard lives in `frontend/` and calls the backend through Vite's `/api` proxy.
It includes a connectivity panel that distinguishes backend `/health`, saved browser admin-token state, and protected API reachability; an operator setup checklist that condenses connectivity, credentials, safety policy, adapter fixtures, queue health, worker heartbeat readiness, and recent PR evidence into demo-ready checks; a demo evidence bundle backed by `GET /api/demo/evidence-bundle` with copyable runbook Markdown backed by `GET /api/demo/runbook`; a demo session snapshot backed by `GET /api/demo/session-snapshot` with copyable session report Markdown backed by `GET /api/demo/session-report`, an archive action backed by `POST /api/demo/session-archives`, and recent archived reports backed by `GET /api/demo/session-archives`; a demo script panel backed by `GET /api/demo/script`; a demo readiness panel backed by `GET /api/demo/readiness`; a live demo smoke checklist backed by `GET /api/demo/smoke-checklist`; task metrics; refresh progress and last-refresh feedback; failure-cause grouping with stable categories and next-action guidance; model token and estimated-cost summaries; latency summaries; a non-sensitive runtime configuration panel backed by `/api/configuration/summary`; backend `/health` status; configuration health hints for missing secrets and weak queue/worker-heartbeat/cost/trigger-rate-limit/quarantine settings; an adapter readiness report plus supported-adapters panel backed by `GET /api/language-adapters`; a fixture verification panel backed by `GET /api/language-adapters/fixtures`; a repository preflight panel with copyable Markdown reports backed by `POST /api/repository-preflight`; a manual task creation form backed by `POST /api/tasks` with read-only trigger evaluation and copyable dry-run evidence reports backed by `POST /api/tasks/evaluate-trigger`; status filters with scoped count badges backed by `GET /api/tasks/status-counts`; repository owner/name filters; language/build-system adapter filters; created time range filters; sort control; and full-history search backed by `GET /api/tasks`, one-click filter reset, total-count and `hasMore`-backed `Load more` task pagination, task creation/update times, GitHub Issue, status comment, and Pull Request links, `/tasks/{taskId}` deep links with legacy `?taskId=` compatibility, copyable links and copyable Markdown reports for selected task details, task detail summaries loaded through `GET /api/tasks/{taskId}/detail`, selected-task adapter metadata with detection reason, selected-task failure diagnosis with category, next action, and sanitized reason, selected-task queue status and queue history with retry/last-error context, execution evidence summaries including generated-diff risk-gate blocks, timeline events, accepted trigger evidence, test runs, tool calls and model calls with durations, empty states for missing detail records, task control actions for cancel/retry with required retry reasons, a read-only queue panel with queue summary, queue items, worker readiness, last poll age, operator action, and worker heartbeat backed by `/api/task-queue/*`, a trigger-decision panel that compares the selected accepted trigger with recent rejected decisions, recent rejected-trigger summaries backed by `GET /api/rejected-triggers/summary`, recent rejected-trigger audit rows backed by `GET /api/rejected-triggers`, manual trigger quarantine create/release controls backed by `POST /api/trigger-quarantines`, a protected admin audit trail backed by `GET /api/admin-audit-events`, quarantine-related operator safety rows filtered from the same stream, recent webhook delivery diagnostics with redelivery guidance and task/rejected-trigger outcome targets backed by `GET /api/github/webhook-deliveries`, and pasted webhook payload diagnostics backed by `POST /api/github/webhook-diagnostics/evaluate-payload`.
The page coordinator is `frontend/src/App.tsx`; reusable dashboard UI lives under `frontend/src/dashboard/components/`, with shared formatting helpers in `frontend/src/dashboard/format.ts`.
The dashboard task list requests `query`, `status`, `repositoryOwner`, `repositoryName`, `language`, `buildSystem`, `createdAfter`, `createdBefore`, `sort`, `limit`, and `offset` from the backend and consumes the task page response with `items`, `limit`, `offset`, `hasMore`, and `total`.
Status count badges and metrics use the same search, repository, adapter, and created-time scope as the task list but intentionally ignore the active `status`, `sort`, `limit`, and `offset` values so every status button shows the distribution for the current investigation scope.
The dashboard also stores `status`, `query`, repository filters, adapter filters, created time filters, and non-default `sort` in the browser URL, so links like `/tasks/{taskId}?status=FAILED&query=maven&repositoryOwner=bingqin2&repositoryName=PatchPilot&language=node&buildSystem=npm&sort=createdAtAsc&createdAfter=2026-06-20T01:00:00Z&createdBefore=2026-06-21T01:00:00Z` reopen the same filtered investigation view. The `Clear filters` action removes `status`, `query`, `repositoryOwner`, `repositoryName`, `language`, `buildSystem`, `createdAfter`, and `createdBefore`, keeping sort, the selected task route, unrelated query parameters, and hash fragments intact.
The selected-task panel uses the aggregate detail endpoint so opening a task needs one backend detail request instead of separate summary, timeline, test-run, tool-call, and model-call requests.
If the backend is down or the Vite proxy target is wrong, the dashboard shows a backend/proxy guidance message instead of a raw JSON parsing error.
The connectivity panel stays visible near the top of the page and identifies whether the backend is reachable, whether this browser has a token saved, and whether protected APIs are currently blocked.
The operator setup checklist stays near the top of the page and reuses already loaded dashboard data to show `Backend connectivity`, `Required credentials`, `Safety policy`, `Adapter fixtures`, `Queue health`, `Worker heartbeat`, and `Recent PR evidence` before a live `/agent fix` demo. It is read-only and does not create tasks or mutate configuration.
The demo evidence bundle is the top-level demo readout. It combines readiness, smoke-check, configuration, adapter fixture, queue, webhook, rejected-trigger, quarantine, and recent Pull Request signals into one status, count set, and next-action list. Its `Copy runbook` action copies a Markdown report suitable for demo notes or operator handoff.
The demo session snapshot combines the current evidence bundle, script, runbook, operator checklist, health contract, share summary, and next actions in one panel so an operator can confirm the demo state before or after the live `/agent fix` trigger. Its `Copy session report` action fetches a Markdown report on demand for review notes, issue comments, or handoff messages. Its `Archive session` action captures the current report into the archive store and lists recent archived reports with their own copy actions.
The demo script panel converts the current evidence bundle into a six-step live-demo path covering backend/dashboard access, configuration and safety posture, repository support, controlled `/agent fix` triggering, task execution tracking, and Pull Request evidence review. Every step includes a command to verify, a success criterion, a troubleshooting panel, and current evidence, and the panel repeats the read-only health contract for the script endpoint.
The smoke checklist is the final live-demo panel: it shows ordered readiness, webhook, execution, and Pull Request evidence so the operator can decide whether to post the real `/agent fix` comment or fix setup first.
The trigger-decision panel answers the immediate safety question for a selected task: why this `/agent` comment was accepted, and what recent comments were refused instead. It reuses the selected task timeline, rejected-trigger rows, rejected-trigger summary, and refusal comment links when available, while the rejected-trigger panel remains the full audit, quarantine, and operator-action workspace.
The admin audit trail panel shows protected mutations across the dashboard and API, including manual task creation, cancel, retry, risk-review approval, rejected-trigger retry, demo session archive, and quarantine actions. Operators can filter by action, operator, resource type, resource id, or scope key and copy a Markdown report for the visible evidence. The rejected-trigger panel shows recent `/agent fix` comments that were intentionally refused before task creation, including the command text, rejection reason, retry eligibility guidance, recent category/source counts, top trigger users/repositories, active quarantines, manual quarantine controls, evidence drilldown for one selected quarantine, and quarantine-related operator safety audit rows. It includes `ABUSE_QUARANTINED` rows when repeated rejected attempts cause automatic cooldown. Retry stays enabled only for rows where a direct manual-task retry is safe; dangerous, unauthorized, rate-limited, quarantined, unsupported, unknown, and already-retried rows show `Retry blocked` with the specific next action. Operators can manually quarantine a trigger user or repository with scope, target, reason, duration, and operator name, inspect the rejected-trigger and operator-action evidence behind an active quarantine, and release an active quarantine from the same panel. Each successful manual create or release is also recorded as an operator safety audit row. Use it when GitHub shows a successful delivery but no task appears, when repeated unsafe traffic needs investigation, or when a false-positive quarantine must be cleared before a demo.
If `PATCHPILOT_ADMIN_TOKEN` is configured, the dashboard header shows whether this browser has an admin token saved. Enter the same token value in `Dashboard admin token` and click `Save dashboard admin token`; the browser stores it under `patchpilot.adminToken`, retries dashboard loading, and later API calls include `X-PatchPilot-Admin-Token`. Use `Clear admin token` when you rotate or remove the local credential.

If the first protected API call returns `Admin token is required`, the dashboard also shows the same recovery path inside the alert so you can restore access without opening browser DevTools.

You can also prefill the token from the browser console before opening the dashboard:

```js
localStorage.setItem('patchpilot.adminToken', 'replace-with-random-admin-token')
```

```bash
cd frontend
npm install
npm run dev
```

Open the printed Vite URL, usually `http://127.0.0.1:5173`. Keep the backend running on `http://127.0.0.1:8080` so dashboard requests can reach the task APIs.

If the backend is started from the IDEA run configuration, it runs on `18080`. Set the frontend proxy target in the repository root `.env` or before starting Vite:

```bash
PATCHPILOT_FRONTEND_BACKEND_URL=http://127.0.0.1:18080 npm run dev
```

Validate the dashboard:

```bash
cd frontend
npm test
npm run build
```

## Backend Tests

Run all backend tests:

```bash
mvn -pl PatchPilot test
```

Or from the module:

```bash
cd PatchPilot
./mvnw test
```

## Safety Boundaries

PatchPilot must not:

- Push directly to the default branch.
- Auto-merge Pull Requests.
- Execute arbitrary model-generated shell commands.
- Accept task triggers from users or repositories outside configured allowlists.
- Drop rejected `/agent fix` attempts without an operator-visible audit record.
- Read or write outside the task workspace.
- Log secrets.
- Report success without verification.
- Create tasks for unsafe `/agent fix` instructions.
- Continue to verification, commit, push, or PR creation after a generated diff fails risk checks.
- Retry a `PENDING_REVIEW` task without cancelling or recording an explicit approval with operator and reason.

## Current Limitations

- Java/Maven, Java/Gradle, Go, Node/Bun, Node/npm, Node/pnpm, Node/yarn, Python/tox, Python/nox, Python/hatch, Python/Poetry, Python/uv, and Python/pytest repositories are the first supported targets through explicit language adapters.
- Custom runner adapters are future work.
- The current runtime is single-process; API and worker separation is future work.
- The React dashboard can create manual demo tasks, but GitHub issue comments remain the primary production trigger. It does not merge Pull Requests.
- Temporary Cloudflare URLs are for local testing only.

## Development Workflow

Long-running work should create or update a plan in `docs/plans/` and record evidence in `docs/progress/execution-log.md`. Durable design decisions belong in `docs/progress/decisions.md`.
