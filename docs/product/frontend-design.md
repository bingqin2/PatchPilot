# Frontend Design Direction

## Purpose

PatchPilot's frontend is an operations dashboard for maintainers, developers, and demos. Its job is to make the backend workflow visible: which issue triggered a task, what the agent did, where it failed, what tests ran, and which Pull Request was created.

The frontend is not the primary production trigger surface. GitHub issue comments remain the main task trigger, and GitHub Pull Requests remain the review surface. The dashboard may offer a manual task creation form for local demos and debugging, but it still goes through the backend queue.

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

When the backend protects operator APIs with `PATCHPILOT_ADMIN_TOKEN`, the dashboard does not require a separate login flow. `App.tsx` shows a compact header control that reports whether this browser has a saved admin token, lets an operator save or replace the local token, and lets the operator clear it when rotating credentials or testing unauthenticated behavior. If a protected API call returns `Admin token is required`, the same save-and-retry path appears inline inside the alert. Saved tokens live in browser `localStorage` as `patchpilot.adminToken`; the API helper reads that value for later requests and sends it as `X-PatchPilot-Admin-Token`.

The page coordinator is `frontend/src/App.tsx`. It loads backend data, owns selected-task state, applies status filters, repository owner/name filters, language/build-system adapter filters, created time range filters, task-list sort, and search state, coordinates manual creation plus cancel/retry actions, and runs local repository preflight diagnostics through `POST /api/repository-preflight`. It loads `/health` before the protected dashboard API batch so the connectivity panel can distinguish "backend/proxy unavailable" from "backend is up but protected APIs need an admin token." The operator setup checklist is deliberately derived from the same loaded data instead of a new API, so it acts as a first-screen summary of the detailed panels below it.
It also loads `GET /api/demo/evidence-bundle`, `GET /api/demo/session-snapshot`, `GET /api/demo/script`, `GET /api/demo/readiness`, `GET /api/demo/smoke-checklist`, `GET /api/language-adapters`, `GET /api/language-adapters/fixtures`, `GET /api/rejected-triggers`, `GET /api/rejected-triggers/summary`, `GET /api/trigger-quarantines`, `GET /api/operator-safety-audits`, and `GET /api/github/webhook-deliveries` so demo evidence, a full demo session snapshot, the read-only demo script, readiness, final smoke-check steps, supported repository shapes, fixed verification commands, detection signals, demo fixture paths, current fixture pass/fail status, recent rejected task triggers, rejected-trigger abuse patterns, active trigger quarantines, recent manual safety mutations, recent GitHub delivery outcomes, and redelivery guidance are visible in the dashboard instead of only in source code, terminal smoke output, curl output, or GitHub's delivery page. It calls `GET /api/demo/runbook` only when an operator clicks `Copy runbook`, so the Markdown report is generated from current backend evidence without adding another always-on refresh request. The rejected-trigger panel also posts manual trigger quarantine create and release actions to the same protected quarantine API so operators can block abusive trigger users or repositories and recover false positives without leaving the dashboard.

