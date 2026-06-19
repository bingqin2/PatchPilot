# Remote Branch Push Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:test-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Push the prepared task branch to the GitHub remote after patch generation, Maven verification, and local commit succeed.

**Architecture:** Extend the existing git runner with a controlled `git push origin HEAD:<branch>` operation, wrap it in an internal `PushTool`, and wire task execution to push only after a successful local commit. This phase does not call GitHub REST APIs, create Pull Requests, or comment on issues.

**Tech Stack:** Java 17, Spring Boot 3.5.x, JUnit 5, AssertJ, existing workspace runner and task executor modules.

## Global Constraints

- Do not call model providers in this phase.
- Do not execute model-generated shell commands.
- Do not create Pull Requests or issue comments.
- Push only the prepared task branch returned by `WorkspaceService#prepareRepository(...)`.
- Git commands must be constructed by backend code only.
- Push must happen only after Maven verification and local commit succeed.
- Maven test failures and commit failures must not push.

---

## Target Files

Create:

- `PatchPilot/src/main/java/io/patchpilot/backend/agent/tool/PushTool.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/agent/tool/PushToolTests.java`

Modify:

- `PatchPilot/src/main/java/io/patchpilot/backend/workspace/runner/GitCommandRunner.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/workspace/runner/GitCommandRunnerTests.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/executor/NoopFixTaskExecutor.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/task/executor/WorkspaceFixTaskExecutorTests.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/github/webhook/GitHubWebhookControllerTests.java`
- `docs/progress/execution-log.md`
- `docs/plans/011-remote-branch-push.md`

## Task 1: Add Controlled Git Push Operation

**Interfaces:**

- `GitCommandRunner#pushBranch(Path repositoryDir, String branchName)` returns `GitCommandResult`.
- It rejects blank branch names with `IllegalArgumentException("Branch name must not be blank")`.
- It uses `git -C <repo> push origin HEAD:<branchName>`.

- [x] Write a failing test that `pushBranch` pushes the current HEAD to a bare `origin` repository branch.
- [x] Write a failing test that blank branch names are rejected.
- [x] Run `mvn -pl PatchPilot -Dtest=GitCommandRunnerTests test` and verify it fails because `pushBranch` does not exist.
- [x] Add `pushBranch` to `GitCommandRunner`.
- [x] Run `mvn -pl PatchPilot -Dtest=GitCommandRunnerTests test` and verify it passes.

## Task 2: Add Push Tool Boundary

**Interfaces:**

- `PushTool#pushBranch(Path repositoryDir, String branchName)` returns git push output.
- It throws `IllegalStateException("git push failed: <output>")` when push fails.

- [x] Write a failing test that `PushTool` passes repository path and branch name to `GitCommandRunner`.
- [x] Write a failing test that push failure is surfaced clearly.
- [x] Run `mvn -pl PatchPilot -Dtest=PushToolTests test` and verify it fails because `PushTool` does not exist.
- [x] Add `PushTool`.
- [x] Run `mvn -pl PatchPilot -Dtest=PushToolTests test` and verify it passes.

## Task 3: Wire Push After Local Commit

**Interfaces:**

- `NoopFixTaskExecutor(WorkspaceService, MavenTestRunner, PatchWorkflow, DiffTool, CommitTool, PushTool)`.
- Executor order: prepare repository -> apply patch workflow -> inspect diff -> run Maven tests -> commit all changes -> push prepared branch.
- Use `preparedWorkspace.branchName()` as the pushed branch name.

- [x] Update `WorkspaceFixTaskExecutorTests` with a recording push tool fake.
- [x] Add a failing assertion that push runs after local commit succeeds.
- [x] Add a failing assertion that push is not called when Maven tests fail.
- [x] Add a failing assertion that push is not called when commit fails.
- [x] Run `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests test` and verify it fails because the executor is not wired.
- [x] Inject `PushTool` into `NoopFixTaskExecutor`.
- [x] Call `pushBranch(preparedWorkspace.repositoryDir(), preparedWorkspace.branchName())` only after `CommitTool#commitAll(...)`.
- [x] Run `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests test` and verify it passes.

## Task 4: Validate And Document

- [x] Run `mvn -pl PatchPilot -Dtest=GitCommandRunnerTests,PushToolTests,WorkspaceFixTaskExecutorTests,GitHubWebhookControllerTests test`.
- [x] Run `mvn test`.
- [x] Run `mvn clean package`.
- [x] Record validation evidence in `docs/progress/execution-log.md`.
- [x] Mark this plan complete.

## Acceptance Checklist

- [x] Successful tasks push `HEAD` to the prepared remote branch.
- [x] Push happens after local commit, not before.
- [x] Maven test failures do not push.
- [x] Commit failures do not push.
- [x] Push failures produce clear task failure reasons.
- [x] Full Maven verification passes.
