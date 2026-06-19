# Local Commit Workflow Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:test-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Create a controlled local git commit on the prepared task branch after patch generation and Maven verification succeed.

**Architecture:** Extend the existing git runner with allowlisted `git add --all` and `git commit -m <message>` operations, wrap them in an internal `CommitTool`, and wire the executor to commit only after tests pass. This phase remains local-only: it does not push branches, call GitHub APIs, create Pull Requests, or comment on issues.

**Tech Stack:** Java 17, Spring Boot 3.5.x, JUnit 5, AssertJ, existing workspace runner and task executor modules.

## Global Constraints

- Do not call model providers in this phase.
- Do not execute model-generated shell commands.
- Do not push branches or create Pull Requests.
- Git commands must be constructed by backend code only.
- Commit creation must happen only after Maven verification succeeds.
- Maven test failures must not create commits.

---

## Target Files

Create:

- `PatchPilot/src/main/java/io/patchpilot/backend/agent/tool/CommitTool.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/agent/tool/CommitToolTests.java`

Modify:

- `PatchPilot/src/main/java/io/patchpilot/backend/workspace/runner/GitCommandRunner.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/workspace/runner/GitCommandRunnerTests.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/executor/NoopFixTaskExecutor.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/task/executor/WorkspaceFixTaskExecutorTests.java`
- `docs/progress/execution-log.md`
- `docs/plans/010-local-commit-workflow.md`

## Task 1: Add Controlled Git Commit Operations

**Interfaces:**

- `GitCommandRunner#stageAll(Path repositoryDir)` returns `GitCommandResult`.
- `GitCommandRunner#commit(Path repositoryDir, String message)` returns `GitCommandResult`.
- `stageAll` uses `git -C <repo> add --all`.
- `commit` rejects blank commit messages and uses `git -C <repo> commit -m <message>`.

- [x] Write a failing test that `stageAll` stages a modified file in a temporary git repository.
- [x] Write a failing test that `commit` creates a commit with message `PatchPilot task task-123`.
- [x] Write a failing test that blank commit messages are rejected.
- [x] Run `mvn -pl PatchPilot -Dtest=GitCommandRunnerTests test` and verify it fails because the methods do not exist.
- [x] Add `stageAll` and `commit` to `GitCommandRunner`.
- [x] Run `mvn -pl PatchPilot -Dtest=GitCommandRunnerTests test` and verify it passes.

## Task 2: Add Commit Tool Boundary

**Interfaces:**

- `CommitTool#commitAll(Path repositoryDir, String message)` returns the git commit output.
- It calls `stageAll` before `commit`.
- It throws `IllegalStateException("git add failed: <output>")` when staging fails.
- It throws `IllegalStateException("git commit failed: <output>")` when commit fails.

- [x] Write a failing test that `CommitTool` stages before committing.
- [x] Write a failing test that staging failure stops commit creation.
- [x] Write a failing test that commit failure is surfaced clearly.
- [x] Run `mvn -pl PatchPilot -Dtest=CommitToolTests test` and verify it fails because `CommitTool` does not exist.
- [x] Add `CommitTool`.
- [x] Run `mvn -pl PatchPilot -Dtest=CommitToolTests test` and verify it passes.

## Task 3: Wire Commit After Maven Verification

**Interfaces:**

- `NoopFixTaskExecutor(WorkspaceService, MavenTestRunner, PatchWorkflow, DiffTool, CommitTool)`.
- Executor order: prepare repository -> apply patch workflow -> inspect diff -> run Maven tests -> commit all changes.
- Commit message: `PatchPilot task <taskId>`.

- [x] Update `WorkspaceFixTaskExecutorTests` with a recording commit tool fake.
- [x] Add a failing assertion that commit runs after Maven tests pass.
- [x] Add a failing assertion that commit is not called when Maven tests fail.
- [x] Run `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests test` and verify it fails because the executor is not wired.
- [x] Inject `CommitTool` into `NoopFixTaskExecutor`.
- [x] Call `commitAll(preparedWorkspace.repositoryDir(), "PatchPilot task " + task.id())` only after successful Maven tests.
- [x] Run `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests test` and verify it passes.

## Task 4: Validate And Document

- [x] Run `mvn -pl PatchPilot -Dtest=GitCommandRunnerTests,CommitToolTests,WorkspaceFixTaskExecutorTests test`.
- [x] Run `mvn -pl PatchPilot -Dtest=GitCommandRunnerTests,CommitToolTests,WorkspaceFixTaskExecutorTests,GitHubWebhookControllerTests test`.
- [x] Run `mvn test`.
- [x] Run `mvn clean package`.
- [x] Record validation evidence in `docs/progress/execution-log.md`.
- [x] Mark this plan complete.

## Acceptance Checklist

- [x] Successful tasks create a local git commit on the prepared task branch.
- [x] Commit creation happens after Maven verification, not before.
- [x] Maven test failures do not create commits.
- [x] Git staging and commit failures produce clear task failure reasons.
- [x] Full Maven verification passes.
