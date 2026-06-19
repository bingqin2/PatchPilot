# Task Execution Timeline Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:test-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Persist and expose a per-task execution timeline so users and future UI surfaces can inspect what happened during a PatchPilot task.

**Architecture:** Add a `fix_task_timeline_event` table keyed by task id, with one event row per meaningful lifecycle step. Provide a `FixTaskTimelineService` with default in-memory and MyBatis-backed implementations, expose `GET /api/tasks/{id}/timeline`, and record events from webhook creation plus async dispatcher transitions.

**Tech Stack:** Java 17, Spring Boot 3.5.x, MyBatis-Plus 3.5.14, Flyway, MySQL 8.4, JUnit 5, AssertJ.

## Global Constraints

- Do not change existing task endpoints or webhook response shapes.
- Timeline API fields are additive and read-only.
- Do not add SSE or WebSocket streaming in this phase.
- Do not store secrets, GitHub tokens, or full command output in event messages.
- Timeline writes must not prevent durable task status transitions.
- New database schema names use snake_case.
- Default profile remains database-free with in-memory storage.

---

## Target Files

Create:

- `PatchPilot/src/main/resources/db/migration/V4__create_fix_task_timeline_event.sql`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/domain/enums/FixTaskTimelineEventType.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/domain/vo/FixTaskTimelineEventVo.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/domain/entity/FixTaskTimelineEventEntity.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/convert/FixTaskTimelineEventConvert.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/mapper/FixTaskTimelineEventMapper.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/service/FixTaskTimelineService.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/service/impl/InMemoryFixTaskTimelineService.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/service/impl/MyBatisFixTaskTimelineService.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/task/mapper/FixTaskTimelineMigrationTests.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/task/convert/FixTaskTimelineEventConvertTests.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/task/service/InMemoryFixTaskTimelineServiceTests.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/task/service/MyBatisFixTaskTimelineServiceTests.java`

Modify:

- `PatchPilot/src/main/java/io/patchpilot/backend/task/TaskController.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/github/webhook/GitHubWebhookService.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/service/impl/AsyncFixTaskDispatcher.java`
- Relevant controller, webhook, and dispatcher tests.
- `docs/progress/execution-log.md`

## Task 1: Add Timeline Schema And Domain Model

**Interfaces:**

- Migration path: `PatchPilot/src/main/resources/db/migration/V4__create_fix_task_timeline_event.sql`.
- Table: `fix_task_timeline_event`.
- Columns: `id varchar(36) primary key`, `task_id varchar(36) not null`, `event_type varchar(64) not null`, `message varchar(2048) null`, `created_at timestamp(6) not null`.
- Index: `idx_fix_task_timeline_event_task_created(task_id, created_at)`.
- `FixTaskTimelineEventType`: `TASK_CREATED`, `STATUS_COMMENT_CREATED`, `RUNNING`, `RUNNING_TESTS`, `PR_CREATED`, `COMPLETED`, `FAILED`.
- `FixTaskTimelineEventVo(String id, String taskId, FixTaskTimelineEventType eventType, String message, Instant createdAt)`.

- [x] Write a failing migration test for V4 table and columns.
- [x] Run `mvn -pl PatchPilot -Dtest=FixTaskTimelineMigrationTests test` and verify it fails because V4 does not exist.
- [x] Add V4 migration.
- [x] Add enum, VO, entity, mapper, and converter.
- [x] Add converter tests and run them.

## Task 2: Implement Timeline Services

**Interfaces:**

- `FixTaskTimelineService#recordEvent(String taskId, FixTaskTimelineEventType eventType, String message)` returns `FixTaskTimelineEventVo`.
- `FixTaskTimelineService#listEvents(String taskId)` returns events sorted by `createdAt` ascending.
- In-memory implementation is active under `default`.
- MyBatis implementation is active under `local` and `docker`.

- [x] Write failing in-memory service tests for record/list ordering.
- [x] Write failing MyBatis service tests for insert and query ordering.
- [x] Implement both services.
- [x] Run timeline service tests.

## Task 3: Expose Timeline API

**Interfaces:**

- `GET /api/tasks/{id}/timeline` returns `ApiResponse<List<FixTaskTimelineEventVo>>`.
- Missing task returns `404` with `Task not found`, matching existing task lookup behavior.
- Existing task with no events returns an empty list.

- [x] Add failing `TaskControllerTests` for timeline API success and missing task.
- [x] Inject `FixTaskTimelineService` into `TaskController`.
- [x] Implement the endpoint.
- [x] Run `mvn -pl PatchPilot -Dtest=TaskControllerTests test`.

## Task 4: Record Lifecycle Events

**Interfaces:**

- Webhook records `TASK_CREATED` after task creation.
- Webhook records `STATUS_COMMENT_CREATED` after the PatchPilot status comment id is saved.
- Dispatcher records `RUNNING`, `RUNNING_TESTS`, `PR_CREATED`, `COMPLETED`, and `FAILED`.
- Timeline write failures are swallowed so task status transitions remain authoritative.
- Duplicate deliveries do not record duplicate timeline events.

- [x] Add failing webhook service tests for creation/status-comment timeline events and duplicate behavior.
- [x] Add failing dispatcher tests for success and failure event sequences.
- [x] Wire `FixTaskTimelineService` into webhook and dispatcher.
- [x] Run webhook and dispatcher tests.

## Task 5: Validate And Document

- [x] Run focused tests for migration, converter, services, controller, webhook, and dispatcher.
- [x] Run `mvn test`.
- [x] Run `mvn clean package`.
- [x] Record validation evidence in `docs/progress/execution-log.md`.
- [x] Mark this plan complete.

## Acceptance Checklist

- [x] Timeline events persist in MySQL and default in-memory mode.
- [x] `GET /api/tasks/{id}/timeline` exposes ordered events.
- [x] Successful tasks record creation, running, testing, PR, and completion events.
- [x] Failed tasks record creation, running/testing when reached, and failure events.
- [x] Timeline write failures do not corrupt task state.
- [x] Full Maven verification passes.