Selected-task detail uses the `/tasks/{taskId}` frontend route and `GET /api/tasks/{taskId}/detail`, an aggregate read-model endpoint that returns the task audit summary, latest queue item, queue history, latest generated diff, timeline events, test runs, tool calls, and model calls together. The task summary includes selected adapter metadata and a nullable detection reason, which the detail evidence strip renders next to the verification command so operators can see why the task chose that language/build path. If the generated-diff risk gate rejects a patch, the task appears as `PENDING_REVIEW`, the same evidence strip shows a `Risk gate BLOCKED` marker, the generated-diff panel shows the exact patch to inspect before approval, and the tool-call list shows the concrete rejection reason. Operators can cancel pending-review tasks or approve the existing generated diff with an approver selected from the configured review-approval allowlist plus a reason. Approval requeues the task to resume the same workspace after the risk gate instead of asking the model to produce a new patch, and approved tasks show the stored approval audit metadata in detail. If no review approvers are configured, the approval form is disabled and explains the missing configuration. Retry remains hidden for pending-review tasks because retry is reserved for failed or cancelled fresh runs. When a failed or cancelled task is retried, the same task records retry lineage with the previous status, failure reason, and retry time so operators can see what state was recovered. Legacy `?taskId=` links still select the same task. Status, search, repository owner/name filters, adapter filters, created time filters, and non-default sort state are stored as URL query parameters, so `/tasks/{taskId}?status=PENDING_REVIEW&query=maven&repositoryOwner=bingqin2&repositoryName=PatchPilot&language=node&buildSystem=npm&sort=createdAtAsc&createdAfter=2026-06-20T01:00:00Z&createdBefore=2026-06-21T01:00:00Z` restores the selected task and task-list view together. The clear-filter action removes `status`, `query`, `repositoryOwner`, `repositoryName`, `language`, `buildSystem`, `createdAfter`, and `createdBefore`, preserving sort, the selected task route, unrelated query parameters, and hash fragments. Markdown task reports use `GET /api/tasks/{taskId}/report` so operators can copy a compact diagnostic summary and generated diff without manually assembling API responses. This keeps the dashboard detail panel to one request per selected task while preserving narrower backend endpoints for curl-based debugging.

Reusable dashboard components live under `frontend/src/dashboard/components/`:

- `TaskListPanel`: task list, status filters with scoped count badges, repository owner/name filters, language/build-system adapter filters, created time range filters, backend-backed search, backend-backed newest/oldest sort control, clear-filter action, pagination counts, issue/status/PR links.
- `TaskDetailPanel`: selected task summary, copyable task deep link, copyable task report, queue state and queue history, execution evidence strip, generated-diff preview, configured-approver approval form, approval audit metadata, retry lineage, timeline, test runs, tool calls, model calls, cancel/retry/approve-review actions.
- `ConnectivityPanel`: top-of-page operational check for backend `/health`, saved browser admin-token state, protected API reachability, and the next corrective action when startup, proxy, or admin-token access is wrong.
- `OperatorSetupChecklistPanel`: read-only demo setup checklist that turns connectivity, required credentials, safety policy, adapter fixtures, queue health, and recent Pull Request evidence into ready/attention checks with next actions.
- `DemoEvidenceBundlePanel`: top-level demo evidence summary that combines readiness, smoke checklist, adapter fixture counts, queue state, recent task/PR evidence, latest webhook delivery, rejected-trigger counts, active quarantine count, and a copyable Markdown runbook.
- `DemoSessionSnapshotPanel`: one-panel demo session summary that combines current evidence, script step count, runbook-backed handoff context, operator checklist, health contract, share summary, and next actions.
- `DemoScriptPanel`: ordered live-demo script with operator actions, verification commands, success criteria, troubleshooting panels, current evidence, and a read-only health contract.
- `DemoReadinessPanel`: first-screen readiness gate that summarizes whether a controlled issue-to-PR demo is ready, needs attention, or is blocked.
- `DemoSmokeChecklistPanel`: final pre-demo checklist that orders readiness gate, webhook delivery, task execution, and Pull Request evidence with concrete next actions.
- `ManualTaskForm`: local demo/debug task creation through `POST /api/tasks`, with explicit repository, issue, trigger user, and `/agent fix` command fields.
- `QueuePanel`: read-only queue health, summary, and queue items.
- `RejectedTriggerPanel`: recent `/agent fix` attempts that were refused before task creation, including command text, repository, trigger user, delivery id, stable rejection category, rejection reason, a category filter, and a compact recent-window summary by category, source, top trigger users, and top repositories. It also surfaces active trigger-user and repository quarantine records with evidence counts, expiry times, creator metadata, manual create controls, release buttons, an evidence drilldown for one selected quarantine, recent operator safety audit rows, and `ABUSE_QUARANTINED` audit rows created by repeated rejected-trigger patterns.
- `WebhookDeliveryPanel`: read-only recent delivery diagnostics for ignored comments, rejected triggers, duplicate deliveries, invalid signatures, bad payloads, active-task collisions, and task-created outcomes, including whether a delivery should be fixed and redelivered from GitHub.
- `ConfigurationPanel`: read-only runtime configuration summary with backend health, provider, model, workspace, queue policy, trigger rate limits, rejected-trigger quarantine policy, generated-diff policy state, configured/missing secret states, and setup health hints.
- `SupportedAdaptersPanel`: read-only support matrix for Java/Maven, Java/Gradle, Go, Node/Bun, Node/npm, Node/pnpm, Node/yarn, Python/tox, Python/nox, Python/hatch, Python/Poetry, Python/uv, and Python/pytest adapters.
- `RepositoryPreflightPanel`: local diagnostic form that runs adapter detection against a backend-local repository path before an operator creates a real task. It shows supported status, selected adapter metadata, verification command, detection reason, and unsupported adapter guidance without running tests or mutating Git.
- `AdapterFixtureVerificationPanel`: read-only fixture verification matrix showing expected versus actual adapter detection for each checked-in demo fixture.
- `FailureCausePanel`, `ModelUsagePanel`, and `LatencyPanel`: operational summary cards for failure grouping, token usage, call counts, estimated model cost, and execution latency.
- `MetricCard`, `RecordLine`, and `SummaryItem`: small shared presentation units.

