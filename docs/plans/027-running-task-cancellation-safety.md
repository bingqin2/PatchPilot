# Running Task Cancellation Safety Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make task cancellation safe for already running tasks by stopping later execution stages before commit, push, or pull request creation.

**Architecture:** Keep cancellation as durable task state in `FixTaskService`. `FixTaskControlService` accepts cancellation for active tasks, while `FixTaskWorker` and `NoopFixTaskExecutor` check durable task state at stage boundaries so cancelled work does not continue into later side effects.

**Tech Stack:** Java 17, Spring Boot 3.5.x, JUnit 5, MockMvc, MyBatis-Plus.

## Global Constraints

- Do not add OS-level process interruption in this phase.
- Cancellation must stop commit, push, and pull request creation after a running task is cancelled.
- Cancelled tasks must not be overwritten to `FAILED` or `COMPLETED` by the worker.
- Keep existing pending cancellation behavior.
- Do not execute git-changing commands from Codex; provide commands for the user.

---

### Task 1: Allow Active Cancellation

**Files:**
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/service/impl/DefaultFixTaskControlService.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/task/service/DefaultFixTaskControlServiceTests.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/task/TaskControllerTests.java`

- [x] Write failing tests showing `RUNNING` and `RUNNING_TESTS` tasks can be cancelled.
- [x] Change control service validation from pending-only to active-task cancellation.
- [x] Keep terminal tasks non-cancellable.

### Task 2: Worker Cancellation Guard

**Files:**
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/task/executor/TaskCancellationException.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/task/executor/TaskCancellationChecker.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/task/executor/DefaultTaskCancellationChecker.java`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/service/impl/FixTaskWorker.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/task/service/FixTaskWorkerTests.java`

- [x] Write failing tests showing a cancelled task is not overwritten to failed.
- [x] Add a cancellation exception and checker abstraction.
- [x] Update worker to stop cleanly when cancellation is observed.

### Task 3: Executor Stage Boundary Checks

**Files:**
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/executor/NoopFixTaskExecutor.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/task/executor/WorkspaceFixTaskExecutorTests.java`

- [x] Write failing tests showing cancellation after tests prevents commit, push, and PR creation.
- [x] Inject `TaskCancellationChecker` into the executor.
- [x] Check cancellation after clone, patch, diff, tests, commit, and push, before moving to the next side-effect stage.

### Task 4: Documentation And Verification

**Files:**
- Modify: `docs/progress/execution-log.md`

- [x] Record implementation summary and validation commands.
- [x] Run targeted tests.
- [x] Run full `mvn -pl PatchPilot test`.
