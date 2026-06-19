# Concurrent Queue Claim Safety Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:test-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Prevent two MySQL-backed queue pollers from successfully claiming and executing the same pending queue item.

**Architecture:** Keep the current select-then-update flow, but make the update conditional on the selected row still being `PENDING` and available. Treat `update` row count as the ownership signal: `1` means this worker claimed the item, `0` means another worker won the race and this poll tick exits without execution.

**Tech Stack:** Java 17, Spring Boot 3.5.x, MyBatis-Plus 3.5.14, MySQL 8.4, JUnit 5, AssertJ, Mockito.

## Global Constraints

- Do not add Redis, RabbitMQ, Kafka, or a distributed lock service.
- Keep `FixTaskQueue#enqueue(String taskId)` unchanged.
- Keep queue recovery, retry, and observability behavior unchanged.
- Do not introduce MySQL-specific `FOR UPDATE SKIP LOCKED` in this phase.
- A worker may skip the current tick if another worker claims the selected item first.

---

## Task 1: Add Race-Loss Claim Test

- [x] Add a test that selected pending item ownership is decided by conditional update row count.
- [x] Verify `claimNext()` returns the running item when the conditional update affects one row.
- [x] Verify `claimNext()` returns empty when the conditional update affects zero rows.
- [x] Confirm the new test fails against the old `updateById` implementation.

## Task 2: Make Claim Update Conditional

- [x] Replace `updateById` in `MyBatisFixTaskQueue#claimNext()` with a conditional `update`.
- [x] Match `id`, `status = PENDING`, and `available_at <= now` in the update wrapper.
- [x] Return a claimed `FixTaskQueueItemVo` only when one row is updated.
- [x] Return `Optional.empty()` when the selected item was already claimed by another worker.

## Task 3: Validate And Document

- [x] Run focused queue claim tests.
- [x] Run queue poller tests to ensure no execution flow regression.
- [x] Run `mvn -pl PatchPilot test`.
- [x] Update `docs/progress/execution-log.md` with validation evidence.

## Acceptance Checklist

- [x] Concurrent workers cannot both successfully claim the same queue item through `claimNext()`.
- [x] Losing a claim race does not execute the task.
- [x] Existing retry and stale recovery tests still pass.
- [x] Full backend tests pass.
