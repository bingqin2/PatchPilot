# Task Test Runner Integration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:test-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Run the Maven test runner during task execution after a repository workspace is prepared.

**Architecture:** Keep async status orchestration in `task.service.impl.AsyncFixTaskDispatcher` and repository/test execution in `task.executor.NoopFixTaskExecutor`. The executor prepares the repository, calls `MavenTestRunner` on the prepared repository path, and throws a clear failure when tests return a non-zero exit code. This phase does not invoke a model, edit files, push branches, or create Pull Requests.

**Tech Stack:** Java 17, Spring Boot 3.5.x, JUnit 5, `ProcessBuilder`.

## Global Constraints

- Use the existing `MavenTestRunner`; do not add arbitrary shell execution.
- Run tests only inside the prepared repository directory.
- Preserve task failure behavior through the existing dispatcher.
- Add only the minimum status needed for test execution visibility.
- Keep output handling concise; do not store full test logs on the task record in this phase.
- Ensure the backend runtime image has Maven available on a Java 17 base image.

---

## Target Files

Modify:

- `PatchPilot/src/main/java/io/patchpilot/backend/task/domain/enums/FixTaskStatus.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/service/FixTaskService.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/service/impl/InMemoryFixTaskService.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/executor/NoopFixTaskExecutor.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/task/service/InMemoryFixTaskServiceTests.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/task/executor/WorkspaceFixTaskExecutorTests.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/runner/service/MavenRuntimePackagingTests.java`
- `PatchPilot/Dockerfile`
- `docs/progress/execution-log.md`
- `docs/plans/007-task-test-runner-integration.md`

## Task 1: Add Running Tests Status

**Interfaces:**

- `FixTaskStatus.RUNNING_TESTS`
- `FixTaskService#markRunningTests(String id)` returns `FixTaskVo`

- [x] Write a failing test in `InMemoryFixTaskServiceTests` that creates a task and verifies `markRunningTests(...)` sets `RUNNING_TESTS`.
- [x] Run `mvn -pl PatchPilot -Dtest=InMemoryFixTaskServiceTests test` and verify compilation fails because `markRunningTests` and `RUNNING_TESTS` do not exist.
- [x] Add `RUNNING_TESTS` to `FixTaskStatus`.
- [x] Add `markRunningTests` to `FixTaskService` and `InMemoryFixTaskService`.
- [x] Run the targeted test and verify it passes.

## Task 2: Run Maven Tests During Task Execution

**Interfaces:**

- `NoopFixTaskExecutor(WorkspaceService workspaceService, MavenTestRunner mavenTestRunner)`
- `MavenTestRunner#runTests(Path repositoryDir)` returns `TestRunResult`

- [x] Update `WorkspaceFixTaskExecutorTests` with a fake `MavenTestRunner` and assert the executor runs tests against `PreparedWorkspaceResult.repositoryDir()`.
- [x] Add a failing executor test where the fake runner returns exit code `1` and output `test failed`; assert `IllegalStateException("maven tests failed: test failed")`.
- [x] Run `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests test` and verify it fails because the executor does not call the runner.
- [x] Inject `MavenTestRunner` into `NoopFixTaskExecutor`.
- [x] Call `mavenTestRunner.runTests(prepared.repositoryDir())` after workspace preparation.
- [x] Throw `IllegalStateException("maven tests failed: " + output)` when the result exit code is non-zero.
- [x] Run the targeted executor test and verify it passes.

## Task 3: Ensure Runtime Maven Availability

- [x] Add a packaging test that verifies the backend runtime image uses the Java 17 Maven base image.
- [x] Run `mvn -pl PatchPilot -Dtest=MavenRuntimePackagingTests test` and verify it fails before the runtime Dockerfile change.
- [x] Update `PatchPilot/Dockerfile` so the runtime stage includes Maven without adding a second JDK.
- [x] Run the packaging test and verify it passes.

## Task 4: Validate And Document

- [x] Run `mvn -pl PatchPilot -Dtest=InMemoryFixTaskServiceTests,WorkspaceFixTaskExecutorTests test`.
- [x] Run `mvn test`.
- [x] Run `mvn clean package`.
- [x] Rebuild and restart Docker compose with `PATCHPILOT_GITHUB_WEBHOOK_SECRET=test-secret-123 docker compose up --build -d`.
- [x] Call `GET /api/tasks` locally and confirm a successful response.
- [x] Record validation evidence in `docs/progress/execution-log.md`.
- [x] Mark this plan complete.

## Acceptance Checklist

- [x] Tasks can enter `RUNNING_TESTS`.
- [x] Task executor prepares the repository before running tests.
- [x] Task executor runs Maven tests in the prepared repository path.
- [x] Non-zero Maven test exits fail the task with a clear reason.
- [x] Backend runtime image has Maven available on the Java 17 Maven base image.
- [x] Full Maven and Docker verification passes.
