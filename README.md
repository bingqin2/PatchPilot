# PatchPilot

PatchPilot is an AI GitHub issue-to-PR agent backend. It receives a GitHub issue comment, creates a durable fix task, inspects the repository through controlled tools, applies a focused patch, runs adapter-selected verification, and opens a Pull Request for human review.

PatchPilot is not a chatbot and does not auto-merge code. The current target is local self-hosted development and private demos.

## What Works Now

- GitHub webhook endpoint for `issue_comment.created`.
- `/agent fix` trigger detection.
- Safety gate rejection for vague, unsafe, or non-actionable `/agent fix` instructions before task creation.
- Optional trigger-user and repository allowlists for webhook and manual task creation.
- Trigger rate limiting by trigger user, repository, and issue before model calls or task creation.
- Webhook signature verification.
- Webhook delivery diagnostics for recent task-created, ignored, rejected, duplicate, bad-request, and invalid-signature outcomes.
- Dashboard visibility for rejected `/agent fix` triggers that were refused before task creation.
- MySQL-backed task, queue, timeline, test-run, tool-call, and model-call records.
- Local workspace clone, branch, diff, commit, push, and Pull Request creation.
- Java/Maven, Java/Gradle, Go, Node/Bun, Node/npm, Node/pnpm, Node/yarn, Python/tox, Python/nox, Python/hatch, Python/Poetry, Python/uv, and Python/pytest language adapters backed by an adapter-driven verification runner with command allowlists.
- Generated diff risk gate that blocks sensitive files, secret-like added lines, binary patches, and overly broad patches before tests, commits, pushes, or Pull Request creation.
- Human approval flow for generated-diff risk rejections: `PENDING_REVIEW` tasks expose the generated diff for inspection, then can be cancelled or explicitly approved to resume from the already-generated workspace and continue verification, commit, push, and Pull Request creation.
- Unsupported repository preflight that fails before patch generation, test execution, Git mutation, or Pull Request creation.
- OpenAI-compatible model client and plan-driven patch workflow.
- Issue comment status updates for accepted, running, verification, success, and failure states.
- Demo readiness gate that summarizes credentials, adapter fixtures, queue health, and recent PR evidence before a live smoke run.
- Live demo smoke checklist that turns readiness, webhook delivery, task execution, and Pull Request evidence into ordered operator steps.
- Demo evidence bundle that combines readiness, smoke-check, configuration, adapter, queue, webhook, rejected-trigger, quarantine, and recent Pull Request evidence for one demo-ready summary.
- Demo script endpoint and dashboard panel that turn the current evidence bundle into ordered live-demo actions, verification commands, troubleshooting panels, and a read-only health contract.
- Copyable demo runbook Markdown that exports the evidence bundle into a concise operator handoff report.

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

Vague comments such as `/agent fix`, `/agent fix help`, or `/agent fix make it better` are rejected before task creation and appear in `/api/rejected-triggers`.

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

Use `/detail` for dashboard-style task inspection. It returns the task audit summary, latest queue item, queue history, latest generated diff, timeline events, test runs, tool calls, and model calls in one response. Use `/report` to copy a Markdown diagnostic summary for a task, including the generated diff when one was captured. The narrower endpoints remain available for focused debugging.
Use `/api/rejected-triggers` to inspect rejected `/agent fix` attempts that did not create tasks, including unsafe command text, unauthorized users, unauthorized repositories, rate-limited requests, abuse-quarantined requests, model-classifier rejections, and the rejection reason. Use `/api/rejected-triggers/summary` to see recent rejection counts by category, source, trigger user, and repository. Use `/api/trigger-quarantines` to inspect active or historical trigger-user and repository quarantine records, including the reason, evidence count, window, expiry, creator, and release metadata. Use `/api/trigger-quarantines/{quarantineId}/evidence` to inspect the rejected-trigger rows and manual safety actions behind one quarantine. Use `/api/operator-safety-audits` to inspect recent manual safety mutations, including quarantine creation and release actions with operator and reason. The same rejection records, summary, active quarantines, quarantine evidence drilldown, manual quarantine form, release action, and operator safety audit rows appear in the dashboard's rejected-trigger panel.
Use `/api/github/webhook-deliveries` to inspect recent GitHub delivery outcomes from PatchPilot itself, including temporary URL reachability, invalid signatures, malformed payloads, ignored comments, duplicate deliveries, active-task collisions, rejections, and created task ids. Each row includes an operator action and a `redeliveryRecommended` flag. For invalid signatures, bad requests, or backend processing failures, fix the underlying URL/secret/backend issue first, then use GitHub's `Redeliver` action on that delivery. Do not redeliver ignored, rejected, duplicate, active-task, or task-created deliveries unless you are intentionally validating that behavior. Raw payloads and signatures are not stored.

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
The manual API uses the same command safety gate as GitHub webhooks. Unsafe requests such as secret exfiltration, destructive repository changes, or arbitrary shell execution are rejected before task creation.
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

