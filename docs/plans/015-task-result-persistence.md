# Task Result Persistence Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:test-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Persist task result metadata so completed tasks expose their Pull Request URL and terminal timestamps through the existing task APIs.

**Architecture:** Extend the existing `FixTaskVo`, MyBatis entity, converter, and service status transitions with result fields. Add a second Flyway migration for additive MySQL columns, and keep the default in-memory profile behavior equivalent to the MySQL service. The async dispatcher will pass the created PR URL into completion persistence.

**Tech Stack:** Java 17, Spring Boot 3.5.x, MyBatis-Plus 3.5.14, Flyway, MySQL 8.4, JUnit 5, AssertJ.

## Global Constraints

- Do not change webhook request/response contracts.
- Do not add model provider calls.
- Do not add a task execution log table in this phase.
- Do not require MySQL for the default profile or current Spring context smoke tests.
- Existing task APIs keep their endpoints and existing JSON fields; new fields are additive only.
- New database schema names use snake_case.
- `pullRequestUrl` is set only after Pull Request creation succeeds.
- `completedAt` is set only for `COMPLETED` tasks.
- `updatedAt` changes on every status transition.

---

## Target Files

Create:

- `PatchPilot/src/main/resources/db/migration/V2__add_fix_task_result_fields.sql`
- `PatchPilot/src/test/java/io/patchpilot/backend/task/mapper/FixTaskResultMigrationTests.java`

Modify:

- `PatchPilot/src/main/java/io/patchpilot/backend/task/domain/vo/FixTaskVo.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/domain/entity/FixTaskEntity.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/convert/FixTaskConvert.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/service/FixTaskService.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/service/impl/InMemoryFixTaskService.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/service/impl/MyBatisFixTaskService.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/service/impl/AsyncFixTaskDispatcher.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/task/convert/FixTaskConvertTests.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/task/service/InMemoryFixTaskServiceTests.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/task/service/MyBatisFixTaskServiceTests.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/task/service/AsyncFixTaskDispatcherTests.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/task/TaskControllerTests.java`
- `docs/progress/execution-log.md`
- `docs/plans/015-task-result-persistence.md`

## Task 1: Add Result Field Migration

**Interfaces:**

- Migration path: `PatchPilot/src/main/resources/db/migration/V2__add_fix_task_result_fields.sql`.
- Add nullable columns to `fix_task`:
  - `pull_request_url varchar(2048) null`
  - `completed_at timestamp(6) null`
  - `updated_at timestamp(6) null`

- [x] Write a failing migration test that reads `V2__add_fix_task_result_fields.sql` and asserts it adds the three columns with exact snake_case names.
- [x] Run `mvn -pl PatchPilot -Dtest=FixTaskResultMigrationTests test` and verify it fails because the migration file does not exist.
- [x] Add `V2__add_fix_task_result_fields.sql`.
- [x] Run `mvn -pl PatchPilot -Dtest=FixTaskResultMigrationTests test` and verify it passes.

## Task 2: Extend Task DTO, Entity, And Converter

**Interfaces:**

- `FixTaskVo` adds nullable fields: `String pullRequestUrl`, `Instant completedAt`, `Instant updatedAt`.
- `FixTaskEntity` adds matching MyBatis fields: `pullRequestUrl`, `completedAt`, `updatedAt`.
- `FixTaskConvert#newEntity(...)` sets `pullRequestUrl=null`, `completedAt=null`, and `updatedAt=createdAt`.
- `FixTaskConvert#replaceStatus(...)` keeps result fields unless explicitly changed by a service method.
- Add `FixTaskConvert#replaceCompleted(FixTaskEntity current, String pullRequestUrl, Instant updatedAt)`.

- [x] Update converter tests so new entities and VOs include the result fields.
- [x] Add a failing converter test for `replaceCompleted(...)`.
- [x] Run `mvn -pl PatchPilot -Dtest=FixTaskConvertTests test` and verify it fails because result fields/method do not exist.
- [x] Update `FixTaskVo`, `FixTaskEntity`, and `FixTaskConvert`.
- [x] Run `mvn -pl PatchPilot -Dtest=FixTaskConvertTests test` and verify it passes.

## Task 3: Persist Result Metadata In Services

**Interfaces:**

- `FixTaskService#markCompleted(String id)` remains as a default compatibility method.
- Add `FixTaskService#markCompleted(String id, String pullRequestUrl)`.
- `markCompleted(id, pullRequestUrl)` sets `status=COMPLETED`, `pullRequestUrl`, `completedAt`, and `updatedAt`.
- Other status transitions keep `pullRequestUrl` and `completedAt`, but set `updatedAt`.
- `markFailed(id, failureReason)` sets `status=FAILED`, `failureReason`, and `updatedAt`.

- [x] Update `InMemoryFixTaskServiceTests` with a failing test that completion stores PR URL and timestamps.
- [x] Update `MyBatisFixTaskServiceTests` with failing tests that completion and failure write result metadata.
- [x] Run `mvn -pl PatchPilot -Dtest=InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests test` and verify it fails.
- [x] Update `FixTaskService`, `InMemoryFixTaskService`, and `MyBatisFixTaskService`.
- [x] Run `mvn -pl PatchPilot -Dtest=InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests test` and verify it passes.

## Task 4: Wire Dispatcher And API Coverage

**Interfaces:**

- `AsyncFixTaskDispatcher` calls `fixTaskService.markCompleted(taskId, executionResult.pullRequestUrl())`.
- Completion comments still receive the completed task and PR URL.
- `/api/tasks` and `/api/tasks/{id}` return additive JSON fields `pullRequestUrl`, `completedAt`, and `updatedAt`.

- [x] Update `AsyncFixTaskDispatcherTests` with a failing assertion that completed tasks retain `pullRequestUrl`.
- [x] Update `TaskControllerTests` with failing JSON assertions for new task fields.
- [x] Run `mvn -pl PatchPilot -Dtest=AsyncFixTaskDispatcherTests,TaskControllerTests test` and verify it fails.
- [x] Update `AsyncFixTaskDispatcher`.
- [x] Run `mvn -pl PatchPilot -Dtest=AsyncFixTaskDispatcherTests,TaskControllerTests test` and verify it passes.

## Task 5: Validate And Document

- [x] Run `mvn -pl PatchPilot -Dtest=FixTaskResultMigrationTests,FixTaskConvertTests,InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests,AsyncFixTaskDispatcherTests,TaskControllerTests,GitHubWebhookControllerTests test`.
- [x] Run `mvn test`.
- [x] Run `mvn clean package`.
- [x] Record validation evidence in `docs/progress/execution-log.md`.
- [x] Mark this plan complete.

## Acceptance Checklist

- [x] MySQL has additive result metadata migration.
- [x] Completed tasks persist `pullRequestUrl`, `completedAt`, and `updatedAt`.
- [x] Failed and intermediate transitions update `updatedAt`.
- [x] Default in-memory profile behaves like MySQL-backed service.
- [x] Existing task API fields remain compatible.
- [x] Full Maven verification passes.
