# Frontend Design Direction

## Purpose

PatchPilot's frontend is an operations dashboard for maintainers, developers, and demos. Its job is to make the backend workflow visible: which issue triggered a task, what the agent did, where it failed, what tests ran, and which Pull Request was created.

The frontend is not the primary trigger surface. GitHub issue comments remain the main task trigger, and GitHub Pull Requests remain the review surface.

## Design Philosophy

The interface should feel like an engineering control plane, not a chatbot, marketing page, or generic AI SaaS product. Operators should be able to scan task state quickly, inspect evidence, and jump back to GitHub when human review is needed.

Core principles:

- Prefer dense but readable operational views over large promotional sections.
- Show real task records, not abstract "AI progress" copy.
- Keep controls explicit and limited to safe operations.
- Make failures first-class: failed tests, cancelled tasks, queue errors, and agent exceptions should be easy to inspect.
- Make setup failures readable: backend/proxy connectivity problems should tell operators what to check.
- Preserve GitHub as the source of code review and merge decisions.

## Current Structure

The current frontend lives in `frontend/` and uses React, Vite, and TypeScript.

During local development, Vite proxies `/api` and `/health` to `PATCHPILOT_FRONTEND_BACKEND_URL` or `VITE_PATCHPILOT_BACKEND_URL` from the shell environment or repository root `.env`, defaulting to `http://127.0.0.1:8080`.

The page coordinator is `frontend/src/App.tsx`. It loads backend data, owns selected-task state, applies status filters and local search, and coordinates cancel/retry actions.

Reusable dashboard components live under `frontend/src/dashboard/components/`:

- `TaskListPanel`: task list, status filters, backend-backed search, pagination counts, issue/status/PR links.
- `TaskDetailPanel`: selected task summary, copyable task deep link, execution evidence strip, timeline, test runs, tool calls, model calls, cancel/retry actions.
- `QueuePanel`: read-only queue health, summary, and queue items.
- `ConfigurationPanel`: read-only runtime configuration summary with backend health, provider, model, workspace, queue policy, configured/missing secret states, and setup health hints.
- `FailureCausePanel`, `ModelUsagePanel`, and `LatencyPanel`: operational summary cards for failure grouping, token usage, call counts, estimated model cost, and execution latency.
- `MetricCard`, `RecordLine`, and `SummaryItem`: small shared presentation units.

Formatting helpers live in `frontend/src/dashboard/format.ts`.

## Current UX Model

The first screen is the working dashboard:

- Metrics summarize task health.
- A compact refresh status tells operators when top-level dashboard data is still loading.
- Operational summaries highlight failure causes, model usage, and latency without requiring terminal inspection.
- The task list supports status filters, backend-backed search over task history, loaded-versus-total counts, and incremental loading.
- Selecting a task updates the `?taskId=` URL parameter and reveals a copyable task link, execution evidence summary, timeline events, Maven test output, tool-call records, model-call records, and GitHub links.
- Queue visibility shows whether work is pending, delayed, running, failed, or cancelled, with failed/delayed/running health hints before the row list.
- Configuration visibility shows backend `/health` status, the active provider, model, workspace root, queue policy, whether required secrets are configured, and clear health hints for missing secrets or weak optional settings without exposing secret values.
- Cancel and retry are available only for task states where those actions make sense.

This keeps the UI focused on operator questions:

- What is running right now?
- What failed and why?
- Did tests pass?
- What did the agent call?
- How many model tokens were used?
- Where is the GitHub issue or Pull Request?
- Can I share a URL that reopens the same task detail?

## Final Target

The long-term frontend target is a complete maintainer dashboard for safe issue-to-PR automation.

Target capabilities:

- Search and filter complete task history.
- Inspect task timeline, model calls, tool calls, command runs, and test records.
- Track queue health, retries, cancellation, and worker recovery.
- Surface cost, latency, success rate, failure rate, and test pass rate.
- Link every important UI object back to GitHub.
- Provide safe operator controls such as cancel, retry, and configuration inspection.
- Support demo readiness by making a full task lifecycle understandable without reading logs.

## Future Work

Additional future work:

- Dedicated task detail route with shareable URLs.
- Dashboard views for model cost and latency.
- Worker and queue health panels.
- Configuration editing for local setup after the read-only health model is stable.

## Non-Goals

The frontend should not:

- Create tasks as the primary workflow before GitHub issue comments are mature.
- Auto-merge Pull Requests.
- Replace GitHub's review UI.
- Hide task execution behind a chat-only interface.
- Become a marketing landing page.
- Execute arbitrary commands from the browser.

## Success Criteria

The frontend is successful when a maintainer can open it during a live demo or local test run and answer these questions without terminal access:

- Which task was created from which GitHub issue?
- What is its current status?
- What did the agent do?
- Which tools and model calls ran?
- Did Maven tests pass?
- If it failed, what was the failure reason?
- If it succeeded, where is the Pull Request?
