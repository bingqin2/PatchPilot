# Task Control API Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add HTTP task control actions for safely cancelling queued tasks and retrying terminal failed or cancelled tasks.

**Architecture:** Controllers stay thin and delegate lifecycle decisions to a task control service. Task state transitions remain in `FixTaskService`; queue state changes remain in `FixTaskQueue`, so MyBatis and in-memory profiles keep equivalent semantics.

**Tech Stack:** Java 17, Spring Boot 3.5.x, JUnit 5, MockMvc, MyBatis-Plus.

## Global Constraints

- Do not claim to cancel already running work until the worker supports interruption.
- Cancel only `PENDING` tasks.
- Retry only `FAILED` or `CANCELLED` tasks.
- Record timeline events for successful cancel and retry actions.
- Do not run git-changing commands from Codex; provide commands for the user.

---

### Task 1: Service-Level Cancel And Retry

**Files:**
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/domain/enums/FixTaskStatus.java`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/domain/enums/FixTaskTimelineEventType.java`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/service/FixTaskService.java`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/service/impl/InMemoryFixTaskService.java`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/service/impl/MyBatisFixTaskService.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/task/service/FixTaskControlService.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/task/service/impl/DefaultFixTaskControlService.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/task/service/InMemoryFixTaskServiceTests.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/task/service/MyBatisFixTaskServiceTests.java`

- [x] Add failing tests for cancellation and retry status transitions.
- [x] Implement `CANCELLED`, `markCancelled`, `markPendingForRetry`, and `DefaultFixTaskControlService`.
- [x] Verify service tests pass.

### Task 2: Queue-Level Cancel Support

**Files:**
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/domain/enums/FixTaskQueueItemStatus.java`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/domain/vo/FixTaskQueueSummaryVo.java`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/service/FixTaskQueue.java`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/service/impl/InMemoryFixTaskQueue.java`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/service/impl/MyBatisFixTaskQueue.java`
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/service/impl/MyBatisFixTaskQueueQueryService.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/task/service/MyBatisFixTaskQueueTests.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/task/TaskControllerTests.java`

- [x] Add failing tests for pending queue-item cancellation and summary counts.
- [x] Implement queue cancellation for pending items by task id.
- [x] Keep retry as a new enqueue rather than mutating old terminal queue items.

### Task 3: HTTP Control Endpoints

**Files:**
- Modify: `PatchPilot/src/main/java/io/patchpilot/backend/task/TaskController.java`
- Test: `PatchPilot/src/test/java/io/patchpilot/backend/task/TaskControllerTests.java`
- Modify: `docs/progress/execution-log.md`

- [x] Add failing MockMvc tests for `POST /api/tasks/{id}/cancel` and `POST /api/tasks/{id}/retry`.
- [x] Return 404 for missing tasks and 409 for invalid lifecycle actions.
- [x] Run targeted and full backend tests.
