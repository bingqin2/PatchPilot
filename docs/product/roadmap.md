# Roadmap

This roadmap breaks PatchPilot into small, testable phases. Each phase should produce working behavior, not only code structure.

Use this document with `docs/product/target-state.md`. The target-state document defines where PatchPilot is going; this roadmap defines how to get there without scope drift.

## Phase 0: Foundation And Documentation

Goal: make the repository understandable, runnable, and ready for focused implementation.

Status: in progress.

Build:

- Product documentation.
- Backend code standard.
- Agent governance documents.
- Root `.gitignore`.
- Local Spring profile.
- Basic health endpoint.
- Basic backend test.
- React frontend scaffold planning.

Exit criteria:

- `mvn test` passes from the backend project.
- The backend starts locally.
- `/health` or `/actuator/health` returns HTTP 200.
- Docs exist under `docs/product`, `docs/plans`, `docs/progress`, and `docs/agent`.

Suggested ExecPlan:

- `docs/plans/000-project-foundation.md`

## Phase 1: GitHub Webhook MVP

Goal: receive and verify GitHub webhook events, then create a fix task for `/agent fix`.

Build:

- GitHub webhook endpoint.
- Signature verification.
- `issue_comment.created` parsing.
- `/agent fix` trigger detection.
- Delivery id idempotency record.
- Task creation.

Do not build yet:

- Repository cloning.
- Model calls.
- PR creation.

Exit criteria:

- GitHub can deliver a webhook to the local or deployed backend.
- Invalid signatures are rejected.
- Non-triggering comments are ignored.
- `/agent fix` creates one task.

Suggested ExecPlan:

- `docs/plans/001-github-webhook-mvp.md`

## Phase 2: Task State And Worker Shell

Goal: create durable task state and run a worker shell asynchronously.

Build:

- `FixTask` model.
- Task status enum.
- Task status APIs.
- Async worker executor.
- Failure recording.
- Basic task logs.

Do not build yet:

- Real repository edits.
- Real model patch generation.

Exit criteria:

- A created task transitions through a simple async lifecycle.
- Worker exceptions mark tasks failed.
- Task detail API shows status and failure reason.

Suggested ExecPlan:

- `docs/plans/002-task-worker-shell.md`

## Phase 3: Workspace And GitHub Client

Goal: clone a repository, create a branch, and comment back on the issue.

Build:

- GitHub App JWT service.
- Installation token service.
- GitHub client wrapper.
- Workspace service.
- Git clone and branch creation.
- Issue comment creation.
- Workspace path guard.

Do not build yet:

- Code patch generation.
- Test running.
- Pull Request creation.

Exit criteria:

- A task can clone a test repository.
- A task can create a branch.
- A task can comment back on the original issue.
- Path guard tests prevent workspace escape.

Suggested ExecPlan:

- `docs/plans/003-workspace-and-github-client.md`

## Phase 4: Agent Tooling And Patch Proposal

Goal: let the agent inspect code through controlled tools and produce a patch proposal.

Build:

- Model provider client.
- Fix issue prompt.
- Structured fix plan output.
- Tool registry.
- Repository tree tool.
- Code search tool.
- File read tool.
- File write tool.
- Diff tool.
- Tool-call audit records.

Do not build yet:

- Large-scale RAG.
- Multi-agent workflow.
- Arbitrary shell commands.

Exit criteria:

- The agent can inspect a small Java repository.
- The agent can propose and apply a simple file change.
- Tool calls are recorded.
- Unsafe file paths are rejected.

Suggested ExecPlan:

- `docs/plans/004-agent-tooling-patch-proposal.md`

## Phase 5: Maven Verification And Pull Request Creation

Goal: run Maven tests and create a Pull Request when verification succeeds.

Build:

- Maven test runner.
- Test result parser.
- Branch commit and push.
- Pull Request creation.
- PR body generation.
- Issue success/failure comments.

Do not build yet:

- Gradle support.
- Docker sandbox.
- Automatic merge.

Exit criteria:

- A simple Java/Maven issue can produce a Pull Request.
- Test output is included in task records and PR text.
- Test failure does not report success.

Suggested ExecPlan:

- `docs/plans/005-maven-verification-pr.md`

## Phase 6: Sandbox, Persistence, And Observability Hardening

Goal: make execution safer and more production-shaped.

Build:

- MySQL task persistence.
- Docker sandbox execution.
- Model-call audit table.
- Tool-call audit table.
- Test-run records.
- Cost and latency metrics.
- Cleanup policies.

Do not build yet:

- Multi-tenant billing.
- Enterprise admin dashboard.

Exit criteria:

- Task history survives restarts.
- Tool calls and model calls can be inspected.
- Commands execute in a safer isolated environment.
- Basic metrics are available.

Suggested ExecPlan:

- `docs/plans/006-sandbox-persistence-observability.md`

## Phase 6.5: Safety Gate And Language Adapter Foundation

Goal: prevent unsafe or unsupported work before expensive execution, and prepare PatchPilot to grow beyond one Java/Maven path.

Build:

- Command parser for supported `/agent` actions.
- Authorization checks for triggering users and repositories.
- Actionability classifier for vague, abusive, malicious, or non-code comments.
- Rate-limit and active-task checks before model or workspace execution.
- `LanguageAdapter` interface for repository detection, allowed test commands, and test result summaries.
- Java/Maven adapter as the first concrete implementation.
- Clear ignored or rejected outcomes for unsupported languages and unsafe requests.

Do not build yet:

- Broad multi-language support in one step.
- Hosted multi-tenant billing.
- Automatic issue triage without an explicit command.

Exit criteria:

- Vague or malicious `/agent` comments do not create executable work.
- Unauthorized users cannot trigger repository execution.
- Supported Java/Maven repositories still run through the existing issue-to-PR path.
- Unsupported repositories fail safely with an actionable reason.
- New language support can be added by implementing an adapter instead of rewriting the agent workflow.


## Phase 7: React Operations Dashboard

Goal: add a small React dashboard for task monitoring and demo clarity.

Build:

- React + Vite + TypeScript frontend scaffold.
- Task list page.
- Task detail page.
- Tool-call trace view.
- Test output panel.
- GitHub issue and Pull Request links.

Do not build yet:

- Full admin console.
- Marketing landing page.
- Browser extension.

Exit criteria:

- A reviewer can see task status, failure reasons, and PR links in the UI.
- The frontend calls real backend APIs instead of static mock data for task records.

Suggested ExecPlan:

- `docs/plans/007-react-operations-dashboard.md`

## Phase 7.5: Multi-Language Adapter Expansion

Goal: extend PatchPilot from Java/Maven to a small set of well-supported repository types without weakening safety boundaries.

Build:

- Java/Gradle adapter.
- Node.js adapter for Bun projects.
- Node.js adapter for npm projects.
- Node.js adapters for pnpm and yarn projects.
- Python adapter for pytest projects.
- Python adapters for Poetry and uv projects.
- Future Python adapters for tox, nox, or hatch projects.
- Adapter-specific documentation and demo repositories. Basic detection fixtures live in `docs/demo-repositories/`.
- Adapter-aware dashboard and task detail labels.

Do not build yet:

- Every language or build system.
- Arbitrary user-supplied test commands.
- Cross-repository or monorepo-wide autonomous refactors.

Exit criteria:

- Each supported adapter can detect a repository, run an allowlisted verification command, record evidence, and create a PR for a simple issue.
- Unsupported projects are rejected before risky execution.
- The README clearly lists supported languages and build systems.

## Phase 8: Product Polish And Demo Readiness

Goal: make the project easy to demonstrate for internships and interviews.

Build:

- Demo repository.
- Seed issues.
- README with architecture diagram.
- Short demo script.
- Success-rate benchmark over a small issue set.
- Optional Chrome extension trigger.

Exit criteria:

- A reviewer can run a full demo.
- The project has a clear architecture story.
- Resume bullets can be backed by working behavior and metrics.

## Phase 9: Public Hosted Service Readiness

Goal: prepare PatchPilot for external users after the self-hosted and private-demo workflows are stable.

Build:

- Public GitHub App installation documentation.
- Hosted deployment runbook.
- Production environment variable documentation.
- Rate limiting and abuse controls.
- Task and audit retention policy.
- Operational health and failure dashboards.
- User-facing self-hosted and hosted usage guides.

Do not build yet:

- Public multi-user hosted service before the core issue-to-PR workflow is reliable.
- Automatic PR merge.

Exit criteria:

- An external repository maintainer can install the GitHub App and use `/agent fix` without cloning the project.
- The hosted service has documented uptime, secret, persistence, and monitoring expectations.

Suggested ExecPlan:

- `docs/plans/009-public-hosted-service-readiness.md`
