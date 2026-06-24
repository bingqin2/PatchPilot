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

### Fix Task Creation

- A triggering issue comment creates a durable fix task.
- A task records repository owner, repository name, issue number, installation id, trigger user, trigger comment, status, timestamps, and failure reason.
- Task creation must return quickly and must not run repository analysis or model calls inline with webhook handling.
- A task can be queried by id for status and result.
- Task creation must pass authorization, command parsing, actionability, and rate-limit checks before expensive execution begins.
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
- Operators should be able to inspect one quarantine and see the rejected-trigger audit rows and manual safety actions that explain it.
- The system should reject unsupported repositories before model execution, patch generation, test execution, Git mutation, or Pull Request creation.
- The local repository preflight diagnostic should return supported status, selected language/build system, verification command, detection reason, and next operator action so unsupported repository shapes can be fixed before a live `/agent fix`.
- The local repository preflight diagnostic should expose its configured allowed roots through non-sensitive configuration summary APIs and the dashboard so operators can verify scope before using it.
- If project detection is possible from webhook or repository metadata before cloning, the system may reject even earlier.
- The system must never follow user instructions that request secret exfiltration, destructive repository changes, arbitrary shell execution, or permission escalation.
- The system should record rejected trigger decisions with clear operator-facing reasons.
- The system should summarize recent rejected trigger decisions by category, source, trigger user, and repository so operators can detect abuse patterns and tune safety configuration.
- The system should expose active and historical trigger quarantine records with scope, scope key, reason, category, evidence count, window, start time, expiry time, and timestamps.
- The system should record manual safety mutations, including trigger quarantine creation and release, with operator, reason, target, and timestamp.
- Non-triggering comments may be ignored without creating task or rejection audit records.

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
- The system comments on the original issue with the PR link.
- The system does not merge Pull Requests automatically.

### Failure Reporting

- Any task can move to `FAILED`.
- User-actionable failures should be posted as issue comments.
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

## Frontend Requirements

PatchPilot uses React for the frontend. The frontend is primarily an operational dashboard for developers and maintainers.

MVP frontend scope:

- View fix tasks.
- View task status and failure reason.
- Open linked GitHub issue and Pull Request.
- Inspect tool-call summaries.
- Inspect verification output.

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
- Cost, latency, success-rate, and test-pass-rate dashboards.
- Human approval for high-risk actions.

## Success Criteria

PatchPilot MVP is successful when:

- A user can install the GitHub App on a test repository.
- A user can comment `/agent fix` on an open issue.
- PatchPilot creates and executes a fix task asynchronously.
- PatchPilot clones the repository and creates a branch.
- PatchPilot generates a patch for a simple supported Java, Node.js, or Python bug.
- Adapter-selected verification runs and the result is recorded.
- A successful task creates a Pull Request.
- A failed task records and reports a clear failure reason.

The broader product is successful when:

- A supported repository can be fixed through the correct language adapter.
- An unsupported repository fails safely with a clear reason.
- A vague, malicious, or unauthorized `/agent` comment does not start repository execution.
- Each successful PR includes evidence from the adapter's verification command.

## Resume Target

The project should support this resume-level description:

```text
Built PatchPilot, a Spring Boot GitHub App that turns issue comments into automated code-fix workflows. The agent validates commands through a safety gate, clones repositories into isolated workspaces, detects supported language adapters, retrieves relevant code context, generates patches, runs allowlisted tests, and opens Pull Requests with execution traces and test summaries.
```
