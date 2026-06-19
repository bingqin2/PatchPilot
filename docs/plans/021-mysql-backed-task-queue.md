# MySQL Backed Task Queue Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:test-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Persist queued task execution requests in MySQL so local/docker deployments do not lose queued tasks on process restart.

**Architecture:** Keep `FixTaskQueue` as the dispatcher boundary. The default profile continues using the current in-process queue. The `local` and `docker` profiles use a MyBatis-backed queue table plus a scheduled poller that claims pending items, invokes `FixTaskWorker`, and marks queue items completed or failed.

**Tech Stack:** Java 17, Spring Boot 3.5.x, MyBatis-Plus 3.5.14, Flyway, MySQL 8.4, JUnit 5, AssertJ, Mockito.

## Global Constraints

- Do not add Redis, RabbitMQ, Kafka, or new Docker services.
- Do not execute tasks directly inside `AsyncFixTaskDispatcher`.
- Keep `FixTaskQueue#enqueue(String taskId)` unchanged for existing callers.
- Keep `default` profile database-free.
- First version is single-instance safe enough; multi-instance locking can be hardened later.
- Queue records must include status, attempts, timestamps, and failure reason.

---

## Task 1: Add Queue Schema And Domain

- [x] Add migration `V8__create_fix_task_queue_item.sql`.
- [x] Add `FixTaskQueueItemStatus` enum with `PENDING`, `RUNNING`, `COMPLETED`, and `FAILED`.
- [x] Add `FixTaskQueueItemEntity`.
- [x] Add `FixTaskQueueItemVo`.
- [x] Add `FixTaskQueueItemConvert`.
- [x] Add `FixTaskQueueItemMapper`.
- [x] Cover migration and conversion with tests.

## Task 2: Add MyBatis Queue Implementation

- [x] Add `MyBatisFixTaskQueue` under `local` and `docker` profiles.
- [x] `enqueue(taskId)` inserts a `PENDING` item with `attempt_count = 0`, `available_at = now`, and timestamps.
- [x] Add `claimNext()` to atomically transition the oldest available pending item to `RUNNING` and increment attempts.
- [x] Add `markCompleted(queueItemId)` and `markFailed(queueItemId, failureReason)`.
- [x] Cover enqueue, claim ordering, completion, and failure with unit tests.

## Task 3: Add Scheduled Queue Poller

- [x] Add `FixTaskQueuePoller` under `local` and `docker` profiles.
- [x] Poller calls `claimNext()` once per tick.
- [x] If no item is available, poller exits without calling the worker.
- [x] If an item is claimed, poller calls `FixTaskWorker.execute(taskId)`.
- [x] Worker success marks queue item `COMPLETED`.
- [x] Worker failure marks queue item `FAILED` with the exception message.
- [x] Enable scheduling in `PatchPilotApplication`.

## Task 4: Preserve Existing In-Memory Behavior

- [x] Keep `InMemoryFixTaskQueue` on the default profile.
- [x] Keep `AsyncFixTaskDispatcher` unchanged except for tests if needed.
- [x] Ensure default-profile Spring context still starts without a database.

## Task 5: Validate And Document

- [x] Run focused queue tests.
- [x] Run `mvn -pl PatchPilot test`.
- [x] Update `docs/progress/execution-log.md` with validation evidence.

## Acceptance Checklist

- [x] `local` and `docker` deployments enqueue task ids into MySQL.
- [x] A scheduled worker can claim and execute queued task ids.
- [x] Queue item status transitions are persisted.
- [x] Queue item failures preserve a readable failure reason.
- [x] Default profile still uses the in-memory queue and full tests pass.
