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
- Operators should be able to paste a GitHub delivery payload into an admin-protected read-only diagnostic endpoint and see signature status, JSON validity, event/action support, `/agent fix` recognition, parsed repository/issue fields, and the next operator action without creating tasks or delivery records.

### Fix Task Creation

- A triggering issue comment creates a durable fix task.
- A task records repository owner, repository name, issue number, installation id, trigger user, trigger comment, status, timestamps, and failure reason.
- Task creation must return quickly and must not run repository analysis or model calls inline with webhook handling.
- A task can be queried by id for status and result.
- Task creation must pass authorization, command parsing, actionability, and rate-limit checks before expensive execution begins.
- Operators should be able to dry-run a proposed `/agent fix` trigger as either a manual API source or a GitHub issue-comment source and see whether it would create a task or be blocked without creating tasks, queue work, rejected-trigger audit rows, GitHub comments, webhook delivery diagnostics, or rate-limit records.
- Task execution must pass a repository language-adapter preflight after workspace preparation and before model patch generation.
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
- Demo readiness and the operator setup checklist should warn when repository-preflight allowed roots do not cover checked-in demo fixture paths.
- Demo readiness, the smoke checklist, the demo script, the session snapshot checklist, and the operator setup checklist should warn when a supported adapter's selected verification executable is not available on the backend process `PATH`.
- Demo readiness and the operator setup checklist should warn when the queue worker has not started, has most recently errored, or has stopped polling within the configured stale threshold.
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
- Evaluation runs should record model, prompt version, repository revision, success metrics, failure categories, cost, latency, and a copyable Markdown report.
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
- Inspect a single demo session snapshot that combines evidence, script, runbook, checklist, health contract, share summary, and next actions.
- Follow a read-only demo script that gives ordered operator actions, verification commands, troubleshooting targets, and health-contract guarantees before and during a live smoke run.
- Copy a Markdown demo runbook generated from the current evidence bundle.
- Copy or download a Markdown demo session report generated from the current session snapshot.
- Archive the current demo session report into a recent list and copy or download archived Markdown reports during or after a live demo. Database-backed local profiles should persist these archives across backend restarts.
- Inspect queue worker readiness, last poll age, and operator action before a live issue-to-PR demo.
- Evaluate a manual `/agent fix` trigger before creating a task and see the gate decisions plus next operator action.
- Evaluate a pasted GitHub webhook payload before redelivery and see whether the temporary URL, webhook secret, event type, action, and `/agent fix` comment shape look correct.
- Inspect and copy a single adapter readiness report covering supported languages, allowlisted verification commands, fixture pass rate, and fixture failures.
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
- Evaluation harness for issue-to-PR benchmark cases across supported repository adapters.
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
- An operator can verify demo readiness through a single evidence bundle covering setup, safety, queue, webhook, and recent PR signals.
- An operator can inspect a single demo session snapshot before or after a live run without manually assembling evidence, script, runbook, checklist, and health-contract responses.
- An operator can follow an ordered demo script whose endpoint is explicitly read-only and whose steps point to dashboard evidence and curl verification commands.
- An operator can copy a Markdown runbook that explains the current demo status and next actions without manually assembling API responses.
- An operator can copy or download a Markdown session report that includes the snapshot, script, checklist, health contract, next actions, and runbook.
- An operator can copy an adapter readiness report that proves current multi-language coverage and highlights fixture drift before a live run.
- An operator can see whether each adapter's verification executable is available in the current backend runtime before a live run.
- An operator can see missing adapter verification executables reflected in the demo readiness gate instead of only in the adapter report.
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