Formatting helpers live in `frontend/src/dashboard/format.ts`.

## Current UX Model

The first screen is the working dashboard:

- Demo readiness summarizes credentials, safety policy state, adapter fixture verification, queue health, and recent Pull Request evidence before an operator starts a live smoke run.
- The demo evidence bundle gives one status, count set, latest evidence links, and next-action list for live-demo readiness.
- The demo session snapshot gives one session id, share summary, checklist, health contract, current evidence, and script context for pre-run confirmation or post-run handoff.
- The demo script turns that evidence into a guided path with concrete commands, success criteria, troubleshooting panels, and a read-only contract before the operator touches GitHub.
- The smoke checklist turns readiness, webhook delivery, task execution, and Pull Request evidence into ordered final checks before the operator posts the live `/agent fix` comment.
- Metrics summarize task health.
- A compact refresh status tells operators when top-level dashboard data is still loading, and the title area shows when the dashboard last refreshed successfully.
- Operational summaries highlight failure causes, model usage, and latency without requiring terminal inspection.
- A manual task form can enqueue a local demo/debug task through the same backend task and queue path used by webhook-created tasks.
- The task list supports URL-backed status filters with scoped count badges, repository owner/name filters, language/build-system adapter filters, created time range filters, backend-backed search over task history, newest/oldest sorting, one-click filter reset, loaded-versus-total counts, and incremental loading.
- Status count badges come from `GET /api/tasks/status-counts`. They follow search, repository, adapter, and created-time filters, but ignore the active status, sort, limit, and offset so operators can see the distribution before switching status tabs.
- Metrics and operational summary panels use the same search, repository, adapter, and created-time scope as the task list, so a filtered Node/npm view does not show global Maven/Python task totals.
- Selecting a task updates the `/tasks/{taskId}` route while preserving active filter query parameters, loads aggregate task detail in one request, and reveals a copyable task link, copyable Markdown report, selected language adapter metadata with detection reason, queue state, queue history, execution evidence summary, timeline events, verification output, tool-call records, model-call records, and GitHub links.
- Generated-diff risk-gate failures are visible before scrolling through raw records. The generated-diff preview shows the exact patch before approval, with `GeneratedDiffRiskGate` still available in tool-call evidence for exact rejection details.
- Pending-review tasks expose an explicit approve-review form that records an approver and reason before resuming the already-generated workspace.
- Queue visibility shows whether work is pending, delayed, running, failed, or cancelled, with failed/delayed/running health hints before the row list.
- Rejected-trigger visibility shows why a `/agent fix` comment did not create a task when the safety gate, allowlists, rate limiter, rejected-trigger quarantine, or model classifier intentionally refused it. Each row shows a category badge, and the panel can filter by category through the URL-backed `rejectedCategory` parameter so operators can isolate vague requests, dangerous instructions, unauthorized users, blocked repositories, rate limits, abuse quarantine, and model-classifier refusals without parsing reason text. The panel also summarizes the recent rejection window by category, source, top trigger users, and top repositories; category summary rows are clickable and reuse the same filter. Active trigger quarantines are shown above the audit rows so operators can see the current enforced safety state before reading individual refusals, manually add a temporary trigger-user or repository block, inspect the evidence behind a selected quarantine, and release a false-positive quarantine. Recent operator safety audit rows are shown in the same panel so manual create/release actions are traceable by operator, reason, target, and time.
- Webhook delivery visibility shows whether recent GitHub deliveries reached PatchPilot, why they were ignored or rejected, and which task id was created when a delivery succeeded.
- Configuration visibility shows backend `/health` status, the active provider, model, workspace root, queue policy, trigger rate-limit thresholds, rejected-trigger quarantine thresholds, generated-diff risk-gate state, protected path pattern count, repository-preflight allowed roots, whether required secrets are configured, and clear health hints for missing secrets or weak optional settings without exposing secret values.
- Supported-adapter visibility shows each supported language/build system, verification command, detection signals, and demo fixture path. Fixture verification visibility shows whether each demo fixture still maps to the expected adapter and command. If either adapter API fails, its panel shows a local warning while the rest of the dashboard can still load.
- Repository preflight visibility lets an operator enter an allowed local path such as `docs/demo-repositories/java-maven`, see the configured allowed roots, run the real adapter registry, and see whether that repository shape would be supported before creating a task.
- If the readiness API fails, the demo readiness panel shows a local warning while the rest of the dashboard can still load.
- Cancel and retry are available only for task states where those actions make sense; retried tasks keep source failure context in the detail panel.

