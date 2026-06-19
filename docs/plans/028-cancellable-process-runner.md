# Cancellable Process Runner Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Allow cancellation of a running task to interrupt its current Maven test process.

**Architecture:** Add a task-scoped process registry that stores the currently running external process for each task. `MavenTestRunner` registers Maven processes when invoked with a task id, and `DefaultFixTaskControlService` asks the registry to cancel the task process when cancelling active tasks.

**Tech Stack:** Java 17, Spring Boot 3.5.x, JUnit 5, MockMvc.

## Global Constraints

- This phase handles Maven process cancellation only.
- Git process cancellation and repository cleanup are deferred to `029-git-process-cancellation-recovery`.
- A cancellation must first mark the task `CANCELLED`, then interrupt any registered task process.
- Registered processes must be unregistered after normal completion, timeout, interruption, or IO failure.
- Do not execute git-changing commands from Codex; provide commands for the user.

---

### Task 1: Task Process Registry

**Files:**
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/task/process/TaskProcessRegistry.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/task/process/TaskProcessRegistryTests.java`

- [x] Write failing tests for registering, unregistering, graceful destroy, and force destroy fallback.
- [x] Implement task-scoped process registration and cancellation.
- [x] Keep registry thread-safe with `ConcurrentHashMap`.

### Task 2: Maven Runner Integration

**Files:**
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/runner/service/MavenTestRunner.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/runner/service/MavenTestRunnerTests.java`

- [x] Add failing tests showing Maven process is registered with the task id and unregistered afterward.
- [x] Add `runTests(String taskId, Path repositoryDir)`.
- [x] Preserve existing `runTests(Path repositoryDir)` behavior for direct callers.

### Task 3: Cancellation Control Integration

**Files:**
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/service/impl/DefaultFixTaskControlService.java`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/executor/NoopFixTaskExecutor.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/task/service/DefaultFixTaskControlServiceTests.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/task/executor/WorkspaceFixTaskExecutorTests.java`

- [x] Add failing tests showing active task cancellation invokes process cancellation.
- [x] Pass task id into Maven test execution.
- [x] Keep pending cancellation behavior unchanged.

### Task 4: Documentation And Verification

**Files:**
- Modify: `docs/progress/execution-log.md`

- [x] Record implementation summary and validation commands.
- [x] Run targeted tests.
- [x] Run full `mvn -pl PatchPilot test`.
