# Target State

## Product Target

PatchPilot should become a production-shaped AI software maintenance backend that can safely assist repository maintainers with well-scoped GitHub issues across multiple programming languages.

The target state is not autonomous merging or unrestricted code execution. The target state is reliable, human-reviewed issue-to-PR automation that can expand language support through explicit, tested adapters.

PatchPilot should only execute when a request is clear, authorized, actionable, and supported. Unclear comments, unsupported repositories, unsafe instructions, and abusive usage should be ignored or rejected before task execution begins.

## Language Support Target

PatchPilot should not pretend to solve every repository through one generic workflow. Each supported language should be integrated through a `LanguageAdapter` boundary that defines:

- How to detect the project type.
- Which files and dependency manifests are important.
- Which verification commands are allowed.
- How test results are summarized.
- Which failure states mean the repository is unsupported.

The first stable adapters are Java/Maven, Java/Gradle, Node/npm, Node/pnpm, Node/yarn, and Python/pytest. Follow-up adapters should prioritize common interview- and demo-friendly stacks:

- Node.js with bun.
- Python with Poetry, tox, nox, or uv.

Adding a new language is only complete when PatchPilot can detect the repository, generate a focused patch, run the adapter's allowed tests, record the evidence, and create a reviewable Pull Request.

## Deployment Target

PatchPilot should support two deployment modes:

### Hosted Service Mode

In hosted service mode, the project owner deploys the PatchPilot backend and publishes a GitHub App installation link. Repository maintainers install the shared PatchPilot GitHub App, choose repositories, and trigger fixes from GitHub issues.

Target hosted deployment shape:

```text
Internet
  -> HTTPS / Nginx
  -> patchpilot-backend container
  -> MySQL
```

Hosted mode requires:

- A public HTTPS backend endpoint.
- A registered GitHub App.
- A GitHub App webhook URL such as `https://api.example.com/api/github/webhook`.
- Server-side environment variables for GitHub App credentials, webhook secret, database credentials, and model provider credentials.
- A backend deployment that can receive webhook events continuously.

### Self-Hosted Mode

In self-hosted mode, users clone the repository, create their own GitHub App, configure their own secrets, and run PatchPilot with Docker Compose or equivalent infrastructure.

Target self-hosted setup:

```text
git clone patchpilot
  -> configure .env
  -> docker compose up --build
  -> install self-owned GitHub App
  -> comment /agent fix on an issue
```

Self-hosted mode requires documentation for:

- GitHub App creation.
- Required GitHub App permissions and webhook events.
- `.env` configuration.
- MySQL startup.
- Backend startup.
- Webhook URL setup through a public domain or local tunnel.

## Deployment Rollout Strategy

PatchPilot should use a staged deployment strategy.

### Stage 1: Local Self-Hosted Development

The current implementation target is local self-hosted development. The project owner runs PatchPilot locally or on a personal development machine, creates a personal GitHub App, and uses a local tunnel when GitHub webhooks need to reach the backend.

Stage 1 goals:

- Validate backend startup.
- Validate MySQL connectivity.
- Validate webhook delivery through a tunnel.
- Validate `/agent fix` on personal test repositories.
- Keep secrets local and avoid public multi-user exposure.

### Stage 2: Private Demo Deployment

After the basic workflow is working, PatchPilot can be deployed to a private server for demos. The GitHub App can remain private or limited to selected repositories.

Stage 2 goals:

- Use a real HTTPS backend URL.
- Run backend with Docker Compose or equivalent.
- Use MySQL with persistent storage.
- Demonstrate issue-to-PR flow on controlled repositories.

### Stage 3: Public Hosted Service

Only after the workflow is mature should PatchPilot be offered as a hosted service for external users. At that point, the GitHub App can be public, documentation should include an installation link, and operational safeguards must be in place.

Stage 3 goals:

- Public GitHub App installation.
- Hosted backend with stable uptime.
- Durable task and audit records.
- Clear user-facing failure messages.
- Rate limiting and abuse controls.
- Secret handling and operational monitoring.

## Public Usage Target

A repository maintainer should be able to use PatchPilot without cloning the code when hosted mode is available:

```text
Open PatchPilot repository or website
  -> Click Install GitHub App
  -> Select repositories
  -> Open a GitHub issue
  -> Comment /agent fix
  -> Review the generated Pull Request
```

For self-hosted usage, a developer should be able to run their own instance:

```text
Clone repository
  -> Create GitHub App
  -> Configure secrets
  -> Start backend and MySQL
  -> Set webhook URL
  -> Install app on repository
  -> Comment /agent fix
```


## User Experience Target

A maintainer should be able to:

1. Install the PatchPilot GitHub App on a repository.
2. Open or select an issue.
3. Comment `/agent fix` or apply a configured label.
4. Watch PatchPilot create a task.
5. Receive a Pull Request with:
   - A focused code change.
   - A summary of reasoning.
   - A list of changed files.
   - Test output.
   - Failure notes if verification did not pass.

If a comment is vague, non-actionable, malicious, or outside configured language support, the maintainer should see a clear ignored or rejected result instead of a risky task run.

## Frontend Target

The frontend target is a React operations dashboard for maintainers and demos. It should make the backend workflow visible instead of hiding the engineering work behind a generic chat screen.

Target screens:

- Task list.
- Task detail.
- Status timeline.
- Tool-call trace.
- Model-call summary.
- Test output.
- GitHub issue and Pull Request links.

## Backend Target

The backend should provide:

- GitHub App authentication.
- Webhook ingestion and verification.
- Durable task records.
- Async worker execution.
- Workspace isolation.
- Command and safety gating before task execution.
- Language and build-system detection through explicit adapters.
- Controlled agent tool calls.
- Maven, Gradle, Node.js, and Python test execution through allowlisted adapter commands.
- Pull Request automation.
- Issue comments for success and failure reporting.
- Audit logs for model calls and tool calls.
- Cost, latency, and success-rate observability.

## Safety Target

PatchPilot must:

- Never auto-merge a Pull Request.
- Never push directly to a default branch.
- Never execute arbitrary model-generated shell commands.
- Never read or write outside the task workspace.
- Never log secrets.
- Never report success without verification.
- Never run a task from an unauthorized user or repository.
- Reject or ignore unclear, unsupported, or unsafe requests before cloning or model execution.
- Enforce per-user, per-repository, and global rate limits before expensive work starts.
- Run repository commands with allowlisted command templates, timeouts, resource limits, and no unnecessary secrets.

## Engineering Target

The codebase should remain understandable to a Java backend interviewer:

- Clear package-by-domain structure.
- Thin controllers.
- Service interfaces and implementations.
- Explicit DTO, VO, BO, Query, Entity objects.
- Auditable tool calls.
- Tests for workflow decisions and unsafe boundaries.
- Local reproducible runtime.

## Resume Target

The final project should demonstrate:

- Java / Spring Boot backend development.
- GitHub App integration.
- Agent workflow design.
- Tool calling.
- Repository automation.
- Async job execution.
- Multi-language build/test adapter design.
- Test execution and result capture.
- Security boundaries around AI actions.
- Observability and auditability.

## Future Runtime Target

The long-term runtime may split into:

```text
patchpilot-api
  Webhook ingestion
  Task status APIs
  GitHub App installation APIs

patchpilot-worker
  Repository cloning
  Agent execution
  Test running
  Pull Request creation
```

This split should happen only after the single-process MVP proves the workflow.