This keeps the UI focused on operator questions:

- What is running right now?
- What failed and why?
- Did tests pass?
- Which language adapter and verification command ran?
- Why was that language adapter selected?
- Which repository shapes are supported right now?
- Do the checked-in demo fixtures still detect as the expected adapters?
- Why did this `/agent fix` comment not create a task?
- What did the agent call?
- How many model tokens were used?
- Where is the GitHub issue or Pull Request?
- Did GitHub's latest webhook delivery reach PatchPilot and, if not, was it a signature, payload, safety, duplicate, or active-task issue?
- Is there enough live-demo evidence to proceed, or should setup be fixed first?
- Can I share a URL that reopens the same task detail?
- Can I copy a compact report that explains the selected task state?

## Final Target

The long-term frontend target is a complete maintainer dashboard for safe issue-to-PR automation.

Target capabilities:

- Search and filter complete task history.
- Inspect task timeline, generated diff, model calls, tool calls, command runs, and test records.
- Track queue health, retries, cancellation, and worker recovery.
- Surface cost, latency, success rate, failure rate, and test pass rate.
- Link every important UI object back to GitHub.
- Provide safe operator controls such as cancel, retry, generated-diff review approval, and configuration inspection.
- Make safety decisions observable by showing rejected trigger audit records, active trigger quarantines, operator safety audit records, and recent rejection-pattern summaries in the same control plane as accepted tasks.
- Show the supported adapter matrix, fixture paths, and fixture pass/fail verification for demo planning and troubleshooting.
- Support manual demo task creation that still uses the durable backend queue.
- Support demo readiness by making a full task lifecycle understandable without reading logs.
- Support a read-only demo script that gives operators the next action and verification command without adding another task-triggering path.
- Support final smoke-check decisions by pairing every live-demo step with current evidence and a next action.

## Future Work

Additional future work:

- Worker and queue health panels.
- Configuration editing for local setup after the read-only health model is stable.

## Non-Goals

The frontend should not:

- Replace GitHub issue comments as the primary production task trigger.
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
- Did verification pass?
- If it failed, what was the failure reason?
- If it succeeded, where is the Pull Request?
