# Test Run Records Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:test-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Persist and expose Maven verification results for each PatchPilot task.

**Architecture:** Add a `fix_task_test_run` record owned by `task`, with in-memory and MyBatis-backed services matching the existing timeline service pattern. The task executor records Maven test command, exit code, output summary, and timing immediately after `MavenTestRunner` returns. Task APIs expose ordered test runs by task id.

**Tech Stack:** Java 17, Spring Boot 3.5.x, MyBatis-Plus 3.5.14, Flyway, MySQL 8.4, JUnit 5, AssertJ, MockMvc.

## Global Constraints

- Do not add new external services.
- Do not store secrets in test output.
- Keep the existing task and timeline APIs unchanged.
- Missing task test-run API returns `404` with `Task not found`.
- Test-run writes should not hide Maven failure results; failures still mark the task `FAILED`.
- Default profile remains database-free with in-memory storage.

---

## Task 1: Add Test Run Schema And Domain

- [x] Add migration `V6__create_fix_task_test_run.sql`.
- [x] Add `FixTaskTestRunVo`.
- [x] Add `FixTaskTestRunEntity`.
- [x] Add `FixTaskTestRunConvert`.
- [x] Add `FixTaskTestRunMapper`.
- [x] Cover migration and conversion with tests.

## Task 2: Add Test Run Services

- [x] Add `FixTaskTestRunService#recordTestRun(...)`.
- [x] Add `FixTaskTestRunService#listTestRuns(String taskId)`.
- [x] Implement in-memory storage for `default`.
- [x] Implement MyBatis storage for `local` and `docker`.
- [x] List records oldest first.

## Task 3: Expose Test Run API

- [x] Add `GET /api/tasks/{id}/test-runs`.
- [x] Return ordered `FixTaskTestRunVo` records for existing tasks.
- [x] Return `404` for missing tasks.
- [x] Keep task listing/detail response shapes unchanged.

## Task 4: Record Maven Verification

- [x] Inject `FixTaskTestRunService` into `NoopFixTaskExecutor`.
- [x] Record every Maven test result before evaluating exit code.
- [x] On non-zero exit code, still fail with the existing `maven tests failed: ...` message.
- [x] Keep commit, push, and PR creation blocked after failing tests.

## Task 5: Validate And Document

- [x] Run focused tests for migration, converter, services, controller, and executor.
- [x] Run `mvn -pl PatchPilot test`.
- [x] Update `docs/progress/execution-log.md` with validation evidence.

## Acceptance Checklist

- [x] Maven command, exit code, output, start time, end time, and duration are recorded.
- [x] Successful and failing Maven runs are both persisted.
- [x] `GET /api/tasks/{id}/test-runs` exposes test runs ordered by start time.
- [x] Missing task requests return `404`.
- [x] Full backend tests pass.
