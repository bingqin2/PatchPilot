# Tool Call Audit Records Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:test-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Persist and expose auditable records for task executor tool calls.

**Architecture:** Add a `fix_task_tool_call` table keyed by task id, with in-memory and MyBatis-backed services following the existing timeline and test-run patterns. The executor records each code-changing or remote-publishing tool call around `PatchWorkflow`, `DiffTool`, `CommitTool`, `PushTool`, and `PullRequestTool`. Maven verification remains tracked by `fix_task_test_run`.

**Tech Stack:** Java 17, Spring Boot 3.5.x, MyBatis-Plus 3.5.14, Flyway, MySQL 8.4, JUnit 5, AssertJ, MockMvc.

## Global Constraints

- Do not add a new external service.
- Do not duplicate Maven test results in tool-call audit records.
- Record failed tool calls before rethrowing the original exception.
- Keep task, timeline, and test-run APIs unchanged.
- Missing task tool-call API returns `404` with `Task not found`.
- Default profile remains database-free with in-memory storage.

---

## Task 1: Add Tool Call Schema And Domain

- [x] Add migration `V7__create_fix_task_tool_call.sql`.
- [x] Add `FixTaskToolCallVo`.
- [x] Add `FixTaskToolCallEntity`.
- [x] Add `FixTaskToolCallConvert`.
- [x] Add `FixTaskToolCallMapper`.
- [x] Cover migration and conversion with tests.

## Task 2: Add Tool Call Services

- [x] Add `FixTaskToolCallService#recordToolCall(...)`.
- [x] Add `FixTaskToolCallService#listToolCalls(String taskId)`.
- [x] Implement in-memory storage for `default`.
- [x] Implement MyBatis storage for `local` and `docker`.
- [x] List records oldest first.

## Task 3: Expose Tool Call API

- [x] Add `GET /api/tasks/{id}/tool-calls`.
- [x] Return ordered `FixTaskToolCallVo` records for existing tasks.
- [x] Return `404` for missing tasks.

## Task 4: Audit Executor Tool Calls

- [x] Inject `FixTaskToolCallService` into `NoopFixTaskExecutor`.
- [x] Record successful `PatchWorkflow`, `DiffTool`, `CommitTool`, `PushTool`, and `PullRequestTool` calls.
- [x] Record failing commit and push calls before stopping later tools.
- [x] Keep Maven failures blocking commit, push, and Pull Request creation.

## Task 5: Validate And Document

- [x] Run focused tests for migration, converter, services, controller, and executor.
- [x] Run `mvn -pl PatchPilot test`.
- [x] Update `docs/progress/execution-log.md` with validation evidence.

## Acceptance Checklist

- [x] Tool name, input summary, output summary, success flag, start time, end time, and duration are recorded.
- [x] Successful and failing tool calls are both persisted.
- [x] `GET /api/tasks/{id}/tool-calls` exposes tool calls ordered by start time.
- [x] Missing task requests return `404`.
- [x] Full backend tests pass.
