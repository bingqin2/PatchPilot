# Queue Recovery and Retry Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:test-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make MySQL-backed queue execution recover from stale running items and retry transient worker failures before permanently failing a queue item.

**Architecture:** Keep the queue in MySQL and add retry/recovery behavior inside `MyBatisFixTaskQueue`. A new `TaskQueueProperties` class owns retry limits and timing defaults. The poller first recovers timed-out `RUNNING` items, then claims one available `PENDING` item and executes it.

**Tech Stack:** Java 17, Spring Boot 3.5.x, MyBatis-Plus 3.5.14, Flyway, MySQL 8.4, JUnit 5, AssertJ, Mockito.

## Global Constraints

- Do not add Redis, RabbitMQ, Kafka, or new Docker services.
- Keep `FixTaskQueue#enqueue(String taskId)` unchanged.
- Keep default profile database-free.
- Retry settings use the `patchpilot.task.queue` prefix.
- Preserve `last_error` for failed attempts.
- Do not retry after `max-attempts` is reached.

---

## Task 1: Queue Retry Configuration

- [x] Add `TaskQueueProperties` with defaults:
  - `maxAttempts = 3`
  - `retryDelayMs = 30000`
  - `visibilityTimeoutMs = 300000`
- [x] Register properties in `PatchPilotApplication`.
- [x] Update `MyBatisFixTaskQueue` constructor to consume `TaskQueueProperties`.

## Task 2: Retry Failed Queue Items

- [x] If a running queue item fails below `maxAttempts`, set it back to `PENDING`.
- [x] Store `last_error`.
- [x] Clear `locked_at`.
- [x] Set `available_at = now + retryDelayMs`.
- [x] If attempts are already at `maxAttempts`, set status to `FAILED`.
- [x] Cover retry and terminal failure with tests.

## Task 3: Recover Stale Running Items

- [x] Add `recoverTimedOutRunningItems()`.
- [x] Select `RUNNING` items with `locked_at` older than `now - visibilityTimeoutMs`.
- [x] Set recovered items back to `PENDING`.
- [x] Clear `locked_at`.
- [x] Make recovered items immediately available.
- [x] Cover stale recovery with tests.

## Task 4: Poller Recovery Flow

- [x] Call `recoverTimedOutRunningItems()` before `claimNext()`.
- [x] Keep existing no-item, success, and failure poller behavior.
- [x] Cover recovery-before-claim with tests.

## Task 5: Validate And Document

- [x] Run focused retry/recovery tests.
- [x] Run `mvn -pl PatchPilot test`.
- [x] Update `docs/progress/execution-log.md` with validation evidence.

## Acceptance Checklist

- [x] Transient worker failures are retried while attempts remain.
- [x] Queue items become terminal `FAILED` after max attempts.
- [x] Stale `RUNNING` queue items are recovered to `PENDING`.
- [x] Poller performs recovery before claiming work.
- [x] Full backend tests pass.
