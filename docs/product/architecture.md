# Architecture

## Architectural Style

PatchPilot starts as a modular Spring Boot backend. The first goal is to prove a reliable GitHub issue-to-PR workflow, not to introduce premature microservices.

The MVP should remain a single deployable backend with clear internal boundaries:

```text
GitHub Webhook
      |
PatchPilot API
      |
Fix Task Service
      |
Async Worker
      |
Workspace + Agent + Tools + Test Runner
      |
GitHub Pull Request
```

The system can later split API and worker processes once task execution volume, sandbox isolation, or deployment needs justify it.

## Frontend Role

PatchPilot should use a React frontend for operational visibility and demos. The frontend is not required for the first webhook-to-PR backend loop, but it becomes useful once task records and traces exist.

Recommended first screens:

- Task list.
- Task detail.
- GitHub issue and PR links.
- Status timeline.
- Tool-call trace.
- Model-call summary.
- Maven test output.

The frontend should stay work-focused and dense enough for repeated debugging. It should not start as a marketing landing page.

## Runtime Flow

```text
GitHub issue comment created
  -> WebhookController verifies signature
  -> WebhookEventRouter detects /agent fix
  -> CommandSafetyGate rejects unsafe, unsupported, or unauthorized commands
  -> FixTaskService creates a task
  -> TaskWorker runs asynchronously
  -> WorkspaceService clones the repository
  -> IssueAnalyzer gathers issue and repository context
  -> FixIssueAgent creates a fix plan
  -> Tools search code, read files, write files, and inspect diff
  -> LanguageAdapter selects an allowlisted verification command
  -> TestRunner runs verification
  -> GitHubClient pushes a branch and opens a Pull Request
  -> GitHubClient comments back on the issue
  -> FixTaskService marks the task completed or failed
```

## Backend Modules

Recommended package layout:

```text
io.patchpilot.backend
  common
    config
    error
    response
    time
  github
    auth
    client
    webhook
  task
    controller
    service
      impl
    mapper
    domain
      entity
      dto
      vo
      bo
      query
      enums
    convert
  workspace
    service
      impl
    domain
  agent
    workflow
    prompt
    tool
    domain
  runner
    service
    domain
  audit
    service
    domain
  observability
```

Package-by-domain is preferred. Layers are created inside each domain only when needed.

## Module Responsibilities

### GitHub Webhook

Responsibilities:

- Receive GitHub webhook requests.
- Verify `X-Hub-Signature-256`.
- Route supported events.
- Detect `/agent fix`.
- Reject unsafe or unauthorized `/agent fix` commands before task creation.
- Deduplicate delivery ids.
- Submit work to task services.

The webhook layer must not run model calls, clone repositories, or execute tests.

### GitHub Client

Responsibilities:

- Generate GitHub App JWTs.
- Fetch and cache installation tokens.
- Read issue metadata.
- Create issue comments.
- Create branches, commits, and Pull Requests through controlled operations.
- Normalize GitHub API errors.

This layer must not contain agent reasoning logic.

### Task

Responsibilities:

- Create fix tasks.
- Store task status and result.
- Manage status transitions.
- Expose task status APIs.
- Coordinate async execution.

Recommended status values:

```text
PENDING
PREPARING_WORKSPACE
ANALYZING_ISSUE
PLANNING_FIX
APPLYING_PATCH
RUNNING_TESTS
CREATING_PR
COMPLETED
FAILED
CANCELLED
```

Status changes should go through a dedicated service instead of direct field mutation across modules.

### Workspace

Responsibilities:

- Create task-scoped workspaces.
- Clone repositories.
- Checkout base branches.
- Create patch branches.
- Read and write files safely.
- Produce git diff summaries.
- Clean up local workspaces when configured.

All file operations must be restricted to the task workspace.

### Agent

Responsibilities:

- Convert issue and repository context into model inputs.
- Produce a structured fix plan.
- Decide which registered tools to call.
- Interpret tool outputs.
- Produce a patch proposal, PR summary, or failure summary.

The agent must not directly access GitHub APIs, the database, or the file system.

### Tools

MVP tools:

- `RepoTreeTool`
- `CodeSearchTool`
- `FileReadTool`
- `FileWriteTool`
- `DiffTool`
- `MavenTestTool`
- `GitTool`
- `GitHubPullRequestTool`
- `GitHubCommentTool`

Tools are the only way for agent reasoning to affect the external world.

### Runner

Responsibilities:

- Use language adapters to detect supported build systems.
- Execute allowed verification commands.
- Capture test output.
- Enforce timeouts.
- Return structured test results.

The first adapter is `JavaMavenLanguageAdapter`. MVP supported commands:

```bash
./mvnw test
mvn test
```

Future adapters should add their own detection and allowlisted verification commands without allowing arbitrary user-supplied shell.

## Data Model

The first durable model should include:

### FixTask

Represents a single issue-to-PR attempt.

Important fields:

- `id`
- `repositoryOwner`
- `repositoryName`
- `issueNumber`
- `installationId`
- `triggerUser`
- `triggerCommentId`
- `status`
- `failureReason`
- `createdAt`
- `startedAt`
- `finishedAt`
- `pullRequestUrl`

### ToolCallRecord

Represents an audited tool invocation.

Important fields:

- `id`
- `taskId`
- `toolName`
- `inputSummary`
- `outputSummary`
- `success`
- `durationMs`
- `createdAt`

### ModelCallRecord

Represents an audited model invocation.

Important fields:

- `id`
- `taskId`
- `model`
- `promptSummary`
- `outputSummary`
- `inputTokens`
- `outputTokens`
- `durationMs`
- `success`
- `createdAt`

### TestRunRecord

Represents a test command result.

Important fields:

- `id`
- `taskId`
- `command`
- `exitCode`
- `stdoutSnippet`
- `stderrSnippet`
- `durationMs`
- `createdAt`

## Technology Choices

### Current Baseline

- Java 17
- Spring Boot 3.5.15
- Maven

### Recommended MVP Additions

- Spring Web
- Spring Validation
- Spring Boot Actuator
- MySQL for early durable persistence
- JUnit 5
- Mockito
- GitHub REST client wrapper
- OpenAI Java SDK or Spring AI

### Model Integration Choice

The MVP can start with a direct OpenAI-compatible Java client and a lightweight in-house workflow. This keeps the first implementation understandable and resume-friendly.

Spring AI can be introduced later when the project needs richer tool abstractions, RAG, vector stores, model portability, and observation integration.

## Security Boundaries

- Verify all GitHub webhooks.
- Do not log GitHub private keys, installation tokens, or model API keys.
- Keep secrets in environment-specific configuration.
- Restrict file operations to the task workspace.
- Restrict command execution to an allowlist.
- Do not let the model create arbitrary shell commands.
- Do not auto-merge Pull Requests.

## Observability

Each task should record:

- Status transitions.
- GitHub repository and issue number.
- Trigger user.
- Model name and token usage.
- Tool call count and summaries.
- Changed files.
- Test command and result.
- Created PR URL.
- Failure reason.

## Future Split Path

The first useful runtime split is:

```text
patchpilot-api      webhook and status APIs
patchpilot-worker   repository clone, agent execution, testing, PR creation
```

Do not split earlier unless there is a concrete need for independent scaling, stricter sandboxing, or separate deployment.
