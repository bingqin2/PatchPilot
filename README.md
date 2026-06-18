# PatchPilot

PatchPilot is an AI GitHub issue-to-PR agent backend. It turns a GitHub Issue into a controlled software maintenance workflow: analyze the issue, inspect the repository, generate a patch, run tests, and open a Pull Request for human review.

PatchPilot is not a general chatbot. It is a backend system that uses agent reasoning through explicit tools, persistent tasks, test execution, and auditable logs.

## Product Goal

```text
Comment on a GitHub issue, let PatchPilot analyze the repository, generate a tested patch, and open a reviewable Pull Request.
```

The MVP focuses on Java/Maven repositories and the `/agent fix` GitHub issue comment trigger.

## Current Repository Layout

```text
.
├── PatchPilot/        # Spring Boot backend project
├── docs/              # Product, architecture, plans, progress, and agent governance
└── frontend/          # React frontend for task monitoring and demos
```

Important documents:

- `docs/product/spec.md` - Product requirements and MVP scope.
- `docs/product/architecture.md` - Backend architecture and module boundaries.
- `docs/product/backend-code-standard.md` - Java backend code standard.
- `docs/product/roadmap.md` - Phase-based implementation roadmap.
- `docs/plans/000-project-foundation.md` - First execution plan.
- `docs/progress/decisions.md` - Project decision log.

## MVP Workflow

```text
GitHub issue comment created
  -> Webhook verifies signature
  -> Router detects /agent fix
  -> Backend creates a fix task
  -> Worker clones the repository
  -> Agent analyzes issue and code context
  -> Tools search, read, and edit files
  -> Test runner executes Maven tests
  -> Backend pushes a patch branch
  -> Backend opens a Pull Request
  -> Backend comments on the issue
```

## Recommended Technology Stack

### Frontend

- React for the web UI.
- Vite as the recommended build tool.
- TypeScript for maintainable UI code.
- React Router for navigation.
- TanStack Query for backend API state.
- A lightweight component system can be chosen later when UI scope is clearer.

The frontend should start as a small operational dashboard, not a marketing site. The first useful screens are task list, task detail, tool-call trace, test output, and PR result.

### Backend Core

- Java 17 for the current baseline. Java 21 is a reasonable later upgrade.
- Spring Boot 3.5.x.
- Maven.
- Spring Web for REST and webhook endpoints.
- Spring Validation for request validation.
- Spring Boot Actuator for health and operational endpoints.

### Persistence And Queueing

Recommended MVP path:

- Start with MySQL for durable task history.
- Add Flyway for database migrations.
- Use application-level async execution first.
- Add Redis later for rate limiting, token caching, and lightweight task coordination.

Alternative early prototype path:

- Use H2 only for narrow tests if needed; the project baseline should use MySQL for local development and demos.

### Data Access

Recommended project standard:

- MyBatis-Plus for persistence.
- `*Entity`, `*Dto`, `*Vo`, `*Bo`, and `*Query` object separation.
- Service interfaces with `service.impl` implementations.

Reason: this matches the code standard in `docs/product/backend-code-standard.md` and keeps the project closer to Java backend interview expectations.

### GitHub Integration

- GitHub App for repository installation and permission control.
- GitHub webhooks for issue comment triggers.
- GitHub REST API for issue comments, branches, commits, and Pull Requests.
- Webhook signature verification with `X-Hub-Signature-256`.

### Agent And Model Integration

Recommended MVP path:

- Use an OpenAI-compatible Java client first.
- Keep the agent workflow explicit in Java services.
- Implement tools as typed Java components.
- Add Spring AI later if RAG, vector stores, or model portability become central.

Alternative path:

- Use Spring AI from the start for ChatClient, tools, structured output, and future RAG integration.

Decision still open: direct OpenAI-compatible client vs Spring AI.

### Repository Execution

MVP:

- Local task workspace under the backend runtime directory.
- Git CLI for clone, branch, diff, commit, and push.
- Maven test runner with command allowlists.

Hardening phase:

- Docker sandbox for repository execution.
- Command timeouts and resource limits.
- Workspace cleanup policy.

### Testing

- JUnit 5.
- Mockito.
- Spring Boot Test.
- Testcontainers when MySQL or GitHub-like integration tests are introduced.

## Non-Goals For MVP

The MVP does not:

- Automatically merge Pull Requests.
- Push directly to the default branch.
- Support every language or build system.
- Execute arbitrary model-generated shell commands.
- Require a browser extension.
- Claim success without running verification.

## Local Development

Backend project:

```bash
cd PatchPilot
./mvnw test
./mvnw spring-boot:run
```

If the Maven wrapper is unavailable or not executable:

```bash
cd PatchPilot
mvn test
mvn spring-boot:run
```

## Documentation Workflow

Long-running work should start with an execution plan under:

```text
docs/plans/
```

Progress and decisions should be recorded in:

```text
docs/progress/execution-log.md
docs/progress/decisions.md
```

## Open Technology Decisions

The main decisions to settle before implementation are:

1. Model integration: OpenAI-compatible Java client first, or Spring AI from the start.
2. Persistence: MySQL as the baseline; H2 only for narrow tests if it reduces setup cost.
3. Data access: MyBatis-Plus immediately, or defer persistence until the task lifecycle is clear.
4. Execution isolation: local workspace first, or Docker sandbox in the MVP.
5. Trigger scope: `/agent fix` only, or also support `ai-fix` labels in the first version.

## Suggested MVP Choices

My recommended first version is:

```text
Java 17
Spring Boot 3.5.x
Maven
Spring Web
Spring Validation
Spring Boot Actuator
MySQL
Flyway
MyBatis-Plus
React + Vite + TypeScript
OpenAI-compatible Java client
GitHub App + GitHub REST API
Application-level async worker
Local workspace execution
Maven test runner
JUnit 5 + Mockito + Spring Boot Test
```

This keeps the first version backend-heavy, interview-friendly, and implementable without turning the project into infrastructure work too early.