Operators can run a local repository preflight before creating a real task:

```bash
curl -X POST http://127.0.0.1:8080/api/repository-preflight \
  -H "Content-Type: application/json" \
  -d '{"repositoryPath":"docs/demo-repositories/java-maven"}'
```

The preflight checks only whether the configured language adapter registry can detect the local path, and only for paths under `PATCHPILOT_REPOSITORY_PREFLIGHT_ALLOWED_ROOT_DIRS`. It does not create a task, call the model, run tests, mutate Git, or open a Pull Request. The dashboard exposes the same check in the `Repository preflight` panel, shows the configured allowed roots next to the form, and includes repository-preflight scope in demo readiness and the operator setup checklist.

Run the safe local adapter smoke when you want to demonstrate supported repository detection without GitHub, model credentials, Docker, or PR creation:

```bash
scripts/adapter-smoke.sh
scripts/adapter-smoke.sh --backend
```

Runtime configuration summary:

```bash
curl "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/configuration/summary
```

This endpoint returns only non-sensitive values and configured/missing booleans for secrets. It also returns safety policy state such as trigger-user allowlists, repository allowlists, review-approval operators, model trigger classification, trigger rate-limit thresholds, rejected-trigger quarantine thresholds, generated-diff policy state, and repository-preflight allowed roots. It never returns raw API keys, GitHub tokens, or webhook secrets.

Demo readiness:

```bash
curl "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/demo/readiness
curl "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/demo/smoke-checklist
curl "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/demo/evidence-bundle
curl "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/demo/script
curl "${ADMIN_HEADER[@]}" http://127.0.0.1:8080/api/demo/runbook
```

