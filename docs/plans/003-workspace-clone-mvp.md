# Workspace Clone MVP Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the no-op task executor with a minimal workspace clone executor that clones the target GitHub repository into a task-scoped local directory.

**Architecture:** Keep task orchestration in `task.executor`, and isolate filesystem/git side effects in a `workspace` module. `WorkspaceService` owns safe workspace paths and clone requests; `GitCommandRunner` owns the allowlisted `git clone` process execution. This phase only clones repositories and leaves patching, tests, commits, pushes, and Pull Requests for later plans.

**Tech Stack:** Java 17, Spring Boot 3.5.x, JUnit 5, Spring Boot Test, `ProcessBuilder`, Docker Compose.

## Global Constraints

- Keep the backend as one Spring Boot module under `PatchPilot/`.
- Follow package-by-domain structure from `docs/product/backend-code-standard.md`.
- Do not call model providers, edit repository files, run Maven commands, push branches, or create Pull Requests in this phase.
- All filesystem operations must stay under `patchpilot.workspace.root-dir`.
- Git commands must be constructed by backend code only; never execute model-generated shell strings.
- Use `PATCHPILOT_GITHUB_TOKEN` as an optional token source for private repositories and repository webhook mode.
- Do not log or expose the token.

---

## Target Files

Create:

- `PatchPilot/src/main/java/io/patchpilot/backend/workspace/config/WorkspaceProperties.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/workspace/domain/bo/CloneWorkspaceCommand.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/workspace/domain/vo/WorkspaceCloneResult.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/workspace/service/WorkspaceService.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/workspace/service/impl/GitWorkspaceService.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/workspace/runner/GitCommandRunner.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/workspace/runner/GitCommandResult.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/github/config/GitHubProperties.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/workspace/service/GitWorkspaceServiceTests.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/workspace/runner/GitCommandRunnerTests.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/task/executor/WorkspaceFixTaskExecutorTests.java`

Modify:

- `PatchPilot/src/main/java/io/patchpilot/backend/PatchPilotApplication.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/executor/NoopFixTaskExecutor.java`
- `PatchPilot/src/main/resources/application.properties`
- `PatchPilot/src/test/java/io/patchpilot/backend/github/webhook/GitHubWebhookControllerTests.java`
- `PatchPilot/Dockerfile`
- `docker-compose.yml`
- `docs/progress/execution-log.md`
- `docs/plans/003-workspace-clone-mvp.md`

## Implementation Tasks

### Task 1: Add Workspace Clone Service Contract

**Files:**

- Create: `PatchPilot/src/main/java/io/patchpilot/backend/workspace/config/WorkspaceProperties.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/workspace/domain/bo/CloneWorkspaceCommand.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/workspace/domain/vo/WorkspaceCloneResult.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/workspace/service/WorkspaceService.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/workspace/service/impl/GitWorkspaceService.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/workspace/runner/GitCommandRunner.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/workspace/runner/GitCommandResult.java`
- Create: `PatchPilot/src/test/java/io/patchpilot/backend/workspace/service/GitWorkspaceServiceTests.java`
- Modify: `PatchPilot/src/main/resources/application.properties`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/PatchPilotApplication.java`

**Interfaces:**

- `WorkspaceProperties` uses prefix `patchpilot.workspace` and has `Path rootDir` defaulting to `/tmp/patchpilot/workspaces`.
- `CloneWorkspaceCommand(String taskId, String repositoryOwner, String repositoryName)`.
- `WorkspaceCloneResult(String taskId, Path workspaceDir, Path repositoryDir)`.
- `WorkspaceService#cloneRepository(CloneWorkspaceCommand command)` returns `WorkspaceCloneResult`.
- `GitCommandRunner#cloneRepository(String repositoryUrl, Path targetDir)` returns `GitCommandResult`.

- [x] **Step 1: Write failing workspace service tests**

Add tests that verify `GitWorkspaceService` creates a task workspace under the configured root, invokes `GitCommandRunner` with `https://github.com/{owner}/{repo}.git`, returns the target repository directory, rejects path traversal task ids, and throws when git clone exits non-zero.

- [x] **Step 2: Run tests to verify failure**

Run:

```bash
JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home PATH=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home/bin:$PATH mvn -pl PatchPilot -Dtest=GitWorkspaceServiceTests test
```

Expected: compilation failure because workspace classes do not exist.

- [x] **Step 3: Implement minimal workspace service**

Implement the records, service interface, properties class, runner interface/result, and `GitWorkspaceService`. Enable configuration properties in `PatchPilotApplication` and add:

```properties
patchpilot.workspace.root-dir=${PATCHPILOT_WORKSPACE_ROOT_DIR:/tmp/patchpilot/workspaces}
```

- [x] **Step 4: Run tests to verify pass**

Run the same targeted Maven command. Expected: `BUILD SUCCESS`.

