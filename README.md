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
- MySQL-backed task, queue, timeline, test-run, tool-call, and model-call records.
- Local workspace clone, branch, diff, commit, push, and Pull Request creation.
- Java/Maven, Java/Gradle, Node/Bun, Node/npm, Node/pnpm, Node/yarn, Python/tox, Python/nox, Python/hatch, Python/Poetry, Python/uv, and Python/pytest language adapters backed by an adapter-driven verification runner with command allowlists.
- Generated diff risk gate that blocks sensitive files, secret-like added lines, binary patches, and overly broad patches before tests, commits, pushes, or Pull Request creation.
- Human approval flow for generated-diff risk rejections: `PENDING_REVIEW` tasks expose the generated diff for inspection, then can be cancelled or explicitly approved to resume from the already-generated workspace and continue verification, commit, push, and Pull Request creation.
- Unsupported repository preflight that fails before patch generation, test execution, Git mutation, or Pull Request creation.
- OpenAI-compatible model client and plan-driven patch workflow.
- Issue comment status updates for accepted, running, verification, success, and failure states.
- Demo readiness gate that summarizes credentials, adapter fixtures, queue health, and recent PR evidence before a live smoke run.

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
```

The webhook secret must match the GitHub webhook configuration. The GitHub token is used for clone, push, issue comments, and Pull Request creation. Do not commit `.env`.

Optional safety allowlists can restrict who and what repository may create tasks:

```bash
PATCHPILOT_ALLOWED_TRIGGER_USERS=bingqin2,local-operator
PATCHPILOT_ALLOWED_REPOSITORIES=bingqin2/PatchPilot
```

Leave either value empty to keep that dimension unrestricted for local development. When configured, both GitHub webhooks and manual dashboard tasks are checked before task creation.

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
curl http://127.0.0.1:8080/api/tasks
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
curl http://127.0.0.1:8080/api/tasks
curl "http://127.0.0.1:8080/api/tasks?status=FAILED&limit=20"
curl "http://127.0.0.1:8080/api/tasks?repositoryOwner=bingqin2&repositoryName=PatchPilot"
curl http://127.0.0.1:8080/api/tasks/{taskId}
curl http://127.0.0.1:8080/api/tasks/{taskId}/detail
curl http://127.0.0.1:8080/api/tasks/{taskId}/report
curl http://127.0.0.1:8080/api/tasks/{taskId}/summary
curl http://127.0.0.1:8080/api/tasks/{taskId}/timeline
curl http://127.0.0.1:8080/api/tasks/{taskId}/test-runs
curl http://127.0.0.1:8080/api/tasks/{taskId}/tool-calls
curl http://127.0.0.1:8080/api/tasks/{taskId}/model-calls
curl "http://127.0.0.1:8080/api/rejected-triggers?limit=20"
```

Use `/detail` for dashboard-style task inspection. It returns the task audit summary, latest queue item, queue history, latest generated diff, timeline events, test runs, tool calls, and model calls in one response. Use `/report` to copy a Markdown diagnostic summary for a task, including the generated diff when one was captured. The narrower endpoints remain available for focused debugging.
Use `/api/rejected-triggers` to inspect rejected `/agent fix` attempts that did not create tasks, including unsafe command text, unauthorized users, unauthorized repositories, and the rejection reason.

If a generated diff is blocked by the risk gate, the task moves to `PENDING_REVIEW` instead of `FAILED`. Inspect the generated diff in the task detail or copied report first, then check the `GeneratedDiffRiskGate` tool-call output for the concrete rejection reason. If the diff is intentionally allowed, approve it through the dashboard or API with an approver and reason:

```bash
curl -X POST http://127.0.0.1:8080/api/tasks/{taskId}/approve-review \
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

PatchPilot currently executes fixes for Java repositories with Maven or Gradle build files, Node.js repositories with Bun, npm, pnpm, or yarn test scripts, and Python repositories with tox, nox, hatch, Poetry, uv, or pytest test signals. After cloning the task workspace, the backend runs the language adapter preflight:

- supported: `pom.xml` with `mvnw`, using `./mvnw test`
- supported: `pom.xml` without `mvnw`, using `mvn test`
- supported: `build.gradle` or `build.gradle.kts` with `gradlew`, using `./gradlew test`
- supported: `build.gradle` or `build.gradle.kts` without `gradlew`, using `gradle test`
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
After a repository is detected, each task stores the selected `language`, `buildSystem`, `verificationCommand`, and nullable `adapterDetectionReason`. These fields are returned by the task APIs and shown in the dashboard detail view so operators can confirm whether a task used Maven, Gradle, Bun, npm, pnpm, yarn, tox, nox, hatch, Poetry, uv, or pytest, and which repository signal caused that selection, without opening raw tool-call logs.

After patch generation, PatchPilot runs a deterministic generated-diff risk gate before adapter verification or any GitHub write. The gate rejects sensitive files such as `.env`, private keys, and GitHub Actions workflows; secret-like added lines; binary patches; and diffs that exceed the configured file or line thresholds. Rejections move the task to `PENDING_REVIEW`, keep the concrete reason in `failureReason`, and store a failed `GeneratedDiffRiskGate` tool call so task reports and the dashboard can explain why execution stopped. Approval requires an operator and reason, and the task keeps that approval audit metadata when it resumes.

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

Run the safe local adapter smoke when you want to demonstrate supported repository detection without GitHub, model credentials, Docker, or PR creation:

```bash
scripts/adapter-smoke.sh
scripts/adapter-smoke.sh --backend
```

Runtime configuration summary:

```bash
curl http://127.0.0.1:8080/api/configuration/summary
```

This endpoint returns only non-sensitive values and configured/missing booleans for secrets. It also returns safety settings such as model trigger classification and trigger rate-limit thresholds. It never returns raw API keys, GitHub tokens, or webhook secrets.

Demo readiness:

```bash
curl http://127.0.0.1:8080/api/demo/readiness
```

This endpoint aggregates backend reachability, required credential flags, model cost configuration, adapter fixture verification, queue state, and recent completed Pull Request evidence into one `READY`, `NEEDS_ATTENTION`, or `BLOCKED` result. Use it before a live `/agent fix` demo to see the next operator action without manually checking every panel or curl endpoint.

Queue state:

```bash
curl http://127.0.0.1:8080/api/task-queue/summary
curl http://127.0.0.1:8080/api/task-queue/items
```

Task metrics:

```bash
curl http://127.0.0.1:8080/api/tasks/metrics/summary
curl http://127.0.0.1:8080/api/tasks/metrics/failure-causes
curl http://127.0.0.1:8080/api/tasks/metrics/model-usage
curl http://127.0.0.1:8080/api/tasks/metrics/latency
```

Task list, status count, and metrics APIs accept the same investigation filters:

```bash
curl "http://127.0.0.1:8080/api/tasks?language=node&buildSystem=npm"
curl "http://127.0.0.1:8080/api/tasks/status-counts?language=node&buildSystem=npm"
curl "http://127.0.0.1:8080/api/tasks/metrics/summary?language=node&buildSystem=npm"
```

Model cost estimates default to `0`. Configure per-token prices when needed:

```bash
PATCHPILOT_AGENT_COST_PROMPT_TOKEN_USD=0.000001
PATCHPILOT_AGENT_COST_COMPLETION_TOKEN_USD=0.000002
```

## Frontend Dashboard

The React dashboard lives in `frontend/` and calls the backend through Vite's `/api` proxy.
It includes a demo readiness panel backed by `GET /api/demo/readiness`, task metrics, refresh progress and last-refresh feedback, failure-cause grouping, model token and estimated-cost summaries, latency summaries, a non-sensitive runtime configuration panel backed by `/api/configuration/summary`, backend `/health` status, configuration health hints for missing secrets and weak queue/cost/trigger-rate-limit settings, a supported-adapters panel backed by `GET /api/language-adapters`, a fixture verification panel backed by `GET /api/language-adapters/fixtures`, a manual task creation form backed by `POST /api/tasks`, status filters with scoped count badges backed by `GET /api/tasks/status-counts`, repository owner/name filters, language/build-system adapter filters, created time range filters, sort control, and full-history search backed by `GET /api/tasks`, one-click filter reset, total-count and `hasMore`-backed `Load more` task pagination, task creation/update times, GitHub Issue, status comment, and Pull Request links, `/tasks/{taskId}` deep links with legacy `?taskId=` compatibility, copyable links and copyable Markdown reports for selected task details, task detail summaries loaded through `GET /api/tasks/{taskId}/detail`, selected-task adapter metadata with detection reason, selected-task queue status and queue history with retry/last-error context, execution evidence summaries including generated-diff risk-gate blocks, timeline events, test runs, tool calls and model calls with durations, empty states for missing detail records, task control actions for cancel/retry, and a read-only queue panel with health hints backed by `/api/task-queue/*`.
The page coordinator is `frontend/src/App.tsx`; reusable dashboard UI lives under `frontend/src/dashboard/components/`, with shared formatting helpers in `frontend/src/dashboard/format.ts`.
The dashboard task list requests `query`, `status`, `repositoryOwner`, `repositoryName`, `language`, `buildSystem`, `createdAfter`, `createdBefore`, `sort`, `limit`, and `offset` from the backend and consumes the task page response with `items`, `limit`, `offset`, `hasMore`, and `total`.
Status count badges and metrics use the same search, repository, adapter, and created-time scope as the task list but intentionally ignore the active `status`, `sort`, `limit`, and `offset` values so every status button shows the distribution for the current investigation scope.
The dashboard also stores `status`, `query`, repository filters, adapter filters, created time filters, and non-default `sort` in the browser URL, so links like `/tasks/{taskId}?status=FAILED&query=maven&repositoryOwner=bingqin2&repositoryName=PatchPilot&language=node&buildSystem=npm&sort=createdAtAsc&createdAfter=2026-06-20T01:00:00Z&createdBefore=2026-06-21T01:00:00Z` reopen the same filtered investigation view. The `Clear filters` action removes `status`, `query`, `repositoryOwner`, `repositoryName`, `language`, `buildSystem`, `createdAfter`, and `createdBefore`, keeping sort, the selected task route, unrelated query parameters, and hash fragments intact.
The selected-task panel uses the aggregate detail endpoint so opening a task needs one backend detail request instead of separate summary, timeline, test-run, tool-call, and model-call requests.
If the backend is down or the Vite proxy target is wrong, the dashboard shows a backend/proxy guidance message instead of a raw JSON parsing error.

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

- Java/Maven, Java/Gradle, Node/Bun, Node/npm, Node/pnpm, Node/yarn, Python/tox, Python/nox, Python/hatch, Python/Poetry, Python/uv, and Python/pytest repositories are the first supported targets through explicit language adapters.
- Custom runner adapters are future work.
- The current runtime is single-process; API and worker separation is future work.
- The React dashboard can create manual demo tasks, but GitHub issue comments remain the primary production trigger. It does not merge Pull Requests.
- Temporary Cloudflare URLs are for local testing only.

## Development Workflow

Long-running work should create or update a plan in `docs/plans/` and record evidence in `docs/progress/execution-log.md`. Durable design decisions belong in `docs/progress/decisions.md`.
