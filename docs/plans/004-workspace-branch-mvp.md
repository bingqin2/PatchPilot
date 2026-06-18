# Workspace Branch MVP Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Extend workspace preparation so each `/agent fix` task clones the repository and creates a controlled task branch.

**Architecture:** Keep task orchestration in `task.executor`, workspace preparation in `workspace.service`, and git side effects in `workspace.runner`. This phase creates a local branch only; it does not edit files, run tests, push branches, or create Pull Requests.

**Tech Stack:** Java 17, Spring Boot 3.5.x, JUnit 5, Spring Boot Test, `ProcessBuilder`, Docker Compose.

## Global Constraints

- Keep the backend as one Spring Boot module under `PatchPilot/`.
- Follow package-by-domain structure from `docs/product/backend-code-standard.md`.
- Do not call model providers, edit repository files, run Maven commands, push branches, or create Pull Requests in this phase.
- All filesystem operations must stay under `patchpilot.workspace.root-dir`.
- Git commands must be constructed by backend code only; never execute model-generated shell strings.
- Branch names must be derived from backend task ids, not user-controlled issue text or comments.
- Do not log or expose `PATCHPILOT_GITHUB_TOKEN`.

---

## Target Files

Create:

- `PatchPilot/src/main/java/io/patchpilot/backend/workspace/domain/vo/PreparedWorkspaceResult.java`

Modify:

- `PatchPilot/src/main/java/io/patchpilot/backend/workspace/service/WorkspaceService.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/workspace/service/impl/GitWorkspaceService.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/workspace/runner/GitCommandRunner.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/executor/NoopFixTaskExecutor.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/workspace/service/GitWorkspaceServiceTests.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/workspace/runner/GitCommandRunnerTests.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/task/executor/WorkspaceFixTaskExecutorTests.java`
- `docs/progress/execution-log.md`
- `docs/plans/004-workspace-branch-mvp.md`

## Implementation Tasks

### Task 1: Add Workspace Preparation Contract

**Files:**

- Create: `PatchPilot/src/main/java/io/patchpilot/backend/workspace/domain/vo/PreparedWorkspaceResult.java`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/workspace/service/WorkspaceService.java`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/workspace/service/impl/GitWorkspaceService.java`
- Modify: `PatchPilot/src/test/java/io/patchpilot/backend/workspace/service/GitWorkspaceServiceTests.java`

**Interfaces:**

- `PreparedWorkspaceResult(String taskId, Path workspaceDir, Path repositoryDir, String branchName)`.
- `WorkspaceService#prepareRepository(CloneWorkspaceCommand command)` returns `PreparedWorkspaceResult`.
- Branch name format is `patchpilot/{taskId}`.

- [x] **Step 1: Write failing workspace preparation tests**

Add tests that verify `prepareRepository` clones the repository, creates branch `patchpilot/{taskId}`, returns branch metadata, and throws when branch creation exits non-zero.

- [x] **Step 2: Run test to verify failure**

Run:

```bash
JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home PATH=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home/bin:$PATH mvn -pl PatchPilot -Dtest=GitWorkspaceServiceTests test
```

Expected: compilation failure because `prepareRepository` and `PreparedWorkspaceResult` do not exist.

- [x] **Step 3: Implement minimal preparation contract**

Add `PreparedWorkspaceResult`, add `prepareRepository` to `WorkspaceService`, and implement it in `GitWorkspaceService` by reusing clone logic and then calling `GitCommandRunner#createBranch(repositoryDir, branchName)`.

- [x] **Step 4: Run test to verify pass**

Run the same targeted Maven command. Expected: `BUILD SUCCESS`.

### Task 2: Add Controlled Branch Git Runner

**Files:**

- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/workspace/runner/GitCommandRunner.java`
- Modify: `PatchPilot/src/test/java/io/patchpilot/backend/workspace/runner/GitCommandRunnerTests.java`

**Interfaces:**

- `GitCommandRunner#createBranch(Path repositoryDir, String branchName)` returns `GitCommandResult`.
- The command uses fixed arguments: `git -C <repositoryDir> checkout -b <branchName>`.
- Blank branch names are rejected before process execution.

- [x] **Step 1: Write failing branch runner tests**

Add tests using a temporary git repository to verify `createBranch` creates the requested branch and rejects blank branch names.

- [x] **Step 2: Run test to verify failure**

Run:

```bash
JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home PATH=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home/bin:$PATH mvn -pl PatchPilot -Dtest=GitCommandRunnerTests test
```

Expected: compilation failure because `createBranch` does not exist.

- [x] **Step 3: Implement branch runner**

Add `createBranch` to `GitCommandRunner`, reuse timeout/output sanitization behavior, and reject blank branch names with `IllegalArgumentException("Branch name must not be blank")`.

- [x] **Step 4: Run test to verify pass**

Run the same targeted Maven command. Expected: `BUILD SUCCESS`.

### Task 3: Wire Executor To Prepared Workspace

**Files:**

- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/executor/NoopFixTaskExecutor.java`
- Modify: `PatchPilot/src/test/java/io/patchpilot/backend/task/executor/WorkspaceFixTaskExecutorTests.java`

**Interfaces:**

- `NoopFixTaskExecutor#execute(FixTaskVo task)` calls `workspaceService.prepareRepository(...)`.

- [x] **Step 1: Write failing executor test**

Update the recording workspace service test so it fails unless `prepareRepository` is called with task id, repository owner, and repository name.

- [x] **Step 2: Run test to verify failure**

Run:

```bash
JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home PATH=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home/bin:$PATH mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests test
```

Expected: failure because the executor still calls `cloneRepository`.

- [x] **Step 3: Implement executor preparation call**

Change `execute` to call `workspaceService.prepareRepository(...)`.

- [x] **Step 4: Run test to verify pass**

Run the same targeted Maven command. Expected: `BUILD SUCCESS`.

### Task 4: Validate And Runtime Check

**Files:**

- Modify: `docs/progress/execution-log.md`
- Modify: `docs/plans/004-workspace-branch-mvp.md`

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
PATCHPILOT_GITHUB_WEBHOOK_SECRET=test-secret-123 docker compose up --build -d
```

Expected: backend container is running on port `8080`.

- [x] **Step 4: Verify runtime behavior**

Trigger a signed local webhook for public repository `octocat/Hello-World`. Expected: task reaches `COMPLETED`; the task workspace contains `repo/.git`, and `git -C <repo> branch --show-current` returns `patchpilot/{taskId}`.

- [x] **Step 5: Update docs**

Record validation evidence in `docs/progress/execution-log.md` and mark completed checkboxes in this plan.

## Acceptance Checklist

- [x] Workspace preparation clones the repository and creates `patchpilot/{taskId}`.
- [x] Branch creation uses controlled `git -C <repo> checkout -b <branch>` arguments.
- [x] Blank branch names are rejected before process execution.
- [x] Executor invokes workspace preparation for webhook-created tasks.
- [x] Branch creation failure marks the task `FAILED` through the existing dispatcher.
- [x] Root `mvn test` passes.
- [x] Root `mvn clean package` passes.
- [x] Docker backend rebuilds and starts.