The readiness endpoint aggregates backend reachability, required credential flags, safety policy state, model cost configuration, adapter fixture verification, queue state, and recent completed Pull Request evidence into one `READY`, `NEEDS_ATTENTION`, or `BLOCKED` result. Use it before a live `/agent fix` demo to see the next operator action without manually checking every panel or curl endpoint.
The smoke checklist endpoint is the final pre-demo readout. It orders the checks as readiness gate, webhook delivery, task execution, and Pull Request evidence, and includes the current evidence plus the next operator action for each step.
The evidence bundle endpoint is the one-call demo summary. It includes readiness, smoke checklist, non-sensitive configuration, adapter fixture counts, queue summary, recent task and Pull Request evidence, latest webhook delivery, rejected-trigger counts, active quarantine count, generated time, and next actions.
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
It includes a connectivity panel that distinguishes backend `/health`, saved browser admin-token state, and protected API reachability; an operator setup checklist that condenses connectivity, credentials, safety policy, adapter fixtures, queue health, and recent PR evidence into demo-ready checks; a demo evidence bundle backed by `GET /api/demo/evidence-bundle` with copyable runbook Markdown backed by `GET /api/demo/runbook`; a demo script panel backed by `GET /api/demo/script`; a demo readiness panel backed by `GET /api/demo/readiness`; a live demo smoke checklist backed by `GET /api/demo/smoke-checklist`; task metrics; refresh progress and last-refresh feedback; failure-cause grouping; model token and estimated-cost summaries; latency summaries; a non-sensitive runtime configuration panel backed by `/api/configuration/summary`; backend `/health` status; configuration health hints for missing secrets and weak queue/cost/trigger-rate-limit/quarantine settings; a supported-adapters panel backed by `GET /api/language-adapters`; a fixture verification panel backed by `GET /api/language-adapters/fixtures`; a manual task creation form backed by `POST /api/tasks`; status filters with scoped count badges backed by `GET /api/tasks/status-counts`; repository owner/name filters; language/build-system adapter filters; created time range filters; sort control; and full-history search backed by `GET /api/tasks`, one-click filter reset, total-count and `hasMore`-backed `Load more` task pagination, task creation/update times, GitHub Issue, status comment, and Pull Request links, `/tasks/{taskId}` deep links with legacy `?taskId=` compatibility, copyable links and copyable Markdown reports for selected task details, task detail summaries loaded through `GET /api/tasks/{taskId}/detail`, selected-task adapter metadata with detection reason, selected-task queue status and queue history with retry/last-error context, execution evidence summaries including generated-diff risk-gate blocks, timeline events, test runs, tool calls and model calls with durations, empty states for missing detail records, task control actions for cancel/retry, a read-only queue panel with health hints backed by `/api/task-queue/*`, recent rejected-trigger summaries backed by `GET /api/rejected-triggers/summary`, recent rejected-trigger audit rows backed by `GET /api/rejected-triggers`, manual trigger quarantine create/release controls backed by `POST /api/trigger-quarantines`, recent operator safety audit rows backed by `GET /api/operator-safety-audits`, and recent webhook delivery diagnostics with redelivery guidance backed by `GET /api/github/webhook-deliveries`.
The page coordinator is `frontend/src/App.tsx`; reusable dashboard UI lives under `frontend/src/dashboard/components/`, with shared formatting helpers in `frontend/src/dashboard/format.ts`.
The dashboard task list requests `query`, `status`, `repositoryOwner`, `repositoryName`, `language`, `buildSystem`, `createdAfter`, `createdBefore`, `sort`, `limit`, and `offset` from the backend and consumes the task page response with `items`, `limit`, `offset`, `hasMore`, and `total`.
Status count badges and metrics use the same search, repository, adapter, and created-time scope as the task list but intentionally ignore the active `status`, `sort`, `limit`, and `offset` values so every status button shows the distribution for the current investigation scope.
The dashboard also stores `status`, `query`, repository filters, adapter filters, created time filters, and non-default `sort` in the browser URL, so links like `/tasks/{taskId}?status=FAILED&query=maven&repositoryOwner=bingqin2&repositoryName=PatchPilot&language=node&buildSystem=npm&sort=createdAtAsc&createdAfter=2026-06-20T01:00:00Z&createdBefore=2026-06-21T01:00:00Z` reopen the same filtered investigation view. The `Clear filters` action removes `status`, `query`, `repositoryOwner`, `repositoryName`, `language`, `buildSystem`, `createdAfter`, and `createdBefore`, keeping sort, the selected task route, unrelated query parameters, and hash fragments intact.
The selected-task panel uses the aggregate detail endpoint so opening a task needs one backend detail request instead of separate summary, timeline, test-run, tool-call, and model-call requests.
If the backend is down or the Vite proxy target is wrong, the dashboard shows a backend/proxy guidance message instead of a raw JSON parsing error.
The connectivity panel stays visible near the top of the page and identifies whether the backend is reachable, whether this browser has a token saved, and whether protected APIs are currently blocked.
The operator setup checklist stays near the top of the page and reuses already loaded dashboard data to show `Backend connectivity`, `Required credentials`, `Safety policy`, `Adapter fixtures`, `Queue health`, and `Recent PR evidence` before a live `/agent fix` demo. It is read-only and does not create tasks or mutate configuration.
The demo evidence bundle is the top-level demo readout. It combines readiness, smoke-check, configuration, adapter fixture, queue, webhook, rejected-trigger, quarantine, and recent Pull Request signals into one status, count set, and next-action list. Its `Copy runbook` action copies a Markdown report suitable for demo notes or operator handoff.
The demo script panel converts the current evidence bundle into a six-step live-demo path covering backend/dashboard access, configuration and safety posture, repository support, controlled `/agent fix` triggering, task execution tracking, and Pull Request evidence review. Every step includes a command to verify, a success criterion, a troubleshooting panel, and current evidence, and the panel repeats the read-only health contract for the script endpoint.
The smoke checklist is the final live-demo panel: it shows ordered readiness, webhook, execution, and Pull Request evidence so the operator can decide whether to post the real `/agent fix` comment or fix setup first.
The rejected-trigger panel shows recent `/agent fix` comments that were intentionally refused before task creation, including the command text, rejection reason, recent category/source counts, top trigger users/repositories, active quarantines, manual quarantine controls, evidence drilldown for one selected quarantine, and recent operator safety audit rows. It includes `ABUSE_QUARANTINED` rows when repeated rejected attempts cause automatic cooldown. Operators can manually quarantine a trigger user or repository with scope, target, reason, duration, and operator name, inspect the rejected-trigger and operator-action evidence behind an active quarantine, and release an active quarantine from the same panel. Each successful manual create or release is also recorded as an operator safety audit row. Use it when GitHub shows a successful delivery but no task appears, when repeated unsafe traffic needs investigation, or when a false-positive quarantine must be cleared before a demo.
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
