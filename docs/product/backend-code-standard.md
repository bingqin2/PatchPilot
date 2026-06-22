# Backend Code Standard

This document defines the backend code structure for PatchPilot. New backend code should follow this standard first, and deviations should be recorded in `docs/progress/decisions.md` or the relevant execution plan.

## Goals

- Keep controllers thin and focused on HTTP concerns.
- Keep business state transitions inside services.
- Use explicit domain objects for API input, API output, internal business data, queries, and persistence.
- Keep agent reasoning separate from tools that change external state.
- Make GitHub, file-system, command execution, and model operations auditable.
- Avoid demo-script style code in production paths.

## Target Module Layout

Backend code is package-by-domain first. Inside each domain, use layered packages only when needed:

```text
io.patchpilot.backend.<module>
  controller
    XxxController.java
  service
    XxxService.java
    impl
      XxxServiceImpl.java
  mapper
    XxxMapper.java
  domain
    entity
      XxxEntity.java
    dto
      XxxDto.java
    vo
      XxxVo.java
    bo
      XxxBo.java
    query
      XxxQuery.java
    enums
      XxxStatus.java
  convert
    XxxConvert.java
  provider
    XxxProviderClient.java
    impl
      XxxProviderClientImpl.java
  config
    XxxProperties.java
```

Small modules should create only the packages they need.

## Layer Responsibilities

### Controller

Controllers own HTTP concerns only:

- Route mapping.
- Request body, path variable, query parameter, and header extraction.
- Bean Validation entry points such as `@Valid`.
- Calling service interfaces.
- Returning `Vo` or response wrapper objects.

Controllers must not:

- Call mappers directly.
- Execute Git commands.
- Read or write repository files.
- Call model providers.
- Run Maven or Gradle commands.
- Contain business state transitions.

### Service Interface

`service/XxxService.java` defines the business contract used by controllers and other modules.

Rules:

- Controllers depend on service interfaces, not implementation classes.
- Service methods should describe business capabilities instead of raw CRUD operations.
- Cross-module behavior should be exposed through narrow service methods.

### Service Implementation

`service/impl/XxxServiceImpl.java` contains business orchestration.

Rules:

- Annotate implementations with `@Service`.
- Use constructor injection with `private final` dependencies.
- Own transactions for operations that update durable state.
- Convert `Dto` input into `Bo` or persistence objects before writes.
- Return `Vo` or command results, not entities.

### Mapper

If MyBatis-Plus is adopted, mapper interfaces should use `BaseMapper<XxxEntity>` and be annotated with `@Mapper`.

Rules:

- Mappers must not be called from controllers.
- Cross-module mapper access is forbidden.
- Custom SQL should live in mapper methods or XML only when it is clearer than wrappers.

If JPA is temporarily used in an early prototype, that decision must be recorded and later migrated or confirmed before the project becomes resume-ready.

### Provider / Adapter

Provider packages contain external integration contracts and implementations.

Examples:

```text
github/client/GitHubClient.java
github/client/impl/GitHubRestClient.java
agent/provider/ModelProviderClient.java
agent/provider/impl/OpenAiCompatibleModelClient.java
```

Rules:

- Services depend on provider interfaces.
- Provider implementations hide vendor-specific request/response details.
- Provider exceptions should be normalized before crossing into service code.

### Agent Workflow

Agent workflow classes organize model reasoning.

Rules:

- The agent may call only registered tools.
- The agent must not access files, databases, GitHub APIs, or shell commands directly.
- The agent should prefer structured outputs over free-form text.
- The agent should fail explicitly when it cannot produce a reliable fix plan.

### Tools

Tools encapsulate controlled side effects.

Rules:

- Each tool has a clear input type and output type.
- Each tool validates input before executing.
- Each tool records an audit entry.
- High-risk tools must enforce allowlists.
- Tools must not call the model.

## Domain Objects

Use domain subpackages to make object intent explicit:

| Package | Suffix | Purpose |
| --- | --- | --- |
| `domain.entity` | `*Entity.java` | Database table mapping object. |
| `domain.dto` | `*Dto.java` | API request or external transfer object. |
| `domain.vo` | `*Vo.java` | API response object. |
| `domain.bo` | `*Bo.java` | Internal business object used inside service workflows. |
| `domain.query` | `*Query.java` | Query, filter, and pagination input object. |
| `domain.enums` | descriptive enum name | Business enum owned by the module. |

Avoid vague names such as `Request`, `Response`, `Model`, or `Info`.

Preferred examples:

```text
CreateFixTaskDto
FixTaskDetailVo
FixTaskExecutionBo
FixTaskQuery
FixTaskStatus
CreatePullRequestDto
ToolCallRecordEntity
```

## Naming Rules

- Use `PatchPilot` only for application-level names.
- Use domain-specific names for classes.
- Use `*Service` for business contracts.
- Use `*ServiceImpl` for concrete service classes.
- Use `*Client` for external API contracts.
- Use `*Adapter` or vendor-specific names for external implementations.
- Use `*Tool` for agent-callable tools.
- Use `*Runner` for command execution components.
- Use `*Properties` for configuration classes.

## Configuration Rules

All PatchPilot configuration uses the `patchpilot` prefix:

```yaml
patchpilot:
  github:
    app-id:
    private-key:
    webhook-secret:
  workspace:
    root-dir:
    cleanup-enabled: true
  agent:
    provider: openai
    model:
    max-tool-calls: 20
  runner:
    command-timeout-seconds: 300
```

Secrets must come from environment-specific configuration and must not be committed.

## Logging Rules

Logs should include:

- `taskId`
- repository full name
- issue number
- status transition
- tool name
- duration
- failure reason

Logs must not include:

- GitHub private keys
- installation tokens
- model API keys
- raw secrets from repository files
- full prompts containing sensitive content

## Command Execution Rules

MVP allowed commands:

```bash
./mvnw test
mvn test
./gradlew test
gradle test
npm test
python3 -m pytest
git status
git diff
git checkout -b <branch>
git add <files>
git commit -m <message>
git push <remote> <branch>
```

Disallowed:

```bash
rm -rf
curl | sh
arbitrary shell generated by the model
commands outside the task workspace
```

## File-System Rules

- All repository file reads and writes must stay inside the task workspace.
- Tool input must not accept absolute file paths.
- `..` path traversal must be rejected.
- Changed files must be recorded.

## Testing Rules

New code should include tests for:

- Webhook signature verification.
- Webhook trigger detection.
- Task status transitions.
- Workspace path guards.
- Tool input validation.
- GitHub client error normalization.
- Maven test result parsing.
- Agent structured output parsing.

Test names should describe behavior:

```text
should_create_task_when_issue_comment_contains_agent_fix
should_reject_file_read_outside_workspace
should_mark_task_failed_when_github_api_returns_403
```

## Review Checklist

Before merging meaningful backend work, check:

- Controllers stay thin.
- Services own state transitions.
- The agent does not bypass tools.
- Tools validate inputs and record audit entries.
- No secrets are logged.
- File operations are workspace-safe.
- Commands use allowlists.
- PR creation is human-review only.
- Tests cover success and failure paths.
