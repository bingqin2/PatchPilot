# PatchPilot

PatchPilot is an AI GitHub issue-to-PR agent backend. It receives a GitHub issue comment, creates a durable fix task, inspects the repository through controlled tools, applies a focused patch, runs Maven tests, and opens a Pull Request for human review.

PatchPilot is not a chatbot and does not auto-merge code. The current target is local self-hosted development and private demos.

## What Works Now

- GitHub webhook endpoint for `issue_comment.created`.
- `/agent fix` trigger detection.
- Webhook signature verification.
- MySQL-backed task, queue, timeline, test-run, tool-call, and model-call records.
- Local workspace clone, branch, diff, commit, push, and Pull Request creation.
- Maven test execution with command allowlists.
- OpenAI-compatible model client and plan-driven patch workflow.
- Issue comment status updates for accepted, running, verification, success, and failure states.

## Repository Layout

```text
.
├── PatchPilot/        # Spring Boot backend
├── docs/              # Product docs, plans, progress logs, and operator guides
├── frontend/          # React operations dashboard
├── docker-compose.yml # Local MySQL + backend runtime
└── .env.example       # Self-hosted configuration template
```

Useful docs:

- `docs/agent/temporary-url-webhook.md` - Cloudflare Tunnel webhook setup.
- `docs/agent/idea-local-run.md` - IntelliJ IDEA local run notes.
- `docs/agent/smoke-test-checklist.md` - End-to-end validation checklist.
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
/agent fix
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
curl http://127.0.0.1:8080/api/tasks/{taskId}/summary
curl http://127.0.0.1:8080/api/tasks/{taskId}/timeline
curl http://127.0.0.1:8080/api/tasks/{taskId}/test-runs
curl http://127.0.0.1:8080/api/tasks/{taskId}/tool-calls
curl http://127.0.0.1:8080/api/tasks/{taskId}/model-calls
```

Queue state:

```bash
curl http://127.0.0.1:8080/api/task-queue/summary
curl http://127.0.0.1:8080/api/task-queue/items
```

Task metrics:

```bash
curl http://127.0.0.1:8080/api/tasks/metrics/summary
```

## Frontend Dashboard

The React dashboard lives in `frontend/` and calls the backend through Vite's `/api` proxy.
It includes task metrics, status filters and full-history search backed by `GET /api/tasks`, total-count and `hasMore`-backed `Load more` task pagination, task creation/update times, GitHub Issue, status comment, and Pull Request links, task detail summaries, timeline events, test runs, tool calls and model calls with durations, empty states for missing detail records, task control actions for cancel/retry, and a read-only queue panel backed by `/api/task-queue/*`.
The page coordinator is `frontend/src/App.tsx`; reusable dashboard UI lives under `frontend/src/dashboard/components/`, with shared formatting helpers in `frontend/src/dashboard/format.ts`.
The dashboard task list requests `query`, `status`, `limit`, and `offset` from the backend and consumes the task page response with `items`, `limit`, `offset`, `hasMore`, and `total`.

```bash
cd frontend
npm install
npm run dev
```

Open the printed Vite URL, usually `http://127.0.0.1:5173`. Keep the backend running on `http://127.0.0.1:8080` so dashboard requests can reach the task APIs.

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
- Read or write outside the task workspace.
- Log secrets.
- Report success without verification.

## Current Limitations

- Maven repositories are the first supported target.
- The current runtime is single-process; API and worker separation is future work.
- The React dashboard does not create tasks or merge Pull Requests.
- Temporary Cloudflare URLs are for local testing only.

## Development Workflow

Long-running work should create or update a plan in `docs/plans/` and record evidence in `docs/progress/execution-log.md`. Durable design decisions belong in `docs/progress/decisions.md`.