### Task 2: Replace Noop Executor With Workspace Clone Executor

**Files:**

- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/executor/NoopFixTaskExecutor.java`
- Create: `PatchPilot/src/test/java/io/patchpilot/backend/task/executor/WorkspaceFixTaskExecutorTests.java`

**Interfaces:**

- Keep class name `NoopFixTaskExecutor` only if not yet renamed; its behavior becomes workspace clone execution.
- Constructor consumes `WorkspaceService`.
- `execute(FixTaskVo task)` calls `workspaceService.cloneRepository(...)`.

- [x] **Step 1: Write failing executor test**

Add a unit test with a recording `WorkspaceService` that verifies `execute` passes task id, repository owner, and repository name to `cloneRepository`.

- [x] **Step 2: Run test to verify failure**

Run:

```bash
JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home PATH=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home/bin:$PATH mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests test
```

Expected: failure because the executor still does not call `WorkspaceService`.

- [x] **Step 3: Implement executor clone call**

Inject `WorkspaceService` into the executor and call it from `execute`.

- [x] **Step 4: Run tests to verify pass**

Run the same targeted Maven command. Expected: `BUILD SUCCESS`.

### Task 3: Add Real Git Runner and Docker Configuration

**Files:**

- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/workspace/runner/GitCommandRunner.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/github/config/GitHubProperties.java`
- Create: `PatchPilot/src/test/java/io/patchpilot/backend/workspace/runner/GitCommandRunnerTests.java`
- Modify: `PatchPilot/src/main/resources/application.properties`
- Modify: `PatchPilot/Dockerfile`
- Modify: `docker-compose.yml`

**Interfaces:**

- `GitCommandRunner#cloneRepository` runs `git clone --depth 1 <repositoryUrl> <targetDir>`.
- If `PATCHPILOT_GITHUB_TOKEN` is configured, authenticated HTTPS clone URL is used internally without exposing the token in results.
- `docker-compose.yml` passes `PATCHPILOT_GITHUB_TOKEN` and `PATCHPILOT_WORKSPACE_ROOT_DIR`.
- The runtime Docker image installs `git` and `ca-certificates`.

- [x] **Step 1: Implement real runner**

Replace the runner interface with a Spring service class, keeping the same `cloneRepository` method signature. Use `ProcessBuilder` with a fixed argument list, capture combined output, and return exit code/output.

- [x] **Step 2: Update configuration**

Add:

```properties
patchpilot.github.token=${PATCHPILOT_GITHUB_TOKEN:}
```

and compose environment entries for `PATCHPILOT_GITHUB_TOKEN` and `PATCHPILOT_WORKSPACE_ROOT_DIR`. Update the runtime Docker image to install `git` and `ca-certificates`.

- [x] **Step 3: Run workspace tests**

Run:

```bash
JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home PATH=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home/bin:$PATH mvn -pl PatchPilot -Dtest=GitWorkspaceServiceTests,WorkspaceFixTaskExecutorTests test
```

Expected: `BUILD SUCCESS`.

### Task 4: Validate, Package, and Runtime Check

**Files:**

- Modify: `docs/progress/execution-log.md`
- Modify: `docs/plans/003-workspace-clone-mvp.md`

- [x] **Step 1: Run all tests**

Run:

```bash
JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home PATH=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home/bin:$PATH mvn test
```

Expected: `BUILD SUCCESS`.

- [x] **Step 2: Run package**

Run:

```bash
JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home PATH=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home/bin:$PATH mvn clean package
```

Expected: `BUILD SUCCESS`.

- [x] **Step 3: Rebuild and restart Docker backend**

Run:

```bash
PATCHPILOT_GITHUB_WEBHOOK_SECRET=test-secret-123 PATCHPILOT_GITHUB_TOKEN=<token> docker compose up --build -d
```

Expected: backend container is running on port `8080`.

- [x] **Step 4: Verify runtime behavior**

Trigger `/agent fix` from GitHub or signed local webhook. Expected task result:

- Public repository clone succeeds and task reaches `COMPLETED`.
- Private repository clone requires `PATCHPILOT_GITHUB_TOKEN`; without it, task reaches `FAILED` with a clone failure reason.

- [x] **Step 5: Update docs**

Record validation evidence in `docs/progress/execution-log.md` and mark completed checkboxes in this plan.

## Acceptance Checklist

- [x] Workspace paths are scoped under `patchpilot.workspace.root-dir`.
- [x] Path traversal task ids are rejected.
- [x] Workspace clone command uses controlled `git clone --depth 1` arguments.
- [x] Executor invokes workspace clone for webhook-created tasks.
- [x] Clone failure marks the task `FAILED` through the existing dispatcher.
- [x] Root `mvn test` passes.
- [x] Root `mvn clean package` passes.
- [x] Docker backend rebuilds and starts.
